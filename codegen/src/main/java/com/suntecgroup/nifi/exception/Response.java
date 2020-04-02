package com.suntecgroup.nifi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.nifi.frontend.bean.ErrorDetails;

@JsonPropertyOrder({ "statusCode", "statusMessage", "error", "data" })
@JsonInclude(Include.NON_NULL)
public class Response<T> {

	private String statusCode;
	private Status statusMessage;
	private ErrorDetails error; 
	private T data;

	public Response(String statusCode, Status statusMessage, ErrorDetails error,
			T data) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.error = error;
		this.data = data;
	}

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

	public ErrorDetails getError() {
		return error;
	}

	public void setError(ErrorDetails error) {
		this.error = error;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}