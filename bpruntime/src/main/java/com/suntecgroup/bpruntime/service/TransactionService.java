package com.suntecgroup.bpruntime.service;

import java.util.List;

import com.suntecgroup.bpruntime.bean.adminconsole.TransactionDetail;

public interface TransactionService {

	public void saveFailedTransaction(TransactionDetail transactionDetail);

	public List<?> getFailedTransactionDetails(String sessionId, String runNumber, String errorType,
			boolean isAddressed);

	public Long getFailedTransactionCount(String sessionId, String runNumber, String errorType, boolean isAddressed,
			String action);

	public String addressBusinessFailure(List<TransactionDetail> transactionList);
}