package com.suntecgroup.custom.processor.model.smartconnector;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "dataType", "inputType", "inputValue" })
public class TypeConversion {
	@JsonProperty("dataType")
	private String dataType;

	@JsonProperty("inputType")
	private String inputType;

	@JsonProperty("inputValue")
	private List<InputValue> inputValue;

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public List<InputValue> getInputValue() {
		return inputValue;
	}

	public void setInputValue(List<InputValue> inputValue) {
		this.inputValue = inputValue;
	}

}
