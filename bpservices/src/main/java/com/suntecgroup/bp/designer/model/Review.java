package com.suntecgroup.bp.designer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Review {
	@JsonProperty("pmsAssetId")
	private Integer pmsAssetId;

	@JsonProperty("isExtendable")
	private boolean extendable;

	@JsonProperty("isProfileable")
	private boolean profileable;

	@JsonProperty("dependencies")
	private Dependencies dependencies;

	public Integer getPmsAssetId() {
		return pmsAssetId;
	}

	public void setPmsAssetId(Integer pmsAssetId) {
		this.pmsAssetId = pmsAssetId;
	}

	public boolean isExtendable() {
		return extendable;
	}

	public void setExtendable(boolean extendable) {
		this.extendable = extendable;
	}

	public boolean isProfileable() {
		return profileable;
	}

	public void setProfileable(boolean profileable) {
		this.profileable = profileable;
	}

	public Dependencies getDependencies() {
		return dependencies;
	}

	public void setDependencies(Dependencies dependencies) {
		this.dependencies = dependencies;
	}

}