package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="entry")
@XmlType(propOrder={"key", "value"})
public class TemplateEntryDesc {
	
	private String key;
	
	private TemplateValue value;
	
	@XmlElement
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@XmlElement
	public TemplateValue getValue() {
		return value;
	}
	public void setValue(TemplateValue value) {
		this.value = value;
	}
	
	

}
