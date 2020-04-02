package com.suntecgroup.bpruntime.bean.nifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "variable" })
public class Variable {

	@JsonProperty("variable")
	private Property variable;

	@JsonProperty("variable")
	public Property getVariable() {
		return variable;
	}

	@JsonProperty("variable")
	public void setVariable(Property variable) {
		this.variable = variable;
	}

}
