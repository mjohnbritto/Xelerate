package com.suntecgroup.xbmc.service.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "businessEntityProperty", "businessEntityAttributes" })
public class BusinessEntityAttribute {
	private BusinessEntityAttributeProperty businessEntityAttributeProperty;

	public BusinessEntityAttributeProperty getBusinessEntityAttributeProperty() {
		return this.businessEntityAttributeProperty;
	}

	public void setBusinessEntityAttributeProperty(BusinessEntityAttributeProperty businessEntityAttributeProperty) {
		this.businessEntityAttributeProperty = businessEntityAttributeProperty;
	}
}
