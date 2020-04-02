package com.suntecgroup.bp.designer.frontend.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Property {

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("value")
	@Expose
	private String value;

	@SerializedName("isMandatory")
	@Expose
	private boolean isMandatory;
	
	@SerializedName("isProfileableAtSolutions")
	@Expose
	private boolean isProfileableAtSolutions;
	
	@SerializedName("isProfileableAtOperation")
	@Expose
	private boolean isProfileableAtOperation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public boolean isProfileableAtSolutions() {
		return isProfileableAtSolutions;
	}

	public void setProfileableAtSolutions(boolean isProfileableAtSolutions) {
		this.isProfileableAtSolutions = isProfileableAtSolutions;
	}

	public boolean isProfileableAtOperation() {
		return isProfileableAtOperation;
	}

	public void setProfileableAtOperation(boolean isProfileableAtOperation) {
		this.isProfileableAtOperation = isProfileableAtOperation;
	}
}
