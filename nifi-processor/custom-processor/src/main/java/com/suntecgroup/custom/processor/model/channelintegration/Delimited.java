package com.suntecgroup.custom.processor.model.channelintegration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "recordDelimiter", "attributeDelimiter", "attributes" })

public class Delimited {

	@JsonProperty("recordDelimiter")
	private String record;

	@JsonProperty("attributeDelimiter")
	private String attribute;

	@JsonProperty("attributes")
	private List<DelimitedAttributes> attributes;

	@JsonProperty("recordDelimiter")
	public String getRecord() {
		return record;
	}

	@JsonProperty("recordDelimiter")
	public void setRecord(String record) {
		this.record = record;
	}

	@JsonProperty("attributeDelimiter")
	public String getAttribute() {
		return attribute;
	}

	@JsonProperty("attributeDelimiter")
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@JsonProperty("attributes")
	public List<DelimitedAttributes> getAttributes() {
		return attributes;
	}

	@JsonProperty("attributes")
	public void setAttributes(List<DelimitedAttributes> attributes) {
		this.attributes = attributes;

	}

}