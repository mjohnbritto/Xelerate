package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MappingParams {

	@SerializedName("requestVarName")
	@Expose
	private String requestVarName;
	@SerializedName("mappedKey")
	@Expose
	private String mappedKey;

	public String getRequestVarName() {
		return requestVarName;
	}

	public void setRequestVarName(String requestVarName) {
		this.requestVarName = requestVarName;
	}

	public String getMappedKey() {
		return mappedKey;
	}

	public void setMappedKey(String mappedKey) {
		this.mappedKey = mappedKey;
	}
}
