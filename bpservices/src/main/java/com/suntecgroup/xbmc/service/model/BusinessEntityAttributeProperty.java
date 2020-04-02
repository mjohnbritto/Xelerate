package com.suntecgroup.xbmc.service.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * BusinessEntityAttributeProperty Model class to hold Business Entity
 * information.
 *
 */
@JsonPropertyOrder({ "beAttrName", "relationshipside", "isMandatory", "dataType", "beAttrId" })
public class BusinessEntityAttributeProperty {
	private String beAttrName;
	private String relationshipside;
	private boolean isMandatory;
	private DataType dataType;
	private int beAttrId;

	public String getRelationshipside() {
		return this.relationshipside;
	}

	public void setRelationshipside(String relationshipside) {
		this.relationshipside = relationshipside;
	}

	public int getBeAttrId() {
		return beAttrId;
	}

	public void setBeAttrId(int beAttrId) {
		this.beAttrId = beAttrId;
	}

	public String getBeAttrName() {
		return beAttrName;
	}

	public void setBeAttrName(String beAttrName) {
		this.beAttrName = beAttrName;
	}

	public boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "ClassPojo [beAttrId = " + beAttrId + ", dataType = " + dataType + ", beAttrName = " + beAttrName
				+ ", isMandatory = " + isMandatory + "]";
	}
}
