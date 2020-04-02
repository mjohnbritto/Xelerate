/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.mappingparameter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "inputMapping" })
public class InputParamMapping {

	@JsonProperty("inputMapping")
	private List<Mapping> inputMapping = null;

	@JsonProperty("inputJSON")
	private String inputJSON = null;

	@JsonProperty("pathParam")
	private String pathParam = null;

	@JsonProperty("queryParam")
	private String queryParam = null;

	
	@JsonProperty("inputMapping")
	public List<Mapping> getInputMapping() {
		return inputMapping;
	}

	@JsonProperty("inputMapping")
	public void setInputMapping(List<Mapping> inputMapping) {
		this.inputMapping = inputMapping;
	}

	@JsonProperty("inputJSON")
	public String getInputJSON() {
		return inputJSON;
	}
	@JsonProperty("inputJSON")
	public void setInputJSON(String inputJSON) {
		this.inputJSON = inputJSON;
	}
	@JsonProperty("pathParam")
	public String getPathParam() {
		return pathParam;
	}
	@JsonProperty("pathParam")
	public void setPathParam(String pathParam) {
		this.pathParam = pathParam;
	}
	@JsonProperty("queryParam")
	public String getQueryParam() {
		return queryParam;
	}
	@JsonProperty("queryParam")
	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}

	
	
	

}
