package com.suntecgroup.bp.designer.frontend.beans;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Functional {

	@SerializedName("businessProcessSetup")
	@Expose
	private BusinessProcessSetup businessProcessSetup;
	@SerializedName("processVariables")
	@Expose
	private List<ProcessVariables> processVariables;

	public BusinessProcessSetup getBusinessProcessSetup() {
		return businessProcessSetup;
	}

	public void setBusinessProcessSetup(BusinessProcessSetup businessProcessSetup) {
		this.businessProcessSetup = businessProcessSetup;
	}

	public List<ProcessVariables> getProcessVariables() {
		return processVariables;
	}

	public void setProcessVariables(List<ProcessVariables> processVariables) {
		this.processVariables = processVariables;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("businessProcessSetup", businessProcessSetup)
				.append("processVariables", processVariables).toString();
	}
}
