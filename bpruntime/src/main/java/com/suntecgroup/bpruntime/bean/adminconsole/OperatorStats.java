package com.suntecgroup.bpruntime.bean.adminconsole;


public class OperatorStats {

//	@Id
//	private OperatorStatsCompKey operatorStatsCompKey;
	private String sessionId;
	private String runNumber;
	private String operatorName;
	//File Input Channel
	private int receivedFilesCount;
	private int acceptedFilesCount;
	private int rejectedFilesCount;
	private int totalRecordsCount;
	private int acceptedRecordsCount;
	private int rejectedRecordsCount;
	//Rest Input Channel
	private int totalRequestsCount;
	
	//Rest OutPut Channel
	private int totalSuccessRecordsCount;
	private int totalSuccessRequestsCount;
	private int totalFailureRecordsCount;
	private int totalFailureRequestsCount;
	
	//File Output Channel
	private int totalFilesWritten;
	private int totalRecordsReceived;
	private int totalWrittenRecords;
	private int totalUnwrittenRecords;
	
	private String receivedTime;
	private String lastModifiedTime;
	
	
	/*public OperatorStatsCompKey getOperatorStatsCompKey() {
		return operatorStatsCompKey;
	}
	public void setOperatorStatsCompKey(OperatorStatsCompKey operatorStatsCompKey) {
		this.operatorStatsCompKey = operatorStatsCompKey;
	}*/
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getRunNumber() {
		return runNumber;
	}
	public void setRunNumber(String runNumber) {
		this.runNumber = runNumber;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	
	public int getReceivedFilesCount() {
		return receivedFilesCount;
	}
	public void setReceivedFilesCount(int receivedFilesCount) {
		this.receivedFilesCount = receivedFilesCount;
	}
	public int getAcceptedFilesCount() {
		return acceptedFilesCount;
	}
	public void setAcceptedFilesCount(int acceptedFilesCount) {
		this.acceptedFilesCount = acceptedFilesCount;
	}
	public int getRejectedFilesCount() {
		return rejectedFilesCount;
	}
	public void setRejectedFilesCount(int rejectedFilesCount) {
		this.rejectedFilesCount = rejectedFilesCount;
	}
	public int getAcceptedRecordsCount() {
		return acceptedRecordsCount;
	}
	public void setAcceptedRecordsCount(int acceptedRecordsCount) {
		this.acceptedRecordsCount = acceptedRecordsCount;
	}
	public int getRejectedRecordsCount() {
		return rejectedRecordsCount;
	}
	public void setRejectedRecordsCount(int rejectedRecordsCount) {
		this.rejectedRecordsCount = rejectedRecordsCount;
	}
	public int getTotalRequestsCount() {
		return totalRequestsCount;
	}
	public void setTotalRequestsCount(int totalRequestsCount) {
		this.totalRequestsCount = totalRequestsCount;
	}
	public int getTotalRecordsCount() {
		return totalRecordsCount;
	}
	public void setTotalRecordsCount(int totalRecordsCount) {
		this.totalRecordsCount = totalRecordsCount;
	}
	
	public int getTotalSuccessRecordsCount() {
		return totalSuccessRecordsCount;
	}
	public void setTotalSuccessRecordsCount(int totalSuccessRecordsCount) {
		this.totalSuccessRecordsCount = totalSuccessRecordsCount;
	}
	public int getTotalSuccessRequestsCount() {
		return totalSuccessRequestsCount;
	}
	public void setTotalSuccessRequestsCount(int totalSuccessRequestsCount) {
		this.totalSuccessRequestsCount = totalSuccessRequestsCount;
	}
	public int getTotalFailureRecordsCount() {
		return totalFailureRecordsCount;
	}
	public void setTotalFailureRecordsCount(int totalFailureRecordsCount) {
		this.totalFailureRecordsCount = totalFailureRecordsCount;
	}
	public int getTotalFailureRequestsCount() {
		return totalFailureRequestsCount;
	}
	public void setTotalFailureRequestsCount(int totalFailureRequestsCount) {
		this.totalFailureRequestsCount = totalFailureRequestsCount;
	}
	public int getTotalFilesWritten() {
		return totalFilesWritten;
	}
	public void setTotalFilesWritten(int totalFilesWritten) {
		this.totalFilesWritten = totalFilesWritten;
	}
	public int getTotalRecordsReceived() {
		return totalRecordsReceived;
	}
	public void setTotalRecordsReceived(int totalRecordsReceived) {
		this.totalRecordsReceived = totalRecordsReceived;
	}
	public int getTotalWrittenRecords() {
		return totalWrittenRecords;
	}
	public void setTotalWrittenRecords(int totalWrittenRecords) {
		this.totalWrittenRecords = totalWrittenRecords;
	}
	public int getTotalUnwrittenRecords() {
		return totalUnwrittenRecords;
	}
	public void setTotalUnwrittenRecords(int totalUnwrittenRecords) {
		this.totalUnwrittenRecords = totalUnwrittenRecords;
	}
	public String getReceivedTime() {
		return receivedTime;
	}
	public void setReceivedTime(String receivedTime) {
		this.receivedTime = receivedTime;
	}
	public String getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
	
}
