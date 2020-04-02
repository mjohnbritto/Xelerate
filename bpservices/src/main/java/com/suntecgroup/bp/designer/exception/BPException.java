package com.suntecgroup.bp.designer.exception;

public class BPException extends RuntimeException {

	private static final long serialVersionUID = 7718828512143293558L;

	private String message;

	public BPException(String message) {
		super();
		this.message = message;
	}

	public BPException(String message, Throwable cause) {
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
