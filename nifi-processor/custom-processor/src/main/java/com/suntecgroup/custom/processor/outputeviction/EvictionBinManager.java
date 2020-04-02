/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor.outputeviction;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessSessionFactory;
import org.apache.nifi.processor.Relationship;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.Eviction;
import com.suntecgroup.custom.processor.utils.Constants;

/**
 * This class is thread safe
 *
 */
public class EvictionBinManager {

	private final AtomicInteger expectedFileCount = new AtomicInteger(0);

	private final AtomicInteger maxBinAgeSeconds = new AtomicInteger(Integer.MAX_VALUE);
	private final Map<String, EvictionBin> groupBinMap = new HashMap<>();
	private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock rLock = rwLock.readLock();
	private final Lock wLock = rwLock.writeLock();

	private int binCount = 0;
	private int maxBinCount = 100;

	private Eviction evictionObj = null;
	private int recordEviction = 0;
	private long proposedTimeEviction = 0;
	private long proposedTimeIdleEviction = 0;
	private long currentTime = 0;

	public static final Relationship REL_STOPPED = new Relationship.Builder().name("Stopped")
			.description("Stopped relationship").build();

	public EvictionBinManager() {
	}

	public void purge() {
		wLock.lock();
		try {
			for (final EvictionBin bin : groupBinMap.values()) {
				if (bin != null && bin.getEvictionType().equalsIgnoreCase("record")) {
					bin.getSession().transfer(bin.getContents(), REL_STOPPED);
					bin.getSession().commit();
				} else {
					bin.getSession().rollback();
				}
			}
			groupBinMap.clear();
			binCount = 0;
		} finally {
			wLock.unlock();
		}
	}

	public int getBinCount() {
		rLock.lock();
		try {
			return binCount;
		} finally {
			rLock.unlock();
		}
	}

	public void setMaxBinAge(final int seconds) {
		maxBinAgeSeconds.set(seconds);
	}

	public Eviction getEvictionObj() {
		return evictionObj;
	}

	public void setEvictionObj(Eviction evictionObj) {
		this.evictionObj = evictionObj;
	}

	public int getRecordEviction() {
		return recordEviction;
	}

	public void setRecordEviction(int recordEviction) {
		this.recordEviction = recordEviction;
	}

	public long getProposedTimeEviction() {
		return proposedTimeEviction;
	}

	public void setProposedTimeEviction(long proposedTimeEviction) {
		this.proposedTimeEviction = proposedTimeEviction;
	}

	public long getProposedTimeIdleEviction() {
		return proposedTimeIdleEviction;
	}

