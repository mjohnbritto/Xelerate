package com.suntecgroup.custom.processor.model.channelintegration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.custom.processor.model.smartconnector.TypeConversion;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "fromValue", "toValue", "from", "to", "category", "fromCurrentNode", "toCurrentNode", "type","attributeType",
	"fromParentNode", "toParentNode", "processVariableAttribute","fromDataType", "dataType","toDataType", "fromPath", "toPath","typeConversionArray" })

public class CIMapping {

	@JsonProperty("fromValue")
	private String fromValue;

	@JsonProperty("toValue")
	private String toValue;

	@JsonProperty("from")
	private String from;

	@JsonProperty("to")
	private String to;

	@JsonProperty("category")
	private String category;

	@JsonProperty("fromCurrentNode")
	private String fromCurrentNode;

	@JsonProperty("toCurrentNode")
	private String toCurrentNode;

	@JsonProperty("type")
	private String type;
	
	@JsonProperty("attributeType")
	private String attributeType;

	@JsonProperty("fromParentNode")
	private String fromParentNode;

	@JsonProperty("toParentNode")
	private String toParentNode;

	@JsonProperty("processVariableAttribute")
	private String processVariableAttribute;
	
	@JsonProperty("dataType")
	private String dataType;
	
	@JsonProperty("toDataType")
	private String toDataType;

	@JsonProperty("fromPath")
	private String fromPath;

	@JsonProperty("toPath")
	private String toPath;
	
	@JsonProperty("typeConversionArray")
	private List<ConversionArray> typeConversionArray;

	@JsonProperty("fromValue")
	public String getFromValue() {
		return fromValue;
	}

	@JsonProperty("fromValue")
	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	@JsonProperty("toValue")
	public String getToValue() {
		return toValue;
	}

	@JsonProperty("toValue")
	public void setToValue(String toValue) {
		this.toValue = toValue;
	}

	@JsonProperty("from")
	public String getFrom() {
		return from;
	}

	@JsonProperty("from")
	public void setFrom(String from) {
		this.from = from;
	}

	@JsonProperty("to")
	public String getTo() {
		return to;
	}

	@JsonProperty("to")
	public void setTo(String to) {
		this.to = to;
	}

	@JsonProperty("category")
	public String getCategory() {
		return category;
	}

	@JsonProperty("category")
	public void setCategory(String category) {
		this.category = category;
	}

	@JsonProperty("fromCurrentNode")
	public String getFromCurrentNode() {
		return fromCurrentNode;
	}

	@JsonProperty("fromCurrentNode")
	public void setFromCurrentNode(String fromCurrentNode) {
		this.fromCurrentNode = fromCurrentNode;
	}

	@JsonProperty("toCurrentNode")
	public String getToCurrentNode() {
		return toCurrentNode;
	}

	@JsonProperty("toCurrentNode")
	public void setToCurrentNode(String toCurrentNode) {
		this.toCurrentNode = toCurrentNode;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonProperty("attributeType")
	public String getAttributeType() {
		return attributeType;
	}

	@JsonProperty("attributeType")
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	@JsonProperty("fromParentNode")
	public String getFromParentNode() {
		return fromParentNode;
	}

	@JsonProperty("fromParentNode")
	public void setFromParentNode(String fromParentNode) {
		this.fromParentNode = fromParentNode;
	}

	@JsonProperty("toParentNode")
	public String getToParentNode() {
		return toParentNode;
	}

	@JsonProperty("toParentNode")
	public void setToParentNode(String toParentNode) {
		this.toParentNode = toParentNode;
	}

	@JsonProperty("processVariableAttribute")
	public String getProcessVariableAttribute() {
		return processVariableAttribute;
	}

	@JsonProperty("processVariableAttribute")
	public void setProcessVariableAttribute(String processVariableAttribute) {
		this.processVariableAttribute = processVariableAttribute;
	}

	@JsonProperty("dataType")
	public String getDataType() {
		return dataType;
	}

	@JsonProperty("dataType")
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@JsonProperty("toDataType")
	public String getToDataType() {
		return toDataType;
	}
	@JsonProperty("toDataType")
	public void setToDataType(String toDataType) {
		this.toDataType = toDataType;
	}

	@JsonProperty("fromPath")
	public String getFromPath() {
		return fromPath;
	}

	@JsonProperty("fromPath")
	public void setFromPath(String fromPath) {
		this.fromPath = fromPath;
	}

	@JsonProperty("toPath")
	public String getToPath() {
		return toPath;
	}

	@JsonProperty("toPath")
	public void setToPath(String toPath) {
		this.toPath = toPath;
	}
	
	@JsonProperty("typeConversionArray")
	public List<ConversionArray> getTypeConversionArray() {
		return typeConversionArray;
	}

	@JsonProperty("typeConversionArray")
	public void setTypeConversionArray(List<ConversionArray> typeConversionArray) {
		this.typeConversionArray = typeConversionArray;
	}

}

