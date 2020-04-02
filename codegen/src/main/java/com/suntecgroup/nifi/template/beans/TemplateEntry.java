package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="entry")
@XmlType(propOrder={"key", "value"})
public class TemplateEntry {

	private String value;

	private String key;

	@XmlElement
	public String getValue ()
	{
		return value;
	}

	public void setValue (String value)
	{
		this.value = value;
	}

	@XmlElement
	public String getKey ()
	{
		return key;
	}

	public void setKey (String key)
	{
		this.key = key;
	}

}
