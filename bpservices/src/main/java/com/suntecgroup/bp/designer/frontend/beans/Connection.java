package com.suntecgroup.bp.designer.frontend.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connection {
		
	@SerializedName("ui_attributes")
	@Expose
	private UIAttributes uiAttributes;

	@SerializedName("link_properties")
	@Expose
	private LinkProperties linkProperties;

	public UIAttributes getUiAttributes() {
		return uiAttributes;
	}

	public void setUiAttributes(UIAttributes uiAttributes) {
		this.uiAttributes = uiAttributes;
	}

	public LinkProperties getLinkProperties() {
		return linkProperties;
	}

	public void setLinkProperties(LinkProperties linkProperties) {
		this.linkProperties = linkProperties;
	}

	

}
