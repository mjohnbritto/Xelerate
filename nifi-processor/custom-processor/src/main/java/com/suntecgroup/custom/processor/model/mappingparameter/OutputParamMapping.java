/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.mappingparameter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "outputMapping" })
public class OutputParamMapping {

	@JsonProperty("outputMapping")
	private List<Mapping> outputMapping = null;

	
	@JsonProperty("pvMapping")
	private List<Mapping> pvMapping = null;
	
	public List<Mapping> getPvMapping() {
		return pvMapping;
	}

	public void setPvMapping(List<Mapping> pvMapping) {
		this.pvMapping = pvMapping;
	}

	public List<Mapping> getOutputMapping() {
		return outputMapping;
	}

	public void setOutputMapping(List<Mapping> outputMapping) {
		this.outputMapping = outputMapping;
	}

}
