/**
 * DecisionMatrixProcessor is a customized processor class for the Nifi Processor.
 *
 * @version 1.0 23 Sep 2018
 * @author Rakesh Kumar Singh
 */
package com.suntecgroup.custom.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.model.buk.Buk;
import com.suntecgroup.custom.processor.model.buk.EventBuk;
import com.suntecgroup.custom.processor.model.startandend.Decision;
import com.suntecgroup.custom.processor.model.startandend.DecisionProcessVariable;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;
import com.suntecgroup.custom.processor.model.startandend.Rule;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.JSONObjectDeserializer;
import com.suntecgroup.custom.processor.utils.NifiUtils;

@Tags({ "decisionprocessor" })
@CapabilityDescription("Decision Matrix for SPLIT")
@SeeAlso({})
@ReadsAttributes({ @ReadsAttribute(attribute = "", description = "") })
@WritesAttributes({
		@WritesAttribute(attribute = DecisionMatrixProcessor.ROUTE_ATTRIBUTE_KEY, description = "The relation to which the FlowFile was routed") })
public class DecisionMatrixProcessor extends AbstractProcessor {

	private ComponentLog LOGGER;

	final List<PropertyDescriptor> modifiableProperty = new ArrayList<PropertyDescriptor>();
	final Set<Relationship> modifiableRelationships = new HashSet<>();
	private Set<Relationship> relationships;
	private volatile Set<String> dynamicPropertyNames = new HashSet<>();
	public static final String ROUTE_ATTRIBUTE_KEY = "DecisionMatrixProcessor.Route";

	private static final String routePropertyNameValue = "Route to Property name";

	public static final Relationship DEFAULT = new Relationship.Builder().name("default")
			.description("If the decisions are false the flowfile will go to default").build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();

	public static final PropertyDescriptor PROCESS_VARIABLE = new PropertyDescriptor.Builder().name("Process Variables")
			.description("process variables json string").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor DECISIONS = new PropertyDescriptor.Builder().name("decisions")
			.description("decisions json string").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor INPUT_BE_TYPE = new PropertyDescriptor.Builder()
			.name("Input Business Entity").description("Set input BE Type").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor INPUT_BE_BUK_ATTRIBUTES = new PropertyDescriptor.Builder()
			.name("Input BE BUK Attributes").description("Input BE BUK attributes array").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor EXCLUSIVEFLAG = new PropertyDescriptor.Builder().name("exclusiveFlag")
			.description("flag for exclusive/inclusive").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor MERGE_SOURCE = new PropertyDescriptor.Builder().name("Merge Source")
			.description("flag for defining the processor as source processor of merge")
			.allowableValues("true", "false").required(true).defaultValue("false")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final AllowableValue ROUTE_PROPERTY_NAME = new AllowableValue(routePropertyNameValue,
			"Route to Property name",
			"A copy of the FlowFile will be routed to each relationship whose corresponding expression evaluates to 'true'");

	private static final List<String> singleOperandOperatorList = Arrays
			.asList(new String[] { "IsNull", "IsNotNull", "IsUpperCase", "IsLowerCase", "isTrue", "isFalse" });

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor PATH_NAME = new PropertyDescriptor.Builder().name("Path_Name")
			.description("path name for the merge processor").addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.required(false).build();

	private List<PropertyDescriptor> properties;
	private ObjectMapper mapper = new ObjectMapper();
	
	private Type jsonObjectType = new TypeToken<JSONObject>() {
	}.getType();
	private JsonDeserializer<JSONObject> customJSONObjectDeserializer = new JSONObjectDeserializer();
	private GsonBuilder gsonBuilder = new GsonBuilder();
	private Gson gson = null;
	
