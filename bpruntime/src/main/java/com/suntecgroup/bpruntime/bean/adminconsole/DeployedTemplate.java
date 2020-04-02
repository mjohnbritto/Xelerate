package com.suntecgroup.bpruntime.bean.adminconsole;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeployedTemplate {

	@SerializedName("bpName")
	@Expose
	private String bpName;

	@SerializedName("bpStatus")
	@Expose
	private String bpStatus;

	@SerializedName("bpSessionCount")
	@Expose
	private int bpSessionCount;

	@SerializedName("bpGroupId")
	@Expose
	private String bpGroupId;

	@SerializedName("bpTemplateId")
	@Expose
	private String bpTemplateId;

	@SerializedName("bpDescription")
	@Expose
	private String bpDescription;

	@SerializedName("department")
	@Expose
	private String department;

	@SerializedName("module")
	@Expose
	private String module;

	@SerializedName("release")
	@Expose
	private String release;

	@SerializedName("assetType")
	@Expose
	private String assetType;

	@SerializedName("assetName")
	@Expose
	private String assetName;

	@SerializedName("artifactId")
	@Expose
	private int artifactId;

	public String getBpName() {
		return bpName;
	}

	public void setBpName(String bpName) {
		this.bpName = bpName;
	}

	public String getBpStatus() {
		return bpStatus;
	}

	public void setBpStatus(String bpStatus) {
		this.bpStatus = bpStatus;
	}

	public int getBpSessionCount() {
		return bpSessionCount;
	}

	public void setBpSessionCount(int bpSessionCount) {
		this.bpSessionCount = bpSessionCount;
	}

	public String getBpGroupId() {
		return bpGroupId;
	}

	public void setBpGroupId(String bpGroupId) {
		this.bpGroupId = bpGroupId;
	}

	public String getBpTemplateId() {
		return bpTemplateId;
	}

	public void setBpTemplateId(String bpTemplateId) {
		this.bpTemplateId = bpTemplateId;
	}

	public String getBpDescription() {
		return bpDescription;
	}

	public void setBpDescription(String bpDescription) {
		this.bpDescription = bpDescription;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}
}
