/* 

 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor.outputeviction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.FragmentAttributes;
import org.apache.nifi.processor.ProcessSession;

import com.suntecgroup.custom.processor.model.channelintegration.outputchannel.Eviction;

/**
 * Note: {@code Bin} objects are NOT thread safe. If multiple threads access a
 * {@code Bin}, the caller must synchronize access.
 */
public class EvictionBin {

	public static final String FRAGMENT_INDEX_ATTRIBUTE = FragmentAttributes.FRAGMENT_INDEX.key();

	private final ProcessSession session;
	private long creationMomentEpochNs;
	private long updationMomentEpochNs;
	private int flowfileCount;

	private final List<FlowFile> binContents = new ArrayList<>();
	private int expectedFileCount = 0;
	private Eviction evictionObj;
	private String evictionType = null;
	private int recordEvictionCount = 0;
	private boolean unProcessedRecord;

	/**
	 * Constructs a new bin
	 */
	public EvictionBin(final ProcessSession session, Eviction evictionObj, int flowfileCount) {
		this.session = session;
		this.creationMomentEpochNs = System.nanoTime();
		this.updationMomentEpochNs = System.nanoTime();
		this.evictionObj = evictionObj;
		this.flowfileCount = flowfileCount;
	}

	/**
	 * Determines if this bin is older than the time specified.
	 *
	 */
	public boolean isTimeBasedEvictionReached(final int duration, final TimeUnit unit) {
		final long ageInNanos = System.nanoTime() - creationMomentEpochNs;
		return ageInNanos > TimeUnit.NANOSECONDS.convert(duration, unit);
	}

	/**
	 * Determines if this bin is older than the time specified.
	 *
	 */
	public boolean isIdleBasedEvictionReached(final int duration, final TimeUnit unit) {
		final long ageInNanos = System.nanoTime() - updationMomentEpochNs;
		return ageInNanos > TimeUnit.NANOSECONDS.convert(duration, unit);
	}

	public ProcessSession getSession() {
		return session;
	}

	/**
	 * If this bin has enough room for the size of the given flow file then it
	 * is added otherwise it is not
	 */
	public void offer(final FlowFile flowFile, final ProcessSession session) {
		session.migrate(getSession(), Collections.singleton(flowFile));
		binContents.add(flowFile);

	}

	/**
	 * @return the underlying list of flow files within this bin
	 */
	public List<FlowFile> getContents() {
		return binContents;
	}

	public Eviction getEvictionObj() {
		return evictionObj;
	}

	public void setEvictionObj(Eviction evictionObj) {
		this.evictionObj = evictionObj;
	}

	public long getCreationMomentEpochNs() {
		return creationMomentEpochNs;
	}

	public void setCreationMomentEpochNs(long creationMomentEpochNs) {
		this.creationMomentEpochNs = creationMomentEpochNs;
	}

	public long getUpdationMomentEpochNs() {
		return updationMomentEpochNs;
	}

	public void setUpdationMomentEpochNs(long updationMomentEpochNs) {
		this.updationMomentEpochNs = updationMomentEpochNs;
	}

	public int getFlowfileCount() {
		return flowfileCount;
	}

	public void setFlowfileCount(int flowfileCount) {
		this.flowfileCount = flowfileCount;
	}

	public String getEvictionType() {
		return evictionType;
	}

	public void setEvictionType(String evictionType) {
		this.evictionType = evictionType;
	}

	public int getExpectedCount() {
		return expectedFileCount;
	}

	public void setExpectedCount(int expectedFileCount) {
		this.expectedFileCount = expectedFileCount;
	}

	public int getRecordEvictionCount() {
		return recordEvictionCount;
	}

	public void setRecordEvictionCount(int recordEvictionCount) {
		this.recordEvictionCount = recordEvictionCount;
	}

	public boolean isUnProcessedRecord() {
		return unProcessedRecord;
	}

	public void setUnProcessedRecord(boolean unProcessedRecord) {
		this.unProcessedRecord = unProcessedRecord;
	}

}