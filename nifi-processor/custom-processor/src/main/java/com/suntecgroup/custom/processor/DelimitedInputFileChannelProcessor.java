package com.suntecgroup.custom.processor;

/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
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
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.channelintegration.CIMapping;
import com.suntecgroup.custom.processor.model.channelintegration.Content;
import com.suntecgroup.custom.processor.model.channelintegration.ConversionArray;
import com.suntecgroup.custom.processor.model.channelintegration.Delimited;
import com.suntecgroup.custom.processor.model.channelintegration.DelimitedAttributes;
import com.suntecgroup.custom.processor.model.channelintegration.FileNameDetails;
import com.suntecgroup.custom.processor.model.channelintegration.Footer;
import com.suntecgroup.custom.processor.model.channelintegration.FormatAttributes;
import com.suntecgroup.custom.processor.model.channelintegration.Header;
import com.suntecgroup.custom.processor.model.channelintegration.RuleAttributes;
import com.suntecgroup.custom.processor.model.channelintegration.SessionDetails;
import com.suntecgroup.custom.processor.model.channelintegration.Validation;
import com.suntecgroup.custom.processor.model.channelintegration.ValidationAttributes;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;
import com.suntecgroup.custom.processor.utils.CommonUtils;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.MappingUtils;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/*
 * This class is for creating a custom NiFi processor to handle the Delimiter Input File Channel Integration.
 * This class would read the incoming delimiter ASCII file and convert it to flow file object.
 * 
 * @version 1.0 - March 2019
 * @author Mohammed Rizwan & Ramesh Kumar B
 */

@SideEffectFree
@Tags({ "Delimited, Channel Integration" })
@CapabilityDescription("Delimited File Input Processor for Channel Integration")
public class DelimitedInputFileChannelProcessor extends AbstractProcessor {
	RestTemplate restTemplate = new RestTemplate();
	private ComponentLog logger;
	// private String channelOutputBEType;
	List<String> failedResult = null;
	private final AtomicReference<OkHttpClient> okHttpClientAtomicReference = new AtomicReference<>();
	private ObjectMapper mapper = null;
	private Gson gson = null;
	private Type mapType = null;
	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;

	public static final PropertyDescriptor PROCESS_VARIABLE = new PropertyDescriptor.Builder().name("Process Variables")
			.description("Set Process variables").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(false).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(false).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_BE_NAME = new PropertyDescriptor.Builder().name("Output BE Name")
			.description("Output BE Name").required(false).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor REMOTE_URL = new PropertyDescriptor.Builder().name("Remote URL")
			.description("Remote URL").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_BE_BUK_ATTRIBUTES = new PropertyDescriptor.Builder()
			.name("Output BE BUK Attributes").description("Output BE BUK Attribute").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor ISBATCHABLE = new PropertyDescriptor.Builder().name("Batchable")
			.description("Is batchable available").required(false).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor BATCH_SIZE = new PropertyDescriptor.Builder().name("Batch Size")
			.description("Batch Size").required(false).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor HEADER = new PropertyDescriptor.Builder().name("Header")
			.description("Channel Integration Header").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor FOOTER = new PropertyDescriptor.Builder().name("Footer")
			.description("Channel Integration footer").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor PV_MAPPING = new PropertyDescriptor.Builder().name("PV Mapping")
			.description("Set Process variables Mappinps").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CONTENT = new PropertyDescriptor.Builder().name("Content")
			.description("Channel Integration content").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CI_NAME = new PropertyDescriptor.Builder().name("CI Name")
			.description("Channel Integration Name").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor MAPPING = new PropertyDescriptor.Builder().name("Mapping")
			.description("Channel Integration Mapping").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor FILE_REJECT = new PropertyDescriptor.Builder().name("File Reject Path")
			.description("Channel Integration Record file path").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor ADDRESSED = new PropertyDescriptor.Builder().name("Addressed Path")
			.description("Current addressed").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor RECORD_REJECT = new PropertyDescriptor.Builder().name("Record Reject Path")
			.description("Channel Integration Record Reject path").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor VALIDATION = new PropertyDescriptor.Builder().name("Validation")
			.description("Channel Integration Validation").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUTBE_DEFINITION = new PropertyDescriptor.Builder()
			.name("OUTPUTBE_DEFINITION").description("Connector Processor OUTPUTBE_DEFINATION").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description("Success relationship").build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();
	
	public static final PropertyDescriptor IDLE_CONNECTION_MAXPOOL_SIZE = new PropertyDescriptor.Builder().name("idleConnectionMaxPoolSize")
			.description("Http connection pool size").required(true)
			.expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();
	
