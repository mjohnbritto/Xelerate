/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.startandend;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 * This is a model/pojo class to the request/response to data
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "precision", "scale", "intValue", "stringValue", "booleanValue", "dateValue", "beValue",
		"bukAttributes" })
public class Value {

	@JsonProperty("precision")
	private Integer precision;
	@JsonProperty("scale")
	private Integer scale;
	@JsonProperty("intValue")
	private BigDecimal intValue;
	@JsonProperty("stringValue")
	private String stringValue;
	@JsonProperty("booleanValue")
	private Boolean booleanValue;
	@JsonProperty("dateValue")
	private String dateValue;
	@JsonProperty("beValue")
	private String beValue;
	@JsonProperty("bukAttributes")
	private String[] bukAttributes;

	@JsonProperty("precision")
	public Integer getPrecision() {
		return precision;
	}

	@JsonProperty("precision")
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	@JsonProperty("scale")
	public Integer getScale() {
		return scale;
	}

	@JsonProperty("scale")
	public void setScale(Integer scale) {
		this.scale = scale;
	}

	@JsonProperty("intValue")
	public BigDecimal getIntValue() {
		return intValue;
	}

	@JsonProperty("intValue")
	public void setIntValue(BigDecimal intValue) {
		this.intValue = intValue;
	}

	@JsonProperty("stringValue")
	public String getStringValue() {
		return stringValue;
	}

	@JsonProperty("stringValue")
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@JsonProperty("booleanValue")
	public Boolean getBooleanValue() {
		return booleanValue;
	}

	@JsonProperty("booleanValue")
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	@JsonProperty("dateValue")
	public String getDateValue() {
		return dateValue;
	}

	@JsonProperty("dateValue")
	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}

	@JsonProperty("beValue")
	public String getBeValue() {
		return beValue;
	}

	@JsonProperty("beValue")
	public void setBeValue(String beValue) {
		this.beValue = beValue;
	}

	@JsonProperty("bukAttributes")
	public String[] getBukAttributes() {
		return bukAttributes;
	}

	@JsonProperty("bukAttributes")
	public void setBukAttributes(String[] bukAttributes) {
		this.bukAttributes = bukAttributes;
	}

	@Override
	public String toString() {
		return "Value [precision=" + precision + ", scale=" + scale + ", intValue=" + intValue + ", stringValue="
				+ stringValue + ", booleanValue=" + booleanValue + ", dateValue=" + dateValue + ", beValue=" + beValue
				+ ", bukAttributes=" + bukAttributes + "]";
	}

}
