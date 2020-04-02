
package com.suntecgroup.nifi.frontend.bean.filechannelintegration.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "count", "duration" })
public class FileNameDuplication {

	@JsonProperty("count")
	private String count;
	@JsonProperty("duration")
	private String duration;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public FileNameDuplication() {
	}

	/**
	 * 
	 * @param duration
	 * @param count
	 */
	public FileNameDuplication(String count, String duration) {
		super();
		this.count = count;
		this.duration = duration;
	}

	@JsonProperty("count")
	public String getCount() {
		return count;
	}

	@JsonProperty("count")
	public void setCount(String count) {
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
