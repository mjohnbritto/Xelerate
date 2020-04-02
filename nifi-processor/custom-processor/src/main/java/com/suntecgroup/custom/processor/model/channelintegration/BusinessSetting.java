package com.suntecgroup.custom.processor.model.channelintegration;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "outputBeType", "batchable", "batchSize", "eventLogging", "outputBEBUKAttributes" })
public class BusinessSetting {

	@JsonProperty("outputBeType")
	private String outputBeType;

	@JsonProperty("batchable")
	private boolean batchable;

	@JsonProperty("batchSize")
	private int batchSize;

	@JsonProperty("eventLogging")
	private boolean eventLogging;

	@JsonProperty("outputBEBUKAttributes")
	private List<String> outputBEBUKAttributes;

	@JsonProperty("outputBeType")
	public String getOutputBeType() {
		return outputBeType;
	}

	@JsonProperty("outputBeType")
	public void setOutputBeType(String outputBeType) {
		this.outputBeType = outputBeType;
	}

	@JsonProperty("batchable")
	public boolean isBatchable() {
		return batchable;
	}

	@JsonProperty("batchable")
	public void setBatchable(boolean batchable) {
		this.batchable = batchable;
	}

	@JsonProperty("batchSize")
	public int getBatchSize() {
		return batchSize;
	}

	@JsonProperty("batchSize")
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	@JsonProperty("eventLogging")
	public boolean isEventLogging() {
		return eventLogging;
	}

	@JsonProperty("eventLogging")
	public void setEventLogging(boolean eventLogging) {
		this.eventLogging = eventLogging;
	}

	@JsonProperty("outputBEBUKAttributes")
	public List<String> getOutputBEBUKAttributes() {
		return outputBEBUKAttributes;
	}

	@JsonProperty("outputBEBUKAttributes")
	public void setOutputBEBUKAttributes(List<String> outputBEBUKAttributes) {
		this.outputBEBUKAttributes = outputBEBUKAttributes;
	}

}