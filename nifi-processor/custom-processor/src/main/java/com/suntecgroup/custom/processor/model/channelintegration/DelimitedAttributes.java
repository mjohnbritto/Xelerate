package com.suntecgroup.custom.processor.model.channelintegration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "attributeName", "segmentPosition", "lineNumber", "buk", "dataType", "attributeType","format","currentNode","parentNode" })

public class DelimitedAttributes {

	@JsonProperty("attributeName")
	private String attributeName;

	@JsonProperty("segmentPosition")
	private int segmentPosition;

	@JsonProperty("lineNumber")
	private int lineNumber;

	@JsonProperty("buk")
	private boolean buk;

	@JsonProperty("dataType")
	private String dataType;

	@JsonProperty("attributeType")
	private AttributeType attributeType;
	
	@JsonProperty("currentNode")
	private String currentNode;

	@JsonProperty("parentNode")
	private String parentNode;
	
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

	@JsonProperty("segmentPosition")
	public int getSegmentPosition() {
		return segmentPosition;
	}

	@JsonProperty("segmentPosition")
	public void setSegmentPosition(int segmentPosition) {
		this.segmentPosition = segmentPosition;
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

	@JsonProperty("attributeType")
	public AttributeType getAttributeType() {
		return attributeType;
	}

	@JsonProperty("attributeType")
	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}
	
	@JsonProperty("format")
	public FormatAttributes getFormat() {
		return format;
	}

	@JsonProperty("format")
	public void setFormat(FormatAttributes format) {
		this.format = format;
	}
	
	@JsonProperty("currentNode")
	public String getCurrentNode() {
		return currentNode;
	}

	@JsonProperty("currentNode")
	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}

	@JsonProperty("parentNode")
	public String getParentNode() {
		return parentNode;
	}

	@JsonProperty("parentNode")
	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

}