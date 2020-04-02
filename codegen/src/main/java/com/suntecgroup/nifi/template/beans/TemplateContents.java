package com.suntecgroup.nifi.template.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement(name="processGroups")
//@XmlType(propOrder={"processorsList", "connectionsList"})

@XmlRootElement(name="contents")
//@XmlType(propOrder={"x", "y"})
public class TemplateContents {
	private List<TemplateFunnels> funnelList;
	private List<TemplateProcessor> processorsList;
	private List<TemplateConnection> connectionsList;

	@XmlElement(name="connections")
	public List<TemplateConnection> getConnectionsList() {
		return connectionsList;
	}

	public void setConnectionsList(List<TemplateConnection> connectionsList) {
		this.connectionsList = connectionsList;
	}

	@XmlElement(name="processors")
	public List<TemplateProcessor> getProcessorsList() {
		return processorsList;
	}

	public void setProcessorsList(List<TemplateProcessor> processorsList) {
		this.processorsList = processorsList;
	}
	@XmlElement(name="funnels")

	public List<TemplateFunnels> getFunnelList() {
		return funnelList;
	}

	public void setFunnelList(List<TemplateFunnels> funnelList) {
		this.funnelList = funnelList;
	}
	
}
