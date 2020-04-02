package com.suntecgroup.xbmc.service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "businessUniqueKeys", "beName", "department", "module", "artifactId" })
public class BusinessEntityProperty {
	private List<Integer> businessUniqueKeys;
	private String beName; 
	private String department;
	private String module;
	private int artifactId;

	public List<Integer> getBusinessUniqueKeys() {
		return this.businessUniqueKeys;
	}

	public void setBusinessUniqueKeys(List<Integer> businessUniqueKeys) {
		this.businessUniqueKeys = businessUniqueKeys;
	}

	public String getBeName() {
		return this.beName;
	}

	public void setBeName(String beName) {
		this.beName = beName;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getModule() {
		return this.module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public int getArtifactId() {
		return this.artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}
}
