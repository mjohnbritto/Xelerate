package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mapping {

	@SerializedName("fromValue")
	@Expose
	private String fromValue;

	@SerializedName("toValue")
	@Expose
	private String toValue;

	@SerializedName("from")
	@Expose
	private String from;

	@SerializedName("to")
	@Expose
	private String to;

	@SerializedName("category")
	@Expose
	private String category;

	@SerializedName("fromCurrentNode")
	@Expose
	private String fromCurrentNode;

	@SerializedName("toCurrentNode")
	@Expose
	private String toCurrentNode;

	@SerializedName("type")
	@Expose
	private String type;

	@SerializedName("fromParentNode")
	@Expose
	private String fromParentNode;

	@SerializedName("toParentNode")
	@Expose
	private String toParentNode;

	@SerializedName("processVariableAttribute")
	@Expose
	private String processVariableAttribute;

	public String getFromValue() {
		return fromValue;
	}

	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	public String getToValue() {
		return toValue;
	}

	public void setToValue(String toValue) {
		this.toValue = toValue;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFromCurrentNode() {
		return fromCurrentNode;
	}

	public void setFromCurrentNode(String fromCurrentNode) {
		this.fromCurrentNode = fromCurrentNode;
	}

	public String getToCurrentNode() {
		return toCurrentNode;
	}

	public void setToCurrentNode(String toCurrentNode) {
		this.toCurrentNode = toCurrentNode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFromParentNode() {
		return fromParentNode;
	}

	public void setFromParentNode(String fromParentNode) {
		this.fromParentNode = fromParentNode;
	}

	public String getToParentNode() {
		return toParentNode;
	}

	public void setToParentNode(String toParentNode) {
		this.toParentNode = toParentNode;
	}

	public String getProcessVariableAttribute() {
		return processVariableAttribute;
	}

	public void setProcessVariableAttribute(String processVariableAttribute) {
		this.processVariableAttribute = processVariableAttribute;
	}

}
