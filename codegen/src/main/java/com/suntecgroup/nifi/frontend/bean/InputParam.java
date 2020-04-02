package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InputParam {

	@SerializedName("contextVariable")
	@Expose
	private String name;
	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("selectedKey")
	@Expose
	private String selectedKey;
	@SerializedName("processVariable")
	@Expose
	private String mappedProcessVariable;
	@SerializedName("isCollection")
	@Expose
	private boolean collection;
	@SerializedName("isMandatory")
	@Expose
	private boolean mandatory;
	@SerializedName("cvDateFormat")
	@Expose
	private String cvDateFormat;
	@SerializedName("value")
	@Expose
	private PVValue inputParamvalue;

	@SerializedName("typeParameter")
	@Expose
	private String typeParameter;

	public String getTypeParameter() {
		return typeParameter;
	}

	public void setTypeParameter(String typeParameter) {
		this.typeParameter = typeParameter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSelectedKey() {
		return selectedKey;
	}

	public void setSelectedKey(String selectedKey) {
		this.selectedKey = selectedKey;
	}

	public String getMappedProcessVariable() {
		return mappedProcessVariable;
	}

	public void setMappedProcessVariable(String mappedProcessVariable) {
		this.mappedProcessVariable = mappedProcessVariable;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isCollection() {
		return collection;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	public String getCvDateFormat() {
		return cvDateFormat;
	}

	public void setCvDateFormat(String cvDateFormat) {
		this.cvDateFormat = cvDateFormat;
	}

	public PVValue getInputParamvalue() {
		return inputParamvalue;
	}

	public void setInputParamvalue(PVValue inputParamvalue) {
		this.inputParamvalue = inputParamvalue;
	}

}