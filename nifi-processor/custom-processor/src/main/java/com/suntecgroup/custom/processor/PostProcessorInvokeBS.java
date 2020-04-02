/*
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.buk.Buk;
import com.suntecgroup.custom.processor.model.buk.EventBuk;
import com.suntecgroup.custom.processor.model.mappingparameter.Mapping;
import com.suntecgroup.custom.processor.model.mappingparameter.OutputBeMapping;
import com.suntecgroup.custom.processor.model.mappingparameter.OutputDefinitionMapping;
import com.suntecgroup.custom.processor.model.mappingparameter.OutputParamMapping;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;
import com.suntecgroup.custom.processor.utils.CommonUtils;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.NifiUtils;

/**
 *
 * PostProcessorInvokeBS is a customized processor class for the Nifi
 * PostProcessor. PostProcessor is the part of InvokeBusinessService(InvokeBS)
 * operator and will execute at the end of the InvokeBS operator.
 *
 * @version 1.0 10 Sep 2018
 * @author Neeraj Sharma
 */
@Tags({ "postprocessor,invokeBS" })
@CapabilityDescription("PostProcessor to InvokeBS")
public class PostProcessorInvokeBS extends AbstractProcessor {

	private ObjectMapper mapper = null;
	private Gson gson = null;
	private Type mapTypeToken = null;
	private Type mapTypeTokenObj = null;

	private ComponentLog logger;

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description("Success relationship").build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();

	public static final PropertyDescriptor OUTPUT_MAPPING_PARAMETER = new PropertyDescriptor.Builder()
			.name("Output Mapping Parameter").description("Post Processor OUTPUT Mapping Params").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_BUSINESS_ENTITY = new PropertyDescriptor.Builder()
			.name("Output Business Entity").description("Post Processor OUTPUT_BUSINESS_ENTITY").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUTBE_DEFINITION = new PropertyDescriptor.Builder()
			.name("OutputBE Definition").description("Post Processor Output Definition").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OPERATOR_NAME = new PropertyDescriptor.Builder().name("Operator Name")
			.description("Post Processor Operator Name").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_BE_BUK_ATTRIBUTES = new PropertyDescriptor.Builder()
			.name("Output BE BUK Attributes").description("Output BE BUK attributes array").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor HTTP_METHOD = new PropertyDescriptor.Builder().name("HTTP Method")
			.description("Post Processor Http Method").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor MERGE_SOURCE = new PropertyDescriptor.Builder().name("Merge Source")
			.description("flag for defining the processor as source processor of merge")
			.allowableValues("true", "false").required(true).defaultValue("false")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor PATH_NAME = new PropertyDescriptor.Builder().name("Path_Name")
			.description("path name for the merge processor").addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.required(false).build();

	private List<PropertyDescriptor> properties;

	private Set<Relationship> relationships;

	@Override
	protected void init(final ProcessorInitializationContext context) {
		final List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
		properties.add(OUTPUT_MAPPING_PARAMETER);
		properties.add(OUTPUT_BUSINESS_ENTITY);
		properties.add(OUTPUTBE_DEFINITION);
		properties.add(OUTPUT_BE_BUK_ATTRIBUTES);
		properties.add(OPERATOR_NAME);
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		properties.add(HTTP_METHOD);
		properties.add(MERGE_SOURCE);
		properties.add(PATH_NAME);

		this.properties = Collections.unmodifiableList(properties);

		final Set<Relationship> relationships = new HashSet<Relationship>();
		this.relationships = Collections.unmodifiableSet(relationships);
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		logger = context.getLogger();
		mapper = new ObjectMapper();
		gson = new GsonBuilder().create();
		mapTypeToken = new TypeToken<Map<String, String>>() {}.getType();
		mapTypeTokenObj = new TypeToken<Map<String, Object>>() {}.getType();
	}

	@Override
	public Set<Relationship> getRelationships() {
		return this.relationships;
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}

	private boolean isMergeSource = false;
	private String outputBeProperty = null;
	private String operatorName = null;
	private String outputBEDefinition = null;
	private OutputParamMapping outputParamMapping = null;
	private OutputBeMapping outputBeParamMapping = null;
	private String pathName = null;
	private JSONArray arrayBukAttributes = null;
	private JsonProvider jsonProvider = null;
	
	@OnScheduled
	public void onScheduled(final ProcessContext context) throws NifiCustomException {
		isMergeSource = context.getProperty(MERGE_SOURCE).evaluateAttributeExpressions().asBoolean();
		if (isMergeSource) {
           pathName = context.getProperty(PATH_NAME).evaluateAttributeExpressions().getValue();
        }
		final String outputMappingProperty = context.getProperty(OUTPUT_MAPPING_PARAMETER).evaluateAttributeExpressions().getValue();
		outputBeProperty = context.getProperty(OUTPUT_BUSINESS_ENTITY).evaluateAttributeExpressions().getValue();
		outputBEDefinition = context.getProperty(OUTPUTBE_DEFINITION).evaluateAttributeExpressions().getValue();
		operatorName = context.getProperty(OPERATOR_NAME).evaluateAttributeExpressions().getValue();
		String bukAttributes = context.getProperty(OUTPUT_BE_BUK_ATTRIBUTES).evaluateAttributeExpressions().getValue();
		arrayBukAttributes = new JSONArray(bukAttributes);
		try {
			if (!operatorName.startsWith(Constants.INVOKE_EXTERNAL)) {
				outputParamMapping = mapper.readValue(outputMappingProperty, OutputParamMapping.class);
			} else {
				outputBeParamMapping = mapper.readValue(outputMappingProperty, OutputBeMapping.class);
			}
		} catch (Exception e) {
			logger.error("Error occurred while reading Postprocessor OutputMapping property: " + e.getMessage(), e);
			throw new NifiCustomException("Exception while parsing output mapping");
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
	@SuppressWarnings("unused")
	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
		FlowFile flowFile = session.get();

		if (null == flowFile) {
			return;
		}

		boolean isMarker = Boolean.parseBoolean(flowFile.getAttribute(Constants.IS_MARKER));

		if (isMergeSource && isMarker) {
			session.removeAttribute(flowFile, Constants.INPUT_BUK);
			session.removeAttribute(flowFile, Constants.OUTPUT_BUK);
			session.removeAttribute(flowFile, Constants.INPUT_OUTPUT_MAPPING);
			session.transfer(flowFile, REL_SUCCESS);
			return;
		}

		OutputDefinitionMapping outputDefinitionObject = null;
		List<Mapping> listOpmParam = null;

		List<EventBuk> outputBEBUKList = new ArrayList<EventBuk>();
		EventBuk invalidEventBuk = new EventBuk();
		String inputBEName = flowFile.getAttribute(Constants.BENAME);

		if (!StringUtils.isBlank(outputBeProperty)) {
			session.putAttribute(flowFile, Constants.BENAME, outputBeProperty);
			inputBEName = outputBeProperty;
		}
		JSONObject opBEDefinitionJSON = null;
		try {
			CommonUtils.validateSessionId(context, session, flowFile, SESSION_ID, logger);
			CommonUtils.validateRunNumber(context, session, flowFile, RUN_NUMBER, logger);
		} catch (Exception ex) {
			logger.error("error occured while reading processing session details: " + ex.getMessage(), ex);
			route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR, ex.getMessage(),
					isMergeSource);
			return;
		}

		try {
			// outputDefinitionObject = mapper.readValue(outputBEDefinition,
			// OutputDefinitionMapping.class);
			opBEDefinitionJSON = new JSONObject(outputBEDefinition);
		} catch (Exception e) {
			logger.error("Error occurred while reading Postprocessor OutputBe property: " + e.getMessage(), e);
			route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR, e.getMessage(),
					isMergeSource);
			return;
		}
		
