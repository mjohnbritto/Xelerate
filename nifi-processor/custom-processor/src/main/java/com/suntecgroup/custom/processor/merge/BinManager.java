/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessSessionFactory;
import org.apache.nifi.util.StringUtils;

import com.suntecgroup.custom.processor.utils.Constants;

/**
 * This class is thread safe
 *
 */
public class BinManager {

	private final AtomicInteger expectedFileCount = new AtomicInteger(0);
	private final AtomicReference<String> fileCountAttribute = new AtomicReference<>(null);

	private final AtomicInteger maxBinAgeSeconds = new AtomicInteger(Integer.MAX_VALUE);
	private final Map<String, Bin> groupBinMap = new HashMap<>();
	private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock rLock = rwLock.readLock();
	private final Lock wLock = rwLock.writeLock();
	private List<InputCVMapping> inputCVMappingList = null;
	private int binCount = 0; 
	private int maxBinCount = 100;

	public BinManager() {
	}

	public void purge() {
		wLock.lock();
		try {
			for (final Bin bin : groupBinMap.values()) {
				bin.getSession().rollback();
			}
			groupBinMap.clear();
			binCount = 0;
		} finally {
			wLock.unlock();
		}
	}

	public void setFileCountAttribute(final String fileCountAttribute) {
		this.fileCountAttribute.set(fileCountAttribute);
	}

	public String getFileCountAttribute() {
		return fileCountAttribute.get();
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

	public List<InputCVMapping> getInputCVMappingList() {
		return inputCVMappingList;
	}

	public void setInputCVMappingList(List<InputCVMapping> inputCVMappingList) {
		this.inputCVMappingList = inputCVMappingList;
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
	 */
	public boolean offer(final String groupIdentifier, FlowFile flowFile, final ProcessSession session,
			final ProcessSessionFactory sessionFactory) {
		boolean binned = false;
		wLock.lock();
		try {

			String isMarkerFile = flowFile.getAttribute(Constants.IS_MARKER);
			if (groupBinMap.containsKey(groupIdentifier)) {
				Bin bin = groupBinMap.get(groupIdentifier);
				if (StringUtils.isEmpty(isMarkerFile)) {
					if (!bin.isFirstDataFileReceived()) {
						bin.setFirstDataFileReceived(true);
						bin.setFirstDataFileAttr(flowFile.getAttributes());
					}
				}
				bin.offer(flowFile, session);
				binned = true;
			} else if (this.binCount < this.maxBinCount) {
				final Bin bin = new Bin(sessionFactory.createSession(), expectedFileCount.get(), inputCVMappingList);
				if (StringUtils.isEmpty(isMarkerFile)) {
					bin.setFirstDataFileReceived(true);
					bin.setFirstDataFileAttr(flowFile.getAttributes());
				}
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

	/**
	 * Finds all bins that are considered full and removes them from the
	 * manager.
	 * 
	 */
	public Collection<Bin> removeReadyBins() {
		final Map<String, Bin> newGroupMap = new HashMap<>();
		final List<Bin> readyBins = new ArrayList<>();
		wLock.lock();
		try {
			for (final Map.Entry<String, Bin> binEntry : groupBinMap.entrySet()) {
				Bin bin = binEntry.getValue();
				if (bin.isFull() || bin.isOlderThan(maxBinAgeSeconds.get(), TimeUnit.SECONDS)) {
					readyBins.add(bin);
				} else {
					newGroupMap.put(binEntry.getKey(), bin);
				}
			}
			groupBinMap.clear();
			groupBinMap.putAll(newGroupMap);
			binCount -= readyBins.size();
		} finally {
			wLock.unlock();
		}
		return readyBins;
	}

}
