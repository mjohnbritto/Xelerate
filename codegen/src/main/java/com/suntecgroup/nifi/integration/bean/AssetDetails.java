package com.suntecgroup.nifi.integration.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetDetails {

	@JsonProperty("assetType")
	private String assetType;
	@JsonProperty("assetName")
	private String assetName;
	@JsonProperty("department")
	private String department;
	@JsonProperty("module")
	private String module;
	@JsonProperty("release")
	private String release;
	@JsonProperty("pms")
	private String pms;
	@JsonProperty("pmsId")
	private String pmsId;

	@JsonProperty("assetName")
	public String getAssetName() {
		return assetName;
	}

	@JsonProperty("assetName")
	public void setAssetName(String assetName) {
		this.assetName = assetName;
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

	@JsonProperty("assetType")
	public String getAssetType() {
		return assetType;
	}

	@JsonProperty("assetType")
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getPms() {
		return pms;
	}

	public void setPms(String pms) {
		this.pms = pms;
	}

	public String getPmsId() {
		return pmsId;
	}

	public void setPmsId(String pmsId) {
		this.pmsId = pmsId;
	}

}