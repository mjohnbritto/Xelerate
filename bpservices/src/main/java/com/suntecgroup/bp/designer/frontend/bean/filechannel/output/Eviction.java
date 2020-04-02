package com.suntecgroup.bp.designer.frontend.bean.filechannel.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "timeBased", "recordCountBased", "eventBased", "idleTime" })
public class Eviction {

	@JsonProperty("timeBased")
	private TimeBased timeBased;
	@JsonProperty("recordCountBased")
	private int recordCountBased;
	@JsonProperty("eventBased")
	private String eventBased;
	@JsonProperty("idleTime")
	private IdleTime idleTime;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Eviction() {
	}

	/**
	 * 
	 * @param recordCountBased
	 * @param idleTime
	 * @param timeBased
	 * @param eventBased
	 */
	public Eviction(TimeBased timeBased, int recordCountBased, String eventBased, IdleTime idleTime) {
		super();
		this.timeBased = timeBased;
		this.recordCountBased = recordCountBased;
		this.eventBased = eventBased;
		this.idleTime = idleTime;
	}

	@JsonProperty("timeBased")
	public TimeBased getTimeBased() {
		return timeBased;
	}

	@JsonProperty("timeBased")
	public void setTimeBased(TimeBased timeBased) {
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
	public IdleTime getIdleTime() {
		return idleTime;
	}

	@JsonProperty("idleTime")
	public void setIdleTime(IdleTime idleTime) {
		this.idleTime = idleTime;
	}

}