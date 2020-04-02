package com.suntecgroup.bp.designer.frontend.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Type {

	@SerializedName("typeCategory")
	@Expose
	private String typeCategory;
	
	@SerializedName("typeName")
	@Expose
	private String typeName;
	


	public Type(String typeCategory, String typeName) {
		this.typeCategory = null;
		this.typeName = null;
	}

	public String getTypeCategory() {
		return typeCategory;
	}

	public void setTypeCategory(String typeCategory) {
		this.typeCategory = typeCategory;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}


}
