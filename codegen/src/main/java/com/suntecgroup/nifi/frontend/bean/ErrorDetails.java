package com.suntecgroup.nifi.frontend.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "template")
public class ErrorDetails {

	private String statusCode;
	private String details;

	public ErrorDetails() {

	}

	public ErrorDetails(String statusCode, String details) {
		super();
		this.statusCode = statusCode;
		this.details = details;
	}

	@XmlElement
	public String getStatusCode() {
		return statusCode;
	}

	@XmlElement
	public String getDetails() {
		return details;
	}

}
