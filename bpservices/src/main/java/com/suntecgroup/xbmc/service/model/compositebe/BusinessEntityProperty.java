package com.suntecgroup.xbmc.service.model.compositebe;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "artifactId", "beName", "anchorBe", "department", "ownerDepartment", "module", "release",
		"isCompositeBE", "businessUniqueKeys", "relationshipData" })
public class BusinessEntityProperty {

	@JsonProperty("artifactId")
	private int artifactId;

	@JsonProperty("beName")
	private String beName;

	@JsonProperty("anchorBe")
	private String anchorBe;

	@JsonProperty("department")
	private String department;

	@JsonProperty("ownerDepartment")
	private String ownerDepartment;

	@JsonProperty("module")
	private String module;

	@JsonProperty("release")
	private String release;

	@JsonProperty("isCompositeBE")
	private String isCompositeBE;

	@JsonProperty("businessUniqueKeys")
	private List<Integer> businessUniqueKeys;

	@JsonProperty("relationshipData")
	private RelationShipData relationshipData;

	@JsonProperty("artifactId")
	public int getArtifactId() {
		return artifactId;
	}

	@JsonProperty("artifactId")
	public void setArtifactId(int artifactId) {
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

	@JsonProperty("anchorBe")
	public String getAnchorBe() {
		return anchorBe;
	}

	@JsonProperty("anchorBe")
	public void setAnchorBe(String anchorBe) {
		this.anchorBe = anchorBe;
	}

	@JsonProperty("department")
	public String getDepartment() {
		return department;
	}

	@JsonProperty("department")
	public void setDepartment(String department) {
		this.department = department;
	}

	@JsonProperty("ownerDepartment")
	public String getOwnerDepartment() {
		return ownerDepartment;
	}

	@JsonProperty("ownerDepartment")
	public void setOwnerDepartment(String ownerDepartment) {
		this.ownerDepartment = ownerDepartment;
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

	@JsonProperty("isCompositeBE")
	public String getIsCompositeBE() {
		return isCompositeBE;
	}

	@JsonProperty("isCompositeBE")
	public void setIsCompositeBE(String isCompositeBE) {
		this.isCompositeBE = isCompositeBE;
	}

	@JsonProperty("businessUniqueKeys")
	public List<Integer> getBusinessUniqueKeys() {
		return businessUniqueKeys;
	}

	@JsonProperty("businessUniqueKeys")
	public void setBusinessUniqueKeys(List<Integer> businessUniqueKeys) {
		this.businessUniqueKeys = businessUniqueKeys;
	}

	@JsonProperty("relationshipData")
	public RelationShipData getRelationshipData() {
		return relationshipData;
	}

	@JsonProperty("relationshipData")
	public void setRelationshipData(RelationShipData relationshipData) {
		this.relationshipData = relationshipData;
	}

}
