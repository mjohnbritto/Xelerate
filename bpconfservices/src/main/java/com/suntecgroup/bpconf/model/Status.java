/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */
package com.suntecgroup.bpconf.model;

/*
 * This is a enum for the status code used in api response
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
public enum Status {

	SUCCESS("200"), FAILURE("101");

	private String statusCode;

	private Status(String status) {
		this.statusCode = status;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

}
