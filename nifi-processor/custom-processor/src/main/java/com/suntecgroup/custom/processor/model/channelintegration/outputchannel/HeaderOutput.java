package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "hasHeader", "headerLines", "fixedWidth", "delimited" })
public class HeaderOutput {

	@JsonProperty("hasHeader")
	private boolean hasHeader;

	@JsonProperty("headerLines")
	private int headerLines;

	@JsonProperty("fixedWidth")
	private List<FixedWidthOutput> fixedWidth;

	@JsonProperty("delimited")
	private DelimitedOutput delimited;

	@JsonProperty("hasHeader")
	public boolean isHasHeader() {
		return hasHeader;
	}

	@JsonProperty("hasHeader")
	public void setHasHeader(boolean hasHeader) {
		this.hasHeader = hasHeader;
	}

	@JsonProperty("headerLines")
	public int getHeaderLines() {
		return headerLines;
	}

	@JsonProperty("headerLines")
	public void setHeaderLines(int headerLines) {
		this.headerLines = headerLines;
	}

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
