package com.suntecgroup.bp.designer.frontend.bean.filechannel.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "staticName", "dynamicName" })
public class OutputFileName {

	@JsonProperty("staticName")
	private String staticName;
	@JsonProperty("dynamicName")
	private String dynamicName;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public OutputFileName() {
	}

	/**
	 * 
	 * @param dynamicName
	 * @param staticName
	 */
	public OutputFileName(String staticName, String dynamicName) {
		super();
		this.staticName = staticName;
		this.dynamicName = dynamicName;
	}

	@JsonProperty("staticName")
	public String getStaticName() {
		return staticName;
	}

	@JsonProperty("staticName")
	public void setStaticName(String staticName) {
		this.staticName = staticName;
	}

	@JsonProperty("dynamicName")
	public String getDynamicName() {
		return dynamicName;
	}

	@JsonProperty("dynamicName")
	public void setDynamicName(String dynamicName) {
		this.dynamicName = dynamicName;
	}

}