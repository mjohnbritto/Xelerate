package com.suntecgroup.bp.designer.frontend.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * BusinessEntityAttributeProperty Model class to hold Business Entity
 * information.
 *
 */
@JsonPropertyOrder({ "beAttrId", "beAttrName", "isMandatory", "foreignKeyBe", "dataType" })
public class BusinessEntityAttributeProperty {

	@JsonProperty("beAttrId")
	private Integer beAttrId;

	@JsonProperty("beAttrName")
	private String beAttrName;

	@JsonProperty("isMandatory")
	private boolean isMandatory;

	@JsonProperty("foreignKeyBe")
	private String foreignKeyBe;

	@JsonProperty("dataType")
	private DataType dataType;

	@JsonProperty("beAttrId")
	public int getBeAttrId() {
		return beAttrId;
	}

	@JsonProperty("beAttrId")
	public void setBeAttrId(int beAttrId) {
		this.beAttrId = beAttrId;
	}

	@JsonProperty("beAttrName")
	public String getBeAttrName() {
		return beAttrName;
	}

	@JsonProperty("beAttrName")
	public void setBeAttrName(String beAttrName) {
		this.beAttrName = beAttrName;
	}

	@JsonProperty("isMandatory")
	public boolean isMandatory() {
		return isMandatory;
	}

	@JsonProperty("isMandatory")
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	@JsonProperty("foreignKeyBe")
	public String getForeignKeyBe() {
		return foreignKeyBe;
	}

	@JsonProperty("foreignKeyBe")
	public void setForeignKeyBe(String foreignKeyBe) {
		this.foreignKeyBe = foreignKeyBe;
	}

	@JsonProperty("dataType")
	public DataType getDataType() {
		return dataType;
	}

	@JsonProperty("dataType")
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
}
