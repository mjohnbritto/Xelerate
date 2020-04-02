package com.suntecgroup.metaconfig.exception;

import java.util.Set;

import com.suntecgroup.metaconfig.model.Response;

public class ApiDocParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Response<Set<String>> errorResponse;
	
	public ApiDocParserException(final Response<Set<String>> errorResponse) {
		this.errorResponse = errorResponse;
	}

	public Response<Set<String>> getErrorResponse() {
		return errorResponse;
	}

}
