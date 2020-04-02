package com.suntecgroup.bpruntime.bean.adminconsole;

import java.util.ArrayList;

public class ReplayData {

	private String sessionId;
	private String runNumber;
	private String templateId;
	private ArrayList<String> flowfileUUIDs;
	private String errorType;

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

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public ArrayList<String> getFlowfileUUIDs() {
		return flowfileUUIDs;
	}

	public void setFlowfileUUIDs(ArrayList<String> flowfileUUIDs) {
		this.flowfileUUIDs = flowfileUUIDs;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

}
