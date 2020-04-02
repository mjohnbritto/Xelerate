package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConversionArray {

	@SerializedName("inputValue")
	@Expose
	private InputValue[] inputValue;

	@SerializedName("dataType")
	@Expose
	private String dataType;

	@SerializedName("inputType")
	@Expose
	private String inputType;

	public InputValue[] getInputValue() {
		return inputValue;
	}

	public void setInputValue(InputValue[] inputValue) {
		this.inputValue = inputValue;
	}

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

}
