package com.suntecgroup.nifi.frontend.bean;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "type", "name", "aliasName", "context", "properties", "businessUniqueKeys", "businessEntityAttributeProperty" })
public class BusinessEntity {

	@JsonProperty("type")
	private String type;

	@JsonProperty("name")
	private String name;

	@JsonProperty("aliasName")
	private String aliasName;

	@JsonProperty("context")
	private Context context;

	@JsonProperty("properties")
	private Object properties;

	@JsonProperty("businessUniqueKeys")
	private List<Integer> businessUniqueKeys;

	@JsonProperty("businessEntityAttributeProperty")
	private List<BusinessEntityAttributeProperty> businessEntityAttributeProperty;

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("aliasName")
	public String getAliasName() {
		return aliasName;
	}

	@JsonProperty("aliasName")
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Object getProperties() {
		return properties;
	}

	public void setProperties(Object properties) {
		this.properties = properties;
	}

	@JsonProperty("businessUniqueKeys")
	public List<Integer> getBusinessUniqueKeys() {
		return businessUniqueKeys;
	}

	@JsonProperty("businessUniqueKeys")
	public void setBusinessUniqueKeys(List<Integer> businessUniqueKeys) {
		this.businessUniqueKeys = businessUniqueKeys;
	}

	@JsonProperty("businessEntityAttributeProperty")
	public List<BusinessEntityAttributeProperty> getBusinessEntityAttributeProperty() {
		return businessEntityAttributeProperty;
	}

	@JsonProperty("businessEntityAttributeProperty")
	public void setBusinessEntityAttributeProperty(
			List<BusinessEntityAttributeProperty> businessEntityAttributeProperty) {
		this.businessEntityAttributeProperty = businessEntityAttributeProperty;
	}
}