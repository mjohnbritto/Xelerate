package com.suntecgroup.xbmc.service.model.compositebe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "businessEntityAttributeProperty" })
public class BusinessEntityAttributes {

	@JsonProperty("businessEntityAttributeProperty")
	private BusinessEntityAttributeProperty businessEntityAttributeProperty;

	@JsonProperty("businessEntityAttributeProperty")
	public BusinessEntityAttributeProperty getBusinessEntityAttributeProperty() {
		return this.businessEntityAttributeProperty;
	}

	@JsonProperty("businessEntityAttributeProperty")
	public void setBusinessEntityAttributeProperty(BusinessEntityAttributeProperty businessEntityAttributeProperty) {
		this.businessEntityAttributeProperty = businessEntityAttributeProperty;
	}
}
