package com.suntecgroup.bp.designer.frontend.beans;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ProcessVariable {
	@SerializedName("key")
	private String key;
	
	@SerializedName("decisionName")
	private String decisionName;

	@SerializedName("processVariable")
	private List<ProcessVariables> processVariable;

	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDecisionName() {
		return decisionName;
	}

	public void setDecisionName(String decisionName) {
		this.decisionName = decisionName;
	}

	public List<ProcessVariables> getProcessVariable() {
		return processVariable;
	}

	public void setProcessVariable(List<ProcessVariables> processVariable) {
		this.processVariable = processVariable;
	}

}
