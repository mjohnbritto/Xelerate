package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Context {

	@SerializedName("artifactId")
	@Expose
	private Integer artifactId;

	@SerializedName("department")
	@Expose
	private String department;

	@SerializedName("ownerDepartment")
	@Expose
	private String ownerDepartment;

	@SerializedName("module")
	@Expose
	private String module;

	@SerializedName("release")
	@Expose
	private String release;

	public Integer getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(Integer artifactId) {
		this.artifactId = artifactId;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getOwnerDepartment() {
		return ownerDepartment;
	}

	public void setOwnerDepartment(String ownerDepartment) {
		this.ownerDepartment = ownerDepartment;
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

}