	@Override
	protected void init(final ProcessorInitializationContext context) {
		modifiableProperty.add(PROCESS_VARIABLE);
		modifiableProperty.add(DECISIONS);
		modifiableProperty.add(INPUT_BE_BUK_ATTRIBUTES);
		modifiableProperty.add(EXCLUSIVEFLAG);
		modifiableProperty.add(INPUT_BE_TYPE);
		modifiableProperty.add(SESSION_ID);
		modifiableProperty.add(RUN_NUMBER);
		modifiableProperty.add(MERGE_SOURCE);
		modifiableProperty.add(PATH_NAME);
		this.properties = Collections.unmodifiableList(modifiableProperty);
		modifiableRelationships.add(DEFAULT);
		modifiableRelationships.add(REL_FAILURE);
		this.relationships = Collections.unmodifiableSet(modifiableRelationships);
		LOGGER = context.getLogger();
		gsonBuilder.registerTypeAdapter(jsonObjectType, customJSONObjectDeserializer);
		gson = gsonBuilder.create();
	}

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}
	
	boolean isMergeSource = false;
	String pathName = "";
	String inputBE = "";
	String decisionJsonString = null;
	List<Decision> decisionList = new ArrayList<>();
	String exclusiveFlag = "false";
	String inputBukAttributes = "";
	JSONArray inputBukAttributesArray = null;
	
	@OnScheduled
	public void onScheduled(final ProcessContext context) throws JsonParseException, JsonMappingException, IOException {
		
		isMergeSource = context.getProperty(MERGE_SOURCE).evaluateAttributeExpressions().asBoolean();
		pathName = context.getProperty(PATH_NAME).evaluateAttributeExpressions().getValue();
		inputBE = context.getProperty(INPUT_BE_TYPE).evaluateAttributeExpressions().getValue();
		decisionJsonString = context.getProperty(DECISIONS).evaluateAttributeExpressions().getValue();
		if (!StringUtils.isBlank(decisionJsonString)) {
			decisionList = mapper.readValue(decisionJsonString, new TypeReference<List<Decision>>() {
			});
		}
		exclusiveFlag = context.getProperty(EXCLUSIVEFLAG).evaluateAttributeExpressions().getValue();
		inputBukAttributes = context.getProperty(INPUT_BE_BUK_ATTRIBUTES).evaluateAttributeExpressions()
				.getValue();
		inputBukAttributesArray = new JSONArray(inputBukAttributes);
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {

		// Reading the flowFile
		final FlowFile origFlowFile = session.get();
		if (null == origFlowFile) {
			return;
		}

		boolean isMarker = Boolean.parseBoolean(origFlowFile.getAttribute(Constants.IS_MARKER));
		boolean isSplitted = Boolean.parseBoolean(origFlowFile.getAttribute(Constants.IS_SPLITTED));

		Map<String, String> reltionAndPath = new HashMap<String, String>();
		
		if (StringUtils.isNotBlank(pathName)) {
			String[] pathArray = pathName.split(" ");
			for (String path : pathArray) {
				String[] pathIndex = path.split("=");
				reltionAndPath.put(pathIndex[0], pathIndex[1]);
			}
		}

		if (isMarker) {
			if (isMergeSource) {
				for (Relationship relationship : context.getAvailableRelationships()) {
					String relationshipName = relationship.getName();
					if (!Constants.FAILURE.equalsIgnoreCase(relationshipName)) {
						FlowFile markerFlowFile = session.clone(origFlowFile);
						if (reltionAndPath.containsKey(relationshipName)) {
							session.putAttribute(markerFlowFile, Constants.ROUTE, reltionAndPath.get(relationshipName));
						}
						session.transfer(markerFlowFile, relationship);
					}
				}
				session.remove(origFlowFile);
				return;
			} else {
				session.remove(origFlowFile);
				return;
			}
		}

		// Data required for failure processing
		String sessionId = context.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
		String runNumber = context.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
		String errorType = Constants.TECHNICALERROR;

		HashMap<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put(Constants.ATTR_SESSION_ID, sessionId);
		attributesMap.put(Constants.ATTR_RUN_NUMBER, runNumber);
		session.putAllAttributes(origFlowFile, attributesMap);

		Map<String, String> flowFileAttributes = origFlowFile.getAttributes();

		// Reader
		InputStream inputStream = null;
		JsonReader reader = null;

		// Writer map of all relationship
		List<OutputStream> streamList = new ArrayList<OutputStream>();
		Map<String, JsonWriter> writerMap = new HashMap<String, JsonWriter>();

		// Relationship & FlowFile map
		Map<String, FlowFile> flowFileMap = new HashMap<String, FlowFile>();
		Map<String, Relationship> relationshipMap = new HashMap<String, Relationship>();

		// Relationship & it's event tracking code
		Map<String, StringBuilder> trackerCodeMap = new HashMap<String, StringBuilder>();

		Set<Relationship> relationshipSet = context.getAvailableRelationships();
		for (Relationship relationship : relationshipSet) {
			relationshipMap.put(relationship.getName(), relationship);
		}

		try {

			String bpProcessVarJsonString = context.getProperty(PROCESS_VARIABLE).evaluateAttributeExpressions()
					.getValue();
			
			

			List<DecisionProcessVariable> updatedPVList = new ArrayList<>();
			
			List<EventBuk> inputBEBUKList = new ArrayList<EventBuk>();

			if (!StringUtils.isBlank(bpProcessVarJsonString)) {
				updatedPVList = mapper.readValue(bpProcessVarJsonString,
						new TypeReference<List<DecisionProcessVariable>>() {
						});
			}
			

			if (StringUtils.isBlank(inputBE)) { // if BE is not configured

				Map<String, Object> eventRecord = new HashMap<String, Object>();
				JSONObject jsonObject = new JSONObject(eventRecord);

				boolean isDecisionFlag = false;
				for (Decision decision : decisionList) {

					if (!Constants.DEFAULT.equalsIgnoreCase(decision.getDecisionName())
							&& relationshipMap.containsKey(decision.getDecisionName())) {

						boolean validExpression = validateExpression(decision.getExpression(), flowFileAttributes,
								jsonObject);

						if (validExpression) {
							// Creating new writer for new relationship
							FlowFile flowFile = session.create(origFlowFile);
							flowFileMap.put(decision.getDecisionName(), flowFile);
							OutputStream outputStream = session.write(flowFile);
							streamList.add(outputStream);
							JsonWriter writer = new JsonWriter(
									new OutputStreamWriter(outputStream, Constants.UTF_ENCODING));
							writer.beginArray();
							writerMap.put(decision.getDecisionName(), writer);

							// Event tracking code
							trackerCodeMap.put(decision.getDecisionName(), new StringBuilder(""));

							isDecisionFlag = true;
							if (exclusiveFlag.equals("true")) {
								break;
							}

						}
					}

				}
				if (!isDecisionFlag) {

					FlowFile flowFile = session.create(origFlowFile);
					flowFileMap.put(Constants.DEFAULT, flowFile);
					OutputStream outputStream = session.write(flowFile);
					streamList.add(outputStream);
					JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, Constants.UTF_ENCODING));
					writer.beginArray();
					writerMap.put(Constants.DEFAULT, writer);

					// Event tracking code
					trackerCodeMap.put(Constants.DEFAULT, new StringBuilder(""));
				}
			} else { // if BE is configured

				// Reader for incoming flow file
			//	FlowFile streamingFlowFile = session.create(origFlowFile);
				inputStream = session.read(origFlowFile);
				reader = new JsonReader(new InputStreamReader(inputStream, Constants.UTF_ENCODING));
				reader.setLenient(true);
				reader.beginArray();

				int eventCount = -1;

				while (reader.hasNext()) {

					eventCount++;
					// Read data into object model
//					Map<String, Object> eventRecord = gsonBuilder.fromJson(reader, type);
					JSONObject eventRecord = gson.fromJson(reader, jsonObjectType);

					// Construct BUK list starts
					EventBuk eventBuk = new EventBuk();
					for (int index = 0; index < inputBukAttributesArray.length(); index++) {
						String key = inputBukAttributesArray.getString(index);
						Object value = eventRecord.get(key);
						if (eventRecord.has(key) && null != value && !StringUtils.isEmpty(value.toString())) {
							eventBuk.addBuk(new Buk(key, value.toString()));
						}
					}
					inputBEBUKList.add(eventBuk);
					// Construct BUK list ends

					boolean isDecisionFlag = false;
					for (Decision decision : decisionList) {

						if (!Constants.DEFAULT.equalsIgnoreCase(decision.getDecisionName())
								&& relationshipMap.containsKey(decision.getDecisionName())) {

							boolean validExpression = validateExpression(decision.getExpression(), flowFileAttributes,
									eventRecord);

							if (validExpression) {
								JsonElement jsonElement = gson.fromJson(eventRecord.toString(),
										JsonElement.class);
								if (writerMap.containsKey(decision.getDecisionName())) {
									// Writing event record into flow file
									gson.toJson(jsonElement, writerMap.get(decision.getDecisionName()));
								} else {
									// Creating new writer for new relationship
									FlowFile flowFile = session.create(origFlowFile);
									flowFileMap.put(decision.getDecisionName(), flowFile);
									OutputStream outputStream = session.write(flowFile);
									streamList.add(outputStream);
									JsonWriter writer = new JsonWriter(
											new OutputStreamWriter(outputStream, Constants.UTF_ENCODING));
									writer.beginArray();
									writerMap.put(decision.getDecisionName(), writer);
									gson.toJson(jsonElement, writerMap.get(decision.getDecisionName()));
								}

								// Event tracking code
								if (trackerCodeMap.containsKey(decision.getDecisionName())) {
									StringBuilder trackCode = trackerCodeMap.get(decision.getDecisionName());
									trackCode.append("," + String.valueOf(eventCount));
								} else {
									trackerCodeMap.put(decision.getDecisionName(),
											new StringBuilder(String.valueOf(eventCount)));
								}

								isDecisionFlag = true;
								if (exclusiveFlag.equals("true")) {
									break;
								}

							}
						}

					}
					if (!isDecisionFlag) {
						JsonElement jsonElement = gson.fromJson(eventRecord.toString(), JsonElement.class);
						if (writerMap.containsKey(Constants.DEFAULT)) {
							gson.toJson(jsonElement, writerMap.get(Constants.DEFAULT));
						} else {
							FlowFile flowFile = session.create(origFlowFile);
							flowFileMap.put(Constants.DEFAULT, flowFile);
							OutputStream outputStream = session.write(flowFile);
							streamList.add(outputStream);
							JsonWriter writer = new JsonWriter(
									new OutputStreamWriter(outputStream, Constants.UTF_ENCODING));
							writer.beginArray();
							writerMap.put(Constants.DEFAULT, writer);
							gson.toJson(jsonElement, writerMap.get(Constants.DEFAULT));
						}

						// Event tracking code
						if (trackerCodeMap.containsKey(Constants.DEFAULT)) {
							StringBuilder trackCode = trackerCodeMap.get(Constants.DEFAULT);
							trackCode.append("," + String.valueOf(eventCount));
						} else {
							trackerCodeMap.put(Constants.DEFAULT, new StringBuilder(String.valueOf(eventCount)));
						}

					}
				}
				reader.close();
//				session.remove(streamingFlowFile);
			}

			// Closing all writer after ends array
			if (null != writerMap && writerMap.size() > 0) {
				for (Entry<String, JsonWriter> entry : writerMap.entrySet()) {
					JsonWriter writer = entry.getValue();
					writer.endArray();
					writer.close();
				}
				writerMap.clear();
			}

			// Framing tracking code for traceability
			StringBuilder UUID_String = null; // new StringBuilder("");
			StringBuilder trackCode_String = null; // new StringBuilder("");
			for (Entry<String, Relationship> relationshipEntry : relationshipMap.entrySet()) {
				String decisionName = relationshipEntry.getKey();
				if (!decisionName.equalsIgnoreCase("Failure")) {
					String uuid = "";
					if (flowFileMap.containsKey(decisionName)) {
						uuid = flowFileMap.get(decisionName).getAttribute("uuid");
					}
					if (null == UUID_String) {
						UUID_String = new StringBuilder("");
						UUID_String.append(uuid);
					} else {
						UUID_String.append("|" + uuid);
					}

					String trackCode = "";
					if (trackerCodeMap.containsKey(decisionName)) {
						trackCode = trackerCodeMap.get(decisionName).toString();
					}
					if (null == trackCode_String) {
						trackCode_String = new StringBuilder("");
						trackCode_String.append(trackCode);
					} else {
						trackCode_String.append("|" + trackCode);
					}
				}
			}

			session.putAttribute(origFlowFile, Constants.INPUT_BUK,
					NifiUtils.convertObjectToJsonString(inputBEBUKList, LOGGER));
			session.putAttribute(origFlowFile, Constants.OUTPUT_FLOWFILEUUID_MAPPING, UUID_String.toString());
			session.putAttribute(origFlowFile, Constants.INPUT_OUTPUT_MAPPING, trackCode_String.toString());
			session.getProvenanceReporter().modifyAttributes(origFlowFile);

			// Transfer flow file to all relationship
			for (Entry<String, FlowFile> mapEntry : flowFileMap.entrySet()) {
				FlowFile flowFile = mapEntry.getValue();
				String decisionName = mapEntry.getKey();
				updateProcessVariables(updatedPVList, flowFile, session, decisionName);
				session.putAttribute(flowFile, ROUTE_ATTRIBUTE_KEY, decisionName);
				session.putAttribute(flowFile, Constants.SOURCE_OPERATOR, context.getName());
				if (isMergeSource) {
					session.putAttribute(flowFile, Constants.IS_SPLITTED, "true");
				}
				session.putAttribute(flowFile, Constants.ROUTE, reltionAndPath.get(decisionName));
				session.transfer(flowFile, relationshipMap.get(mapEntry.getKey()));
			}

			// If a flow file is not available due to a error, construct and
			// send a dummy flow file to the next processor and define the flow
			// file as marker File
			createAndSendMarkerflowFile(relationshipMap, flowFileMap, origFlowFile, session, false, isMergeSource,
					reltionAndPath);

			session.remove(origFlowFile);
			session.commit();

		} catch (IOException ex) {
			LOGGER.error("IOException exception occurred :: " + ex.getMessage(), ex);
			try {
				if (null != reader) {
					reader.close();
				}
				if (null != writerMap && writerMap.size() > 0) {
					for (Entry<String, JsonWriter> entry : writerMap.entrySet()) {
						if (null != entry.getValue()) {
							JsonWriter writer = entry.getValue();
							writer.endArray();
							writer.close();
						}
					}
				}
			} catch (IOException ioException) {
				LOGGER.error("IOException occured while closing the reader " + ioException.getMessage(), ioException);
			}
			session.rollback();
			FlowFile flowFile = session.get();

			String errorMessage = ex.getMessage();
			session.putAllAttributes(flowFile, attributesMap);
			flowFile = NifiUtils.updateFailureDetails(context, session, flowFile, inputBE, errorType, errorMessage);
			if (isSplitted) {
				createAndSendMarkerflowFile(relationshipMap, flowFileMap, flowFile, session, true, isMergeSource,
						reltionAndPath);
			}
			session.transfer(flowFile, REL_FAILURE);
			session.commit();
		} catch (ParseException ex) {
			LOGGER.error("ParseException exception occurred :: " + ex.getMessage(), ex);
			try {
				if (null != reader) {
					reader.close();
				}
				if (null != writerMap && writerMap.size() > 0) {
					for (Entry<String, JsonWriter> entry : writerMap.entrySet()) {
						if (null != entry.getValue()) {
							JsonWriter writer = entry.getValue();
							writer.endArray();
							writer.close();
						}
					}
				}
			} catch (IOException ioException) {
				LOGGER.error("IOException occured while closing the reader " + ioException.getMessage(), ioException);
			}
			session.rollback();
			FlowFile flowFile = session.get();

			String errorMessage = ex.getMessage();
			session.putAllAttributes(flowFile, attributesMap);
			flowFile = NifiUtils.updateFailureDetails(context, session, flowFile, inputBE, errorType, errorMessage);
			if (isSplitted) {
				createAndSendMarkerflowFile(relationshipMap, flowFileMap, flowFile, session, true, isMergeSource,
						reltionAndPath);
			}
			session.transfer(flowFile, REL_FAILURE);
			session.commit();
		} catch (Exception ex) {
			LOGGER.error("Exception occurred :: " + ex.getMessage(), ex);
			try {
				if (null != reader) {
					reader.close();
				}
				if (null != writerMap && writerMap.size() > 0) {
					for (Entry<String, JsonWriter> entry : writerMap.entrySet()) {
						if (null != entry.getValue()) {
							JsonWriter writer = entry.getValue();
							writer.endArray();
							writer.close();
						}
					}
				}
			} catch (IOException ioException) {
				LOGGER.error("IOException occured while closing the reader " + ioException.getMessage(), ioException);
			}
			session.rollback();
			FlowFile flowFile = session.get();

			String errorMessage = ex.getMessage();
			session.putAllAttributes(flowFile, attributesMap);
			flowFile = NifiUtils.updateFailureDetails(context, session, flowFile, inputBE, errorType, errorMessage);
			if (isSplitted) {
				createAndSendMarkerflowFile(relationshipMap, flowFileMap, flowFile, session, true, isMergeSource,
						reltionAndPath);
			}
			session.transfer(flowFile, REL_FAILURE);
			session.commit();
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != streamList && streamList.size() > 0) {
					for (OutputStream outputStream : streamList) {
						if (null != outputStream) {
							outputStream.close();
						}
					}
				}
			} catch (IOException exception) {
				LOGGER.error("Exception occured while closing the stream " + exception.getMessage(), exception);
			}
		}
	}

	private void createAndSendMarkerflowFile(Map<String, Relationship> relationshipMap,
			Map<String, FlowFile> flowFileMap, FlowFile origFlowFile, ProcessSession session, boolean isException,
			boolean isMergeSource, Map<String, String> reltionAndPath) {
		if (isMergeSource) {
			for (Entry<String, Relationship> relationShip : relationshipMap.entrySet()) {
				if ((isException || (!flowFileMap.keySet().contains(relationShip.getKey())))
						&& (!relationShip.getKey().equalsIgnoreCase(Constants.FAILURE))) {
					FlowFile flowFile = NifiUtils.cloneFlowfileWithoutContent(origFlowFile, session, LOGGER);
					if (!isException) {
						session.putAttribute(flowFile, Constants.MARKER_TYPE, Constants.BUSINESS_FAIL);
					}
					if (reltionAndPath.containsKey(relationShip.getKey())) {
						session.putAttribute(flowFile, Constants.ROUTE, reltionAndPath.get(relationShip.getKey()));
					}
					session.transfer(flowFile, relationshipMap.get(relationShip.getKey()));
				}

			}
		}
	}

	@Override
	public void onPropertyModified(final PropertyDescriptor descriptor, final String oldValue, final String newValue) {
		try {
			List<Decision> decisionList = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			final Set<String> newDynamicPropertyNames = new HashSet<>(dynamicPropertyNames);
			if (newValue == null) {
				newDynamicPropertyNames.remove(descriptor.getName());
			} else if (oldValue == null) { // new property
				newDynamicPropertyNames.add(descriptor.getName());
			}

			modifiableProperty.add(descriptor);
			this.dynamicPropertyNames = Collections.unmodifiableSet(newDynamicPropertyNames);

			// formulate the new set of Relationships
			final Set<Relationship> newRelationships = new HashSet<>();
			if (descriptor.getDisplayName().equals("decisions")) {
				if (null != newValue) {
					decisionList = mapper.readValue(newValue, new TypeReference<List<Decision>>() {
					});
					for (Decision decision : decisionList) {
						newRelationships.add(new Relationship.Builder().name(decision.getDecisionName()).build());
					}
				}
			}
			modifiableRelationships.addAll(newRelationships);
		} catch (Exception ex) {
			LOGGER.error("Error occurred :: " + ex.getMessage(), ex);
		}

	}

	@Override
	protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(final String propertyDescriptorName) {
		return new PropertyDescriptor.Builder().required(false).name(propertyDescriptorName)
				.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).dynamic(true)
				.expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).build();
	}

	private static void updateProcessVariables(List<DecisionProcessVariable> updatedPVList, FlowFile flowFile,
			ProcessSession session, String decisionName) throws IOException {
		for (DecisionProcessVariable updatedProcessVariables : updatedPVList) {
			if (decisionName.equals(updatedProcessVariables.getDecisionName())) {
				for (ProcessVariable updatedProcessVar : updatedProcessVariables.getProcessVariable()) {
					getDatafromFlowFileAttributes(flowFile, updatedProcessVar, session);
				}
				break;
			}
		}
	}

	private static void getDatafromFlowFileAttributes(FlowFile flowFile, ProcessVariable updatedProcessVariable,
			ProcessSession session) throws IOException {

		String processVariableStr = flowFile.getAttribute(updatedProcessVariable.getName());
		ObjectMapper mapper = new ObjectMapper();
		if (!StringUtils.isEmpty(processVariableStr)) {

			ProcessVariable processVariable = mapper.readValue(processVariableStr, ProcessVariable.class);
			String typeName = updatedProcessVariable.getType().getTypeName();
			switch (typeName) {
			case "Number":
				processVariable.getType().setTypeName(typeName);
				processVariable.getValue().setIntValue(updatedProcessVariable.getValue().getIntValue());
				session.removeAttribute(flowFile, processVariable.getName());
				session.putAttribute(flowFile, processVariable.getName(), processVariable.toJsonString());
				break;
			case "String":
				processVariable.getType().setTypeName(typeName);
				processVariable.getValue().setStringValue(updatedProcessVariable.getValue().getStringValue());
				session.removeAttribute(flowFile, processVariable.getName());
				session.putAttribute(flowFile, processVariable.getName(), processVariable.toJsonString());
				break;
			case "Boolean":
				processVariable.getType().setTypeName(typeName);
				processVariable.getValue().setBooleanValue(updatedProcessVariable.getValue().getBooleanValue());
				session.removeAttribute(flowFile, processVariable.getName());
				session.putAttribute(flowFile, processVariable.getName(), processVariable.toJsonString());
				break;
			case "DateTime":
				processVariable.getType().setTypeName(typeName);
				processVariable.getValue().setDateValue(updatedProcessVariable.getValue().getDateValue());
				session.removeAttribute(flowFile, processVariable.getName());
				session.putAttribute(flowFile, processVariable.getName(), processVariable.toJsonString());
				break;
			default:
				break;
			}
		}
	}

	private boolean validateExpression(String decisionMatrixExpression, Map<String, String> flowFileAttr,
			JSONObject jsonObject)
			throws JsonParseException, JsonMappingException, ParseException, IOException {
		boolean nifiExpression = false;
		if (!decisionMatrixExpression.isEmpty()) {
			JSONObject expressionContent = new JSONObject(decisionMatrixExpression);
			String condition = expressionContent.getString(Constants.CONDITION);
			JSONArray rulesArray = expressionContent.getJSONArray(Constants.RULES);
			nifiExpression = expressionRules(condition, rulesArray, flowFileAttr, jsonObject);
		}
		return nifiExpression;
	}

	private String getLeafAttributeValueFromRootObject(JSONObject dataObject, String requiredAttribute) {

		String attrValue = StringUtils.EMPTY;
		JSONObject tmpObj = dataObject;
		if (null != dataObject) {
			String field = requiredAttribute;
			String[] fieldArray = field.split("\\.");

			for (int i = 1; i < (fieldArray.length); i++) {
				String attrName = fieldArray[i];
				if (null == tmpObj) {
					return attrValue;
				} else if (i == (fieldArray.length - 1)) {
					attrValue = String.valueOf(tmpObj.get(attrName));
				} else {
					tmpObj = tmpObj.optJSONObject(attrName);
				}
			}
		}
		return attrValue;
	}

	private String getDatafromFlowFileAttributes(Map<String, String> flowFileAttr, String requiredAttribute, String nestedAtrribute) {

		String processVariableStr = (String) flowFileAttr.get(requiredAttribute);
		String attrValue = StringUtils.EMPTY;
		if (!StringUtils.isEmpty(processVariableStr)) {
			try {
				ProcessVariable processVariable = mapper.readValue(processVariableStr, ProcessVariable.class);

				if (Constants.PV_TYPE_CATEGORY_PRIMITIVE
						.equalsIgnoreCase(processVariable.getType().getTypeCategory())) {
					switch (processVariable.getType().getTypeName().toLowerCase()) {
					case Constants.dataTypeNumber:
						attrValue = processVariable.getValue().getIntValue().toString();
						break;
					case Constants.dataTypeString:
						attrValue = processVariable.getValue().getStringValue().toString();
						break;
					case Constants.dataTypeBoolean:
						attrValue = processVariable.getValue().getBooleanValue().toString();
						break;
					case Constants.dataTypeDate:
						attrValue = processVariable.getValue().getDateValue();
						break;
					default:
						break;
					}
				} else {
					String beValue = processVariable.getValue().getBeValue();
					JSONObject dataObject = new JSONObject(beValue);
					attrValue = getLeafAttributeValueFromRootObject(dataObject, nestedAtrribute);
					return attrValue;
				}

			} catch (Exception ex) {
				LOGGER.error("Error occurred :: " + ex.getMessage(), ex);
			}
		}
		return attrValue;
	}

	private boolean expressionRules(String condition, JSONArray rulesArray, Map<String, String> flowFileAttr,
			JSONObject dataObject)
			throws ParseException, JsonParseException, JsonMappingException, IOException {
		boolean nifiExpression = false;
		if (condition.equals(Constants.OR)) {
			if (rulesArray.length() > 0) {
				for (int i = 0; i < rulesArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) rulesArray.get(i);
					if (jsonObject.has(Constants.CONDITION) && jsonObject.get(Constants.CONDITION) != null) {
						nifiExpression = expressionRules(jsonObject.getString(Constants.CONDITION),
								jsonObject.getJSONArray(Constants.RULES), flowFileAttr, dataObject);
						if (nifiExpression) {
							break;
						}
					} else {
						Rule rules = mapper.readValue(jsonObject.toString(), Rule.class);
						nifiExpression = createNifiExpression(rules, flowFileAttr, dataObject);
						if (nifiExpression) {
							break;
						}
					}
				}
			}
		} else if (condition.equals(Constants.AND)) {
			if (rulesArray.length() > 0) {
				for (int i = 0; i < rulesArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) rulesArray.get(i);
					if (jsonObject.has(Constants.CONDITION) && jsonObject.get(Constants.CONDITION) != null) {
						nifiExpression = expressionRules(jsonObject.getString(Constants.CONDITION),
								jsonObject.getJSONArray(Constants.RULES), flowFileAttr, dataObject);
						if (!nifiExpression) {
							break;
						}
					} else {
						Rule rule = mapper.readValue(jsonObject.toString(), Rule.class);
						nifiExpression = createNifiExpression(rule, flowFileAttr, dataObject);
						if (!nifiExpression) {
							break;
						}
					}
				}
			}
		}
		return nifiExpression;
	}

	private boolean createNifiExpression(Rule rule, Map<String, String> flowFileAttr, JSONObject dataObject)
			throws ParseException {
		boolean evaluationResult = false;
		String lhsValue = StringUtils.EMPTY;
		String rhsValue = StringUtils.EMPTY;
		String operator = rule.getOperator();
		String dataType = rule.getType();
		String fromValue = StringUtils.EMPTY;
		String toValue = StringUtils.EMPTY;
		if (rule.getEntity() != null && Constants.PROCESS_VARIABLE.equals(rule.getEntity().trim())) {
			String processVariableName = rule.getField();
			lhsValue = getDatafromFlowFileAttributes(flowFileAttr, processVariableName, rule.getLhsPVAttribute());
			if (singleOperandOperatorList.contains(operator)) {
				evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
			} else if (Constants.BETWEEN_OPERATOR.equals(rule.getOperator())) {
				fromValue = rule.getFromValue();
				toValue = rule.getToValue();
				evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
			} else if (rule.getSelectedValue() != null) {
				if (Constants.PROCESS_VARIABLE.equals(rule.getSelectedValue().trim())) {
					rhsValue = getDatafromFlowFileAttributes(flowFileAttr, processVariableName,
							rule.getRhsPVAttribute());
					evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
				} else if (Constants.BE_ATTRIBUTE.equals(rule.getSelectedValue().trim())) {
					rhsValue = getLeafAttributeValueFromRootObject(dataObject, rule.getBeAttribute());
					evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
				} else if (Constants.CUSTOM_VALUE.equals(rule.getSelectedValue().trim())) {
					rhsValue = rule.getCustomValue();
					evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
				}
			}

		} else if (rule.getEntity() != null && Constants.BE_ATTRIBUTE.equals(rule.getEntity().trim())) {
			lhsValue = getLeafAttributeValueFromRootObject(dataObject, rule.getField());
			if (singleOperandOperatorList.contains(rule.getOperator())) {
				evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
			} else if (Constants.BETWEEN_OPERATOR.equals(rule.getOperator())) {
				fromValue = rule.getFromValue();
				toValue = rule.getToValue();
				evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
			} else if (rule.getSelectedValue() != null) {
				if (Constants.PROCESS_VARIABLE.equals(rule.getSelectedValue().trim())) {
					rhsValue = getDatafromFlowFileAttributes(flowFileAttr, rule.getProcessVariable(),
							rule.getRhsPVAttribute());
					evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
				} else if (Constants.BE_ATTRIBUTE.equals(rule.getSelectedValue().trim())) {
					rhsValue = getLeafAttributeValueFromRootObject(dataObject, rule.getBeAttribute());
					evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
				} else if (Constants.CUSTOM_VALUE.equals(rule.getSelectedValue().trim())) {
					rhsValue = rule.getCustomValue();
					evaluationResult = executeRule(lhsValue, rhsValue, operator, dataType, fromValue, toValue);
				}
			}
		}
		return evaluationResult;
	}
	
	private boolean executeRule(String lhsVal, String rhsVal, String operator, String type, String valueFrom,
			String valueTo) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
		Date lhsDate;
		Date rhsDate;
		Date rhsDate1;
		BigDecimal lhsNo;
		BigDecimal rhsNo;
		BigDecimal fromNo;
		BigDecimal toNo;

		switch (operator) {
		case "Equals":
			if (type.equalsIgnoreCase("Datetime")) {
				lhsDate = sdf.parse(lhsVal);
				rhsDate = sdf.parse(rhsVal);
				return lhsDate.equals(rhsDate);
			} else if (type.equalsIgnoreCase("Number")) {
				lhsNo = new BigDecimal(lhsVal);
				rhsNo = new BigDecimal(rhsVal);
				return lhsNo.compareTo(rhsNo) == 0;
			} else {
				return lhsVal.equals(rhsVal);
			}
		case "NotEquals":
			if (type.equalsIgnoreCase("Datetime")) {
				lhsDate = sdf.parse(lhsVal);
				rhsDate = sdf.parse(rhsVal);
				return !lhsVal.equals(rhsVal);
			} else if (type.equalsIgnoreCase("Number")) {
				lhsNo = new BigDecimal(lhsVal);
				rhsNo = new BigDecimal(rhsVal);
				return lhsNo.compareTo(rhsNo) != 0;
			} else {
				return !lhsVal.equals(rhsVal);
			}
		case "IsNull":
			return lhsVal == null;
		case "IsNotNull":
			return lhsVal != null;
		case "StartingWith":
			return lhsVal.startsWith(rhsVal);
		case "EndingWith":
			return lhsVal.endsWith(rhsVal);
		case "Containing":
			return lhsVal.contains(rhsVal);
		case "NotContains":
			return !lhsVal.contains(rhsVal);
		case "IsEqualsIgnoreCase":
			return lhsVal.equalsIgnoreCase(rhsVal);
		case "IsUpperCase":
			rhsVal = lhsVal.toUpperCase();
			return lhsVal.equals(rhsVal);
		case "IsLowerCase":
			rhsVal = lhsVal.toLowerCase();
			return lhsVal.equals(rhsVal);
		case "LengthEqualTo":
			return lhsVal.length() == Integer.valueOf(rhsVal);
		case "LengthLessThan":
			return lhsVal.length() < Integer.valueOf(rhsVal);
		case "LengthGreaterThan":
			return lhsVal.length() > Integer.valueOf(rhsVal);
		case "DataPattern":
			return lhsVal.matches(rhsVal);
		case "LessThan":
			lhsNo = new BigDecimal(lhsVal);
			rhsNo = new BigDecimal(rhsVal);
			return lhsNo.compareTo(rhsNo) < 0;
		case "GreaterThan":
			lhsNo = new BigDecimal(lhsVal);
			rhsNo = new BigDecimal(rhsVal);
			return lhsNo.compareTo(rhsNo) > 0;
		case "GreaterThanOrEquals":
			lhsNo = new BigDecimal(lhsVal);
			rhsNo = new BigDecimal(rhsVal);
			return lhsNo.compareTo(rhsNo) >= 0;
		case "LessThanOrEquals":
			lhsNo = new BigDecimal(lhsVal);
			rhsNo = new BigDecimal(rhsVal);
			return lhsNo.compareTo(rhsNo) <= 0;
		case "Before":
			lhsDate = sdf.parse(lhsVal);
			rhsDate = sdf.parse(rhsVal);
			return lhsDate.before(rhsDate);
		case "After":
			lhsDate = sdf.parse(lhsVal);
			rhsDate = sdf.parse(rhsVal);
			return lhsDate.after(rhsDate);
		case "BeforeDate":
			lhsDate = sdf.parse(lhsVal);
			rhsDate = sdf.parse(rhsVal);
			return lhsDate.before(rhsDate);
		case "AfterDate":
			lhsDate = sdf.parse(lhsVal);
			rhsDate = sdf.parse(rhsVal);
			return lhsDate.after(rhsDate);
		case "NotEqualsBeforeDate":
			lhsDate = sdf.parse(lhsVal);
			rhsDate = sdf.parse(rhsVal);
			return !lhsDate.before(rhsDate);
		case "Between":
			if (type.equalsIgnoreCase("Datetime")) {
				lhsDate = sdf.parse(lhsVal);
				rhsDate = sdf.parse(valueFrom);
				rhsDate1 = sdf.parse(valueTo);
				return lhsDate.after(rhsDate) && lhsDate.before(rhsDate1);
			} else if (type.equalsIgnoreCase("Number")) {
				lhsNo = new BigDecimal(lhsVal);
				fromNo = new BigDecimal(valueFrom);
				toNo = new BigDecimal(valueTo);
				return ((lhsNo.compareTo(fromNo) > 0) && (lhsNo.compareTo(toNo) < 0));
			}
			return false;
		case "isTrue":
			return Boolean.valueOf(lhsVal) == true;
		case "isFalse":
			return Boolean.valueOf(lhsVal) == false;
		default:
			return false;
		}
	}
	
	

}
