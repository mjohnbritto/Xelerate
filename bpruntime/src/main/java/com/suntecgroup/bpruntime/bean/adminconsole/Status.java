package com.suntecgroup.bpruntime.bean.adminconsole;

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
