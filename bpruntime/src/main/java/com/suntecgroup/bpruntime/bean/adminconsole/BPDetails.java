package com.suntecgroup.bpruntime.bean.adminconsole;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BPDetails {

	@SerializedName("bpName")
	@Expose
	private String bpName;
	@SerializedName("operatorName")
	@Expose
	private String operatorName;
	@SerializedName("operatorProperties")
	@Expose
	private List<OperatorProperties> operatorProperties;

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

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public List<OperatorProperties> getOperatorProperties() {
		return operatorProperties;
	}

	public void setOperatorProperties(List<OperatorProperties> operatorProperties) {
		this.operatorProperties = operatorProperties;
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
