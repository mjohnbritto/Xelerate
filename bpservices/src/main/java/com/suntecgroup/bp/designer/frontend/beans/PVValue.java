package com.suntecgroup.bp.designer.frontend.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PVValue {

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
	private String stringValue;
	@SerializedName("booleanValue")
	@Expose
	private String booleanValue;
	@SerializedName("dateValue")
	@Expose
	private String dateValue;

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

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	
	public String getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(String booleanValue) {
		this.booleanValue = booleanValue;
	}

	public String getDateValue() {
		return dateValue;
	}

	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}

}
