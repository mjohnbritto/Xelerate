package com.suntecgroup.custom.processor.model.channelintegration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "attributeName", "startingPosition", "width", "lineNumber", "buk", "dataType", "format" })

public class FixedWidth {

	@JsonProperty("attributeName")
	private String attributeName;

	@JsonProperty("startingPosition")
	private int startingPosition;

	@JsonProperty("width")
	private int width;

	@JsonProperty("lineNumber")
	private int lineNumber;

	@JsonProperty("buk")
	private boolean buk;

	@JsonProperty("dataType")
	private String dataType;

	@JsonProperty("format")
	private FormatAttributes format;

	@JsonProperty("attributeName")
	public String getAttributeName() {
		return attributeName;
	}

	@JsonProperty("attributeName")
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@JsonProperty("startingPosition")
	public int getStartingPoint() {
		return startingPosition;
	}

	@JsonProperty("startingPosition")
	public void setStartingPoint(int startingPosition) {
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

	@JsonProperty("buk")
	public boolean isBuk() {
		return buk;
	}

	@JsonProperty("buk")
	public void setBuk(boolean buk) {
		this.buk = buk;
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
	public FormatAttributes getFormat() {
		return format;
	}

	@JsonProperty("format")
	public void setFormat(FormatAttributes format) {
		this.format = format;
	}

}
