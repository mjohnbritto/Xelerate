/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.startandend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 * This is a model/pojo class to the request/response to data
 * 
 * @version 1.0 - December 2018
 * @author rakesh.sin
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "entity", "field", "type","operator", "selectedValue", "processVariable","beAttribute",
	"customValue","fromValue","toValue", "lhsPVAttribute", "rhsPVAttribute"})
public class Rule {

	@JsonProperty("entity")
	private String entity;
	@JsonProperty("field")
	private String field;	
	@JsonProperty("type")
	private String type;
	@JsonProperty("operator")
	private String operator;
	@JsonProperty("selectedValue")
	private String selectedValue;
	@JsonProperty("processVariable")
	private String processVariable;
	@JsonProperty("beAttribute")
	private String beAttribute;
	@JsonProperty("customValue")
	private String customValue;
	@JsonProperty("fromValue")
	private String fromValue;
	@JsonProperty("toValue")
	private String toValue;
	@JsonProperty("lhsPVAttribute")
	private String lhsPVAttribute;
	@JsonProperty("rhsPVAttribute")
	private String rhsPVAttribute;
	
	public String getRhsPVAttribute() {
		return rhsPVAttribute;
	}
	public void setRhsPVAttribute(String rhsPVAttribute) {
		this.rhsPVAttribute = rhsPVAttribute;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getSelectedValue() {
		return selectedValue;
	}
	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}
	public String getProcessVariable() {
		return processVariable;
	}
	public void setProcessVariable(String processVariable) {
		this.processVariable = processVariable;
	}
	public String getBeAttribute() {
		return beAttribute;
	}
	public void setBeAttribute(String beAttribute) {
		this.beAttribute = beAttribute;
	}
	public String getCustomValue() {
		return customValue;
	}
	public void setCustomValue(String customValue) {
		this.customValue = customValue;
	}
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
	public String getLhsPVAttribute() {
		return lhsPVAttribute;
	}
	public void setLhsPVAttribute(String lhsPVAttribute) {
		this.lhsPVAttribute = lhsPVAttribute;
	}
}
