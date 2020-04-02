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
import com.suntecgroup.custom.processor.model.channelintegration.FileNameDetails;
import com.suntecgroup.custom.processor.model.channelintegration.FixedWidth;
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

/**
 * This class is for creating a custom NiFi processor to handle the File Input
 * Channel Integration operator
 * 
 * @version 28 Feb 2019
 * @author Ramesh Kumar B
 */
@SideEffectFree
@Tags({ "FileInput, Channel Integration" })
@CapabilityDescription("File Input Processor for Channel Integration")
public class FixedWidthInputFileChannelProcessor extends AbstractProcessor {

	RestTemplate restTemplate = new RestTemplate();

	private ComponentLog logger;
	// private String channelOutputBEType;
	List<String> failedResult = null;
	private Gson gson = null;
	private Type mapTypeObj = null;
	private ObjectMapper mapper = null;

	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	private final AtomicReference<OkHttpClient> okHttpClientAtomicReference = new AtomicReference<>();
	
	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;

	public static final PropertyDescriptor PROCESS_VARIABLE = new PropertyDescriptor.Builder().name("Process Variables")
			.description("Set Process variables").required(false).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
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
			.description("Channel Integration Header").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor FOOTER = new PropertyDescriptor.Builder().name("Footer")
			.description("Channel Integration footer").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor PV_MAPPING = new PropertyDescriptor.Builder().name("PV Mapping")
			.description("Set Process variables Mappinps").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CONTENT = new PropertyDescriptor.Builder().name("Content")
			.description("Channel Integration content").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CI_NAME = new PropertyDescriptor.Builder().name("CI Name")
			.description("Channel Integration Name").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor MAPPING = new PropertyDescriptor.Builder().name("Mapping")
			.description("Channel Integration Mapping").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor FILE_REJECT = new PropertyDescriptor.Builder().name("File Reject Path")
			.description("Channel Integration Record file path").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor ADDRESSED = new PropertyDescriptor.Builder().name("Addressed Path")
			.description("Current addressed").required(true).defaultValue("null")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RECORD_REJECT = new PropertyDescriptor.Builder().name("Record Reject Path")
			.description("Channel Integration Record Reject path").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor VALIDATION = new PropertyDescriptor.Builder().name("Validation")
			.description("Channel Integration Validation").required(true)
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
		properties.add(IDLE_CONNECTION_MAXPOOL_SIZE);
		properties.add(IDLE_CONNECTION_ALIVE_DURATION);

		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		this.relationships = Collections.unmodifiableSet(relationships);
		logger = context.getLogger();
		gson = new GsonBuilder().create();
		mapTypeObj = new TypeToken<Map<String, Object>>() {}.getType();
		mapper = new ObjectMapper();
	};

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}
	
	private String transactionStatusUrl = null;
	private String remoteURL = null;
	
	@OnScheduled
	public void onScheduled(final ProcessContext processContext) {
		okHttpClientAtomicReference.set(null);
		long connectionAliveDuration = processContext.getProperty(IDLE_CONNECTION_ALIVE_DURATION).asLong();
		int maxPoolSize = processContext.getProperty(IDLE_CONNECTION_MAXPOOL_SIZE).asInteger();
		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder();
		okHttpClientBuilder.connectionPool(new ConnectionPool(maxPoolSize, connectionAliveDuration, TimeUnit.SECONDS));
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
		boolean isSuccess = false;
		boolean isBatchSuccess = false;
		boolean isNoBatchSuccess = false;
		String isHeaderValid = null;
		String isFooterValid = null;
		String message = null;
		List<String> addressRecordLst = new ArrayList<String>();
		List<String> recordRejectLst = new ArrayList<String>();

		int receivedFilesCount = 1;
		int acceptedFilesCount = 0;
		int rejectedFilesCount = 0;
		int totalRecordsCount = 0;
		int acceptedRecordsCount = 0;
		int rejectedRecordsCount = 0;

		if (flowFileObj == null) {
			return;
		}

		String transactionId = generateTransactionId();
		processSession.putAttribute(flowFileObj, "transactionId", transactionId);
		FlowFile flowFileOutput = processSession.clone(flowFileObj);
		List<FlowFile> flowFileOutputList = new ArrayList<>();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

		String processVaribles = processContext.getProperty(PROCESS_VARIABLE).evaluateAttributeExpressions().getValue();
		String outputBeType = processContext.getProperty(OUTPUT_BE_NAME).evaluateAttributeExpressions().getValue();
		String remoteURL = processContext.getProperty(REMOTE_URL).evaluateAttributeExpressions().getValue();
		Boolean isBatchable = Boolean
				.parseBoolean(processContext.getProperty(ISBATCHABLE).evaluateAttributeExpressions().getValue());
		int batchSize = Integer
				.parseInt(processContext.getProperty(BATCH_SIZE).evaluateAttributeExpressions().getValue());
		String headerProperty = processContext.getProperty(HEADER).evaluateAttributeExpressions().getValue();
		String footerProperty = processContext.getProperty(FOOTER).evaluateAttributeExpressions().getValue();
		String pvMappingProperty = processContext.getProperty(PV_MAPPING).evaluateAttributeExpressions().getValue();
		String contentProperty = processContext.getProperty(CONTENT).evaluateAttributeExpressions().getValue();
		String ciNameProperty = processContext.getProperty(CI_NAME).evaluateAttributeExpressions().getValue();
		String mappingProperty = processContext.getProperty(MAPPING).evaluateAttributeExpressions().getValue();
		String fileRejectPath = processContext.getProperty(FILE_REJECT).evaluateAttributeExpressions().getValue();
		String addressedPath = processContext.getProperty(ADDRESSED).evaluateAttributeExpressions().getValue();
		String recordRejectPath = processContext.getProperty(RECORD_REJECT).evaluateAttributeExpressions().getValue();
		String validation = processContext.getProperty(VALIDATION).evaluateAttributeExpressions().getValue();

		try {

			Validation validationObj = null;
			List<String> inputFileData = null;
			List<Map<String, String>> contentResultList = new ArrayList<>();
			List<ProcessVariable> processVariables = new ArrayList<>();
			Map<String, Object> processMapBP = new LinkedHashMap<>();
			Map<String, Object> processMap = new LinkedHashMap<>();
			Map<String, Object> pvRunTime=new LinkedHashMap<>();
			CommonUtils.validateSessionId(processContext, processSession, null, SESSION_ID, logger);
			CommonUtils.validateRunNumber(processContext, processSession, null, RUN_NUMBER, logger);

			String sessionId = processContext.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
			String runNumber = processContext.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();

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

			Content content = null;

			try {
				content = mapper.readValue(contentProperty, Content.class);

			} catch (Exception e) {
				throw new NifiCustomException(
						ciNameProperty + ":Error occurred while reading Content property: " + e.getMessage());
			}

			List<CIMapping> pvMappingList = null;
			try {
				pvMappingList = Arrays.asList(mapper.readValue(pvMappingProperty, CIMapping[].class));

			} catch (Exception e) {
				throw new NifiCustomException(
						"Error occurred while reading PV Mapping Property value: " + e.getMessage());
			}

			try {
				validationObj = mapper.readValue(validation, Validation.class);

			} catch (Exception e) {
				logger.error("Error occurred at updateOperatorStatistics FileChannelInput :: ",e);
			}
			
			List<CIMapping> mappingList = null;
			try {
				mappingList = Arrays.asList(mapper.readValue(mappingProperty, CIMapping[].class));

			} catch (Exception e) {
				throw new NifiCustomException("Error occurred while reading mapping property: " + e.getMessage());
			}
			String fileName = flowFileObj.getAttribute("filename");
			// File Name Validation
			String fileNameValidate = validateFileName(sessionId, runNumber, fileName, validationObj, remoteURL);
			if (fileNameValidate.equalsIgnoreCase("Success")) {
				int fileHeaderLines = 0;
				int fileFooterLines = 0;
				List<String> headerInputFileData = new ArrayList<String>();
				List<String> footerInputFileData = new ArrayList<String>();
				List<Map<String, Object>> headerResultList = new ArrayList<>();
				List<Map<String, Object>> footerResultList = new ArrayList<>();

				InputStream fileInputStream = processSession.read(flowFileObj);
				inputFileData = fetchInputFileData(fileInputStream);
				try {

					if (header != null && header.isHasHeader()) {
						fileHeaderLines = header.getHeaderLines();

						int size = fileHeaderLines - 1;
						for (int i = 0; i <= size; i++) {
							headerInputFileData.add(inputFileData.get(i));
						}
						// To read the attribute name and get data from header
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

					contentResultList = performFixedWidthAction(inputFileData, fileHeaderLines, fileFooterLines,
							content, ciNameProperty, addressRecordLst, recordRejectLst);

					// Take headerResultList & contentResultList and validate
					// for MAX, MIN, COUNT, AVG, SUM

					if (contentResultList != null && contentResultList.size() > 0) {

						isHeaderValid = validateHeaderFooterContent(headerResultList, contentResultList, validationObj,
								"header");

						isFooterValid = validateHeaderFooterContent(footerResultList, contentResultList, validationObj,
								"footer");
					}

				} catch (Exception e) {
					isHeaderValid = "Failed";
					isFooterValid = "Failed";
					message = e.getMessage();

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
						try {

							processVariables = CommonUtils.convertJsonStringToJava(processVaribles, logger);

							if (processVariables != null && processVariables.size() > 0) {
								processMapBP = fetchProcessVariablesDetails(processVariables);
								processMap = updatePV(processMapBP, pvRunTime, headerResultList, footerResultList,
										pvMappingList);
							}

						} catch (Exception e) {
							throw new NifiCustomException(ciNameProperty
									+ " : Error occurred while reading Process Variable property: " + e.getMessage());
						}

						if (null != addressRecordLst && addressRecordLst.size() > 0) {
							writeToFile(addressRecordLst, addressedPath + "//" + fileName, addressedPath);
						}

						if (null != recordRejectLst && recordRejectLst.size() > 0) {
							writeToFile(recordRejectLst, recordRejectPath + "//" + fileName, recordRejectPath);
						}
						if (null != mappingList && contentResultList != null && contentResultList.size() > 0) {
							// map inputdata value to output be
							try{
							resultList = mappingDataToOuput(contentResultList, mappingList, processMap);
							}catch(Exception e){
								throw new NifiCustomException(e.getMessage());
							}
						}

						if (null != resultList && resultList.size() > 0) {
							if (isBatchable) {
								List<List<Map<String, String>>> batch = Lists.partition(resultList, batchSize);
								FlowFile flowFileBatchObject = null;
								for (List<Map<String, String>> currentObj : batch) {

									try {
										flowFileBatchObject = processSession.clone(flowFileObj);
										String transactionId_ = generateTransactionId();

										isBatchSuccess = writeFlowFileContent(processSession, currentObj,
												flowFileBatchObject, ciNameProperty);

										processSession.putAttribute(flowFileBatchObject, "transactionId", transactionId_);
										processSession.putAttribute(flowFileBatchObject, "beName", outputBeType);
										processSession.putAttribute(flowFileBatchObject, "channelId", ciNameProperty);
										processSession.putAttribute(flowFileBatchObject, "channelName", ciNameProperty);
										for (Map.Entry<String, Object> processMapObj : pvRunTime.entrySet()) {
											processSession.putAttribute(flowFileBatchObject, processMapObj.getKey(),
													toStr(processMapObj.getValue()));
										}
										flowFileOutputList.add(flowFileBatchObject);
										processSession.transfer(flowFileBatchObject, REL_SUCCESS);
									} catch (NifiCustomException nifiCustomException) {
										logger.error("Error occurred at Batch FileChannelInput :: "
												+ nifiCustomException.getMessage(), nifiCustomException);

										throw new NifiCustomException("Error occurred at Batch FileChannelInput :: "
												+ nifiCustomException.getMessage());
									}

								}
								isSuccess = true;
							} else {
								FlowFile flowFileNonBatchObject = processSession.clone(flowFileObj);

								isNoBatchSuccess = writeFlowFileContent(processSession, resultList,
										flowFileNonBatchObject, ciNameProperty);
								processSession.putAttribute(flowFileNonBatchObject, "beName", outputBeType);
								processSession.putAttribute(flowFileNonBatchObject, "channelId", ciNameProperty);
								processSession.putAttribute(flowFileNonBatchObject, "channelName", ciNameProperty);
								for (Map.Entry<String, Object> processMapObj : pvRunTime.entrySet()) {
									processSession.putAttribute(flowFileNonBatchObject, processMapObj.getKey(),
											toStr(processMapObj.getValue()));
								}
								flowFileOutputList.add(flowFileNonBatchObject);
								processSession.transfer(flowFileNonBatchObject, REL_SUCCESS);
								isSuccess = true;

							}

						} else {
							throw new NifiCustomException(
									ciNameProperty + " failed due no mapped content record to process ");
						}
					} else {

						rejectedFilesCount = 1;
						totalRecordsCount = inputFileData.size() - (fileHeaderLines + fileFooterLines);
						rejectedRecordsCount = totalRecordsCount;
						// File rejected HF validation
						updateOperatorStatistics(sessionId, runNumber, ciNameProperty, fileName, receivedFilesCount,
								acceptedFilesCount, rejectedFilesCount, totalRecordsCount, acceptedRecordsCount,
								rejectedRecordsCount);

						if (message != null) {
							inputFileData.add(message);
							writeToFile(inputFileData, fileRejectPath + "//" + fileName, fileRejectPath);
							throw new NifiCustomException(ciNameProperty + " failed due to exception ");

						} else {

							// File Reject
							inputFileData.add("Header information: " + isHeaderValid);
							inputFileData.add("Footer information: " + isFooterValid);
							writeToFile(inputFileData, fileRejectPath + "//" + fileName, fileRejectPath);
							throw new NifiCustomException(ciNameProperty + " failed due to header and footer failed");

						}

					}

				} else {
					rejectedFilesCount = 1;
					totalRecordsCount = inputFileData.size() - (header.getHeaderLines() + footer.getFooterLines());
					rejectedRecordsCount = totalRecordsCount;

					updateOperatorStatistics(sessionId, runNumber, ciNameProperty, fileName, receivedFilesCount,
							acceptedFilesCount, rejectedFilesCount, totalRecordsCount, acceptedRecordsCount,
							rejectedRecordsCount);

					inputFileData.add("No Accepted Records found in input file");
					writeToFile(inputFileData, fileRejectPath + "//" + fileName, fileRejectPath);

					if (null != recordRejectLst && recordRejectLst.size() > 0) {
						writeToFile(recordRejectLst, recordRejectPath + "\\" + fileName, recordRejectPath);
					}
					throw new NifiCustomException(ciNameProperty + " No Accepted Records found in input file ");

				}
			} else {

				InputStream fileInputStream = processSession.read(flowFileObj);
				inputFileData = fetchInputFileData(fileInputStream);

				rejectedFilesCount = 1;
				totalRecordsCount = inputFileData.size() - (header.getHeaderLines() + footer.getFooterLines());
				rejectedRecordsCount = totalRecordsCount;
				// File rejected FileName validation
				updateOperatorStatistics(sessionId, runNumber, ciNameProperty, fileName, receivedFilesCount,
						acceptedFilesCount, rejectedFilesCount, totalRecordsCount, acceptedRecordsCount,
						rejectedRecordsCount);

				inputFileData.add(fileNameValidate);
				writeToFile(inputFileData, fileRejectPath + "//" + fileName, fileRejectPath);
				throw new NifiCustomException(ciNameProperty + " File Name validation failed ");

			}

		} catch (NifiCustomException nifiCustomException) {
			for (FlowFile obj : flowFileOutputList) {
				processSession.remove(obj);
			}
			processSession.remove(flowFileOutput);
			logger.error("Error occurred at FileChannelInput :: " + nifiCustomException.getMessage(), nifiCustomException);
			processSession.putAttribute(flowFileObj, Constants.ERRORMESSAGE, nifiCustomException.getMessage());
			processSession.transfer(flowFileObj, REL_FAILURE);
			processSession.commit();
			return;
		} catch (Exception e) {

			logger.error("Error occurred at FileChannelInput :: " + e.getMessage(), e);
			for (FlowFile obj : flowFileOutputList) {
				processSession.remove(obj);
			}
			logger.error("Error occurred at FileChannelInput :: " + e.getMessage(), e);
			processSession.putAttribute(flowFileObj, Constants.ERRORMESSAGE, e.getMessage());
			processSession.remove(flowFileOutput);
			processSession.transfer(flowFileObj, REL_FAILURE);
			processSession.commit();
			return;

		}
		if (isSuccess) {

			processSession.remove(flowFileObj);
			processSession.remove(flowFileOutput);
			processSession.commit();

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
					logger.error("Error occurred at validateFileName FileChannelInput :: ",e);
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

	public List<Map<String, String>> mappingDataToOuput(List<Map<String, String>> inputData,
			List<CIMapping> mappingList, Map<String, Object> processMap) throws Exception {

		List<Map<String, String>> resultList = new ArrayList<>();

		if (mappingList != null) {

			// map inputdata value to output be

			for (Map<String, String> inMap : inputData) {

				Iterator<Entry<String, String>> inIt = inMap.entrySet().iterator();
				Map<String, String> finalMap = new LinkedHashMap<String, String>();

				for (CIMapping mappingValueType : mappingList) {
					//Type Conversion
					List<ConversionArray> typeConversionArrayObj = mappingValueType.getTypeConversionArray();
					if (mappingValueType.getType().equalsIgnoreCase("IBE")) {
						while (inIt.hasNext()) {

							Entry<String, String> ev = (Entry<String, String>) inIt.next();
							String key = ev.getKey();
							String value = ev.getValue();
							
							if (null != typeConversionArrayObj) {
								if (typeConversionArrayObj.size() >= 1) {
									try{
										value = MappingUtils.convertData(value, typeConversionArrayObj);
									}catch(Exception e){
										throw new Exception(e.getMessage());
									}
								}
							}
							for (CIMapping mappingValue : mappingList) {

								String from = mappingValue.getFromValue();
								String to = mappingValue.getToValue();

								if (key != null && key.toLowerCase().equals(from.toLowerCase())) {
									finalMap.put(to, value);
									break;
								}

							}

						}
					} else if (mappingValueType.getType().equalsIgnoreCase("EV")) {
						finalMap.put(mappingValueType.getToValue(), mappingValueType.getFromValue());
					} else if (mappingValueType.getType().equalsIgnoreCase("PV")) {
						String pvName = mappingValueType.getFromValue();
						String pvValue = null;
						if (processMap != null && processMap.containsKey(pvName)) {
							pvValue = toStr(processMap.get(pvName));
						}

						finalMap.put(mappingValueType.getToValue(), pvValue);

					}
				}

				if (finalMap.size() > 0) {
					resultList.add(finalMap);
				}
			}
		}
		return resultList;

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

	private List<Map<String, Object>> processHeader(List<String> inputFileHeaderData, Header header,
			int totalHeaderLines) throws NifiCustomException {

		List<Map<String, Object>> headerResultList = new ArrayList<>();

		List<FixedWidth> fixedWidth = null;

		try {
			fixedWidth = header.getFixedWidth();
			if (fixedWidth != null) {
				headerResultList = processHeaderFooterRecord(fixedWidth, inputFileHeaderData, "header");
			}
		} catch (Exception ex) {
			throw new NifiCustomException(ex.getMessage());

		}
		return headerResultList;
	}

	private List<Map<String, Object>> processFooter(List<String> inputFileFooterData, Footer footer,
			int totalFooterLines) throws NifiCustomException {
		List<Map<String, Object>> footerResultList = new ArrayList<>();
		List<FixedWidth> fixedWidth = null;

		try {
			fixedWidth = footer.getFixedWidth();
			if (fixedWidth != null) {
				footerResultList = processHeaderFooterRecord(fixedWidth, inputFileFooterData, "footer");
			}
		} catch (Exception ex) {
			throw new NifiCustomException(ex.getMessage());

		}
		return footerResultList;

	}

	private List<Map<String, Object>> processHeaderFooterRecord(List<FixedWidth> fixedWidthObj,
			List<String> inputFileData, String source) throws NifiCustomException {

		FormatAttributes formatAttributes = new FormatAttributes();
		List<Map<String, Object>> resultMapList = new ArrayList<>();
		int lineNumber = 0;
		for (String line : inputFileData) {
			lineNumber++;
			for (FixedWidth obj : fixedWidthObj) {
				if (lineNumber == obj.getLineNumber()) {
					int length = 0;
					String value = "";
					int startPos = obj.getStartingPoint();
					int width = obj.getWidth();
					length = startPos + width;
					String attributeName = obj.getAttributeName();
					try {
						if (startPos <= line.length() - 1 && line.length() >= length) {
							value = line.substring(startPos, length).trim();
						} else if (startPos <= line.length() - 1) {
							value = line.substring(startPos, line.length()).trim();
						}
					} catch (Exception e) {
						throw new NifiCustomException(
								"Unable to parse" + source + "for" + attributeName + "in line:" + line);

					}
					String dataType = obj.getDataType();
					formatAttributes = obj.getFormat();
					if (validateData(value, dataType, attributeName, formatAttributes)) {
						if ("DateTime".equalsIgnoreCase(dataType)) {
							value = convertDate(value, formatAttributes);
						} else if ("Boolean".equalsIgnoreCase(dataType)) {
							value = convertBoolean(value, formatAttributes);
						}
						Map<String, Object> resultMap = new HashMap<>();
						resultMap.put(attributeName, value);
						resultMapList.add(resultMap);
					} else {
						throw new NifiCustomException(source + "valiation failed for " + attributeName + " value- "
								+ value + " is not a valid " + dataType);

					}
				}

			}

		}

		return resultMapList;

	}

	public static Boolean validateData(String value, String dataType, String attName,
			FormatAttributes formatAttributes) {

		if ("String".equalsIgnoreCase(dataType) && value.matches("^[a-zA-Z0-9\\s]*$")) {

			return true;

		} else if ("Number".equalsIgnoreCase(dataType) && value.matches("^[\\d+\\s\\.]*$")) {
			if (null != formatAttributes.getScale() && StringUtils.isNotEmpty(formatAttributes.getScale())) {
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

	private String validateHeaderFooterContent(List<Map<String, Object>> resultList,
			List<Map<String, String>> contentResultList, Validation validationObj, String source)
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
			List<Map<String, Object>> resultList, List<Map<String, String>> contentResultList, String source)
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

			for (Map<String, String> contentObj : contentResultList) {

				currentValue = fetchContentTotalValue(contentObj, contentField);
				if (currentValue != null && toStr(currentValue).trim().length() > 0) {
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

	public void writeToFile(List<String> contentData, String filePath, String dirPath) {
		checkCreateDir(dirPath);
		File file = new File(filePath);
		FileWriter fr = null;
		BufferedWriter br = null;

		try {
			fr = new FileWriter(file);
			br = new BufferedWriter(fr);
			for (String data : contentData) {
				String dataWithNewLine = data + System.getProperty("line.separator");
				br.write(dataWithNewLine);
			}
		} catch (IOException e) {
			logger.error("Error occurred at writeToFile FileChannelInput :: ",e);
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				logger.error("Error occurred at writeToFile FileChannelInput :: ",e);
			}
		}

	}

	private BigDecimal fetchContentTotalValue(Map<String, String> contentObj, String contentField)
			throws NifiCustomException {

		BigDecimal currentValue = BigDecimal.ZERO;

		try {

			Iterator<Entry<String, String>> mapIterator = contentObj.entrySet().iterator();
			while (mapIterator.hasNext()) {
				Entry<String, String> ev = (Entry<String, String>) mapIterator.next();
				if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(contentField)) {

					String temp = (String) ev.getValue();
					if (temp != null) {
						currentValue = currentValue.add(new BigDecimal(temp));
						return currentValue;
					}
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

	private BigDecimal fetchHeaderFooterFieldValue(String fieldName, List<Map<String, Object>> resultList,
			String source, BigDecimal resultNumber) throws NifiCustomException {

		// resultList contains header or footer data
		try {

			for (Map<String, Object> currentData : resultList) {
				Iterator<Entry<String, Object>> mapIterator = currentData.entrySet().iterator();
				while (mapIterator.hasNext()) {
					Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();
					if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(fieldName)) {

						Object temp = ev.getValue();
						if (temp != null) {
							resultNumber = resultNumber.add(new BigDecimal(toStr(temp)));
							return resultNumber;
						}
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

	public List<Map<String, String>> performFixedWidthAction(List<String> inputFileData, int fileHeaderLines,
			int fileFooterLines, Content content, String ciNameProperty, List<String> addressRecordLst,
			List<String> recordRejectLst) throws NifiCustomException {

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> inputData = new ArrayList<Map<String, String>>();
		try {

			List<String> contentInputFileData = null;

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

				contentInputFileData = inputFileData.subList(fromIndex, toIndex);
				inputData = processContent(contentInputFileData, fileHeaderLines, fileFooterLines, content,
						addressRecordLst, recordRejectLst);
			}

			logger.info("inputData: " + inputData);
			logger.info("Result List " + resultList);

		}

		catch (Exception e) {
			throw new NifiCustomException("Error occurred while reading mapping property: " + e.getMessage());
		}

		return inputData;
	}

	public List<String> fetchInputFileData(InputStream fileInputStream)

			throws NifiCustomException {

		List<String> inputFileData = new ArrayList<String>();
		BufferedReader fileRead = null;

		try {

			fileRead = new BufferedReader(new InputStreamReader(fileInputStream, Constants.UTF_ENCODING));
			String line = fileRead.readLine();
			while (line != null) {

				inputFileData.add(line);
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

	private List<Map<String, String>> processContent(List<String> inputFileData, int fileHeaderLines,
			int fileFooterLines, Content content, List<String> addressRecordLst, List<String> recordRejectLst)
			throws NifiCustomException {

		List<FixedWidth> fixedWidthObj = null;
		List<String> bukStringList = new ArrayList<>();
		FormatAttributes formatAttributes = new FormatAttributes();
		List<Map<String, String>> contentOutputList = new ArrayList<>();

		try {

			if (content != null) {
				fixedWidthObj = content.getFixedWidth();
			}

			if (fixedWidthObj != null && fixedWidthObj.size() > 0) {

				for (String line : inputFileData) {
					Boolean dataValidate = true;
					String bukString = "";

					Map<String, String> mapObj = new HashMap<>();
					for (FixedWidth contentData : fixedWidthObj) {
						int length = 0;
						String value = "";
						int startPos = contentData.getStartingPoint();
						int width = contentData.getWidth();
						length = startPos + width;
						String attributeName = contentData.getAttributeName();
						String dataType = contentData.getDataType();
						try {
							if (startPos <= line.length() - 1 && line.length() >= length) {
								value = line.substring(startPos, length).trim();
							} else if (startPos <= line.length() - 1) {
								value = line.substring(startPos, line.length()).trim();
							}
						} catch (Exception e) {
							String rejectReason = "Error occurred while processing Content attribute: " + attributeName
									+ " with exception :" + e.getMessage();
							recordRejectLst.add(line.concat(rejectReason));
							break;
						}

						Boolean isBuk = contentData.isBuk();
						formatAttributes = contentData.getFormat();
						dataValidate = validateData(value, dataType, attributeName, formatAttributes);
						if (!dataValidate) {
							String rejectReason = "  " + attributeName + " value- " + value + " is not a valid "
									+ dataType;
							recordRejectLst.add(line.concat(rejectReason));
							break;

						} else {
							if (isBuk) {
								bukString = bukString.concat(value);
							}
							if ("DateTime".equalsIgnoreCase(dataType)) {
								value = convertDate(value, formatAttributes);
							} else if ("Boolean".equalsIgnoreCase(dataType)) {
								value = convertBoolean(value, formatAttributes);
							}
							mapObj.put(attributeName, value);
						}
					}

					if (dataValidate) {
						if (bukStringList.size() > 0 && bukStringList.contains(bukString)) {
							String rejectReason = "  Duplicate BUK value";
							recordRejectLst.add(line.concat(rejectReason));

						} else {
							if (null != bukString && "" != bukString)
								bukStringList.add(bukString);
							addressRecordLst.add(line);
							contentOutputList.add(mapObj);
						}
					}
				}

			}
			/*
			 * if (addressList.size() > 0) writeToFile(addressList,
			 * addressedPath + "//" + fileName); if (rejectList.size() > 0)
			 * writeToFile(rejectList, recordRejectPath + "//" + fileName);
			 */
		} catch (Exception e) {

			throw new NifiCustomException("Error occurred while processing Content: " + e.getMessage());

		}

		return contentOutputList;

	}

	public boolean writeFlowFileContent(final ProcessSession processSession, List<Map<String, String>> resultMappedList,
			FlowFile flowFileOutput, String ciName) throws NifiCustomException {
		OutputStream writeOutputStream = null;
		JsonWriter writer = null;
		boolean isSuccess = false;

		try {
			writeOutputStream = processSession.write(flowFileOutput);
			writer = new JsonWriter(new OutputStreamWriter(writeOutputStream, Constants.UTF_ENCODING));

			writer.beginArray();

			for (int i = 0; i < resultMappedList.size(); i++) {

				Map<String, String> resultMap = resultMappedList.get(i);
				gson.toJson(resultMap, mapTypeObj, writer);
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

	private void checkCreateDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists())
			file.mkdirs();

	}

	private String generateTransactionId() {
		return UUID.randomUUID().toString();
	}

	
	private String toStr(Object input) {
		return "" + (input == null ? "" : input);
	}
}