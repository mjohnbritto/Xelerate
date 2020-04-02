package com.suntecgroup.xbmc.service.model.baseVersion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "department", "module", "release" })
public class Context {

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
	public Context() {
	}

	/**
	 *
	 * @param release
	 * @param module
	 * @param department
	 */
	public Context(String department, String module, String release) {
		super();
		this.department = department;
		this.module = module;
		this.release = release;
	}

	@JsonProperty("department")
	public String getDepartment() {
		return department;
	}

	@JsonProperty("department")
	public void setDepartment(String department) {
		this.department = department;
	}

	@JsonProperty("module")
	public String getModule() {
		return module;
	}

	@JsonProperty("module")
	public void setModule(String module) {
		this.module = module;
	}

	@JsonProperty("release")
	public String getRelease() {
		return release;
	}

	@JsonProperty("release")
	public void setRelease(String release) {
		this.release = release;
	}
}
