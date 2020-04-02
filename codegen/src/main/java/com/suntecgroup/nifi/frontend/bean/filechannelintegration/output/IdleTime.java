package com.suntecgroup.nifi.frontend.bean.filechannelintegration.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "count", "duration" })
public class IdleTime {

	@JsonProperty("count")
	private int count;
	@JsonProperty("duration")
	private String duration;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public IdleTime() {
	}

	/**
	 * 
	 * @param duration
	 * @param count
	 */
	public IdleTime(int count, String duration) {
		super();
		this.count = count;
		this.duration = duration;
	}

	@JsonProperty("count")
	public int getCount() {
		return count;
	}

	@JsonProperty("count")
	public void setCount(int count) {
		this.count = count;
	}

	@JsonProperty("duration")
	public String getDuration() {
		return duration;
	}

	@JsonProperty("duration")
	public void setDuration(String duration) {
		this.duration = duration;
	}

}