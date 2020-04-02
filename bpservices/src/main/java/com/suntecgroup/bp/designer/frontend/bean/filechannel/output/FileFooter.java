
package com.suntecgroup.bp.designer.frontend.bean.filechannel.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "contentField", "footerField", "calculation", "evaluate" })
public class FileFooter {

	@JsonProperty("contentField")
	private String contentField;
	@JsonProperty("footerField")
	private String footerField;
	@JsonProperty("calculation")
	private String calculation;
	@JsonProperty("evaluate")
	private String evaluate;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public FileFooter() {
	}

	/**
	 * 
	 * @param calculation
	 * @param evaluate
	 * @param footerField
	 * @param contentField
	 */
	public FileFooter(String contentField, String footerField, String calculation, String evaluate) {
		super();
		this.contentField = contentField;
		this.footerField = footerField;
		this.calculation = calculation;
		this.evaluate = evaluate;
	}

	@JsonProperty("contentField")
	public String getContentField() {
		return contentField;
	}

	@JsonProperty("contentField")
	public void setContentField(String contentField) {
		this.contentField = contentField;
	}

	@JsonProperty("footerField")
	public String getFooterField() {
		return footerField;
	}

	@JsonProperty("footerField")
	public void setFooterField(String footerField) {
		this.footerField = footerField;
	}

	@JsonProperty("calculation")
	public String getCalculation() {
		return calculation;
	}

	@JsonProperty("calculation")
	public void setCalculation(String calculation) {
		this.calculation = calculation;
	}

	@JsonProperty("evaluate")
	public String getEvaluate() {
		return evaluate;
	}

	@JsonProperty("evaluate")
	public void setEvaluate(String evaluate) {
		this.evaluate = evaluate;
	}

}
