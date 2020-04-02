package com.suntecgroup.nifi.template.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="properties")
public class TemplateProperties {

	private List<TemplateEntry> entryList;

	@XmlElement(name="entry")
	public List<TemplateEntry> getEntryList() {
		return entryList;
	}

	public void setEntryList(List<TemplateEntry> entryList) {
		this.entryList = entryList;
	}
}
