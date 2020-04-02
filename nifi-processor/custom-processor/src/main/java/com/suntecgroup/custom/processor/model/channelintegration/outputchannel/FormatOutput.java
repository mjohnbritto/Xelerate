package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "trueValue", "falseValue", "precision", "scale", "dateTime" })
public class FormatOutput {

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

	@JsonProperty("trueValue")
	public String getTrueValue() {
		return trueValue;
	}

	@JsonProperty("trueValue")
	public void setTrueValue(String trueValue) {
		this.trueValue = trueValue;
	}

	@JsonProperty("falseValue")
	public String getFalseValue() {
		return falseValue;
	}

	@JsonProperty("falseValue")
	public void setFalseValue(String falseValue) {
		this.falseValue = falseValue;
	}

	@JsonProperty("precision")
	public String getPrecision() {
		return precision;
	}

	@JsonProperty("precision")
	public void setPrecision(String precision) {
		this.precision = precision;
	}

	@JsonProperty("scale")
	public String getScale() {
		return scale;
	}

	@JsonProperty("scale")
	public void setScale(String scale) {
		this.scale = scale;
	}

	@JsonProperty("dateTime")
	public String getDateTime() {
		return dateTime;
	}

	@JsonProperty("dateTime")
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
}
