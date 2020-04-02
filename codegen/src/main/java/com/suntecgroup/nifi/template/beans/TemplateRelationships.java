package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="relationships")
@XmlType(propOrder={"autoTerminate", "name"})
public class TemplateRelationships {
	
	private String autoTerminate;

	private String name;
	
	public TemplateRelationships(String name, String autoTerminate) {
		super();
		this.name = name;
		this.autoTerminate = autoTerminate;
	}

	public TemplateRelationships() {
	}

	@XmlElement
	public String getAutoTerminate ()
	{
		return autoTerminate;
	}

	public void setAutoTerminate (String autoTerminate)
	{
		this.autoTerminate = autoTerminate;
	}

	@XmlElement
	public String getName ()
	{
		return name;
	}

	public void setName (String name)
	{
		this.name = name;
	}
}