	public static final PropertyDescriptor IDLE_CONNECTION_ALIVE_DURATION = new PropertyDescriptor.Builder().name("idleConnectionAliveDuration")
			.description("Http connection alive duration").required(true)
			.expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	@Override
	public void init(final ProcessorInitializationContext context) {

		List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(PROCESS_VARIABLE);
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		properties.add(OUTPUT_BE_NAME);
		properties.add(REMOTE_URL);
		properties.add(OUTPUT_BE_BUK_ATTRIBUTES);
		properties.add(ISBATCHABLE);
		properties.add(BATCH_SIZE);
		properties.add(HEADER);
		properties.add(FOOTER);
		properties.add(PV_MAPPING);
		properties.add(CONTENT);
		properties.add(CI_NAME);
		properties.add(MAPPING);
		properties.add(FILE_REJECT);
		properties.add(ADDRESSED);
		properties.add(RECORD_REJECT);
		properties.add(VALIDATION);
		properties.add(OUTPUTBE_DEFINITION);
		properties.add(IDLE_CONNECTION_MAXPOOL_SIZE);
		properties.add(IDLE_CONNECTION_ALIVE_DURATION);

		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		this.relationships = Collections.unmodifiableSet(relationships);
		logger = context.getLogger();
		allIgnoreTypes.add("Root");
		allIgnoreTypes.add("SubRoot");
		allIgnoreTypes.add("BeRoot");
		allIgnoreTypes.add("BeSubRoot");
		allIgnoreTypes.add("PvRoot");
		allIgnoreTypes.add("PvSubRoot");
		mapper = new ObjectMapper();
		gson = new GsonBuilder().create();
		mapType = new TypeToken<Map<String, Object>>() {}.getType();
	};

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	final List<String> allIgnoreTypes = new ArrayList<String>();
	private String transactionStatusUrl = null;
	private String remoteURL = null;
	@OnScheduled
	public void onScheduled(final ProcessContext processContext) {
		okHttpClientAtomicReference.set(null);
		long connectionAliveDuration = processContext.getProperty(IDLE_CONNECTION_ALIVE_DURATION).asLong();
		int maxPoolSize = processContext.getProperty(IDLE_CONNECTION_MAXPOOL_SIZE).asInteger();
		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder();
		okHttpClientBuilder.connectionPool(new ConnectionPool(maxPoolSize, connectionAliveDuration,   TimeUnit.SECONDS));
		okHttpClientBuilder.retryOnConnectionFailure(false);
		okHttpClientAtomicReference.set(okHttpClientBuilder.build());
		remoteURL = processContext.getProperty(REMOTE_URL).evaluateAttributeExpressions().getValue();
		transactionStatusUrl = remoteURL + "/bpruntime/sessionmanager/updateFilesRecStatus";
		mapper = new ObjectMapper();
	}
	@Override
	public void onTrigger(final ProcessContext processContext, final ProcessSession processSession)
			throws ProcessException {

		FlowFile flowFileObj = processSession.get();
		if (flowFileObj == null) {
			return;
		}

		String transactionId = generateTransactionId();
		processSession.putAttribute(flowFileObj, "transactionId", transactionId);

		boolean isBatchSuccess = false;
		boolean isNoBatchSuccess = false;
		String isHeaderValid = null;
		String isFooterValid = null;
		String exceptionMessage = null;
		String isValidContent = "success";

		int receivedFilesCount = 1;
		int acceptedFilesCount = 0;
		int rejectedFilesCount = 0;
		int totalRecordsCount = 0;
		int acceptedRecordsCount = 0;
		int rejectedRecordsCount = 0;

		String fileName = flowFileObj.getAttribute("filename");

		List<Map<String, Object>> resultMappedList = null;

		FlowFile flowFileOutput = processSession.clone(flowFileObj);
		List<String> addressRecordLst = new ArrayList<String>();
		List<String> recordRejectLst = new ArrayList<String>();
		List<Map<String, Object>> contentResultList = new ArrayList<Map<String, Object>>();

		final String processVarJsonStr = processContext.getProperty(PROCESS_VARIABLE).evaluateAttributeExpressions()
				.getValue();

		String beName = processContext.getProperty(OUTPUT_BE_NAME).evaluateAttributeExpressions().getValue();
		String batchAble = processContext.getProperty(ISBATCHABLE).evaluateAttributeExpressions().getValue();
		String batchSize = processContext.getProperty(BATCH_SIZE).evaluateAttributeExpressions().getValue();
		String remoteURL = processContext.getProperty(REMOTE_URL).evaluateAttributeExpressions().getValue();
		String headerProperty = processContext.getProperty(HEADER).evaluateAttributeExpressions().getValue();
		String footerProperty = processContext.getProperty(FOOTER).evaluateAttributeExpressions().getValue();
		String pvMappingProperty = processContext.getProperty(PV_MAPPING).evaluateAttributeExpressions().getValue();
		String contentProperty = processContext.getProperty(CONTENT).evaluateAttributeExpressions().getValue();

		String mappingProperty = processContext.getProperty(MAPPING).evaluateAttributeExpressions().getValue();
		String ciNameProperty = processContext.getProperty(CI_NAME).evaluateAttributeExpressions().getValue();

		String addressedPath = processContext.getProperty(ADDRESSED).evaluateAttributeExpressions().getValue();
		String fileRejectPath = processContext.getProperty(FILE_REJECT).evaluateAttributeExpressions().getValue();
		String recordRejectPath = processContext.getProperty(RECORD_REJECT).evaluateAttributeExpressions().getValue();

		String validation = processContext.getProperty(VALIDATION).evaluateAttributeExpressions().getValue();
		String outputBEDefinition = processContext.getProperty(OUTPUTBE_DEFINITION).evaluateAttributeExpressions()
				.getValue();

		try {

			List<ProcessVariable> processVariables = new ArrayList<ProcessVariable>();

			Map<String, Object> processMapBP = new LinkedHashMap<>();
			Map<String, Object> processMap = new LinkedHashMap<>();
			Map<String, Object> pvRunTime = new LinkedHashMap<>();

			CommonUtils.validateSessionId(processContext, processSession, null, SESSION_ID, logger);
			CommonUtils.validateRunNumber(processContext, processSession, null, RUN_NUMBER, logger);

			String sessionId = processContext.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
			String runNumber = processContext.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();

			Map<String, Object> outputStructureMap = null;
			try {
				outputStructureMap = mapper.readValue(outputBEDefinition, Map.class);
			} catch (Exception e) {
				throw new NifiCustomException("Error occurred while reading outputbe property: " + e.getMessage());

			}
			// resultOutputMap contains Output Definition with Attribute
			// name and value as its structure.
			Map<String, Object> resultOutputMap = new LinkedHashMap<String, Object>();

			if (outputStructureMap != null && outputStructureMap.size() > 0) {

				resultOutputMap = fetchCompositeOutputStructure(outputStructureMap, resultOutputMap);

			}

			Header header = null;

			try {
				header = mapper.readValue(headerProperty, Header.class);

			} catch (Exception e) {
				throw new NifiCustomException(
						ciNameProperty + " :Error occurred while reading Header property: " + e.getMessage());
			}

			Footer footer = null;

			try {
				footer = mapper.readValue(footerProperty, Footer.class);

			} catch (Exception e) {
				throw new NifiCustomException(
						ciNameProperty + " :Error occurred while reading Footer property: " + e.getMessage());
			}

			List<CIMapping> pvMappingList = null;
			try {
				pvMappingList = Arrays.asList(mapper.readValue(pvMappingProperty, CIMapping[].class));

			} catch (Exception e) {
				throw new NifiCustomException("Error occurred while reading mapping property: " + e.getMessage());
			}

			Content content = null;

			try {
				content = mapper.readValue(contentProperty, Content.class);

			} catch (Exception e) {
				throw new NifiCustomException(
						ciNameProperty + ":Error occurred while reading Content property: " + e.getMessage());
			}

			List<CIMapping> mappingList = null;
			try {
				mappingList = Arrays.asList(mapper.readValue(mappingProperty, CIMapping[].class));

			} catch (Exception e) {
				throw new NifiCustomException("Error occurred while reading mapping property: " + e.getMessage());
			}

			Validation validationObj = null;
			try {
				validationObj = mapper.readValue(validation, Validation.class);

			} catch (Exception e) {
				throw new NifiCustomException("Error occurred while reading Validation property: " + e.getMessage());
			}

			// File Name Validation
			String fileNameValidate = validateFileName(sessionId, runNumber, fileName, validationObj, remoteURL);

			InputStream fileInputStream = processSession.read(flowFileObj);
			List<String> inputFileData = fetchInputFileData(flowFileObj, fileInputStream, fileName);

			if (fileNameValidate.equalsIgnoreCase("Success")) {

				int fileHeaderLines = 0;
				int fileFooterLines = 0;

				List<String> headerInputFileData = new ArrayList<String>();
				List<String> footerInputFileData = new ArrayList<String>();
				List<Map<String, Object>> headerResultList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> footerResultList = new ArrayList<Map<String, Object>>();
				Map<String, String> processMapFFattr = new HashMap<>();

				try {

					if (header != null && header.isHasHeader()) {
						fileHeaderLines = header.getHeaderLines();

						int size = fileHeaderLines - 1;
						for (int i = 0; i <= size; i++) {
							headerInputFileData.add(inputFileData.get(i));
						}
						// To read the attribute name and get data from header
						// lines in ascii txt file
						// and create process variable.
						headerResultList = processHeader(headerInputFileData, header, fileHeaderLines);

					}

					if (footer != null && footer.isHasFooter()) {
						fileFooterLines = footer.getFooterLines();

						int from = inputFileData.size() - fileFooterLines;
						int to = inputFileData.size();

						for (int i = from; i < to; i++) {
							footerInputFileData.add(inputFileData.get(i));
						}
						footerResultList = processFooter(footerInputFileData, footer, fileFooterLines);

					}

					contentResultList = performDelimitedAction(inputFileData, fileHeaderLines, fileFooterLines, content,
							ciNameProperty, addressRecordLst, recordRejectLst);

					// Take headerResultList & contentResultList and validate
					// for MAX, MIN, COUNT,
					// AVG, SUM

					if (addressRecordLst != null && addressRecordLst.size() > 0) {

						isHeaderValid = validateHeaderFooterContent(headerResultList, contentResultList, validationObj,
								"header");

						isFooterValid = validateHeaderFooterContent(footerResultList, contentResultList, validationObj,
								"footer");
					} else {

						rejectedFilesCount = 1;
						totalRecordsCount = inputFileData.size() - (header.getHeaderLines() + footer.getFooterLines());
						rejectedRecordsCount = totalRecordsCount;
						// No Addressed Records found in file
						updateOperatorStatistics(sessionId, runNumber, ciNameProperty, fileName, receivedFilesCount,
								acceptedFilesCount, rejectedFilesCount, totalRecordsCount, acceptedRecordsCount,
								rejectedRecordsCount);

						// This will write file reject, record reject since all
						// the records is failed.
						// keep isHeaderValid and isFooterValid as null
						isValidContent = "Skipped. No content processed due to entire content records being a recordreject. Skipping Header & Footer validation check";
						inputFileData.add(isValidContent);
						writeToFile(inputFileData, fileName, fileRejectPath);

						if (recordRejectLst != null && recordRejectLst.size() > 0) {
							writeToFile(recordRejectLst, fileName, recordRejectPath);
						}
					}

				} catch (Exception e) {
					isHeaderValid = "Failed";
					isFooterValid = "Failed";
					exceptionMessage = e.getMessage();

				}

				if (isHeaderValid != null && isFooterValid != null) {

					if (isHeaderValid.trim().equalsIgnoreCase("success")
							&& isFooterValid.trim().equalsIgnoreCase("success")) {

						acceptedFilesCount = 1;
						totalRecordsCount = inputFileData.size() - (fileHeaderLines + fileFooterLines);
						acceptedRecordsCount = addressRecordLst.size();
						rejectedRecordsCount = recordRejectLst.size();
						// File accepted
						updateOperatorStatistics(sessionId, runNumber, ciNameProperty, fileName, receivedFilesCount,
								acceptedFilesCount, rejectedFilesCount, totalRecordsCount, acceptedRecordsCount,
								rejectedRecordsCount);

						// to update process varible value with header and
						// footer values
						try {

							processVariables = CommonUtils.convertJsonStringToJava(processVarJsonStr, logger);

							if (processVariables != null && processVariables.size() > 0) {
								processMapBP = fetchProcessVariablesDetails(processVariables);
								processMap = updatePV(processMapBP, pvRunTime, headerResultList, footerResultList,
										pvMappingList);
							}
						} catch (Exception e) {
							throw new NifiCustomException(ciNameProperty
									+ " : Error occurred while reading Process Variable property: " + e.getMessage());
						}
						if (addressRecordLst != null && addressRecordLst.size() == 0) {
							writeToFile(inputFileData, fileName, fileRejectPath);
						}

						else {
							if (addressRecordLst != null && addressRecordLst.size() > 0) {
								writeToFile(addressRecordLst, fileName, addressedPath);
							}

							if (recordRejectLst != null && recordRejectLst.size() > 0) {
								writeToFile(recordRejectLst, fileName, recordRejectPath);
							}
						}

						if (mappingList != null && contentResultList != null && contentResultList.size() > 0) {
							// map inputdata value to output be
							try {
								resultMappedList = mappingDataToOuput(contentResultList, mappingList, resultOutputMap,
										processMap, ciNameProperty);
							} catch (Exception e) {
								throw new NifiCustomException(
										ciNameProperty + " : Error occurred while mapping data: " + e.getMessage());
							}
						}

						// Write Output and Create FlowfileOutput
						if (resultMappedList != null && resultMappedList.size() > 0) {

							if (batchAble != null && batchAble.trim().equalsIgnoreCase("true")) {

								int batchSizeNumber = (batchSize != null && batchSize.trim().length() > 0)
										? Integer.valueOf(batchSize) : 0;

								List<List<Map<String, Object>>> batch = Lists.partition(resultMappedList,
										batchSizeNumber);
								FlowFile flowFileBatchObject = null;
								for (List<Map<String, Object>> currentObj : batch) {

									try {
										flowFileBatchObject = processSession.clone(flowFileObj);
										String transactionId_ = generateTransactionId();

										isBatchSuccess = writeFlowFileContent(processSession, currentObj,
												flowFileBatchObject, ciNameProperty);

										processSession.putAttribute(flowFileBatchObject, "transactionId", transactionId_);
										processSession.putAttribute(flowFileBatchObject, "beName", beName);
										processSession.putAttribute(flowFileBatchObject, "channelId", ciNameProperty);
										processSession.putAttribute(flowFileBatchObject, "channelName", ciNameProperty);
										for (Map.Entry<String, Object> processMapObj : pvRunTime.entrySet()) {
											processSession.putAttribute(flowFileBatchObject, processMapObj.getKey(),
													gson.toJson(processMapObj.getValue()));
										}
										processSession.transfer(flowFileBatchObject, REL_SUCCESS);

									} catch (NifiCustomException nifiCustomException) {
										logger.error("Error occurred at Batch FileChannelInput :: "
												+ nifiCustomException.getMessage(), nifiCustomException);

										processSession.remove(flowFileBatchObject);
										processSession.remove(flowFileOutput);

										processSession.transfer(flowFileObj, REL_FAILURE);
										processSession.commit();
										return;
									} catch (Exception e) {

										logger.error("Error occurred at Batch FileChannelInput :: " + e.getMessage(),
												e);
										processSession.remove(flowFileBatchObject);
										processSession.remove(flowFileOutput);
										processSession.transfer(flowFileObj, REL_FAILURE);
										processSession.commit();
										return;

									}

								}

							}

							else {
								isNoBatchSuccess = writeFlowFileContent(processSession, resultMappedList,
										flowFileOutput, ciNameProperty);
								processSession.putAttribute(flowFileOutput, "beName", beName);
								processSession.putAttribute(flowFileOutput, "channelId", ciNameProperty);
								processSession.putAttribute(flowFileOutput, "channelName", ciNameProperty);
								for (Map.Entry<String, Object> processMapObj : pvRunTime.entrySet()) {
									processSession.putAttribute(flowFileOutput, processMapObj.getKey(),
											gson.toJson(processMapObj.getValue()));
								}

							}

						}

						else {
							throw new NifiCustomException(
									ciNameProperty + " failed due no mapped content record to process ");
						}

					}

					else {

						rejectedFilesCount = 1;
						totalRecordsCount = inputFileData.size() - (fileHeaderLines + fileFooterLines);
						rejectedRecordsCount = totalRecordsCount;
						// File rejected HF validation
						updateOperatorStatistics(sessionId, runNumber, ciNameProperty, fileName, receivedFilesCount,
								acceptedFilesCount, rejectedFilesCount, totalRecordsCount, acceptedRecordsCount,
								rejectedRecordsCount);

						if (exceptionMessage != null) {
							inputFileData.add(exceptionMessage);
							writeToFile(inputFileData, fileName, fileRejectPath);
							throw new NifiCustomException(
									ciNameProperty + " failed due to exception: " + exceptionMessage);

						} else {

							// File Reject
							inputFileData.add("Header information: " + isHeaderValid);
							inputFileData.add("Footer information: " + isFooterValid);
							writeToFile(inputFileData, fileName, fileRejectPath);
							throw new NifiCustomException(ciNameProperty + " failed due to header and footer failed");

						}

					}

				}
			}

			else {

				rejectedFilesCount = 1;
				totalRecordsCount = inputFileData.size() - (header.getHeaderLines() + footer.getFooterLines());
				rejectedRecordsCount = totalRecordsCount;
				// File rejected FileName validation
				updateOperatorStatistics(sessionId, runNumber, ciNameProperty, fileName, receivedFilesCount,
						acceptedFilesCount, rejectedFilesCount, totalRecordsCount, acceptedRecordsCount,
						rejectedRecordsCount);

				// File Reject
				inputFileData.add(fileNameValidate);
				writeToFile(inputFileData, fileName, fileRejectPath);
				throw new NifiCustomException(ciNameProperty + " failed due to File Reject ");
			}

		} catch (NifiCustomException nifiCustomException) {
			logger.error("Error occurred at FileChannelInput :: " + nifiCustomException.getMessage(),
					nifiCustomException);
			processSession.remove(flowFileOutput);
			processSession.putAttribute(flowFileObj, Constants.ERRORMESSAGE, nifiCustomException.getMessage());
			processSession.transfer(flowFileObj, REL_FAILURE);
			processSession.commit();
			return;
		} catch (Exception e) {
			logger.error("Error occurred at FileChannelInput :: " + e.getMessage(), e);
			processSession.remove(flowFileOutput);
			processSession.putAttribute(flowFileObj, Constants.ERRORMESSAGE, e.getMessage());
			processSession.transfer(flowFileObj, REL_FAILURE);
			processSession.commit();
			return;

		}

		if (isBatchSuccess) {
			// For batch success
			processSession.remove(flowFileObj);
			processSession.remove(flowFileOutput);
			processSession.commit();

		}

		else if (isNoBatchSuccess) {
			// For Complete flow success
			processSession.remove(flowFileObj);
			processSession.transfer(flowFileOutput, REL_SUCCESS);
			processSession.commit();
		} else {
			processSession.remove(flowFileOutput);
			processSession.transfer(flowFileObj, REL_FAILURE);
			processSession.commit();
		}

	}

