package com.suntecgroup.xbmc.service.model.compositebe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "beAttrName", "isMandatory", "dataType", "beAttrId" })
public class BusinessEntityAttributeProperty {

	@JsonProperty("beAttrName")
	private String beAttrName;

	@JsonProperty("isMandatory")
	private boolean isMandatory;

	@JsonProperty("dataType")
	private DataType dataType;

	@JsonProperty("beAttrId")
	private int beAttrId;

	@JsonProperty("beAttrName")
	public String getBeAttrName() {
		return beAttrName;
	}

	@JsonProperty("beAttrName")
	public void setBeAttrName(String beAttrName) {
		this.beAttrName = beAttrName;
	}

	@JsonProperty("isMandatory")
	public boolean getIsMandatory() {
		return isMandatory;
	}

	@JsonProperty("isMandatory")
	public void setIsMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	@JsonProperty("dataType")
	public DataType getDataType() {
		return dataType;
	}

	@JsonProperty("dataType")
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	@JsonProperty("beAttrId")
	public int getBeAttrId() {
		return beAttrId;
	}

	@JsonProperty("beAttrId")
	public void setBeAttrId(int beAttrId) {
		this.beAttrId = beAttrId;
	}

}
