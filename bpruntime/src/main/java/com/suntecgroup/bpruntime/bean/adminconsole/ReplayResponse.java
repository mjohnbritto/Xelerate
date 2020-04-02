package com.suntecgroup.bpruntime.bean.adminconsole;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="replayResponse")
@XmlType(propOrder={"statusCode", "statusDescription"})
public class ReplayResponse {

	private String statusCode;
	private String statusDescription;
	
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
	

}
