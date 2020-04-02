
package com.suntecgroup.nifi.frontend.bean.filechannelintegration.output;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "fixedWidth", "delimited" })
public class Content {

	@JsonProperty("fixedWidth")
	private List<ContentFixedWidth> fixedWidth = null;
	@JsonProperty("delimited")
	private Delimited delimited;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Content() {
	}

	/**
	 * 
	 * @param delimited
	 * @param fixedWidth
	 */
	public Content(List<ContentFixedWidth> fixedWidth, Delimited delimited) {
		super();
		this.fixedWidth = fixedWidth;
		this.delimited = delimited;
	}

	public List<ContentFixedWidth> getFixedWidth() {
		return fixedWidth;
	}

	public void setFixedWidth(List<ContentFixedWidth> fixedWidth) {
		this.fixedWidth = fixedWidth;
	}

	public Delimited getDelimited() {
		return delimited;
	}

	public void setDelimited(Delimited delimited) {
		this.delimited = delimited;
	}

}
