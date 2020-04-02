/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.startandend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 * This is a model/pojo class to the request/response to data
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "typeCategory", "typeName" })
public class Type {

	@JsonProperty("typeCategory")
	private String typeCategory;
	@JsonProperty("typeName")
	private String typeName;

	@JsonProperty("typeCategory")
	public String getTypeCategory() {
		return typeCategory;
	}

	@JsonProperty("typeCategory")
	public void setTypeCategory(String typeCategory) {
		this.typeCategory = typeCategory;
	}

	@JsonProperty("typeName")
	public String getTypeName() {
		return typeName;
	}

	@JsonProperty("typeName")
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String toString() {
		return "Type [typeCategory=" + typeCategory + ", typeName=" + typeName + "]";
	}
	
	

}
