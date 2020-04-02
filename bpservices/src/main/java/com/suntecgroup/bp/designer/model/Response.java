package com.suntecgroup.bp.designer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.bp.designer.exception.ErrorDetail;

@JsonPropertyOrder({ "statusCode", "statusMessage", "error", "data" })
@JsonInclude(Include.NON_NULL)
public class Response<T> {

	private String statusCode;
	private Status statusMessage;
	private ErrorDetail error;
	private T data;

	public Response(String statusCode, Status statusMessage, ErrorDetail error,
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

	public ErrorDetail getError() {
		return error;
	}

	public void setError(ErrorDetail error) {
		this.error = error;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}