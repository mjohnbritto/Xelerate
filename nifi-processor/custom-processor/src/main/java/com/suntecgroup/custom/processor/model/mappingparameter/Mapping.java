/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.mappingparameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "contextVariable", "type", "selectedKey", "processVariable", "value" })
public class Mapping {

	@JsonProperty("contextVariable")
	private String contextVariable;
	@JsonProperty("type")
	private String type;
	@JsonProperty("selectedKey")
	private String selectedKey;
	@JsonProperty("processVariable")
	private String processVariable;
	@JsonProperty("isMandatory")
	private boolean mandatory;
	@JsonProperty("value")
	private Value value;
	@JsonProperty("isCollection")
	private boolean isCollection;
	@JsonProperty("cvDateFormat")
	private String cvDateFormat;

	@JsonProperty("response")
	private String response;

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@JsonProperty("typeParameter")
	private String typeParameter;

	@JsonProperty("typeParameter")
	public String getTypeParameter() {
		return typeParameter;
	}

	@JsonProperty("typeParameter")
	public void setTypeParameter(String typeParameter) {
		this.typeParameter = typeParameter;
	}

	@JsonProperty("contextVariable")
	public String getContextVariable() {
		return contextVariable;
	}

	@JsonProperty("contextVariable")
	public void setContextVariable(String contextVariable) {
		this.contextVariable = contextVariable;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("selectedKey")
	public String getSelectedKey() {
		return selectedKey;
	}

	@JsonProperty("selectedKey")
	public void setSelectedKey(String selectedKey) {
		this.selectedKey = selectedKey;
	}

	@JsonProperty("processVariable")
	public String getProcessVariable() {
		return processVariable;
	}

	@JsonProperty("processVariable")
	public void setProcessVariable(String processVariable) {
		this.processVariable = processVariable;
	}

	@JsonProperty("isMandatory")
	public boolean isMandatory() {
		return mandatory;
	}

	@JsonProperty("isMandatory")
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@JsonProperty("value")
	public Value getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(Value value) {
		this.value = value;
	}

	@JsonProperty("isCollection")
	public boolean isCollection() {
		return isCollection;
	}

	@JsonProperty("isCollection")
	public void setCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}

	public String getCvDateFormat() {
		return cvDateFormat;
	}

	public void setCvDateFormat(String cvDateFormat) {
		this.cvDateFormat = cvDateFormat;
	}

}