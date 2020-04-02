/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.startandend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 * This is a model/pojo class to the request/response to data
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "isMandatory", "isProfileableAtSolutions", "isProfileableAtOperation" })
public class Flags {

	@JsonProperty("isMandatory")
	private Boolean isMandatory;
	@JsonProperty("isProfileableAtSolutions")
	private Boolean isProfileableAtSolutions;
	@JsonProperty("isProfileableAtOperation")
	private Boolean isProfileableAtOperation;

	@JsonProperty("isMandatory")
	public Boolean getIsMandatory() {
		return isMandatory;
	}

	@JsonProperty("isMandatory")
	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	@JsonProperty("isProfileableAtSolutions")
	public Boolean getIsProfileableAtSolutions() {
		return isProfileableAtSolutions;
	}

	@JsonProperty("isProfileableAtSolutions")
	public void setIsProfileableAtSolutions(Boolean isProfileableAtSolutions) {
		this.isProfileableAtSolutions = isProfileableAtSolutions;
	}

	@JsonProperty("isProfileableAtOperation")
	public Boolean getIsProfileableAtOperation() {
		return isProfileableAtOperation;
	}

	@JsonProperty("isProfileableAtOperation")
	public void setIsProfileableAtOperation(Boolean isProfileableAtOperation) {
		this.isProfileableAtOperation = isProfileableAtOperation;
	}

	@Override
	public String toString() {
		return "Flags [isMandatory=" + isMandatory + ", isProfileableAtSolutions=" + isProfileableAtSolutions
				+ ", isProfileableAtOperation=" + isProfileableAtOperation + "]";
	}

}
