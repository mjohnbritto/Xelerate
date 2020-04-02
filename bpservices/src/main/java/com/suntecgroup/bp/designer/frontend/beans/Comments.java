package com.suntecgroup.bp.designer.frontend.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comments {

	@SerializedName("comments")
	@Expose
	private String comment;

	public String getComments() {
		return this.comment;
	}

	public void setComments(String comments) {
		this.comment = comments;
	}
}
