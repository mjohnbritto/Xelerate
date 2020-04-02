
package com.suntecgroup.bp.designer.frontend.bean.filechannel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "hasFooter", "footerLines", "fixedWidth", "delimited" })
public class Footer {

	@JsonProperty("hasFooter")
	private boolean hasFooter;
	@JsonProperty("footerLines")
	private String footerLines;
	@JsonProperty("fixedWidth")
	private List<FixedWidth> fixedWidth = null;
	@JsonProperty("delimited")
	private Delimited delimited;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Footer() {
	}

	/**
	 * 
	 * @param delimited
	 * @param footerLines
	 * @param fixedWidth
	 * @param hasFooter
	 */
	public Footer(boolean hasFooter, String footerLines, List<FixedWidth> fixedWidth, Delimited delimited) {
		super();
		this.hasFooter = hasFooter;
		this.footerLines = footerLines;
		this.fixedWidth = fixedWidth;
		this.delimited = delimited;
	}

	@JsonProperty("hasFooter")
	public boolean isHasFooter() {
		return hasFooter;
	}

	@JsonProperty("hasFooter")
	public void setHasFooter(boolean hasFooter) {
		this.hasFooter = hasFooter;
	}

	@JsonProperty("footerLines")
	public String getFooterLines() {
		return footerLines;
	}

	@JsonProperty("footerLines")
	public void setFooterLines(String footerLines) {
		this.footerLines = footerLines;
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
