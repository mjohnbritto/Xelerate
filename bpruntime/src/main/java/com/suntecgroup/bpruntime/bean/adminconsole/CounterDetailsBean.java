/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.suntecgroup.bpruntime.bean.adminconsole;

import java.util.HashMap;
import java.util.Map;

public class CounterDetailsBean {

	private TransactionCounters masterCounter = new TransactionCounters();

	private Map<String, TransactionCounters> transactionsMap = new HashMap<>();

	public TransactionCounters getMasterCounter() {
		return masterCounter;
	}

	public void setMasterCounter(TransactionCounters masterCounter) {
		this.masterCounter = masterCounter;
	}

	public Map<String, TransactionCounters> getTransactionsMap() {
		return transactionsMap;
	}

	public void setTransactionsMap(Map<String, TransactionCounters> transactionsMap) {
		this.transactionsMap = transactionsMap;
	}

	// Adding new transaction - start operator
	public void addTransaction(String transactionId, int eventsCount) {
		TransactionCounters tc = new TransactionCounters();
		tc.setTransactionTotalCount(1);
		tc.setTransactionSuccessCount(0);
		tc.setTransactionFailureCount(0);
		tc.setTransactionTechnicalFailureCount(0);
		tc.setTransactionBusinessFailureCount(0);
		tc.setEventTotalCount(eventsCount);
		transactionsMap.put(transactionId, tc);
		masterCounter.incrementTotalCount(eventsCount);
	}

	// Mark transaction as success - end operator
	public void markTransactionSuccess(String transactionId) {

		TransactionCounters tc = transactionsMap.get(transactionId);
		if (tc.getTransactionSuccessCount() == 0 && tc.getTransactionTechnicalFailureCount() == 0
				&& tc.getTransactionBusinessFailureCount() == 0) {
			masterCounter.incrementSuccessCount(tc.getEventTotalCount());
		}
		int count = tc.getTransactionSuccessCount();
		tc.setTransactionSuccessCount(++count);
		tc.setEventSuccessCount(tc.getEventSuccessCount() + tc.getEventTotalCount());
	}

	/*
	 * // Mark transaction as failure - failure operator public void
	 * markTransactionFailure(String transactionId) {
	 * 
	 * TransactionCounters tc = transactionsMap.get(transactionId);
	 * 
	 * if (tc.getTransactionSuccessCount() == 0 &&
	 * tc.getTransactionFailureCount() == 0) {
	 * masterCounter.incrementFailureCount(tc.getEventTotalCount()); } if
	 * (tc.getTransactionSuccessCount() > 0 && tc.getTransactionFailureCount()
	 * == 0) { masterCounter.decrementSuccessCount(tc.getEventTotalCount());
	 * masterCounter.incrementFailureCount(tc.getEventTotalCount()); } int count
	 * = tc.getTransactionFailureCount();
	 * tc.setTransactionFailureCount(++count);
	 * tc.setEventFailureCount(tc.getEventFailureCount()+tc.getEventTotalCount()
	 * ); }
	 */

	// Mark transaction as failure - failure operator (business failure)
	public boolean markTransactionBusinessFailure(String transactionId) {
		
		boolean masterUpdate = false;
		TransactionCounters tc = transactionsMap.get(transactionId);

		if (tc.getTransactionSuccessCount() == 0 && tc.getTransactionBusinessFailureCount() == 0
				&& tc.getTransactionTechnicalFailureCount() == 0) {
			masterCounter.incrementBusinessFailureCount(tc.getEventTotalCount());
			masterUpdate = true;
		}
		if (tc.getTransactionSuccessCount() > 0 && tc.getTransactionBusinessFailureCount() == 0
				&& tc.getTransactionTechnicalFailureCount() == 0) {
			masterCounter.decrementSuccessCount(tc.getEventTotalCount());
			masterCounter.incrementBusinessFailureCount(tc.getEventTotalCount());
			masterUpdate = true;
		}
		int count = tc.getTransactionBusinessFailureCount();
		tc.setTransactionFailureCount(tc.getTransactionFailureCount() + count);
		tc.setTransactionBusinessFailureCount(++count);
		tc.setEventBusinessFailureCount(tc.getEventBusinessFailureCount() + tc.getEventTotalCount());
		return masterUpdate;
	}

	// Mark transaction as failure - failure operator (technical failure)
	public boolean markTransactionTechnicalFailure(String transactionId) {
		boolean masterUpdate = false;
		TransactionCounters tc = transactionsMap.get(transactionId);

		if (tc.getTransactionSuccessCount() == 0 && tc.getTransactionBusinessFailureCount() == 0
				&& tc.getTransactionTechnicalFailureCount() == 0) {
			masterCounter.incrementTechnicalFailureCount(tc.getEventTotalCount());
			masterUpdate = true;
		}
		if (tc.getTransactionSuccessCount() > 0 && tc.getTransactionBusinessFailureCount() == 0
				&& tc.getTransactionTechnicalFailureCount() == 0) {
			masterCounter.decrementSuccessCount(tc.getEventTotalCount());
			masterCounter.incrementTechnicalFailureCount(tc.getEventTotalCount());
			masterUpdate = true;
		}
		int count = tc.getTransactionTechnicalFailureCount();
		tc.setTransactionFailureCount(tc.getTransactionFailureCount() + count);
		tc.setTransactionTechnicalFailureCount(++count);
		tc.setEventTechnicalFailureCount(tc.getEventTechnicalFailureCount() + tc.getEventTotalCount());
		return masterUpdate;
	}

}
