package com.suntecgroup.nifi.template.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TemplateContextParameter {

	private String name;

	private String description;

	private DataType dataType;

	private int contextParamId;

	private boolean collection;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public int getContextParamId() {
		return contextParamId;
	}

	public void setContextParamId(int contextParamId) {
		this.contextParamId = contextParamId;
	}

	public boolean isCollection() {
		return collection;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("description", description)
				.append("dataType", dataType).append("contextParamId", contextParamId).toString();
	}

}
