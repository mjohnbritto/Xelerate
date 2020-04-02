package com.suntecgroup.bpruntime.dao;

import java.util.List;

import com.suntecgroup.bpruntime.bean.adminconsole.FailedTransaction;

public interface TransactionDAO {

	public FailedTransaction saveFailedTransaction(FailedTransaction failedTransaction);

	public List<FailedTransaction> getFailedTransactionDetails(String sessionId, String runNumber, String errorType,
			boolean isAddressed, String action);

	public List<FailedTransaction> getFailedTransaction(String sessionId, String runNumber, String trasactionId);

}
