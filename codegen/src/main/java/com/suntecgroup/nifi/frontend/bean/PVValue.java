package com.suntecgroup.nifi.frontend.bean;

import java.util.List;

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
	private boolean booleanValue;
	@SerializedName("dateValue")
	@Expose
	private String dateValue;

	@SerializedName("beValue")
	@Expose
	private String beValue;
	
	@SerializedName("bukAttributes")
	@Expose
	private List<String> bukAttributes;

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

	public String getBeValue() {
		return beValue;
	}

	public void setBeValue(String beValue) {
		this.beValue = beValue;
	}

	public List<String> getBukAttributes() {
		return bukAttributes;
	}

	public void setBukAttributes(List<String> bukAttributes) {
		this.bukAttributes = bukAttributes;
	}

	@Override
	public String toString() {
		return "PVValue [intValue=" + intValue + ", stringValue=" + stringValue + ", booleanValue=" + booleanValue
				+ ", dateValue=" + dateValue + ", beValue=" + beValue + "]";
	}

}
