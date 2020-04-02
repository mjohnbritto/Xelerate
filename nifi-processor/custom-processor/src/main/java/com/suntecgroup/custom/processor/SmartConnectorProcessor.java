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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.buk.Buk;
import com.suntecgroup.custom.processor.model.buk.EventBuk;
import com.suntecgroup.custom.processor.model.smartconnector.SmartMapping;
import com.suntecgroup.custom.processor.model.smartconnector.TypeConversion;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;
import com.suntecgroup.custom.processor.utils.CommonUtils;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.NifiUtils;
import com.suntecgroup.custom.processor.utils.SmartMappingUtils;

/*
 * This class is for creating a custom NiFi processor to handle the Smart Connector. This class
 * would read the incoming Business Entity and convert it to expected Outgoing Business Entity format.
 * 
 * @version 1.0 - September 2018
 * @author Mohammed Rizwan
 */

@Tags({ "connector" })
@CapabilityDescription("connector")
public class SmartConnectorProcessor extends AbstractProcessor {

	private ComponentLog logger;

	private List<PropertyDescriptor> properties;

	private Set<Relationship> relationships;

	private ObjectMapper mapper = null;
	
	private Gson gson = null;
	
	private Type mapTypeObj = null;
	
	private Type mapType = null;
	
	public static final PropertyDescriptor MAPPING_PARAMETER = new PropertyDescriptor.Builder()
			.name("MAPPING_PARAMETER").description("Connector Processor MAPPING_PARAMETER").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUTBE_DEFINITION = new PropertyDescriptor.Builder()
			.name("OUTPUTBE_DEFINITION").description("Connector Processor OUTPUTBE_DEFINATION").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor INPUTBE_NAME = new PropertyDescriptor.Builder().name("INPUT BUSINESS ENTITY")
			.description("Connector Processor Input Business Entity").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUTBE_NAME = new PropertyDescriptor.Builder()
			.name("OUTPUT BUSINESS ENTITY").description("Connector Processor Output Business Entity").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CONNECTOR_NAME = new PropertyDescriptor.Builder().name("CONNECTOR_NAME")
			.description("Connector Name").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor INPUT_BE_BUK_ATTRIBUTES = new PropertyDescriptor.Builder()
			.name("Input BE BUK Attributes").description("Input BE BUK attributes array").required(true)
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

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("success")
			.description("All files are routed to success").build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("failure")
			.description("All files are routed to failure").build();
	
	private String mappingParameterProperty = null;
	private List<SmartMapping> mappingList = null;
	private String outputBEDefinition = null;
	private Map<String, Object> outputStructureMap = null;
	private String connectorName = null;
	private String inputBE = null;
	private String outputBEType = null;
	private JSONArray arrayInBukAttributes = null;
	private JSONArray arrayOutBukAttributes = null;
	boolean isMergeSource = false;
	String pathName = null;
	
	@OnScheduled
	public void onScheduled(final ProcessContext processContext) throws NifiCustomException {
		mappingParameterProperty = processContext.getProperty(MAPPING_PARAMETER).evaluateAttributeExpressions()
				.getValue();
		outputBEDefinition = processContext.getProperty(OUTPUTBE_DEFINITION).evaluateAttributeExpressions()
				.getValue();
		try {
			mappingList = Arrays.asList(mapper.readValue(mappingParameterProperty, SmartMapping[].class));
		} catch (Exception e) {
			throw new NifiCustomException("Error occurred while reading mapping property: " + e.getMessage());
		}
		
		try {
			outputStructureMap = mapper.readValue(outputBEDefinition, Map.class);
		} catch (Exception e) {
			throw new NifiCustomException("Error occurred while reading outputbe property: " + e.getMessage());
		}
		connectorName = processContext.getProperty(CONNECTOR_NAME).evaluateAttributeExpressions().getValue();
		inputBE = processContext.getProperty(INPUTBE_NAME).evaluateAttributeExpressions().getValue();
		outputBEType = processContext.getProperty(OUTPUTBE_NAME).evaluateAttributeExpressions().getValue();
		String bukAttributes = processContext.getProperty(INPUT_BE_BUK_ATTRIBUTES).evaluateAttributeExpressions()
				.getValue();
		arrayInBukAttributes = new JSONArray(bukAttributes);
		bukAttributes = processContext.getProperty(OUTPUT_BE_BUK_ATTRIBUTES).evaluateAttributeExpressions().getValue();
		arrayOutBukAttributes = new JSONArray(bukAttributes);
		isMergeSource = processContext.getProperty(MERGE_SOURCE).evaluateAttributeExpressions().asBoolean();
		if (isMergeSource) {
			pathName = processContext.getProperty(PATH_NAME).evaluateAttributeExpressions().getValue();
		}
	}

