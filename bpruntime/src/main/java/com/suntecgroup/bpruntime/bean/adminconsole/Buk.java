package com.suntecgroup.bpruntime.bean.adminconsole;

/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 * Buk model class
 * 
 * @version 1.0 - March 2019
 * @author Thatchanamoorthy
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "attributeName", "attributeValue" })
public class Buk {

	@JsonProperty("attributeName")
   private String attributeName;
	@JsonProperty("attributeValue")
	private String attributeValue;

	@JsonProperty("attributeName")
	public String getAttributeName() {
     return attributeName;
	}

	@JsonProperty("attributeName")
	public void setAttributeName(String attributeName) {
     this.attributeName = attributeName;
	}

	@JsonProperty("attributeValue")
	public String getAttributeValue() {
     return attributeValue;
	}

	@JsonProperty("attributeValue")
	public void setAttributeValue(String attributeValue) {
     this.attributeValue = attributeValue;
	}

}