package com.suntecgroup.bp.designer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReviewNew {
	@JsonProperty("pmsAssetId")
	private Integer pmsAssetId;

	@JsonProperty("isExtendable")
	private String extendable;

	@JsonProperty("isProfileable")
	private String profileable;

	@JsonProperty("dependencies")
	private Dependencies dependencies;

	public Integer getPmsAssetId() {
		return pmsAssetId;
	}

	public void setPmsAssetId(Integer pmsAssetId) {
		this.pmsAssetId = pmsAssetId;
	}

	public String getExtendable() {
		return extendable;
	}

	public void setExtendable(String extendable) {
		this.extendable = extendable;
	}

	public String getProfileable() {
		return profileable;
	}

	public void setProfileable(String profileable) {
		this.profileable = profileable;
	}

	public Dependencies getDependencies() {
		return dependencies;
	}

	public void setDependencies(Dependencies dependencies) {
		this.dependencies = dependencies;
	}

}