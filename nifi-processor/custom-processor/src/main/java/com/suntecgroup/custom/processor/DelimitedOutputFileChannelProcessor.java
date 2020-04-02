package com.suntecgroup.custom.processor;

/*
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.annotation.behavior.TriggerWhenEmpty;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.channelintegration.OperatorStats;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.ContentOutput;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.DelimitedAttributesOutput;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.DelimitedOutput;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.FooterOutput;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.HeaderOutput;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.OutputFile;
import com.suntecgroup.custom.processor.model.smartconnector.SmartMapping;
import com.suntecgroup.custom.processor.model.smartconnector.TypeConversion;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;
import com.suntecgroup.custom.processor.outputeviction.EvictionBin;
import com.suntecgroup.custom.processor.outputeviction.EvictionBinFiles;
import com.suntecgroup.custom.processor.outputeviction.EvictionBinManager;
import com.suntecgroup.custom.processor.utils.CommonUtils;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.SmartMappingUtils;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/*
 * This class is for creating a custom NiFi processor to handle the Delimiter Output File Channel Integration.
 * This class would read the Flow file content and its attributes to create and write an output file.
 *
 * @version 1.0 - April 2019
 * @author Mohammed Rizwan
 */

@Tags({ "Delimited, Output Channel Integration" })
@CapabilityDescription("Delimited File Output Processor for Channel Integration")
@TriggerWhenEmpty
public class DelimitedOutputFileChannelProcessor extends EvictionBinFiles {

	private ComponentLog logger;
	private ObjectMapper mapper = null;
	private Gson gson = null;
	private Type typeMap = null;
	
	RestTemplate restTemplate = new RestTemplate();
	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;
	final List<String> allIgnoreTypes = new ArrayList<String>();
	
	private final AtomicReference<OkHttpClient> okHttpClientAtomicReference = new AtomicReference<>();

	public static final PropertyDescriptor HEADER = new PropertyDescriptor.Builder().name("Header")
			.description("Delimited Output Channel Integration Header").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor FOOTER = new PropertyDescriptor.Builder().name("Footer")
			.description("Delimited Output Channel Integration footer").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CONTENT = new PropertyDescriptor.Builder().name("Content")
			.description("Delimited Output Channel Integration content").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor REMOTE_URL = new PropertyDescriptor.Builder().name("Remote URL")
			.description("Remote URL").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CI_NAME = new PropertyDescriptor.Builder().name("CI Name")
			.description("Delimited Output Channel Integration Name").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor MAPPING = new PropertyDescriptor.Builder().name("Mapping")
			.description("Delimited Output Channel Integration Mapping").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor EVICTION = new PropertyDescriptor.Builder().name("Eviction")
			.description("Delimited Output Channel Integration Eviction").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_FILEPATH = new PropertyDescriptor.Builder().name("Output FilePath")
			.description("Delimited Output File Path").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_FILENAME = new PropertyDescriptor.Builder().name("Output FileName")
			.description("Delimited Output File Name").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description("Success relationship").build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();

	public static final Relationship REL_STOPPED = new Relationship.Builder().name("Stopped")
			.description("Stopped relationship").build();

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
		properties.add(HEADER);
		properties.add(FOOTER);
		properties.add(CONTENT);
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		properties.add(REMOTE_URL);
		properties.add(CI_NAME);
		properties.add(MAPPING);
		properties.add(EVICTION);
		properties.add(OUTPUT_FILEPATH);
		properties.add(OUTPUT_FILENAME);
		properties.add(IDLE_CONNECTION_MAXPOOL_SIZE);
		properties.add(IDLE_CONNECTION_ALIVE_DURATION);
		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		relationships.add(REL_STOPPED);
		this.relationships = Collections.unmodifiableSet(relationships);
		logger = context.getLogger();
		
