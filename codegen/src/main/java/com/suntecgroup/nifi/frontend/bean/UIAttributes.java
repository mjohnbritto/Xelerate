package com.suntecgroup.nifi.frontend.bean;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UIAttributes {

	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("isBusinessFailure")
	@Expose
	private boolean isBusinessFailure;
	
	@SerializedName("key")
	@Expose
	private String key;
	@SerializedName("pattern")
	@Expose
	private String pattern;
	@SerializedName("toArrow")
	@Expose
	private String toArrow;
	@SerializedName("from")
	@Expose
	private String sourceName;

	@SerializedName("to")
	@Expose
	private String destinationName;

	@SerializedName("points")
	@Expose
	private List<Float> points;

	@SerializedName("decisionName")
	@Expose
	private String decisionName;
	
	private String connName;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getToArrow() {
		return toArrow;
	}

	public void setToArrow(String toArrow) {
		this.toArrow = toArrow;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public List<Float> getPoints() {
		return points;
	}

	public void setPoints(List<Float> points) {
		this.points = points;
	}

	public String getDecisionName() {
		return decisionName;
	}

	public void setDecisionName(String decisionName) {
		this.decisionName = decisionName;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public boolean isIsBusinessFailure() {
		return isBusinessFailure;
	}

	public void setIsisBusinessFailure(boolean isBusinessFailure) {
		this.isBusinessFailure = isBusinessFailure;
	}

}
