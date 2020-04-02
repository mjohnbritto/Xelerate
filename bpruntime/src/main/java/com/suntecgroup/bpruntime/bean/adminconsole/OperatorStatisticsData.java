package com.suntecgroup.bpruntime.bean.adminconsole;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "bytesRead", "bytesWritten", "bytesTransferred", "bytesIn", 
	"flowFilesIn", "bytesOut", "flowFilesOut", "tasks", "totalTaskDuration",
	"flowFilesRemoved", "averageLineageDuration", "averageTaskDuration","timestamp"})
public class OperatorStatisticsData {
	
	private String bytesRead;
	private String bytesWritten;
	private String bytesTransferred;
	private String bytesIn;
	private String flowFilesIn;
	private String bytesOut;
	private String flowFilesOut;
	private String tasks;
	private String totalTaskDuration;
	private String flowFilesRemoved;
	private String averageLineageDuration;
	private String averageTaskDuration;
	private String timestamp;
	public String getBytesRead() {
		return bytesRead;
	}
	public void setBytesRead(String bytesRead) {
		this.bytesRead = bytesRead;
	}
	public String getBytesWritten() {
		return bytesWritten;
	}
	public void setBytesWritten(String bytesWritten) {
		this.bytesWritten = bytesWritten;
	}
	public String getBytesTransferred() {
		return bytesTransferred;
	}
	public void setBytesTransferred(String bytesTransferred) {
		this.bytesTransferred = bytesTransferred;
	}
	public String getBytesIn() {
		return bytesIn;
	}
	public void setBytesIn(String bytesIn) {
		this.bytesIn = bytesIn;
	}
	public String getFlowFilesIn() {
		return flowFilesIn;
	}
	public void setFlowFilesIn(String flowFilesIn) {
		this.flowFilesIn = flowFilesIn;
	}
	public String getBytesOut() {
		return bytesOut;
	}
	public void setBytesOut(String bytesOut) {
		this.bytesOut = bytesOut;
	}
	public String getFlowFilesOut() {
		return flowFilesOut;
	}
	public void setFlowFilesOut(String flowFilesOut) {
		this.flowFilesOut = flowFilesOut;
	}
	public String getTasks() {
		return tasks;
	}
	public void setTasks(String tasks) {
		this.tasks = tasks;
	}
	public String getTotalTaskDuration() {
		return totalTaskDuration;
	}
	public void setTotalTaskDuration(String totalTaskDuration) {
		this.totalTaskDuration = totalTaskDuration;
	}
	public String getFlowFilesRemoved() {
		return flowFilesRemoved;
	}
	public void setFlowFilesRemoved(String flowFilesRemoved) {
		this.flowFilesRemoved = flowFilesRemoved;
	}
	public String getAverageLineageDuration() {
		return averageLineageDuration;
	}
	public void setAverageLineageDuration(String averageLineageDuration) {
		this.averageLineageDuration = averageLineageDuration;
	}
	public String getAverageTaskDuration() {
		return averageTaskDuration;
	}
	public void setAverageTaskDuration(String averageTaskDuration) {
		this.averageTaskDuration = averageTaskDuration;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	

}
