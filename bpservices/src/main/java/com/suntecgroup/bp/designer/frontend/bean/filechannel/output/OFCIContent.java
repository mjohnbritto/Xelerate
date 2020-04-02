
package com.suntecgroup.bp.designer.frontend.bean.filechannel.output;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Delimited;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.FixedWidth;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "fixedWidth", "delimited" })
public class OFCIContent {

	@JsonProperty("fixedWidth")
	private List<FixedWidth> fixedWidth = null;
	@JsonProperty("delimited")
	private Delimited delimited;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public OFCIContent() {
	}

	/**
	 * 
	 * @param delimited
	 * @param fixedWidth
	 */
	public OFCIContent(List<FixedWidth> fixedWidth, Delimited delimited) {
		super();
		this.fixedWidth = fixedWidth;
		this.delimited = delimited;
	}

	public List<FixedWidth> getFixedWidth() {
		return fixedWidth;
	}

	public void setFixedWidth(List<FixedWidth> fixedWidth) {
		this.fixedWidth = fixedWidth;
	}

	public Delimited getDelimited() {
		return delimited;
	}

	public void setDelimited(Delimited delimited) {
		this.delimited = delimited;
	}

}
