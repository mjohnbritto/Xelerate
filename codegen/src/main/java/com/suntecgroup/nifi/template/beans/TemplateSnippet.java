package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="snippet")
@XmlType(propOrder={"processGroups"})
public class TemplateSnippet {
	
	private TemplateProcessGroups processGroups;
	
	@XmlElement(name="processGroups")
	public TemplateProcessGroups getProcessGroups() {
		return processGroups;
	}

	public void setProcessGroups(TemplateProcessGroups processGroups) {
		this.processGroups = processGroups;
	}
    
    

}
