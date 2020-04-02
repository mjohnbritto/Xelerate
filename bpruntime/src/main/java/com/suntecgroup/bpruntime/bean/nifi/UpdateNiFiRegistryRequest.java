package com.suntecgroup.bpruntime.bean.nifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "processGroupRevision", "variableRegistry" })
public class UpdateNiFiRegistryRequest {

	@JsonProperty("processGroupRevision")
	private ProcessGroupRevision processGroupRevision;
	@JsonProperty("variableRegistry")
	private VariableRegistry variableRegistry;

	@JsonProperty("processGroupRevision")
	public ProcessGroupRevision getProcessGroupRevision() {
		return processGroupRevision;
	}

	@JsonProperty("processGroupRevision")
	public void setProcessGroupRevision(
			ProcessGroupRevision processGroupRevision) {
		this.processGroupRevision = processGroupRevision;
	}

	@JsonProperty("variableRegistry")
	public VariableRegistry getVariableRegistry() {
		return variableRegistry;
	}

	@JsonProperty("variableRegistry")
	public void setVariableRegistry(VariableRegistry variableRegistry) {
		this.variableRegistry = variableRegistry;
	}

}
