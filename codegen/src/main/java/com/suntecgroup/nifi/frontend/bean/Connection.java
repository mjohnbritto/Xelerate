package com.suntecgroup.nifi.frontend.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connection {
		
	@SerializedName("ui_attributes")
	@Expose
	private UIAttributes ui_attributes;
	
	@SerializedName("link_properties")
	@Expose
	private LinkProperties link_properties;

	
	public Connection(UIAttributes ui_attributes, LinkProperties link_properties) {
		super();
		this.ui_attributes = ui_attributes;
		this.link_properties = link_properties;
	}

	public UIAttributes getUi_attributes() {
		return ui_attributes;
	}

	public void setUi_attributes(UIAttributes ui_attributes) {
		this.ui_attributes = ui_attributes;
	}

	public LinkProperties getLink_properties() {
		return link_properties;
	}

	public void setLink_properties(LinkProperties link_properties) {
		this.link_properties = link_properties;
	}

	@Override
	public String toString() {
		return "Connection [from " + ui_attributes.getSourceName() + " to " + ui_attributes.getDestinationName() + "]";
	}

}
