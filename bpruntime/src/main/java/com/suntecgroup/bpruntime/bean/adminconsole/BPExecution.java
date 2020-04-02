package com.suntecgroup.bpruntime.bean.adminconsole;

public class BPExecution {

	private String sessionId;
	private String status;
	private String startTime;
	private String endTime;
	private String totalEventsProcessed;
	private String sessionProcessGroupId;
	private String sessionTemplateId;
	private String successCount;
	private String failureCount;
	private String inactiveSince;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTotalEventsProcessed() {
		return totalEventsProcessed;
	}

	public void setTotalEventsProcessed(String totalEventsProcessed) {
		this.totalEventsProcessed = totalEventsProcessed;
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

	public String getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(String successCount) {
		this.successCount = successCount;
	}

	public String getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(String failureCount) {
		this.failureCount = failureCount;
	}

	public String getInactiveSince() {
		return inactiveSince;
	}

	public void setInactiveSince(String inactiveSince) {
		this.inactiveSince = inactiveSince;
	}

}
