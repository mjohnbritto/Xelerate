package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "timeBased", "recordCountBased", "eventBased", "idleTime" })
public class Eviction {

	@JsonProperty("timeBased")
	private Map<String, Object> timeBased;

	@JsonProperty("recordCountBased")
	private int recordCountBased;

	@JsonProperty("eventBased")
	private String eventBased;

	@JsonProperty("idleTime")
	private Map<String, Object> idleTime;

	@JsonProperty("timeBased")
	public Map<String, Object> getTimeBased() {
		return timeBased;
	}

	@JsonProperty("timeBased")
	public void setTimeBased(Map<String, Object> timeBased) {
		this.timeBased = timeBased;
	}

	@JsonProperty("recordCountBased")
	public int getRecordCountBased() {
		return recordCountBased;
	}

	@JsonProperty("recordCountBased")
	public void setRecordCountBased(int recordCountBased) {
		this.recordCountBased = recordCountBased;
	}

	@JsonProperty("eventBased")
	public String getEventBased() {
		return eventBased;
	}

	@JsonProperty("eventBased")
	public void setEventBased(String eventBased) {
		this.eventBased = eventBased;
	}

	@JsonProperty("idleTime")
	public Map<String, Object> getIdleTime() {
		return idleTime;
	}

	@JsonProperty("idleTime")
	public void setIdleTime(Map<String, Object> idleTime) {
		this.idleTime = idleTime;
	}

}