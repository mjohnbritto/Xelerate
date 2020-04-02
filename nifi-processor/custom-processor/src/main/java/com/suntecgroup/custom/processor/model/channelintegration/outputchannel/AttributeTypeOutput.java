package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "type", "delimiter" })
public class AttributeTypeOutput {

	@JsonProperty("type")
	private String type;

	@JsonProperty("delimiter")
	private Map<String, String> delimiter;

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("delimiter")
	public Map<String, String> getDelimiter() {
		return delimiter;
	}

	@JsonProperty("delimiter")
	public void setDelimiter(Map<String, String> delimiter) {
		this.delimiter = delimiter;
	}

}
