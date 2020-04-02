package com.suntecgroup.bpruntime.bean.adminconsole;


public class SessionDetails {

	private CompositeKey compositeKey;
	private String sessionId;
	private String runNumber;
	private String bpName;
	private String status;
	private String startTime;
	private String endTime;
	private String lastUpdatedTime;
	private String sessionProcessGroupId;
	private String sessionTemplateId;
	private String inactiveSince;
	private Boolean isSessionInUse;
	private int receivedFilesCount;
	private int acceptedFilesCount;
	private int rejectedFilesCount;
	private int totalRecordsCount;
	private int acceptedRecordsCount;
	private int rejectedRecordsCount;
	private int transactionTotalCount;
	private int transactionSuccessCount;
	private int transactionTechnicalFailureCount;
	private int transactionBusinessFailureCount;
	private int transactionAddressedFailureCount;
	private int transactionUnAddressedFailureCount;
	private int eventTotalCount;
	private int eventSuccessCount;
	private int eventTechnicalFailureCount;
	private int eventBusinessFailureCount;
	private int eventAddressedFailureCount;
	private int eventUnAddressedFailureCount;
	private String runType;
	private String dependentRunNumber;
	private boolean isTechnicalFailureReplayed;

	public CompositeKey getCompositeKey() {
		return compositeKey;
	}

	public void setCompositeKey(CompositeKey compositeKey) {
		this.compositeKey = compositeKey;
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

	public String getBpName() {
		return bpName;
	}

	public void setBpName(String bpName) {
		this.bpName = bpName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(String lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public String getSessionProcessGroupId() {
		return sessionProcessGroupId;
	}

	public void setSessionProcessGroupId(String sessionProcessGroupId) {
		this.sessionProcessGroupId = sessionProcessGroupId;
	}

	public String getSessionTemplateId() {
		return sessionTemplateId;
	}

	public void setSessionTemplateId(String sessionTemplateId) {
		this.sessionTemplateId = sessionTemplateId;
	}

	public String getInactiveSince() {
		return inactiveSince;
	}

	public void setInactiveSince(String inactiveSince) {
		this.inactiveSince = inactiveSince;
	}

	public Boolean getIsSessionInUse() {
		return isSessionInUse;
	}

	public void setIsSessionInUse(Boolean isSessionInUse) {
		this.isSessionInUse = isSessionInUse;
	}

	public int getTransactionTotalCount() {
		return transactionTotalCount;
	}

	public void setTransactionTotalCount(int transactionTotalCount) {
		this.transactionTotalCount = transactionTotalCount;
	}

	public int getTransactionSuccessCount() {
		return transactionSuccessCount;
	}

	public void setTransactionSuccessCount(int transactionSuccessCount) {
		this.transactionSuccessCount = transactionSuccessCount;
	}

	public int getTransactionTechnicalFailureCount() {
		return transactionTechnicalFailureCount;
	}

	public void setTransactionTechnicalFailureCount(int transactionTechnicalFailureCount) {
		this.transactionTechnicalFailureCount = transactionTechnicalFailureCount;
	}

	public int getTransactionBusinessFailureCount() {
		return transactionBusinessFailureCount;
	}

	public void setTransactionBusinessFailureCount(int transactionBusinessFailureCount) {
		this.transactionBusinessFailureCount = transactionBusinessFailureCount;
	}

	public int getTransactionAddressedFailureCount() {
		return transactionAddressedFailureCount;
	}

	public void setTransactionAddressedFailureCount(int transactionAddressedFailureCount) {
		this.transactionAddressedFailureCount = transactionAddressedFailureCount;
	}

	public int getEventTotalCount() {
		return eventTotalCount;
	}

	public int getTransactionUnAddressedFailureCount() {
		return transactionUnAddressedFailureCount;
	}

	public void setTransactionUnAddressedFailureCount(int transactionUnAddressedFailureCount) {
		this.transactionUnAddressedFailureCount = transactionUnAddressedFailureCount;
	}

	public void setEventTotalCount(int eventTotalCount) {
		this.eventTotalCount = eventTotalCount;
	}

	public int getEventSuccessCount() {
		return eventSuccessCount;
	}

	public void setEventSuccessCount(int eventSuccessCount) {
		this.eventSuccessCount = eventSuccessCount;
	}

	public int getEventTechnicalFailureCount() {
		return eventTechnicalFailureCount;
	}

	public void setEventTechnicalFailureCount(int eventTechnicalFailureCount) {
		this.eventTechnicalFailureCount = eventTechnicalFailureCount;
	}

	public int getEventBusinessFailureCount() {
		return eventBusinessFailureCount;
	}

	public void setEventBusinessFailureCount(int eventBusinessFailureCount) {
		this.eventBusinessFailureCount = eventBusinessFailureCount;
	}

	public int getEventAddressedFailureCount() {
		return eventAddressedFailureCount;
	}

	public void setEventAddressedFailureCount(int eventAddressedFailureCount) {
		this.eventAddressedFailureCount = eventAddressedFailureCount;
	}

	public int getEventUnAddressedFailureCount() {
		return eventUnAddressedFailureCount;
	}

	public void setEventUnAddressedFailureCount(int eventUnAddressedFailureCount) {
		this.eventUnAddressedFailureCount = eventUnAddressedFailureCount;
	}

	public String getRunType() {
		return runType;
	}

	public void setRunType(String runType) {
		this.runType = runType;
	}

	public String getDependentRunNumber() {
		return dependentRunNumber;
	}

	public void setDependentRunNumber(String dependentRunNumber) {
		this.dependentRunNumber = dependentRunNumber;
	}

	public boolean getIsTechnicalFailureReplayed() {
		return isTechnicalFailureReplayed;
	}

	public void setIsTechnicalFailureReplayed(boolean isTechnicalFailureReplayed) {
		this.isTechnicalFailureReplayed = isTechnicalFailureReplayed;
	}
	//ChannelIntegration 

	public int getReceivedFilesCount() {
		return receivedFilesCount;
	}

	public void setReceivedFilesCount(int receivedFilesCount) {
		this.receivedFilesCount = receivedFilesCount;
	}

	public int getAcceptedFilesCount() {
		return acceptedFilesCount;
	}

	public void setAcceptedFilesCount(int acceptedFilesCount) {
		this.acceptedFilesCount = acceptedFilesCount;
	}

	public int getRejectedFilesCount() {
		return rejectedFilesCount;
	}

	public void setRejectedFilesCount(int rejectedFilesCount) {
		this.rejectedFilesCount = rejectedFilesCount;
	}

	public int getTotalRecordsCount() {
		return totalRecordsCount;
	}

	public void setTotalRecordsCount(int totalRecordsCount) {
		this.totalRecordsCount = totalRecordsCount;
	}

	public int getAcceptedRecordsCount() {
		return acceptedRecordsCount;
	}

	public void setAcceptedRecordsCount(int acceptedRecordsCount) {
		this.acceptedRecordsCount = acceptedRecordsCount;
	}

	public int getRejectedRecordsCount() {
		return rejectedRecordsCount;
	}

	public void setRejectedRecordsCount(int rejectedRecordsCount) {
		this.rejectedRecordsCount = rejectedRecordsCount;
	}
	

}
