/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.startandend;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "decisionName", "processVariable"})
public class DecisionProcessVariable {

	@JsonProperty("decisionName")
	private String decisionName;
	
	@JsonProperty("processVariable")
	private List<ProcessVariable> processVariable;

	@JsonProperty("decisionName")
	public String getDecisionName() {
		return decisionName;
	}
	
	@JsonProperty("decisionName")
	public void setDecisionName(String decisionName) {
		this.decisionName = decisionName;
	}
	
	@JsonProperty("processVariable")
	public List<ProcessVariable> getProcessVariable() {
		return processVariable;
	}

	@JsonProperty("processVariable")
	public void setProcessVariable(List<ProcessVariable> processVariable) {
		this.processVariable = processVariable;
	}
}