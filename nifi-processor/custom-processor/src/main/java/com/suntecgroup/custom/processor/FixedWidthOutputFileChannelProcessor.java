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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.apache.nifi.annotation.behavior.SideEffectFree;
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
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.channelintegration.OperatorStats;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.ContentOutput;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.FixedWidthOutput;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.FooterOutput;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.HeaderOutput;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.OutputFile;
import com.suntecgroup.custom.processor.model.smartconnector.SmartMapping;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;
import com.suntecgroup.custom.processor.outputeviction.EvictionBin;
import com.suntecgroup.custom.processor.outputeviction.EvictionBinFiles;
import com.suntecgroup.custom.processor.outputeviction.EvictionBinManager;
import com.suntecgroup.custom.processor.utils.CommonUtils;
import com.suntecgroup.custom.processor.utils.Constants;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/*
 * This class is for creating a custom NiFi processor to handle the FixedWidth Output File Channel Integration.
 * This class would read the Flow file content and its attributes to create and write an output file.
 *
 * @version 1.0 - April 2019
 * @author Mohammed Rizwan
 */

@SideEffectFree
@Tags({ "Fixed Width, Output Channel Integration" })
@CapabilityDescription("Fixed Width File Output Processor for Channel Integration")
public class FixedWidthOutputFileChannelProcessor extends EvictionBinFiles {

	private ComponentLog logger;
	private ObjectMapper mapper = null;
	private Gson gson = null;
	private Type mapTypeObj = null;

	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;
	
	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	private final AtomicReference<OkHttpClient> okHttpClientAtomicReference = new AtomicReference<>();

	public static final PropertyDescriptor HEADER = new PropertyDescriptor.Builder().name("Header")
			.description("FixedWidth Output Channel Integration Header").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor FOOTER = new PropertyDescriptor.Builder().name("Footer")
			.description("FixedWidth Output Channel Integration footer").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CONTENT = new PropertyDescriptor.Builder().name("Content")
			.description("FixedWidth Output Channel Integration content").required(true)
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
			.description("FixedWidth Output Channel Integration Name").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor MAPPING = new PropertyDescriptor.Builder().name("Mapping")
			.description("FixedWidth Output Channel Integration Mapping").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor EVICTION = new PropertyDescriptor.Builder().name("Eviction")
			.description("FixedWidth Output Channel Integration Eviction").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_FILEPATH = new PropertyDescriptor.Builder().name("Output FilePath")
			.description("Output File Path").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor OUTPUT_FILENAME = new PropertyDescriptor.Builder().name("Output FileName")
			.description("Output File Name").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

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

