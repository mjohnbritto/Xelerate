package com.suntecgroup.nifi.frontend.bean;

import java.util.List;

public class StructuredProcessVariables {

	private String decisionName;

	private List<ProcessVariables> processVariables;

	public String getDecisionName() {
		return decisionName;
	}

	public void setDecisionName(String decisionName) {
		this.decisionName = decisionName;
	}

	public List<ProcessVariables> getProcessVariables() {
		return processVariables;
	}

	public void setProcessVariables(List<ProcessVariables> processVariables) {
		this.processVariables = processVariables;
	}

}