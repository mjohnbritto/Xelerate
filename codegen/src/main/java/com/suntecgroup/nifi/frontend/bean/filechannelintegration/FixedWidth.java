
package com.suntecgroup.nifi.frontend.bean.filechannelintegration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FixedWidth {

	@JsonProperty("attributeName")
	private String attributeName;
	@JsonProperty("startingPosition")
	private String startingPosition;
	@JsonProperty("width")
	private String width;
	@JsonProperty("dataType")
	private String dataType;
	@JsonProperty("format")
	private Format format=null;
	@JsonProperty("lineNumber")
	private String lineNumber = null;
	@JsonProperty("buk")
	private boolean buk = false;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public FixedWidth() {
	}

	@JsonProperty("attributeName")
	public String getAttributeName() {
		return attributeName;
	}

	@JsonProperty("attributeName")
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@JsonProperty("startingPoint")
	public String getStartingPoint() {
		return startingPosition;
	}

	@JsonProperty("startingPoint")
	public void setStartingPoint(String startingPoint) {
		this.startingPosition = startingPoint;
	}

	@JsonProperty("width")
	public String getWidth() {
		return width;
	}

	@JsonProperty("width")
	public void setWidth(String width) {
		this.width = width;
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
	public Format getFormat() {
		return format;
	}

	@JsonProperty("format")
	public void setFormat(Format format) {
		this.format = format;
	}

	@JsonProperty("lineNumber")
	public String getLineNumber() {
		return lineNumber;
	}

	@JsonProperty("lineNumber")
	public void setLineNumber(String lineNumber) {
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

}