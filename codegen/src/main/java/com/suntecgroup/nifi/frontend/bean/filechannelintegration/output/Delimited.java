
package com.suntecgroup.nifi.frontend.bean.filechannelintegration.output;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Delimited {

	@JsonProperty("recordDelimiter")
	private String recordDelimiter;
	@JsonProperty("attributeDelimiter")
	private String attributeDelimiter;
	@JsonProperty("attributes")
	private List<ContentAttribute> attributes = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Delimited() {
	}

	/**
	 * 
	 * @param recordDelimiter
	 * @param attributeDelimiter
	 * @param attributes
	 */
	public Delimited(String record, String attribute, List<ContentAttribute> attributes) {
		super();
		this.recordDelimiter = record;
		this.attributeDelimiter = attribute;
		this.attributes = attributes;
	}

	@JsonProperty("recordDelimiter")
	public String getRecord() {
		return recordDelimiter;
	}

	@JsonProperty("recordDelimiter")
	public void setRecord(String record) {
		this.recordDelimiter = record;
	}

	@JsonProperty("attributeDelimiter")
	public String getAttribute() {
		return attributeDelimiter;
	}

	@JsonProperty("attributeDelimiter")
	public void setAttribute(String attribute) {
		this.attributeDelimiter = attribute;
	}

	@JsonProperty("attributes")
	public List<ContentAttribute> getAttributes() {
		return attributes;
	}

	@JsonProperty("attributes")
	public void setAttributes(List<ContentAttribute> attributes) {
		this.attributes = attributes;
	}

}
