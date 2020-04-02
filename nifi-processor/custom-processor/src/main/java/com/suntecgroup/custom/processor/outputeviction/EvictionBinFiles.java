/* 

 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor.outputeviction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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
import org.codehaus.jackson.map.ObjectMapper;

import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.Eviction;

/**
 * Base class for file-binning processors.
 *
 */
public abstract class EvictionBinFiles extends AbstractSessionFactoryProcessor {

	public static final PropertyDescriptor MAX_BIN_COUNT = new PropertyDescriptor.Builder()
			.name("Maximum number of Bins")
			.description("Specifies the maximum number of bins that can be held in memory at any one time")
			.defaultValue("50").required(true).addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR).build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor EVICTION = new PropertyDescriptor.Builder().name("Eviction")
			.description("Delimited Output Channel Integration Eviction").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	private final EvictionBinManager binManager = new EvictionBinManager();

	private final Queue<EvictionBin> readyBins = new LinkedBlockingQueue<>();

	public static final Relationship REL_STOPPED = new Relationship.Builder().name("Stopped")
			.description("Stopped relationship").build();

	@OnStopped
	public final void resetState() {
		binManager.purge();
		EvictionBin bin;
		while ((bin = readyBins.poll()) != null) {

			if (bin != null && bin.getEvictionType().equalsIgnoreCase("record")) {
				bin.getSession().transfer(bin.getContents(), REL_STOPPED);
				bin.getSession().commit();
			} else {
				bin.getSession().rollback();
			}
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
	protected abstract void setUpBinManager(EvictionBinManager binManager, ProcessContext context);

	/**
	 * Processes a single bin. Implementing class is responsible for committing
	 * each session
	 */
	protected abstract void processBin(EvictionBin unmodifiableBin, ProcessContext context)
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

		if (!isScheduled()) {
			return;
		}
		final ComponentLog logger = getLogger();

		try {
			ProcessSession session = sessionFactory.createSession();
			ObjectMapper mapper = new ObjectMapper();
			final String evictionProperty = context.getProperty(EVICTION).evaluateAttributeExpressions().getValue();

			Eviction evictionObj = null;
			try {
				evictionObj = mapper.readValue(evictionProperty, Eviction.class);
				binManager.setEvictionObj(evictionObj);
				this.setUpBinManager(binManager, context);
			} catch (IOException e) {
				logger.error("Error occurred while reading Eviction property: " + e.getMessage());
				return;
			}

			boolean isBinned = binFlowFile(context, sessionFactory, session, logger, evictionObj);
			String evictionType = getEvictionType(evictionObj);
			performEvictionOnBinnedRecords(context, sessionFactory, session, evictionType, logger);
			final int binsProcessed = processBins(context);

		} catch (NifiCustomException ex) {
			logger.error("Error occurred at OutputFile Channel:  " + ex.getMessage());
			return;

		}
	}

	private String getEvictionType(Eviction evictionObj) {

		String evictionType = "";
		int timeEvictionCount = 0;
		int recordCount = evictionObj.getRecordCountBased();
		Map<String, Object> timeEviction = evictionObj.getTimeBased();
		if (timeEviction != null && timeEviction.containsKey("count")) {
			timeEvictionCount = (int) timeEviction.get("count");
		}

		if (timeEvictionCount > 0 && recordCount > 0) {
			// both time and record
			evictionType = "recordTime";
		} else if (timeEvictionCount > 0) {
			// only time
			evictionType = "time";
		} else if (recordCount > 0) {
			// only record
			evictionType = "record";
		} else {
			// no eviction
			evictionType = "none";
		}
		return evictionType;
	}

	private boolean binFlowFile(final ProcessContext context, final ProcessSessionFactory sessionFactory,
			final ProcessSession session, final ComponentLog logger, Eviction evictionObj) throws NifiCustomException {

		List<FlowFile> flowFiles = session.get(1);
		if (flowFiles.isEmpty()) {
			return false;
		}
		FlowFile flowFile = flowFiles.get(0);
		final String groupingIdentifier = getGroupId(context, flowFile); // this
																			// is
																			// transaction
																			// id
		boolean isBinned = binManager.offer(groupingIdentifier, flowFile, session, sessionFactory, logger, evictionObj);

		if (!isBinned) {
			session.penalize(flowFile);
			session.rollback(true);
		}

		return isBinned;
	}

	private void performEvictionOnBinnedRecords(final ProcessContext context, ProcessSessionFactory sessionFactory,
			ProcessSession session, String evictionType, ComponentLog logger) throws NifiCustomException {
		for (final EvictionBin bin : binManager.removeEvictionReadyBins(sessionFactory, session, evictionType,
				logger)) {
			if (bin == null) {
				// read more flowfiles to meet eviction parameters
				break;
			} else {
				this.readyBins.add(bin);
			}
		}
	}

	private int processBins(final ProcessContext context) {
		final ComponentLog logger = getLogger();
		int processedBins = 0;
		EvictionBin bin;

		while ((bin = readyBins.poll()) != null) {
			// String transactionId =
			// bin.getContents().get(0).getAttribute(Constants.TRANSACTION_ID);
			try {
				this.processBin(bin, context);

			} catch (final Exception e) {
				logger.error("error occured at outputchannel" + e.getMessage(), e);
			}
			processedBins++;
		}
		return processedBins;
	}

	@OnScheduled
	public  void onScheduled(final ProcessContext context) throws IOException {

	}

}
