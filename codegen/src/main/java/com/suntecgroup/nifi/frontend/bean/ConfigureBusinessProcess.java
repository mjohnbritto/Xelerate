package com.suntecgroup.nifi.frontend.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConfigureBusinessProcess {
	@SerializedName("functional")
	@Expose
	private Functional functional;
	
	@SerializedName("properties")
	@Expose
	private BPsetUpProperties properties;
	

	public Functional getFunctional() {
		return functional;
	}
	public void setFunctional(Functional functional) {
		this.functional = functional;
	}
	public BPsetUpProperties getProperties() {
		return properties;
	}
	public void setProperties(BPsetUpProperties properties) {
		this.properties = properties;
	}
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("functional", functional)
				.append("properties", properties).toString();
	}
	
}
