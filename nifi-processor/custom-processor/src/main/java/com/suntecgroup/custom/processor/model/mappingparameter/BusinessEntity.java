/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.mappingparameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "artifactId", "beName", "department", "module", "release", "businessUniqueKeys",
		"businessEntityAttributeProperty" })
public class BusinessEntity {

	@JsonProperty("artifactId")
	private Integer artifactId;

	@JsonProperty("beName")
	private String beName;

	@JsonProperty("department")
	private String department;

	@JsonProperty("module")
	private String module;

	@JsonProperty("release")
	private String release;

	@JsonProperty("businessUniqueKeys")
	private List<Integer> businessUniqueKeys;

	@JsonProperty("businessEntityAttributeProperty")
	private List<BusinessEntityAttributeProperty> businessEntityAttributeProperty;

	@JsonProperty("artifactId")
	public Integer getArtifactId() {
		return artifactId;
	}

	@JsonProperty("artifactId")
	public void setArtifactId(Integer artifactId) {
		this.artifactId = artifactId;
	}

	@JsonProperty("beName")
	public String getBeName() {
		return beName;
	}

	@JsonProperty("beName")
	public void setBeName(String beName) {
		this.beName = beName;
	}

	@JsonProperty("department")
	public String getDepartment() {
		return department;
	}

	@JsonProperty("department")
	public void setDepartment(String department) {
		this.department = department;
	}

	@JsonProperty("module")
	public String getModule() {
		return module;
	}

	@JsonProperty("module")
	public void setModule(String module) {
		this.module = module;
	}

	@JsonProperty("release")
	public String getRelease() {
		return release;
	}

	@JsonProperty("release")
	public void setRelease(String release) {
		this.release = release;
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

	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}