	private List<Map<String, Object>> mappingDataToOuput(List<Map<String, Object>> contentResultList,
			List<CIMapping> mappingList, Map<String, Object> resultOutputMap, Map<String, Object> processMap,
			String ciNameProperty) throws Exception {
		List<Map<String, Object>> resultOutBeList = new ArrayList<>();
		List<Map<String, Object>> resultOutDenormLst = new ArrayList<>();
		Map<String, Object> outputBEMappedObj = new HashMap<>();
		// Root level denormalization
		List<String> rootDenorMainArrayLst = mappingList.stream()
				.filter(t -> t.getToCurrentNode().equalsIgnoreCase("") && t.getDataType().equalsIgnoreCase("Array")
						&& "root".equalsIgnoreCase(t.getFromPath().split("-")[2]))
				.map(CIMapping::getFromValue).collect(Collectors.toList());
		// Sub level denormalization
		List<String> subMainArrayLst = mappingList.stream().filter(
				t -> t.getToDataType().equalsIgnoreCase("Array") )
				.map(CIMapping::getToValue).collect(Collectors.toList());
		Set<String> subDenorMainArrayLst = subMainArrayLst.stream()
				.filter(n -> subMainArrayLst.stream().filter(x -> x.equalsIgnoreCase(n)).count() > 1)
				.collect(Collectors.toSet());
		// got denorm list

		if (rootDenorMainArrayLst.size() >= 1) {
			try {
				Map<String, List<CIMapping>> denormMap = MappingUtils.fetch(rootDenorMainArrayLst, mappingList);
				List<CIMapping> priAttrList = mappingList.stream()
						.filter(t -> t.getFromPath().split("-").length == 2 || t.getFromPath().split("-").length == 3)
						.filter(t -> !t.getDataType().equalsIgnoreCase("array")
								&& !t.getDataType().equalsIgnoreCase("object"))
						.collect(Collectors.toList());
				for (Map<String, Object> inputBERecord : contentResultList) {
					resultOutDenormLst = MappingUtils.inisiateRootDenorm(inputBERecord, mappingList, processMap,
							rootDenorMainArrayLst, priAttrList, denormMap);
					resultOutBeList.addAll(resultOutDenormLst);
				}
			} catch (Exception e) {
				logger.error("Error occurred at mappingDataToOuput Root Denormalization :: ", e);
				throw new Exception(e.getMessage());
			}
		} else {
			try {
				for (Map<String, Object> inputBERecord : contentResultList) {
					outputBEMappedObj = initiateCompositeMethod(inputBERecord, mappingList, resultOutputMap,
							ciNameProperty, processMap, subDenorMainArrayLst);
					resultOutBeList.add(outputBEMappedObj);
				}
			} catch (Exception e) {
				logger.error("Error occurred at mappingDataToOuput :: ", e);
			}
		}
		return resultOutBeList;
	}