	@Override
	protected void init(final ProcessorInitializationContext context) {
		final List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
		properties.add(MAPPING_PARAMETER);
		properties.add(OUTPUTBE_DEFINITION);
		properties.add(CONNECTOR_NAME);
		properties.add(INPUTBE_NAME);
		properties.add(OUTPUTBE_NAME);
		properties.add(INPUT_BE_BUK_ATTRIBUTES);
		properties.add(OUTPUT_BE_BUK_ATTRIBUTES);
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		properties.add(MERGE_SOURCE);
		properties.add(PATH_NAME);
		this.properties = Collections.unmodifiableList(properties);
		final Set<Relationship> relationships = new HashSet<Relationship>();
		this.relationships = Collections.unmodifiableSet(relationships);
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		allIgnoreTypes.add("Root");
		allIgnoreTypes.add("SubRoot");
		allIgnoreTypes.add("BeRoot");
		allIgnoreTypes.add("BeSubRoot");
		allIgnoreTypes.add("PvRoot");
		allIgnoreTypes.add("PvSubRoot");
		logger = context.getLogger();
		mapper = new ObjectMapper();
		gson = new GsonBuilder().create();
		mapTypeObj = new TypeToken<Map<String, Object>>() {}.getType();
		mapType = new TypeToken<Map<String, String>>() {}.getType();
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	List<String> failedResult = null;

	final List<String> allIgnoreTypes = new ArrayList<String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.nifi.processor.AbstractProcessor#onTrigger(org.apache.nifi.
	 * processor.ProcessContext, org.apache.nifi.processor.ProcessSession)
	 */
	@Override
	public void onTrigger(ProcessContext processContext, ProcessSession processSession) throws ProcessException {

		FlowFile flowFileObj = processSession.get();
		if (flowFileObj == null) {
			return;
		}	

		List<String> bukValues = new ArrayList<String>();

		boolean isMarker = Boolean.parseBoolean(flowFileObj.getAttribute(Constants.IS_MARKER));

		if (isMarker) {
			if (isMergeSource) {
				processSession.transfer(flowFileObj, REL_SUCCESS);
				return;
			} else {
				processSession.remove(flowFileObj);
				return;
			}
		}

		// Data for failure processing
		String errorType = Constants.TECHNICALERROR;
		String sessionId = processContext.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
		String runNumber = processContext.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
		HashMap<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put(Constants.ATTR_SESSION_ID, sessionId);
		attributesMap.put(Constants.ATTR_RUN_NUMBER, runNumber);
		processSession.putAllAttributes(flowFileObj, attributesMap);

		List<EventBuk> inputBEBUKList = new ArrayList<EventBuk>();
		List<EventBuk> outputBEBUKList = new ArrayList<EventBuk>();
		EventBuk invalidInEventBuk = new EventBuk();
		EventBuk invalidOutEventBuk = new EventBuk();
		int eventsCount = 0;

		failedResult = new ArrayList<String>();

		Map<String, String> flowFileAttributes = flowFileObj.getAttributes();

		// Reading & Writing.

		FlowFile flowFileOutput = processSession.clone(flowFileObj);
		InputStream smartInput = processSession.read(flowFileObj);
		OutputStream smartOutput = processSession.write(flowFileOutput);
		JsonReader reader = null;
		JsonWriter writer = null;

		if (smartInput != null) {
			try {

				

				

				// resultOutputMap contains Output Definition with Attribute
				// name and value as its structure.
				Map<String, Object> resultOutputMap = new LinkedHashMap<String, Object>();

				if (outputStructureMap != null && outputStructureMap.size() > 0) {

					resultOutputMap = fetchCompositeOutputStructure(outputStructureMap, resultOutputMap);

				}

				boolean isBreak = false;
				reader = new JsonReader(new InputStreamReader(smartInput, Constants.UTF_ENCODING));
				writer = new JsonWriter(new OutputStreamWriter(smartOutput, Constants.UTF_ENCODING));

				reader.setLenient(true);
				List<Map<String, Object>> smartOuputList = new LinkedList<Map<String, Object>>();

				reader.beginArray();
				writer.beginArray();

				while (reader.hasNext()) {
					eventsCount++;
					// Read data into object model
					Map<String, Object> jsonRecord = gson.fromJson(reader, mapTypeObj);

					// construct In BUK list @JOHN starts here
					EventBuk eventBuk = new EventBuk();
					for (int index = 0; index < arrayInBukAttributes.length(); index++) {
						String key = arrayInBukAttributes.getString(index);
						Object value = jsonRecord.get(key);
						if (!jsonRecord.containsKey(key) || null == value || StringUtils.isEmpty(value.toString())) {
							// BUK attribute is not available/value is empty
							invalidInEventBuk.addBuk(new Buk(key, ""));
						} else {
							eventBuk.addBuk(new Buk(key, value.toString()));
						}
					}
					inputBEBUKList.add(eventBuk);
					// construct In BUK list @JOHN ends here

					// Smart connector logic starts.
					smartOuputList = processRecord(flowFileAttributes, jsonRecord, inputBEBUKList, mappingList,
							resultOutputMap, bukValues, connectorName);
					// Smart connector logic ends.

					// writing
					if (smartOuputList != null && smartOuputList.size() > 0) {
						Map<String, Object> resultMap = smartOuputList.get(0);
						gson.toJson(resultMap, mapType, writer);

						// construct BUK list @JOHN starts here
						eventBuk = new EventBuk();
						for (int index = 0; index < arrayOutBukAttributes.length(); index++) {
							String key = arrayOutBukAttributes.getString(index);
							Object value = resultMap.get(key);
							if (!resultMap.containsKey(key) || null == value || StringUtils.isEmpty(value.toString())) {
								// BUK attribute is not available/value is empty
								invalidOutEventBuk.addBuk(new Buk(key, ""));
							} else {
								eventBuk.addBuk(new Buk(key, value.toString()));
							}
						}
						outputBEBUKList.add(eventBuk);
						// construct BUK list @JOHN ends here
					}

				}

				if ((invalidInEventBuk.getBuk() != null && invalidInEventBuk.getBuk().size() > 0)
						|| (invalidOutEventBuk.getBuk() != null && invalidOutEventBuk.getBuk().size() > 0)) {
					throw new NifiCustomException("Failed due to Invalid BUK!");
				}

				if (!isBreak) {
					reader.endArray();
				}

				writer.endArray();

			} catch (NifiCustomException ex) {
				logger.error("NifiCustomException occured while reading the JSON: " + ex.getMessage(), ex);
				closeStream(smartInput, smartOutput, reader, writer);

				if (isMergeSource) {
					FlowFile flowFile = NifiUtils.cloneFlowfileWithoutContent(flowFileOutput, processSession, logger);
					processSession.transfer(flowFile, REL_SUCCESS);
				}
				processSession.remove(flowFileOutput);

				String errorMessage = ex.getMessage();
				processSession.putAllAttributes(flowFileObj, attributesMap);
				flowFileObj = NifiUtils.updateFailureDetails(processContext, processSession, flowFileObj, inputBE,
						errorType, errorMessage);

				processSession.transfer(flowFileObj, REL_FAILURE);
				processSession.commit();
				return;
			} catch (Exception ex) {
				logger.error("Exception occured while reading the JSON: " + ex.getMessage(), ex);
				closeStream(smartInput, smartOutput, reader, writer);

				if (isMergeSource) {
					FlowFile flowFile = NifiUtils.cloneFlowfileWithoutContent(flowFileOutput, processSession, logger);
					processSession.transfer(flowFile, REL_SUCCESS);
				}

				processSession.remove(flowFileOutput);

				String errorMessage = ex.getMessage();
				processSession.putAllAttributes(flowFileObj, attributesMap);
				flowFileObj = NifiUtils.updateFailureDetails(processContext, processSession, flowFileObj, inputBE,
						errorType, errorMessage);

				processSession.transfer(flowFileObj, REL_FAILURE);
				processSession.commit();
				return;
			} finally {
				closeStream(smartInput, smartOutput, reader, writer);
			}
		}

		try {

			if (eventsCount < 1) {
				throw new NifiCustomException(
						connectorName + " failed to generate the output due to empty or null records");
			}

			else {
				processSession.putAttribute(flowFileOutput, Constants.INPUT_BUK,
						NifiUtils.convertObjectToJsonString(inputBEBUKList, logger));
				processSession.putAttribute(flowFileOutput, Constants.OUTPUT_BUK,
						NifiUtils.convertObjectToJsonString(outputBEBUKList, logger));

				String inputOutputMapping = "";
				for (int index = 0; index < eventsCount; index++) {
					inputOutputMapping += index;
					if ((index + 1) < eventsCount) {
						inputOutputMapping += "|";
					}
				}
				processSession.putAttribute(flowFileOutput, Constants.INPUT_OUTPUT_MAPPING, inputOutputMapping);

				processSession.getProvenanceReporter().modifyAttributes(flowFileOutput);

				processSession.remove(flowFileObj);
				processSession.putAttribute(flowFileOutput, "beName", outputBEType);
				if (isMergeSource) {
					processSession.putAttribute(flowFileOutput, Constants.ROUTE, pathName);
				}
				processSession.transfer(flowFileOutput, REL_SUCCESS);
				processSession.commit();

			}
		} catch (NifiCustomException nifiCustomException) {
			logger.error("Error occurred at SmartConnector :: " + nifiCustomException.getMessage(),
					nifiCustomException);

			if (isMergeSource) {
				FlowFile flowFile = NifiUtils.cloneFlowfileWithoutContent(flowFileOutput, processSession, logger);
				processSession.transfer(flowFile, REL_SUCCESS);
			}
			processSession.remove(flowFileOutput);

			String errorMessage = nifiCustomException.getMessage();
			processSession.putAllAttributes(flowFileObj, attributesMap);
			flowFileObj = NifiUtils.updateFailureDetails(processContext, processSession, flowFileObj, inputBE,
					errorType, errorMessage);

			processSession.transfer(flowFileObj, REL_FAILURE);
			processSession.commit();

		}

		catch (Exception ex) {
			logger.error("Exception occured at SmartConnector :: " + ex.getMessage(), ex);

			if (isMergeSource) {
				FlowFile flowFile = NifiUtils.cloneFlowfileWithoutContent(flowFileOutput, processSession, logger);
				processSession.transfer(flowFile, REL_SUCCESS);
			}
			processSession.remove(flowFileOutput);

			String errorMessage = ex.getMessage();
			processSession.putAllAttributes(flowFileObj, attributesMap);
			flowFileObj = NifiUtils.updateFailureDetails(processContext, processSession, flowFileObj, inputBE,
					errorType, errorMessage);

			processSession.transfer(flowFileObj, REL_FAILURE);
			processSession.commit();

		}

	}

