package com.suntecgroup.bp.designer.model;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"department","module","release","artifact_id","assetType","assetName","version","status","checkOutUser"})
@Document(collection = "AssetComments")
public class AssetComments {
	@Id
	private CompositeKey compositeKey;
	@NotNull
	private String department;
	@NotNull
	private String module;
	@NotNull
	private String release;

	@NotNull
	private String assetType;
	@NotNull
	private String assetName;
	private int version;
	private String status;
	private String checkOutUser;
	
	private int artifactId;
	private String actionType;
	
	
	public int getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	private ArrayList<UserComments> designerComments;
	private ArrayList<UserComments> reviewerComments;
	private ArrayList<UserComments> approverComments;
	
	public CompositeKey getCompositeKey() {
		return compositeKey;
	}
	public void setCompositeKey(CompositeKey compositeKey) {
		this.compositeKey = compositeKey;
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
//	public String getPms() {
//		return pms;
//	}
//	public void setPms(String pms) {
//		this.pms = pms;
//	}
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
	public ArrayList<UserComments> getDesignerComments() {
		return designerComments;
	}
	public void setDesignerComments(ArrayList<UserComments> designerComments) {
		this.designerComments = designerComments;
	}
	public ArrayList<UserComments> getReviewerComments() {
		return reviewerComments;
	}
	public void setReviewerComments(ArrayList<UserComments> reviewerComments) {
		this.reviewerComments = reviewerComments;
	}
	public ArrayList<UserComments> getApproverComments() {
		return approverComments;
	}
	public void setApproverComments(ArrayList<UserComments> approverComments) {
		this.approverComments = approverComments;
	}
	

}
