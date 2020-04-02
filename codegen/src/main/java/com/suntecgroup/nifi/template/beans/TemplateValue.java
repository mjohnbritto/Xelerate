package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "value")
@XmlType(propOrder = { "identifiesControllerService", "name" })
public class TemplateValue {

	private String name;

	private String identifiesControllerService;

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public String getIdentifiesControllerService() {
		return identifiesControllerService;
	}

	public void setIdentifiesControllerService(
			String identifiesControllerService) {
		this.identifiesControllerService = identifiesControllerService;
	}

}
