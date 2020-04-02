/*
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.buk.Buk;
import com.suntecgroup.custom.processor.model.buk.EventBuk;
import com.suntecgroup.custom.processor.model.contextparameter.ContextParamData;
import com.suntecgroup.custom.processor.model.contextparameter.ContextParameter;
import com.suntecgroup.custom.processor.model.mappingparameter.InputParamMapping;
import com.suntecgroup.custom.processor.model.mappingparameter.Mapping;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;
import com.suntecgroup.custom.processor.utils.CommonUtils;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.NifiUtils;

/**
 * PreProcessorInvokeBS is a customized processor class for the Nifi
 * PreProcessor. Preprocessor is the part of InvokeBusinessService(InvokeBS)
 * operator and will execute at the beginning of the InvokeBS operator.
 *
 * @version 1.0 10 Sep 2018
 * @author Neeraj Sharma
 */

@Tags({ "preprocessor,invokeBS" })
@CapabilityDescription("PreProcessor to InvokeBS")
public class PreProcessorInvokeBS extends AbstractProcessor {

	private ObjectMapper mapper = null;
	private Gson gson = null;
	private TypeReference<Map<String, Object>> mapTypeRef = null;

	private ComponentLog logger;

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description("Success relationship").build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();

	public static final PropertyDescriptor CONTEXT_PARAMETER = new PropertyDescriptor.Builder()
			.name("Context Parameter").description("Pre Processor CONTEXT_PARAMETER").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor INPUT_BUSINESS_ENTITY = new PropertyDescriptor.Builder()
			.name("Input Business Entity").description("Pre Processor INPUT_BUSINESS_ENTITY").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor SERVICE_NAME = new PropertyDescriptor.Builder().name("Service Name")
			.description("Pre Processor SERVICE_NAME").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor API_NAME = new PropertyDescriptor.Builder().name("Api Name")
			.description("Pre Processor API_NAME").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor INPUT_MAPPING_PARAMETER = new PropertyDescriptor.Builder()
			.name("Input Mapping Parameter").description("Pre Processor INPUT MAPPING Params").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor INPUTBE_DEFINITION = new PropertyDescriptor.Builder()
			.name("InputBE Definition").description("PreProcessor Input Definition").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor INPUT_BE_BUK_ATTRIBUTES = new PropertyDescriptor.Builder()
			.name("Input BE BUK Attributes").description("Input BE BUK attributes array").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OPERATOR_NAME = new PropertyDescriptor.Builder().name("Operator Name")
			.description("PreProcessor Operator Name").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor HTTP_METHOD = new PropertyDescriptor.Builder().name("HTTP Method")
			.description("PreProcessor Http Method ").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor MERGE_SOURCE = new PropertyDescriptor.Builder().name("Merge Source")
			.description("flag for defining the processor as source processor of merge")
			.allowableValues("true", "false").required(true).defaultValue("false")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();
	public static final PropertyDescriptor HEADERS = new PropertyDescriptor.Builder().name("Headers")
			.description("header for preprocessorIBS").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();
	public static final PropertyDescriptor SECURITY = new PropertyDescriptor.Builder().name("Security")
			.description("security for preprocessor ibs").sensitive(true).required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();
	public static final PropertyDescriptor BS_CATEGORY = new PropertyDescriptor.Builder().name("BS Category")
			.description("Type of BS").allowableValues("internal", "external").required(false).defaultValue("internal")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor BATCHABLE = new PropertyDescriptor.Builder().name("isBatchable")
			.description("API is support list of Objects").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	private List<PropertyDescriptor> properties;

	private Set<Relationship> relationships;

