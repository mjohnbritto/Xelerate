/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.smartconnector;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "fromValue", "toValue", "from", "to", "category", "fromCurrentNode", "toCurrentNode", "type",
		"fromParentNode", "toParentNode", "processVariableAttribute", "datatype", "fromPath", "toPath" })

public class SmartMapping {

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

	
	public String getToDataType() {
		return toDataType;
	}

	public void setToDataType(String toDataType) {
		this.toDataType = toDataType;
	}

	@JsonProperty("typeConversionArray")
	private ArrayList<TypeConversion> typeConversionArray;
	
	public ArrayList<TypeConversion> getTypeConversionArray() {
		return typeConversionArray;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setTypeConversionArray(ArrayList<TypeConversion> typeConversionArray) {
		this.typeConversionArray = typeConversionArray;
	}

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

}