	public void closeStream(InputStream smartInput, OutputStream smartOutput, JsonReader reader, JsonWriter writer) {
		try {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (smartInput != null) {
				smartInput.close();
			}

			if (smartOutput != null) {
				smartOutput.close();
			}
		} catch (IOException e) {
			logger.debug("Exception occured while closing the stream " + e.getMessage(), e);
		}
	}

	/**
	 * This method would read and process the flowfile content records.
	 * 
	 * @param flowFileAttributes
	 *            - contains flowfile attributes
	 * @param record
	 *            - contains input record taken from flowfile content data.
	 * @return List - modified record as per outputbe definition.
	 * @throws Exception 
	 */
	private List<Map<String, Object>> processRecord(Map<String, String> flowFileAttributes, Map<String, Object> record,
			List<EventBuk> inputBeBUK, List<SmartMapping> mappingList, Map<String, Object> resultOutputMap,
			List<String> bukValues, String connectorName) throws Exception {

		List<Map<String, Object>> successLst = new LinkedList<Map<String, Object>>();

		if (record != null && record.size() > 0) {

			successLst = constructCompositeRecord(flowFileAttributes, record, inputBeBUK, mappingList,
					resultOutputMap, bukValues, connectorName);

		}

		return successLst;

	}

	private List<Map<String, Object>> constructCompositeRecord(Map<String, String> flowFileAttributes,
			Map<String, Object> inputBERecord, List<EventBuk> inputBeBUK,
			List<SmartMapping> mappingList, Map<String, Object> resultOutputMap, List<String> bukValues,
			String connectorName) throws Exception {

		Map<String, Object> mappingResultMap = new LinkedHashMap<String, Object>();
		Map<String, Object> inputbukAttributeWithValues = new LinkedHashMap<String, Object>();

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		if (inputBeBUK != null && inputBeBUK.size() > 0) {
			for (EventBuk eventObj : inputBeBUK) {
				List<Buk> bukDetails = eventObj.getBuk();
				for (Buk bukObj : bukDetails) {
					String name = bukObj.getAttributeName();
					Object value = "";

					if (inputBERecord != null && inputBERecord.containsKey(name)) {
						value = inputBERecord.get(name);
						if (value != null && ((value instanceof String) || (value instanceof Number)))
							inputbukAttributeWithValues.put(name, inputBERecord.get(name));
					}
				}
			}
		}
		// Sub level denormalization
		List<String> subMainArrayLst = mappingList.stream().filter(t -> t.getToDataType().equalsIgnoreCase("Array"))
				.map(SmartMapping::getToValue).collect(Collectors.toList());
		Set<String> subDenorMainArrayLst = subMainArrayLst.stream()
				.filter(n -> subMainArrayLst.stream().filter(x -> x.equalsIgnoreCase(n)).count() > 1)
				.collect(Collectors.toSet());
		mappingResultMap = initiateCompositeMethod(inputBERecord, mappingList, inputbukAttributeWithValues,
				resultOutputMap, bukValues, connectorName, flowFileAttributes, subDenorMainArrayLst);

		if (mappingResultMap != null && mappingResultMap.size() > 0) {
			resultList.add(mappingResultMap);
		}

		return resultList;
	}

