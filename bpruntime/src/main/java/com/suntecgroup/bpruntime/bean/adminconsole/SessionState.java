package com.suntecgroup.bpruntime.bean.adminconsole;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionState {
	@SerializedName("sessionId")
	@Expose
	private String sessionId;
	@SerializedName("sessionProcessGroupId ")
	@Expose
	private String sessionProcessGroupId;
	@SerializedName("status ")
	@Expose
	private String status;
	@SerializedName("sessionTemplateId")
	@Expose
	private String sessionTemplateId;
	@SerializedName("runNumber")
	@Expose
	private String runNumber;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionProcessGroupId() {
		return sessionProcessGroupId;
	}

	public void setSessionProcessGroupId(String sessionProcessGroupId) {
		this.sessionProcessGroupId = sessionProcessGroupId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSessionTemplateId() {
		return sessionTemplateId;
	}

	public void setSessionTemplateId(String sessionTemplateId) {
		this.sessionTemplateId = sessionTemplateId;
	}

	public String getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(String runNumber) {
		this.runNumber = runNumber;
	}

}
