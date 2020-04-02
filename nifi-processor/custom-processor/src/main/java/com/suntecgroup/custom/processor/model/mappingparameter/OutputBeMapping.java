/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.mappingparameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "outputMapping", "pvMapping" })
public class OutputBeMapping {

	@JsonProperty("outputMapping")
	private String outputMapping = null;

	@JsonProperty("pvMapping")
	private String pvMapping = null;

	@JsonProperty("outputMapping")
	public String getOutputMapping() {
		return outputMapping;
	}
	@JsonProperty("outputMapping")
	public void setOutputMapping(String outputMapping) {
		this.outputMapping = outputMapping;
	}
	@JsonProperty("pvMapping")
	public String getPvMapping() {
		return pvMapping;
	}
	@JsonProperty("pvMapping")
	public void setPvMapping(String pvMapping) {
		this.pvMapping = pvMapping;
	}

	
	
}
