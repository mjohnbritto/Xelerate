package com.suntecgroup.xbmc.service.model.baseVersion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "assetDetails", "context" })
public class Data {

	@JsonProperty("assetDetails")
	private AssetDetails assetDetails;
	@JsonProperty("context")
	private Context context;

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public Data() {
	}

	/**
	 *
	 * @param assetDetails
	 * @param context
	 */
	public Data(AssetDetails assetDetails, Context context) {
		super();
		this.assetDetails = assetDetails;
		this.context = context;
	}

	@JsonProperty("assetDetails")
	public AssetDetails getAssetDetails() {
		return assetDetails;
	}

	@JsonProperty("assetDetails")
	public void setAssetDetails(AssetDetails assetDetails) {
		this.assetDetails = assetDetails;
	}

	@JsonProperty("context")
	public Context getContext() {
		return context;
	}

	@JsonProperty("context")
	public void setContext(Context context) {
		this.context = context;
	}

}