/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.contextparameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "type", "precision", "scale", "dateFormat", "roundingMode" })
public class DataType {

	@JsonProperty("type")
	private String type;
	@JsonProperty("precision")
	private Integer precision;
	@JsonProperty("scale")
	private Integer scale;
	@JsonProperty("dateFormat")
	private Object dateFormat;
	@JsonProperty("roundingMode")
	private String roundingMode;

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

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

	@JsonProperty("dateFormat")
	public Object getDateFormat() {
		return dateFormat;
	}

	@JsonProperty("dateFormat")
	public void setDateFormat(Object dateFormat) {
		this.dateFormat = dateFormat;
	}

	@JsonProperty("roundingMode")
	public String getRoundingMode() {
		return roundingMode;
	}

	@JsonProperty("roundingMode")
	public void setRoundingMode(String roundingMode) {
		this.roundingMode = roundingMode;
	}

}
