package com.suntecgroup.metaconfig.model;

import java.util.List;

import io.swagger.oas.inflector.examples.models.Example;

public class ApiDocResponse {
	private String httpMethod;
	private List<String> endpointUrl;
	private List<ApiParameter> parameters;
	private ApiSecurity apiSecurity;
	private Example requestBody;
	private Example responseBody;

	public List<String> getEndpointUrl() {
		return endpointUrl;
	}

	public void setEndpointUrl(List<String> endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	public List<ApiParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ApiParameter> parameters) {
		this.parameters = parameters;
	}

	public Example getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(Example requestBody) {
		this.requestBody = requestBody;
	}

	public Example getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(Example responseBody) {
		this.responseBody = responseBody;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public ApiSecurity getApiSecurity() {
		return apiSecurity;
	}

	public void setApiSecurity(ApiSecurity apiSecurity) {
		this.apiSecurity = apiSecurity;
	}
}