		allIgnoreTypes.add("root");
		allIgnoreTypes.add("subroot");
		allIgnoreTypes.add("beroot");
		allIgnoreTypes.add("besubroot");
		allIgnoreTypes.add("pvroot");
		allIgnoreTypes.add("pvsubroot");
		mapper = new ObjectMapper();
		gson = new GsonBuilder().create();
		typeMap = new TypeToken<Map<String, Object>>() {}.getType();
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
		transactionStatusUrl = remoteURL + "/bpruntime/sessionmanager/updateOperatorStats";
		mapper = new ObjectMapper();
	}
	@Override
	protected void processBin(EvictionBin bin, ProcessContext processContext)
			throws ProcessException, NifiCustomException {

		final ProcessSession processSession = bin.getSession();
		final List<FlowFile> contents = bin.getContents();

		if (CollectionUtils.isEmpty(contents)) {
			return;
		}

		/**
		 * 1) Received Records - Records received based on session id and
		 * runtime & OperatorName 2) Written Records - Records written to File.
		 * 3) Un-Written Records - Records not written to file. 4) Files written
		 * - number of files created.
		 */

		// For Admin Console
		int totalRecordsReceived = 0;
		int totalWrittenRecords = 0;
		int totalUnwrittenRecords = 0;
		int totalFilesWritten = 0;
		Map<String, Integer> statisticsMap = new LinkedHashMap<String, Integer>();
		statisticsMap.put("totalRecordsReceived", 0);
		statisticsMap.put("totalWrittenRecords", 0);
		statisticsMap.put("totalUnwrittenRecords", 0);
		statisticsMap.put("totalFilesWritten", 0);

		List<String> filesCreated = new ArrayList<String>();

		List<Map<String, Object>> outputFlowFileList = new ArrayList<Map<String, Object>>();

		String mappingProperty = processContext.getProperty(MAPPING).evaluateAttributeExpressions().getValue();
		String contentProperty = processContext.getProperty(CONTENT).evaluateAttributeExpressions().getValue();
		String ciNameProperty = processContext.getProperty(CI_NAME).evaluateAttributeExpressions().getValue();
		String outputFilePath = processContext.getProperty(OUTPUT_FILEPATH).evaluateAttributeExpressions().getValue();
		String outputFileNameProperty = processContext.getProperty(OUTPUT_FILENAME).evaluateAttributeExpressions()
				.getValue();

		String headerProperty = processContext.getProperty(HEADER).evaluateAttributeExpressions().getValue();
		String footerProperty = processContext.getProperty(FOOTER).evaluateAttributeExpressions().getValue();

		// String evictionProperty =
		// processContext.getProperty(EVICTION).evaluateAttributeExpressions().getValue();
		String remoteURL = processContext.getProperty(REMOTE_URL).evaluateAttributeExpressions().getValue();
		String sessionId = "";
		String runNumber = "";

		CommonUtils.validateSessionId(processContext, processSession, null, SESSION_ID, logger);
		CommonUtils.validateRunNumber(processContext, processSession, null, RUN_NUMBER, logger);

		sessionId = processContext.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
		sessionId = sessionId.trim();
		runNumber = processContext.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
		runNumber = runNumber.trim();

		OutputFile outputFile = null;
		try {
			outputFile = mapper.readValue(outputFileNameProperty, OutputFile.class);

		} catch (Exception e) {
			logger.error(ciNameProperty + ":Error occurred while reading outputJSON property " + e.getMessage(), e);

			throw new NifiCustomException(
					ciNameProperty + ":Error occurred while reading outputJSON property: " + e.getMessage());
		}

		if (bin.getEvictionType().equalsIgnoreCase("record")) {
			// this is time + record based or record eviction. Here we will have
			// single flowfile objects

			FlowFile flowFileObj = contents.get(0);
			try {
				int recordEviction = bin.getRecordEvictionCount();
				outputFlowFileList = getFlowFileContent(processSession, flowFileObj, ciNameProperty);
				totalRecordsReceived = outputFlowFileList.size();
				totalUnwrittenRecords = outputFlowFileList.size();
				statisticsMap.put("totalRecordsReceived", totalRecordsReceived);
				statisticsMap.put("totalUnwrittenRecords", totalUnwrittenRecords);

				List<List<Map<String, Object>>> recordBatch = Lists.partition(outputFlowFileList, recordEviction);
				for (List<Map<String, Object>> outputFlowFileListTemp : recordBatch) {
					// Proceed with the list field as per the time
					if (outputFlowFileListTemp.size() > 0) {
						beginProcess(flowFileObj, filesCreated, outputFlowFileListTemp, mappingProperty,
								contentProperty, ciNameProperty, outputFilePath, outputFile, statisticsMap,
								headerProperty, footerProperty);
					}
				}

			} catch (NifiCustomException nifiCustomException) {
				logger.error("Error occurred at FileOutputChannel :: " + nifiCustomException.getMessage(),
						nifiCustomException);
				removeFiles(filesCreated);
				totalWrittenRecords = 0;
				totalUnwrittenRecords = totalRecordsReceived;
				totalFilesWritten = 0;
				statisticsMap.put("totalWrittenRecords", totalWrittenRecords);
				statisticsMap.put("totalUnwrittenRecords", totalUnwrittenRecords);
				statisticsMap.put("totalFilesWritten", totalFilesWritten);
				updateOperatorStats(sessionId, runNumber, ciNameProperty, statisticsMap);
				processSession.transfer(flowFileObj, REL_FAILURE);
				processSession.commit();
				return;
			} catch (Exception e) {

				logger.error("Error occurred at FileOutputChannel :: " + e.getMessage(), e);
				removeFiles(filesCreated);
				totalWrittenRecords = 0;
				totalUnwrittenRecords = totalRecordsReceived;
				totalFilesWritten = 0;
				statisticsMap.put("totalWrittenRecords", totalWrittenRecords);
				statisticsMap.put("totalUnwrittenRecords", totalUnwrittenRecords);
				statisticsMap.put("totalFilesWritten", totalFilesWritten);
				updateOperatorStats( sessionId, runNumber, ciNameProperty, statisticsMap);
				processSession.transfer(flowFileObj, REL_FAILURE);
				processSession.commit();
				return;
			}

			updateOperatorStats(sessionId, runNumber, ciNameProperty, statisticsMap);
			processSession.transfer(flowFileObj, REL_SUCCESS);

		}

		else {
			// this is only time based or no eviction. Here we will have
			// multiple flowfile objects

			FlowFile flowFileObj = null;
			try {
				int count = 0;
				for (FlowFile flowObj : contents) {
					count++;
					if (count == 1) {
						flowFileObj = bin.getSession().clone(flowObj);
					}
					List<Map<String, Object>> outputFlowFileListTemp = getFlowFileContent(processSession, flowObj,
							ciNameProperty);
					if (outputFlowFileListTemp != null && outputFlowFileListTemp.size() > 0) {
						outputFlowFileList.addAll(outputFlowFileListTemp);
					}
					processSession.remove(flowObj);
				}

				totalRecordsReceived = totalRecordsReceived + outputFlowFileList.size();
				totalUnwrittenRecords = totalRecordsReceived + outputFlowFileList.size();
				statisticsMap.put("totalRecordsReceived", totalRecordsReceived);
				statisticsMap.put("totalUnwrittenRecords", totalUnwrittenRecords);
				beginProcess(flowFileObj, filesCreated, outputFlowFileList, mappingProperty, contentProperty,
						ciNameProperty, outputFilePath, outputFile, statisticsMap, headerProperty, footerProperty);
			} catch (NifiCustomException nifiCustomException) {
				logger.error("Error occurred at FileOutputChannel :: " + nifiCustomException.getMessage(),
						nifiCustomException);

				removeFiles(filesCreated);
				totalWrittenRecords = 0;
				totalUnwrittenRecords = totalRecordsReceived;
				totalFilesWritten = 0;
				statisticsMap.put("totalWrittenRecords", totalWrittenRecords);
				statisticsMap.put("totalUnwrittenRecords", totalUnwrittenRecords);
				statisticsMap.put("totalFilesWritten", totalFilesWritten);
				updateOperatorStats( sessionId, runNumber, ciNameProperty, statisticsMap);
				processSession.transfer(flowFileObj, REL_FAILURE);
				processSession.commit();
				return;
			} catch (Exception e) {

				logger.error("Error occurred at FileOutputChannel :: " + e.getMessage(), e);
				removeFiles(filesCreated);
				totalWrittenRecords = 0;
				totalUnwrittenRecords = totalRecordsReceived;
				totalFilesWritten = 0;
				statisticsMap.put("totalWrittenRecords", totalWrittenRecords);
				statisticsMap.put("totalUnwrittenRecords", totalUnwrittenRecords);
				statisticsMap.put("totalFilesWritten", totalFilesWritten);
				updateOperatorStats( sessionId, runNumber, ciNameProperty, statisticsMap);
				processSession.transfer(flowFileObj, REL_FAILURE);
				processSession.commit();
				return;
			}

			updateOperatorStats( sessionId, runNumber, ciNameProperty, statisticsMap);
			processSession.transfer(flowFileObj, REL_SUCCESS);
		}

		processSession.commit();
	}

	private void beginProcess(FlowFile flowFileObj, List<String> filesCreated,
			List<Map<String, Object>> outputFlowFileList, String mappingProperty, String contentProperty,
			String ciNameProperty, String outputFilePath, OutputFile outputFile, Map<String, Integer> statisticsMap,
			String headerProperty, String footerProperty) throws NifiCustomException, Exception {

		String staticValue = outputFile.getStaticName();
		String dynamicValue = outputFile.getDynamicName();
		String timeStampFormat = outputFile.getTimeStampFormat();

		List<String> outputHeaderList = new ArrayList<String>();
		List<String> outputFooterList = new ArrayList<String>();
		List<String> contentResultList = new ArrayList<String>();
		Map<String, String> flowFileAttributes = flowFileObj.getAttributes();

		HeaderOutput header = null;
		FooterOutput footer = null;
		List<Map<String, Object>> mappedContent = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			header = mapper.readValue(headerProperty, HeaderOutput.class);
		} catch (Exception e) {
			logger.error(ciNameProperty + ":Error occurred while reading Header property " + e.getMessage(), e);

			throw new NifiCustomException(
					ciNameProperty + ":Error occurred while reading Header property: " + e.getMessage());
		}

		try {
			footer = mapper.readValue(footerProperty, FooterOutput.class);
		} catch (Exception e) {
			logger.error(ciNameProperty + ":Error occurred while reading Footer property " + e.getMessage(), e);

			throw new NifiCustomException(
					ciNameProperty + ":Error occurred while reading Footer property: " + e.getMessage());
		}

		// Only Static
		if (staticValue != null && staticValue.trim().length() > 0
				&& (dynamicValue == null || (dynamicValue != null && dynamicValue.trim().length() < 1))) {

			String fileName = staticValue + "_" + CommonUtils.getCurrentTimeStampFormatted(timeStampFormat);

			if (outputFlowFileList != null && outputFlowFileList.size() > 0) {

				mappedContent = getCompositeMappedContent(outputFlowFileList, mappingProperty, ciNameProperty,
						flowFileAttributes, mapper, contentProperty);

				contentResultList = getContentDelimitedList(contentProperty, ciNameProperty, mapper, mappedContent);

				statisticsMap.put("totalWrittenRecords",
						statisticsMap.get("totalWrittenRecords") + contentResultList.size());
				statisticsMap.put("totalUnwrittenRecords",
						statisticsMap.get("totalUnwrittenRecords") - contentResultList.size());
			}

			if (header != null && header.isHasHeader()) {
				outputHeaderList = getHeaderDetails(flowFileObj, ciNameProperty, header, mappedContent);
			}

			if (footer != null && footer.isHasFooter()) {
				outputFooterList = getFooterDetails(flowFileObj, ciNameProperty, footer, mappedContent);
			}

			int totalFilesWritten = writeFile(outputHeaderList, outputFooterList, contentResultList, outputFilePath,
					fileName, filesCreated);
			statisticsMap.put("totalFilesWritten", statisticsMap.get("totalFilesWritten") + totalFilesWritten);
		}

		else {
			// for dynamic filename
			Map<String, List<Map<String, Object>>> groupedRealData = new TreeMap<String, List<Map<String, Object>>>();

			if (outputFlowFileList != null && outputFlowFileList.size() > 0) {
				boolean isContentDone = false;

				for (Map<String, Object> outputFlowFileObj : outputFlowFileList) {

					Object result = getRealTimeDynamicName(dynamicValue, outputFlowFileObj);

					String data = String.valueOf(result);

					if (data != null && data.trim().length() > 0) {

						if (!groupedRealData.containsKey(data)) {
							List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
							list.add(outputFlowFileObj);

							groupedRealData.put(data, list);
						} else {
							groupedRealData.get(data).add(outputFlowFileObj);
						}
					}

				}

				Iterator<Entry<String, List<Map<String, Object>>>> iterate = groupedRealData.entrySet().iterator();

				while (iterate.hasNext()) {
					isContentDone = true;
					String fileName = null;
					Entry<String, List<Map<String, Object>>> ev = (Entry<String, List<Map<String, Object>>>) iterate
							.next();

					String dynamicFileName = ev.getKey();
					List<Map<String, Object>> valueList = (List<Map<String, Object>>) ev.getValue();

					if (staticValue != null && staticValue.trim().length() > 0) {
						fileName = outputFile.getStaticName();
					}
					if (fileName == null) {
						fileName = dynamicFileName + "_" + CommonUtils.getCurrentTimeStampFormatted(timeStampFormat);
					} else {
						fileName = fileName + "_" + dynamicFileName + "_" + CommonUtils.getCurrentTimeStampFormatted(timeStampFormat);
					}

					if (valueList != null && valueList.size() > 0) {

						mappedContent = getCompositeMappedContent(valueList, mappingProperty, ciNameProperty,
								flowFileAttributes, mapper, contentProperty);

						contentResultList = getContentDelimitedList(contentProperty, ciNameProperty, mapper,
								mappedContent);

						statisticsMap.put("totalWrittenRecords",
								statisticsMap.get("totalWrittenRecords") + contentResultList.size());
						statisticsMap.put("totalUnwrittenRecords",
								statisticsMap.get("totalUnwrittenRecords") - contentResultList.size());

					} else {
						throw new NifiCustomException("Failed due to FlowFile Content is empty!");
					}

					if (header != null && header.isHasHeader()) {
						outputHeaderList = getHeaderDetails(flowFileObj, ciNameProperty, header, mappedContent);
					}

					if (footer != null && footer.isHasFooter()) {
						outputFooterList = getFooterDetails(flowFileObj, ciNameProperty, footer, mappedContent);
					}

					int totalFilesWritten = writeFile(outputHeaderList, outputFooterList, contentResultList,
							outputFilePath, fileName, filesCreated);
					statisticsMap.put("totalFilesWritten", statisticsMap.get("totalFilesWritten") + totalFilesWritten);

				}

				if (!isContentDone) {
					throw new NifiCustomException(
							"Failed due to FlowFile Content is empty for Dynamic attribute!" + dynamicValue);
				}

			} else {
				throw new NifiCustomException("Flowfile has empty content ");

			}
		}
	}

	private Object getRealTimeDynamicName(String dynamicValue, Map<String, Object> outputFlowFileObj)
			throws NifiCustomException {
		Object result = null;
		String[] split = dynamicValue.split("\\.");
		Map<String, Object> findHierarichyMap = outputFlowFileObj;

		for (int i = 0; i < split.length; i++) {
			String temp = split[i];

			if (temp != null && temp.trim().length() > 0 && !allIgnoreTypes.contains(temp.trim().toLowerCase())) {

				result = findHierarichyMap.get(temp);

				if (result != null && result instanceof Map) {
					findHierarichyMap = (Map<String, Object>) result;
				}
				if (result != null && result instanceof List) {
					throw new NifiCustomException("Dynamic path " + dynamicValue + " cannot be referenced to List");
				}
			}
		}
		return result;
	}

	private int writeFile(List<String> outputHeaderList, List<String> outputFooterList, List<String> contentResultList,
			String outputFilePath, String fileName, List<String> filesCreated) throws NifiCustomException {
		List<String> totalFileOutputList = new ArrayList<String>();

		int totalFilesWritten = 0;
		if (outputHeaderList != null && outputHeaderList.size() > 0) {
			totalFileOutputList.addAll(outputHeaderList);
		}

		if (contentResultList != null && contentResultList.size() > 0) {
			totalFileOutputList.addAll(contentResultList);
		}

		if (outputFooterList != null && outputFooterList.size() > 0) {
			totalFileOutputList.addAll(outputFooterList);
		}

		if (totalFileOutputList != null && totalFileOutputList.size() > 0) {
			writeToFile(totalFileOutputList, outputFilePath, fileName, filesCreated);
			totalFilesWritten = totalFilesWritten + 1;
		}
		return totalFilesWritten;
	}

	private List<String> getContentDelimitedList(String contentProperty, String ciNameProperty, ObjectMapper mapper,
			List<Map<String, Object>> mappedContent) throws NifiCustomException {

		List<String> contentResultList = new ArrayList<String>();
		ContentOutput content = null;
		DelimitedOutput delimited = null;

		try {
			content = mapper.readValue(contentProperty, ContentOutput.class);
			if (content != null) {
				delimited = content.getDelimited();

			}

		} catch (Exception e) {
			logger.error(ciNameProperty + ":Error occurred while reading Content property " + e.getMessage(), e);

			throw new NifiCustomException(
					ciNameProperty + ":Error occurred while reading Content property: " + e.getMessage());
		}

		// Mappedresult list will be converted to list of strings with root and
		// attribute delimiters

		if (mappedContent != null && mappedContent.size() > 0) {

			String rootDelimited = delimited.getRecord();
			String rootAttributeDelimiter = delimited.getAttribute();
			List<DelimitedAttributesOutput> delimitedAttributes = delimited.getAttributes();

			for (Map<String, Object> currentMappedObj : mappedContent) {

				StringBuilder sb = new StringBuilder();

				for (DelimitedAttributesOutput contentObj : delimitedAttributes) {
					String currentNode = contentObj.getCurrentNode();
					String parentNode = contentObj.getParentNode();
					String attributeName = contentObj.getAttributeName();

					// only root is done here
					if ("root".equalsIgnoreCase(currentNode) && "".equalsIgnoreCase(parentNode.trim())) {

						String dataType = contentObj.getDataType();

						if (dataType != null && dataType.equalsIgnoreCase("object")) {

							Map<String, String> attributeRecordSplitterMap = contentObj.getAttributeType()
									.getDelimiter();
							String innerRecordSplit = attributeRecordSplitterMap.get("record");
							String innerAttributeSplit = attributeRecordSplitterMap.get("attribute");

							Map<String, Object> innerInputRecord = (Map) currentMappedObj.get(attributeName);

							Object value = fetchDelimitedObjectValue(attributeName, currentNode, delimitedAttributes,
									innerInputRecord, innerRecordSplit, innerAttributeSplit, "object");
							sb.append(value);
							sb.append(rootAttributeDelimiter);
						}
						else if (dataType != null && dataType.equalsIgnoreCase("array")) {
							Map<String, String> attributeRecordSplitterMap = contentObj.getAttributeType()
									.getDelimiter();
							String innerRecordSplit = attributeRecordSplitterMap.get("record");
							String innerAttributeSplit = attributeRecordSplitterMap.get("attribute");

							List<Map<String, Object>> innerInputRecordList = (List) currentMappedObj.get(attributeName);

							if (null != innerInputRecordList) {
								StringBuilder sbInner = new StringBuilder();
								for (Map<String, Object> inputObj : innerInputRecordList) {
									Object value = fetchDelimitedObjectValue(attributeName, currentNode,
											delimitedAttributes, inputObj, innerRecordSplit, innerAttributeSplit,
											"array");
									sbInner.append(value);
								}
								sb.append(sbInner.toString());
								sb.append(rootAttributeDelimiter);
							}
						} else {
							Object value = currentMappedObj.get(attributeName);
							sb.append(value);
							sb.append(rootAttributeDelimiter);
						}
					}
				}

				if (sb != null && sb.length() > 0) {
					String temp = sb.toString();
					String result = temp.substring(0, temp.length() - 1);
					result = result + rootDelimited;
					contentResultList.add(result);
				}
			}
		}

		return contentResultList;
	}

	private List<Map<String, Object>> getCompositeMappedContent(List<Map<String, Object>> outputFlowFileList,
			String mappingProperty, String ciNameProperty, Map<String, String> flowFileAttributes, ObjectMapper mapper,
			String contentProperty) throws NifiCustomException {

		List<SmartMapping> smartMappingList = null;
		try {
			smartMappingList = Arrays.asList(mapper.readValue(mappingProperty, SmartMapping[].class));

		} catch (Exception e) {
			logger.error(ciNameProperty + ":Error occurred while reading mapping property " + e.getMessage(), e);

			throw new NifiCustomException("Error occurred while reading mapping property: " + e.getMessage());
		}

		// composite logic begins here

		List<Map<String, Object>> mappedContent = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> resultOutDenormLst = new ArrayList<>();
		Map<String, Object> refinedOutputStructureMap = convertContentToOutputStructure(contentProperty);
		Map<String, Object> temp = new LinkedHashMap<String, Object>();

		// Root level denormalization
		List<String> rootDenorMainArrayLst = smartMappingList.stream()
				.filter(t -> t.getToCurrentNode().equalsIgnoreCase("") && t.getDataType().equalsIgnoreCase("Array")
						&& "root".equalsIgnoreCase(t.getFromPath().split("-")[2]))
				.map(SmartMapping::getFromValue).collect(Collectors.toList());
		// Sub level denormalization
		List<String> subMainArrayLst = smartMappingList.stream().filter(t -> t.getToDataType().equalsIgnoreCase("Array"))
				.map(SmartMapping::getToValue).collect(Collectors.toList());
		Set<String> subDenorMainArrayLst = subMainArrayLst.stream()
				.filter(n -> subMainArrayLst.stream().filter(x -> x.equalsIgnoreCase(n)).count() > 1)
				.collect(Collectors.toSet());
		
		if (rootDenorMainArrayLst.size() >= 1) {
			try {
				Map<String, List<SmartMapping>> denormMap = SmartMappingUtils.fetch(rootDenorMainArrayLst, smartMappingList);
				List<SmartMapping> priAttrList = smartMappingList.stream()
						.filter(t -> t.getFromPath().split("-").length == 2 || t.getFromPath().split("-").length == 3)
						.filter(t -> !t.getDataType().equalsIgnoreCase("array")
								&& !t.getDataType().equalsIgnoreCase("object"))
						.collect(Collectors.toList());
				for (Map<String, Object> inputBERecord : outputFlowFileList) {
					resultOutDenormLst = SmartMappingUtils.inisiateRootDenorm(inputBERecord, smartMappingList, refinedOutputStructureMap,
							rootDenorMainArrayLst, priAttrList, denormMap);
					mappedContent.addAll(resultOutDenormLst);
				}
			} catch (Exception e) {
				logger.error("Error occurred at 'inisiateRootDenorm' for Root Denormalization :: ", e);
			}
		} else {
			for (Map<String, Object> currentRecord : outputFlowFileList) {
				try {
					temp = initiateCompositeBEMapping(currentRecord, ciNameProperty, flowFileAttributes, smartMappingList,
							refinedOutputStructureMap, subDenorMainArrayLst);
				} catch (Exception e) {
					logger.error("Error occurred at initiateCompositeBEMapping :: ", e);
				}
	
				if (temp != null && temp.size() > 0) {
					mappedContent.add(temp);
				}
			}
		}
		return mappedContent;
	}

	private Object fetchDelimitedObjectValue(String currentNodeAttr, String parentNodeAttr,
			List<DelimitedAttributesOutput> delimitedAttributes, Map<String, Object> innerInputRecord,
			String innerRecordSplit, String innerAttributeSplit, String source) {

		StringBuilder sb = new StringBuilder();

		Map currentMappedObj = innerInputRecord;

		for (DelimitedAttributesOutput contentObj : delimitedAttributes) {
			String currentNode = contentObj.getCurrentNode();
			String parentNode = contentObj.getParentNode();
			String attributeName = contentObj.getAttributeName();

			// only root is done here
			if (currentNodeAttr.equalsIgnoreCase(currentNode) && parentNodeAttr.equalsIgnoreCase(parentNode.trim())) {

				String dataType = contentObj.getDataType();

				if (dataType != null && dataType.equalsIgnoreCase("object")) {

					Map<String, String> attributeRecordSplitterMap = contentObj.getAttributeType().getDelimiter();
					String innerRecordSplitTemp = attributeRecordSplitterMap.get("record");
					String innerAttributeSplitTemp = attributeRecordSplitterMap.get("attribute");

					Map<String, Object> innerInputRecordTemp = (Map) currentMappedObj.get(attributeName);

					if (null != innerInputRecordTemp) {
						Object value = fetchDelimitedObjectValue(attributeName, currentNode, delimitedAttributes,
								innerInputRecordTemp, innerRecordSplitTemp, innerAttributeSplitTemp, "object");
						sb.append(value).append(innerAttributeSplit);
					}
				}

				else if (dataType != null && dataType.equalsIgnoreCase("array")) {

					Map<String, String> attributeRecordSplitterMap = contentObj.getAttributeType().getDelimiter();
					String innerRecordSplitTemp = attributeRecordSplitterMap.get("record");
					String innerAttributeSplitTemp = attributeRecordSplitterMap.get("attribute");

					List<Map<String, Object>> innerInputRecordList = (List) currentMappedObj.get(attributeName);

					if (null != innerInputRecordList) {
						StringBuilder sbInner = new StringBuilder();
						for (Map<String, Object> inputObj : innerInputRecordList) {
							Object value = fetchDelimitedObjectValue(attributeName, currentNode, delimitedAttributes,
									inputObj, innerRecordSplitTemp, innerAttributeSplitTemp, "array");
							sbInner.append(value);
						}
						sb.append(sbInner.toString()).append(innerAttributeSplit);
					}
				} else {

					Object value = currentMappedObj.get(attributeName);
					sb.append(value).append(innerAttributeSplit);

				}
			}
		}

		String result = null;

		if (sb != null && sb.length() > 0) {
			result = sb.substring(0, sb.length() - 1);
			if (source.equalsIgnoreCase("array")) {
				result = result + innerRecordSplit;
			}

		}

		return result;
	}

	public List<Map<String, Object>> getFlowFileContent(final ProcessSession processSession, FlowFile flowFileObj,
			String ciName) throws NifiCustomException {

		List<Map<String, Object>> outputFlowFileList = new ArrayList<Map<String, Object>>();
		JsonReader reader = null;
		InputStream outputChannelInput = null;
		try {

			outputChannelInput = processSession.read(flowFileObj);

			if (outputChannelInput != null) {
				reader = new JsonReader(new InputStreamReader(outputChannelInput, Constants.UTF_ENCODING));
				reader.beginArray();

				while (reader.hasNext()) {
					Map<String, Object> jsonRecord = gson.fromJson(reader, typeMap);
					outputFlowFileList.add(jsonRecord);
				}

				reader.endArray();
			}
		} catch (Exception e) {
			logger.error("Exception while reading Flowfile content " + e.getMessage(), e);

			throw new NifiCustomException("Failed: Exception while reading FlowFile Content");

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (outputChannelInput != null) {
					outputChannelInput.close();
				}

			} catch (IOException e) {
				logger.error(ciName + " IOException while closing stream");
			}
		}
		return outputFlowFileList;
	}

	public List<String> getHeaderDetails(FlowFile flowFileObj, String ciName, HeaderOutput header,
			List<Map<String, Object>> mappedContent) throws NifiCustomException {

		List<String> resultList = new ArrayList<String>();
		DelimitedOutput delimitedObj = header.getDelimited();
		int maximumLines = header.getHeaderLines();
		resultList = fetchHeaderFooterValues(flowFileObj, delimitedObj, maximumLines, mappedContent);
		return resultList;
	}

	public List<String> getFooterDetails(FlowFile flowFileObj, String ciName, FooterOutput footer,
			List<Map<String, Object>> mappedContent) throws NifiCustomException {

		List<String> resultList = new ArrayList<String>();
		DelimitedOutput delimitedObj = footer.getDelimited();
		int maximumLines = footer.getFooterLines();
		resultList = fetchHeaderFooterValues(flowFileObj, delimitedObj, maximumLines, mappedContent);
		return resultList;
	}

	private List<String> fetchHeaderFooterValues(FlowFile flowFileObj, DelimitedOutput delimitedObj, int maximumLines,
			List<Map<String, Object>> mappedContent) throws NifiCustomException {

		List<String> resultList = new ArrayList<String>();
		Map<String, String> flowFileAttributes = flowFileObj.getAttributes();
		String rootRecordDelimiter = delimitedObj.getRecord();
		String rootAttributeDelimiter = delimitedObj.getAttribute();
		int rootLineCount = 1;

		List<DelimitedAttributesOutput> delimitedAttributeList = delimitedObj.getAttributes();
		Map<Integer, Map<Integer, Object>> lineValue = new TreeMap<Integer, Map<Integer, Object>>();
		List<String> mathematicalOperations = new ArrayList<String>();

		mathematicalOperations.add("count");
		mathematicalOperations.add("min");
		mathematicalOperations.add("max");
		mathematicalOperations.add("sum");
		mathematicalOperations.add("avg");

		// The goal is to first map the attributes to the specific position
		// in the line number and then create a string list with
		// delimited attributes

		while (rootLineCount <= maximumLines) {

			String temp = prepareHeaderFooterLogic(mappedContent, flowFileAttributes, rootRecordDelimiter,
					rootAttributeDelimiter, rootLineCount, delimitedAttributeList, mathematicalOperations, "root", "",
					null, null, "root");

			resultList.add(temp);
			rootLineCount++;
		}

		return resultList;
	}

	private String prepareHeaderFooterLogic(List<Map<String, Object>> mappedContent,
			Map<String, String> flowFileAttributes, String rootRecordDelimiter, String rootAttributeDelimiter,
			int currentLine, List<DelimitedAttributesOutput> delimitedAttributeList,
			List<String> mathematicalOperations, String currentNodeAttr, String parentNodeAttr,
			Map<String, Object> pvObjectRecord, String skipValue, String source) throws NifiCustomException {

		StringBuilder sb = new StringBuilder();
		String resultStr = null;

		for (DelimitedAttributesOutput daObj : delimitedAttributeList) {

			String currentNode = daObj.getCurrentNode();
			String parentNode = daObj.getParentNode();
			String type = daObj.getType();
			String attributeName = daObj.getAttributeName();
			String dataType = daObj.getDataType();
			String value = daObj.getValue();

			int lineNumber = daObj.getLineNumber();

			if (lineNumber == currentLine) {

				if (currentNode != null && currentNode.trim().equalsIgnoreCase(currentNodeAttr) && parentNode != null
						&& parentNode.trim().equalsIgnoreCase(parentNodeAttr)) {
					Object result = null;

					if (type != null && type.equalsIgnoreCase("pv")) {

						if (skipValue != null) {
							// for array since we already pull and pass the
							// inner array value. skip the path
							value = value.replace(skipValue, "");
						}

						if (dataType != null && dataType.equalsIgnoreCase("object")) {

							// value has pv as an object name.. Fetch the Object
							Map<String, String> delimiter = daObj.getAttributeType().getDelimiter();
							String record = delimiter.get("record");
							String attribute = delimiter.get("attribute");

							Map<String, Object> pvObjectRecordOriginal = getObjectfromFlowFileAttributes(
									flowFileAttributes, value);

							result = prepareHeaderFooterLogic(mappedContent, flowFileAttributes, record, attribute,
									currentLine, delimitedAttributeList, mathematicalOperations, attributeName,
									currentNode, pvObjectRecordOriginal, null, "object");
						}

						else if (dataType != null && dataType.equalsIgnoreCase("array")) {

							Map<String, String> delimiter = daObj.getAttributeType().getDelimiter();
							String record = delimiter.get("record");
							String attribute = delimiter.get("attribute");

							Map<String, Object> pvArrayRecordOriginal = getObjectfromFlowFileAttributes(
									flowFileAttributes, value);

							String getPathWithoutAttribute = getPathWithoutAttribute(value);
							List<Map<String, Object>> listObjectFromPV = getArrayObjectFromPV(getPathWithoutAttribute,
									pvArrayRecordOriginal);
							StringBuilder sbInner = new StringBuilder();
							Object innerResult = null;

							for (Map<String, Object> pvList : listObjectFromPV) {
								innerResult = prepareHeaderFooterLogic(mappedContent, flowFileAttributes, record,
										attribute, currentLine, delimitedAttributeList, mathematicalOperations,
										attributeName, currentNode, pvList, getPathWithoutAttribute, "array");
								sbInner.append(innerResult);
							}

							result = sbInner.toString();

						}

						else {
							// root primitive this attribute is taken from Pv as
							// an Attribute
							if (currentNode != null && currentNode.trim().equalsIgnoreCase("root")) {
								result = getDatafromFlowFileAttributes(flowFileAttributes, value);
							} else {
								result = getValueFromPVPath(value, pvObjectRecord);
							}
						}

					}

					else if (type != null && type.equalsIgnoreCase("ev")) {
						result = value;

					}

					else if (type != null && mathematicalOperations.contains(type.trim())) {
						result = getMathematicalResult(value, mappedContent, type);
					}

					sb.append(result).append(rootAttributeDelimiter);
				}
			}
		}

		if (sb != null && sb.length() > 0) {

			String temp = sb.toString();
			resultStr = temp.substring(0, temp.length() - 1);
			if (!source.equalsIgnoreCase("object")) {
				resultStr = resultStr + rootRecordDelimiter;
			}
		}

		return resultStr;
	}

	private List<Map<String, Object>> getArrayObjectFromPV(String getPathWithoutAttribute,
			Map<String, Object> pvObjectRecord) {
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();

		if (null != getPathWithoutAttribute) {
			String[] split = getPathWithoutAttribute.split("\\.");
			Object result = null;
			Map findHierarichyMap = pvObjectRecord;

			for (String temp : split) {
				result = findHierarichyMap.get(temp);
				if (result instanceof Map) {
					findHierarichyMap = (Map<String, Object>) result;
				} else if (result instanceof List) {
					resultMap = (List<Map<String, Object>>) result;
					return resultMap;
				}
			}
		}
		return resultMap;
	}

	private String getPathWithoutAttribute(String fromPath) {
		String result = null;

		String[] split = fromPath.split("\\.");

		// skipping last entry
		for (int i = 0; i < split.length - 1; i++) {

			String temp = split[i];

			if (result == null) {
				result = temp;
			} else {
				result = result + temp;
			}
		}

		return result;
	}

	private Object getValueFromPVPath(String fromPath, Map<String, Object> pvObjectRecord) throws NifiCustomException {

		Map<String, Object> findHierarichyMap = pvObjectRecord;

		Object result = null;
		String[] split = fromPath.split("\\.");

		for (int i = 0; i < split.length; i++) {

			String temp = split[i];

			if (temp != null && temp.trim().length() > 0 && !allIgnoreTypes.contains(temp.trim().toLowerCase())) {

				result = findHierarichyMap.get(temp);

				if (result != null && result instanceof Map) {
					findHierarichyMap = (Map<String, Object>) result;
				} else if (result != null && result instanceof List) {
					return "";
				}
			}
		}

		return result;
	}

	private Object getMathematicalResult(String inputPath, List<Map<String, Object>> mappedContent, String functionName)
			throws NifiCustomException {
		Object result = null;

		if (functionName != null && functionName.trim().length() > 0) {

			List<String> contentValues = fetchContentValue(inputPath, mappedContent);

			if (contentValues != null && contentValues.size() > 0) {

				if ("count".equalsIgnoreCase(functionName.trim().toLowerCase())) {
					result = contentValues.size();
				} else {

					List<BigDecimal> newContentValues = contentValues.stream().map(temp -> {
						BigDecimal obj = new BigDecimal(temp.toString());
						return obj;
					}).collect(Collectors.toList());

					Collections.sort(newContentValues);

					switch (functionName.trim().toLowerCase()) {
					case "avg":
						result = getAverage(newContentValues);
						break;
					case "max":
						result = newContentValues.get(newContentValues.size() - 1);
						break;
					case "min":
						result = newContentValues.get(0);
						break;
					case "sum":
						result = getSum(newContentValues);
						break;
					default:
						throw new NifiCustomException(
								"Failed: Due to unknown relational condition  " + functionName + " for calculation");
					}
				}
			}

		}

		return result;

	}

	private BigDecimal getSum(List<BigDecimal> contentValues) throws NifiCustomException {

		BigDecimal sumContentValue = BigDecimal.ZERO;

		for (BigDecimal currentValue : contentValues) {
			sumContentValue = sumContentValue.add(currentValue);
		}

		return sumContentValue;
	}

	private BigDecimal getAverage(List<BigDecimal> contentValues) throws NifiCustomException {

		int totalrecordCount = contentValues.size();
		BigDecimal sumContentValue = getSum(contentValues);

		BigDecimal averageContent = sumContentValue.divide(new BigDecimal(totalrecordCount), 2, RoundingMode.HALF_UP);

		return averageContent;
	}

	private List<String> fetchContentValue(String inputPath, List<Map<String, Object>> mappedContent)
			throws NifiCustomException {
		List<String> resultList = new ArrayList<String>();

		// All the records in the contentList is proccessed

		for (Map<String, Object> record : mappedContent) {
			List<String> value = getContentNestedValue(inputPath, record);
			resultList.addAll(value);
		}

		return resultList;
	}

	private List<String> getContentNestedValue(String inputPath, Map<String, Object> record)
			throws NifiCustomException {

		List<String> resList = new ArrayList<String>();

		Object result = null;
		Map<String, Object> findHierarichyMap = record;
		String skipPath = null;
		String[] split = inputPath.split("\\.");

		try {
			for (int i = 0; i < split.length; i++) {
				String temp = split[i];

				if (skipPath == null) {
					skipPath = temp;
				} else {
					skipPath = skipPath + "." + temp;
				}

				if (temp != null && temp.trim().length() > 0 && !allIgnoreTypes.contains(temp.trim().toLowerCase())) {

					result = findHierarichyMap.get(temp);

					if (result != null && result instanceof Map) {
						findHierarichyMap = (Map<String, Object>) result;
					} else if (result != null && result instanceof List) {

						List<Map<String, Object>> innerList = (List<Map<String, Object>>) result;
						String newPath = inputPath.replace(skipPath, "");

						for (Map<String, Object> innerRecord : innerList) {
							List<String> innerResultList = getContentNestedValue(newPath, innerRecord);
							if (innerResultList != null && innerResultList.size() > 0) {
								resList.addAll(innerResultList);
							}

						}
						return resList;
					}
				}
			}

			if (result != null) {
				resList.add(result.toString());
			}

		} catch (Exception ex) {
			logger.error("Exception occured while fetching contentvalue for header/footer mathematical calculation "
					+ ex.getMessage(), ex);

			throw new NifiCustomException(
					"Exception occured while fetching contentvalue for header/footer mathematical calculation "
							+ ex.getMessage());
		}

		return resList;
	}

	public void writeToFile(List<String> data, String filePath, String fileName, List<String> filesCreated)
			throws NifiCustomException {

		String txtFilePath = filePath + File.separator + fileName + ".txt";
		OutputStreamWriter outputStream = null;
		BufferedWriter bufferedWriterObject = null;

		try {

			File directory = new File(filePath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			outputStream = new OutputStreamWriter(new FileOutputStream(txtFilePath), StandardCharsets.UTF_8);
			bufferedWriterObject = new BufferedWriter(outputStream);
			filesCreated.add(txtFilePath);
			int lastIndex = data.size() - 1;
			int i = 0;
			String dataWithNewLine;
			for (String data1 : data) {
				if (i == lastIndex) {
					if (data1.endsWith("\\n")) {
						dataWithNewLine = data1.substring(0, data1.length() - 2);
					} else {
						dataWithNewLine = data1;
					}
				} else {
					if (data1.endsWith("\\n")) {
						dataWithNewLine = data1.substring(0, data1.length() - 2) + "\n";
					} else {
						dataWithNewLine = data1;
					}
				}
				bufferedWriterObject.write(dataWithNewLine);
				bufferedWriterObject.flush();
				i++;
			}
		} catch (IOException e) {
			throw new NifiCustomException("Failed while writing file!");
		} finally {
			try {
				if (bufferedWriterObject != null) {
					bufferedWriterObject.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				String doneFile = filePath + File.separator + fileName + ".txt" + ".done";
				File emptyFile = new File(doneFile);
				FileWriter emptyFileWriter = new FileWriter(emptyFile);
				emptyFileWriter.close();
				filesCreated.add(doneFile);

			} catch (IOException e) {
				logger.error("Failed while creating .done file!");
			}
		}

	}

	public void removeFiles(List<String> fileNameList) {

		for (String fileName : fileNameList) {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	public void updateOperatorStats(String sessionId, String runNumber, String operatorName,
			Map<String, Integer> statisticsMap) {

		OperatorStats operatorStats = new OperatorStats();
		operatorStats.setSessionId(sessionId);
		operatorStats.setRunNumber(runNumber);
		operatorStats.setOperatorName(operatorName);
		operatorStats.setTotalRecordsReceived(statisticsMap.get("totalRecordsReceived"));
		operatorStats.setTotalWrittenRecords(statisticsMap.get("totalWrittenRecords"));
		operatorStats.setTotalUnwrittenRecords(statisticsMap.get("totalUnwrittenRecords"));
		operatorStats.setTotalFilesWritten(statisticsMap.get("totalFilesWritten"));
		OkHttpClient okHttpClient = okHttpClientAtomicReference.get();
		Request.Builder requestBuilder = new Request.Builder();
		requestBuilder = requestBuilder.url(transactionStatusUrl);
		try {
			requestBuilder = requestBuilder.post(RequestBody.create(MediaType.parse(DEFAULT_CONTENT_TYPE), mapper.writeValueAsString(operatorStats)));
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

	private Object getDatafromFlowFileAttributes(Map<String, String> flowFileAttributeObj, String requiredAttribute)
			throws NifiCustomException {

		String processVariableStr = (String) flowFileAttributeObj.get(requiredAttribute);
		ObjectMapper mapper = new ObjectMapper();

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
				logger.error("Error occurred while reading flowfile attributes " + e.getMessage(), e);
				throw new NifiCustomException("Error occurred while reading flowfile Attributes: " + e.getMessage());
			}
		}
		return null;
	}

	private Map<String, Object> convertContentToOutputStructure(String contentProperty) {

		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

		ContentOutput content = null;
		DelimitedOutput delimited = null;
		ObjectMapper objMapper = new ObjectMapper();

		try {
			content = objMapper.readValue(contentProperty, ContentOutput.class);
			if (content != null) {
				delimited = content.getDelimited();

			}

		} catch (Exception e) {
			logger.error("Error occurred while reading Content property " + e.getMessage(), e);
		}

		if (delimited != null) {
			resultMap = getObjectArrayStructure(delimited, "root", "");
		}

		return resultMap;

	}

	private Map<String, Object> getObjectArrayStructure(DelimitedOutput delimited, String currentNodeRoot,
			String parentNodeRoot) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

		if (delimited != null) {
			List<DelimitedAttributesOutput> demList = delimited.getAttributes();

			for (DelimitedAttributesOutput delimitedObj : demList) {

				String type = delimitedObj.getType();
				String dataType = delimitedObj.getDataType();
				String currentNode = delimitedObj.getCurrentNode();
				String parentNode = delimitedObj.getParentNode();
				String attributeName = delimitedObj.getAttributeName();

				if (currentNode != null && currentNode.equalsIgnoreCase(currentNodeRoot) && parentNode != null
						&& parentNode.equalsIgnoreCase(parentNodeRoot)) {

					if (dataType != null && dataType.equalsIgnoreCase("Object")) {
						Map<String, Object> temp = getObjectArrayStructure(delimited, attributeName, currentNode);
						String newName = attributeName + ",object";
						resultMap.put(newName, temp);

					} else if (dataType != null && dataType.equalsIgnoreCase("array")) {
						List<Map<String, Object>> arrayObj = new ArrayList<Map<String, Object>>();
						Map<String, Object> temp = getObjectArrayStructure(delimited, attributeName, currentNode);
						arrayObj.add(temp);
						String newName = attributeName + ",array";
						resultMap.put(newName, arrayObj);
					} else {
						resultMap.put(attributeName, dataType);
					}

				}
			}
		}

		return resultMap;
	}

	private Map<String, Object> initiateCompositeBEMapping(Map<String, Object> inputBERecord, String operatorName,
			Map<String, String> flowFileAttributes, List<SmartMapping> mappingList,
			Map<String, Object> refinedOutputStructureMap, Set<String> subDenorMainArrayLst) throws NifiCustomException, Exception {

		Object result = null;
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		Iterator<Entry<String, Object>> outputDefinitionIterate = refinedOutputStructureMap.entrySet().iterator();

		while (outputDefinitionIterate.hasNext()) {
			Entry<String, Object> keyValue = (Entry<String, Object>) outputDefinitionIterate.next();
			String toValueKey = keyValue.getKey();
			Object value = keyValue.getValue();

			// In ROOT, if we have object
			if (toValueKey != null && toValueKey.trim().contains(",object")) {

				String[] beNameObj = toValueKey.split(",object");

				String toCurrent = beNameObj[0];
				String toParent = "Root";

				Map<String, Object> objectMap = (Map) value;
				String toPath = toCurrent + "-" + toParent + "-";

				result = prepareObjectDetails(toParent, toCurrent, inputBERecord, mappingList, objectMap, toPath,
						flowFileAttributes, null, subDenorMainArrayLst);

				if (null != result) {
					resultMap.put(toCurrent, result);
				}

			}

			// In ROOT, if we have array
			else if (toValueKey != null && toValueKey.trim().contains(",array")) {

				String[] beNameObj = toValueKey.split(",array");

				String toCurrent = beNameObj[0];
				String toParent = "Root";

				if (subDenorMainArrayLst.contains(toCurrent)) {
					// array belong to denorm
					List<Map<String, Object>> resultList = SmartMappingUtils.getSubArayDenorm(inputBERecord, mappingList,
							toCurrent);
					if (null != result) {
						resultMap.put(toCurrent, resultList);
					}
				} else {
					
					String newToValue = toCurrent + "-" + toParent + "-";
	
					List<Map<String, Object>> listOuputStructure = (List) value;
	
					Map<String, Object> pvInputRecordWithPath = fetchArrayDataFromPVInputRecord(inputBERecord, mappingList,
							newToValue, null, flowFileAttributes);
	
					String skipFromPath = (String) pvInputRecordWithPath.get("fromPath");
					List<Map<String, Object>> inputRecordList = (List) pvInputRecordWithPath.get("inputRecordList");
					String source = (String) pvInputRecordWithPath.get("source");
	
					if (inputRecordList != null) {
						List<Object> resultList = new ArrayList<Object>();
	
						// iterating one record at a time from list of inner input
						// record
	
						for (Map<String, Object> inputRecordArray : inputRecordList) {
							result = prepareArrayDetails(toParent, toCurrent, inputRecordArray, mappingList,
									listOuputStructure, newToValue, skipFromPath, flowFileAttributes, source);
	
							resultList.add(result);
						}
						if (null != result) {
							resultMap.put(toCurrent, resultList);
						}
					}
				}
			}

			else {

				// In ROOT, we have Primitive type

				String toPathValue = toValueKey + "-" + "Root" + "-";

				result = fetchValueFromInputMap(inputBERecord, mappingList, toPathValue, null, flowFileAttributes,
						null);

				if (null != result) {
					resultMap.put(toValueKey, result);
				}
			}
		}

		return resultMap;
	}

	private Object fetchValueFromInputMap(Map<String, Object> inputBERecord, List<SmartMapping> mappingList,
			String newToValue, String skipFromValue, Map<String, String> flowFileAttributes, String source)
			throws NifiCustomException {

		Object result = null;
		String type = null;

		for (SmartMapping mapping : mappingList) {

			type = mapping.getType();

			if (type != null && !allIgnoreTypes.contains(type.trim().toLowerCase())) {
				String fromPath = mapping.getFromPath();
				String toPath = mapping.getToPath();
				String fromValue = mapping.getFromValue();
				ArrayList<TypeConversion> typeConversionArray = mapping.getTypeConversionArray();

				if (toPath != null && toPath.trim().equalsIgnoreCase(newToValue.trim())) {

					if (type.trim().equalsIgnoreCase("OutputFileBE")) {

						if (skipFromValue != null) {
							String newFromPath = fromPath.replace(skipFromValue, "");
							result = getInputRecordFromNestedStructure(newFromPath, inputBERecord, fromValue,typeConversionArray);
							return result;
						} else {
							result = getInputRecordFromNestedStructure(fromPath, inputBERecord, fromValue,typeConversionArray);
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

	private Object getInputRecordFromNestedStructure(String fromPath, Map<String, Object> inputBERecord,
			String fromValue, ArrayList<TypeConversion> typeConversionArray) throws NifiCustomException {

		Map<String, Object> findHierarichyMap = inputBERecord;

		Object result = null;
		String[] split = fromPath.split("-");

		for (int i = split.length - 1; i >= 0; i--) {
			String temp = split[i];
			if (temp != null && temp.trim().length() > 0 && !allIgnoreTypes.contains(temp.trim().toLowerCase())) {
				result = findHierarichyMap.get(temp);
				if (typeConversionArray != null) {
					result = CommonUtils.typeConvertResult(result, typeConversionArray);
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

	private Map<String, Object> getObjectfromFlowFileAttributes(Map<String, String> flowFileAttributeObj,
			String requiredAttribute) throws NifiCustomException {

		String processVariableStr = (String) flowFileAttributeObj.get(requiredAttribute);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> objectProcessVariable = null;

		if (!StringUtils.isEmpty(processVariableStr)) {
			try {
				ProcessVariable processVariable = mapper.readValue(processVariableStr, ProcessVariable.class);
				String pvObjectJsonStr = processVariable.getValue().getBeValue();

				objectProcessVariable = mapper.readValue(pvObjectJsonStr, Map.class);

			} catch (Exception e) {
				logger.error("Error occurred while reading flowfile object attributes " + e.getMessage(), e);

				throw new NifiCustomException(
						"Error occurred while reading flowfile Object Attributes: " + e.getMessage());
			}
		}
		return objectProcessVariable;
	}

	private Object prepareObjectDetails(String toParent, String toCurrent, Map<String, Object> inputBERecord,
			List<SmartMapping> mappingList, Map<String, Object> objectBEMap, String toPath,
			Map<String, String> flowFileAttributes, String source, Set<String> subDenorMainArrayLst) throws NifiCustomException, Exception {

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

	private Map<String, Object> fetchArrayDataFromPVInputRecord(Map<String, Object> inputBERecord,
			List<SmartMapping> mappingList, String newToValue, String skipFromPath,
			Map<String, String> flowFileAttributes) throws NifiCustomException {

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
				else if (mapping.getType() != null && allIgnoreTypes.contains(mapping.getType().trim().toLowerCase())) {

					if (toPath != null && toPath.trim().equalsIgnoreCase(newToValue)) {

						Map<String, Object> findHierarichyMap = inputBERecord;
						String[] split = fromPath.split("-");

						for (int i = split.length - 1; i >= 0; i--) {

							String temp = split[i];

							if (temp != null && temp.trim().length() > 0
									&& !allIgnoreTypes.contains(temp.trim().toLowerCase())) {
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
			logger.error("Exception occured in fetchArrayDataFromPVInputRecord " + ex.getMessage(), ex);

			throw new NifiCustomException("Exception occured in fetchArrayDataFromPVInputRecord " + ex.getMessage());
		}

		return pvInputRecordWithPath;
	}

	private Object prepareArrayDetails(String toParent, String toCurrent, Map<String, Object> inputBERecord,
			List<SmartMapping> mappingList, List<Map<String, Object>> objectBEMap, String newToValue,
			String skipFromPath, Map<String, String> flowFileAttributes, String source) throws NifiCustomException {

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
			Map<String, String> flowFileAttributes, String source) throws NifiCustomException {

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

	@Override
	protected String getGroupId(ProcessContext context, FlowFile flowFile) {
		String groupId = flowFile.getAttribute("transactionId");
		return groupId;
	}

	@Override
	protected void setUpBinManager(EvictionBinManager binManager, ProcessContext context) {
		// TODO Auto-generated method stub

	}
	
}