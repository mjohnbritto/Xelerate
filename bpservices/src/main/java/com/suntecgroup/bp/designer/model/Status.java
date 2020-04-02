package com.suntecgroup.bp.designer.model;

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
