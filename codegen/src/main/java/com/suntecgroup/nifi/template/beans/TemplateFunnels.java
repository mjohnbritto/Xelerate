package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="funnels")
@XmlType(propOrder={"id", "parentGroupId", "position"})
public class TemplateFunnels {
	
	private String id;
	
	private String name;

	private String parentGroupId;

	private TemplatePosition position;
	
	@XmlElement
	public String getId ()
	{
		return id;
	}

	public void setId (String id)
	{
		this.id = id;
	}

	@XmlElement
	public TemplatePosition getPosition ()
	{
		return position;
	}

	public void setPosition (TemplatePosition position)
	{
		this.position = position;
	}

	@XmlElement
	public String getParentGroupId ()
	{
		return parentGroupId;
	}

	public void setParentGroupId (String parentGroupId)
	{
		this.parentGroupId = parentGroupId;
	}

	@XmlTransient
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
