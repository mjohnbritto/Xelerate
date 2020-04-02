/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.lifecycle.OnStopped;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractSessionFactoryProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessSessionFactory;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.util.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.NifiUtils;

/**
 * Base class for file-binning processors.
 *
 */
public abstract class BinFiles extends AbstractSessionFactoryProcessor {

	public static final PropertyDescriptor FILE_COUNT = new PropertyDescriptor.Builder().name("Expected File Count")
			.description("The expected number of files to start merging process.").required(true).defaultValue("3")
			.addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR).build();

	public static final PropertyDescriptor MAX_BIN_COUNT = new PropertyDescriptor.Builder()
			.name("Maximum number of Bins")
			.description("Specifies the maximum number of bins that can be held in memory at any one time")
			.defaultValue("50").required(true).addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR).build();

	public static final PropertyDescriptor MAX_BIN_AGE = new PropertyDescriptor.Builder().name("Max Bin Age")
			.description(
					"The maximum age of a Bin that will trigger a Bin to be complete. Expected format is <duration> <time unit> "
							+ "where <duration> is a positive integer and time unit is one of seconds, minutes, hours")
			.required(false).addValidator(StandardValidators.createTimePeriodValidator(1, TimeUnit.SECONDS,
					Integer.MAX_VALUE, TimeUnit.SECONDS))
			.build();

	public static final PropertyDescriptor MAPPING = new PropertyDescriptor.Builder().name("Input and CV mapping")
			.description("Mapping information between BE and CV").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor MERGE_SOURCE = new PropertyDescriptor.Builder().name("Merge Source")
			.description("flag for defining the processor as source processor of merge")
			.allowableValues("true", "false").required(true).defaultValue("false")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();
	
	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final Relationship REL_ORIGINAL = new Relationship.Builder().name("original")
			.description("The FlowFiles that were used to create the bundle").build();
	public static final Relationship REL_FAILURE = new Relationship.Builder().name("failure")
			.description(
					"If the bundle cannot be created, all FlowFiles that would have been used to created the bundle will be transferred to failure")
			.build();
	public static final Relationship REL_MERGED = new Relationship.Builder().name("merged")
			.description("The FlowFile containing the merged content").build();

	private final BinManager binManager = new BinManager();
	private final Queue<Bin> readyBins = new LinkedBlockingQueue<>();

	@OnStopped
	public final void resetState() {
		binManager.purge();
		Bin bin;
		while ((bin = readyBins.poll()) != null) {
			bin.getSession().rollback();
		}
	}

	/**
	 * Returns a group ID representing a bin. This allows flow files to be
	 * binned into like groups.
	 */
	protected abstract String getGroupId(final ProcessContext context, final FlowFile flowFile);

	/**
	 * Performs any additional setup of the bin manager. Called during the
	 * OnScheduled phase.
	 */
	protected abstract void setUpBinManager(BinManager binManager, ProcessContext context);

	/**
	 * Processes a single bin. Implementing class is responsible for committing
	 * each session
	 */
	protected abstract void processBin(Bin unmodifiableBin, ProcessContext context)
			throws ProcessException, NifiCustomException;

	/**
	 * Allows additional custom validation to be done. This will be called from
	 * the parent's customValidation method.
	 */
	protected Collection<ValidationResult> additionalCustomValidation(final ValidationContext context) {
		return new ArrayList<>();
	}

	@Override
	public final void onTrigger(final ProcessContext context, final ProcessSessionFactory sessionFactory)
			throws ProcessException {

		final int flowFilesBinned;
		if (!isScheduled()) {
			return;
		}
		flowFilesBinned = binFlowFile(context, sessionFactory);
		final int binsMigrated = migrateBins(context);
		final int binsProcessed = processBins(context);

		// If we accomplished nothing then let's yield
		if (flowFilesBinned == 0 && binsMigrated == 0 && binsProcessed == 0) {
//			context.yield();
		}
		
	}

	private int migrateBins(final ProcessContext context) {
		int added = 0;
		for (final Bin bin : binManager.removeReadyBins()) {
			this.readyBins.add(bin);
			added++;
		}
		return added;
	}

	private int processBins(final ProcessContext context) {
		final ComponentLog logger = getLogger();
		String errorType = Constants.TECHNICALERROR;
		String errorMessage = StringUtils.EMPTY;
		String inputBE = StringUtils.EMPTY;
		int processedBins = 0;
		Bin bin;
		int index = 1;
		while ((bin = readyBins.poll()) != null) {
			String transactionId = bin.getContents().get(0).getAttribute(Constants.TRANSACTION_ID);
			try {
				if (validateBin(bin)) {
					this.processBin(bin, context);
				} else {
					final ProcessSession binSession = bin.getSession();
					for (FlowFile flowFile : bin.getContents()) {
						String isMarkerFile = flowFile.getAttribute(Constants.IS_MARKER);
						String markerType = flowFile.getAttribute(Constants.MARKER_TYPE);
						if (!StringUtils.isEmpty(isMarkerFile) && Boolean.valueOf(isMarkerFile) && StringUtils.isEmpty(markerType)) {
							binSession.remove(flowFile);
						} else {
							binSession.putAttribute(flowFile, Constants.GROUP_INDEX, String.valueOf(index++));
							binSession.getProvenanceReporter().modifyAttributes(flowFile);
							flowFile = NifiUtils.updateFailureDetails(context, binSession, flowFile, inputBE, errorType, errorMessage);
							binSession.transfer(flowFile, REL_FAILURE);

						}
					}

					// Sending marker file to the next processor
					boolean isMergeSource = context.getProperty(MERGE_SOURCE).evaluateAttributeExpressions()
							.asBoolean();
					if (isMergeSource) {
						FlowFile emptyFlowFile = binSession.create();
						binSession.putAttribute(emptyFlowFile, Constants.TRANSACTION_ID, transactionId);
						binSession.putAttribute(emptyFlowFile, Constants.IS_MARKER, "true");
						binSession.transfer(emptyFlowFile, REL_MERGED);
					}
					binSession.commit();
				}
			} catch (final ProcessException e) {
				logger.error("Failed to process bundle of {} files due to {}",
						new Object[] { bin.getContents().size(), e });

				final ProcessSession binSession = bin.getSession();
				for (FlowFile flowFile : bin.getContents()) {
					String isMarkerFile = flowFile.getAttribute(Constants.IS_MARKER);
					String markerType = flowFile.getAttribute(Constants.MARKER_TYPE);
					if (!StringUtils.isEmpty(isMarkerFile) && Boolean.valueOf(isMarkerFile) && StringUtils.isEmpty(markerType)) {
						binSession.remove(flowFile);
					} else {
						binSession.putAttribute(flowFile, Constants.GROUP_INDEX, String.valueOf(index++));
						binSession.getProvenanceReporter().modifyAttributes(flowFile);
						flowFile = NifiUtils.updateFailureDetails(context, binSession, flowFile, inputBE, errorType, errorMessage);
						binSession.transfer(flowFile, REL_FAILURE);
					}
				}
				// Sending marker file to the next processor
				boolean isMergeSource = context.getProperty(MERGE_SOURCE).evaluateAttributeExpressions().asBoolean();
				if (isMergeSource) {
					FlowFile emptyFlowFile = binSession.create();
					binSession.putAttribute(emptyFlowFile, Constants.TRANSACTION_ID, transactionId);
					binSession.putAttribute(emptyFlowFile, Constants.IS_MARKER, "true");
					binSession.transfer(emptyFlowFile, REL_MERGED);
				}
				binSession.commit();
			} catch (final Exception e) {
				logger.error("Failed to process bundle of {} files due to {}; rolling back sessions",
						new Object[] { bin.getContents().size(), e });
				final ProcessSession binSession = bin.getSession();
				for (FlowFile flowFile : bin.getContents()) {
					String isMarkerFile = flowFile.getAttribute(Constants.IS_MARKER);
					String markerType = flowFile.getAttribute(Constants.MARKER_TYPE);
					if (!StringUtils.isEmpty(isMarkerFile) && Boolean.valueOf(isMarkerFile) && StringUtils.isEmpty(markerType)) {
						binSession.remove(flowFile);
					} else {
						binSession.putAttribute(flowFile, Constants.GROUP_INDEX, String.valueOf(index++));
						binSession.getProvenanceReporter().modifyAttributes(flowFile);
						flowFile = NifiUtils.updateFailureDetails(context, binSession, flowFile, inputBE, errorType, errorMessage);
						binSession.transfer(flowFile, REL_FAILURE);
					}
				}
				// Sending marker file to the next processor
				boolean isMergeSource = context.getProperty(MERGE_SOURCE).evaluateAttributeExpressions().asBoolean();
				if (isMergeSource) {
					FlowFile emptyFlowFile = null;
					emptyFlowFile = binSession.create();
					binSession.putAttribute(emptyFlowFile, Constants.TRANSACTION_ID, transactionId);
					binSession.putAttribute(emptyFlowFile, Constants.IS_MARKER, "true");
					binSession.transfer(emptyFlowFile, REL_MERGED);
				}
				binSession.commit();
			}
			processedBins++;
		}
		return processedBins;

	}

	boolean validateBin(Bin bin) {

		boolean isValid = true;
		final List<FlowFile> contents = bin.getContents();
		List<InputCVMapping> inputChannelList = (List<InputCVMapping>) bin.getMapping();

		Map<String, List<FlowFile>> inputChannelFilesMap = groupFlowFilesByChannel(contents);

		// comparing whether expected file count is equal to actual file count
		for (InputCVMapping mapping : inputChannelList) {
			if (mapping.getFileCount() != inputChannelFilesMap.get(mapping.getConnectionName()).size()) {
				isValid = false;
			}
		}

		if (isValid) {
			outer: for (Map.Entry<String, List<FlowFile>> inputChannelFiles : inputChannelFilesMap.entrySet()) {
				List<FlowFile> flowFiles = inputChannelFiles.getValue();
				String pathName = inputChannelFiles.getKey();
				int dataFilesCount = 0;
				for (FlowFile flowFile : flowFiles) {
					String markerFlag = flowFile.getAttribute(Constants.IS_MARKER);
					if (StringUtils.isBlank(markerFlag)) {
						++dataFilesCount;
					} else {
						boolean isMarker = Boolean.valueOf(markerFlag);
						if (isMarker) {
							String markerType = flowFile.getAttribute(Constants.MARKER_TYPE);
							if (!StringUtils.isBlank(markerType) && Constants.BUSINESS_FAIL.equals(markerType)) {
								continue;
							} else {
								isValid = false;
								break outer;
							}
						}
					}
				}

				for (InputCVMapping mapping : inputChannelList) {
					if (mapping.getConnectionName().equalsIgnoreCase(pathName)) {
						if (mapping.isMandatoryCV() && dataFilesCount == 0) {
							isValid = false;
							break outer;
						}
					}
				}
			}
		}
		return isValid;
	}

	public Map<String, List<FlowFile>> groupFlowFilesByChannel(List<FlowFile> contents) {
		Map<String, List<FlowFile>> channelAndFilesMap = new HashMap<String, List<FlowFile>>();
		for (FlowFile flowFile : contents) {
			String pathName = flowFile.getAttribute(Constants.ROUTE);
			if (channelAndFilesMap.containsKey(pathName)) {
				List<FlowFile> flowFileList = channelAndFilesMap.get(pathName);
				flowFileList.add(flowFile);
			} else {
				List<FlowFile> flowFileList = new ArrayList<FlowFile>();
				flowFileList.add(flowFile);
				channelAndFilesMap.put(pathName, flowFileList);
			}
		}
		return channelAndFilesMap;
	}

	private int binFlowFile(final ProcessContext context, final ProcessSessionFactory sessionFactory) {
		final ProcessSession session = sessionFactory.createSession();
		int flowFileBinned = 0;
		List<FlowFile> flowFiles = session.get(1);
		if (flowFiles.isEmpty()) {
			return flowFileBinned;
		}
		FlowFile flowFile = flowFiles.get(0);
		String sessionId = context.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
		sessionId = sessionId.trim();
		session.putAttribute(flowFile, Constants.ATTR_SESSION_ID, sessionId);
		String runNumber = context.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
		runNumber = runNumber.trim();
		session.putAttribute(flowFile, Constants.ATTR_RUN_NUMBER, runNumber);
		String isMarkerFile = flowFile.getAttribute(Constants.IS_MARKER);
		if (StringUtils.isEmpty(isMarkerFile)) {
			session.getProvenanceReporter().modifyAttributes(flowFile);
		}
		final String groupingIdentifier = getGroupId(context, flowFile); 
		boolean isBinned = binManager.offer(groupingIdentifier, flowFile, session, sessionFactory);
		if (isBinned) {			
			++flowFileBinned;
		} else {
			session.penalize(flowFile);
			session.rollback(true);
		}
		return flowFileBinned;
	}

	@OnScheduled
	public final void onScheduled(final ProcessContext context) throws IOException {

		if (context.getProperty(MAX_BIN_AGE).isSet()) {
			binManager.setMaxBinAge(context.getProperty(MAX_BIN_AGE).asTimePeriod(TimeUnit.SECONDS).intValue());
		} else {
			binManager.setMaxBinAge(Integer.MAX_VALUE);
		}

		if (context.getProperty(FILE_COUNT).isSet()) {
			binManager.setExpectedFileCount(context.getProperty(FILE_COUNT).asInteger().intValue());
		} else {
			binManager.setExpectedFileCount(Integer.MAX_VALUE);
		}

		final String mapping = context.getProperty(MAPPING).evaluateAttributeExpressions().getValue();
		ObjectMapper mapper = new ObjectMapper();
		List<InputCVMapping> mappingList = mapper.readValue(mapping, new TypeReference<List<InputCVMapping>>() {
		});
		binManager.setInputCVMappingList(mappingList);
		this.setUpBinManager(binManager, context);

		if (context.getProperty(MAX_BIN_COUNT).isSet()) {
			binManager.setMaxBinCount(context.getProperty(MAX_BIN_COUNT).asInteger());
		} else {
			binManager.setMaxBinCount(new Integer(50));
		}

	}
}
