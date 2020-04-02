/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor.merge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.FragmentAttributes;
import org.apache.nifi.processor.ProcessSession;

/**
 * Note: {@code Bin} objects are NOT thread safe. If multiple threads access a
 * {@code Bin}, the caller must synchronize access.
 */
public class Bin {

	public static final String FRAGMENT_INDEX_ATTRIBUTE = FragmentAttributes.FRAGMENT_INDEX.key();

	private final ProcessSession session;
	private final long creationMomentEpochNs;
	private final List<FlowFile> binContents = new ArrayList<>();
	private List<InputCVMapping> mapping;
	private int expectedFileCount = 0;
	private boolean firstDataFileReceived = false;
	private Map<String, String> firstDataFileAttr = null;

	/**
	 * Constructs a new bin
	 *
	 */

	public Bin(final ProcessSession session, final int expectedFileCount, List<InputCVMapping> mappingList) {
		this.session = session;
		this.creationMomentEpochNs = System.nanoTime();
		this.expectedFileCount = expectedFileCount;
		this.mapping = mappingList;
	}

	public ProcessSession getSession() {
		return session;
	}

	/**
	 * Indicates whether the bin has enough items to be considered full.
	 * 
	 * @return true if considered full; false otherwise
	 */
	public boolean isFull() {
		return (expectedFileCount == binContents.size());
	}

	/**
	 * Determines if this bin is older than the time specified.
	 *
	 */
	public boolean isOlderThan(final int duration, final TimeUnit unit) {
		final long ageInNanos = System.nanoTime() - creationMomentEpochNs;
		return ageInNanos > TimeUnit.NANOSECONDS.convert(duration, unit);
	}

	/**
	 * Determines if this bin is older than the specified bin
	 *
	 */
	public boolean isOlderThan(final Bin other) {
		return creationMomentEpochNs < other.creationMomentEpochNs;
	}

	/**
	 * If this bin has enough room for the size of the given flow file then it
	 * is added otherwise it is not
	 */
	public void offer(final FlowFile flowFile, final ProcessSession session) {
		session.migrate(getSession(), Collections.singleton(flowFile));
		binContents.add(flowFile);
	}

	private static final Pattern intPattern = Pattern.compile("\\d+");

	
	/**
	 * @return the underlying list of flow files within this bin
	 */
	public List<FlowFile> getContents() {
		return binContents;
	}

	public long getBinAge() {
		final long ageInNanos = System.nanoTime() - creationMomentEpochNs;
		return TimeUnit.MILLISECONDS.convert(ageInNanos, TimeUnit.NANOSECONDS);
	}

	public List<InputCVMapping> getMapping() {
		return mapping;
	}

	public void setMapping(List<InputCVMapping> mapping) {
		this.mapping = mapping;
	}

	public int getExpectedCount() {
		return expectedFileCount;
	}

	public void setExpectedCount(int expectedFileCount) {
		this.expectedFileCount = expectedFileCount;
	}

	public boolean isFirstDataFileReceived() {
		return firstDataFileReceived;
	}

	public void setFirstDataFileReceived(boolean firstDataFileReceived) {
		this.firstDataFileReceived = firstDataFileReceived;
	}

	public Map<String, String> getFirstDataFileAttr() {
		return firstDataFileAttr;
	}

	public void setFirstDataFileAttr(Map<String, String> firstDataFileAttr) {
		this.firstDataFileAttr = firstDataFileAttr;
	}
}
