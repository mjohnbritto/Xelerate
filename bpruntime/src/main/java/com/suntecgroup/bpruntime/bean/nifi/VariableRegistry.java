package com.suntecgroup.bpruntime.bean.nifi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "variables", "processGroupId" })
public class VariableRegistry {

	@JsonProperty("variables")
	private List<Variable> variables = null;
	@JsonProperty("processGroupId")
	private String processGroupId;

	@JsonProperty("variables")
	public List<Variable> getVariables() {
		return variables;
	}

	@JsonProperty("variables")
	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	@JsonProperty("processGroupId")
	public String getProcessGroupId() {
		return processGroupId;
	}

	@JsonProperty("processGroupId")
	public void setProcessGroupId(String processGroupId) {
		this.processGroupId = processGroupId;
	}

}
