package com.suntecgroup.nifi.template.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TemplateFlags {

	@SerializedName("isMandatory")
	@Expose
	private boolean isMandatory;
	@SerializedName("isProfileableAtSolutions")
	@Expose
	private boolean isProfileableAtSolutions;
	@SerializedName("isProfileableAtOperation")
	@Expose
	private boolean isProfileable;
	

	
	public boolean isMandatory() {
		return isMandatory;
	}
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	public boolean isProfileableAtSolutions() {
		return isProfileableAtSolutions;
	}
	public void setProfieableAtSolutions(boolean isProfileableAtSolutions) {
		this.isProfileableAtSolutions = isProfileableAtSolutions;
	}
	public boolean isProfieableAtOperation() {
		return isProfileable;
	}
	public void setProfieableAtOperation(boolean isProfileableAtOperation) {
		this.isProfileable = isProfileableAtOperation;
	}
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("isMandatory", isMandatory).append("isProfileableAtSolutions", isProfileableAtSolutions)
				.append("isProfileableAtOperation", isProfileable).toString();
	}
	
	
}
