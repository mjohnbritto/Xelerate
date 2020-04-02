package com.suntecgroup.nifi.datamodel;

public class SuntecMetaData {

	public SuntecMetaData() {
		departmentName = "none";
		moduleName = "none";
		releaseNo = "none";
		assetType = "none";
		assetName = "none";
		artifactId = 0;
	}

	// Meta Data
	private String departmentName;
	private String moduleName;
	private String releaseNo;
	private int artifactId;
	private String assetType;
	private String assetName;

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

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		if (departmentName != null)
			this.departmentName = departmentName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		if (moduleName != null)
			this.moduleName = moduleName;
	}

	public String getReleaseNo() {
		return releaseNo;
	}

	public void setReleaseNo(String releaseNo) {
		if (releaseNo != null)
			this.releaseNo = releaseNo;
	}

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}
	
}
