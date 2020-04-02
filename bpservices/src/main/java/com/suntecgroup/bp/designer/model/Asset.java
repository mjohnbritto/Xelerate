package com.suntecgroup.bp.designer.model;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "department", "module", "release", "pms", "assetType", "assetName", "assetDetail", "version",
		"status", "checkOutUser" })
@Document(collection = "Asset")
public class Asset {
	@Id
	private CompositeKey id;
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
	@NotNull
	private String actionType;
	@NotNull
	private int artifact_id;
	private int version;
	private String status;
	private String checkOutUser;

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public int getArtifact_id() {
		return artifact_id;
	}

	public void setArtifact_id(int artifact_id) {
		this.artifact_id = artifact_id;
	}

	public CompositeKey getCompositeKey() {
		return id;
	}

	public void setCompositeKey(CompositeKey id) {
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

}
