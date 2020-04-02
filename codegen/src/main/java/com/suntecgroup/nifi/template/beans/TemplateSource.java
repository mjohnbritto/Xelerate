package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="source")
@XmlType(propOrder={"groupId", "id", "type"})
public class TemplateSource {

	private String id;

	private String groupId;

	private String type;

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
	public String getGroupId ()
	{
		return groupId;
	}

	public void setGroupId (String groupId)
	{
		this.groupId = groupId;
	}

	@XmlElement
	public String getType ()
	{
		return type;
	}

	public void setType (String type)
	{
		this.type = type;
	}


}