	private Map<String, Object> initiateCompositeMethod(Map<String, Object> inputBERecord, List<SmartMapping> mappingList,
			Map<String, Object> inputbukAttributeWithValues, Map<String, Object> resultOutputMap,
			List<String> bukValues, String connectorName, Map<String, String> flowFileAttributes,
			Set<String> subDenorMainArrayLst) throws Exception {

		// This method will concentrate on ROOT part and the submethod will work
		// on inner data.

		Object result = null;
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

		String bukString = "";

		Iterator<Entry<String, Object>> outputDefinitionIterate = resultOutputMap.entrySet().iterator();

		while (outputDefinitionIterate.hasNext()) {

			Entry<String, Object> keyValue = (Entry<String, Object>) outputDefinitionIterate.next();

			String toValueKey = keyValue.getKey();
			Object value = keyValue.getValue();

			// In ROOT, if we have object
			if (toValueKey != null && toValueKey.trim().contains(",object")) {

				String[] beNameObj = toValueKey.split(",object");

				String toCurrent = beNameObj[0];
				String toParent = "BeRoot";

				Map<String, Object> objectMap = (Map) value;
				String toPath = toCurrent + "-" + toParent + "-";

				result = prepareObjectDetails(toParent, toCurrent, inputBERecord, mappingList, objectMap, toPath,
						flowFileAttributes, null, subDenorMainArrayLst);

				resultMap.put(toCurrent, result);

			}

			// In ROOT, if we have array
			else if (toValueKey != null && toValueKey.trim().contains(",array")) {

				String[] beNameObj = toValueKey.split(",array");

				String toCurrent = beNameObj[0];
				String toParent = "BeRoot";
				if (subDenorMainArrayLst.contains(toCurrent)) {
					// array belong to denorm
					List<Map<String, Object>> resultList = SmartMappingUtils.getSubArayDenorm(inputBERecord, mappingList,
							toCurrent);
					resultMap.put(toCurrent, resultList);
				} else {
					String newToValue = toCurrent + "-" + toParent + "-";

					List<Map<String, Object>> listOuputStructure = (List) value;

					Map<String, Object> pvInputRecordWithPath = fetchArrayDataFromPVInputRecord(inputBERecord,
							mappingList, newToValue, null, flowFileAttributes);

					String skipFromPath = (String) pvInputRecordWithPath.get("fromPath");
					List<Map<String, Object>> inputRecordList = (List) pvInputRecordWithPath.get("inputRecordList");
					String source = (String) pvInputRecordWithPath.get("source");

					if (inputRecordList != null) {
						List<Object> resultList = new ArrayList<Object>();

						// iterating one record at a time from list of inner
						// input
						// record

						for (Map<String, Object> inputRecordArray : inputRecordList) {
							result = prepareArrayDetails(toParent, toCurrent, inputRecordArray, mappingList,
									listOuputStructure, newToValue, skipFromPath, flowFileAttributes, source);

							resultList.add(result);
						}
						resultMap.put(toCurrent, resultList);
					}
				}
			}

			else {

				// In ROOT, we have Primitive type

				Map<String, Object> objectMap = (Map) value;

				boolean isBuk = (boolean) objectMap.get("isBuk");
				boolean required = (boolean) objectMap.get("required");

				String toPathValue = toValueKey + "-" + "BeRoot" + "-";

				result = fetchValueFromInputMap(inputBERecord, mappingList, toPathValue, null, flowFileAttributes,
						null);

				if (isBuk) {
					if (bukString.trim().length() > 0) {
						bukString = bukString + "-" + result;
					} else {
						bukString = "-" + result;

					}
				}

				if (required) {
					if (result != null) {
						resultMap.put(toValueKey, result);
					} else {
						throw new NifiCustomException(connectorName + " - failed due to mandatory OutputBE attribute "
								+ toValueKey + " has null or empty value while processing input record "
								+ inputbukAttributeWithValues);
					}
				} else {
					resultMap.put(toValueKey, result);
				}
			}
		}

		if (bukString != null && !bukValues.contains(bukString.toLowerCase())) {
			bukValues.add(bukString.toLowerCase());
		} else {
			//throw new NifiCustomException("Duplicate BUK values found while creating OutputBE record for input record " + inputbukAttributeWithValues);
		}

		return resultMap;
	}

