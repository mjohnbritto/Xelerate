package com.suntecgroup.bpruntime.bean.adminconsole;

import java.util.List;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FailedTransaction {

	private String id;

	@SerializedName("sessionId")
	@Expose
	private String sessionId;

	@SerializedName("runNumber")
	@Expose
	private String runNumber;

	@SerializedName("transactionId")
	@Expose
	private String transactionId;

	@SerializedName("flowfileUUID")
	@Expose
	private String flowfileUUID;

	@SerializedName("operatorName")
	@Expose
	private String operatorName;

	@SerializedName("beName")
	@Expose
	private String beName;

	@SerializedName("errorType")
	@Expose
	private String errorType;

	/*@SerializedName("buk")
	@Expose
	private List<EventBuk> buk;*/
	
	@SerializedName("eventsCount")
	@Expose
	private String eventsCount;

	@SerializedName("errorMessage")
	@Expose
	private List<String> errorMessage;
	
	@SerializedName("normalisedErrorMessage")
	@Expose
	private String normalisedErrorMessage;


	@SerializedName("addressed")
	@Expose
	private boolean addressed;

	@SerializedName("action")
	@Expose
	private String action;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getFlowfileUUID() {
		return flowfileUUID;
	}

	public void setFlowfileUUID(String flowfileUUID) {
		this.flowfileUUID = flowfileUUID;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getBeName() {
		return beName;
	}

	public void setBeName(String beName) {
		this.beName = beName;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/*public List<EventBuk> getBuk() {
		return buk;
	}

	public void setBuk(List<EventBuk> buk) {
		this.buk = buk;
	}*/

	public String getEventsCount() {
		return eventsCount;
	}

	public void setEventsCount(String eventsCount) {
		this.eventsCount = eventsCount;
	}

	public List<String> getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(List<String> errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getNormalisedErrorMessage() {
		return normalisedErrorMessage;
	}

	public void setNormalisedErrorMessage(String normalisedErrorMessage) {
		this.normalisedErrorMessage = normalisedErrorMessage;
	}

	public boolean isAddressed() {
		return addressed;
	}

	public void setAddressed(boolean addressed) {
		this.addressed = addressed;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
