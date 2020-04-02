package com.suntecgroup.nifi.frontend.bean.filechannelintegration.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "customValue", "operator", "selectedValue", "type" })
public class Rule {

	@JsonProperty("customValue")
	private String customValue;
	@JsonProperty("operator")
	private String operator;
	@JsonProperty("selectedValue")
	private String selectedValue;
	@JsonProperty("type")
	private String type;

	@JsonProperty("customValue")
	public String getCustomValue() {
		return customValue;
	}

	@JsonProperty("customValue")
	public void setCustomValue(String customValue) {
		this.customValue = customValue;
	}

	@JsonProperty("operator")
	public String getOperator() {
		return operator;
	}

	@JsonProperty("operator")
	public void setOperator(String operator) {
		this.operator = operator;
	}

	@JsonProperty("selectedValue")
	public String getSelectedValue() {
		return selectedValue;
	}

	@JsonProperty("selectedValue")
	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

}