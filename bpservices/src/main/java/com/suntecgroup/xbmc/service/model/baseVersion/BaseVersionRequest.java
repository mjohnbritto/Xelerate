package com.suntecgroup.xbmc.service.model.baseVersion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseVersionRequest {

	@JsonProperty("artifactId")
	private long artifactId;

	@JsonProperty("type")
	private String type;

	@JsonProperty("assetName")
	private String assetName;

	@JsonProperty("department")
	private String department;

	@JsonProperty("module")
	private String module;

	@JsonProperty("release")
	private String release;

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public BaseVersionRequest() {
	}

	public long getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(long artifactId) {
		this.artifactId = artifactId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

}