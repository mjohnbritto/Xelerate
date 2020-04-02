package com.suntecgroup.bpruntime.service;

import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.BPState;
import com.suntecgroup.bpruntime.bean.adminconsole.OperatorStats;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionState;
import com.suntecgroup.bpruntime.bean.adminconsole.TransactionDetail;

public interface SessionManagerService {

	public String getSessionList(String templateId);

	public ApiResponse<String> startSession(BPState bpState);

	public ApiResponse<String> stopSession(SessionState sessionState);

	public String getBpStatus(String templateId);

	public int getSessionCountForBp(String templateId);

	public String updateTransactionStatus(TransactionDetail transactionDtl);
	
	public String updateFilesRecStatus(TransactionDetail transactionDtl);
	
	public String getFilesNameStatus(TransactionDetail transactionDtl);
	
	public String updateOperatorStats(TransactionDetail operatorStats);
	
	public String getOperatorStats(TransactionDetail operatorDtl); 
}
