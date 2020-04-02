package com.suntecgroup.nifi.frontend.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.suntecgroup.nifi.template.beans.Template;

@XmlRootElement(name = "businessProcessResponse")
@XmlType(propOrder = { "statusCode", "statusDescription", "businessFlowData",
		"errorDetails", "template" })
public class BPFlowResponseXml {

	private String statusCode;
	private String statusDescription;
	private BPFlowRequest businessFlowData;
	private List<ErrorDetails> errorDetails;
	private Template template;

	@XmlElement
	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	@XmlElement
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@XmlElement
	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	@XmlElement
	public BPFlowRequest getBusinessFlowData() {
		return businessFlowData;
	}

	public void setBusinessFlowData(BPFlowRequest businessFlowData) {
		this.businessFlowData = businessFlowData;
	}

	@XmlElement
	public List<ErrorDetails> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetails> errorDetails) {
		this.errorDetails = errorDetails;
	}

}
