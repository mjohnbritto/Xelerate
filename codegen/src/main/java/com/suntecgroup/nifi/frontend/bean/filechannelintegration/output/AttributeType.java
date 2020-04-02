
package com.suntecgroup.nifi.frontend.bean.filechannelintegration.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "type", "methods" })
public class AttributeType {

	@JsonProperty("type")
	private String type;
	@JsonProperty("delimiter")
	private Delimiter delimiter = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public AttributeType() {
	}

	/**
	 * 
	 * @param methods
	 * @param type
	 */
	public AttributeType(String type, Delimiter delimiter) {
		super();
		this.type = type;
		this.delimiter = delimiter;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	public Delimiter getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(Delimiter delimiter) {
		this.delimiter = delimiter;
	}

}
