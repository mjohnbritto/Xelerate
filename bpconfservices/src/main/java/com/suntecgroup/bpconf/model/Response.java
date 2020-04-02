/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */
package com.suntecgroup.bpconf.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 * This class is a Model for response data sent by api
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@JsonPropertyOrder({ "statusCode", "statusMessage", "error", "data" })
@JsonInclude(Include.NON_NULL)
public class Response<T> {

	private String statusCode;
	private Status statusMessage;
	private ErrorDetail error;
	private T data;

	public Response(String statusCode, Status statusMessage, ErrorDetail error, T data) {
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