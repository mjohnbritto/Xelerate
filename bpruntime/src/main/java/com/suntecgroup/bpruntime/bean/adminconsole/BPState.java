package com.suntecgroup.bpruntime.bean.adminconsole;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BPState {
	
	@SerializedName("bpName")
	@Expose
	private String bpName;
	@SerializedName("bpGroupId ")
	@Expose
	private String templateId;
	@SerializedName("templateId ")
	@Expose
	private String bpGroupId;
	@SerializedName("bpState")
	@Expose
	private String bpState;
	public String getBpName() {
		return bpName;
	}
	public void setBpName(String bpName) {
		this.bpName = bpName;
	}
	public String getBpGroupId() {
		return bpGroupId;
	}
	public void setBpGroupId(String bpGroupId) {
		this.bpGroupId = bpGroupId;
	}
	public String getBpState() {
		return bpState;
	}
	public void setBpState(String bpState) {
		this.bpState = bpState;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	

}
