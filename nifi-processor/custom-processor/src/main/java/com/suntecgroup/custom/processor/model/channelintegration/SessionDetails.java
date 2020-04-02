package com.suntecgroup.custom.processor.model.channelintegration;

public class SessionDetails {
	
	private String sessionId;
	private String runNumber;
	private String OperatorName;
	private String fileName;
	private int receivedFilesCount;
	private int acceptedFilesCount;
	private int rejectedFilesCount;
	private int totalRecordsCount;
	private int acceptedRecordsCount;
	private int rejectedRecordsCount;
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
		return OperatorName;
	}
	public void setOperatorName(String operatorName) {
		OperatorName = operatorName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	public int getTotalRecordsCount() {
		return totalRecordsCount;
	}
	public void setTotalRecordsCount(int totalRecordsCount) {
		this.totalRecordsCount = totalRecordsCount;
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
	
	

}
