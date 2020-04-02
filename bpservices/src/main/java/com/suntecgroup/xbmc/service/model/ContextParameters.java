package com.suntecgroup.xbmc.service.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model class to store Context Parameters information. This model is used for
 * JSON data for Business Service details.
 *
 */
@JsonPropertyOrder({ "name", "dataType" })
public class ContextParameters {

	private String name;
	private DataType dataType;
	private int contextParamId;

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getContextParamId() {
		return contextParamId;
	}

	public void setContextParamId(int contextParamId) {
		this.contextParamId = contextParamId;
	}

	@Override
	public String toString() {
		return "ClassPojo [dataType = " + dataType + ", name = " + name + ", contextParamId = " + contextParamId + "]";
	}
}
