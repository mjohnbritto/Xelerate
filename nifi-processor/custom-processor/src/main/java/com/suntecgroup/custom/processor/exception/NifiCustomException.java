/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.exception;

public class NifiCustomException extends Exception {

	private static final long serialVersionUID = 7718828512143293558L;

	private String message;

	public NifiCustomException(String message) {
		super();
		this.message = message;
	}

	public NifiCustomException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}