	public void setProposedTimeIdleEviction(long proposedTimeIdleEviction) {
		this.proposedTimeIdleEviction = proposedTimeIdleEviction;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	public int getMaxBinCount() {
		return maxBinCount;
	}

	public void setMaxBinCount(int maxBinCount) {
		this.maxBinCount = maxBinCount;
	}

	public AtomicInteger getExpectedFileCount() {
		return expectedFileCount;
	}

	public void setExpectedFileCount(final int fileCount) {
		this.expectedFileCount.set(fileCount);
	}

	/**
	 * Adds the given flowFiles to the first available bin in which it fits for
	 * the given group or creates a new bin in the specified group if necessary.
	 * 
	 * @throws NifiCustomException
	 *
	 */
	public boolean offer(final String groupIdentifier, FlowFile flowFile, final ProcessSession session,
			final ProcessSessionFactory sessionFactory, final ComponentLog logger, Eviction evictionObj)
			throws NifiCustomException {
		boolean binned = false;
		wLock.lock();

		try {
			if (groupBinMap.containsKey(groupIdentifier)) {
				EvictionBin bin = groupBinMap.get(groupIdentifier);
				groupBinMap.put(groupIdentifier, bin);
				bin.offer(flowFile, session);
				bin.setFlowfileCount(bin.getFlowfileCount() + 1);
				bin.setUpdationMomentEpochNs(System.nanoTime());
				binned = true;
			} else if (this.binCount < this.maxBinCount) {
				final EvictionBin bin = new EvictionBin(sessionFactory.createSession(), evictionObj, 1);
				bin.offer(flowFile, session);
				groupBinMap.put(groupIdentifier, bin);
				binned = true;
				this.binCount++;
			} else {
				binned = false;
			}
		} finally {
			wLock.unlock();
		}
		return binned;
	}

	public List<Map<String, Object>> getFlowFileContent(final ProcessSession processSession, FlowFile flowFileObj,
			final ComponentLog logger) throws NifiCustomException {

		List<Map<String, Object>> outputFlowFileList = new ArrayList<Map<String, Object>>();
		JsonReader reader = null;
		InputStream outputChannelInput = null;
		try {

			outputChannelInput = processSession.read(flowFileObj);

			if (outputChannelInput != null) {
				reader = new JsonReader(new InputStreamReader(outputChannelInput, Constants.UTF_ENCODING));
				Gson gson = new GsonBuilder().create();
				Type type = new TypeToken<Map<String, Object>>() {
				}.getType();
				reader.beginArray();

				while (reader.hasNext()) {
					Map<String, Object> jsonRecord = gson.fromJson(reader, type);
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
				logger.error(" IOException while closing stream");
			}
		}
		return outputFlowFileList;
	}

	public Collection<EvictionBin> removeEvictionReadyBins(ProcessSessionFactory sessionFactory, ProcessSession session,
			String evictionType, ComponentLog logger) throws NifiCustomException {

		final Map<String, EvictionBin> newGroupMap = new HashMap<>();
		final List<EvictionBin> readyBins = new ArrayList<>();
		wLock.lock();

		try {
			if (evictionType.equalsIgnoreCase("time")) {

				for (final Map.Entry<String, EvictionBin> binEntry : groupBinMap.entrySet()) {
					EvictionBin bin = binEntry.getValue();
					Eviction eviction = bin.getEvictionObj();
					bin.setEvictionType("time");

					boolean isIdleTimeReached = false;
					int idleCount = (int) eviction.getIdleTime().get("count");
					if (idleCount > 0) {
						isIdleTimeReached = isIdleReached(bin, eviction);
					}
					boolean isTimeLimitReached = isTimeReached(bin, eviction);

					if (isTimeLimitReached || isIdleTimeReached) {
						readyBins.add(bin);
					} else {
						newGroupMap.put(binEntry.getKey(), bin);
					}
				}
			}

			else if (evictionType.equalsIgnoreCase("recordTime")) {

				getRecordBasedReadyBins(sessionFactory, session, logger, newGroupMap, readyBins, "recordTime");

			}

			else if (evictionType.equalsIgnoreCase("record")) {
				getRecordBasedReadyBins(sessionFactory, session, logger, newGroupMap, readyBins, "record");
			}

			else {
				EvictionBin bin = null;
				for (final Map.Entry<String, EvictionBin> binEntry : groupBinMap.entrySet()) {
					bin = binEntry.getValue();
					bin.setEvictionType("none");
					readyBins.add(bin);
				}
			}
			groupBinMap.clear();
			groupBinMap.putAll(newGroupMap);
			binCount -= readyBins.size();
		} catch (Exception ex) {
			logger.error("Exception occured at removeEvictionReadyBins: " + ex, ex);
		}

		finally {
			wLock.unlock();
		}
		return readyBins;
	}

	private void getRecordBasedReadyBins(ProcessSessionFactory sessionFactory, ProcessSession session,
			ComponentLog logger, final Map<String, EvictionBin> newGroupMap, final List<EvictionBin> readyBins,
			String evictionType) throws NifiCustomException {
		for (final Map.Entry<String, EvictionBin> binEntry : groupBinMap.entrySet()) {

			String groupId = binEntry.getKey();
			EvictionBin bin = binEntry.getValue();
			// session = bin.getSession();
			Eviction eviction = bin.getEvictionObj();
			int recordCount = eviction.getRecordCountBased();

			List<FlowFile> binContents = bin.getContents();

			List<Map<String, Object>> totalRecords = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> processedRecords = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> unProcessedRecords = new ArrayList<Map<String, Object>>();

			boolean isProcessed = false;
			boolean isUnProcessed = false;
			FlowFile flowFileTemp = null;
			FlowFile flowFileTempReturn = null;

			int count = 0;
			boolean isIdleTimeReached = isIdleReached(bin, eviction);

			if (bin.isUnProcessedRecord() && bin.getEvictionType().equalsIgnoreCase("record")
					&& bin.getFlowfileCount() == 1) {

				if (isIdleTimeReached
						|| (evictionType.equalsIgnoreCase("recordTime") && isTimeReached(bin, eviction))) {

					prepareRecordBin(sessionFactory, logger, newGroupMap, readyBins, evictionType, groupId, bin,
							eviction, recordCount, isIdleTimeReached, binContents, totalRecords, processedRecords,
							unProcessedRecords, isProcessed, isUnProcessed, flowFileTemp, flowFileTempReturn, count);

				} else {

					bin.setEvictionType("record");
					bin.setRecordEvictionCount(recordCount);
					bin.setUnProcessedRecord(true);
					newGroupMap.put(groupId, bin);
				}
			}

			else {
				prepareRecordBin(sessionFactory, logger, newGroupMap, readyBins, evictionType, groupId, bin, eviction,
						recordCount, isIdleTimeReached, binContents, totalRecords, processedRecords, unProcessedRecords,
						isProcessed, isUnProcessed, flowFileTemp, flowFileTempReturn, count);
			}

		}
	}

	private void prepareRecordBin(ProcessSessionFactory sessionFactory, ComponentLog logger,
			final Map<String, EvictionBin> newGroupMap, final List<EvictionBin> readyBins, String evictionType,
			String groupId, EvictionBin bin, Eviction eviction, int recordCount, boolean isIdleTimeReached,
			List<FlowFile> binContents, List<Map<String, Object>> totalRecords,
			List<Map<String, Object>> processedRecords, List<Map<String, Object>> unProcessedRecords,
			boolean isProcessed, boolean isUnProcessed, FlowFile flowFileTemp, FlowFile flowFileTempReturn, int count)
			throws NifiCustomException {
		for (FlowFile flowFileObj : binContents) {
			count++;
			if (count == 1) {
				flowFileTemp = bin.getSession().clone(flowFileObj);
				flowFileTempReturn = bin.getSession().clone(flowFileObj);
			}

			List<Map<String, Object>> recordList = getFlowFileContent(bin.getSession(), flowFileObj, logger);
			totalRecords.addAll(recordList);
			bin.getSession().remove(flowFileObj);
		}

		int recordSize = totalRecords.size();
		int remainder = recordSize % recordCount;

		for (int i = 0; i < (recordSize - remainder); i++) {
			processedRecords.add(totalRecords.get(i));
		}
		for (int i = (recordSize - remainder); i < recordSize; i++) {
			unProcessedRecords.add(totalRecords.get(i));
		}

		if (isIdleTimeReached && unProcessedRecords.size() > 0) {
			// if IdleTime Reached, then dont separate the records into
			// multiple flowfile and club all into one
			processedRecords.addAll(unProcessedRecords);
			unProcessedRecords = new ArrayList<Map<String, Object>>();
		}
		if (evictionType.equalsIgnoreCase("recordTime") && unProcessedRecords.size() > 0) {

			boolean isTimeLimitReached = isTimeReached(bin, eviction);

			if (isTimeLimitReached) {
				processedRecords.addAll(unProcessedRecords);
				unProcessedRecords = new ArrayList<Map<String, Object>>();
			}
		}

		if (processedRecords.size() > 0) {
			isProcessed = true;
			writeFlowFileContent(bin.getSession(), processedRecords, flowFileTemp);
			final EvictionBin binNew = new EvictionBin(sessionFactory.createSession(), evictionObj, 1);
			binNew.setCreationMomentEpochNs(bin.getCreationMomentEpochNs());
			binNew.setUpdationMomentEpochNs(bin.getUpdationMomentEpochNs());
			binNew.setEvictionType("record");
			binNew.setRecordEvictionCount(recordCount);
			binNew.setUnProcessedRecord(false);
			binNew.offer(flowFileTemp, bin.getSession());
			readyBins.add(binNew);

		}
		if (unProcessedRecords.size() > 0) {
			isUnProcessed = true;
			writeFlowFileContent(bin.getSession(), unProcessedRecords, flowFileTempReturn);
			final EvictionBin binNew = new EvictionBin(sessionFactory.createSession(), evictionObj, 1);

			if (isProcessed) {
				binNew.setCreationMomentEpochNs(System.nanoTime());
				binNew.setUpdationMomentEpochNs(System.nanoTime());
			} else {
				binNew.setCreationMomentEpochNs(bin.getCreationMomentEpochNs());
				binNew.setUpdationMomentEpochNs(bin.getUpdationMomentEpochNs());
			}

			binNew.setEvictionType("record");
			binNew.setRecordEvictionCount(recordCount);
			binNew.setUnProcessedRecord(true);
			binNew.offer(flowFileTempReturn, bin.getSession());
			newGroupMap.put(groupId, binNew);
		}

		// bin.getSession().remove(binContents);

		if (isProcessed && !isUnProcessed) {
			bin.getSession().remove(flowFileTempReturn);
		} else if (!isProcessed && isUnProcessed) {
			bin.getSession().remove(flowFileTemp);
		} else if (!isProcessed && !isUnProcessed) {
			bin.getSession().remove(flowFileTemp);
			bin.getSession().remove(flowFileTempReturn);
		}
		bin.getSession().commit();
	}

	private boolean writeFlowFileContent(final ProcessSession processSession,
			List<Map<String, Object>> resultMappedList, FlowFile flowFileOutput) throws NifiCustomException {
		OutputStream writeOutputStream = null;
		JsonWriter writer = null;
		boolean isSuccess = false;

		try {
			writeOutputStream = processSession.write(flowFileOutput);
			writer = new JsonWriter(new OutputStreamWriter(writeOutputStream, Constants.UTF_ENCODING));
			Gson gsonWriter = new GsonBuilder().create();

			Type typeWriter = new TypeToken<Map<String, Object>>() {
			}.getType();
			writer.beginArray();

			for (int i = 0; i < resultMappedList.size(); i++) {

				Map<String, Object> resultMap = resultMappedList.get(i);
				gsonWriter.toJson(resultMap, typeWriter, writer);
			}

			writer.endArray();
			isSuccess = true;

		} catch (Exception e) {
			throw new NifiCustomException(" failed due to error while writing the flowfile");

		} finally {

			try {
				if (writer != null) {
					writer.close();
				}

				if (writeOutputStream != null) {
					writeOutputStream.close();
				}

			} catch (IOException io) {
				throw new NifiCustomException(" failed due to error while writing the flowfile");
			}

		}
		return isSuccess;
	}

	private boolean isIdleReached(EvictionBin bin, Eviction eviction) {
		boolean isIdleTimeReached = false;
		int idleEvictionCount = 0;
		String idleDuration = null;
		Map<String, Object> idleEviction = eviction.getIdleTime();
		if (idleEviction != null && idleEviction.containsKey("count")) {
			idleEvictionCount = (int) idleEviction.get("count");
		}

		String eventBasedTime = eviction.getEventBased();
		if (eventBasedTime != null && eventBasedTime.trim().equalsIgnoreCase("NOTHING-TO-PROCESS-FURTHER")) {
			if (idleEvictionCount > 0) {
				idleDuration = (String) idleEviction.get("duration");
				if (idleDuration != null && idleDuration.contains("secs")) {
					isIdleTimeReached = bin.isIdleBasedEvictionReached(idleEvictionCount, TimeUnit.SECONDS);

				} else if (idleDuration != null && idleDuration.contains("mins")) {
					isIdleTimeReached = bin.isIdleBasedEvictionReached(idleEvictionCount, TimeUnit.MINUTES);
				}
			}
		}
		return isIdleTimeReached;
	}

	private boolean isTimeReached(EvictionBin bin, Eviction eviction) {

		boolean isTimeLimitReached = false;
		Map<String, Object> timeEviction = eviction.getTimeBased();
		int durationLength = (int) timeEviction.get("count");
		String duration = (String) timeEviction.get("duration");

		if (duration != null && duration.contains("secs")) {

			isTimeLimitReached = bin.isTimeBasedEvictionReached(durationLength, TimeUnit.SECONDS);
		}

		else if (duration != null && duration.contains("mins")) {
			isTimeLimitReached = bin.isTimeBasedEvictionReached(durationLength, TimeUnit.MINUTES);
		}

		return isTimeLimitReached;
	}

}
