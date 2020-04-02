package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompositeKeyId {

	@SerializedName("department")
	@Expose
	private String department;

	@SerializedName("module")
	@Expose
	private String module;

	@SerializedName("release")
	@Expose
	private String release;

	@SerializedName("pms")
	@Expose
	private String pms;

	@SerializedName("version")
	@Expose
	private int version;

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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
