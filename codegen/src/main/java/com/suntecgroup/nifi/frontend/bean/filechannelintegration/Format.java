package com.suntecgroup.nifi.frontend.bean.filechannelintegration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Format {

	@JsonProperty("trueValue")
	private String trueValue;

	@JsonProperty("falseValue")
	private String falseValue;

	@JsonProperty("precision")
	private String precision;

	@JsonProperty("scale")
	private String scale;

	@JsonProperty("dateTime")
	private String dateTime;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Format() {
	}

	public String getTrueValue() {
		return trueValue;
	}

	public void setTrueValue(String trueValue) {
		this.trueValue = trueValue;
	}

	public String getFalseValue() {
		return falseValue;
	}

	public void setFalseValue(String falseValue) {
		this.falseValue = falseValue;
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

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

}