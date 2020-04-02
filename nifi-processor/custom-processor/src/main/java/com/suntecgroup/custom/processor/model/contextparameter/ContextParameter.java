/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.contextparameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "dataType", "contextParamId", "description" })
public class ContextParameter {

	@JsonProperty("name")
	private String name;
	@JsonProperty("dataType")
	private DataType dataType;
	@JsonProperty("contextParamId")
	private Integer contextParamId;
	@JsonProperty("description")
	private String description;
	@JsonProperty("collection")
	private boolean collection;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("dataType")
	public DataType getDataType() {
		return dataType;
	}

	@JsonProperty("dataType")
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	@JsonProperty("contextParamId")
	public Integer getContextParamId() {
		return contextParamId;
	}

	@JsonProperty("contextParamId")
	public void setContextParamId(Integer contextParamId) {
		this.contextParamId = contextParamId;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("collection")
	public boolean isCollection() {
		return collection;
	}

	@JsonProperty("collection")
	public void setCollection(boolean collection) {
		this.collection = collection;
	}
}
