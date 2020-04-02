
package com.suntecgroup.nifi.frontend.bean.filechannelintegration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "record", "attribute" })
public class Delimiter {

	@JsonProperty("record")
	private String record;
	@JsonProperty("attribute")
	private String attribute;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Delimiter() {
	}

	/**
	 * 
	 * @param record
	 * @param attribute
	 */
	public Delimiter(String record, String attribute) {
		super();
		this.record = record;
		this.attribute = attribute;
	}

	@JsonProperty("record")
	public String getRecord() {
		return record;
	}

	@JsonProperty("record")
	public void setRecord(String record) {
		this.record = record;
	}

	@JsonProperty("attribute")
	public String getAttribute() {
		return attribute;
	}

	@JsonProperty("attribute")
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

}
