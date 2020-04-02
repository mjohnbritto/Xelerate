package com.suntecgroup.bpruntime.bean.adminconsole;

public class BPStatusDetails {

	private String bpName;
	private String bpStatus;
	private int bpSessionCount;
	private String bpGroupId;
	private String bpTemplateId;
	private String bpDescription;

	private String department;
	private String module;
	private String release;
	private String assetType;
	private String assetName;
	private int artifactId;

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

	public String getBpDescription() {
		return bpDescription;
	}

	public void setBpDescription(String bpDescription) {
		this.bpDescription = bpDescription;
	}

	public int getBpSessionCount() {
		return bpSessionCount;
	}

	public void setBpSessionCount(int bpSessionCount) {
		this.bpSessionCount = bpSessionCount;
	}

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

	public String getBpGroupId() {
		return bpGroupId;
	}

	public void setBpGroupId(String bpGroupId) {
		this.bpGroupId = bpGroupId;
	}

	public int hashcode() {
		return bpName.hashCode();
	}

	@Override
	public String toString() {
		return bpName;
	}

	public String getBpTemplateId() {
		return bpTemplateId;
	}

	public void setBpTemplateId(String bpTemplateId) {
		this.bpTemplateId = bpTemplateId;
	}

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}
}