	@Override
	protected void init(final ProcessorInitializationContext context) {
		final List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
		properties.add(CONTEXT_PARAMETER);
		properties.add(INPUT_MAPPING_PARAMETER);
		properties.add(INPUT_BUSINESS_ENTITY);
		properties.add(SERVICE_NAME);
		properties.add(API_NAME);
		properties.add(INPUTBE_DEFINITION);
		properties.add(INPUT_BE_BUK_ATTRIBUTES);
		properties.add(HTTP_METHOD);
		properties.add(OPERATOR_NAME);
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		properties.add(MERGE_SOURCE);
		properties.add(HEADERS);
		properties.add(SECURITY);
		properties.add(BS_CATEGORY);
		properties.add(BATCHABLE);
		this.properties = Collections.unmodifiableList(properties);
		final Set<Relationship> relationships = new HashSet<Relationship>();
		this.relationships = Collections.unmodifiableSet(relationships);
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		logger = context.getLogger();
		mapper = new ObjectMapper();
		gson = new GsonBuilder().create();
		mapTypeRef = new TypeReference<Map<String, Object>>() {};
	}

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}

	private String contextParamProperty = null;
	private String inputMappingProperty = null;
	private String inputBeProperty = null;
	private String serviceNameProperty = null;
	private String apiNameProperty = null;
	private String operatorName = null;
	private boolean isMergeSource = false;
	private String bs_category = null;
	private boolean isBatch = false;
	private String header = null;
	private String security = null;
	private boolean isOutputRestChannelExternal = false;
	private InputParamMapping ipmParamMappingJson = null;
	private String inputBEDefination = null;
	private JSONArray arrayBukAttributes = null;
	private JsonProvider jsonProvider = null;
	
	@OnScheduled
	public void onScheduled(final ProcessContext context) throws NifiCustomException {
		contextParamProperty = context.getProperty(CONTEXT_PARAMETER).evaluateAttributeExpressions().getValue();
		inputMappingProperty = context.getProperty(INPUT_MAPPING_PARAMETER).evaluateAttributeExpressions().getValue();
		inputBeProperty = context.getProperty(INPUT_BUSINESS_ENTITY).evaluateAttributeExpressions().getValue();
		serviceNameProperty = context.getProperty(SERVICE_NAME).evaluateAttributeExpressions().getValue();
		logger.info("serviceNameProperty in PreProcessor:" + serviceNameProperty);
		apiNameProperty = context.getProperty(API_NAME).evaluateAttributeExpressions().getValue();
		logger.info("apiNameProperty " + "" + "in PreProcessor:" + apiNameProperty);
		inputBEDefination = context.getProperty(INPUTBE_DEFINITION).evaluateAttributeExpressions().getValue();
		operatorName = context.getProperty(OPERATOR_NAME).evaluateAttributeExpressions().getValue();
		String bukAttributes = context.getProperty(INPUT_BE_BUK_ATTRIBUTES).evaluateAttributeExpressions().getValue();
		arrayBukAttributes = new JSONArray(bukAttributes);
		isMergeSource = context.getProperty(MERGE_SOURCE).evaluateAttributeExpressions().asBoolean();
		bs_category = context.getProperty(BS_CATEGORY).evaluateAttributeExpressions().getValue();
		isBatch = context.getProperty(BATCHABLE).evaluateAttributeExpressions().asBoolean();
		header = context.getProperty(HEADERS).evaluateAttributeExpressions().getValue();
		security = context.getProperty(SECURITY).evaluateAttributeExpressions().getValue();
		isOutputRestChannelExternal = (bs_category.equals("external")
				&& operatorName.startsWith(Constants.OUTPUT_REST_CHANNEL)) ? true : false;
		if (null != inputMappingProperty) {
			try {
				ipmParamMappingJson = mapper.readValue(inputMappingProperty, InputParamMapping.class);
			} catch (IOException e) {
				throw new NifiCustomException("Exception while parsing param mapping property");
			}
		}
		jsonProvider = JsonProvider.provider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.nifi.processor.AbstractProcessor#onTrigger(org.apache.nifi.
	 * processor.ProcessContext, org.apache.nifi.processor.ProcessSession)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.nifi.processor.AbstractProcessor#onTrigger(org.apache.nifi.
	 * processor.ProcessContext, org.apache.nifi.processor.ProcessSession)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
		JSONObject contextParamJson = null;
		List<ContextParameter> listContextParam = null;
		InputParamMapping ipmParamMapping = null;
		ContextParamData contextParamData = null;
		int totalRecordsCount = 0;

		FlowFile flowFile = session.get();

		if (flowFile == null) {
			return;
		}

		boolean isMarker = Boolean.parseBoolean(flowFile.getAttribute(Constants.IS_MARKER));
		if (isMarker) {
			if (isMergeSource) {
				session.transfer(flowFile, REL_SUCCESS);
				return;
			} else {
				session.remove(flowFile);
				return;
			}
		}

		try {
			totalRecordsCount = CommonUtils.getEventsCount(session, flowFile, logger);
		} catch (NifiCustomException exception) {
			logger.error("Error setting flow file count in Preprocessor" + exception.getMessage(), exception);
		}

		List<EventBuk> inputBEBUKList = new ArrayList<EventBuk>();
		EventBuk invalidEventBuk = new EventBuk();

		String sessionId = context.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
		String strRunNumber = context.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
		String inputBEName = flowFile.getAttribute(Constants.BENAME);
		JSONObject inputBEDefinitionJSON = null;
		
		if(!StringUtils.isBlank(inputBeProperty)){
			session.putAttribute(flowFile, Constants.BENAME, inputBeProperty);
		}
		
		try {
			if (inputBEDefination != null) {
				inputBEDefinitionJSON = new JSONObject(inputBEDefination);
			}
		} catch (Exception exception) {
			logger.error("error occured while reading  inputBe property: " + exception.getMessage(), exception);
			route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
					exception.getMessage(), isMergeSource);
			return;
		}
		// Adding new flag to mark business failures
		List<String> failedResult = new ArrayList<>();
		// Invoke BS external for Post request
		if ((!StringUtils.isBlank(inputMappingProperty))
				&& (operatorName.startsWith(Constants.INVOKE_EXTERNAL) || isOutputRestChannelExternal)) {
			Map<String, Object> jsonHeaderMap = new HashMap<>();
			Map<String, Object> jsonSecurityMap = new HashMap<>();
			Map<String, Object> jsonMap = new HashMap<>();

			if (inputBEDefinitionJSON != null) {
				if (inputBEDefinitionJSON.length() > 0) {

					JSONArray inputBEObjArray = getFlowFileJSONObj(flowFile, session);
					StringWriter writer = null;
					for (int i = 0; i < inputBEObjArray.length(); i++) {
						writer = new StringWriter();
						try (JsonGenerator tempMap = jsonProvider.createGenerator(writer)){
							JSONObject inputBEObj = inputBEObjArray.getJSONObject(i);
							JSONObject innerInputBEDefinitionJSON = inputBEDefinitionJSON.getJSONObject("inputBe");
							JSONObject propertiesObject = innerInputBEDefinitionJSON.getJSONObject(Constants.PROPERTIES);
							validatebyDefinition(propertiesObject, null, toMap(inputBEObj), tempMap, false, failedResult);
						}
					}
				}
			}

			if (!StringUtils.isBlank(inputMappingProperty)) {
				if (failedResult.isEmpty()) {
					ipmParamMapping = ipmParamMappingJson;
					JSONObject procesObj = null;
					JSONObject finalJson = new JSONObject();
					JSONArray finalJsonArray = new JSONArray();
					boolean isQueryParam = false, isPathParam = false;

					if (null != ipmParamMapping.getInputJSON()) {
						String inputParam = ipmParamMapping.getInputJSON().toString();
						finalJsonArray = getFinalJson(finalJson, inputParam, session, flowFile, "Post", failedResult);
					}
					if (null != ipmParamMapping.getQueryParam() && !ipmParamMapping.getQueryParam().isEmpty()) {
						String inputParam = ipmParamMapping.getQueryParam().toString();
						getFinalJson(finalJson, inputParam, session, flowFile, "Query", failedResult);
						isQueryParam = true;
					}
					if (null != ipmParamMapping.getPathParam() && !ipmParamMapping.getPathParam().isEmpty()) {
						String inputParam = ipmParamMapping.getPathParam().toString();
						getFinalJson(finalJson, inputParam, session, flowFile, "Path", failedResult);
						isPathParam = true;
					}
					JsonWriter writer = null;
					OutputStream preprocessorOutput = null;
					InputStream flowFileInputStream = null;
					try {
						if (isQueryParam || isPathParam) {
							jsonMap = mapper.readValue(finalJson.toString(), mapTypeRef);
						}

						if (null != header) {
							JSONObject finalJsonHeader = new JSONObject();
							getFinalJson(finalJsonHeader, header, session, flowFile, "Header", failedResult);
							jsonHeaderMap = mapper.readValue(finalJsonHeader.toString(), mapTypeRef);
						}
						JSONObject securityJson = new JSONObject();
						if (null != security) {
							getFinalJson(securityJson, security, session, flowFile, "Security", failedResult);
							jsonSecurityMap = mapper.readValue(securityJson.toString(), mapTypeRef);
						}
						if (isBatch) {
							String str = finalJsonArray.toString();
							flowFileInputStream = new ByteArrayInputStream(str.getBytes());
							session.importFrom(flowFileInputStream, flowFile);
						} else {
							if (finalJson != null && finalJson.length() > 0) {
								String str = finalJson.toString();
								flowFileInputStream = new ByteArrayInputStream(str.getBytes());
								session.importFrom(flowFileInputStream, flowFile);
								EventBuk eventBuk = new EventBuk();
								for (int index = 0; index < arrayBukAttributes.length(); index++) {
									String key = arrayBukAttributes.getString(index);
									if (null != procesObj && procesObj.has(key)) {
										Object value = procesObj.get(key);
										if (!procesObj.has(key) || null == value
												|| StringUtils.isEmpty(value.toString())) {
											invalidEventBuk.addBuk(new Buk(key, ""));
										} else {
											eventBuk.addBuk(new Buk(key, value.toString()));
										}
									}
								}
								inputBEBUKList.add(eventBuk);
							}
						}
					} catch (Exception ex) {
						logger.error("Exception occured at preprocessor: " + ex.getMessage(), ex);
						closeStream(null, preprocessorOutput, null, writer);
						route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
								ex.getMessage());
						return;
					} finally {
						closeStream(flowFileInputStream, preprocessorOutput, null, writer);
					}
				}
			}
			try {
				if (failedResult != null && failedResult.size() > 0) {
					throw new NifiCustomException(
							operatorName + " failed due to missing mapping value for mandatory attributes: "
									+ failedResult.toString());
				} else {
					String test = NifiUtils.convertObjectToJsonString(inputBEBUKList,logger);
					session.putAttribute(flowFile, Constants.INPUT_BUK,test);
					session.putAllAttributes(flowFile, convertMapType(jsonMap));
					session.putAllAttributes(flowFile, convertMapType(jsonHeaderMap));
					session.putAllAttributes(flowFile, convertMapType(jsonSecurityMap));
					updateEventCount(session, flowFile, context, totalRecordsCount);
					session.transfer(flowFile, REL_SUCCESS);
					session.commit();
				}
			} catch (NifiCustomException nifiCustomException) {
				logger.error("Error occurred at Preprocesser :: " + nifiCustomException.getMessage(),
						nifiCustomException);
				route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
						nifiCustomException.getMessage());
			} catch (Exception ex) {
				logger.error("Error occurred at Preprocesser :: " + ex.getMessage(), ex);
				route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR, ex.getMessage());
			}

		} else {
			try {

				if (StringUtils.isEmpty(sessionId) || "0".equals(sessionId)) {
					throw new NifiCustomException("Invalid session Id");
				} else {
					sessionId = sessionId.trim();
					session.putAttribute(flowFile, Constants.ATTR_SESSION_ID, sessionId);
				}
				// Run number validation

				int runNumber = Integer.parseInt(strRunNumber);
				if (runNumber < 1) {
					throw new NifiCustomException("Invalid run number");
				} else {
					session.putAttribute(flowFile, Constants.ATTR_RUN_NUMBER, String.valueOf(runNumber));
				}
			} catch (Exception ex) {
				logger.error("error occured while reading processing session details: " + ex.getMessage(), ex);
				route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR, ex.getMessage(),
						isMergeSource);
				return;
			}

			try {
				if (contextParamProperty != null) {
					contextParamData = mapper.readValue(contextParamProperty, ContextParamData.class);
				}
			} catch (Exception exception) {
				logger.error("error occured  while reading context param property: " + exception.getMessage(),
						exception);
				route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
						exception.getMessage(), isMergeSource);
				return;
			}

			if (contextParamData != null) {
				listContextParam = contextParamData.getContextParameters();
				if (listContextParam != null) {
					ipmParamMapping = ipmParamMappingJson;
				}
			} else if (null != inputMappingProperty) {
					ipmParamMapping = ipmParamMappingJson;
			}

			if (ipmParamMapping != null) {
				contextParamJson = new JSONObject();
			}

			if (contextParamJson != null && contextParamJson.length() == 0) {
				// update context params using input mapping params
				for (Iterator<ContextParameter> iterator = listContextParam.iterator(); iterator.hasNext();) {
					ContextParameter contextParameter = (ContextParameter) iterator.next();

					Object contextParamValue = getMappedIPMValue(contextParameter.getName(), flowFile, ipmParamMapping);
					if (null != contextParamValue) {
						if(contextParameter.isCollection()){
							JSONArray array = new JSONArray();
							array.put(contextParamValue);
							contextParamJson.put(contextParameter.getName(), array);
						}else{
						contextParamJson.put(contextParameter.getName(), contextParamValue);
						}
					} else {
						if(contextParameter.isCollection()){
							JSONArray array = new JSONArray();
							contextParamJson.put(contextParameter.getName(), array);
						}else{
						contextParamJson.put(contextParameter.getName(), "");
					}
				}
			}
			}

			// Reading & Writing.

			FlowFile flowFileOutput = session.clone(flowFile);
			InputStream preprocessorInput = session.read(flowFile);
			OutputStream preprocessorOutput = session.write(flowFileOutput);
			JsonReader reader = null;
			JsonWriter writer = null;
			failedResult = new ArrayList<String>();
			Map<String, String> processorOuputMap = new HashMap<>();
			if (preprocessorInput != null) {
				try {
					List<Map<String, Object>> processorOuputList = new LinkedList<Map<String, Object>>();
					reader = new JsonReader(new InputStreamReader(preprocessorInput, Constants.UTF_ENCODING));
					writer = new JsonWriter(new OutputStreamWriter(preprocessorOutput, Constants.UTF_ENCODING));

					Type mapType = mapTypeRef.getType();
					
					if (!StringUtils.isBlank(inputBeProperty)) {
						reader.beginArray();
						writer.beginObject();
						writer.name(StringUtils.uncapitalize(inputBeProperty));
						writer.beginArray();
						while (reader.hasNext()) {

							// Read data into object model
							Map<String, Object> jsonRecord = gson.fromJson(reader, mapType);
							// Preprocessor logic starts.
							processorOuputList = processRecord(jsonRecord, inputBEDefinitionJSON, failedResult);
							// Preprocessor logic ends.

							if (failedResult != null && failedResult.size() > 0) {
								// isBreak = true;
								// processorOuputList = null;
								continue;
							}

							// writing
							if (processorOuputList != null && processorOuputList.size() > 0
									&& !StringUtils.isBlank(inputBeProperty)) {
								Map<String, Object> resultMap = processorOuputList.get(0);
								gson.toJson(resultMap, mapType, writer);
							}

							// construct BUK list @JOHN starts here
							EventBuk eventBuk = new EventBuk();
							for (int index = 0; index < arrayBukAttributes.length(); index++) {
								String key = arrayBukAttributes.getString(index);
								Object value = jsonRecord.get(key);
								if (!jsonRecord.containsKey(key) || null == value
										|| StringUtils.isEmpty(value.toString())) {
									// BUK attribute is not available/value is
									// empty
									invalidEventBuk.addBuk(new Buk(key, ""));
								} else {
									eventBuk.addBuk(new Buk(key, value.toString()));
								}
							}
							inputBEBUKList.add(eventBuk);
							// construct BUK list @JOHN ends here
						}

						if (invalidEventBuk.getBuk() != null && invalidEventBuk.getBuk().size() > 0) {
							throw new NifiCustomException("Failed due to Invalid BUK!");
						}

						if (true) {
							reader.endArray();
							writer.endArray();
						}
					} else {
						writer.beginObject();
					}

					// Writing context details.
					writer.name(Constants.context);
					writer.beginObject();
					writer.name(Constants.contextParameters);
					if (null == contextParamJson) {
						writer.beginObject();
						writer.endObject();
					} else {
						final String val = contextParamJson.toString();
						Map<String, Object> result = (Map<String, Object>) mapper.readValue(val,
								HashMap.class);
						gson.toJson(result, mapType, writer);
					}

					// validationContext
					writer.name(Constants.validationContext);
					writer.beginObject();
					writer.name(Constants.failFast);
					writer.value(true);
					writer.endObject();
					// close context
					writer.endObject();

					// reqeust body close
					writer.endObject();

				} catch (NifiCustomException nifiException) {
					logger.error("Exception Occurred:: " + nifiException.getMessage(), nifiException);
					closeStream(preprocessorInput, preprocessorOutput, reader, writer);
					session.remove(flowFileOutput);
					route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
							nifiException.getMessage(), isMergeSource);
					return;
				} catch (Exception ex) {
					logger.error("Exception occured at preprocessor: " + ex.getMessage(), ex);
					closeStream(preprocessorInput, preprocessorOutput, reader, writer);
					session.remove(flowFileOutput);
					route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
							ex.getMessage(), isMergeSource);
					return;
				} finally {
					closeStream(preprocessorInput, preprocessorOutput, reader, writer);
				}
			}

			try {
				if (failedResult != null && failedResult.size() > 0) {
					throw new NifiCustomException(
							operatorName + " failed due to missing mapping value for mandatory attributes: "
									+ failedResult.toString());
				} else {
					session.putAttribute(flowFileOutput, Constants.INPUT_BUK,
							NifiUtils.convertObjectToJsonString(inputBEBUKList,logger));
					session.putAllAttributes(flowFileOutput, processorOuputMap);
					session.remove(flowFile);
					updateEventCount(session, flowFileOutput, context, totalRecordsCount);
					route(flowFileOutput, REL_SUCCESS, context, session, null, null, null, isMergeSource);
				}
			} catch (NifiCustomException nifiCustomException) {
				logger.error("Error occurred at Preprocesser :: " + nifiCustomException.getMessage(),
						nifiCustomException);
				session.remove(flowFileOutput);
				route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
						nifiCustomException.getMessage(), isMergeSource);
			} catch (Exception ex) {
				logger.error("Error occurred at Preprocesser :: " + ex.getMessage(), ex);
				session.remove(flowFileOutput);
				route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR, ex.getMessage(),
						isMergeSource);
			}
		}
	}

	private Map<String, String> convertMapType(Map<String, Object> jsonMap) throws JsonProcessingException {
		Map<String,String> compatibleJson = new HashMap<String, String>();
		Set<String> keys = jsonMap.keySet();
		for(String key:keys){
			Object complexObj = jsonMap.get(key);
			String jsonString = complexObj.toString();
			compatibleJson.put(key, jsonString);
		}
		return compatibleJson;
	}

	public JSONObject contructRequestPayload(JSONObject flowFileJSON, JSONObject inputJSONObj, JSONObject finalJson,
			JSONArray inputObjJSONArray, FlowFile flowFile, String inputType, ProcessSession session, List<String> failedResult) {
		Iterator<?> inputObjIterator = inputJSONObj.keys();
		String inputObjkey = null;
		String objDataType = null;
		JSONObject reqObj = new JSONObject();
		while (inputObjIterator.hasNext()) {
			inputObjkey = (String) inputObjIterator.next();
			objDataType = inputJSONObj.get(inputObjkey).getClass().getSimpleName();
			Object outputJsonval =inputJSONObj.get(inputObjkey);
			String[] keys = null;
			Map<String, Object> inputObjRecord = new HashMap<>();
			inputObjRecord.put(inputObjkey, inputJSONObj.get(inputObjkey));
			String mapKey = String.valueOf(inputJSONObj.get(inputObjkey));
			if (outputJsonval instanceof String) {
				try {
					// get key value after removing the type conversion details
					// if present
					String val = null;

					val = (String) NifiUtils.getTypeConverted((String) outputJsonval, null, true);
					mapKey = (String) NifiUtils.getTypeConverted(mapKey, null, true);

					if (!inputObjkey.equalsIgnoreCase("objName")) {
						// objDataType =
						// flowFileJSON.get(mapKey).getClass().getSimpleName();
						Object finalValue = null;
						if (val.startsWith(Constants.PV)) {
							Mapping map = new Mapping();
							String[] processVariable = val.split(Constants.PV);
							map.setProcessVariable(processVariable[1]);
							finalValue = getFlowFileAttributes(flowFile, map, processVariable[1]);
							if (finalValue == null) {
								finalValue = getFlowFileAttributes(flowFileJSON, map, processVariable[1]);
							}
							finalJson.put(inputObjkey, finalValue);
							reqObj.put(inputObjkey, finalValue);
						} else if (val.startsWith(Constants.EV)) {
							String[] enterValue = val.split(Constants.EV);
							finalValue = enterValue[1];
							finalJson.put(inputObjkey, finalValue);
							reqObj.put(inputObjkey, finalValue);
						} else if (inputType.equalsIgnoreCase("Security")) {
							finalValue = val;
						} else {
							if (flowFileJSON.has(mapKey)) {
								if (Constants.INTEGER.equalsIgnoreCase(objDataType)) {
									finalValue = flowFileJSON.getInt(mapKey);
								} else if (Constants.String.equalsIgnoreCase(objDataType)) {
									finalValue = flowFileJSON.getString(mapKey);
								} else if (Constants.BOOLEAN.equalsIgnoreCase(objDataType)) {
									finalValue = flowFileJSON.getBoolean(mapKey);
								}
							} else {
								String error = "Failed to fetch value from Flowfile data for attribute " + mapKey
										+ ". Since " + mapKey + " is null or empty "
										+ " in the Request Flow file record with buk ";
								failedResult.add(error);
							}
						}
						if (null != finalValue) {
							// apply type conversion on final value if
							// applicable
							finalValue = NifiUtils.getTypeConverted((String) outputJsonval, finalValue, false);
							finalJson.put(inputObjkey, finalValue.toString());
							reqObj.put(inputObjkey, finalValue.toString());
						}
					}
				} catch (Exception ex) {
					logger.error("Exception occured while fetvhing value from flow file"+ex.getMessage());
				}
			} else {
				JSONArray jsonArrVal = (JSONArray) inputJSONObj.get(inputObjkey);
				JSONArray childFinalJSONArray = new JSONArray();
				HashMap<String, JSONArray> multipleArrayholder = new HashMap<>();
				for (int y = 0; y < jsonArrVal.length(); y++) {
					String val = (String) jsonArrVal.get(y);
					if (val.startsWith("obj")) {
						objDataType = Constants.JSON_OBJECT;
						keys = val.split("_");
					} else if (val.startsWith("arr")) {
						objDataType = Constants.JSON_ARRAY;
						keys = val.split("_");
					}

					if (objDataType.equalsIgnoreCase(Constants.JSON_OBJECT)) {
						JSONObject childFlowFileJSON = new JSONObject();
						JSONObject childObj = new JSONObject();
						JSONObject childJson = new JSONObject();
						JSONArray inputMappingJsonArray = (JSONArray) inputObjJSONArray.get(1);
						for (int i = 0; i < inputMappingJsonArray.length(); i++) {
							JSONObject item = inputMappingJsonArray.getJSONObject(i);
							if (val.equalsIgnoreCase(item.get("objName").toString())) {
								childObj = item;
								break;
							}
						}

						Object ChildObject = getChildFlowFileJson(keys, flowFileJSON, Constants.JSON_OBJECT, session,
								flowFile,objDataType);

						if (ChildObject instanceof JSONObject) {
							childFlowFileJSON = (JSONObject) ChildObject;
						} else if (ChildObject instanceof JSONArray) {
							childFlowFileJSON = (JSONObject) ((JSONArray) ChildObject).get(0);
						}
						childObj = contructRequestPayload(childFlowFileJSON, childObj, childJson, inputObjJSONArray,
								flowFile, inputType, session, failedResult);
						if (childObj.length() > 0) {
							if (!StringUtils.equalsIgnoreCase("BeRoot", inputObjkey)) {
								if(reqObj.has(inputObjkey)){
									childObj = includeAdditionalPramas(childObj, (JSONObject)reqObj.get(inputObjkey));
								}
								finalJson.put(inputObjkey, childObj);
								reqObj.put(inputObjkey, childObj);
							} else {
								Iterator childObjItr = childObj.keys();
								while (childObjItr.hasNext()) {
									String childKey = (String) childObjItr.next();
									Object childValue = childObj.get(childKey);
									finalJson.put(childKey, childValue);
									reqObj.put(childKey, childValue);
								}
							}
						}
						// reqObj.put(inputObjkey, childObj);
					} else if (objDataType.equalsIgnoreCase(Constants.JSON_ARRAY)) {
						JSONObject childObj = new JSONObject();
						JSONArray childFlowJSONArray = (JSONArray) getChildFlowFileJson(keys, flowFileJSON,
								Constants.JSON_ARRAY, session, flowFile,objDataType);
						JSONArray inputMappingJsonArray = (JSONArray) inputObjJSONArray.get(1);
						for (int i = 0; i < inputMappingJsonArray.length(); i++) {
							JSONObject item = inputMappingJsonArray.getJSONObject(i);
							if (val.equalsIgnoreCase(item.get("objName").toString())) {
								childObj = item;
								break;
							}
						}
						JSONArray tempArray = new JSONArray();
						for (int count = 0; count < childFlowJSONArray.length(); count++) {
							JSONObject childJsonItem = childFlowJSONArray.getJSONObject(count);
							JSONObject finalchildJson = new JSONObject();
							JSONObject finalChildObj = new JSONObject();
							finalChildObj = contructRequestPayload(childJsonItem, childObj, finalchildJson,
									inputObjJSONArray, flowFile, inputType, session, failedResult);
							if (finalChildObj.length() > 0) {
								if (StringUtils.equalsIgnoreCase("BeRoot", inputObjkey)) {
									Iterator childObjItr= finalChildObj.keys();
									while(childObjItr.hasNext()){
										String childKey = (String) childObjItr.next();
										Object childValue = finalChildObj.get(childKey);
										finalJson.put(childKey, childValue);
										reqObj.put(childKey, childValue);
									}
								}else{
									tempArray.put(finalChildObj);
									childFinalJSONArray.put(finalChildObj);
									multipleArrayholder.put(val, tempArray);
								}
							}
						}
						
						if (childFinalJSONArray.length() > 0) {
							if (!StringUtils.equalsIgnoreCase("BeRoot", inputObjkey)) {
								if(multipleArrayholder.size() == 2){
									childFinalJSONArray = normaliseArray(multipleArrayholder);
								}
								finalJson.put(inputObjkey, childFinalJSONArray);
								reqObj.put(inputObjkey, childFinalJSONArray);
							}
						}
					}
				}
			}
		}
		return reqObj;
	}

	private JSONArray normaliseArray(HashMap<String, JSONArray> multipleArrayholder) {
		String combinedKey = multipleArrayholder.keySet().toString();
		Iterator itr = multipleArrayholder.values().iterator();
		JSONArray arrSet1 = (JSONArray) itr.next();
		JSONArray arrSet2 =  (JSONArray) itr.next();
		JSONArray normalisedSet = new JSONArray();
		for (int i = 0; i < arrSet1.length(); i++) {
			JSONObject objA = (JSONObject) arrSet1.get(i);
			for (int j = 0; j < arrSet2.length(); j++) {
				JSONObject objB = (JSONObject) arrSet2.get(j);
				JSONObject objC = new JSONObject();
				Iterator aItr = objA.keys();
				Iterator bItr = objB.keys();
				while (aItr.hasNext()) {
					String key = (String) aItr.next();
					objC.put(key, objA.get(key));
				}
				while (bItr.hasNext()) {
					String key = (String) bItr.next();
					objC.put(key, objB.get(key));
				}
				// add normalised entry to array
				normalisedSet.put(objC);
			}
		}
		multipleArrayholder.clear();
		multipleArrayholder.put(combinedKey, normalisedSet);
		return normalisedSet;
	}

	private JSONObject includeAdditionalPramas(JSONObject newObject, JSONObject previousObject) {
		Iterator it = newObject.keys();
		while(it.hasNext()){
			String key = (String) it.next();
			previousObject.put(key, newObject.get(key));
		}
		
		return previousObject;
	}

	private Object getChildFlowFileJson(String[] oldKeys, JSONObject flowFileJSON, String type, ProcessSession session,
			FlowFile flowFile, String objDataType) {

		Object finalObject = null;
		int depth = 0;
		String pathKey = oldKeys[oldKeys.length - 1];
		String[] keys = pathKey.split("-");
		try {
			if (keys[keys.length - 1].startsWith("pv")) {
				JSONArray pvArray = null;
				JSONObject pvObject = null;
				String processVariableStr = flowFile.getAttribute(keys[keys.length - 2]);
				if (processVariableStr.startsWith("[")) {
					pvArray = new JSONArray(processVariableStr);
					finalObject = pvArray.get(0);
				} else if (processVariableStr.startsWith("{")) {
					pvObject = new JSONObject(processVariableStr);
					finalObject = pvObject;
				} else {
					throw new Exception("Unexpected type received in PV, Expected: array or object");
				}
				if (keys.length < 3) {
					// minimum length is 2, which gives root level object,hence
					// return
					return finalObject;
				} else {
					depth = 3;
				}
			} else {
				final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				session.exportTo(flowFile, bytes);
				final String flowFileContent = bytes.toString();
				JSONArray flowFileJSONArray = new JSONArray(flowFileContent);
				finalObject = flowFileJSONArray.get(0);
				depth = 2;
			}
			for (int i = (keys.length - depth); i >= 0; i--) {
				if (finalObject instanceof JSONObject) {
					Object entry = ((JSONObject) finalObject).get(keys[i]);
					if (entry instanceof JSONArray) {
						if(objDataType.equalsIgnoreCase(Constants.JSON_OBJECT)){
							//Json object should not be contained inside an array
							throw new Exception("object found inside an array but not able to fetch from mapping");
						}else{
							JSONArray arrayEntry = (JSONArray) entry;
							finalObject = arrayEntry;
						}
					} else {
						finalObject = (JSONObject) entry;
					}
				}else if((finalObject instanceof JSONArray)){
					JSONArray finalObjectAsArray = ((JSONArray) finalObject);
					JSONArray finalNode = new JSONArray();
					for (int j = 0; j < finalObjectAsArray.length(); j++) {
						Object entry = ((JSONObject) finalObjectAsArray.get(j)).get(keys[i]);
						if (entry instanceof JSONArray) {
							JSONArray arrayEntry = (JSONArray) entry;
							for(int l=0; l<arrayEntry.length(); l++){
								finalNode.put(arrayEntry.get(l));
							}
						} else {
							finalObject = (JSONObject) entry;
							finalNode.put(finalObject);
						}
					}
					finalObject = finalNode;
				}
			}
			if (type.equals(Constants.JSON_ARRAY) && !(finalObject instanceof JSONArray)) {
				return new JSONArray().put(finalObject);
			}
		} catch (Exception ex) {
			logger.error("Error occured while fetching the inner object" + ex);
		}
		return finalObject;
	}

	public void closeStream(InputStream preprocessorInput, OutputStream preprocessorOutput, JsonReader reader,
			JsonWriter writer) {
		try {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (preprocessorInput != null) {
				preprocessorInput.close();
			}

			if (preprocessorOutput != null) {
				preprocessorOutput.close();
			}
		} catch (IOException e) {
			logger.debug("Exception occured while closing the stream " + e.getMessage(), e);
		}
	}

	/**
	 * This method would read and process the flowfile content records.
	 * 
	 * @param inputRecord
	 *            - Map object with input record
	 * @return List - validated inputrecord as per inputbe definition.
	 */
	private List<Map<String, Object>> processRecord(Map<String, Object> inputRecord, JSONObject inputDefinition, List<String> failedResult) {

		List<Map<String, Object>> successLst = new LinkedList<Map<String, Object>>();

		if (inputRecord != null && inputRecord.size() > 0) {
			successLst = constructRecordsforEffectiveBE(successLst, inputDefinition, inputRecord, failedResult);

		}
		return successLst;
	}


	/**
	 * getMappedIPMValue-fetch context parameter value either from flow file
	 * attributes or from input param mapping
	 * 
	 * @param isFlowFailed
	 * @param name
	 * @param inputParamMapping
	 * @param context
	 * @param flowFile
	 * @param session
	 * @return String
	 */
	private Object getMappedIPMValue(String cantextParam, FlowFile flowFile, InputParamMapping ipmParamMapping) {

		List<Mapping> listInputParam = ipmParamMapping.getInputMapping();
		if (listInputParam != null) {

			if (listInputParam != null && listInputParam.size() > 0) {
				for (Mapping mappingParam : listInputParam) {
					if (cantextParam.equalsIgnoreCase(mappingParam.getContextVariable())) {
						if (mappingParam.getSelectedKey().equalsIgnoreCase(Constants.processVariable)) {
							String processVariableStr = flowFile.getAttribute(mappingParam.getProcessVariable());
							if (!StringUtils.isBlank(processVariableStr)) {
								try {
									ProcessVariable processVariable = mapper.readValue(processVariableStr,
											ProcessVariable.class);
									if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_PRIMITIVE,
											processVariable.getType().getTypeCategory())) {
										switch (processVariable.getType().getTypeName().toLowerCase()) {
										case Constants.dataTypeNumber:
											return processVariable.getValue().getIntValue();
										case Constants.dataTypeString:
											return processVariable.getValue().getStringValue();
										case Constants.dataTypeBoolean:
											return processVariable.getValue().getBooleanValue();
										case Constants.dataTypeDate:
											return processVariable.getValue().getDateValue();
										default:
											break;
										}
									} else if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_BE,
											processVariable.getType().getTypeCategory())) {
										JSONObject jsonObject = new JSONObject(processVariable.getValue().getBeValue());
										return jsonObject;
									}

								} catch (IOException e) {
									logger.error("IOException occurred at getMappedIPMValue :: " + e.getMessage(), e);
								}
								return null;
							} else {
								return null;
							}
						} else {
							switch (mappingParam.getType().toLowerCase()) {
							case Constants.dataTypeNumber:
								return mappingParam.getValue().getIntValue();
							case Constants.dataTypeString:
								return mappingParam.getValue().getStringValue();
							case Constants.dataTypeBoolean:
								return mappingParam.getValue().getBooleanValue();
							case Constants.dataTypeDate:
								return mappingParam.getValue().getDateValue();
							default:
								break;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void route(FlowFile flowfile, Relationship relationship, final ProcessContext context,
			final ProcessSession session, String beName, String errorType, String errorMessage, boolean isMergeSource) {
		if (StringUtils.equalsIgnoreCase(REL_FAILURE.getName(), relationship.getName())) {
			// Adding condition for merge operator to send marker file, in case
			// of failure
			if (isMergeSource) {
				FlowFile flowFile = NifiUtils.cloneFlowfileWithoutContent(flowfile, session, logger);
				session.transfer(flowFile, REL_SUCCESS);
			}
			// Update failure details in flowfile attributes
			flowfile = NifiUtils.updateFailureDetails(context, session, flowfile, beName, errorType, errorMessage);
		}
		session.transfer(flowfile, relationship);
		session.commit();
	}

	private void route(FlowFile flowfile, Relationship relationship, final ProcessContext context,
			final ProcessSession session, String beName, String errorType, String errorMessage) {
		if (StringUtils.equalsIgnoreCase(REL_FAILURE.getName(), relationship.getName())) {
			// Update failure details in flowfile attributes
			flowfile = NifiUtils.updateFailureDetails(context, session, flowfile, beName, errorType, errorMessage);
		}
		session.transfer(flowfile, relationship);
		session.commit();
	}

	public void updateJSONProperty(JSONObject jsonObj, JSONObject childJSON, String keyUpdate, Object valueNew,
			boolean isChildCall, List<String> jsonObjectList, JSONObject tmpJSONObj) {
		Iterator<?> iterator;
		JSONObject tmpJSONObj1 = tmpJSONObj;
		if (isChildCall) {
			iterator = childJSON.keys();
		} else {
			iterator = jsonObj.keys();
		}
		String key = null;
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			if ((jsonObj.optJSONArray(key) == null) && (jsonObj.optJSONObject(key) == null)) {
				if ((key.equals(keyUpdate))) {
					JSONObject updateJSONObj = jsonObj;
					JSONArray jsonArrayObj = new JSONArray();
					int index = 0;
					for (int i = 0; i < jsonObjectList.size(); i++) {
						jsonArrayObj.put(i, updateJSONObj);
						String[] filter = jsonObjectList.get(i).split("/");
						if (filter.length > 1) {
							int sub = Integer.parseInt(filter[1]);
							if (sub == 0) {
								index = i;
								tmpJSONObj1 = updateJSONObj;
								updateJSONObj = (JSONObject) updateJSONObj.getJSONArray(filter[0]).get(sub);
							} else {
								JSONObject tempObj = (JSONObject) jsonArrayObj.get(index);
								updateJSONObj = (JSONObject) tempObj.getJSONArray(filter[0]).get(sub);
							}
						} else {
							updateJSONObj = updateJSONObj.getJSONObject(jsonObjectList.get(i));
						}
					}
					updateJSONObj.put(key, valueNew);
					break;
				}
			}
			if (isChildCall) {
				if (childJSON.optJSONObject(key) != null) {
					jsonObjectList.add(key);
					updateJSONProperty(jsonObj, childJSON.getJSONObject(key), keyUpdate, valueNew, true, jsonObjectList,
							tmpJSONObj1);
					jsonObjectList = new ArrayList<>();
				}
				if (childJSON.optJSONArray(key) != null) {
					JSONArray jArray = childJSON.getJSONArray(key);
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject nodeObj = (JSONObject) jArray.get(i);
						jsonObjectList.add(key + "/" + i);
						updateJSONProperty(jsonObj, nodeObj, keyUpdate, valueNew, true, jsonObjectList, tmpJSONObj1);
					}
					jsonObjectList = new ArrayList<>();
				}
			} else {
				if (jsonObj.optJSONObject(key) != null) {
					jsonObjectList.add(key);
					updateJSONProperty(jsonObj, jsonObj.getJSONObject(key), keyUpdate, valueNew, true, jsonObjectList,
							tmpJSONObj1);
					jsonObjectList = new ArrayList<>();
				}
				if (jsonObj.optJSONArray(key) != null) {
					JSONArray jArray = jsonObj.getJSONArray(key);
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject nodeObj = (JSONObject) jArray.get(i);
						jsonObjectList.add(key + "/" + i);
						updateJSONProperty(jsonObj, nodeObj, keyUpdate, valueNew, true, jsonObjectList, tmpJSONObj1);
					}
					jsonObjectList = new ArrayList<>();

				}
			}

		}
	}

	private JSONArray getFinalJson(JSONObject finalJson, String inputJSON, ProcessSession session, FlowFile flowFile,
			String inputType, List<String> failedResult) {
		JSONObject inputJSONObj = null;
		JSONObject procesObj = null;
		JSONArray inputObjJSONArray = new JSONArray();
		/* if("[".startsWith(inputJSON)){ */
		if (StringUtils.startsWith(inputJSON, "[")) {
			inputObjJSONArray = new JSONArray(inputJSON);
			if (inputObjJSONArray.length() > 0) {
				inputJSONObj = (JSONObject) inputObjJSONArray.get(0);
			}
		} else {
			inputJSONObj = new JSONObject(inputJSON);
			inputObjJSONArray.put(inputJSONObj);
		}
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		session.exportTo(flowFile, bytes);
		final String flowFileContent = bytes.toString();
		JSONArray flowFileJSONArray = new JSONArray(flowFileContent);
		JSONArray requestPlayLoadArray = new JSONArray();
		if (null != inputJSONObj) {
			for (int j = 0; j < flowFileJSONArray.length(); j++) {
				procesObj = (JSONObject) flowFileJSONArray.get(0);
				JSONObject item = contructRequestPayload(procesObj, inputJSONObj, finalJson, inputObjJSONArray,
						flowFile, inputType, session, failedResult);
				requestPlayLoadArray.put(j, item);
			}
		}
		return requestPlayLoadArray;
	}

	private Object getFlowFileAttributes(Object flowFile, Mapping processMap, String key) {
		String processVariableStr = "";
		if (flowFile instanceof FlowFile) {
			processVariableStr = ((FlowFile) flowFile).getAttribute(processMap.getProcessVariable());
		} else if (flowFile instanceof JSONObject) {
			processVariableStr = flowFile.toString();
		}
		if (!StringUtils.isEmpty(processVariableStr)) {
			try {
				ProcessVariable processVariable = mapper.readValue(processVariableStr, ProcessVariable.class);
				if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_PRIMITIVE,
						processVariable.getType().getTypeCategory())) {
					switch (processVariable.getType().getTypeName().toLowerCase()) {
					case Constants.dataTypeNumber:
						return processVariable.getValue().getIntValue();
					case Constants.dataTypeString:
						return processVariable.getValue().getStringValue();
					case Constants.dataTypeBoolean:
						return processVariable.getValue().getBooleanValue();
					case Constants.dataTypeDate:
						return processVariable.getValue().getDateValue();
					default:
						break;
					}
				} else if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_BE,
						processVariable.getType().getTypeCategory())) {
					JSONObject obj = null;
					// value will be present inside the BE, can be array or
					// object
					String beValue = processVariable.getValue().getBeValue();
					if (beValue.startsWith("{")) {
						obj = new JSONObject(beValue);
					} else if(beValue.startsWith("[")){
						JSONArray arr = new JSONArray(beValue);
						obj = (JSONObject) arr.get(0);
					}else{
						return null;
					}
					
					return obj.get(key);
				}
			} catch (IOException e) {
				logger.error("IOException occurred at getMappedIPMValue :: " + e.getMessage(), e);
			}
			return null;
		} else {
			return null;
		}

	}

	private JSONArray getFlowFileJSONObj(FlowFile flowFile, ProcessSession session) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		session.exportTo(flowFile, bytes);
		final String flowFileContent = bytes.toString();
		JSONArray flowFileJSONArray = new JSONArray(flowFileContent);
		// JSONObject inputBEObj = flowFileJSONArray.getJSONObject(0);
		return flowFileJSONArray;
	}

	private void updateEventCount(ProcessSession session, FlowFile flowfile, ProcessContext context, int count) {
		if (context.getName().startsWith(Constants.OUTPUT_REST_CHANNEL)) {
			session.putAttribute(flowfile, "totalRecordsCount", String.valueOf(count));
		}
	}

	/**
	 * This method performs the necessary checks and returns the validated
	 * output as per inputbe definition.
	 * 
	 * @param successLst
	 *            - List with modified record
	 * @param inputDefinition
	 *            - Object of InputDefinitionMapping
	 * @param inputBERecord
	 *            - Map object with input record
	 * @return List - validated inputrecord as per inputbe definition.
	 */
	private List<Map<String, Object>> constructRecordsforEffectiveBE(List<Map<String, Object>> successLst,
			JSONObject inputDefinition, Map<String, Object> inputBERecord, List<String> failedResult) {
		StringWriter writer = new StringWriter();
		try (JsonGenerator tempMap = jsonProvider.createGenerator(writer)) {
			JSONObject inputBEdefiniton = inputDefinition.getJSONObject(Constants.INPUTBE);
			JSONObject propertiesObject = inputBEdefiniton.getJSONObject(Constants.PROPERTIES);
			boolean isCompositeBE = checkPropertiesforBEType(propertiesObject);
			// if composite BE

			tempMap.writeStartObject();
			if (isCompositeBE) {
				String anchorName = inputBEdefiniton.getString("aliasName");
				tempMap.writeStartObject(anchorName);
			}
			List<LinkedHashMap<String, Object>> resultJSON = new ArrayList<LinkedHashMap<String, Object>>();
			validatebyDefinition(propertiesObject, resultJSON, inputBERecord, tempMap, true, failedResult);
			if (isCompositeBE) {
				tempMap.writeEnd();
			}
			tempMap.writeEnd();
			tempMap.flush();
			String processedJSON = writer.toString();
			Map<String, Object> finalJsonMap = gson.fromJson(processedJSON, HashMap.class);
			successLst.add(finalJsonMap);
		} catch (Exception exception) {
			failedResult.add("Exception during the JSON Construction from definition " + exception.getMessage());
			logger.debug("Exception occurred:" + exception.getMessage(), exception);
		}

		return successLst;
	}

	@SuppressWarnings("unchecked")
	private void validatebyDefinition(JSONObject propertiesObject, List<LinkedHashMap<String, Object>> resultJSON,
			Map<String, Object> inputBERecord, JsonGenerator tempMap, boolean isConstruction, List<String> failedResult) {

		Iterator<String> keyset = propertiesObject.keys();
		// tempMap.writeStartObject();
		while (keyset.hasNext()) {
			String attributeName = keyset.next();
			String typeAttributeValue = ""; // value can be String/Object/Array
			// current key can have either composite BE value or properties
			// value
			// Checking by type

			JSONObject attributeValue = (JSONObject) propertiesObject.get(attributeName);

			Iterator<String> propertyObjKeyset = attributeValue.keys();
			while (propertyObjKeyset.hasNext()) {

				String fieldName = propertyObjKeyset.next();
				if (fieldName.equals(Constants.TYPE)) {
					typeAttributeValue = (String) attributeValue.get(fieldName);
					break;
				}

			}

			// implementing seperate logics for Composite BE and simple BE
			if (typeAttributeValue.equals("array") || typeAttributeValue.equals("object")) {
				// attributeName = getNameForCBEforObject(attributeValue);
				// value can be array or object type

				if (typeAttributeValue.equals("array")) {
					JSONObject cbePropertiesObject = getPropertiesForCBE(attributeValue);
					/* tempMap.writeStartArray(attributeName); */
					validateAndProduceResult(attributeName, attributeValue, inputBERecord, tempMap, cbePropertiesObject,
							"array", isConstruction, failedResult);
					// tempMap.writeEnd();
				} else {
					JSONObject cbePropertiesObject = getPropertiesForCBEForObject(attributeValue);
					/* tempMap.writeStartObject(attributeName); */
					validateAndProduceResult(attributeName, attributeValue, inputBERecord, tempMap, cbePropertiesObject,
							"object", isConstruction, failedResult);
					/* tempMap.writeEnd(); */
				}

			} else {
				// iteratively add the contents
				validateAndProduceResult(attributeName, attributeValue, inputBERecord, tempMap, propertiesObject,
						"string", isConstruction, failedResult);
			}

		}
		// tempMap.writeEnd();

	}

	@SuppressWarnings("unchecked")
	private void validateAndProduceResult(String attributeName, JSONObject attributeValue,
			Map<String, Object> inputBERecord, JsonGenerator tempMap, JSONObject propertiesObject, String typeOfElement,
			boolean isConstruction, List<String> failedResult) {

		Object flowfiledata = NifiUtils.getDatafromFlowFileContent(inputBERecord, attributeName);
		String flowFileAttrValue = "";
		if (flowfiledata != null) {
			if (flowfiledata instanceof String || flowfiledata instanceof Number || flowfiledata instanceof Boolean) {
				if ((typeOfElement.equalsIgnoreCase(Constants.ARRAY_TYPE)
						|| typeOfElement.equalsIgnoreCase(Constants.OBJECT_TYPE))) {
					String error = "Type mismatch occured. Expected type in input BE defintion is " + typeOfElement
							+ " but the received type is " + flowfiledata.getClass().getSimpleName()
							+ " Retry with correct data for " + attributeName;
					failedResult.add(error);
				}
				boolean isMandatory = true;
				if (flowfiledata != null) {
					flowFileAttrValue = flowfiledata.toString();
				}

				Iterator<String> propertyObjKeyset = attributeValue.keys();
				while (propertyObjKeyset.hasNext()) {
					String fieldName = propertyObjKeyset.next();
					if (fieldName.equals(Constants.REQUIRED)) {
						isMandatory = (boolean) attributeValue.get(fieldName);
					}
				}
				
				//FIX FOR XPB-135 STARTS HERE
				boolean isDateTime = false;
				if(attributeValue.has(Constants.TYPE) && Constants.TYPE_VALUE_DATETIME.equals(attributeValue.get(Constants.TYPE))){
					isDateTime = true;
				}
				if(isDateTime && !StringUtils.isBlank(flowFileAttrValue) && attributeValue.has(Constants.ATTRIBUTE_DATE_FORMAT)){
					String desDataFormat = (String) attributeValue.get(Constants.ATTRIBUTE_DATE_FORMAT);
					flowFileAttrValue = CommonUtils.convertDate(flowFileAttrValue, Constants.DATE_FORMAT, desDataFormat);
				}
				//FIX FOR XPB-135 ENDS HERE
				

				if (isMandatory) {
					if (flowFileAttrValue != null && flowFileAttrValue.trim().length() > 0) {
						if(isConstruction)tempMap.write(attributeName, flowFileAttrValue);
					} else {
						String error = "Failed to fetch value from Flowfile data for attribute " + attributeName
								+ ". Since " + attributeName + " is null or empty "
								+ " in the Input BE record with buk " + attributeName;
						failedResult.add(error);
					}

				} else if (flowFileAttrValue != null && flowFileAttrValue.trim().length() > 0) {
					if(isConstruction)tempMap.write(attributeName, flowFileAttrValue);
				}

			} else if (flowfiledata instanceof List) {
				if (!typeOfElement.equalsIgnoreCase("array")) {
					String error = "Type mismatch occured. Expected type in input BE defintion is " + typeOfElement
							+ " but the received type is " + flowfiledata.getClass().getSimpleName()
							+ " Retry with correct data for " + attributeName;
					failedResult.add(error);
				}
				if(isConstruction)tempMap.writeStartArray(attributeName);
				doConstructionforComplexTypes(inputBERecord, tempMap, flowfiledata, propertiesObject, propertiesObject,
						"array",isConstruction, failedResult);
				if(isConstruction)tempMap.writeEnd();

			} else {
				if (!typeOfElement.equalsIgnoreCase("object")) {
					String error = "Type mismatch occured. Expected type in input BE defintion is " + typeOfElement
							+ " but the received type is " + flowfiledata.getClass().getSimpleName()
							+ " Retry with correct data for " + attributeName;
					failedResult.add(error);
				}
				if(isConstruction)tempMap.writeStartObject(attributeName);
				doConstructionforComplexTypes(inputBERecord, tempMap, flowfiledata, propertiesObject, propertiesObject,
						"object",isConstruction, failedResult);
				// for every object add its parent relationship for every
				// children
				if(isConstruction)tempMap.writeEnd();
			}
		}

	}

	private String getNameForCBE(JSONObject attributeValue) {
		Iterator<String> propertyObjKeyset = attributeValue.keys();
		while (propertyObjKeyset.hasNext()) {

			String fieldName = propertyObjKeyset.next();
			if (fieldName.equals(Constants.ITEMS)) {
				JSONObject itemsObject = (JSONObject) attributeValue.get(fieldName);
				return (String) itemsObject.get(Constants.ALIASNAME);
			}

		}

		return "";
	}

	private String getNameForCBEforObject(JSONObject attributeValue) {
		Iterator<String> propertyObjKeyset = attributeValue.keys();
		while (propertyObjKeyset.hasNext()) {

			String fieldName = propertyObjKeyset.next();
			if (fieldName.equals(Constants.ALIASNAME)) {
				return (String) attributeValue.get(fieldName);
			}
		}
		return "";
	}

	private JSONObject getPropertiesForCBE(JSONObject attributeValue) {
		Iterator<String> propertyObjKeyset = attributeValue.keys();
		while (propertyObjKeyset.hasNext()) {

			String fieldName = propertyObjKeyset.next();
			if (fieldName.equals(Constants.ITEMS)) {
				JSONObject itemsObject = (JSONObject) attributeValue.get(fieldName);
				return (JSONObject) itemsObject.get(Constants.PROPERTIES);

			}

		}

		return null;
	}

	private JSONObject getPropertiesForCBEForObject(JSONObject attributeValue) {
		Iterator<String> propertyObjKeyset = attributeValue.keys();
		while (propertyObjKeyset.hasNext()) {

			String fieldName = propertyObjKeyset.next();
			if (fieldName.equals(Constants.PROPERTIES)) {
				return (JSONObject) attributeValue.get(fieldName);

			}

		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public void doConstructionforComplexTypes(Map<String, Object> inputBERecord, JsonGenerator tempMap,
			Object flowfiledata, JSONObject propertiesObject, JSONObject attributeValue, String jsonType, boolean isConstruction, List<String> failedResult) {
		JSONArray jsonArray = new JSONArray();
		boolean isArrayOfObjects = false;

		if (flowfiledata instanceof List) {
			isArrayOfObjects = true;
			jsonArray = new JSONArray(gson.toJson(flowfiledata));
		} else {
			// consider only object type reaches this else
			JSONObject jsonObject = new JSONObject(gson.toJson(flowfiledata));
			jsonArray.put(jsonObject);
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			inputBERecord = gson.fromJson(jsonArray.get(i).toString(), HashMap.class);
			if (isArrayOfObjects&&isConstruction)
				tempMap.writeStartObject();
			if (isConstruction) {
				tempMap.writeStartObject("parentRelation");
				tempMap.writeEnd();
			}
			JSONObject objectInArray = jsonArray.getJSONObject(i);

			Iterator<String> propertykeySet = propertiesObject.keys();
			while (propertykeySet.hasNext()) {

				boolean isMandatory = true;
				String type = "";
				String attrName = propertykeySet.next();

				JSONObject attrValue = (JSONObject) propertiesObject.get(attrName);
				Iterator<String> propertyObjKeyset = attrValue.keys();
				while (propertyObjKeyset.hasNext()) {
					String fieldName = propertyObjKeyset.next();
					if (fieldName.equals(Constants.TYPE)) {
						type = (String) attrValue.get(fieldName);
					}
					if (fieldName.equals(Constants.REQUIRED)) {
						isMandatory = (boolean) attrValue.get(fieldName);
					}
				}

				if (type.equalsIgnoreCase(Constants.ARRAY_TYPE)) {
					String attributeName2 = getNameForCBE(attrValue);
					JSONObject cbePropertiesObject = getPropertiesForCBE(attrValue);
					// array - so start array
					// tempMap.writeStartArray(attributeName2);
					validateAndProduceResult(attributeName2, attributeValue, inputBERecord, tempMap,
							cbePropertiesObject, Constants.ARRAY_TYPE, isConstruction, failedResult);
					// tempMap.writeEnd();
				} else if (type.equals(Constants.OBJECT_TYPE)) {
					String attributeName2 = getNameForCBEforObject(attrValue);
					JSONObject cbePropertiesObject = getPropertiesForCBEForObject(attrValue);
					// Object - so start object
					// writeStartObject(attributeName2);
					validateAndProduceResult(attributeName2, attributeValue, inputBERecord, tempMap,
							cbePropertiesObject, Constants.OBJECT_TYPE, isConstruction, failedResult);
					// tempMap.writeEnd();
				} else {
					try {
						if (!objectInArray.has(attrName) && isMandatory) {
							String error = "Failed to fetch value from Flowfile data for attribute " + attrName
									+ ". Since " + attrName + " is null or empty " + " in the Input BE record with buk "
									+ attrName;
							failedResult.add(error);
						} else if (objectInArray.has(attrName)) {
							Map<String, Object> finalJsonMap = toMap(objectInArray);
							
							String finalAttributeValue = finalJsonMap.get(attrName).toString();
							
							//FIX FOR XPB-135 STARTS HERE
							boolean isDateTime = false;
							if(Constants.TYPE_VALUE_DATETIME.equals(type)){
								isDateTime = true;
							}
							if(isDateTime && !StringUtils.isBlank(finalAttributeValue) && attrValue.has(Constants.ATTRIBUTE_DATE_FORMAT)){
								String desDataFormat = (String) attrValue.get(Constants.ATTRIBUTE_DATE_FORMAT);
								finalAttributeValue = CommonUtils.convertDate(finalAttributeValue, Constants.DATE_FORMAT, desDataFormat);
							}
							//FIX FOR XPB-135 ENDS HERE
							
							if (isMandatory && (finalJsonMap.get(attrName).toString().trim().length() > 0)) {
								if(isConstruction)tempMap.write(attrName, finalAttributeValue);
							} else if ((finalJsonMap.get(attrName).toString().trim().length() > 0)) {
								if(isConstruction)tempMap.write(attrName, finalAttributeValue);
							}
						}
					} catch (Exception ex) {
						failedResult.add("unable to add data" + ex.getMessage());
					}
				}
			}
			// tempMap.writeStartObject("parentRelation");
			// tempMap.writeEnd();
			if (isArrayOfObjects && isConstruction)

				tempMap.writeEnd();
		}
	}

	private boolean checkPropertiesforBEType(JSONObject propertiesObject) {
		String typeAttributeValue = "";
		Iterator<String> keyset = propertiesObject.keys();
		while (keyset.hasNext()) {
			String attributeName = keyset.next();
			JSONObject attributeValue = (JSONObject) propertiesObject.get(attributeName);
			Iterator<String> propertyObjKeyset = attributeValue.keys();
			while (propertyObjKeyset.hasNext()) {
				String fieldName = propertyObjKeyset.next();
				if (fieldName.equals(Constants.TYPE)) {
					typeAttributeValue = (String) attributeValue.get(fieldName);
					if (typeAttributeValue.equalsIgnoreCase(Constants.ARRAY_TYPE)
							|| typeAttributeValue.equalsIgnoreCase(Constants.OBJECT_TYPE)) {
						return true;
					}
					break;
				}
			}
		}
		return false;
	}

	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}

}
