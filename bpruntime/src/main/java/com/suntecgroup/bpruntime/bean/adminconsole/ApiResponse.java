package com.suntecgroup.bpruntime.bean.adminconsole;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "statusCode", "statusMessage", "statusDescription", "responseDetails" })
public class ApiResponse<T> {
	
	private String statusCode;
	private Status statusMessage;
	private String statusDescription;
	private T responseDetails;
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public Status getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(Status statusMessage) {
		this.statusMessage = statusMessage;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	public T getResponseDetails() {
		return responseDetails;
	}
	public void setResponseDetails(T responseDetails) {
		this.responseDetails = responseDetails;
	}
	public ApiResponse(String statusCode, Status statusMessage, String statusDescription, T responseDetails) {
		super();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.statusDescription = statusDescription;
		this.responseDetails = responseDetails;
	}
	

}
