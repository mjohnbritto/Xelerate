package com.suntecgroup.xbmc.service.model.baseVersion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "artifactId", "type", "assetName", "actionType" })
public class AssetDetails {

	@JsonProperty("artifactId")
	private int artifactId;
	@JsonProperty("type")
	private String type;
	@JsonProperty("assetName")
	private String assetName;
	@JsonProperty("actionType")
	private String actionType;

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public AssetDetails() {
	}

	/**
	 *
	 * @param actionType
	 * @param assetName
	 * @param artifactId
	 * @param type
	 */
	public AssetDetails(int artifactId, String type, String assetName, String actionType) {
		super();
		this.artifactId = artifactId;
		this.type = type;
		this.assetName = assetName;
		this.actionType = actionType;
	}

	@JsonProperty("artifactId")
	public int getArtifactId() {
		return artifactId;
	}

	@JsonProperty("artifactId")
	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("assetName")
	public String getAssetName() {
		return assetName;
	}

	@JsonProperty("assetName")
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	@JsonProperty("actionType")
	public String getActionType() {
		return actionType;
	}

	@JsonProperty("actionType")
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
}
