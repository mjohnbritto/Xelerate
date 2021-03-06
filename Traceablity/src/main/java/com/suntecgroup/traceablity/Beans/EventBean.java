package com.suntecgroup.traceablity.Beans;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventBean {
	@SerializedName("Id")
	@Expose
	private String Id;

	@SerializedName("transactionId")
	@Expose
	private String transactionId;
	@SerializedName("from")
	@Expose
	private String from;
	@SerializedName("path")
	@Expose
	private String path;

	@SerializedName("to")
	@Expose
	private String to;
	@SerializedName("sessionId")
	@Expose
	private String sessionId;
	@SerializedName("buk")
	@Expose
	private Buk[] buk;
	@SerializedName("runNumber")
	@Expose
	private String runNumber;

	@SerializedName("flowfileUUID")
	@Expose
	private String flowfileUUID;

	@SerializedName("payload")
	@Expose
	private String payload;
	@SerializedName("logType")
	@Expose
	private String logType;

	/**
	 * @return
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getfrom() {
		return from;
	}

	public String getto() {
		return to;
	}

	public Buk[] getBuk() {
		return buk;
	}

	public void setBuk(Buk[] buk) {
		this.buk = buk;
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

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	@Override
	public String toString() {
		return "ClassPojo [transactionId = " + transactionId + ", buk = " + buk + ", sessionId = " + sessionId
				+ ", runNumber = " + runNumber + ", flowfileUUID = " + flowfileUUID + ", payload = " + payload
				+ ", logType = " + logType + "]";
	}

	public String toJsonString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(this);
			return jsonString;
		} catch (Exception exception) {
			return "";
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