		Map<String, Object> contextMap = new HashMap<String, Object>();
		final List<String> failedResult = new ArrayList<>();
		// For Invoke external Process
		if (!StringUtils.isBlank(outputBeProperty) && operatorName.startsWith(Constants.INVOKE_EXTERNAL)) {
			String mappingValue = "";
			int eventsCount = 0;
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			JsonWriter writerExternal = null;
			OutputStream postProcessorOutput = null;
			FlowFile flowFileOutput = null;
			List<Map<String, Object>> outputMapList = new ArrayList<>();
			try {
				session.exportTo(flowFile, bytes);
				final String contents = bytes.toString();
				JSONObject flowFileJSON = new JSONObject();
				JSONArray inputObjJSONArray = new JSONArray();
				if (!StringUtils.isEmpty(contents)) {
					if (StringUtils.startsWith(contents, "[")) {
						inputObjJSONArray = new JSONArray(contents);

						/*
						 * if (inputObjJSONArray.length() > 0) { flowFileJSON =
						 * (JSONObject) inputObjJSONArray.get(0); }
						 */

					} else {
						flowFileJSON = new JSONObject(contents);
						inputObjJSONArray.put(0, flowFileJSON);
					}
				}
				flowFileOutput = session.clone(flowFile);
				postProcessorOutput = session.write(flowFileOutput);
				writerExternal = new JsonWriter(new OutputStreamWriter(postProcessorOutput, Constants.UTF_ENCODING));
				for (int i = 0; i <= inputObjJSONArray.length(); i++) {
					Map<String, Object> outputMap = new HashMap<>();
					if (i != 0 && inputObjJSONArray.length() == i) {
						break;
					}
					if (inputObjJSONArray.length() > 0) {
						flowFileJSON = inputObjJSONArray.getJSONObject(i);
						updateProcessVariables(flowFileJSON, outputBeParamMapping, session, flowFile, failedResult);
					}
					JSONArray outputMappingArray = new JSONArray(outputBeParamMapping.getOutputMapping());

					List<Map<String, Object>> processorOuputListExternal = new LinkedList<Map<String, Object>>();
					JSONArray responsePayLoadArray = new JSONArray();
					Object finalValue = null;
					JSONObject finalJson = new JSONObject();
					if (null != outputMappingArray && outputMappingArray.length() > 0) {
						JSONObject outputJSONObj = outputMappingArray.getJSONObject(0);
						JSONObject finalJson1 = constructResponsePayload(flowFileJSON, outputJSONObj, finalJson,
								session, outputMappingArray, flowFile, failedResult);
						Map<String, Object> tempMap = toMap(finalJson1);
						processorOuputListExternal.add(tempMap);
						if (processorOuputListExternal != null && processorOuputListExternal.size() > 0) {
							for (Map<String, Object> map : processorOuputListExternal) {
								outputMap.putAll(map);
								eventsCount++;
							}
						}
					}
					outputMapList.add(outputMap);
				}
				EventBuk eventBuk = new EventBuk();
				writerExternal.beginArray();
				for (int i = 0; i < outputMapList.size(); i++) {
					Map<String, Object> outputMapForEvent = outputMapList.get(i);
					if (outputMapForEvent.size() > 0) {
						for (int index = 0; index < arrayBukAttributes.length(); index++) {
							String key = arrayBukAttributes.getString(index);
							Object value = outputMapForEvent.get(key);
							if (!outputMapForEvent.containsKey(key) || null == value
									|| StringUtils.isEmpty(value.toString())) {
								invalidEventBuk.addBuk(new Buk(key, ""));
							} else {
								eventBuk.addBuk(new Buk(key, value.toString()));
							}
						}
					}
					gson.toJson(outputMapForEvent, mapTypeToken, writerExternal);
				}
				writerExternal.endArray();
			} catch (Exception ex) {
				logger.error("Exception occured at postprocessor: " + ex.getMessage(), ex);
				closeStream(null, postProcessorOutput, null, writerExternal);
				session.remove(flowFile);
				route(flowFileOutput, REL_FAILURE, context, session, null, Constants.TECHNICALERROR, null,
						isMergeSource);
				return;
			} finally {
				closeStream(null, postProcessorOutput, null, writerExternal);
			}

			try {
				if (null != failedResult && failedResult.size() > 0) {
					throw new NifiCustomException(
							operatorName + " failed due to missing mapping value for mandatory attributes: "
									+ failedResult.toString());
				} else {
					session.putAttribute(flowFileOutput, Constants.OUTPUT_BUK,
							NifiUtils.convertObjectToJsonString(outputBEBUKList, logger));
					session.putAttribute(flowFileOutput, Constants.INPUT_OUTPUT_MAPPING, mappingValue);
					session.getProvenanceReporter().modifyAttributes(flowFileOutput);
					session.remove(flowFile);
					session.removeAttribute(flowFileOutput, Constants.INPUT_BUK);
					session.removeAttribute(flowFileOutput, Constants.OUTPUT_BUK);
					session.removeAttribute(flowFileOutput, Constants.INPUT_OUTPUT_MAPPING);
					route(flowFileOutput, REL_SUCCESS, context, session, null, null, null, isMergeSource);
				}

			} catch (NifiCustomException nifiCustomException) {
				logger.error("Error occurred at Postprocessor :: " + nifiCustomException.getMessage(),
						nifiCustomException);
				session.remove(flowFile);
				route(flowFileOutput, REL_FAILURE, context, session, null, Constants.TECHNICALERROR, null,
						isMergeSource);

			} catch (Exception ex) {
				logger.error("Error occurred at Postprocessor :: " + ex.getMessage(), ex);
				session.remove(flowFile);
				route(flowFileOutput, REL_FAILURE, context, session, null, Constants.TECHNICALERROR, null,
						isMergeSource);
			}

		} else {

			FlowFile flowFileOutput = session.clone(flowFile);
			InputStream postProcessorInput = session.read(flowFile);
			OutputStream postProcessorOutput = session.write(flowFileOutput);
			JsonReader reader = null;
			JsonWriter writer = null;

			int eventsCount = 0;
			String mappingValue = "";
			if (postProcessorInput != null) {
				try {

					List<Map<String, Object>> processorOuputList = new LinkedList<Map<String, Object>>();
					boolean isBreak = false;

					reader = new JsonReader(new InputStreamReader(postProcessorInput, Constants.UTF_ENCODING));
					writer = new JsonWriter(new OutputStreamWriter(postProcessorOutput, Constants.UTF_ENCODING));

					reader.beginObject();
					writer.beginArray();

					while (reader.hasNext()) {

						JsonToken nextElement = reader.peek();

						if (nextElement.equals(JsonToken.NAME)) {
							String name = reader.nextName();
							if (name != null && Constants.context.equals(name.trim().toLowerCase())) {
								Map<String, Object> jsonRecord = gson.fromJson(reader, mapTypeTokenObj);
								Iterator<Entry<String, Object>> entry = jsonRecord.entrySet().iterator();
								while (entry.hasNext()) {
									Entry<String, Object> en = (Entry<String, Object>) entry.next();
									contextMap.put(en.getKey(), en.getValue());
								}
							} else if (!StringUtils.isBlank(outputBeProperty)
									&& name.toLowerCase().equals(outputBeProperty.toLowerCase())) {
								reader.beginArray();
								while (reader.hasNext()) {
									eventsCount++;
									// Read data into object model
									Map<String, Object> jsonRecord = gson.fromJson(reader, mapTypeTokenObj);

									// postprocessor logic starts.
									if (!StringUtils.isBlank(outputBeProperty)) {
										processorOuputList = processRecord(session, jsonRecord, opBEDefinitionJSON, failedResult);
									}
									// postprocessor logic ends.

									if (failedResult != null && failedResult.size() > 0) {
										isBreak = true;
										break;
									}

									if (processorOuputList != null && processorOuputList.size() > 0) {
										Map<String, Object> resultMap = processorOuputList.get(0);
										gson.toJson(resultMap, mapTypeToken, writer);

										// construct BUK list @JOHN starts here
										EventBuk eventBuk = new EventBuk();
										for (int index = 0; index < arrayBukAttributes.length(); index++) {
											String key = arrayBukAttributes.getString(index);
											Object value = resultMap.get(key);
											if (!resultMap.containsKey(key) || null == value
													|| StringUtils.isEmpty(value.toString())) {
												// BUK attribute is not
												// available/value is empty
												invalidEventBuk.addBuk(new Buk(key, ""));
											} else {
												eventBuk.addBuk(new Buk(key, value.toString()));
											}
										}
										outputBEBUKList.add(eventBuk);
										// construct BUK list @JOHN ends here
									}
								}
								reader.endArray();

							} else if (!StringUtils.isBlank(outputBeProperty)
									&& operatorName.startsWith(Constants.INVOKE_EXTERNAL)) {
								final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
								session.exportTo(flowFile, bytes);
								final String contents = bytes.toString();
								writer.value(reader.toString());
							} else {
								reader.skipValue();
							}
						}

						jsonManipulation(reader, nextElement);
					}

					if (invalidEventBuk.getBuk() != null && invalidEventBuk.getBuk().size() > 0) {
						throw new NifiCustomException("Failed due to Invalid BUK!");
					}

					if (!isBreak) {
						reader.endObject();
						writer.endArray();
					}

				} catch (NifiCustomException nifiException) {
					logger.error("Exception Occurred:: " + nifiException.getMessage(), nifiException);
					closeStream(postProcessorInput, postProcessorOutput, reader, writer);
					session.remove(flowFileOutput);
					route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
							nifiException.getMessage(), isMergeSource);
					return;
				} catch (Exception ex) {
					logger.error("Exception occured at postprocessor: " + ex.getMessage(), ex);
					closeStream(postProcessorInput, postProcessorOutput, reader, writer);
					session.remove(flowFileOutput);
					route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
							ex.getMessage(), isMergeSource);
					return;
				}

