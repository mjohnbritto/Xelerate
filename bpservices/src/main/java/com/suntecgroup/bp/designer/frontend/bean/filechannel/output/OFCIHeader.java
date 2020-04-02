
package com.suntecgroup.bp.designer.frontend.bean.filechannel.output;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Delimited;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.FixedWidth;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "hasHeader", "headerLines", "fixedWidth", "delimited" })
public class OFCIHeader {

	@JsonProperty("hasHeader")
	private boolean hasHeader;
	@JsonProperty("headerLines")
	private int headerLines;
	@JsonProperty("fixedWidth")
	private List<FixedWidth> fixedWidth = null;
	@JsonProperty("delimited")
	private Delimited delimited = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public OFCIHeader() {
	}

	/**
	 * 
	 * @param delimited
	 * @param fixedWidth
	 * @param headerLines
	 * @param hasHeader
	 */
	public OFCIHeader(boolean hasHeader, int headerLines, List<FixedWidth> fixedWidth, Delimited delimited) {
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
