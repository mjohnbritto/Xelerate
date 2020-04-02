package com.suntecgroup.nifi.metaconfig.bean;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Processor {

	@SerializedName("name")
	@Expose
	String name;
	
	@SerializedName("properties")
	@Expose
	List<Property> properties;

	public Processor() {
	}
	
	public Processor(String name, List<Property> properties) {
		this.name = name;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
}