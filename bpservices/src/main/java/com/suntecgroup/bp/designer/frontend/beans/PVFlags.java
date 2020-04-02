package com.suntecgroup.bp.designer.frontend.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PVFlags {

	@SerializedName("isMandatory")
	@Expose
	private boolean isIsMandatory;
	@SerializedName("isProfileableAtSolutions")
	@Expose
	private boolean isIsProfileableAtSolutions;
	@SerializedName("isProfileableAtOperation")
	@Expose
	private boolean isIsProfileableAtOperation;

	public boolean isIsMandatory() {
		return isIsMandatory;
	}

	public void setIsMandatory(boolean isIsMandatory) {
		this.isIsMandatory = isIsMandatory;
	}

	public boolean isIsProfileableAtSolutions() {
		return isIsProfileableAtSolutions;
	}

	public void setIsProfileableAtSolutions(boolean isIsProfileableAtSolutions) {
		this.isIsProfileableAtSolutions = isIsProfileableAtSolutions;
	}

	public boolean isIsProfileableAtOperation() {
		return isIsProfileableAtOperation;
	}

	public void setIsProfileableAtOperation(boolean isIsProfileableAtOperation) {
		this.isIsProfileableAtOperation = isIsProfileableAtOperation;
	}

}
