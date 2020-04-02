
package com.suntecgroup.nifi.frontend.bean.filechannelintegration.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FixedWidth {

	@JsonProperty("attributeName")
	private String attributeName;
	@JsonProperty("startingPosition")
	private int startingPosition;
	@JsonProperty("width")
	private String width;
	@JsonProperty("lineNumber")
	private String lineNumber = null;
	@JsonProperty("type")
	private String type;
	@JsonProperty("value")
	private String value;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public FixedWidth() {
	}

	@JsonProperty("attributeName")
	public String getAttributeName() {
		return attributeName;
	}

	@JsonProperty("attributeName")
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@JsonProperty("width")
	public String getWidth() {
		return width;
	}

	@JsonProperty("width")
	public void setWidth(String width) {
		this.width = width;
	}

	@JsonProperty("lineNumber")
	public String getLineNumber() {
		return lineNumber;
	}

	@JsonProperty("lineNumber")
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	@JsonProperty("startingPosition")
	public int getStartingPosition() {
		return startingPosition;
	}

	@JsonProperty("startingPosition")
	public void setStartingPosition(int startingPosition) {
		this.startingPosition = startingPosition;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}

}