	private Object prepareArrayDetails(String toParent, String toCurrent, Map<String, Object> inputBERecord,
			List<SmartMapping> mappingList, List<Map<String, Object>> objectBEMap, String newToValue, String skipFromPath,
			Map<String, String> flowFileAttributes, String source) throws Exception {

		Object result = null;
		Map<String, Object> mappingResultMap = new LinkedHashMap<String, Object>();

		Iterator<Entry<String, Object>> outputDefinitionIterate = objectBEMap.get(0).entrySet().iterator();

		while (outputDefinitionIterate.hasNext()) {
			String toPathTemp = newToValue;

			Entry<String, Object> keyValue = (Entry<String, Object>) outputDefinitionIterate.next();
			String toValue = keyValue.getKey();
			Object value = keyValue.getValue();

			if (toValue != null && toValue.trim().contains(",object")) {
				Map<String, Object> ouputObjectMap = (Map) value;

				String[] beNameObj = toValue.split(",object");
				String toCurrentInner = beNameObj[0];
				toParent = toCurrent;

				toPathTemp = toCurrentInner + "-" + toPathTemp;

				result = prepareObjectDetailsForArray(toParent, toCurrentInner, inputBERecord, mappingList,
						ouputObjectMap, toPathTemp, skipFromPath, flowFileAttributes, source);

				mappingResultMap.put(toCurrentInner, result);

			}

			else if (toValue != null && toValue.trim().contains(",array")) {

				String[] beNameObj = toValue.split(",array");
				String toCurrentInner = beNameObj[0];
				toParent = toCurrent;

				toPathTemp = toCurrentInner + "-" + toPathTemp;

				List<Map<String, Object>> listOuputStructure = (List) value;

				Map<String, Object> inputRecordWithFromPath = fetchArrayDataFromPVInputRecord(inputBERecord,
						mappingList, toPathTemp, skipFromPath, flowFileAttributes);

				String fromNewPath = (String) inputRecordWithFromPath.get("fromPath");
				List<Map<String, Object>> inputRecordList = (List) inputRecordWithFromPath.get("inputRecordList");
				source = (String) inputRecordWithFromPath.get("source");

				if (inputRecordList != null) {
					List<Object> resultList = new ArrayList<Object>();

					// iterating one record at a time from list of inner input
					// record
					for (Map<String, Object> inputRecordArray : inputRecordList) {
						result = prepareArrayDetails(toParent, toCurrentInner, inputRecordArray, mappingList,
								listOuputStructure, toPathTemp, fromNewPath, flowFileAttributes, source);

						resultList.add(result);
					}
					mappingResultMap.put(toCurrentInner, resultList);

				}
			}

			else {

				toPathTemp = toValue + "-" + toPathTemp;
				result = fetchValueFromInputMap(inputBERecord, mappingList, toPathTemp, skipFromPath,
						flowFileAttributes, source);

				mappingResultMap.put(toValue, result);

			}
		}

		return mappingResultMap;

	}

