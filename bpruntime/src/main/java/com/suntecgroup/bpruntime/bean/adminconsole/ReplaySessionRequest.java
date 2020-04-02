package com.suntecgroup.bpruntime.bean.adminconsole;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReplaySessionRequest {
	@SerializedName("sessionId")
	@Expose
	private String sessionId;
	@SerializedName("runNumber")
	@Expose
	private String runNumber;
	@SerializedName("sessionProcessGroupId ")
	@Expose
	private String sessionProcessGroupId;
	@SerializedName("sessionTemplateId")
	@Expose
	private String sessionTemplateId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(String runNumber) {
		this.runNumber = runNumber;
	}

	public String getSessionProcessGroupId() {
		return sessionProcessGroupId;
	}

	public void setSessionProcessGroupId(String sessionProcessGroupId) {
		this.sessionProcessGroupId = sessionProcessGroupId;
	}

	public String getSessionTemplateId() {
		return sessionTemplateId;
	}

	public void setSessionTemplateId(String sessionTemplateId) {
		this.sessionTemplateId = sessionTemplateId;
	}

}
