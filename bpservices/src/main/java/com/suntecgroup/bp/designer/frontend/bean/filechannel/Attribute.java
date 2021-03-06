
package com.suntecgroup.bp.designer.frontend.bean.filechannel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attribute {

	@JsonProperty("key")
	private String key;
	@JsonProperty("attributeName")
	private String attributeName;
	@JsonProperty("segmentPosition")
	private String segmentPosition;
	@JsonProperty("buk")
	private String buk = "";
	@JsonProperty("lineNumber")
	private String lineNumber = null;
	@JsonProperty("dataType")
	private String dataType;
	@JsonProperty("attributeType")
	private AttributeType attributeType;
	@JsonProperty("format")
	private Format format = null;
	@JsonProperty("parent")
	private String parent;
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

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
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

	@JsonProperty("key")
	public String getKey() {
		return key;
	}

	@JsonProperty("key")
	public void setKey(String key) {
		this.key = key;
	}

	@JsonProperty("attributeName")
	public String getAttributeName() {
		return attributeName;
	}

	@JsonProperty("attributeName")
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@JsonProperty("segmentPosition")
	public String getSegmentPosition() {
		return segmentPosition;
	}

	@JsonProperty("segmentPosition")
	public void setSegmentPosition(String segmentPosition) {
		this.segmentPosition = segmentPosition;
	}

	@JsonProperty("lineNumber")
	public String getLineNumber() {
		return lineNumber;
	}

	@JsonProperty("lineNumber")
	public void setLineNumber(String lineNumber) {
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

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}

	@JsonProperty("parent")
	public String getParent() {
		return parent;
	}

	@JsonProperty("parent")
	public void setParent(String parent) {
		this.parent = parent;
	}

	@JsonProperty("buk")
	public String isBuk() {
		return buk;
	}

	@JsonProperty("buk")
	public void setBuk(String buk) {
		this.buk = buk;
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

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

}