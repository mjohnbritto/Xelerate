package com.suntecgroup.nifi.integration.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Event {

	@JsonProperty("eventType")
	private String eventType;
	@JsonProperty("assetDetails")
	private AssetDetails assetDetails;

	@JsonProperty("eventType")
	public String getEventType() {
		return eventType;
	}

	@JsonProperty("eventType")
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	@JsonProperty("assetDetails")
	public AssetDetails getAssetDetails() {
		return assetDetails;
	}

	@JsonProperty("assetDetails")
	public void setAssetDetails(AssetDetails assetDetails) {
		this.assetDetails = assetDetails;
	}

}