package com.suntecgroup.nifi.frontend.bean.filechannelintegration;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.suntecgroup.nifi.frontend.bean.ConversionArray;

public class Mapping {

	@SerializedName("attributeType")
	@Expose
	private String attributeType;

	@SerializedName("category")
	@Expose
	private String category;

	@SerializedName("dataType")
	@Expose
	private String dataType;

	@SerializedName("from")
	@Expose
	private String from;

	@SerializedName("fromCurrentNode")
	@Expose
	private String fromCurrentNode;

	@SerializedName("fromParentDataType")
	@Expose
	private String fromParentDataType;

	@SerializedName("fromParentNode")
	@Expose
	private String fromParentNode;

	@SerializedName("fromPath")
	@Expose
	private String fromPath;

	@SerializedName("fromValue")
	@Expose
	private String fromValue;

	@SerializedName("processVariableAttribute")
	@Expose
	private String processVariableAttribute;

	@SerializedName("to")
	@Expose
	private String to;

	@SerializedName("toCurrentNode")
	@Expose
	private String toCurrentNode;

	@SerializedName("toDataType")
	@Expose
	private String toDataType;

	@SerializedName("toParentNode")
	@Expose
	private String toParentNode;

	@SerializedName("toPath")
	@Expose
	private String toPath;

	@SerializedName("toValue")
	@Expose
	private String toValue;

	@SerializedName("type")
	@Expose
	private String type;

	@SerializedName("typeConversionArray")
	@Expose
	private List<ConversionArray> typeConversionArray;

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFromCurrentNode() {
		return fromCurrentNode;
	}

	public void setFromCurrentNode(String fromCurrentNode) {
		this.fromCurrentNode = fromCurrentNode;
	}

	public String getFromParentNode() {
		return fromParentNode;
	}

	public void setFromParentNode(String fromParentNode) {
		this.fromParentNode = fromParentNode;
	}

	public String getFromPath() {
		return fromPath;
	}

	public void setFromPath(String fromPath) {
		this.fromPath = fromPath;
	}

	public String getFromValue() {
		return fromValue;
	}

	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	public String getProcessVariableAttribute() {
		return processVariableAttribute;
	}

	public void setProcessVariableAttribute(String processVariableAttribute) {
		this.processVariableAttribute = processVariableAttribute;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getToCurrentNode() {
		return toCurrentNode;
	}

	public void setToCurrentNode(String toCurrentNode) {
		this.toCurrentNode = toCurrentNode;
	}

	public String getToParentNode() {
		return toParentNode;
	}

	public void setToParentNode(String toParentNode) {
		this.toParentNode = toParentNode;
	}

	public String getToPath() {
		return toPath;
	}

	public void setToPath(String toPath) {
		this.toPath = toPath;
	}

	public String getToValue() {
		return toValue;
	}

	public void setToValue(String toValue) {
		this.toValue = toValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFromParentDataType() {
		return fromParentDataType;
	}

	public void setFromParentDataType(String fromParentDataType) {
		this.fromParentDataType = fromParentDataType;
	}

	public String getToDataType() {
		return toDataType;
	}

	public void setToDataType(String toDataType) {
		this.toDataType = toDataType;
	}

	public List<ConversionArray> getTypeConversionArray() {
		return typeConversionArray;
	}

	public void setTypeConversionArray(List<ConversionArray> typeConversionArray) {
		this.typeConversionArray = typeConversionArray;
	}

}