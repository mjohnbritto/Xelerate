package com.suntecgroup.metaconfig.services.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Iterables;
import com.suntecgroup.metaconfig.constant.MetaConstant;
import com.suntecgroup.metaconfig.exception.ApiDocParserException;
import com.suntecgroup.metaconfig.model.ApiDocResponse;
import com.suntecgroup.metaconfig.model.ApiParameter;
import com.suntecgroup.metaconfig.model.ApiSecurity;
import com.suntecgroup.metaconfig.model.Response;

import io.swagger.oas.inflector.examples.ExampleBuilder;
import io.swagger.oas.inflector.examples.models.Example;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;

/**
 * Parser implementation for Open API spec 3.0
 * 
 * @author murugeshpd
 *
 */
@Component
public class OpenApiParserServiceImpl implements ApiDocParserService {

	private static final Logger LOG = LoggerFactory.getLogger(OpenApiParserServiceImpl.class);
	
	/**
	 * To parse the yaml/swagger json of format OpenApi-3.0 spec. It accepts Api
	 * doc URL or yaml file and produces the response. If there are multiple
	 * API's configured in the file then, the service will return error response
	 * will available operation ids.
	 * 
	 * @param file
	 * @param operation
	 * @param contentType
	 * @param apiDocUrl
	 */
	@Override
	public ApiDocResponse parse(final MultipartFile file, final String operation, final String contentType,
			final String apiDocUrl) throws ApiDocParserException {
		ApiDocResponse apiDocResponse = null;
		final OpenAPI openAPI = getOpenApi(file, apiDocUrl);
		final Set<String> allOperationIds = getAllOperationIds(openAPI);
		if (!CollectionUtils.isEmpty(allOperationIds) && (allOperationIds.size() == 1 || StringUtils.isNotBlank(operation))) {
			LOG.info("Processing API Doc started");
			OperationHolder operationHolder = null;
			final String operationId = (allOperationIds.size() == 1) ? Iterables.getFirst(allOperationIds, null) : operation;
			if (openAPI != null && openAPI.getPaths() != null && !CollectionUtils.isEmpty(openAPI.getPaths().values())) {
				operationHolder = openAPI.getPaths().entrySet().stream().map(e -> {
					final PathItem s = e.getValue();
					return getOperationById(operationId, e.getKey(), getOperationMap(s));
				}).filter(Objects::nonNull).findFirst().orElse(null);
			}
			apiDocResponse = populateResponse(contentType, openAPI, operationHolder);
			LOG.info("Processing API Doc ended");
		} else {
			LOG.info("More than one API available in the API Doc so returning operation Ids {}", String.join(",", allOperationIds));
			final Response<Set<String>> errorResponse = new Response<Set<String>>(MetaConstant.API_PARSER_VALIDATION_FAILURE_OPERATION_ID_CODE, "More than one API is configured in the API document", allOperationIds);
			throw new ApiDocParserException(errorResponse);
		}
		return apiDocResponse;
	}
	

	private ApiDocResponse populateResponse(final String contentType, final OpenAPI openAPI,
			final OperationHolder operationHolder) {
		final ApiDocResponse apiDocResponse = new ApiDocResponse();
		if(operationHolder != null) {
			final Components components = openAPI.getComponents();
			@SuppressWarnings("rawtypes")
			final Map<String, Schema> schemas = (components != null && components.getSchemas() != null) ? components.getSchemas() : null;
			final Map<String, SecurityScheme> securitySchemes = (components != null) ? components.getSecuritySchemes() : null;
			final Operation operation = operationHolder.getOperation();
			apiDocResponse.setHttpMethod(operationHolder.getHttpMethod());
			apiDocResponse.setEndpointUrl(getApiEndpointUrls(operationHolder, openAPI.getServers()));
			apiDocResponse.setParameters(getParameters(operation.getParameters()));
			apiDocResponse.setApiSecurity(getSecurityDetails(operation, securitySchemes));
			apiDocResponse.setRequestBody(getAPIRequest(schemas, operation.getRequestBody(), contentType));
			apiDocResponse.setResponseBody(getAPIResponse(schemas, operation.getResponses(), contentType));
		}
		return apiDocResponse;
	}

