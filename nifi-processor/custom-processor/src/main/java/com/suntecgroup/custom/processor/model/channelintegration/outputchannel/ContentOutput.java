package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "fixedWidth", "delimited" })
public class ContentOutput {

	@JsonProperty("fixedWidth")
	private List<FixedWidthOutput> fixedWidth;

	@JsonProperty("delimited")
	private DelimitedOutput delimited;
	
	@JsonProperty("fixedWidth")
	public List<FixedWidthOutput> getFixedWidth() {
		return fixedWidth;
	}

	@JsonProperty("fixedWidth")
	public void setFixedWidth(List<FixedWidthOutput> fixedWidth) {
		this.fixedWidth = fixedWidth;
	}

	@JsonProperty("delimited")
	public DelimitedOutput getDelimited() {
		return delimited;
	}

	@JsonProperty("delimited")
	public void setDelimited(DelimitedOutput delimited) {
		this.delimited = delimited;
	}
}
