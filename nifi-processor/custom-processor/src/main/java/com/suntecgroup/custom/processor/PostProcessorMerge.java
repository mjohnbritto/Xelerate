/*
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor;

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

import javax.json.Json;
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
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.buk.Buk;
import com.suntecgroup.custom.processor.model.buk.EventBuk;
import com.suntecgroup.custom.processor.model.mappingparameter.Mapping;
import com.suntecgroup.custom.processor.model.mappingparameter.OutputBeMapping;
import com.suntecgroup.custom.processor.model.mappingparameter.OutputParamMapping;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;
import com.suntecgroup.custom.processor.utils.CommonUtils;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.MapDeserializer;
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
@Tags({ "postprocessor,merge" })
@CapabilityDescription("PostProcessor to Merge")
public class PostProcessorMerge extends AbstractProcessor {

	List<String> failedResult = null;
	private ObjectMapper mapper = null;

	private ComponentLog logger;

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description("Success relationship").build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();

	public static final PropertyDescriptor OUTPUT_MAPPING_PARAMETER = new PropertyDescriptor.Builder()
			.name("Output Mapping Parameter").description("Post Processor OUTPUT Mapping Params").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_BUSINESS_ENTITY = new PropertyDescriptor.Builder()
			.name("Output Business Entity").description("Post Processor OUTPUT_BUSINESS_ENTITY").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUTBE_DEFINITION = new PropertyDescriptor.Builder()
			.name("OutputBE Definition").description("Post Processor Output Definition").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OPERATOR_NAME = new PropertyDescriptor.Builder().name("Operator Name")
			.description("Post Processor Operator Name").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_BE_BUK_ATTRIBUTES = new PropertyDescriptor.Builder()
			.name("Output BE BUK Attributes").description("Output BE BUK attributes array").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
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
	Type type = new TypeToken<Map<String, Object>>() {
	}.getType();
	JsonDeserializer<Map<String, Object>> customMapDeserializer = new MapDeserializer();
	GsonBuilder gsonBuilder = new GsonBuilder();
	Gson gson = null;
	
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
		properties.add(MERGE_SOURCE);
		properties.add(PATH_NAME);

		this.properties = Collections.unmodifiableList(properties);

		final Set<Relationship> relationships = new HashSet<Relationship>();
		this.relationships = Collections.unmodifiableSet(relationships);
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		mapper = new ObjectMapper();
		gsonBuilder.registerTypeAdapter(type, customMapDeserializer);
		gson = gsonBuilder.create();
		logger = context.getLogger();
	}

	@Override
	public Set<Relationship> getRelationships() {
		return this.relationships;
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}
	
	String outputMappingProperty = null;
	OutputParamMapping outputParamMapping = null;
	String outputBeProperty = null;
	String outputBEDefinition = null;
	String operatorName = null;
	String bukAttributes = null;
	JSONArray arrayBukAttributes = null;
	JSONObject opBEDefinitionJSON = null;
	boolean isMergeSource = false;
	String pathName = null;

	@OnScheduled
	public void onScheduled(final ProcessContext context) throws NifiCustomException {

		try {		
			outputMappingProperty = context.getProperty(OUTPUT_MAPPING_PARAMETER).evaluateAttributeExpressions()
					.getValue();		
			outputParamMapping = mapper.readValue(outputMappingProperty, OutputParamMapping.class);
			outputBeProperty = context.getProperty(OUTPUT_BUSINESS_ENTITY).evaluateAttributeExpressions().getValue();
			outputBEDefinition = context.getProperty(OUTPUTBE_DEFINITION).evaluateAttributeExpressions().getValue();
			operatorName = context.getProperty(OPERATOR_NAME).evaluateAttributeExpressions().getValue();
			bukAttributes = context.getProperty(OUTPUT_BE_BUK_ATTRIBUTES).evaluateAttributeExpressions().getValue();
			arrayBukAttributes = new JSONArray(bukAttributes);
			opBEDefinitionJSON = new JSONObject(outputBEDefinition);
			isMergeSource = context.getProperty(MERGE_SOURCE).evaluateAttributeExpressions().asBoolean();
			if (isMergeSource) {
				pathName = context.getProperty(PATH_NAME).evaluateAttributeExpressions().getValue();
			}			
		} catch (Exception e) {
			logger.error("Error occurred while reading Postprocessor OutputMapping property: " + e.getMessage(), e);
			throw new NifiCustomException("Exception while parsing output mapping");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.nifi.processor.AbstractProcessor#onTrigger(org.apache.nifi.
	 * processor.ProcessContext, org.apache.nifi.processor.ProcessSession)
	 */
	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {

		FlowFile flowFile = session.get();

		if (null == flowFile) {
			return;
		}
		boolean isMarker = Boolean.parseBoolean(flowFile.getAttribute(Constants.IS_MARKER));

		if (isMergeSource && isMarker) {
			session.transfer(flowFile, REL_SUCCESS);
			return;
		}

		
		List<Mapping> listOpmParam = null;
		
		
		List<EventBuk> outputBEBUKList = new ArrayList<EventBuk>();
		EventBuk invalidEventBuk = new EventBuk();
		String inputBEName = flowFile.getAttribute(Constants.BENAME);
		
		try {
			CommonUtils.validateSessionId(context, session, flowFile, SESSION_ID, logger);
			CommonUtils.validateRunNumber(context, session, flowFile, RUN_NUMBER, logger);
		} catch (Exception ex) {
			logger.error("error occured while reading processing session details: " + ex.getMessage(), ex);
			route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR, ex.getMessage(),
					isMergeSource);
			return;
		}

		Map<String, Object> contextMap = new HashMap<String, Object>();
		failedResult = new ArrayList<String>();

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
				
				Gson gsonWriter = new GsonBuilder().create();

				Type typeWriter = new TypeToken<Map<String, String>>() {
				}.getType();

				reader.beginObject();
				writer.beginArray();

				while (reader.hasNext()) {

					JsonToken nextElement = reader.peek();

					if (nextElement.equals(JsonToken.NAME)) {
						String name = reader.nextName();
						if (name != null && Constants.context.equals(name.trim().toLowerCase())) {
							Map<String, Object> jsonRecord = gson.fromJson(reader, type);
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
								Map<String, Object> jsonRecord = gson.fromJson(reader, type);

								// postprocessor logic starts.
								if (!StringUtils.isBlank(outputBeProperty)) {
									processorOuputList = processRecord(session, jsonRecord, opBEDefinitionJSON);
								}
								// postprocessor logic ends.
								if (failedResult != null && failedResult.size() > 0) {
									isBreak = true;
									break;
								}

								if (processorOuputList != null && processorOuputList.size() > 0) {
									Map<String, Object> resultMap = processorOuputList.get(0);
									gsonWriter.toJson(resultMap, typeWriter, writer);

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

			} catch (

			NifiCustomException nifiException) {
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
				route(flowFile, REL_FAILURE, context, session, inputBEName, Constants.TECHNICALERROR, ex.getMessage(),
						isMergeSource);
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
		@SuppressWarnings("unchecked")
		Map<String, Object> contextParametersMap = (Map<String, Object>) contextMap.get(Constants.contextParameters);
		JSONObject contextParamJson = new JSONObject(contextParametersMap);

		if (outputParamMapping != null) {
			listOpmParam = outputParamMapping.getOutputMapping();

			if (listOpmParam != null && listOpmParam.size() > 0) {
				for (Mapping mappingParam : listOpmParam) {
					setFlowFileAttribute(mappingParam, contextParamJson, session, flowFileOutput, mapper);
				}
			}
		}

		try {
			if (failedResult != null && failedResult.size() > 0) {
				throw new NifiCustomException(operatorName
						+ " failed due to missing mapping value for mandatory attributes: " + failedResult.toString());
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
				route(flowFileOutput, REL_SUCCESS, context, session, null, null, null, isMergeSource);
			}
		} catch (NifiCustomException nifiCustomException) {
			logger.error("Error occurred at Postprocessor :: " + nifiCustomException.getMessage(), nifiCustomException);
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
			JSONObject outputDefinitionObject) {
		List<Map<String, Object>> successLst = new LinkedList<Map<String, Object>>();
		if (inputRecord != null && inputRecord.size() > 0) {
			successLst = constructRecordsforEffectiveBE(successLst, outputDefinitionObject, inputRecord);
		}
		return successLst;
	}
	
	private List<Map<String, Object>> constructRecordsforEffectiveBE(List<Map<String, Object>> successLst,
			JSONObject inputDefinition, Map<String, Object> inputBERecord) {
		try {
			Map<String, Object> anchorInputBERecord = null;
			String aliasName = inputDefinition.optString(Constants.ALIASNAME);
			
			if (!StringUtils.isEmpty(aliasName)) {				
				Object obj = inputBERecord.get(aliasName);
				Gson gson = new GsonBuilder().create();
				Type typeReader = new TypeToken<Map<String, Object>>() {
				}.getType();
				anchorInputBERecord = gson.fromJson(obj.toString(), typeReader);
			} else {
				anchorInputBERecord = inputBERecord;
			}
			JSONObject inputBEdefiniton = inputDefinition.getJSONObject(Constants.OUTPUTBE);
			JSONObject propertiesObject = inputBEdefiniton.getJSONObject(Constants.PROPERTIES);
			StringWriter writer = new StringWriter();
			JsonGenerator tempMap = Json.createGenerator(writer);
			List<LinkedHashMap<String, Object>> resultJSON = new ArrayList<LinkedHashMap<String, Object>>();
			validatebyDefinition(propertiesObject, resultJSON, anchorInputBERecord, tempMap);
//			Map<String, Object> finalJsonMap = new Gson().fromJson(processedJSON, HashMap.class);
			successLst.add(anchorInputBERecord);
		} catch (Exception ex) {
			failedResult.add("Exception during the JSON Construction from definition " + ex.getMessage());
		}

		return successLst;
	}
	
	@SuppressWarnings("unchecked")
	private void validatebyDefinition(JSONObject propertiesObject, List<LinkedHashMap<String, Object>> resultJSON,
			Map<String, Object> inputBERecord, JsonGenerator tempMap) {

		Iterator<String> keyset = propertiesObject.keys();
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
				attributeName = getNameForCBE(attributeValue);
				// value can be array or object type

				if (typeAttributeValue.equals("array")) {
					// tempMap.writeStartArray(attributeName);
					JSONObject cbePropertiesObject = getPropertiesForCBE(attributeValue);
					validateAndProduceResult(attributeName, attributeValue, inputBERecord, tempMap, cbePropertiesObject,
							"array");
					// tempMap.writeEnd();
				} else {
					// tempMap.writeStartObject(attributeName);
					JSONObject cbePropertiesObject = getPropertiesForCBEForObject(attributeValue);
					validateAndProduceResult(attributeName, attributeValue, inputBERecord, tempMap, cbePropertiesObject,
							"object");
					// tempMap.writeEnd();
				}

			} else {
				// iteratively add the contents
				validateAndProduceResult(attributeName, attributeValue, inputBERecord, tempMap, propertiesObject,
						"string");
			}

		}
		tempMap.writeEnd();
		tempMap.flush();
	}
	
	@SuppressWarnings("unchecked")
	private void validateAndProduceResult(String attributeName, JSONObject attributeValue,
			Map<String, Object> inputBERecord, JsonGenerator tempMap, JSONObject propertiesObject,
			String typeOfElement) {

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

				if (isMandatory) {
					if (flowFileAttrValue != null && flowFileAttrValue.trim().length() > 0) {
						tempMap.write(attributeName, flowFileAttrValue);
					}

					else {
						String error = "Failed to fetch value from Flowfile data for attribute " + attributeName
								+ ". Since " + attributeName + " is null or empty "
								+ " in the Input BE record with buk " + attributeName;
						failedResult.add(error);
					}

				} else if (flowFileAttrValue != null && flowFileAttrValue.trim().length() > 0) {
					tempMap.write(attributeName, flowFileAttrValue);
				}

			} else if (flowfiledata instanceof List) {
				if (!typeOfElement.equalsIgnoreCase("array")) {
					String error = "Type mismatch occured. Expected type in input BE defintion is " + typeOfElement
							+ " but the received type is " + flowfiledata.getClass().getSimpleName()
							+ " Retry with correct data for " + attributeName;
					failedResult.add(error);
				}
				tempMap.writeStartArray(attributeName);
				doConstructionforComplexTypes(inputBERecord, tempMap, flowfiledata, propertiesObject, propertiesObject,
						"array");
				tempMap.writeEnd();

			} else {
				if (!typeOfElement.equalsIgnoreCase("object")) {
					String error = "Type mismatch occured. Expected type in input BE defintion is " + typeOfElement
							+ " but the received type is " + flowfiledata.getClass().getSimpleName()
							+ " Retry with correct data for " + attributeName;
					failedResult.add(error);
				}
				tempMap.writeStartObject(attributeName);
				doConstructionforComplexTypes(inputBERecord, tempMap, flowfiledata, propertiesObject, propertiesObject,
						"object");
				tempMap.writeEnd();
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	public void doConstructionforComplexTypes(Map<String, Object> inputBERecord, JsonGenerator tempMap,
			Object flowfiledata, JSONObject propertiesObject, JSONObject attributeValue, String jsonType) {
		Gson gson = new Gson();
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
			if (isArrayOfObjects)
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
							cbePropertiesObject, "array");
					// tempMap.writeEnd();
				} else if (type.equals("object")) {
					String attributeName2 = getNameForCBEforObject(attrValue);
					JSONObject cbePropertiesObject = getPropertiesForCBEForObject(attrValue);
					// Object - so start object
					// writeStartObject(attributeName2);
					validateAndProduceResult(attributeName2, attributeValue, inputBERecord, tempMap,
							cbePropertiesObject, "object");
					// tempMap.writeEnd();
				} else {
					try {
						if (!objectInArray.has(attrName) && isMandatory) {
							String error = "Failed to fetch value from Flowfile data for attribute " + attrName
									+ ". Since " + attrName + " is null or empty " + " in the Input BE record with buk "
									+ attrName;
							failedResult.add(error);
						} else if(objectInArray.has(attrName)) {
							if (isMandatory && (objectInArray.get(attrName).toString().trim().length() > 0)) {
								tempMap.write(attrName, (String) objectInArray.get(attrName));
							} else if ((objectInArray.get(attrName).toString().trim().length() > 0)) {
								tempMap.write(attrName, (String) objectInArray.get(attrName));
							}
						}
					} catch (Exception ex) {
						failedResult.add("unable to add data");
					}
				}
			}

			if (isArrayOfObjects)
				tempMap.writeEnd();
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
			FlowFile flowFileOutput, ObjectMapper mapper) {

		// Start : Get the process variable from the flowfile attribute
		ProcessVariable processVariable = null;
		String processVariableStr = flowFileOutput.getAttribute(mappingParam.getProcessVariable());
		if (!StringUtils.isEmpty(processVariableStr)) {
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
			// Adding condition for merge operator to send marker file, in case
			// of failure
			if (isMergeSource) {
				session.putAttribute(flowfile, Constants.ROUTE, pathName);
				FlowFile markerFlowFile = NifiUtils.cloneFlowfileWithoutContent(flowfile, session, logger);
				session.transfer(markerFlowFile, REL_SUCCESS);
			}
			// Update failure details in flowfile attributes
			flowfile = NifiUtils.updateFailureDetails(context, session, flowfile, beName, errorType, errorMessage);
		} else if (StringUtils.equalsIgnoreCase(REL_SUCCESS.getName(), relationship.getName())) {
			// adding path name to the flow file
			if (isMergeSource) {
				String pathName = context.getProperty(PATH_NAME).evaluateAttributeExpressions().getValue();
				session.putAttribute(flowfile, Constants.ROUTE, pathName);
			}
		}
		session.transfer(flowfile, relationship);
		session.commit();
	}

}
