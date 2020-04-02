
package com.suntecgroup.nifi.frontend.bean.filechannelintegration.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentFixedWidth {

	@JsonProperty("attributeName")
	private String attributeName;
	@JsonProperty("startingPosition")
	private int startingPosition;
	@JsonProperty("width")
	private String width;
	@JsonProperty("dataType")
	private String dataType;
	@JsonProperty("format")
	private ContentFormat format;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public ContentFixedWidth() {
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public int getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(int startingPosition) {
		this.startingPosition = startingPosition;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public ContentFormat getFormat() {
		return format;
	}

	public void setFormat(ContentFormat format) {
		this.format = format;
	}


}