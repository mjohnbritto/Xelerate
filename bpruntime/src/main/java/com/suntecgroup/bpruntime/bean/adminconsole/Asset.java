package com.suntecgroup.bpruntime.bean.adminconsole;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "department", "module", "release", "pms", "assetType", "assetName", "assetDetail", "version",
		"status", "checkOutUser" })
public class Asset {
	private AssetCompositeKey id;
	@NotNull
	private String department;
	@NotNull
	private String module;
	@NotNull
	private String release;
	@NotNull
	private String pms;
	@NotNull
	private String assetType;
	@NotNull
	private String assetName;
	@NotNull
	private String assetDetail;
	private int version;
	private String status;
	private String checkOutUser;
	private int artifactId;
	private int assetVersion;

	public AssetCompositeKey getCompositeKey() {
		return id;
	}

	public void setCompositeKey(AssetCompositeKey id) {
		this.id = id;
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

	public String getAssetDetail() {
		return assetDetail;
	}

	public void setAssetDetail(String assetDetail) {
		this.assetDetail = assetDetail;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCheckOutUser() {
		return checkOutUser;
	}

	public void setCheckOutUser(String checkOutUser) {
		this.checkOutUser = checkOutUser;
	}

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

	public int getAssetVersion() {
		return assetVersion;
	}

	public void setAssetVersion(int assetVersion) {
		this.assetVersion = assetVersion;
	}

}
