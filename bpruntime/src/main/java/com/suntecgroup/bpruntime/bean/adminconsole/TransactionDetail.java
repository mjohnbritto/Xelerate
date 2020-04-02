
package com.suntecgroup.bpruntime.bean.adminconsole;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionDetail {

	@SerializedName("transactionId")
	@Expose
	private String transactionId;

	@SerializedName("sessionId")
	@Expose
	private String sessionId;

	@SerializedName("runNumber")
	@Expose
	private String runNumber;
	
	@SerializedName("fileName")
	@Expose
	private String fileName;
	
	@SerializedName("count")
	@Expose
	private String count;
	
	@SerializedName("duration")
	@Expose
	private String duration;
	
	@SerializedName("receivedFilesCount")
	@Expose
	private int receivedFilesCount;

	@SerializedName("acceptedFilesCount")
	@Expose
	private int acceptedFilesCount;
	
	@SerializedName("rejectedFilesCount")
	@Expose
	private int rejectedFilesCount;
	
	@SerializedName("totalRecordsCount")
	@Expose
	private int totalRecordsCount;
	
	@SerializedName("acceptedRecordsCount")
	@Expose
	private int acceptedRecordsCount;
	
	@SerializedName("rejectedRecordsCount")
	@Expose
	private int rejectedRecordsCount;

	@SerializedName("eventsCount")
	@Expose
	private String eventsCount;

	@SerializedName("source")
	@Expose
	private String source;

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

	@SerializedName("buk")
	@Expose
	private List<EventBuk> buk;

	@SerializedName("errorMessage")
	@Expose
	private List<String> errorMessage;

	@SerializedName("addressed")
	@Expose
	private boolean addressed;

	@SerializedName("action")
	@Expose
	private String action;

	@SerializedName("templateId")
	@Expose
	private String templateId;
	
	@SerializedName("totalRequestsCount")
	@Expose
	private int totalRequestsCount;
	
	@SerializedName("totalFailureRequestsCount")
	@Expose
	private int totalFailureRequestsCount;
	
	@SerializedName("totalSuccessRequestsCount")
	@Expose
	private int totalSuccessRequestsCount;
	
	@SerializedName("totalSuccessRecordsCount")
	@Expose
	private int totalSuccessRecordsCount;
	
	@SerializedName("totalFailureRecordsCount")
	@Expose
	private int totalFailureRecordsCount;
	
	@SerializedName("totalFilesWritten")
	@Expose
	private int totalFilesWritten;
	
	@SerializedName("totalRecordsReceived")
	@Expose
	private int totalRecordsReceived;
	
	@SerializedName("totalWrittenRecords")
	@Expose
	private int totalWrittenRecords;
	
	@SerializedName("totalUnwrittenRecords")
	@Expose
	private int totalUnwrittenRecords;
	
	private Date receivedTime;
	
	private Date lastModifiedTime;
	
		
	public int getTotalRequestsCount() {
		return totalRequestsCount;
	}

	public void setTotalRequestsCount(int totalRequestsCount) {
		this.totalRequestsCount = totalRequestsCount;
	}
	
	public int getTotalFailureRequestsCount() {
		return totalFailureRequestsCount;
	}

	public void setTotalFailureRequestsCount(int totalFailureRequestsCount) {
		this.totalFailureRequestsCount = totalFailureRequestsCount;
	}

	
	public int getTotalSuccessRequestsCount() {
		return totalSuccessRequestsCount;
	}

	public void setTotalSuccessRequestsCount(int totalSuccessRequestsCount) {
		this.totalSuccessRequestsCount = totalSuccessRequestsCount;
	}

	public int getTotalSuccessRecordsCount() {
		return totalSuccessRecordsCount;
	}

	public void setTotalSuccessRecordsCount(int totalSuccessRecordsCount) {
		this.totalSuccessRecordsCount = totalSuccessRecordsCount;
	}

	public int getTotalFailureRecordsCount() {
		return totalFailureRecordsCount;
	}

	public void setTotalFailureRecordsCount(int totalFailureRecordsCount) {
		this.totalFailureRecordsCount = totalFailureRecordsCount;
	}

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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getReceivedFilesCount() {
		return receivedFilesCount;
	}
	
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
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

	public String getEventsCount() {
		return eventsCount;
	}

	public void setEventsCount(String eventsCount) {
		this.eventsCount = eventsCount;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public List<EventBuk> getBuk() {
		return buk;
	}

	public void setBuk(List<EventBuk> buk) {
		this.buk = buk;
	}

	public List<String> getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(List<String> errorMessage) {
		this.errorMessage = errorMessage;
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

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public int getTotalFilesWritten() {
		return totalFilesWritten;
	}

	public void setTotalFilesWritten(int totalFilesWritten) {
		this.totalFilesWritten = totalFilesWritten;
	}

	public int getTotalRecordsReceived() {
		return totalRecordsReceived;
	}

	public void setTotalRecordsReceived(int totalRecordsReceived) {
		this.totalRecordsReceived = totalRecordsReceived;
	}

	public int getTotalWrittenRecords() {
		return totalWrittenRecords;
	}

	public void setTotalWrittenRecords(int totalWrittenRecords) {
		this.totalWrittenRecords = totalWrittenRecords;
	}

	public int getTotalUnwrittenRecords() {
		return totalUnwrittenRecords;
	}

	public void setTotalUnwrittenRecords(int totalUnwrittenRecords) {
		this.totalUnwrittenRecords = totalUnwrittenRecords;
	}

	/**
	 * @return the receivedTime
	 */
	public Date getReceivedTime() {
		return receivedTime;
	}

	/**
	 * @param receivedTime the receivedTime to set
	 */
	public void setReceivedTime(Date receivedTime) {
		this.receivedTime = receivedTime;
	}

	/**
	 * @return the lastModifiedTime
	 */
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * @param lastModifiedTime the lastModifiedTime to set
	 */
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
	
}