			// this loop is for time + record eviction or only record eviction. Here we will have single flowfile object

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
				updateOperatorStats(sessionId, runNumber, ciNameProperty, statisticsMap);
				processSession.transfer(flowFileObj, REL_FAILURE);
				processSession.commit();
				return;
			}
			updateOperatorStats(sessionId, runNumber, ciNameProperty, statisticsMap);
			processSession.transfer(flowFileObj, REL_SUCCESS);
		} else {

			// this is only time based eviction or no eviction. Here we can have multiple flowfile objects

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
		processSession.commit();

	}

	private void beginProcess(FlowFile flowFileObj, List<String> filesCreated,
			List<Map<String, Object>> outputFlowFileList, String mappingProperty, String contentProperty,
			String ciNameProperty, String outputFilePath, OutputFile outputFile, Map<String, Integer> statisticsMap,
			String headerProperty, String footerProperty) throws NifiCustomException {

		String staticValue = outputFile.getStaticName();
		String dynamicValue = outputFile.getDynamicName();
		String timeStampFormat = outputFile.getTimeStampFormat();
		List<String> contentResultList = new ArrayList<String>();
		List<String> outputHeaderList = new ArrayList<String>();
		List<String> outputFooterList = new ArrayList<String>();

		Map<String, String> flowFileAttributes = flowFileObj.getAttributes();

		// for static file
		if (staticValue != null && staticValue.trim().length() > 0
				&& (dynamicValue == null || (dynamicValue != null && dynamicValue.trim().length() < 1))) {

			String fileName = staticValue + "_" + CommonUtils.getCurrentTimeStampFormatted(timeStampFormat);

			if (outputFlowFileList != null && outputFlowFileList.size() > 0) {

				List<Map<String, String>> updatedMappedList = getContentMappedList(outputFlowFileList,
						flowFileAttributes, mappingProperty);

				contentResultList = getContentOutputList(contentProperty, ciNameProperty, updatedMappedList);

				// Get Header data
				HeaderOutput header = null;

				try {
					header = mapper.readValue(headerProperty, HeaderOutput.class);
				} catch (Exception e) {
					logger.error(ciNameProperty + ":Error occurred while reading Header property " + e.getMessage(), e);

					throw new NifiCustomException(
							ciNameProperty + ":Error occurred while reading Header property: " + e.getMessage());
				}

				FooterOutput footer = null;

				try {
					footer = mapper.readValue(footerProperty, FooterOutput.class);

				} catch (Exception e) {
					logger.error(ciNameProperty + ":Error occurred while reading footer property " + e.getMessage(), e);

					throw new NifiCustomException(
							ciNameProperty + ":Error occurred while reading Footer property: " + e.getMessage());
				}

				if (header != null && header.isHasHeader()) {
					outputHeaderList = getHeaderDetails(flowFileObj, ciNameProperty, header, updatedMappedList);
				}

				if (footer != null && footer.isHasFooter()) {
					outputFooterList = getFooterDetails(flowFileObj, ciNameProperty, footer, updatedMappedList);
				}

				statisticsMap.put("totalWrittenRecords",
						statisticsMap.get("totalWrittenRecords") + contentResultList.size());
				statisticsMap.put("totalUnwrittenRecords",
						statisticsMap.get("totalUnwrittenRecords") - contentResultList.size());

			} else {
				throw new NifiCustomException("Failed due to FlowFile Content is empty!");
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

						List<Map<String, String>> updatedMappedList = getContentMappedList(valueList,
								flowFileAttributes, mappingProperty);

						// Used the mapped list containing real time data to
						// compare with content json and create content file
						// data

						contentResultList = getContentOutputList(contentProperty, ciNameProperty,
								updatedMappedList);

						statisticsMap.put("totalWrittenRecords",
								statisticsMap.get("totalWrittenRecords") + contentResultList.size());
						statisticsMap.put("totalUnwrittenRecords",
								statisticsMap.get("totalUnwrittenRecords") - contentResultList.size());

					} else {
						throw new NifiCustomException("Failed due to FlowFile Content is empty for Dynamic attribute!");
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

			if (temp != null && temp.trim().length() > 0 && !temp.trim().equalsIgnoreCase("root")) {
				result = findHierarichyMap.get(temp);
			}
		}
		return result;
	}

	private int writeFile(List<String> outputHeaderList, List<String> outputFooterList, List<String> contentResultList,
			String outputFilePath, String fileName, List<String> filesCreated) throws NifiCustomException {

		int totalFilesWritten = 0;
		List<String> totalFileOutputList = new ArrayList<String>();

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

	private List<String> getContentOutputList(String contentProperty, String ciNameProperty,
			List<Map<String, String>> updatedMappedList) throws NifiCustomException {
		ContentOutput content = null;
		List<FixedWidthOutput> fixedWidthLst = null;
		List<String> contentResultList = new ArrayList<String>();

		try {
			content = mapper.readValue(contentProperty, ContentOutput.class);
			if (content != null) {
				fixedWidthLst = content.getFixedWidth();

			}

		} catch (Exception e) {
			logger.error(ciNameProperty + ":Error occurred while reading content property " + e.getMessage(), e);

			throw new NifiCustomException(
					ciNameProperty + ":Error occurred while reading Content property: " + e.getMessage());
		}

		if (updatedMappedList != null && updatedMappedList.size() > 0) {

			Map<Integer, String> contentResult = new TreeMap<Integer, String>();

			for (Map<String, String> currentMappedObj : updatedMappedList) {
				StringBuilder sb = new StringBuilder();

				for (FixedWidthOutput contentObj : fixedWidthLst) {

					String attributeName = contentObj.getAttributeName();
					int startingPosition = contentObj.getStartingPosition();
					int width = contentObj.getWidth();
					int finalWidth = startingPosition + width;
					String value = currentMappedObj.get(attributeName);

					int j = 0;
					int valueLength = 0;
					char valueChar[] = null;

					for (int i = startingPosition; i < finalWidth; i++) {

						if (value == null) {
							value = "null";
						}

						valueLength = value.length();
						valueChar = value.toCharArray();

						if (j < valueLength) {
							contentResult.put(i, String.valueOf(valueChar[j]));
						} else {
							contentResult.put(i, " ");
						}
						j++;
					}

				}

				if (contentResult.size() > 0) {
					Iterator<Entry<Integer, String>> itOb = contentResult.entrySet().iterator();
					int count = 0;

					while (itOb.hasNext()) {
						Entry<Integer, String> ev = (Entry<Integer, String>) itOb.next();
						// sb.append(ev.getValue());
						int starting = ev.getKey();

						if (count < starting) {
							for (int i = count; i < starting; i++) {
								sb.append(" ");
							}
							count = starting;
						}

						if (ev.getKey() == count) {
							sb.append(ev.getValue());
						}
						count++;

					}
					if (sb.length() > 0) {
						contentResultList.add(sb.toString());
					}
				}
			}
		}

		return contentResultList;
	}

	private List<Map<String, String>> getContentMappedList(List<Map<String, Object>> outputFlowFileList,
			Map<String, String> flowFileAttributes, String mappingProperty) throws NifiCustomException {

		List<Map<String, String>> updatedMappedList = new ArrayList<Map<String, String>>();

		List<SmartMapping> mappingList = null;
		try {
			// mappingList = mapper.readValue(mappingProperty, List.class);
			mappingList = Arrays.asList(mapper.readValue(mappingProperty, SmartMapping[].class));

		} catch (Exception e) {
			logger.error("Error occurred while reading mapping property " + e.getMessage(), e);

			throw new NifiCustomException("Error occurred while reading mapping property: " + e.getMessage());
		}

		for (Map<String, Object> currentRecord : outputFlowFileList) {
			Map<String, String> mappedRecord = new LinkedHashMap<String, String>();

			for (SmartMapping mappingObj : mappingList) {

				String type = mappingObj.getType();
				String beAttributeName = mappingObj.getFromValue();
				String newAttributeName = mappingObj.getToValue();
				String fromPath = mappingObj.getFromPath();

				String value = null;

				if (type != null && !type.trim().equalsIgnoreCase("root")) {

					// From PV
					if (type.trim().equalsIgnoreCase("pv") || type.trim().equalsIgnoreCase("PvTypeBe")) {
						Object resultValue = getDatafromFlowFileAttributes(flowFileAttributes, beAttributeName, fromPath);
						if (resultValue != null) {
							value = resultValue.toString();
						}
					}

					// From enter value
					else if (type.trim().equalsIgnoreCase("ev")) {
						value = beAttributeName;
					}

					else if (type.trim().equalsIgnoreCase("OutputFileBE")) {
						if (currentRecord.containsKey(beAttributeName)) {
							Object valueObj = currentRecord.get(beAttributeName);

							if (valueObj != null && !(valueObj instanceof List)) {
								value = valueObj.toString();
							}

						}
					}
					mappedRecord.put(newAttributeName, value);
				}
			}
			updatedMappedList.add(mappedRecord);
		}

		return updatedMappedList;
	}

	public List<Map<String, Object>> getFlowFileContent(final ProcessSession processSession, FlowFile flowFileObj,
			String ciName) throws NifiCustomException {

		List<Map<String, Object>> outputFlowFileList = new ArrayList<Map<String, Object>>();
		JsonReader reader = null;
		InputStream outputChannelInput = null;
		try {

			outputChannelInput = processSession.read(flowFileObj);

			if (outputChannelInput != null) {
				reader = new JsonReader(new InputStreamReader(outputChannelInput, StandardCharsets.UTF_8));
				
				reader.beginArray();

				while (reader.hasNext()) {
					Map<String, Object> jsonRecord = gson.fromJson(reader, mapTypeObj);
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
			List<Map<String, String>> updatedMappedList) throws NifiCustomException {

		List<String> resultList = new ArrayList<String>();
		List<FixedWidthOutput> fixedWidth = header.getFixedWidth();
		int maximumLines = header.getHeaderLines();
		resultList = fetchHeaderFooterValues(flowFileObj, fixedWidth, maximumLines, updatedMappedList);
		return resultList;
	}

	public List<String> getFooterDetails(FlowFile flowFileObj, String ciName, FooterOutput footer,
			List<Map<String, String>> updatedMappedList) throws NifiCustomException {

		List<String> resultList = new ArrayList<String>();
		List<FixedWidthOutput> fixedWidth = footer.getFixedWidth();
		int maximumLines = footer.getFooterLines();
		resultList = fetchHeaderFooterValues(flowFileObj, fixedWidth, maximumLines, updatedMappedList);
		return resultList;
	}

	private List<String> fetchHeaderFooterValues(FlowFile flowFileObj, List<FixedWidthOutput> fixedWidth,
			int maximumLines, List<Map<String, String>> updatedMappedList) throws NifiCustomException {

		List<String> resultList = new ArrayList<String>();
		Map<Integer, Map<Integer, String>> lineValue = new TreeMap<Integer, Map<Integer, String>>();
		Map<String, String> flowFileAttributes = flowFileObj.getAttributes();

		List<String> mathematicalOperations = new ArrayList<String>();

		mathematicalOperations.add("count");
		mathematicalOperations.add("min");
		mathematicalOperations.add("max");
		mathematicalOperations.add("sum");
		mathematicalOperations.add("avg");

		for (FixedWidthOutput fwObj : fixedWidth) {

			String value = null;
			String type = fwObj.getType();

			int lineNumber = fwObj.getLineNumber();
			int startingPosition = fwObj.getStartingPosition();
			int width = fwObj.getWidth();

			if (lineNumber <= maximumLines) {
				if (type != null && type.equalsIgnoreCase("pv")) {
					if (fwObj != null && fwObj.getValue() != null && fwObj.getValue().trim().length() > 0) {
						Object resultValue = getDatafromFlowFileAttributes(flowFileAttributes, fwObj.getValue(), "");
						if (resultValue != null) {
							value = resultValue.toString();
						}

					}
				} else if (type != null && type.equalsIgnoreCase("ev")) {
					value = fwObj.getValue();
				} else if (type != null && mathematicalOperations.contains(type.trim())) {
					value = getMathematicalResult(value, updatedMappedList, type);
				}

				if (lineValue.containsKey(lineNumber)) {
					Map<Integer, String> segmentValue = (Map<Integer, String>) lineValue.get(lineNumber);
					arrangeHeaderFooterData(lineValue, lineNumber, startingPosition, width, value, segmentValue);
				} else {
					Map<Integer, String> segmentValue = new TreeMap<Integer, String>();
					arrangeHeaderFooterData(lineValue, lineNumber, startingPosition, width, value, segmentValue);
				}

			}
		}

		if (lineValue != null && lineValue.size() > 0) {
			Iterator<Entry<Integer, Map<Integer, String>>> itObj = lineValue.entrySet().iterator();

			while (itObj.hasNext()) {

				Entry<Integer, Map<Integer, String>> ev = (Entry<Integer, Map<Integer, String>>) itObj.next();
				StringBuilder sb = new StringBuilder();
				Map<Integer, String> segValue = ev.getValue();
				int count = 0;

				Iterator<Entry<Integer, String>> innerIt = segValue.entrySet().iterator();

				while (innerIt.hasNext()) {
					Entry<Integer, String> ev1 = (Entry<Integer, String>) innerIt.next();

					int starting = ev1.getKey();

					if (count < starting) {
						for (int i = count; i < starting; i++) {
							sb.append(" ");
						}
						count = starting;
					}

					if (ev1.getKey() == count) {
						sb.append(ev1.getValue());
					}
					count++;
				}

				if (sb.length() > 0) {
					resultList.add(sb.toString());
				}
			}
		}

		return resultList;
	}

	private String getMathematicalResult(String inputPath, List<Map<String, String>> mappedContent, String functionName)
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

		String tempResult = null;

		if (result != null) {
			tempResult = result.toString();
		}

		return tempResult;

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

	private List<String> fetchContentValue(String inputPath, List<Map<String, String>> mappedContent)
			throws NifiCustomException {
		List<String> resultList = new ArrayList<String>();

		// All the records in the contentList is processed

		for (Map<String, String> record : mappedContent) {
			List<String> value = getContentNestedValue(inputPath, record);
			resultList.addAll(value);
		}

		return resultList;
	}

	private List<String> getContentNestedValue(String inputPath, Map<String, String> record)
			throws NifiCustomException {

		String result = null;
		List<String> resList = new ArrayList<String>();

		Map<String, String> findHierarichyMap = record;
		String[] split = inputPath.split("\\.");

		try {

			for (int i = 0; i < split.length; i++) {

				String temp = split[i];

				if (temp != null && temp.trim().length() > 0 && !temp.trim().equalsIgnoreCase("root")) {

					result = findHierarichyMap.get(temp);

				}
			}

			if (result != null) {
				resList.add(result);
			}

		} catch (Exception ex) {
			logger.error("Error occurred while reading contentvalue for header/footer mathematical calculation "
					+ ex.getMessage(), ex);

			throw new NifiCustomException(
					"Exception occured while fetching contentvalue for header/footer mathematical calculation "
							+ ex.getMessage());
		}

		return resList;
	}

	private void arrangeHeaderFooterData(Map<Integer, Map<Integer, String>> lineValue, int lineNumber,
			int startingPosition, int width, String value, Map<Integer, String> segmentValue) {
		int j = 0;
		int valueLength = 0;
		char valueChar[] = null;
		int finalWidth = startingPosition + width;
		for (int i = startingPosition; i < finalWidth; i++) {
			if (value == null) {
				value = "null";
			}

			valueLength = value.length();
			valueChar = value.toCharArray();

			if (j < valueLength) {
				segmentValue.put(i, String.valueOf(valueChar[j]));
			} else {
				segmentValue.put(i, " ");
			}
			j++;
		}
		if (segmentValue != null && segmentValue.size() > 0) {
			lineValue.put(lineNumber, segmentValue);
		}
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

			outputStream = new OutputStreamWriter(new FileOutputStream(txtFilePath), Constants.UTF_ENCODING);
			bufferedWriterObject = new BufferedWriter(outputStream);
			filesCreated.add(txtFilePath);

			for (String data1 : data) {
				String dataWithNewLine = data1 + System.getProperty("line.separator");
				bufferedWriterObject.write(dataWithNewLine);
				bufferedWriterObject.flush();
			}
		} catch (IOException e) {
			logger.error("Failed while writnig file " + e.getMessage(), e);
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

	private Object getDatafromFlowFileAttributes(Map<String, String> flowFileAttributeObj, String requiredAttribute, String fromPath)
			throws NifiCustomException {

		ObjectMapper mapper = new ObjectMapper();
		String targetPV = requiredAttribute;
		if (!StringUtils.isBlank(fromPath) && fromPath.contains("pvTypeBe")) {
			// get exact PV name and set to targetPV
			try {
				targetPV = fromPath.split("-")[fromPath.split("-").length - 2];
			} catch (Exception e) {
				targetPV = requiredAttribute;
			}
		}
		String processVariableStr = (String) flowFileAttributeObj.get(targetPV);
		Map<String, Object> objectProcessVariable = null;

		if (!StringUtils.isEmpty(processVariableStr)) {
			try {
				ProcessVariable processVariable = mapper.readValue(processVariableStr, ProcessVariable.class);

				if (Constants.PV_TYPE_CATEGORY_BE.equals(processVariable.getType().getTypeCategory())) {
					// this PV is of type BE
					String pvObjectJsonStr = processVariable.getValue().getBeValue();
					objectProcessVariable = mapper.readValue(pvObjectJsonStr, Map.class);
					if (null != objectProcessVariable) {
						return getValueFromPVPath(fromPath, objectProcessVariable);
					}
				} else {
					// this PV is of primitive type
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
				}
			} catch (Exception e) {
				logger.error("Error occurred while reading flowfile attributes " + e.getMessage(), e);

				throw new NifiCustomException("Error occurred while reading flowfile Attributes: " + e.getMessage());
			}
		}
		return null;
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

	private Object getValueFromPVPath(String fromPath, Map<String, Object> pvObjectRecord) throws NifiCustomException {

		Map<String, Object> findHierarichyMap = pvObjectRecord;

		Object result = null;
		String[] split = fromPath.split("\\-");

		for (int i = split.length - 1; i >= 0; i--) {
			String temp = split[i];
			if (temp != null && temp.trim().length() > 0 && !temp.trim().equalsIgnoreCase("pvtypebe")) {
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
	
}