	private Object prepareObjectDetailsForArray(String toParent, String toCurrent, Map<String, Object> inputBERecord,
			List<SmartMapping> mappingList, Map<String, Object> objectBEMap, String toPath, String skipFromValue,
			Map<String, String> flowFileAttributes, String source) throws Exception {

		Object result = null;
		Map<String, Object> mappingResultMap = new LinkedHashMap<String, Object>();

		Iterator<Entry<String, Object>> outputDefinitionIterate = objectBEMap.entrySet().iterator();

		while (outputDefinitionIterate.hasNext()) {

			String toPathTemp = toPath;

			Entry<String, Object> keyValue = (Entry<String, Object>) outputDefinitionIterate.next();
			String toValue = keyValue.getKey();
			Object value = keyValue.getValue();

			if (toValue != null && toValue.trim().contains(",object")) {

				Map<String, Object> ouputObjectMap = (Map) value;
				String[] beNameObj = toValue.split(",object");
				String toCurrentInner = beNameObj[0];
				toParent = toCurrent;
				toPathTemp = toCurrentInner + "-" + toPathTemp;

				result = prepareObjectDetailsForArray(toParent, toCurrentInner, inputBERecord, mappingList,
						ouputObjectMap, toPathTemp, skipFromValue, flowFileAttributes, source);

				mappingResultMap.put(toCurrentInner, result);

			}

			else if (toValue != null && toValue.trim().contains(",array")) {
				// object having array scenario

				String[] beNameObj = toValue.split(",array");

				String toCurrentInner = beNameObj[0];
				toParent = toCurrent;

				String newToValue = toCurrentInner + "-" + toPathTemp;

				List<Map<String, Object>> listOuputStructure = (List) value;

				Map<String, Object> pvInputRecordWithPath = fetchArrayDataFromPVInputRecord(inputBERecord, mappingList,
						newToValue, null, flowFileAttributes);

				String skipFromPath = (String) pvInputRecordWithPath.get("fromPath");
				List<Map<String, Object>> inputRecordList = (List) pvInputRecordWithPath.get("inputRecordList");
				source = (String) pvInputRecordWithPath.get("source");

				if (inputRecordList != null) {
					List<Object> resultList = new ArrayList<Object>();

					// iterating one record at a time from list of inner input
					// record
					for (Map<String, Object> inputRecordArray : inputRecordList) {
						result = prepareArrayDetails(toParent, toCurrentInner, inputRecordArray, mappingList,
								listOuputStructure, newToValue, skipFromPath, flowFileAttributes, source);

						resultList.add(result);
					}
					mappingResultMap.put(toCurrentInner, resultList);
				}
			}

			else {

				toPathTemp = toValue + "-" + toPathTemp;

				result = fetchValueFromInputMap(inputBERecord, mappingList, toPathTemp, skipFromValue,
						flowFileAttributes, source);

				mappingResultMap.put(toValue, result);
			}
		}

		return mappingResultMap;

	}

	private Map<String, Object> fetchArrayDataFromPVInputRecord(Map<String, Object> inputBERecord,
			List<SmartMapping> mappingList, String newToValue, String skipFromPath, Map<String, String> flowFileAttributes)
			throws NifiCustomException {

		List<Map<String, Object>> inputRecordList = null;
		Map<String, Object> pvInputRecordWithPath = new LinkedHashMap<String, Object>();

		try {

			for (SmartMapping mapping : mappingList) {

				Object result = null;
				String type = mapping.getType();
				String fromPath = mapping.getFromPath();
				String toPath = mapping.getToPath();

				if (skipFromPath != null) {
					fromPath = fromPath.replace(skipFromPath, "");
				}

				// pvroot is an top level object and this case will never happen
				// here for array to array mapping.

				if (type != null && "PvSubRoot".equalsIgnoreCase(type.trim())) {

					if (toPath != null && toPath.trim().equalsIgnoreCase(newToValue)) {
						String processVariableName = mapping.getProcessVariableAttribute();

						Map<String, Object> pvObjectArray = getObjectfromFlowFileAttributes(flowFileAttributes,
								processVariableName);

						String[] split = fromPath.split("-");
						Map<String, Object> findHierarichyMap = pvObjectArray;

						for (int i = split.length - 1; i >= 0; i--) {

							String temp = split[i];

							if (temp != null && temp.trim().length() > 0 && !(temp.trim().equalsIgnoreCase("pvtypebe"))
									&& !(temp.trim().equalsIgnoreCase(processVariableName))) {

								result = findHierarichyMap.get(temp);

								if (result != null && result instanceof Map) {
									findHierarichyMap = (Map<String, Object>) result;
								}
							}
						}
						inputRecordList = (List) result;

						pvInputRecordWithPath.put("fromPath", fromPath);
						pvInputRecordWithPath.put("inputRecordList", inputRecordList);
						pvInputRecordWithPath.put("source", "pv");

						break;

					}
				}

				// Input record
				else if (mapping.getType() != null/* && allIgnoreTypes.contains(mapping.getType().trim())*/) {

					if (toPath != null && toPath.trim().equalsIgnoreCase(newToValue)) {

						Map<String, Object> findHierarichyMap = inputBERecord;
						String[] split = fromPath.split("-");

						for (int i = split.length - 1; i >= 0; i--) {

							String temp = split[i];

							if (temp != null && temp.trim().length() > 0 && !allIgnoreTypes.contains(temp.trim())) {
								result = findHierarichyMap.get(temp);

								if (result != null && result instanceof Map) {
									findHierarichyMap = (Map<String, Object>) result;
								}
							}
						}
						inputRecordList = (List) result;

						pvInputRecordWithPath.put("fromPath", fromPath);
						pvInputRecordWithPath.put("inputRecordList", inputRecordList);
						break;
					}
				}
			}
		} catch (Exception ex) {
			throw new NifiCustomException("Exception occured in fetchArrayDataFromPVInputRecord " + ex.getMessage());
		}

		return pvInputRecordWithPath;
	}

