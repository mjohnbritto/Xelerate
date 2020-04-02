package com.suntecgroup.custom.processor.model.channelintegration;

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
	private int footerLines;

	@JsonProperty("fixedWidth")
	private List<FixedWidth> fixedWidth;

	@JsonProperty("delimited")
	private Delimited delimited;

	@JsonProperty("hasFooter")
	public boolean isHasFooter() {
		return hasFooter;
	}

	@JsonProperty("hasFooter")
	public void setHasFooter(boolean hasFooter) {
		this.hasFooter = hasFooter;
	}

	@JsonProperty("footerLines")
	public int getFooterLines() {
		return footerLines;
	}

	@JsonProperty("footerLines")
	public void setFooterLines(int footerLines) {
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
