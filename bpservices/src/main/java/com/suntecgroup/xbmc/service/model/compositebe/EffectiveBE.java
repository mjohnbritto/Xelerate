
package com.suntecgroup.xbmc.service.model.compositebe;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "businessEntityProperty", "businessEntityAttributes", "cbeMapping", "assetType" })

public class EffectiveBE {

	@JsonProperty("businessEntityProperty")
	private BusinessEntityProperty businessEntityObject;

	@JsonProperty("businessEntityAttributes")
	private List<BusinessEntityAttributes> businessEntityAttributes;

	@JsonProperty("cbeMapping")
	private CBEMapping cbeMappingObject;

	@JsonProperty("assetType")
	private String assetType;

	@JsonProperty("businessEntityProperty")
	public BusinessEntityProperty getBusinessEntityObject() {
		return businessEntityObject;
	}

	@JsonProperty("businessEntityProperty")
	public void setBusinessEntityObject(BusinessEntityProperty businessEntityObject) {
		this.businessEntityObject = businessEntityObject;
	}

	@JsonProperty("businessEntityAttributes")
	public List<BusinessEntityAttributes> getBusinessEntityAttributes() {
		return businessEntityAttributes;
	}

	@JsonProperty("businessEntityAttributes")
	public void setBusinessEntityAttributes(List<BusinessEntityAttributes> businessEntityAttributes) {
		this.businessEntityAttributes = businessEntityAttributes;
	}

	@JsonProperty("cbeMapping")
	public CBEMapping getCbeMappingObject() {
		return cbeMappingObject;
	}

	@JsonProperty("cbeMapping")
	public void setCbeMappingObject(CBEMapping cbeMappingObject) {
		this.cbeMappingObject = cbeMappingObject;
	}

	@JsonProperty("assetType")
	public String getAssetType() {
		return assetType;
	}

	@JsonProperty("assetType")
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

}
