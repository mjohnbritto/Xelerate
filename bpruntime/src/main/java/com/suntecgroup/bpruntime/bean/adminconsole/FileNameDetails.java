package com.suntecgroup.bpruntime.bean.adminconsole;


public class FileNameDetails {
	private String sessionId;
	private String runNumber;
	private String fileName;
	private String receivedTime;
	private String lastModifiedTime;
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
