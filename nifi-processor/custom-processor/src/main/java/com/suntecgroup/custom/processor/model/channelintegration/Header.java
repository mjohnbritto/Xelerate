package com.suntecgroup.custom.processor.model.channelintegration;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "hasHeader", "headerLines", "fixedWidth", "delimited" })
public class Header {

	@JsonProperty("hasHeader")
	private boolean hasHeader;

	@JsonProperty("headerLines")
	private int headerLines;

	@JsonProperty("fixedWidth")
	private List<FixedWidth> fixedWidth;

	@JsonProperty("delimited")
	private Delimited delimited;

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