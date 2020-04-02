package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InputBeAttributeArray {

	@SerializedName("beAttrName")
	@Expose
	private String beAttrName;

	@SerializedName("relationshipside")
	@Expose
	private String relationshipside;

	@SerializedName("isMandatory")
	@Expose
	private boolean isMandatory;
	@SerializedName("dataType")
	@Expose
	private DataType dataType;

	@SerializedName("beAttrId")
	@Expose
	private int beAttrId;

	public String getBeAttrName() {
		return beAttrName;
	}

	public void setBeAttrName(String beAttrName) {
		this.beAttrName = beAttrName;
	}

	public String getRelationshipside() {
		return relationshipside;
	}

	public void setRelationshipside(String relationshipside) {
		this.relationshipside = relationshipside;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public int getBeAttrId() {
		return beAttrId;
	}

	public void setBeAttrId(int beAttrId) {
		this.beAttrId = beAttrId;
	}

}
