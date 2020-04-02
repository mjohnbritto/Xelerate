
package com.suntecgroup.bp.designer.frontend.bean.filechannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mapping {

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

	public String getFromParent() {
		return fromParent;
	}

	public void setFromParent(String fromParent) {
		this.fromParent = fromParent;
	}

	public String getToParent() {
		return toParent;
	}

	public void setToParent(String toParent) {
		this.toParent = toParent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
