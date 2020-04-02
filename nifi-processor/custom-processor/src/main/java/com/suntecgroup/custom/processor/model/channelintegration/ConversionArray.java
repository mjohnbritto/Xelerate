package com.suntecgroup.custom.processor.model.channelintegration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "inputValue", "dataType", "inputType"})
public class ConversionArray {
	
	@JsonProperty("inputValue")
	private InputValue[] inputValue;

	@JsonProperty("dataType")
	private String dataType;

	@JsonProperty("inputType")
	private String inputType;

	@JsonProperty("inputValue")
	public InputValue[] getInputValue() {
		return inputValue;
	}

	@JsonProperty("inputValue")
	public void setInputValue(InputValue[] inputValue) {
		this.inputValue = inputValue;
	}

	@JsonProperty("dataType")
	public String getDataType() {
		return dataType;
	}

	@JsonProperty("dataType")
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@JsonProperty("inputType")
	public String getInputType() {
		return inputType;
	}

	@JsonProperty("inputType")
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	
	


}
