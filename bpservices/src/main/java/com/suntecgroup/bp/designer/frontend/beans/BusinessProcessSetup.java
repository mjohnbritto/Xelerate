package com.suntecgroup.bp.designer.frontend.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessProcessSetup {

	@SerializedName("processName")
	@Expose
	private String processName;
	@SerializedName("processDescription")
	@Expose
	private String processDescription;
	@SerializedName("enableBoundedExecution")
	@Expose
	private boolean enableBoundedExecution;
	@SerializedName("isProfileable")
	@Expose
	private boolean isProfileable;

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessDescription() {
		return processDescription;
	}

	public void setProcessDescription(String processDescription) {
		this.processDescription = processDescription;
	}

	public boolean isEnableBoundedExecution() {
		return enableBoundedExecution;
	}

	public void setEnableBoundedExecution(boolean enableBoundedExecution) {
		this.enableBoundedExecution = enableBoundedExecution;
	}

	public boolean isProfileable() {
		return isProfileable;
	}

	public void setProfileable(boolean isProfileable) {
		this.isProfileable = isProfileable;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("processName", processName)
				.append("processDescription", processDescription)
				.append("enableBoundedExecution", enableBoundedExecution).append("isProfileable", isProfileable)
				.toString();
	}
}
