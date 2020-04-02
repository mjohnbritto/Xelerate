
package com.suntecgroup.bp.designer.frontend.bean.filechannel;

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
	private String headerLines;
	@JsonProperty("fixedWidth")
	private List<FixedWidth> fixedWidth = null;
	@JsonProperty("delimited")
	private Delimited delimited = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Header() {
	}

	/**
	 * 
	 * @param delimited
	 * @param fixedWidth
	 * @param headerLines
	 * @param hasHeader
	 */
	public Header(boolean hasHeader, String headerLines, List<FixedWidth> fixedWidth, Delimited delimited) {
		super();
		this.hasHeader = hasHeader;
		this.headerLines = headerLines;
		this.fixedWidth = fixedWidth;
		this.delimited = delimited;
	}

	@JsonProperty("hasHeader")
	public boolean isHasHeader() {
		return hasHeader;
	}

	@JsonProperty("hasHeader")
	public void setHasHeader(boolean hasHeader) {
		this.hasHeader = hasHeader;
	}

	@JsonProperty("headerLines")
	public String getHeaderLines() {
		return headerLines;
	}

	@JsonProperty("headerLines")
	public void setHeaderLines(String headerLines) {
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