	private Map<HttpMethod, Operation> getOperationMap(final PathItem pathItem) {
		final Map<HttpMethod, Operation> operationMap = new HashMap<>();
		operationMap.put(HttpMethod.GET, pathItem.getGet());
		operationMap.put(HttpMethod.POST, pathItem.getPost());
		operationMap.put(HttpMethod.PUT, pathItem.getPut());
		operationMap.put(HttpMethod.DELETE, pathItem.getDelete());
		operationMap.put(HttpMethod.PATCH, pathItem.getPatch());
		operationMap.put(HttpMethod.HEAD, pathItem.getHead());
		operationMap.put(HttpMethod.TRACE, pathItem.getTrace());
		operationMap.put(HttpMethod.OPTIONS, pathItem.getOptions());
		return operationMap;
	}
	
	private ApiSecurity getSecurityDetails(final Operation operation, final Map<String, SecurityScheme> schemas) {
		ApiSecurity apiSecurityResult = null;
		if(!CollectionUtils.isEmpty(operation.getSecurity()) && schemas != null) {
			apiSecurityResult = operation.getSecurity().stream().map(o -> {
				return o.entrySet().stream().filter(e -> schemas.containsKey(e.getKey())).map(e -> {
						ApiSecurity apiSecurity  = new ApiSecurity();
						final SecurityScheme schema = schemas.get(e.getKey());
						//TODO: Only OAUTH security is supported now.
						if(SecurityScheme.Type.OAUTH2.equals(schema.getType()) && schema != null) {
							apiSecurity.setApiScopes(e.getValue());
							apiSecurity.setSecurityDetail(schema.getFlows());
						} else {
							List<String> securityDeatils =new ArrayList<>();
							securityDeatils.add(schema.getScheme());
							apiSecurity.setApiScopes(securityDeatils);;
							LOG.warn("Security type not supported {}", schema.getType());
						}
					return apiSecurity;
				}).filter(Objects::nonNull).findFirst().orElse(null);
			}).filter(Objects::nonNull).findFirst().orElse(null);
		}
		return apiSecurityResult;
	}
	/**
	 * To fetch all the operation id's available in the API documentation. 
	 */
	private Set<String> getAllOperationIds(final OpenAPI openAPI) {
		Set<String> operationIds = null;
		if (openAPI != null && openAPI.getPaths() != null) {
			operationIds = openAPI.getPaths().values().stream().map(s -> {
				return getOperationIds(s.getGet(), s.getPost(), s.getPut(), s.getDelete(), s.getPatch(), s.getHead(),
						s.getTrace(), s.getOptions());
			}).flatMap(Set::stream).collect(Collectors.toSet());
		}
		return operationIds;
	}
	
	/**
	 * To construct the API end-point URL.
	 */
	private List<String> getApiEndpointUrls(final OperationHolder operationHolder, final List<Server> servers) {
		List<String> endpointUrls = null;
		if(!CollectionUtils.isEmpty(servers)) {
			endpointUrls = servers.stream().map(s -> s.getUrl() + operationHolder.getUrlPath()).collect(Collectors.toList());
		}
		return endpointUrls;
	}
	
	/**
	 * To retrieve all the parameters from the API document.
	 */
	private List<ApiParameter> getParameters(final List<Parameter> params) {
		List<ApiParameter> apiParams = null;
		if(!CollectionUtils.isEmpty(params)) {
			apiParams = params.stream().map(p -> {
				final ApiParameter apiParam = new ApiParameter();
				apiParam.setName(p.getName());
				apiParam.setRequired(p.getRequired());
				apiParam.setParamType(p.getIn());
				apiParam.setDataType(p.getSchema().getType());
				return apiParam;
			}).collect(Collectors.toList());
		}
		return apiParams;
	}
	
	/**
	 * To construct the sample JSON API request from the JSON schema. 
	 */
	@SuppressWarnings("rawtypes")
	private Example getAPIRequest(final Map<String, Schema> schemas, final RequestBody requestBody, final String contentType) {
		Example example = null;
		if (requestBody != null) {
			example = getExample(schemas, requestBody.getContent(), contentType);
		}
		return example;
	}

