/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.contextparameter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "contextParameters" })
public class ContextParamData {

	@JsonProperty("contextParameters")
	private List<ContextParameter> contextParameters = null;

	@JsonProperty("contextParameters")
	public List<ContextParameter> getContextParameters() {
		return contextParameters;
	}

	@JsonProperty("contextParameters")
	public void setContextParameters(List<ContextParameter> contextParameters) {
		this.contextParameters = contextParameters;
	}
}
