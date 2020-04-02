
package com.suntecgroup.bp.designer.frontend.bean.filechannel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "fixedWidth", "autoGen", "delimited" })
public class Content {

	@JsonProperty("fixedWidth")
	private List<FixedWidth> fixedWidth = null;
	@JsonProperty("autoGen")
	private boolean autoGen;
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
	 * @param autoGen
	 * @param delimited
	 * @param fixedWidth
	 */
	public Content(List<FixedWidth> fixedWidth, boolean autoGen, Delimited delimited) {
		super();
		this.fixedWidth = fixedWidth;
		this.autoGen = autoGen;
		this.delimited = delimited;
	}

	@JsonProperty("fixedWidth")
	public List<FixedWidth> getFixedWidth() {
		return fixedWidth;
	}

	@JsonProperty("fixedWidth")
	public void setFixedWidth(List<FixedWidth> fixedWidth) {
		this.fixedWidth = fixedWidth;
	}

	@JsonProperty("autoGen")
	public boolean isAutoGen() {
		return autoGen;
	}

	@JsonProperty("autoGen")
	public void setAutoGen(boolean autoGen) {
		this.autoGen = autoGen;
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
