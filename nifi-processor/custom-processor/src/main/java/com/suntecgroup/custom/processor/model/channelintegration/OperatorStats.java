package com.suntecgroup.custom.processor.model.channelintegration;

public class OperatorStats {
	private String sessionId;
	private String runNumber;
	private String operatorName;
	private int totalRequestsCount;
	private int totalRecordsCount;
	private int totalRecordsReceived;
	private int totalWrittenRecords;
	private int totalUnwrittenRecords;
	private int totalFilesWritten;
	private int totalSuccessRecordsCount;
	private int totalSuccessRequestsCount;
	private int totalFailureRecordsCount;
	private int totalFailureRequestsCount;

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

	public int getTotalFilesWritten() {
		return totalFilesWritten;
	}

	public void setTotalFilesWritten(int totalFilesWritten) {
		this.totalFilesWritten = totalFilesWritten;
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

	public void setTotalSuccessRequestsCount(int totalSucessRequestsCount) {
		this.totalSuccessRequestsCount = totalSucessRequestsCount;
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


}
