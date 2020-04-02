package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "attributeName", "startingPosition", "width", "lineNumber", "dataType", "format", "type",
		"value" })
public class FixedWidthOutput {

	@JsonProperty("attributeName")
	private String attributeName;

	@JsonProperty("startingPosition")
	private int startingPosition;

	@JsonProperty("width")
	private int width;

	@JsonProperty("lineNumber")
	private int lineNumber;

	@JsonProperty("dataType")
	private String dataType;

	@JsonProperty("format")
	private FormatOutput format;

	@JsonProperty("type")
	private String type;

	@JsonProperty("value")
	private String value;

	@JsonProperty("attributeName")
	public String getAttributeName() {
		return attributeName;
	}

	@JsonProperty("attributeName")
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@JsonProperty("startingPosition")
	public int getStartingPosition() {
		return startingPosition;
	}

	@JsonProperty("startingPosition")
	public void setStartingPosition(int startingPosition) {
		this.startingPosition = startingPosition;
	}

	@JsonProperty("width")
	public int getWidth() {
		return width;
	}

	@JsonProperty("width")
	public void setWidth(int width) {
		this.width = width;
	}

	@JsonProperty("lineNumber")
	public int getLineNumber() {
		return lineNumber;
	}

	@JsonProperty("lineNumber")
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@JsonProperty("dataType")
	public String getDataType() {
		return dataType;
	}

	@JsonProperty("dataType")
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@JsonProperty("format")
	public FormatOutput getFormat() {
		return format;
	}

	@JsonProperty("format")
	public void setFormat(FormatOutput format) {
		this.format = format;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}

}