	/**
	 * To construct the sample JSON from the JSON schema. 
	 */
	@SuppressWarnings("rawtypes")
	private Example getExample(final Map<String, Schema> schemas, final Content content, final String contentType) {
		Example example = null;
		if(content != null) {
			Schema<?> schema = null;
			if (content.get(contentType) != null) {
				schema = content.get(contentType).getSchema();
			} else if (content.get("*/*") != null) {
				schema = content.get("*/*").getSchema();
			}
			if(schema != null) {
				example = ExampleBuilder.fromSchema(schema, schemas);
			}
		}
		return example;
	}
	
	/**
	 * To construct the sample JSON API response from the JSON schema.
	 */
	@SuppressWarnings("rawtypes")
	private Example getAPIResponse(final Map<String, Schema> schemas, final ApiResponses apiResponses,
			final String contentType) {
		Example response = null;
		if (apiResponses != null) {
			// Only response status code of 200 is considered as the response.
			// It can also contain other status codes.
			response = apiResponses.entrySet().stream().filter(e -> StringUtils.equals(e.getKey(), "200")).map(e -> {
				return getExample(schemas, e.getValue().getContent(), contentType);
			}).filter(Objects::nonNull).findFirst().orElse(null);

		}
		return response;
	}
	
	/**
	 * Parses the provided YAML file or API Doc URL and converts it into a
	 * OpenAPI object.
	 * 
	 * @param file
	 * @param apiDocUrl
	 * @return
	 * @throws ApiDocParserException
	 */
	private OpenAPI getOpenApi(final MultipartFile file, final String apiDocUrl) throws ApiDocParserException {
		final ParseOptions options = new ParseOptions();
		options.setFlatten(true);
		options.setResolve(true);
		options.setResolveCombinators(true);
		OpenAPI openAPI = null;
		String apiDoc = null;
		try {
			if (file != null) {
				apiDoc = IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8.name());
				LOG.debug("Processing API Doc YAML {}", apiDoc);
				openAPI = new OpenAPIV3Parser().readContents(apiDoc, null, options).getOpenAPI();
			} else {
				LOG.info("Processing API Doc URL {}", apiDocUrl);
				openAPI = new OpenAPIV3Parser().read(apiDocUrl, null, options);
			}
		} catch (Exception e) {
			LOG.error("Exception while parsing the API Doc {}", apiDoc, e);
			final Response<Set<String>> errorResponse = new Response<Set<String>>(MetaConstant.API_PARSER_FAILURE_CODE,
					String.format("Exception while parsing the doc - %s", e.getMessage()), null);
			throw new ApiDocParserException(errorResponse);
		}
		return openAPI;
	}
	
	/**
	 * To get all the operation id's for an end-point because an end-point can have multiple operations like GET, POST etc.
	 * 
	 * @param operations
	 * @return
	 */
	private Set<String> getOperationIds(final Operation... operations) {
		Set<String> operationIds = null;
		if (operations != null) {
			operationIds = Stream.of(operations).filter(Objects::nonNull).map(o -> o.getOperationId()).collect(Collectors.toSet());
		}
		return operationIds;
	}
	
	/**
	 * To fetch the details of operation based on the provided operation id.
	 * 
	 * @param operationId
	 * @param urlPath
	 * @param operations
	 * @return
	 */
	private OperationHolder getOperationById(final String operationId, final String urlPath,
			final Map<HttpMethod, Operation> operations) {
		OperationHolder operation = null;
		if (operations != null && StringUtils.isNotEmpty(operationId)) {
			operation = operations.entrySet().stream()
				.filter(o -> o.getValue() != null && StringUtils.equals(o.getValue().getOperationId(), operationId))
				.map(e -> {
					final OperationHolder operationHol = new OperationHolder();
					operationHol.setOperation(e.getValue());
					operationHol.setUrlPath(urlPath);
					operationHol.setHttpMethod(e.getKey().name());
					return operationHol;
				}).findFirst().orElse(null);
		}
		return operation;
	}
	
	private class OperationHolder {
		private String urlPath;
		private String httpMethod;
		private Operation operation;

		public String getUrlPath() {
			return urlPath;
		}

		public void setUrlPath(String urlPath) {
			this.urlPath = urlPath;
		}

		public Operation getOperation() {
			return operation;
		}

		public void setOperation(Operation operation) {
			this.operation = operation;
		}

		public String getHttpMethod() {
			return httpMethod;
		}

		public void setHttpMethod(String httpMethod) {
			this.httpMethod = httpMethod;
		}
	}

}
