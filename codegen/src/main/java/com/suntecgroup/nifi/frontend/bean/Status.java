package com.suntecgroup.nifi.frontend.bean;

/**
 * Enum class for Status information.
 *
 */
public enum Status {

	SUCCESS("100"), FAILURE("101");

	private String statusCode;

	private Status(String status) {
		this.statusCode = status;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

}
