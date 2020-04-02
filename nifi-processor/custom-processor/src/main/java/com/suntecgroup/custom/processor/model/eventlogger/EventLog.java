/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.eventlogger;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suntecgroup.custom.processor.model.buk.Buk;

/*
 * Model class for Event info
 * 
 * @version 1.0 - December 2018
 * @author Thatchana
 */

public class EventLog {

	@JsonProperty("transactionId")
	private String transactionId;

	@JsonProperty("sessionId")
	private String sessionId;

	@JsonProperty("runNumber")
	private String runNumber;

	@JsonProperty("flowfileUUID")
	private String flowfileUUID;

	@JsonProperty("clusterNodeId")
	private String clusterNodeId;

	@JsonProperty("logType")
	private String logType;

	@JsonProperty("buk")
	private List<Buk> buk = null;

	@JsonProperty("payload")
	private String payload;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(String runNumber) {
		this.runNumber = runNumber;
	}

	public String getFlowfileUUID() {
		return flowfileUUID;
	}

	public void setFlowfileUUID(String flowfileUUID) {
		this.flowfileUUID = flowfileUUID;
	}

	public String getClusterNodeId() {
		return clusterNodeId;
	}

	public void setClusterNodeId(String clusterNodeId) {
		this.clusterNodeId = clusterNodeId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public List<Buk> getBuk() {
		return buk;
	}

	public void setBuk(List<Buk> buk) {
		this.buk = buk;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String jsonString) {
		this.payload = jsonString;
	}
}
