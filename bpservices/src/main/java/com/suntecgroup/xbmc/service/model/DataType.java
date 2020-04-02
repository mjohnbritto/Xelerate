package com.suntecgroup.xbmc.service.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DataType model holds data and its information. This model is used as part of
 * JSON data for ContextParameters.
 *
 */
@JsonPropertyOrder({ "type", "precision", "scale", "dateFormat" })
public class DataType {

	private String type;
	private int precision;
	private int scale;
	private String dateFormat;

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String string) {
		this.dateFormat = string;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ClassPojo [scale = " + scale + ", precision = " + precision + ", dateFormat = " + dateFormat
				+ ", type = " + type + "]";
	}
}