package com.suntecgroup.nifi.template.beans;

public class Value {
	public Value() {
		precision = 0;
		scale = 0;
		intValue = 0;
		stringValue = "";
		booleanValue = false;
		dateValue = "";

	}

	private int precision;

	private int scale;

	private int intValue;

	private String stringValue;

	private boolean booleanValue;

	private String dateValue;

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		if (precision != 0)
			this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
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

}
