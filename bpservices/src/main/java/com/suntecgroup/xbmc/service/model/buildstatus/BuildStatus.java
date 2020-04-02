package com.suntecgroup.xbmc.service.model.buildstatus;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildStatus {

	@JsonProperty("assetDetails")
	private List<AssetDetail> assetDetails;

	@JsonProperty("jobStatus")
	private JobStatus jobStatus;

	@JsonProperty("buildType")
	private String buildType;

	@JsonProperty("pmsIdentifier")
	private String pmsIdentifier;

	public List<AssetDetail> getAssetDetails() {
		return assetDetails;
	}

	public void setAssetDetails(List<AssetDetail> assetDetails) {
		this.assetDetails = assetDetails;
	}

	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getBuildType() {
		return buildType;
	}

	public void setBuildType(String buildType) {
		this.buildType = buildType;
	}

	public String getPmsIdentifier() {
		return pmsIdentifier;
	}

	public void setPmsIdentifier(String pmsIdentifier) {
		this.pmsIdentifier = pmsIdentifier;
	}

}