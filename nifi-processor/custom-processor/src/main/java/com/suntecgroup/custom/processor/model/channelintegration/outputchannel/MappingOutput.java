package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "fromValue", "toValue", "from", "to", "category", "fromParent", "toParent", "type" })
public class MappingOutput {

	@JsonProperty("fromValue")
	private String fromValue;

	@JsonProperty("toValue")
	private String toValue;

	@JsonProperty("from")
	private String from;

	@JsonProperty("to")
	private String to;

	@JsonProperty("category")
	private String category;

	@JsonProperty("fromParent")
	private String fromParent;

	@JsonProperty("toParent")
	private String toParent;

	@JsonProperty("type")
	private String type;

	@JsonProperty("fromValue")
	public String getFromValue() {
		return fromValue;
	}

	@JsonProperty("fromValue")
	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	@JsonProperty("toValue")
	public String getToValue() {
		return toValue;
	}

	@JsonProperty("toValue")
	public void setToValue(String toValue) {
		this.toValue = toValue;
	}

	@JsonProperty("from")
	public String getFrom() {
		return from;
	}

	@JsonProperty("from")
	public void setFrom(String from) {
		this.from = from;
	}

	@JsonProperty("to")
	public String getTo() {
		return to;
	}

	@JsonProperty("to")
	public void setTo(String to) {
		this.to = to;
	}

	@JsonProperty("category")
	public String getCategory() {
		return category;
	}

	@JsonProperty("category")
	public void setCategory(String category) {
		this.category = category;
	}

	@JsonProperty("fromParent")
	public String getFromParent() {
		return fromParent;
	}

	@JsonProperty("fromParent")
	public void setFromParent(String fromParent) {
		this.fromParent = fromParent;
	}

	@JsonProperty("toParent")
	public String getToParent() {
		return toParent;
	}

	@JsonProperty("toParent")
	public void setToParent(String toParent) {
		this.toParent = toParent;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

}
