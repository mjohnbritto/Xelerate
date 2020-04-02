package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ContextParameters {

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
	private String processVariable;

	@SerializedName("isMandatory")
	@Expose
	private boolean mandatory;

	@SerializedName("isCollection")
	@Expose
	private boolean collection;

	@SerializedName("value")
	@Expose
	private PVValue value;
	@SerializedName("processVariableArray")
	@Expose
	private List<ProcessVariableArray> processVariableArray;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSelectedKey() {
		return selectedKey;
	}

	public void setSelectedKey(String selectedKey) {
		this.selectedKey = selectedKey;
	}

	public String getProcessVariable() {
		return processVariable;
	}

	public void setProcessVariable(String processVariable) {
		this.processVariable = processVariable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("contextVariable", name).append("processVariable", processVariable)
				.append("type", type).append("value", value).append("selectedKey", selectedKey)
				.append("processVariableArray", processVariableArray).toString();
	}

	public PVValue getValue() {
		return value;
	}

	public void setValue(PVValue value) {
		this.value = value;
	}

	public List<ProcessVariableArray> getProcessVariableArray() {
		return processVariableArray;
	}

	public void setProcessVariableArray(List<ProcessVariableArray> processVariableArray) {
		this.processVariableArray = processVariableArray;
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
}
