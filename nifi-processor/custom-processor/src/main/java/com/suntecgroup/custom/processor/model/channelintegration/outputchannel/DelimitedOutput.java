
package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "recordDelimiter", "attributeDelimiter", "attributes" })
public class DelimitedOutput {

	@JsonProperty("recordDelimiter")
	private String record;

	@JsonProperty("attributeDelimiter")
	private String attribute;

	@JsonProperty("attributes")
	private List<DelimitedAttributesOutput> attributes;

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
	public List<DelimitedAttributesOutput> getAttributes() {
		return attributes;
	}

	@JsonProperty("attributes")
	public void setAttributes(List<DelimitedAttributesOutput> attributes) {
		this.attributes = attributes;
	}

}