	private Map<String, Object> initiateCompositeMethod(Map<String, Object> inputBERecord, List<CIMapping> mappingList,
			Map<String, Object> resultOutputMap, String connectorName, Map<String, Object> flowFileAttributes,
			Set<String> subDenorMainArrayLst) throws NifiCustomException {
		Object result = null;
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
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
						flowFileAttributes, null,subDenorMainArrayLst);
				resultMap.put(toCurrent, result);

			}

			// In ROOT, if we have array
			else if (toValueKey != null && toValueKey.trim().contains(",array")) {

				String[] beNameObj = toValueKey.split(",array");
				List<Map<String, Object>> arrResultList=new ArrayList<>();
				String toCurrent = beNameObj[0];
				String toParent = "BeRoot";

				if (subDenorMainArrayLst.contains(toCurrent)) {
					// array belong to denorm
					try{
					arrResultList = MappingUtils.getSubArayDenorm(inputBERecord, mappingList,
							toCurrent);
					}catch(Exception e){
						throw new NifiCustomException(e.getMessage());
					}
					resultMap.put(toCurrent, arrResultList);
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
				boolean required = (boolean) objectMap.get("required");
				String toPathValue = toValueKey + "-" + "BeRoot" + "-";
				try {
					result = fetchValueFromInputMap(inputBERecord, mappingList, toPathValue, null, flowFileAttributes,
							null);
				} catch (Exception e) {
					throw new NifiCustomException(e.getMessage());
				}
				if (required) {
					if (result != null) {
						resultMap.put(toValueKey, result);
					} else {
						throw new NifiCustomException(connectorName + " - failed due to mandatory OutputBE attribute "
								+ toValueKey + " has null or empty value while processing input record ");
					}
				} else {
					resultMap.put(toValueKey, result);
				}
			}
		}

		return resultMap;
	}

	private String validateHeaderFooterContent(List<Map<String, Object>> resultList,
			List<Map<String, Object>> contentResultList, Validation validationObj, String source)
			throws NifiCustomException {

		String result = "success";
		List<ValidationAttributes> validationList = null;

		if (source != null && source.equalsIgnoreCase("header")) {
			validationList = validationObj.getHeaderValidation();
			result = beginValidationForHeaderFooterWithContent(validationList, resultList, contentResultList, source);
			return result;

		}

		if (source != null && source.equalsIgnoreCase("footer")) {
			validationList = validationObj.getFooterValidation();
			result = beginValidationForHeaderFooterWithContent(validationList, resultList, contentResultList, source);
			return result;

		}

		return result;

	}

	private String beginValidationForHeaderFooterWithContent(List<ValidationAttributes> validationList,
			List<Map<String, Object>> resultList, List<Map<String, Object>> contentResultList, String source)
			throws NifiCustomException {
		String result = "success";

		for (ValidationAttributes vaObj : validationList) {

			String fieldName = null;

			if (source != null && source.equalsIgnoreCase("header")) {
				fieldName = vaObj.getHeaderField();

			} else if (source != null && source.equalsIgnoreCase("footer")) {
				fieldName = vaObj.getFooterField();

			}

			String contentField = vaObj.getContentField();
			String function = vaObj.getCalculation();

			String evaluateCriteria = vaObj.getEvaluate();
			BigDecimal headerFooterValue = BigDecimal.ZERO;

			headerFooterValue = fetchHeaderFooterFieldValue(fieldName, resultList, source, headerFooterValue);

			if (headerFooterValue == null) {
				result = source + " validation Failed: Due to " + source
						+ " value is either null or not available for attribute " + fieldName;
				return result;
			}

			BigDecimal currentValue = BigDecimal.ZERO;
			BigDecimal sumContentValue = BigDecimal.ZERO;
			List<BigDecimal> contentValues = new ArrayList<BigDecimal>();

			int totalrecordCount = 0;

			for (Map<String, Object> contentObj : contentResultList) {

				currentValue = fetchContentTotalValue(contentObj, contentField);
				if (currentValue != null && currentValue.toString().trim().length() > 0) {
					totalrecordCount++;
					sumContentValue = sumContentValue.add(currentValue);
					contentValues.add(currentValue);
				} else {
					result = source
							+ " validation Failed: Due to content value is either not available or its null for attribute "
							+ contentField;
					return result;
				}

			}

			if (function != null) {

				result = performMathematicalCalculation(totalrecordCount, sumContentValue, contentValues, fieldName,
						headerFooterValue, contentField, evaluateCriteria, source, function);
				if (result.toLowerCase().contains("failed")) {
					return result;
				}

			} else {
				result = "Failed: Due to " + source + " calculation name " + function + " given under attribute "
						+ fieldName + " is not valid!";
				return result;
			}

		}

		return result;
	}

	public Map<String, Object> updatePV(Map<String, Object> processMapBP, Map<String, Object> pvRunTime,
			List<Map<String, Object>> headerResultList, List<Map<String, Object>> footerResultList,
			List<CIMapping> pvMappingList) throws NifiCustomException {

		for (CIMapping pvMappingValueType : pvMappingList) {

			if (null != pvMappingValueType.getToCurrentNode()
					&& pvMappingValueType.getToCurrentNode().equalsIgnoreCase("BeRoot")) {

				if (pvMappingValueType.getAttributeType().equalsIgnoreCase("FA")) {
					if (null != pvMappingValueType.getDataType()
							&& !pvMappingValueType.getDataType().equalsIgnoreCase("object")) {
						String pvName = pvMappingValueType.getToValue();
						String footerAttrName = pvMappingValueType.getFromValue();
						String footerAttrValue = null;
						String[] fromPath = pvMappingValueType.getFromPath().split("-");
						footerAttrValue = CommonUtils.fetchHFFieldValuePrimitive(footerAttrName, fromPath, footerResultList,
								"footer");
						pvRunTime.put(pvName, footerAttrValue);
					} else if (null != pvMappingValueType.getDataType()
							&& pvMappingValueType.getDataType().equalsIgnoreCase("object")) {
						String pvNameObj = pvMappingValueType.getToValue();
						Map<String, Object> footerAttrValueObj = new HashMap<String, Object>();
						// Pv Type BE Object
						footerAttrValueObj = CommonUtils.fetchFeildValueFromRootObject(pvNameObj, footerResultList, pvMappingList,
								"footer");
						pvRunTime.put(pvNameObj, footerAttrValueObj);
					} else if (null != pvMappingValueType.getDataType()
							&& pvMappingValueType.getDataType().equalsIgnoreCase("array")) {
						String pvNameArray = pvMappingValueType.getToValue();
						String attrNameArray = pvMappingValueType.getFromValue();
						List<Map<String, Object>> footerAttrValueLstObj = new ArrayList<>();
						// Pv Type BE Array
						footerAttrValueLstObj = CommonUtils.fetchFeildValueFromRootArray(pvNameArray, attrNameArray,
								footerResultList, pvMappingList, "footer");
						pvRunTime.put(pvNameArray, footerAttrValueLstObj);
					}

				} else if (pvMappingValueType.getAttributeType().equalsIgnoreCase("HA")) {
					if (null != pvMappingValueType.getDataType()
							&& !pvMappingValueType.getDataType().equalsIgnoreCase("object")
							&& !pvMappingValueType.getDataType().equalsIgnoreCase("array")) {
						String pvName = pvMappingValueType.getToValue();
						String headerAttrName = pvMappingValueType.getFromValue();
						String headerAttrValue = null;
						String[] fromPath = pvMappingValueType.getFromPath().split("-");
						headerAttrValue = CommonUtils.fetchHFFieldValuePrimitive(headerAttrName, fromPath, headerResultList,
								"header");
						pvRunTime.put(pvName, headerAttrValue);

					} else if (null != pvMappingValueType.getDataType()
							&& pvMappingValueType.getDataType().equalsIgnoreCase("object")) {
						String pvNameObj = pvMappingValueType.getToValue();
						Map<String, Object> headerAttrValueObj = new HashMap<String, Object>();
						// Pv Type BE
						headerAttrValueObj = CommonUtils.fetchFeildValueFromRootObject(pvNameObj, headerResultList, pvMappingList,
								"header");

						pvRunTime.put(pvNameObj, headerAttrValueObj);

					} else if (null != pvMappingValueType.getDataType()
							&& pvMappingValueType.getDataType().equalsIgnoreCase("array")) {
						String pvNameArray = pvMappingValueType.getToValue();
						String attrNameArray = pvMappingValueType.getFromValue();
						List<Map<String, Object>> heaaderAttrValueLstObj = new ArrayList<>();
						// Pv Type BE Array
						heaaderAttrValueLstObj = CommonUtils.fetchFeildValueFromRootArray(pvNameArray, attrNameArray,
								headerResultList, pvMappingList, "header");
						pvRunTime.put(pvNameArray, heaaderAttrValueLstObj);
					}

				}

			}

		}
		for (Map.Entry<String, Object> entry : pvRunTime.entrySet()) {
			processMapBP.put(entry.getKey(), entry.getValue());

		}
		return processMapBP;
	}

	private BigDecimal fetchHeaderFooterFieldValue(String fieldName, List<Map<String, Object>> resultList,
			String source, BigDecimal resultNumber) throws NifiCustomException {

		try {

			for (Map<String, Object> currentData : resultList) {
				Iterator<Entry<String, Object>> mapIterator = currentData.entrySet().iterator();
				while (mapIterator.hasNext()) {
					Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();

					if (!(ev.getValue() instanceof List) && !(ev.getValue() instanceof Map)) {
						// if the object is primitive
						if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(fieldName)) {

							String temp = (String) ev.getValue();
							if (temp != null) {
								resultNumber = resultNumber.add(new BigDecimal(temp));
								return resultNumber;
							}
						}
					} else if (ev.getValue() instanceof List) {
						List<Map<String, Object>> tempList = (List) ev.getValue();
						resultNumber = fetchHeaderFooterFieldValue(fieldName, tempList, source, resultNumber);
					} else if (ev.getValue() instanceof Map) {
						Map<String, Object> tempSubObj = (Map) ev.getValue();
						resultNumber = fetchHeaderFooterFieldValueFromObject(fieldName, tempSubObj, source,
								resultNumber);

					}
				}
			}

			if (resultNumber != null && resultNumber.toString().trim().equals("0")) {
				resultNumber = null;
			}
		}

		catch (Exception ex) {
			throw new NifiCustomException("Error while fetching attribute " + fieldName + " value from " + source
					+ " due to required number value is null or empty");
		}

		return resultNumber;
	}

	public BigDecimal fetchHeaderFooterFieldValueFromObject(String fieldName, Map<String, Object> tempObj,
			String source, BigDecimal resultNumber) throws NifiCustomException {
		try {
			Iterator<Entry<String, Object>> mapIterator = tempObj.entrySet().iterator();
			while (mapIterator.hasNext()) {
				Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();
				if (!(ev.getValue() instanceof List) && !(ev.getValue() instanceof Map)) {
					// if the object is primitive
					if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(fieldName)) {

						String temp = (String) ev.getValue();
						if (temp != null) {
							resultNumber = resultNumber.add(new BigDecimal(temp));
							return resultNumber;
						}
					}

				} else if (ev.getValue() instanceof List) {
					List<Map<String, Object>> tempList = (List) ev.getValue();
					resultNumber = fetchHeaderFooterFieldValue(fieldName, tempList, source, resultNumber);
				} else if (ev.getValue() instanceof Map) {
					Map<String, Object> tempSubObj = (Map) ev.getValue();
					resultNumber = fetchHeaderFooterFieldValueFromObject(fieldName, tempSubObj, source, resultNumber);

				}

			}
		} catch (Exception e) {
			throw new NifiCustomException("Error while fetching attribute " + fieldName + " value from " + source
					+ " due to required number value is null or empty");
		}
		return resultNumber;
	}

	private String performMathematicalCalculation(int totalrecordCount, BigDecimal sumContentValue,
			List<BigDecimal> contentValues, String fieldName, BigDecimal value, String contentField, String expression,
			String source, String avgSum) {

		String result = "success";

		if (totalrecordCount > 0 && contentValues.size() > 0) {
			Collections.sort(contentValues);

			if (avgSum.equalsIgnoreCase("avg")) {
				BigDecimal averageContent = sumContentValue.divide(new BigDecimal(totalrecordCount), 2,
						RoundingMode.HALF_UP);
				result = performEvaluation(fieldName, value, averageContent, contentField, expression, result,
						totalrecordCount, source);
			} else if (avgSum.equalsIgnoreCase("sum")) {
				result = performEvaluation(fieldName, value, sumContentValue, contentField, expression, result,
						totalrecordCount, source);
			} else if (avgSum.equalsIgnoreCase("count")) {
				BigDecimal count = new BigDecimal(totalrecordCount);
				result = performEvaluation(fieldName, value, count, contentField, expression, result, totalrecordCount,
						source);
			} else if (avgSum.equalsIgnoreCase("min")) {
				BigDecimal minContent = contentValues.get(0);
				result = performEvaluation(fieldName, value, minContent, contentField, expression, result,
						totalrecordCount, source);
			} else if (avgSum.equalsIgnoreCase("max")) {
				BigDecimal maxContent = contentValues.get(contentValues.size() - 1);
				result = performEvaluation(fieldName, value, maxContent, contentField, expression, result,
						totalrecordCount, source);
			} else {
				result = "Failed: Due to " + source + " calculation is unknown  for attribute " + fieldName;

			}
		}

		else {
			result = "Failed while computing expression for  " + source + " " + fieldName + " and content "
					+ contentField;
		}

		return result;
	}

	public String performEvaluation(String headerField, BigDecimal value, BigDecimal averageContent,
			String contentField, String expression, String result, int totalrecordCount, String source) {

		switch (expression.trim().toLowerCase()) {
		case ">":
			if (!(value.compareTo(averageContent) > 0)) {
				result = "Failed because " + source
						+ " value and content value does not meet relational condition > for " + source + " field Name "
						+ headerField + " and content field Name " + contentField;
				return result;
			}
			break;
		case "<":
			if (!(value.compareTo(averageContent) < 0)) {
				result = "Failed because " + source
						+ " value and content value does not meet relational condition < for  " + source
						+ " field Name " + headerField + " and content field Name " + contentField;
				return result;
			}
			break;
		case "=":
			if (!(value.compareTo(averageContent) == 0)) {
				result = "Failed because " + source
						+ " value and content value does not meet relational condition = for  " + source
						+ " field Name " + headerField + " and content field Name " + contentField;
				return result;
			}
			break;
		default:
			result = "Failed: Due to unknown relational condition  " + expression + " for calculation";
			break;
		}

		return result;
	}

	private BigDecimal fetchContentTotalValue(Map<String, Object> contentObj, String contentField)
			throws NifiCustomException {

		BigDecimal currentValue = BigDecimal.ZERO;

		try {

			Iterator<Entry<String, Object>> mapIterator = contentObj.entrySet().iterator();
			while (mapIterator.hasNext()) {
				Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();

				if (!(ev.getValue() instanceof List)) {
					// if the object is primitive
					if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(contentField)) {

						String temp = (String) ev.getValue();
						if (temp != null) {
							currentValue = currentValue.add(new BigDecimal(temp));
							return currentValue;
						}
					}
				}

				else {
					List<Map<String, Object>> tempList = (List) ev.getValue();
					currentValue = fetchHeaderFooterFieldValue(contentField, tempList, "Content", currentValue);
					return currentValue;
				}

			}
			if (currentValue != null && currentValue.toString().trim().equals("0")) {
				currentValue = null;
			}
		} catch (Exception ex) {
			throw new NifiCustomException("Error while fetching attribute " + contentField
					+ " value from content due to required number value is null or empty");
		}

		return currentValue;
	}

	public Map<String, Object> fetchProcessVariablesDetails(List<ProcessVariable> processVariables) {
		Map<String, Object> processMap = new LinkedHashMap<String, Object>();

		for (ProcessVariable pv : processVariables) {
			String name = pv.getName();
			String value = pv.getType().getTypeName();
			Object result = null;

			if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_PRIMITIVE, pv.getType().getTypeCategory())) {

				if (value != null && value.trim().equalsIgnoreCase("String")) {
					result = pv.getValue().getStringValue();

				} else if (value != null && value.trim().equalsIgnoreCase("Number")) {
					result = String.valueOf(pv.getValue().getIntValue());

				} else if (value != null && value.trim().equalsIgnoreCase("Boolean")) {
					result = String.valueOf(pv.getValue().getBooleanValue());

				} else if (value != null && value.trim().equalsIgnoreCase("DateTime")) {
					result = String.valueOf(pv.getValue().getDateValue());

				}
			} else if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_BE, pv.getType().getTypeCategory())) {
				result = String.valueOf(pv.getValue().getBeValue());
			}

			processMap.put(name, result);

		}

		return processMap;

	}

	public static boolean isDataValidJsonObject(String inputData) {
		try {
			new JSONObject(inputData);
		} catch (JSONException jsonObjectException) {
			return false;
		}
		return true;
	}

	public boolean writeFlowFileContent(final ProcessSession processSession, List<Map<String, Object>> resultMappedList,
			FlowFile flowFileOutput, String ciName) throws NifiCustomException {
		OutputStream writeOutputStream = null;
		JsonWriter writer = null;
		boolean isSuccess = false;

		try {
			writeOutputStream = processSession.write(flowFileOutput);
			writer = new JsonWriter(new OutputStreamWriter(writeOutputStream, Constants.UTF_ENCODING));
			writer.beginArray();

			for (int i = 0; i < resultMappedList.size(); i++) {

				Map<String, Object> resultMap = resultMappedList.get(i);
				gson.toJson(resultMap, mapType, writer);
			}

			writer.endArray();
			isSuccess = true;

		} catch (Exception e) {
			throw new NifiCustomException(ciName + " failed due to error while writing the flowfile");

		} finally {

			try {
				if (writer != null) {
					writer.close();
				}

				if (writeOutputStream != null) {
					writeOutputStream.close();
				}

			} catch (IOException io) {
				throw new NifiCustomException(ciName + " failed due to error while writing the flowfile");
			}

		}
		return isSuccess;
	}

	public List<Map<String, Object>> performDelimitedAction(List<String> inputFileData, int fileHeaderLines,
			int fileFooterLines, Content content, String ciNameProperty, List<String> addressRecordLst,
			List<String> recordRejectLst) throws NifiCustomException {

		List<String> contentInputFileData = new ArrayList<String>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		try {

			int totalHeaderFooter = fileHeaderLines + fileFooterLines;
			int inputDataSize = inputFileData.size();

			if (totalHeaderFooter >= inputDataSize) {
				throw new NifiCustomException(ciNameProperty + ": No Content data available in the Input file");
			} else {

				int fromIndex = 0;
				if (fileHeaderLines != 0) {
					fromIndex = fileHeaderLines;
				}

				int toIndex = inputFileData.size();
				if (fileFooterLines != 0) {
					toIndex = inputFileData.size() - fileFooterLines;
				}

				for (int i = fromIndex; i < toIndex; i++) {
					contentInputFileData.add(inputFileData.get(i));
				}

				resultList = processContent(contentInputFileData, fileHeaderLines, fileFooterLines, content,
						addressRecordLst, recordRejectLst);
			}

		}

		catch (Exception e) {
			throw new NifiCustomException("Error while reading content: " + e.getMessage());
		}

		return resultList;
	}

	public List<String> fetchInputFileData(FlowFile flowFileObj, InputStream fileInputStream, String fileName)
			throws NifiCustomException {

		List<String> inputFileData = new ArrayList<String>();
		BufferedReader fileRead = null;

		try {
			fileRead = new BufferedReader(new InputStreamReader(fileInputStream, Constants.UTF_ENCODING));
			String line = fileRead.readLine();
			while (line != null) {
				if (line.trim().length() > 0) {
					inputFileData.add(line);
				}
				line = fileRead.readLine();
			}

		} catch (Exception e) {
			throw new NifiCustomException("Error occurred while reading Input File: " + e.getMessage());
		}

		finally {
			try {
				if (fileRead != null) {
					fileRead.close();
				}

				if (fileInputStream != null) {
					fileInputStream.close();
				}

			} catch (IOException e) {
				logger.debug("Exception occured while closing the stream " + e.getMessage(), e);
			}
		}

		return inputFileData;

	}

	// Fetch Header from Real time data ascii text using Header JSON.
	private List<Map<String, Object>> processHeader(List<String> inputFileData, Header header, int totalHeaderLines)
			throws NifiCustomException {

		List<Map<String, Object>> headerResultList = new ArrayList<Map<String, Object>>();

		Delimited delimited = null;

		try {
			delimited = header.getDelimited();
			if (delimited != null) {
				headerResultList = processHeaderFooterRecord(delimited, inputFileData, "header");
			}
		} catch (Exception ex) {
			throw new NifiCustomException(ex.getMessage());

		}
		return headerResultList;
	}

	private List<Map<String, Object>> processHeaderFooterRecord(Delimited delimited, List<String> inputFileData,
			String source) throws NifiCustomException {

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String rootRecordSeparater = delimited.getRecord();
		List<DelimitedAttributes> daList = delimited.getAttributes();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

		// Start with the JSON and use the JSON to pull value from inputFileData
		for (DelimitedAttributes da : daList) {

			String attributeName = da.getAttributeName();
			int SegmentPostion = da.getSegmentPosition();
			int lineNumber = da.getLineNumber();
			String type = da.getDataType();
			Object value = null;

			fetchHeaderFooterData(delimited, inputFileData, rootRecordSeparater, da, attributeName, SegmentPostion,
					lineNumber, type, value, resultMap, source);
		}
		if (resultMap != null && resultMap.size() > 0) {
			resultList.add(resultMap);
		}

		return resultList;

	}

	private Map<String, Object> fetchHeaderFooterData(Delimited delimited, List<String> inputFileData,
			String rootRecordSeparater, DelimitedAttributes da, String attributeName, int segmentPostion,
			int lineNumber, String type, Object value, Map<String, Object> resultMap, String source)
			throws NifiCustomException {
		String currentNode = da.getCurrentNode();
		for (int i = 0; i < inputFileData.size(); i++) {
			if ((lineNumber - 1) == i) {
				// The record from which the value needs to be pulled
				String currentLine = inputFileData.get(i);
				String[] recordSplitter = currentLine.split(Pattern.quote(rootRecordSeparater));

				for (String currentRecord : recordSplitter) {

					if (currentRecord != null && currentRecord.trim().length() > 0) {

						String[] attributedSplit = currentRecord.split(Pattern.quote(delimited.getAttribute()));
						// get the attributePosition
						if (currentNode != null && currentNode.equalsIgnoreCase("root")) {
							if (attributedSplit != null && countDelimitedSegments(currentRecord,
									delimited.getAttribute()) < segmentPostion) {
								throw new NifiCustomException("Failed: " + source + " The segment postion "
										+ segmentPostion + " given for " + attributeName
										+ " does not have any data in the Input Record " + currentRecord);
							}
							String attributeData = "";
							try {
								attributeData = attributedSplit[segmentPostion - 1];
							} catch (ArrayIndexOutOfBoundsException ex) {
								continue;
							}

							if (type != null && !type.equalsIgnoreCase("object") && !type.equalsIgnoreCase("array")) {
								FormatAttributes formatAttributes = da.getFormat();
								if (validateData(type, attributeData, formatAttributes)) {
									if ("DateTime".equalsIgnoreCase(type) && StringUtils.isNotEmpty(attributeData)) {
										attributeData = convertDate(attributeData, formatAttributes);
									} else if ("Boolean".equalsIgnoreCase(type)) {
										attributeData = convertBoolean(attributeData, formatAttributes);
									}
									resultMap.put(attributeName, attributeData);
									return resultMap;
								} else {
									throw new NifiCustomException("Failed: " + source + " value not fetched"
											+ " due to dataType " + type + " issue for attribute " + attributeName);
								}

							} else if (type != null && type.equalsIgnoreCase("object")) {
								Map<String, String> internalRecordDelimiter = da.getAttributeType().getDelimiter();
								String internalAttribute = (String) internalRecordDelimiter.get("attribute");
								value = processHeaderFooterInnerObject(delimited, attributeName, source, attributeData,
										internalAttribute);
								resultMap.put(attributeName, value);
								return resultMap;

							} else if (type != null && type.equalsIgnoreCase("array")) {
								Map<String, String> internalRecordDelimiter = da.getAttributeType().getDelimiter();
								String internalRec = (String) internalRecordDelimiter.get("record");
								String internalAttribute = (String) internalRecordDelimiter.get("attribute");
								String[] subRecordSpliter = attributeData.split(Pattern.quote(internalRec));
								value = processHeaderFooterInnerArray(delimited, attributeName, source,
										subRecordSpliter, internalAttribute);
								resultMap.put(attributeName, value);
								return resultMap;

							}
						}
					}
				}
			}
		}

		return resultMap;
	}

	private Map<String, Object> processHeaderFooterInnerObject(Delimited delimited, String parent, String source,
			String attributeDataObj, String internalAtt) throws NifiCustomException {
		Object value = null;
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		String[] subAttributeSpliter = attributeDataObj.split(Pattern.quote(internalAtt));
		List<DelimitedAttributes> delimtiedAttributeList = delimited.getAttributes();
		for (DelimitedAttributes daObj : delimtiedAttributeList) {
			String parentTemp = daObj.getCurrentNode();

			if (parent != null && parent.equalsIgnoreCase(parentTemp)) {
				String attributeName = daObj.getAttributeName();
				int segmentPosition = daObj.getSegmentPosition();

				if (subAttributeSpliter != null
						&& countDelimitedSegments(attributeDataObj, internalAtt) < segmentPosition) {
					throw new NifiCustomException("Failed: " + source + " The segment postion " + segmentPosition
							+ " given for " + attributeName + " does not have any data in the Input Record "
							+ attributeDataObj);
				}

				String attributeData = "";
				try {
					attributeData = subAttributeSpliter[segmentPosition - 1];
				} catch (ArrayIndexOutOfBoundsException ex) {
					continue;
				}

				String type = daObj.getDataType();
				if (type != null && !type.equalsIgnoreCase("object") && !type.equalsIgnoreCase("array")) {
					FormatAttributes formatAttributes = daObj.getFormat();
					if (validateData(type, attributeData, formatAttributes)) {
						if ("DateTime".equalsIgnoreCase(type) && StringUtils.isNotEmpty(attributeData)) {
							attributeData = convertDate(attributeData, formatAttributes);
						} else if ("Boolean".equalsIgnoreCase(type)) {
							attributeData = convertBoolean(attributeData, formatAttributes);
						}
						result.put(attributeName, attributeData);
					} else {
						throw new NifiCustomException("Failed: " + source + "  value not fetched due to dataType "
								+ type + " issue for attribute " + attributeName);
					}

				} else if (type != null && type.equalsIgnoreCase("object")) {
					Map<String, String> internalRecordDelimiter = daObj.getAttributeType().getDelimiter();
					String internalAttribute = (String) internalRecordDelimiter.get("attribute");
					value = processHeaderFooterInnerObject(delimited, attributeName, source, attributeData,
							internalAttribute);
					result.put(attributeName, value);

				} else if (type != null && type.equalsIgnoreCase("array")) {

					Map<String, String> internalRecordDelimiter = daObj.getAttributeType().getDelimiter();
					String internalRec = (String) internalRecordDelimiter.get("record");
					String internalAttribute = (String) internalRecordDelimiter.get("attribute");
					String[] subRecordSpliter = attributeData.split(Pattern.quote(internalRec));
					List<Map<String, Object>> innerTempList = new ArrayList<Map<String, Object>>();
					innerTempList = processHeaderFooterInnerArray(delimited, attributeName, source, subRecordSpliter,
							internalAttribute);
					result.put(attributeName, innerTempList);

				}
			}
		}

		return result;

	}

	private List<Map<String, Object>> processHeaderFooterInnerArray(Delimited delimited, String parent, String source,
			String[] subRecordSplit, String internalAtt) throws NifiCustomException {
		Object value = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (int j = 0; j < subRecordSplit.length; j++) {
			String[] subAttributeSpliter = subRecordSplit[j].split(Pattern.quote(internalAtt));

			Map<String, Object> mapObj = new LinkedHashMap<String, Object>();

			List<DelimitedAttributes> delimtiedAttributeList = delimited.getAttributes();

			for (DelimitedAttributes daObj : delimtiedAttributeList) {
				String parentTemp = daObj.getCurrentNode();

				if (parent != null && parent.equalsIgnoreCase(parentTemp)) {
					String attributeName = daObj.getAttributeName();
					int segmentPosition = daObj.getSegmentPosition();

					if (subAttributeSpliter != null
							&& countDelimitedSegments(subRecordSplit[j], internalAtt) < segmentPosition) {
						throw new NifiCustomException("Failed: " + source + "|| The segment postion " + segmentPosition
								+ " given for " + attributeName + " does not have any data in the Input Record "
								+ subRecordSplit[j]);
					}

					String attributeData = "";
					try {
						attributeData = subAttributeSpliter[segmentPosition - 1];
					} catch (ArrayIndexOutOfBoundsException ex) {
						continue;
					}

					String type = daObj.getDataType();
					if (type != null && !type.equalsIgnoreCase("object") && !type.equalsIgnoreCase("array")) {
						FormatAttributes formatAttributes = daObj.getFormat();
						if (validateData(type, attributeData, formatAttributes)) {
							if ("DateTime".equalsIgnoreCase(type) && StringUtils.isNotEmpty(attributeData)) {
								attributeData = convertDate(attributeData, formatAttributes);
							} else if ("Boolean".equalsIgnoreCase(type)) {
								attributeData = convertBoolean(attributeData, formatAttributes);
							}
							mapObj.put(attributeName, attributeData);
						} else {
							throw new NifiCustomException("Failed: " + source + "  value not fetched due to dataType "
									+ type + " issue for attribute " + attributeName);
						}

					} else if (type != null && type.equalsIgnoreCase("object")) {
						Map<String, String> internalRecordDelimiter = daObj.getAttributeType().getDelimiter();
						String internalAttribute = (String) internalRecordDelimiter.get("attribute");
						value = processHeaderFooterInnerObject(delimited, attributeName, source, attributeData,
								internalAttribute);
						mapObj.put(attributeName, value);

					} else if (type != null && type.equalsIgnoreCase("array")) {
						Map<String, String> internalRecordDelimiter = daObj.getAttributeType().getDelimiter();
						String internalRec = (String) internalRecordDelimiter.get("record");
						String internalAttribute = (String) internalRecordDelimiter.get("attribute");
						String[] subRecordSpliter = attributeData.split(Pattern.quote(internalRec));
						List<Map<String, Object>> innerTempList = new ArrayList<Map<String, Object>>();
						innerTempList = processHeaderFooterInnerArray(delimited, attributeName, source,
								subRecordSpliter, internalAttribute);

						mapObj.put(attributeName, innerTempList);

					}
				}

			}

			if (mapObj != null && mapObj.size() > 0) {
				result.add(mapObj);
			}
		}

		return result;
	}

	private List<Map<String, Object>> processFooter(List<String> inputFileData, Footer footer, int totalFooterLines)
			throws NifiCustomException {

		List<Map<String, Object>> footerResultList = new ArrayList<Map<String, Object>>();

		Delimited delimited = null;

		try {
			delimited = footer.getDelimited();
			if (delimited != null) {
				footerResultList = processHeaderFooterRecord(delimited, inputFileData, "footer");
			}
		} catch (Exception ex) {
			throw new NifiCustomException(ex.getMessage());

		}
		return footerResultList;

	}

	private List<Map<String, Object>> processContent(List<String> inputFileData, int fileHeaderLines,
			int fileFooterLines, Content content, List<String> addressRecordLst, List<String> recordRejectLst)
			throws NifiCustomException {

		Delimited delimited = null;
		List<Map<String, Object>> attributesMappedWithRealData = new ArrayList<Map<String, Object>>();

		try {

			if (content != null) {
				delimited = content.getDelimited();

				if (delimited != null) {
					attributesMappedWithRealData = constructRecord(inputFileData, delimited, addressRecordLst,
							recordRejectLst);
				}
			}
		} catch (Exception e) {
			throw new NifiCustomException(e.getMessage());
		}

		return attributesMappedWithRealData;
	}

	public List<Map<String, Object>> constructRecord(List<String> inputFileData, Delimited delimited,
			List<String> addressRecordLst, List<String> recordRejectLst) {

		String rootRecordSeparater = delimited.getRecord();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<DelimitedAttributes> daList = delimited.getAttributes();

		List<String> bukMergedRecord = new ArrayList<String>();

		// The logic starts from below and its working :D
		for (String currentLine : inputFileData) {

			String[] recordSplitter = currentLine.split(Pattern.quote(rootRecordSeparater));
			if (recordSplitter != null && recordSplitter.length > 0) {

				// the line can have multiple records
				for (String currentRecord : recordSplitter) {

					if (currentRecord != null && currentRecord.trim().length() > 0) {

						// this is root
						String mergedRecord = "";
						String recordReject = currentRecord;
						String[] attributedSplit = currentRecord.split(Pattern.quote(delimited.getAttribute()));
						Map<String, Object> attrbuteDataMap = new LinkedHashMap<String, Object>();

						for (DelimitedAttributes da : daList) {
							if (attrbuteDataMap.containsKey("mergedRecord")) {
								mergedRecord = (String) attrbuteDataMap.get("mergedRecord");
							}
							String parent = da.getCurrentNode();
							if (parent != null && parent.equalsIgnoreCase("root")) {
								attrbuteDataMap = validateRecordAttribute(delimited, attrbuteDataMap, attributedSplit,
										da, parent, recordReject, result, mergedRecord);
							}

							if (attrbuteDataMap != null && attrbuteDataMap.containsKey("RecordReject")) {
								// THIS IS A REJECTED RECORD
								recordRejectLst.add((String) attrbuteDataMap.get("RecordReject"));
								break;
							}

						}

						// THIS IS A ADDRESSED RECORD.
						if (attrbuteDataMap != null && attrbuteDataMap.size() > 0
								&& !attrbuteDataMap.containsKey("RecordReject")) {

							// MergedRecord Contains BUK Information

							if (attrbuteDataMap.containsKey("mergedRecord")) {
								mergedRecord = (String) attrbuteDataMap.get("mergedRecord");
								attrbuteDataMap.remove("mergedRecord");

								if (!bukMergedRecord.contains(mergedRecord)) {
									bukMergedRecord.add(mergedRecord);
									addressRecordLst.add(currentRecord);
									result.add(attrbuteDataMap);

								} else {
									recordReject = currentRecord + rootRecordSeparater
											+ " Reject because its duplicate BUK value ";
									recordRejectLst.add(recordReject);
								}

							} else {

								addressRecordLst.add(currentRecord);
								result.add(attrbuteDataMap);
							}
						}
					}
				}
			}
		}

		return result;

	}

	public Map<String, Object> validateRecordAttribute(Delimited delimited, Map<String, Object> attrbuteDataMap,
			String[] attributedSplit, DelimitedAttributes da, String parentKey, String recordReject,
			List<Map<String, Object>> result, String mergedRecord) {

		String parent = da.getCurrentNode();
		String rootRecordSeparater = delimited.getRecord();
		if (parent != null && parent.equalsIgnoreCase(parentKey)) {
			String attributeName = da.getAttributeName();
			String dataType = da.getDataType();
			int segmentPosition = da.getSegmentPosition();

			if (dataType != null && !dataType.equalsIgnoreCase("object") && !dataType.equalsIgnoreCase("array")) {

				if (attributedSplit != null && attributedSplit.length < segmentPosition) {
					if (!da.isBuk()) {
						attrbuteDataMap.put(attributeName, "");
						return attrbuteDataMap;
					}
					recordReject = recordReject + rootRecordSeparater + "  The segment position " + segmentPosition
							+ " given for " + attributeName + " does not have any data in the Input Record.";
					attrbuteDataMap.put("RecordReject", recordReject);
					return attrbuteDataMap;
				}

				String realData = attributedSplit[segmentPosition - 1];
				FormatAttributes formatAttributes = da.getFormat();
				if (validateData(dataType, realData, formatAttributes)) {
					if ("DateTime".equalsIgnoreCase(dataType) && StringUtils.isNotEmpty(realData)) {
						realData = convertDate(realData, formatAttributes);
					} else if ("Boolean".equalsIgnoreCase(dataType)) {
						realData = convertBoolean(realData, formatAttributes);
					}
					attrbuteDataMap.put(attributeName, realData);
				} else {
					recordReject = recordReject + rootRecordSeparater + attributeName + " value " + realData
							+ " does not match with the datatype " + dataType;
					attrbuteDataMap.put("RecordReject", recordReject);
					return attrbuteDataMap;
				}

				// check for BUK
				// Check any duplicate value is already present in the existing
				// RESULT list..

				if (da.isBuk() && parent.equalsIgnoreCase("root")) {
					
					if (StringUtils.isBlank(realData)) {
						if (attrbuteDataMap.containsKey("mergedRecord")) {
							attrbuteDataMap.remove("mergedRecord");
						}
						recordReject = recordReject + rootRecordSeparater + attributeName + " value is empty";
						attrbuteDataMap.put("RecordReject", recordReject);
						return attrbuteDataMap;
					} else {
						if (attrbuteDataMap.containsKey("mergedRecord")) {
							mergedRecord = attrbuteDataMap.get("mergedRecord") + String.valueOf(realData);
							attrbuteDataMap.put("mergedRecord", mergedRecord);
						} else {
							mergedRecord = mergedRecord + String.valueOf(realData);
							attrbuteDataMap.put("mergedRecord", mergedRecord);
						}
					}
				}

			} else if (dataType != null && dataType.equalsIgnoreCase("object")) {
				String attributeName1 = da.getAttributeName();
				Map<String, String> internalRecordDelimiter = da.getAttributeType().getDelimiter();
				String internalAttribute = (String) internalRecordDelimiter.get("attribute");
				Map<String, Object> subObject = new LinkedHashMap<String, Object>();
				if (attributedSplit != null && attributedSplit.length < segmentPosition) {
					if (!da.isBuk()) {
						attrbuteDataMap.put(attributeName1, subObject);
						return attrbuteDataMap;
					}
					recordReject = recordReject + rootRecordSeparater + "  The segment position " + segmentPosition
							+ " given for " + attributeName + " does not have any data in the Input Record";
					attrbuteDataMap.put("RecordReject", recordReject);
					return attrbuteDataMap;
				}
				String innerAttributeData = attributedSplit[segmentPosition - 1];
				if (innerAttributeData != null && innerAttributeData.trim().length() > 0) {
					String[] inAtttt = innerAttributeData.split(Pattern.quote(internalAttribute));
					List<DelimitedAttributes> daListForObject = delimited.getAttributes();

					for (DelimitedAttributes internalObj : daListForObject) {
						String parentInternal = internalObj.getCurrentNode();

						if (attributeName1.equalsIgnoreCase(parentInternal)) {
							subObject = validateRecordAttribute(delimited, subObject, inAtttt, internalObj,
									attributeName1, recordReject, result, mergedRecord);

							if (subObject != null && subObject.containsKey("RecordReject")) {
								return subObject;
							}
						}
					}

					if (subObject != null && subObject.size() > 0) {
						attrbuteDataMap.put(attributeName1, subObject);
					}
				}
			} else if (dataType != null && dataType.equalsIgnoreCase("array")) {

				String attributeName1 = da.getAttributeName();
				// this is the SUB objectname

				Map<String, String> internalRecordDelimiter = da.getAttributeType().getDelimiter();
				String internalRec = (String) internalRecordDelimiter.get("record");
				String internalAttribute = (String) internalRecordDelimiter.get("attribute");
				List<Map<String, Object>> subObjectList = new ArrayList<Map<String, Object>>();

				if (attributedSplit != null && attributedSplit.length < segmentPosition) {
					if (!da.isBuk()) {
						attrbuteDataMap.put(attributeName1, subObjectList);
						return attrbuteDataMap;
					}
					recordReject = recordReject + rootRecordSeparater + "  The segment position " + segmentPosition
							+ " given for " + attributeName + " does not have any data in the Input Record";
					attrbuteDataMap.put("RecordReject", recordReject);
					return attrbuteDataMap;
				}

				String innerAttributeData = attributedSplit[segmentPosition - 1];
				String[] innerAttributeDataArray = innerAttributeData.split(Pattern.quote(internalRec));

				if (innerAttributeDataArray.length > 0) {

					for (String inRec : innerAttributeDataArray) {

						if (inRec != null && inRec.trim().length() > 0) {
							String[] inAtttt = inRec.split(Pattern.quote(internalAttribute));
							List<DelimitedAttributes> daListForObject = delimited.getAttributes();
							Map<String, Object> subObject = new LinkedHashMap<String, Object>();

							for (DelimitedAttributes internalObj : daListForObject) {
								String parentInternal = internalObj.getCurrentNode();

								if (attributeName1.equalsIgnoreCase(parentInternal)) {
									subObject = validateRecordAttribute(delimited, subObject, inAtttt, internalObj,
											attributeName1, recordReject, result, mergedRecord);

									if (subObject != null && subObject.containsKey("RecordReject")) {
										return subObject;
									}
								}
							}

							if (subObject != null && subObject.size() > 0) {
								subObjectList.add(subObject);
								attrbuteDataMap.put(attributeName1, subObjectList);
							}
						}
					}

				} else {
					// if null inner object in realtime data, then assign empty object
					attrbuteDataMap.put(attributeName1, subObjectList);
				}
			}
		}

		return attrbuteDataMap;
	}

	public boolean validateData(String dataType, String value, FormatAttributes formatAttributes) {

		if(StringUtils.isEmpty(value)){
			return true;
		} else if ("String".equalsIgnoreCase(dataType) && value.matches("^[a-zA-Z0-9,.\\-_\\s]*$")) {
			return true;
		} else if ("Number".equalsIgnoreCase(dataType) && value.matches("^[\\d+\\s\\.]*$")) {
			if (null != formatAttributes.getScale() && StringUtils.isNotEmpty(formatAttributes.getScale())
					&& (null != formatAttributes.getPrecision()
							&& StringUtils.isNotEmpty(formatAttributes.getPrecision()))) {
				int scale = Integer.valueOf(formatAttributes.getScale());
				int precision = Integer.valueOf(formatAttributes.getPrecision());
				String scaleVal = "";
				int valueScalelen = 0;
				if (value.contains(".")) {
					if (value.split("\\.").length == 2) {
						scaleVal = value.split("\\.")[1];
					}
				}
				if (null != scaleVal && StringUtils.isNotEmpty(scaleVal)) {
					valueScalelen = scaleVal.length();
				}
				if (valueScalelen <= scale) {
					if (value.contains(".") && value.length() - 1 <= precision) {
						return true;
					} else if (value.length() <= precision) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else if ("Boolean".equalsIgnoreCase(dataType)
				&& (formatAttributes.getTrueValue().equalsIgnoreCase(value.trim())
						|| formatAttributes.getFalseValue().equalsIgnoreCase(value.trim()))) {
			return true;
		} else if ("DateTime".equalsIgnoreCase(dataType)) {
			String dateFormat = formatAttributes.getDateTime();
			if (null != dateFormat && StringUtils.isNotEmpty(dateFormat)) {
				SimpleDateFormat sdfrmt = new SimpleDateFormat(dateFormat);
				try {
					sdfrmt.parse(value.trim());
					return true;
				} catch (ParseException e) {
					return false;
				}
			} else
				return false;

		} else
			return false;
	}

public void writeToFile(List<String> data, String fileNameString, String dirPath) {
		checkCreateDir(dirPath);
		SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMddhhmmssms");
		
		String fileName=fileNameString.substring(0, fileNameString.length()-4);
		String ext=fileNameString.substring(fileNameString.length()-4,fileNameString.length());
		String dateTime=fileNameFormat.format(new Date());
		
		File file = new File(dirPath + "//" + fileName + "_" + dateTime + ext);
		FileWriter fileWriterObject = null;
		BufferedWriter bufferedWriterObject = null;

		try {
			fileWriterObject = new FileWriter(file);
			bufferedWriterObject = new BufferedWriter(fileWriterObject);
			for (String data1 : data) {
				String dataWithNewLine = data1 + System.getProperty("line.separator");
				bufferedWriterObject.write(dataWithNewLine);
			}
		} catch (IOException e) {
			logger.error("Error occurred at writeToFile FileChannelInput :: ", e);
		} finally {
			try {
				bufferedWriterObject.close();
				fileWriterObject.close();
			} catch (IOException e) {
				logger.error("Error occurred at writeToFile FileChannelInput :: ", e);
			}
		}

	}

	public String validateFileName(String sessionId, String runNumber, String fileName, Validation validationObj,
			String remoteURL) {
		String response = "Success";
		boolean fileNameValStatus = true;
		String fName = fileName.split("\\.")[0];
		String count = "";
		String duration = "";
		Map<String, String> fileNameDuplicationLObj = validationObj.getFileNameDuplication();
		String condition = validationObj.getFileNameValidation().getCondition();
		List<RuleAttributes> ruleAttrList = validationObj.getFileNameValidation().getRules();
		if (null != ruleAttrList && ruleAttrList.size() > 0) {
			fileNameValStatus = expressionRules(condition, ruleAttrList, fName);
		}
		String fileNameDupStatus = "";
		if (fileNameValStatus) {
			count = fileNameDuplicationLObj.get("count");
			duration = fileNameDuplicationLObj.get("duration");
			FileNameDetails fileNameDetails = new FileNameDetails();
			fileNameDetails.setSessionId(sessionId);
			fileNameDetails.setRunNumber(runNumber);
			fileNameDetails.setFileName(fileName);
			fileNameDetails.setCount(count);
			fileNameDetails.setDuration(duration);

			if (count != null && count != "" && duration != null && duration != "") {
				final String ROOT_URI = remoteURL + "/bpruntime/sessionmanager/getFilesNameStatus";
				try {
					ResponseEntity<String> apiResponse = restTemplate.postForEntity(ROOT_URI, fileNameDetails,
							String.class);
					fileNameDupStatus = apiResponse.getBody();
					System.out.println(apiResponse);
				} catch (Exception e) {
					fileNameDupStatus = "FileName Validation Failed exception message: ROOT_URI-" + e.getMessage();
					logger.error("Error occurred at validateFileName FileChannelInput :: ", e);
				}
				if (!fileNameDupStatus.contains("Success"))
					response = fileNameDupStatus;
			} else {
				return response;
			}

		} else {
			response = "File Name Validation Failed:Unsatisfied Rule Set";
		}
		return response;
	}

	private boolean expressionRules(String condition, List<RuleAttributes> ruleAttrList, String fileName) {
		boolean nifiExpression = false;
		if (condition.equals(Constants.OR)) {
			for (RuleAttributes ruleAttributesObj : ruleAttrList) {
				nifiExpression = createNifiExpression(ruleAttributesObj, fileName);
				if (nifiExpression) {
					break;
				}
			}

		} else if (condition.equals(Constants.AND)) {
			for (RuleAttributes ruleAttributesObj : ruleAttrList) {
				nifiExpression = createNifiExpression(ruleAttributesObj, fileName);
				if (!nifiExpression) {
					break;
				}
			}
		}

		return nifiExpression;
	}

	private boolean createNifiExpression(RuleAttributes ruleAttributesObj, String fileName) {
		String lhsValue = fileName;
		String rhsValue = ruleAttributesObj.getCustomValue();
		boolean nifiExpression = false;
		nifiExpression = generateExpression(lhsValue, rhsValue, ruleAttributesObj.getOperator());
		return nifiExpression;
	}

	private boolean generateExpression(String lhsVal, String rhsVal, String operator) {

		switch (operator) {
		case "Equals":
			return lhsVal.equals(rhsVal);
		case "NotEquals":
			return !lhsVal.equals(rhsVal);
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
		default:
			return false;
		}
	}

	public void updateOperatorStatistics(String sessionId, String runNumber, String ciNameProperty, String fileName,
			int receivedFilesCount, int acceptedFilesCount, int rejectedFilesCount, int totalRecordsCount,
			int acceptedRecordsCount, int rejectedRecordsCount) {
		SessionDetails sessionDetails = new SessionDetails();
		sessionDetails.setSessionId(sessionId);
		sessionDetails.setRunNumber(runNumber);
		sessionDetails.setOperatorName(ciNameProperty);
		sessionDetails.setFileName(fileName);
		sessionDetails.setReceivedFilesCount(receivedFilesCount);
		sessionDetails.setAcceptedFilesCount(acceptedFilesCount);
		sessionDetails.setRejectedFilesCount(rejectedFilesCount);
		sessionDetails.setTotalRecordsCount(totalRecordsCount);
		sessionDetails.setAcceptedRecordsCount(acceptedRecordsCount);
		sessionDetails.setRejectedRecordsCount(rejectedRecordsCount);
		OkHttpClient okHttpClient = okHttpClientAtomicReference.get();
		Request.Builder requestBuilder = new Request.Builder();
		requestBuilder = requestBuilder.url(transactionStatusUrl);
		try {
			requestBuilder = requestBuilder.post(RequestBody.create(MediaType.parse(DEFAULT_CONTENT_TYPE), mapper.writeValueAsString(sessionDetails)));
		} catch (IOException e) {
			logger.error("Error occurred at updateOperatorStatistics:: " + e.getMessage());
		}
		ResponseBody responseBody = null;
		try (Response responseHttp = okHttpClient.newCall(requestBuilder.build()).execute()) {
			responseBody = responseHttp.body();
			if(responseHttp.code() != 200) {
				logger.error("Exception while updating the status :: {}", new Object[]{responseHttp.code()});
			}
		} catch (IOException e) {
			logger.error("Error occurred at updateOperatorStatistics :: {}", new Object[]{e.getMessage()}, e);
		} finally {
			if(responseBody != null) {
				responseBody.close();
			}
		}
	}

	private String convertDate(String value, FormatAttributes formatAttributes) {
		SimpleDateFormat oldFormat = new SimpleDateFormat(formatAttributes.getDateTime());
		SimpleDateFormat newFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		Date date = new Date();
		try {
			date = oldFormat.parse(value.trim());
		} catch (ParseException e) {
		}
		return newFormat.format(date);
	}

	private String convertBoolean(String value, FormatAttributes formatAttributes) {
		return formatAttributes.getTrueValue().equalsIgnoreCase(value.trim()) ? "true" : "false";
	}

	private void checkCreateDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists())
			file.mkdirs();
	}

	private String generateTransactionId() {
		return UUID.randomUUID().toString();
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

	private Object prepareObjectDetails(String toParent, String toCurrent, Map<String, Object> inputBERecord,
			List<CIMapping> mappingList, Map<String, Object> objectBEMap, String toPath,
			Map<String, Object> flowFileAttributes, String source,Set<String> subDenorMainArrayLst) throws NifiCustomException {

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
				List<Map<String, Object>> arrResultList=new ArrayList<>();
				String toCurrentInner = beNameObj[0];
				toParent = toCurrent;
				if (subDenorMainArrayLst.contains(toCurrentInner)) {
					// array belong to denorm
					try{
					arrResultList = MappingUtils.getSubArayDenorm(inputBERecord, mappingList,
							toCurrentInner);
					}catch(Exception e){
						throw new NifiCustomException(e.getMessage());
					}
					mappingResultMap.put(toCurrentInner, arrResultList);
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
				try {
					result = fetchValueFromInputMap(inputBERecord, mappingList, toPathTemp, null, flowFileAttributes,
							source);
				} catch (Exception e) {
					throw new NifiCustomException(e.getMessage());
				}

				mappingResultMap.put(toValue, result);

			}
		}

		return mappingResultMap;

	}

	private Map<String, Object> fetchArrayDataFromPVInputRecord(Map<String, Object> inputBERecord,
			List<CIMapping> mappingList, String newToValue, String skipFromPath, Map<String, Object> flowFileAttributes)
			throws NifiCustomException {

		List<Map<String, Object>> inputRecordList = null;
		Map<String, Object> pvInputRecordWithPath = new LinkedHashMap<String, Object>();

		try {

			for (CIMapping mapping : mappingList) {

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
						Map<String, Object> pvObjectArray = (Map) flowFileAttributes.get(processVariableName);

						/*
						 * Map<String, Object> pvObjectArray =
						 * getObjectfromFlowFileAttributes(flowFileAttributes,
						 * processVariableName);
						 */

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
				else if (mapping.getType() != null && allIgnoreTypes.contains(mapping.getType().trim())) {

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

	private Object prepareArrayDetails(String toParent, String toCurrent, Map<String, Object> inputBERecord,
			List<CIMapping> mappingList, List<Map<String, Object>> objectBEMap, String newToValue, String skipFromPath,
			Map<String, Object> flowFileAttributes, String source) throws NifiCustomException {

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
				try {
					result = fetchValueFromInputMap(inputBERecord, mappingList, toPathTemp, skipFromPath,
							flowFileAttributes, source);

				} catch (Exception e) {
					throw new NifiCustomException(e.getMessage());
				}
				
				mappingResultMap.put(toValue, result);

			}
		}

		return mappingResultMap;

	}

	private Object fetchValueFromInputMap(Map<String, Object> inputBERecord, List<CIMapping> mappingList,
			String newToValue, String skipFromValue, Map<String, Object> flowFileAttributes, String source)
			throws NifiCustomException, Exception {

		Object result = null;
		String type = null;

		for (CIMapping mapping : mappingList) {

			type = mapping.getType();

			if (type != null && !allIgnoreTypes.contains(type.trim())) {
				String fromPath = mapping.getFromPath();
				String toPath = mapping.getToPath();
				String fromValue = mapping.getFromValue();
				List<ConversionArray> typeConversionArrayObj = mapping.getTypeConversionArray();
				if (toPath != null && toPath.trim().equalsIgnoreCase(newToValue.trim())) {

					if (type.trim().equalsIgnoreCase("ibe")) {

						if (skipFromValue != null) {
							String newFromPath = fromPath.replace(skipFromValue, "");
							result = getInputRecordFromNestedStructure(newFromPath, inputBERecord, fromValue,
									typeConversionArrayObj);
							return result;
						} else {
							result = getInputRecordFromNestedStructure(fromPath, inputBERecord, fromValue,
									typeConversionArrayObj);
							return result;
						}
					}

					else if (type.trim().equalsIgnoreCase("pv")) {
						// This is PV as an primitive variable
						String processVariableName = mapping.getProcessVariableAttribute();
						result = flowFileAttributes.get(processVariableName);
						// result =
						// getDatafromFlowFileAttributes(flowFileAttributes,
						// processVariableName);
						return result;

					}

					else if (type.trim().equalsIgnoreCase("PvTypeBE")) {

						String[] split = null;
						Map<String, Object> findHierarichyMap = null;

						String processVariableName = mapping.getProcessVariableAttribute();

						if (source != null && source.trim().equalsIgnoreCase("pv")) {
							findHierarichyMap = inputBERecord;
						} else {
							findHierarichyMap = (Map) flowFileAttributes.get(processVariableName);
							// findHierarichyMap =
							// getObjectfromFlowFileAttributes(flowFileAttributes,processVariableName);
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

	private Object prepareObjectDetailsForArray(String toParent, String toCurrent, Map<String, Object> inputBERecord,
			List<CIMapping> mappingList, Map<String, Object> objectBEMap, String toPath, String skipFromValue,
			Map<String, Object> flowFileAttributes, String source) throws NifiCustomException {

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

				try {
					result = fetchValueFromInputMap(inputBERecord, mappingList, toPathTemp, skipFromValue,
							flowFileAttributes, source);

				} catch (Exception e) {
					throw new NifiCustomException(e.getMessage());
				}
				
				

				mappingResultMap.put(toValue, result);
			}
		}

		return mappingResultMap;

	}

	private Object getInputRecordFromNestedStructure(String fromPath, Map<String, Object> inputBERecord,
			String fromValue, List<ConversionArray> typeConversionArrayObj) throws NifiCustomException {

		Map<String, Object> findHierarichyMap = inputBERecord;

		Object result = null;
		String[] split = fromPath.split("-");

		for (int i = split.length - 1; i >= 0; i--) {

			String temp = split[i];

			if (temp != null && temp.trim().length() > 0 && !allIgnoreTypes.contains(temp.trim())) {

				result = findHierarichyMap.get(temp);

				if (result != null && result instanceof Map) {
					findHierarichyMap = (Map<String, Object>) result;
				} else if (result != null && result instanceof List) {
					throw new NifiCustomException("Primitive attribute " + fromValue + " value cannot be pulled from "
							+ temp + " array hierarchy ");
				}
			}
		}
		try {
			String value = result.toString();
			if (typeConversionArrayObj != null) {
				if (typeConversionArrayObj.size() >= 1) {
					result = MappingUtils.convertData(value, typeConversionArrayObj);
				}
			}
		} catch (Exception e) {
			throw new NifiCustomException(e.getMessage());
		}
		return result;
	}
	
	private static int countDelimitedSegments(String str, String delimiter) {
		int count = 0;
		boolean proceed = false;
		int pos = str.length() - 1;
		if (("" + delimiter).equals(str)) {
			count = 2;
		} else if (("" + delimiter + delimiter).equals(str)) {
			count = 3;
		} else {
			if (delimiter.length() == 1) {
				do {
					if (str.charAt(pos) == delimiter.charAt(0)) {
						count++;
					} else {
						break;
					}
					if (pos >= 0) {
						if (pos == 0) {
							count += (str.charAt(pos) == delimiter.charAt(0)) ? 1 : 0;
							proceed = false;
						} else {
							proceed = (str.charAt(--pos) == delimiter.charAt(0));
						}
					}
				} while (proceed);
			} else {
				count = 0;
			}
		}
		return (str.split(Pattern.quote(delimiter)).length + count);
	}

}