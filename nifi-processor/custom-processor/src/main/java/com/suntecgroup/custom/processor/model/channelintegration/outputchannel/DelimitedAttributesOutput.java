package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "attributeName", "segmentPosition", "lineNumber", "dataType", "attributeType", "format",
		"currentNode", "parentNode", "type", "value" })
public class DelimitedAttributesOutput {

	@JsonProperty("attributeName")
	private String attributeName;

	@JsonProperty("segmentPosition")
	private int segmentPosition;

	@JsonProperty("lineNumber")
	private int lineNumber;

	@JsonProperty("dataType")
	private String dataType;

	@JsonProperty("attributeType")
	private AttributeTypeOutput attributeType;

	@JsonProperty("format")
	private FormatOutput format;

	@JsonProperty("currentNode")
	private String currentNode;

	@JsonProperty("parentNode")
	private String parentNode;

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

	@JsonProperty("dataType")
	public String getDataType() {
		return dataType;
	}

	@JsonProperty("dataType")
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@JsonProperty("attributeType")
	public AttributeTypeOutput getAttributeType() {
		return attributeType;
	}

	@JsonProperty("attributeType")
	public void setAttributeType(AttributeTypeOutput attributeType) {
		this.attributeType = attributeType;
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