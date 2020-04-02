package com.suntecgroup.bp.designer.frontend.beans;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinkProperties {
	
	@SerializedName("businessSettings")
	@Expose
	private BusinessSettings businessSettings;

	@SerializedName("mapping")
	@Expose
	private List<Mapping> mapping;
	@SerializedName("properties")
	@Expose
	private List<Property> properties;
	@SerializedName("comments")
	@Expose
	private Comments comments;
	public BusinessSettings getBusinessSettings() {
		return businessSettings;
	}
	public void setBusinessSettings(BusinessSettings businessSettings) {
		this.businessSettings = businessSettings;
	}
	
	public List<Mapping> getMapping() {
		return mapping;
	}
	public void setMapping(List<Mapping> mapping) {
		this.mapping = mapping;
	}
	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public Comments getComments() {
		return comments;
	}
	public void setComments(Comments comments) {
		this.comments = comments;
	}

}