	private Object fetchValueFromInputMap(Map<String, Object> inputBERecord, List<SmartMapping> mappingList,
			String newToValue, String skipFromValue, Map<String, String> flowFileAttributes, String source)
			throws Exception {

		Object result = null;
		String type = null;

		for (SmartMapping mapping : mappingList) {

			type = mapping.getType();

			if (type != null && !allIgnoreTypes.contains(type.trim())) {
				String fromPath = mapping.getFromPath();
				String toPath = mapping.getToPath();
				String fromValue = mapping.getFromValue();
				ArrayList<TypeConversion> typeConversionArray = mapping.getTypeConversionArray();
				

				if (toPath != null && toPath.trim().equalsIgnoreCase(newToValue.trim())) {

					if (type.trim().equalsIgnoreCase("smartip")) {

						if (skipFromValue != null) {
							String newFromPath = fromPath.replace(skipFromValue, "");
							result = getInputRecordFromNestedStructure(newFromPath, inputBERecord, fromValue,typeConversionArray);
							return result;
						} else {
							result = getInputRecordFromNestedStructure(fromPath, inputBERecord, fromValue, typeConversionArray);
							return result;
						}
					}

					else if (type.trim().equalsIgnoreCase("pv")) {
						// This is PV as an primitive variable
						String processVariableName = mapping.getProcessVariableAttribute();
						result = getDatafromFlowFileAttributes(flowFileAttributes, processVariableName);
						return result;

					}

					else if (type.trim().equalsIgnoreCase("PvTypeBE")) {

						// This method call will only read primitive value from
						// root or object and NEVER
						// Array.

						String[] split = null;
						Map<String, Object> findHierarichyMap = null;

						String processVariableName = mapping.getProcessVariableAttribute();

						if (source != null && source.trim().equalsIgnoreCase("pv")) {
							findHierarichyMap = inputBERecord;
						} else {
							findHierarichyMap = getObjectfromFlowFileAttributes(flowFileAttributes,
									processVariableName);
						}

						if (skipFromValue != null) {
							String newFromPath = fromPath.replace(skipFromValue, "");
							split = newFromPath.split("-");

						} else {
							split = fromPath.split("-");
						}

						String temp = null;
						for (int i = split.length - 1; i >= 0; i--) {

							temp = split[i];

							if (temp != null && temp.trim().length() > 0 && !(temp.trim().equalsIgnoreCase("pvtypebe"))
									&& !(temp.trim().equalsIgnoreCase(processVariableName))) {

								result = findHierarichyMap.get(temp);

								if (result != null && result instanceof Map) {
									findHierarichyMap = (Map<String, Object>) result;
								}
							}
						}

						if (result != null && result instanceof List) {
							throw new NifiCustomException(
									"Primitive attribute " + fromValue + " value cannot be pulled from " + temp
											+ " array hierarchy of PV " + processVariableName);
						}

						return result;
					}

					else if (type.trim().equalsIgnoreCase("ev")) {

						result = mapping.getFromValue();
						return result;

					}
				}
			}
		}

		return result;
	}

	private Object prepareObjectDetails(String toParent, String toCurrent, Map<String, Object> inputBERecord,
			List<SmartMapping> mappingList, Map<String, Object> objectBEMap, String toPath,
			Map<String, String> flowFileAttributes, String source, Set<String> subDenorMainArrayLst) throws Exception {

		Object result = null;
		Map<String, Object> mappingResultMap = new LinkedHashMap<String, Object>();

		Iterator<Entry<String, Object>> outputDefinitionIterate = objectBEMap.entrySet().iterator();

		while (outputDefinitionIterate.hasNext()) {

			String toPathTemp = toPath;

			Entry<String, Object> keyValue = (Entry<String, Object>) outputDefinitionIterate.next();
			String toValue = keyValue.getKey();
			Object value = keyValue.getValue();

			if (toValue != null && toValue.trim().contains(",object")) {
				Map<String, Object> ouputObjectMap = (Map) value;

				String[] beNameObj = toValue.split(",object");
				String toCurrentInner = beNameObj[0];
				toParent = toCurrent;
				toPathTemp = toCurrentInner + "-" + toPathTemp;

				result = prepareObjectDetails(toParent, toCurrentInner, inputBERecord, mappingList, ouputObjectMap,
						toPathTemp, flowFileAttributes, source, subDenorMainArrayLst);

				mappingResultMap.put(toCurrentInner, result);

			}

			else if (toValue != null && toValue.trim().contains(",array")) {
				// object having array scenario

				String[] beNameObj = toValue.split(",array");

				String toCurrentInner = beNameObj[0];
				toParent = toCurrent;

				if (subDenorMainArrayLst.contains(toCurrentInner)) {
					// array belong to denorm
					List<Map<String, Object>> resultList = SmartMappingUtils.getSubArayDenorm(inputBERecord, mappingList,
							toCurrentInner);
					mappingResultMap.put(toCurrentInner, resultList);
				} else {
					String newToValue = toCurrentInner + "-" + toPathTemp;
	
					List<Map<String, Object>> listOuputStructure = (List) value;
	
					Map<String, Object> pvInputRecordWithPath = fetchArrayDataFromPVInputRecord(inputBERecord, mappingList,
							newToValue, null, flowFileAttributes);
	
					String skipFromPath = (String) pvInputRecordWithPath.get("fromPath");
					List<Map<String, Object>> inputRecordList = (List) pvInputRecordWithPath.get("inputRecordList");
					source = (String) pvInputRecordWithPath.get("source");
	
					if (inputRecordList != null) {
						List<Object> resultList = new ArrayList<Object>();
	
						// iterating one record at a time from list of inner input
						// record
						for (Map<String, Object> inputRecordArray : inputRecordList) {
							result = prepareArrayDetails(toParent, toCurrentInner, inputRecordArray, mappingList,
									listOuputStructure, newToValue, skipFromPath, flowFileAttributes, source);
	
							resultList.add(result);
						}
						mappingResultMap.put(toCurrentInner, resultList);
					}
				}
			}

			else {

				// This is a primitive type id inside a Object.
				toPathTemp = toValue + "-" + toPathTemp;
				result = fetchValueFromInputMap(inputBERecord, mappingList, toPathTemp, null, flowFileAttributes,
						source);
				mappingResultMap.put(toValue, result);

			}
		}

		return mappingResultMap;

	}

