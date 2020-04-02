
package com.suntecgroup.nifi.frontend.bean.filechannelintegration.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "contentField", "headerField", "calculation", "evaluate" })
public class FileHeader {

	@JsonProperty("contentField")
	private String contentField;
	@JsonProperty("headerField")
	private String headerField;
	@JsonProperty("calculation")
	private String calculation;
	@JsonProperty("evaluate")
	private String evaluate;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public FileHeader() {
	}

	/**
	 * 
	 * @param calculation
	 * @param evaluate
	 * @param headerField
	 * @param contentField
	 */
	public FileHeader(String contentField, String headerField, String calculation, String evaluate) {
		super();
		this.contentField = contentField;
		this.headerField = headerField;
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

	@JsonProperty("headerField")
	public String getHeaderField() {
		return headerField;
	}

	@JsonProperty("headerField")
	public void setHeaderField(String headerField) {
		this.headerField = headerField;
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
