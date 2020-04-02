package com.suntecgroup.bp.designer.model;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"review","assetComments"})
public class SendForReview {
	@Valid
	private Review review;
	@Valid
	private AssetComments assetComments;
	public Review getReview() {
		return review;
	}
	public void setReview(Review review) {
		this.review = review;
	}
	public AssetComments getAssetComments() {
		return assetComments;
	}
	public void setAssetComments(AssetComments assetComments) {
		this.assetComments = assetComments;
	}
	
	
}