	private Object getInputRecordFromNestedStructure(String fromPath, Map<String, Object> inputBERecord,
			String fromValue, ArrayList<TypeConversion> typeConversionArray) throws Exception {

		Map<String, Object> findHierarichyMap = inputBERecord;

		Object result = null;
		String[] split = fromPath.split("-");

		for (int i = split.length - 1; i >= 0; i--) {

			String temp = split[i];

			if (temp != null && temp.trim().length() > 0 && !allIgnoreTypes.contains(temp.trim())) {

				result = findHierarichyMap.get(temp);
				if(typeConversionArray!=null){
				CommonUtils.typeConvertResult(result, typeConversionArray);
				}

				if (result != null && result instanceof Map) {
					findHierarichyMap = (Map<String, Object>) result;
				} else if (result != null && result instanceof List) {
					throw new NifiCustomException("Primitive attribute " + fromValue + " value cannot be pulled from "
							+ temp + " array hierarchy ");
				}
			}
		}

		return result;
	}

	private Map<String, Object> fetchCompositeOutputStructure(Map<String, Object> outputStructureMap,
			Map<String, Object> resultMap) {

		Map<String, Object> temp = (Map<String, Object>) outputStructureMap.get("properties");

		Iterator<Entry<String, Object>> iterator = temp.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, Object> ev = iterator.next();

			String key = ev.getKey();

			Map<String, Object> value = (Map<String, Object>) ev.getValue();

			if (value != null && value.size() > 0) {

				String type = (String) value.get("type");

				if (type.equalsIgnoreCase("object")) {
					String beName = (String) value.get("aliasName");

					Map<String, Object> temp1 = new LinkedHashMap<String, Object>();
					if (value != null && value.size() > 0) {
						temp1 = fetchCompositeOutputStructure(value, temp1);
					}
					resultMap.put(beName + ",object", temp1);

				} else if (type.equalsIgnoreCase("array")) {
					Map<String, Object> items = (Map) value.get("items");
					String beName = (String) items.get("aliasName");
					Map<String, Object> temp2 = new LinkedHashMap<String, Object>();
					if (items != null && items.size() > 0) {
						temp2 = fetchCompositeOutputStructure(items, temp2);
					}
					List<Map<String, Object>> arrayObj = new ArrayList<Map<String, Object>>();
					arrayObj.add(temp2);
					resultMap.put(beName + ",array", arrayObj);
				} else {
					resultMap.put(key, value);
				}

			}

		}

		return resultMap;
	}

	private Object getDatafromFlowFileAttributes(Map<String, String> flowFileAttributeObj, String requiredAttribute)
			throws NifiCustomException {

		String processVariableStr = (String) flowFileAttributeObj.get(requiredAttribute);

		if (!StringUtils.isEmpty(processVariableStr)) {
			try {
				ProcessVariable processVariable = mapper.readValue(processVariableStr, ProcessVariable.class);

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
			} catch (Exception e) {
				throw new NifiCustomException("Error occurred while reading flowfile Attributes: " + e.getMessage());
			}
		}
		return null;
	}

	private Map<String, Object> getObjectfromFlowFileAttributes(Map<String, String> flowFileAttributeObj,
			String requiredAttribute) throws NifiCustomException {

		String processVariableStr = (String) flowFileAttributeObj.get(requiredAttribute);
		Map<String, Object> objectProcessVariable = null;

		if (!StringUtils.isEmpty(processVariableStr)) {
			try {
				ProcessVariable processVariable = mapper.readValue(processVariableStr, ProcessVariable.class);
				String pvObjectJsonStr = processVariable.getValue().getBeValue();

				objectProcessVariable = mapper.readValue(pvObjectJsonStr, Map.class);

			} catch (Exception e) {
				throw new NifiCustomException(
						"Error occurred while reading flowfile Object Attributes: " + e.getMessage());
			}
		}
		return objectProcessVariable;
	}

}