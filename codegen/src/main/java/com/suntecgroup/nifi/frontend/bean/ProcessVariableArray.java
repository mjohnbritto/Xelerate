package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProcessVariableArray {

	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("description")
	@Expose
	private String description;

	@SerializedName("typeCategory")
	@Expose
	private String typeCategory;
	@SerializedName("typeName")
	@Expose
	private String typeName;
	@SerializedName("bEobjects")
	@Expose
	private String bEobjects;

	@SerializedName("precision")
	@Expose
	private String precision;
	@SerializedName("scale")
	@Expose
	private String scale;

	@SerializedName("intValue")
	@Expose
	private String intValue;
	@SerializedName("stringValue")
	@Expose
	private boolean stringValue;
	@SerializedName("booleanValue")
	@Expose
	private boolean booleanValue;

	@SerializedName("dateValue")
	@Expose
	private String dateValue;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTypeCategory() {
		return typeCategory;
	}

	public void setTypeCategory(String typeCategory) {
		this.typeCategory = typeCategory;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getbEobjects() {
		return bEobjects;
	}

	public void setbEobjects(String bEobjects) {
		this.bEobjects = bEobjects;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getIntValue() {
		return intValue;
	}

	public void setIntValue(String intValue) {
		this.intValue = intValue;
	}

	public boolean isBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public String getDateValue() {
		return dateValue;
	}

	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
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

	public boolean isStringValue() {
		return stringValue;
	}

	public void setStringValue(boolean stringValue) {
		this.stringValue = stringValue;
	}
}
