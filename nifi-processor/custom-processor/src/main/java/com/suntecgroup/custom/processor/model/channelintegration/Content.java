package com.suntecgroup.custom.processor.model.channelintegration;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "autoGen", "relationship", "fixedWidth", "delimited" })

public class Content {

	@JsonProperty("autoGen")
	private boolean autoGen;

	@JsonProperty("relationship")
	private List<Map<String, List<String>>> relationship;

	@JsonProperty("fixedWidth")
	private List<FixedWidth> fixedWidth;

	@JsonProperty("delimited")
	private Delimited delimited;

	@JsonProperty("autoGen")
	public boolean isAutoGen() {
		return autoGen;
	}

	@JsonProperty("autoGen")
	public void setAutoGen(boolean autoGen) {
		this.autoGen = autoGen;
	}

	@JsonProperty("relationship")
	public List<Map<String, List<String>>> isRelationship() {
		return relationship;
	}

	@JsonProperty("relationship")
	public void setRelationship(List<Map<String, List<String>>> relationship) {
		this.relationship = relationship;
	}

	@JsonProperty("fixedWidth")
	public List<FixedWidth> getFixedWidth() {
		return fixedWidth;
	}

	@JsonProperty("fixedWidth")
	public void setFixedWidth(List<FixedWidth> fixedWidth) {
		this.fixedWidth = fixedWidth;
	}

	@JsonProperty("delimited")
	public Delimited getDelimited() {
		return delimited;
	}

	@JsonProperty("delimited")
	public void setDelimited(Delimited delimited) {
		this.delimited = delimited;
	}

}