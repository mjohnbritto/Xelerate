package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BPFlowUIResponse {

	@SerializedName("department")
	@Expose
	private String department;
	@SerializedName("module")
	@Expose
	private String module;
	@SerializedName("release")
	@Expose
	private String release;

	@SerializedName("artifactId")
	@Expose
	private int artifactId;

	@SerializedName("pms")
	@Expose
	private String pms;
	@SerializedName("assetType")
	@Expose
	private String assetType;

	@SerializedName("assetName")
	@Expose
	private String assetName;

	@SerializedName("checkOutUser")
	@Expose
	private String checkOutUser;

	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("version")
	@Expose
	private String version;

	@SerializedName("assetDetail")
	@Expose
	private String assetDetail;

	@SerializedName("id")
	@Expose
	private CompositeKeyId id;

	@SerializedName("actionType")
	@Expose
	private String actionType;

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
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

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

	public String getPms() {
		return pms;
	}

	public void setPms(String pms) {
		this.pms = pms;
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

	public String getCheckOutUser() {
		return checkOutUser;
	}

	public void setCheckOutUser(String checkOutUser) {
		this.checkOutUser = checkOutUser;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAssetDetail() {
		return assetDetail;
	}

	public void setAssetDetail(String assetDetail) {
		this.assetDetail = assetDetail;
	}

	public CompositeKeyId getId() {
		return id;
	}

	public void setId(CompositeKeyId id) {
		this.id = id;
	}
}