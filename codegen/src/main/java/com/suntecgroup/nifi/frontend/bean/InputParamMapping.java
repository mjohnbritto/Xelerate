package com.suntecgroup.nifi.frontend.bean;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InputParamMapping {	

	@SerializedName("pathParams")
	@Expose
	private List<MappingParams> pathParams;

	@SerializedName("queryParams")
	@Expose
	private List<MappingParams> queryParams;

	@SerializedName("contentParam")
	@Expose
	private String contentParam;

	public List<MappingParams> getPathParams() {
		return pathParams;
	}

	public void setPathParams(List<MappingParams> pathParams) {
		this.pathParams = pathParams;
	}

	public List<MappingParams> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(List<MappingParams> queryParams) {
		this.queryParams = queryParams;
	}

	public String getContentParam() {
		return contentParam;
	}

	public void setContentParam(String contentParam) {
		this.contentParam = contentParam;
	}	
	
}
