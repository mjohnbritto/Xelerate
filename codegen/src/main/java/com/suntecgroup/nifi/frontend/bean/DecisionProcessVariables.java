package com.suntecgroup.nifi.frontend.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DecisionProcessVariables {

	@SerializedName("processVariable")
	@Expose
	private String processVariable;

	@SerializedName("TypeCategory")
	@Expose
	private String typeCategory;

	@SerializedName("type")
	@Expose
	private String type;

	@SerializedName("value")
	@Expose
	private PVValue value;

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

	public PVValue getValue() {
		return value;
	}

	public void setValue(PVValue value) {
		this.value = value;
	}

	public String getTypeCategory() {
		return typeCategory;
	}

	public void setTypeCategory(String typeCategory) {
		this.typeCategory = typeCategory;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("TypeCategory", typeCategory)
				.append("type", type)
				.append("processVariable", processVariable)
				.append("value", value).toString();
	}
}
