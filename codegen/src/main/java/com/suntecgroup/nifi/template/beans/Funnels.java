package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;

public class Funnels {
	private String id;

	private String parentGroupId;

	private TemplatePosition position;
	@XmlElement
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement
	public TemplatePosition getPosition() {
		return position;
	}

	public void setPosition(TemplatePosition position) {
		this.position = position;
	}

	@XmlElement
	public String getParentGroupId() {
		return parentGroupId;
	}

	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

}
