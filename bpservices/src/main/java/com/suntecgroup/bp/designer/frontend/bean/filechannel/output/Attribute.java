
package com.suntecgroup.bp.designer.frontend.bean.filechannel.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attribute {

	@JsonProperty("attributeName")
	private String attributeName;
	@JsonProperty("segmentPosition")
	private String segmentPosition;
	@JsonProperty("lineNumber")
	private String lineNumber = null;
	@JsonProperty("dataType")
	private String dataType;
	@JsonProperty("attributeType")
	private AttributeType attributeType;
	@JsonProperty("format")
	private Format format=null;
	@JsonProperty("disabled")
	private boolean disabled;
	@JsonProperty("currentNode")
	private String currentNode;
	@JsonProperty("parentNode")
	private String parentNode;
	@JsonProperty("type")
	private String type;
	@JsonProperty("value")
	private String value;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Attribute() {
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getSegmentPosition() {
		return segmentPosition;
	}

	public void setSegmentPosition(String segmentPosition) {
		this.segmentPosition = segmentPosition;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}

	public String getParentNode() {
		return parentNode;
	}

	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


}