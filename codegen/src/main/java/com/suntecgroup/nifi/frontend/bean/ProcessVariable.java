package com.suntecgroup.nifi.frontend.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ProcessVariable {
	@SerializedName("decisionName")
	private String decisionName;

	@SerializedName("processVariable")
	private List<ProcessVariables> processVariable;

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
