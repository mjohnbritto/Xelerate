package com.suntecgroup.xbmc.service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * BEMetaData is a model class for holding Business Entity details.
 *
 */

@JsonPropertyOrder({ "businessEntityProperty", "businessEntityAttributes" })
public class BEMetaData {

	private BusinessEntityProperty businessEntityProperty;
	private List<BusinessEntityAttribute> businessEntityAttributes;

	public BusinessEntityProperty getBusinessEntityProperty() {
		return this.businessEntityProperty;
	}

	public void setBusinessEntityProperty(BusinessEntityProperty businessEntityProperty) {
		this.businessEntityProperty = businessEntityProperty;
	}

	public List<BusinessEntityAttribute> getBusinessEntityAttributes() {
		return this.businessEntityAttributes;
	}

	public void setBusinessEntityAttributes(List<BusinessEntityAttribute> businessEntityAttributes) {
		this.businessEntityAttributes = businessEntityAttributes;
	}
}