				finally {
					closeStream(postProcessorInput, postProcessorOutput, reader, writer);
				}
			}

			// Updated attributes and setting it in output flowfile.
			if (null != contextMap.get(Constants.IO_CORRELATION)) {
				mappingValue = contextMap.get(Constants.IO_CORRELATION).toString();
			}
			Map<String, Object> contextParametersMap = (Map<String, Object>) contextMap
					.get(Constants.contextParameters);
			JSONObject contextParamJson = new JSONObject(contextParametersMap);

			if (outputParamMapping != null) {
				listOpmParam = outputParamMapping.getOutputMapping();

				if (listOpmParam != null && listOpmParam.size() > 0) {
					for (Mapping mappingParam : listOpmParam) {
						setFlowFileAttribute(mappingParam, contextParamJson, session, flowFileOutput);
					}
				}
			}

			try {
				if (failedResult != null && failedResult.size() > 0) {
					throw new NifiCustomException(
							operatorName + " failed due to missing mapping value for mandatory attributes: "
									+ failedResult.toString());
				} else if (!StringUtils.isBlank(outputBeProperty) && eventsCount < 1) {
					throw new NifiCustomException(
							operatorName + " failed to generate the output due to InputBE is empty or null");
				}

				else {
					session.putAttribute(flowFileOutput, Constants.OUTPUT_BUK,
							NifiUtils.convertObjectToJsonString(outputBEBUKList, logger));
					session.putAttribute(flowFileOutput, Constants.INPUT_OUTPUT_MAPPING, mappingValue);

					session.getProvenanceReporter().modifyAttributes(flowFileOutput);

					session.remove(flowFile);
					session.removeAttribute(flowFileOutput, Constants.INPUT_BUK);
					session.removeAttribute(flowFileOutput, Constants.OUTPUT_BUK);
					session.removeAttribute(flowFileOutput, Constants.INPUT_OUTPUT_MAPPING);
					route(flowFileOutput, REL_SUCCESS, context, session, null, null, null, isMergeSource);
				}
			} catch (NifiCustomException nifiCustomException) {
				logger.error("Error occurred at Postprocessor :: " + nifiCustomException.getMessage(),
						nifiCustomException);
				session.remove(flowFileOutput);
				route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR,
						nifiCustomException.getMessage(), isMergeSource);

			} catch (Exception ex) {
				logger.error("Error occurred at Postprocessor :: " + ex.getMessage(), ex);
				session.remove(flowFileOutput);
				route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR, ex.getMessage(),
						isMergeSource);
			}
		}
	}

	public void setProcessVariable(ProcessVariable processVariable, Object value) {
		switch (processVariable.getType().getTypeName().toLowerCase()) {
		case Constants.dataTypeNumber:
			BigDecimal intValue = StringUtils.isEmpty(value.toString()) ? new BigDecimal(0)
					: new BigDecimal(value.toString());
			processVariable.getValue().setIntValue(intValue);
			break;
		case Constants.dataTypeString:
			String stringValue = StringUtils.isEmpty(value.toString()) ? "" : value.toString();
			processVariable.getValue().setStringValue(stringValue);
			break;
		case Constants.dataTypeBoolean:
			Boolean booleanValue = StringUtils.isEmpty(value.toString()) ? true
					: Boolean.parseBoolean(value.toString());
			processVariable.getValue().setBooleanValue(booleanValue);
			break;
		case Constants.dataTypeDate:
			String dateValue = StringUtils.isEmpty(value.toString()) ? "" : value.toString();
			processVariable.getValue().setDateValue(dateValue);
			break;
		default:
			break;
		}
	}


	public void closeStream(InputStream postprocessorInput, OutputStream postprocessorOutput, JsonReader reader,
			JsonWriter writer) {
		try {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (postprocessorInput != null) {
				postprocessorInput.close();
			}

			if (postprocessorOutput != null) {
				postprocessorOutput.close();
			}
		} catch (IOException e) {
			logger.debug("Exception occured while closing the stream " + e.getMessage(), e);
		}
	}

	/**
	 * This method will process the incoming record.
	 * 
	 * @param processSession
	 *            - object of ProcessSession
	 * @param inputRecord
	 *            - contains the input be record
	 * @param outputDefinitionObject
	 *            - contains ouputbe definition
	 * @return List of updated record
	 */
	private List<Map<String, Object>> processRecord(ProcessSession processSession, Map<String, Object> inputRecord,
			JSONObject outputDefinitionObject, List<String> failedResult) {
		List<Map<String, Object>> successLst = new LinkedList<Map<String, Object>>();
		if (inputRecord != null && inputRecord.size() > 0) {
			// inputRecord = validateInputRecord(inputRecord,
			// outputDefinitionObject);
			// /successLst = constructRecords(successLst,
			// outputDefinitionObject, inputRecord);
			successLst = constructRecordsforEffectiveBE(successLst, outputDefinitionObject, inputRecord, failedResult);
		}
		return successLst;
	}

	/**
	 * This method is used for reading json.
	 * 
	 * @param reader
	 *            - object of json reader
	 * @param nextElement
	 *            - contains next element while reading the json
	 * @throws IOException
	 *             - IOException is thrown
	 */
	private void jsonManipulation(JsonReader reader, JsonToken nextElement) throws IOException {

		switch (nextElement) {
		case BEGIN_OBJECT: {
			reader.beginObject();
			break;
		}
		case END_OBJECT: {
			reader.endObject();
			break;
		}
		case BEGIN_ARRAY: {
			reader.beginArray();
			break;
		}
		case END_ARRAY: {
			reader.endArray();
			break;
		}
		case STRING: {
			reader.skipValue();
			break;
		}
		case NUMBER: {
			reader.skipValue();
			break;
		}
		case BOOLEAN: {
			reader.skipValue();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * setFlowFileAttribute - set flow file attribute using output param mapping
	 * 
	 * @param mappingParam
	 * @param contextParamJson
	 * @param session
	 * @param flowFile
	 */
	private void setFlowFileAttribute(Mapping mappingParam, JSONObject contextParamJson, ProcessSession session,
			FlowFile flowFileOutput) {

		// Start : Get the process variable from the flowfile attribute
		ProcessVariable processVariable = null;
		String processVariableStr = flowFileOutput.getAttribute(mappingParam.getProcessVariable());
		if (!StringUtils.isBlank(processVariableStr)) {
			try {
				processVariable = mapper.readValue(processVariableStr, ProcessVariable.class);
			} catch (IOException e) {
				logger.error("IOException occured while reading process variable: " + e.getMessage(), e);
			}
		} else {
			return;
		}
		// End : Get the process variable from the flowfile attribute

		if (mappingParam.getSelectedKey().equalsIgnoreCase(Constants.contextVariable)) {
			for (Object cvName : contextParamJson.keySet()) {
				if (cvName.toString().equalsIgnoreCase(mappingParam.getContextVariable())) {
					Object value = contextParamJson.get((String) cvName);

					if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_PRIMITIVE,
							processVariable.getType().getTypeCategory())) {
						switch (processVariable.getType().getTypeName().toLowerCase()) {
						case Constants.dataTypeNumber:
							BigDecimal intValue = StringUtils.isEmpty(value.toString()) ? new BigDecimal(0)
									: new BigDecimal(value.toString());
							processVariable.getValue().setIntValue(intValue);
							break;
						case Constants.dataTypeString:
							String stringValue = StringUtils.isEmpty(value.toString()) ? "" : value.toString();
							processVariable.getValue().setStringValue(stringValue);
							break;
						case Constants.dataTypeBoolean:
							Boolean booleanValue = StringUtils.isEmpty(value.toString()) ? true
									: Boolean.parseBoolean(value.toString());
							processVariable.getValue().setBooleanValue(booleanValue);
							break;
						case Constants.dataTypeDate:
							String dateValue = StringUtils.isEmpty(value.toString()) ? "" : value.toString();
							processVariable.getValue().setDateValue(dateValue);
							break;
						default:
							break;
						}
					} else if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_BE,
							processVariable.getType().getTypeCategory())) {
						processVariable.getValue().setBeValue(value.toString());
					}

					break;
				}
			}
		} else {
			switch (processVariable.getType().getTypeName().toLowerCase()) {
			case Constants.dataTypeNumber:
				processVariable.getValue().setPrecision(mappingParam.getValue().getPrecision());
				processVariable.getValue().setScale(mappingParam.getValue().getScale());
				processVariable.getValue().setIntValue(mappingParam.getValue().getIntValue());
				break;
			case Constants.dataTypeString:
				processVariable.getValue().setStringValue(mappingParam.getValue().getStringValue());
				break;
			case Constants.dataTypeBoolean:
				processVariable.getValue().setBooleanValue(mappingParam.getValue().getBooleanValue());
				break;
			case Constants.dataTypeDate:
				processVariable.getValue().setDateValue(mappingParam.getValue().getDateValue());
				break;
			default:
				break;
			}
		}

		session.putAttribute(flowFileOutput, mappingParam.getProcessVariable(), processVariable.toJsonString());
	}

	private void route(FlowFile flowfile, Relationship relationship, final ProcessContext context,
			final ProcessSession session, String beName, String errorType, String errorMessage, boolean isMergeSource) {

		if (StringUtils.equalsIgnoreCase(REL_FAILURE.getName(), relationship.getName())) {
			if (isMergeSource) {
				session.putAttribute(flowfile, Constants.ROUTE, pathName);
				FlowFile markerFlowFile = NifiUtils.cloneFlowfileWithoutContent(flowfile, session, logger);
				session.removeAttribute(markerFlowFile, Constants.INPUT_BUK);
				session.removeAttribute(markerFlowFile, Constants.OUTPUT_BUK);
				session.removeAttribute(markerFlowFile, Constants.INPUT_OUTPUT_MAPPING);
				session.transfer(markerFlowFile, REL_SUCCESS);
			}
			// Update failure details in flowfile attributes
			flowfile = NifiUtils.updateFailureDetails(context, session, flowfile, beName, errorType, errorMessage);
		}
		session.transfer(flowfile, relationship);
		session.commit();
	}

	// Updated attributes and setting it in output flow file.
	private void updateProcessVariables(JSONObject flowFileJSON, OutputBeMapping outputBeParamMapping,
			ProcessSession session, FlowFile flowFileOutput, List<String> failedResult) {
		if (!StringUtils.isEmpty(outputBeParamMapping.getPvMapping())) {
			JSONArray pvMapObj = new JSONArray(outputBeParamMapping.getPvMapping());
			JSONObject pvobj = pvMapObj.getJSONObject(0);
			updateProcessVariableWithBE(flowFileJSON, pvobj, session, pvMapObj, flowFileOutput, failedResult);

		}
	}

	private void updateProcessVariableWithBE(JSONObject flowFileJSON, JSONObject pvobj, ProcessSession session,
			JSONArray pvMapObj, FlowFile flowFileOutput, List<String> failedResult) {
		String pvObjkey = null;
		String objDataType = null;
		try {
			if (null != pvobj && pvobj.length() > 0) {
				Iterator<?> pvObjIterator = pvobj.keys();
				while (pvObjIterator.hasNext()) {
					Object pvValue = null;
					pvObjkey = (String) pvObjIterator.next();
					Object pvJsonval = pvobj.get(pvObjkey);
					String[] keys = null;
					if (pvJsonval instanceof String) {
						String val = (String) pvobj.get(pvObjkey);
						if (!pvObjkey.equalsIgnoreCase("objName")) {
							if (val.startsWith(Constants.EV)) {
								String[] enterValue = val.split(Constants.EV);
								pvValue = enterValue[1];
							} else {
								if (null != flowFileJSON && flowFileJSON.has(val)) {
									objDataType = flowFileJSON.get(val).getClass().getSimpleName();
									if (Constants.INTEGER.equalsIgnoreCase(objDataType)) {
										pvValue = flowFileJSON.getInt(val);
									} else if (Constants.String.equalsIgnoreCase(objDataType)) {
										pvValue = flowFileJSON.getString(val);
									} else if (Constants.BOOLEAN.equalsIgnoreCase(objDataType)) {
										pvValue = flowFileJSON.getBoolean(val);
									}
									if (null != pvValue) {
										session.putAttribute(flowFileOutput, pvObjkey, pvValue.toString());
									}
								}
							}
						}
					} else {
						JSONArray jsonArrVal = (JSONArray) pvobj.get(pvObjkey);
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
								JSONArray pvMappingJsonArray = (JSONArray) pvMapObj.get(1);
								for (int i = 0; i < pvMappingJsonArray.length(); i++) {
									JSONObject item = pvMappingJsonArray.getJSONObject(i);
									if (val.equalsIgnoreCase(item.get("objName").toString())) {
										childObj = item;
										break;
									}
								}
								childFlowFileJSON = (JSONObject) getChildFlowFileJson(keys, flowFileJSON,
										Constants.JSON_OBJECT, session, flowFileOutput, objDataType);
								constructResponsePayload(childFlowFileJSON, childObj, childJson, session, pvMapObj,
										flowFileOutput, failedResult);

							} else if (objDataType.equalsIgnoreCase(Constants.JSON_ARRAY)) {
								JSONArray childFlowJSONArray = (JSONArray) getChildFlowFileJson(keys, flowFileJSON,
										Constants.JSON_ARRAY, session, flowFileOutput, objDataType);
								JSONArray outputMappingJsonArray = (JSONArray) pvMapObj.get(1);
								for (int i = 0; i < outputMappingJsonArray.length(); i++) {
									JSONObject item = outputMappingJsonArray.getJSONObject(i);
									if (val.equalsIgnoreCase(item.get("objName").toString())) {
										break;
									}
								}
								for (int count = 0; count < childFlowJSONArray.length(); count++) {
									JSONObject childJsonItem = childFlowJSONArray.getJSONObject(count);
									JSONObject finalchildJson = new JSONObject();
									JSONObject finalChildObj = new JSONObject();
									constructResponsePayload(childJsonItem, finalChildObj, finalchildJson, session,
											pvMapObj, flowFileOutput, failedResult);
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.info("Exception occurred while updating process variable::::" + e);
		}

	}

	private List<Map<String, Object>> constructRecordsforEffectiveBE(List<Map<String, Object>> successLst,
			JSONObject inputDefinition, Map<String, Object> inputBERecord, List<String> failedResult) {
		StringWriter writer = new StringWriter();
		try (JsonGenerator tempMap = jsonProvider.createGenerator(writer)){
			if (inputBERecord.size() == 1) {
				inputBERecord = (Map<String, Object>) inputBERecord.get(inputBERecord.keySet().iterator().next());
			}
			JSONObject inputBEdefiniton = inputDefinition.getJSONObject(Constants.OUTPUTBE);
			JSONObject propertiesObject = inputBEdefiniton.getJSONObject(Constants.PROPERTIES);
			List<LinkedHashMap<String, Object>> resultJSON = new ArrayList<LinkedHashMap<String, Object>>();
			validatebyDefinition(propertiesObject, resultJSON, inputBERecord, tempMap, true, failedResult);
			String processedJSON = writer.toString();
			Map<String, Object> finalJsonMap = gson.fromJson(processedJSON, HashMap.class);
			successLst.add(finalJsonMap);
		} catch (Exception ex) {
			failedResult.add("Exception during the JSON Construction from definition " + ex.getMessage());
		}

		return successLst;
	}

	@SuppressWarnings("unchecked")
	private void validatebyDefinition(JSONObject propertiesObject, List<LinkedHashMap<String, Object>> resultJSON,
			Map<String, Object> inputBERecord, JsonGenerator tempMap, boolean isConstruction, List<String> failedResult) {

		Iterator<String> keyset = propertiesObject.keys();
		if (isConstruction)
			tempMap.writeStartObject();
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
				// attributeName = getNameForCBE(attributeValue);
				// value can be array or object type

				if (typeAttributeValue.equals("array")) {
					// tempMap.writeStartArray(attributeName);
					JSONObject cbePropertiesObject = getPropertiesForCBE(attributeValue);
					validateAndProduceResult(attributeName, attributeValue, inputBERecord, tempMap, cbePropertiesObject,
							"array", isConstruction, failedResult);
					// tempMap.writeEnd();
				} else {
					// tempMap.writeStartObject(attributeName);
					JSONObject cbePropertiesObject = getPropertiesForCBEForObject(attributeValue);
					validateAndProduceResult(attributeName, attributeValue, inputBERecord, tempMap, cbePropertiesObject,
							"object", isConstruction, failedResult);
					// tempMap.writeEnd();
				}

			} else {
				// iteratively add the contents
				validateAndProduceResult(attributeName, attributeValue, inputBERecord, tempMap, propertiesObject,
						"string", isConstruction, failedResult);
			}

		}
		if (isConstruction) {
			tempMap.writeEnd();
			tempMap.flush();
		}

	}

	@SuppressWarnings("unchecked")
	private void validateAndProduceResult(String attributeName, JSONObject attributeValue,
			Map<String, Object> inputBERecord, JsonGenerator tempMap, JSONObject propertiesObject, String typeOfElement,
			boolean isConstruction, List<String> failedResult) {

		String attrType = attributeValue.get(Constants.TYPE).toString();
		Object flowfiledata = null;
		if (Constants.TYPE_VALUE_NUMBER.equals(attrType)) {
			if ("0.0".equals(attributeValue.get(Constants.SCALE).toString())) {
				flowfiledata = NifiUtils.getDatafromFlowFileContent(inputBERecord, attributeName, true);
			} else {
				flowfiledata = NifiUtils.getDatafromFlowFileContent(inputBERecord, attributeName, false);
			}
		} else {
			flowfiledata = NifiUtils.getDatafromFlowFileContent(inputBERecord, attributeName);
		}
		
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
					String srcDataFormat = (String) attributeValue.get(Constants.ATTRIBUTE_DATE_FORMAT);
					flowFileAttrValue = CommonUtils.convertDate(flowFileAttrValue, srcDataFormat, Constants.DATE_FORMAT);
				}
				//FIX FOR XPB-135 ENDS HERE
				
				if (isMandatory) {
					if (flowFileAttrValue != null && flowFileAttrValue.trim().length() > 0) {
						if (isConstruction)
							tempMap.write(attributeName, flowFileAttrValue);
					}

					else {
						String error = "Failed to fetch value from Flowfile data for attribute " + attributeName
								+ ". Since " + attributeName + " is null or empty "
								+ " in the Input BE record with buk " + attributeName;
						failedResult.add(error);
					}

				} else if (flowFileAttrValue != null && flowFileAttrValue.trim().length() > 0 && isConstruction) {
					if (isConstruction)
						tempMap.write(attributeName, flowFileAttrValue);
				}

			} else if (flowfiledata instanceof List) {
				if (!typeOfElement.equalsIgnoreCase("array")) {
					String error = "Type mismatch occured. Expected type in input BE defintion is " + typeOfElement
							+ " but the received type is " + flowfiledata.getClass().getSimpleName()
							+ " Retry with correct data for " + attributeName;
					failedResult.add(error);
				}
				if (isConstruction)
					tempMap.writeStartArray(attributeName);
				doConstructionforComplexTypes(inputBERecord, tempMap, flowfiledata, propertiesObject, propertiesObject,
						"array", isConstruction, failedResult);
				if (isConstruction)
					tempMap.writeEnd();

			} else {
				if (!typeOfElement.equalsIgnoreCase("object")) {
					String error = "Type mismatch occured. Expected type in input BE defintion is " + typeOfElement
							+ " but the received type is " + flowfiledata.getClass().getSimpleName()
							+ " Retry with correct data for " + attributeName;
					failedResult.add(error);
				}
				if (isConstruction)
					tempMap.writeStartObject(attributeName);
				doConstructionforComplexTypes(inputBERecord, tempMap, flowfiledata, propertiesObject, propertiesObject,
						"object", isConstruction, failedResult);
				if (isConstruction)
					tempMap.writeEnd();
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
			Object flowfiledata, JSONObject propertiesObject, JSONObject attributeValue, String jsonType,
			boolean isConstruction, List<String> failedResult) {
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
			if (isArrayOfObjects && isConstruction)
				tempMap.writeStartObject();
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

				if (type.equalsIgnoreCase("array")) {
					String attributeName2 = getNameForCBE(attrValue);
					JSONObject cbePropertiesObject = getPropertiesForCBE(attrValue);
					// array - so start array
					// tempMap.writeStartArray(attributeName2);
					validateAndProduceResult(attributeName2, attributeValue, inputBERecord, tempMap,
							cbePropertiesObject, "array", isConstruction, failedResult);
					// tempMap.writeEnd();
				} else if (type.equals("object")) {
					String attributeName2 = getNameForCBEforObject(attrValue);
					JSONObject cbePropertiesObject = getPropertiesForCBEForObject(attrValue);
					// Object - so start object
					// writeStartObject(attributeName2);
					validateAndProduceResult(attributeName2, attributeValue, inputBERecord, tempMap,
							cbePropertiesObject, "object", isConstruction, failedResult);
					// tempMap.writeEnd();
				} else {
					try {
						if (!objectInArray.has(attrName) && isMandatory) {
							String error = "Failed to fetch value from Flowfile data for attribute " + attrName
									+ ". Since " + attrName + " is null or empty " + " in the Input BE record with buk "
									+ attrName;
							failedResult.add(error);
						} else if (objectInArray.has(attrName)) {
							
							String finalAttributeValue=objectInArray.get(attrName).toString();
							
							//FIX FOR XPB-135 STARTS HERE
							boolean isDateTime = false;
							if(Constants.TYPE_VALUE_DATETIME.equals(type)){
								isDateTime = true;
							}
							if(isDateTime && !StringUtils.isBlank(finalAttributeValue) && attrValue.has(Constants.ATTRIBUTE_DATE_FORMAT)){
								String srcDataFormat = (String) attrValue.get(Constants.ATTRIBUTE_DATE_FORMAT);
								finalAttributeValue = CommonUtils.convertDate(finalAttributeValue, srcDataFormat, Constants.DATE_FORMAT);
							}
							//FIX FOR XPB-135 ENDS HERE
							
							if (isMandatory && (objectInArray.get(attrName).toString().trim().length() > 0)) {
								if (isConstruction)
									tempMap.write(attrName, finalAttributeValue);
							} else if ((objectInArray.get(attrName).toString().trim().length() > 0)) {
								if (isConstruction)
									tempMap.write(attrName, finalAttributeValue);
							}
						}
					} catch (Exception ex) {
						failedResult.add("unable to add data");
					}
				}
			}

			if (isArrayOfObjects && isConstruction)
				tempMap.writeEnd();
		}
	}

	private JSONObject constructResponsePayload(JSONObject flowFileJSON, JSONObject outputJsonObj, JSONObject finalJson,
			ProcessSession session, JSONArray outputMappingArray, FlowFile flowFile, List<String> failedResult) throws Exception {
		String outputObjkey = null;
		String objDataType = null;
		JSONObject reqObj = new JSONObject();
		Iterator<?> outputJsonObjItr = outputJsonObj.keys();
		while (outputJsonObjItr.hasNext()) {
			outputObjkey = (String) outputJsonObjItr.next();
			Object outputJsonval = outputJsonObj.get(outputObjkey);
			String[] keys = null;
			if (outputJsonval instanceof String) {
				// get key value after removing the type conversion details if
				// present
				String val = (String) NifiUtils.getTypeConverted((String) outputJsonval, null, true);
				if (!outputObjkey.equalsIgnoreCase("objName")) {
					// objDataType =
					// outputJsonObj.get(outputObjkey).getClass().getSimpleName();
					Object finalValue = null;
					if (val.startsWith(Constants.PV)) {
						Mapping map = new Mapping();
						String[] processVariable = val.split(Constants.PV);
						map.setProcessVariable(processVariable[1]);
						finalValue = getFlowFileAttributes(flowFile, map, processVariable[1]);
						if (finalValue == null) {
							finalValue = getFlowFileAttributes(flowFileJSON, map, processVariable[1]);
						}
						finalJson.put(outputObjkey, finalValue);
						reqObj.put(outputObjkey, finalValue);
					} else if (val.startsWith(Constants.EV)) {
						String[] enterValue = val.split(Constants.EV);
						finalValue = enterValue[1];
						finalJson.put(outputObjkey, finalValue);
						reqObj.put(outputObjkey, finalValue);
					} else {
						if (null != flowFileJSON && flowFileJSON.has(val)) {
							objDataType = flowFileJSON.get(val).getClass().getSimpleName();
							if (Constants.INTEGER.equalsIgnoreCase(objDataType)) {
								finalValue = flowFileJSON.getInt(val);
							} else if (Constants.String.equalsIgnoreCase(objDataType)) {
								finalValue = flowFileJSON.getString(val);
							} else if (Constants.BOOLEAN.equalsIgnoreCase(objDataType)) {
								finalValue = flowFileJSON.getBoolean(val);
							}
						} else {
							String error = "Failed to fetch value from Flowfile data for attribute " + val + ". Since "
									+ val + " is null or empty " + " in the Response Flow file record with buk ";
							failedResult.add(error);
						}

					}
					if (finalValue != null && finalValue != "") {
						// apply type conversion on final value if applicable
						finalValue = NifiUtils.getTypeConverted((String) outputJsonval, finalValue, false);
						finalJson.put(outputObjkey, finalValue.toString());
						reqObj.put(outputObjkey, finalValue.toString());
					}
				}
			} else {
				JSONArray jsonArrVal = (JSONArray) outputJsonObj.get(outputObjkey);
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
						JSONArray outputMappingJsonArray = (JSONArray) outputMappingArray.get(1);
						for (int i = 0; i < outputMappingJsonArray.length(); i++) {
							JSONObject item = outputMappingJsonArray.getJSONObject(i);
							if (val.equalsIgnoreCase(item.get("objName").toString())) {
								childObj = item;
								break;
							}
						}
						Object ChildObject = getChildFlowFileJson(keys, flowFileJSON, Constants.JSON_OBJECT, session,
								flowFile, objDataType);

						if (ChildObject instanceof JSONObject) {
							childFlowFileJSON = (JSONObject) ChildObject;
						} else if (ChildObject instanceof JSONArray) {
							childFlowFileJSON = (JSONObject) ((JSONArray) ChildObject).get(0);
						}
						childObj = constructResponsePayload(childFlowFileJSON, childObj, childJson, session,
								outputMappingArray, flowFile, failedResult);

						if (childObj.length() > 0) {
							if (StringUtils.equalsIgnoreCase("Root", outputObjkey)) {
								Iterator childObjItr = childObj.keys();
								while (childObjItr.hasNext()) {
									String childKey = (String) childObjItr.next();
									Object childValue = childObj.get(childKey);
									finalJson.put(childKey, childValue);
									reqObj.put(childKey, childValue);
								}
							} else {
								// if key already exists
								if (reqObj.has(outputObjkey)) {
									childObj = includeAdditionalPramas(childObj, (JSONObject) reqObj.get(outputObjkey));
								}
								finalJson.put(outputObjkey, childObj);
								reqObj.put(outputObjkey, childObj);
							}
						}

					} else if (objDataType.equalsIgnoreCase(Constants.JSON_ARRAY)) {

						JSONObject childObj = new JSONObject();
						Object childFlowobj = getChildFlowFileJson(keys, flowFileJSON, Constants.JSON_ARRAY, session,
								flowFile, objDataType);
						JSONArray outputMappingJsonArray = (JSONArray) outputMappingArray.get(1);
						for (int i = 0; i < outputMappingJsonArray.length(); i++) {
							JSONObject item = outputMappingJsonArray.getJSONObject(i);
							if (val.equalsIgnoreCase(item.get("objName").toString())) {
								childObj = item;
								break;
							}
						}
						JSONArray tempArray = new JSONArray();
						if (childFlowobj != null) {
							JSONArray childFlowJSONArray = (JSONArray) childFlowobj;
							for (int count = 0; count < childFlowJSONArray.length(); count++) {
								JSONObject childJsonItem = childFlowJSONArray.getJSONObject(count);
								JSONObject finalchildJson = new JSONObject();
								JSONObject finalChildObj = new JSONObject();
								finalChildObj = constructResponsePayload(childJsonItem, childObj, finalchildJson,
										session, outputMappingArray, flowFile, failedResult);
								if (finalChildObj.length() > 0) {
									if (StringUtils.equalsIgnoreCase("Root", outputObjkey)) {
										Iterator childObjItr = finalChildObj.keys();
										while (childObjItr.hasNext()) {
											String childKey = (String) childObjItr.next();
											Object childValue = finalChildObj.get(childKey);
											finalJson.put(childKey, childValue);
											reqObj.put(childKey, childValue);
										}
									} else {
										tempArray.put(finalChildObj);
										childFinalJSONArray.put(finalChildObj);
										multipleArrayholder.put(val, tempArray);
									}
								}
							}
						}

						if (!StringUtils.equalsIgnoreCase("Root", outputObjkey)) {
							if (multipleArrayholder.size() == 2) {
								childFinalJSONArray = normaliseArray(multipleArrayholder);
							}
							finalJson.put(outputObjkey, childFinalJSONArray);
							reqObj.put(outputObjkey, childFinalJSONArray);
						}
					}

				}

			}

		}
		return reqObj;
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
				if (flowFileContent.startsWith("[")) {
					JSONArray flowFileJSONArray = new JSONArray(flowFileContent);
					finalObject = flowFileJSONArray.get(0);
				} else if (flowFileContent.startsWith("{")) {
					finalObject = new JSONObject(flowFileContent);
				}

				depth = 2;
			}
			for (int i = (keys.length - depth); i >= 0; i--) {
				if (finalObject instanceof JSONObject) {
					Object entry = ((JSONObject) finalObject).get(keys[i]);
					if (entry instanceof JSONArray) {
						if (objDataType.equalsIgnoreCase(Constants.JSON_OBJECT)) {
							// Json object should not be contained inside an
							// array
							throw new Exception("object found inside an array but not able to fetch from mapping");
						} else {
							JSONArray arrayEntry = (JSONArray) entry;
							finalObject = arrayEntry;
						}
					} else {
						finalObject = (JSONObject) entry;
					}
				} else if ((finalObject instanceof JSONArray)) {
					JSONArray finalObjectAsArray = ((JSONArray) finalObject);
					JSONArray finalNode = new JSONArray();
					for (int j = 0; j < finalObjectAsArray.length(); j++) {
						Object entry = ((JSONObject) finalObjectAsArray.get(j)).get(keys[i]);
						if (entry instanceof JSONArray) {
							JSONArray arrayEntry = (JSONArray) entry;
							for (int l = 0; l < arrayEntry.length(); l++) {
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

	private JSONObject includeAdditionalPramas(JSONObject newObject, JSONObject previousObject) {
		Iterator it = newObject.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			previousObject.put(key, newObject.get(key));
		}

		return previousObject;
	}

	private JSONArray normaliseArray(HashMap<String, JSONArray> multipleArrayholder) {
		String combinedKey = multipleArrayholder.keySet().toString();
		Iterator itr = multipleArrayholder.values().iterator();
		JSONArray arrSet1 = (JSONArray) itr.next();
		JSONArray arrSet2 = (JSONArray) itr.next();
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
					} else if (beValue.startsWith("[")) {
						JSONArray arr = new JSONArray(beValue);
						obj = (JSONObject) arr.get(0);
					} else {
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

}