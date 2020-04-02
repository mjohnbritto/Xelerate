package com.suntecgroup.nifi.template.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="descriptors")
public class TemplateDescriptors {
	
	private List<TemplateEntryDesc> entryDescList;

	@XmlElement(name="entry")
	public List<TemplateEntryDesc> getEntryDescList() {
		return entryDescList;
	}

	public void setEntryDescList(List<TemplateEntryDesc> entryDescList) {
		this.entryDescList = entryDescList;
	}
	
	

}
