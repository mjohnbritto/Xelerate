package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="position")
@XmlType(propOrder={"x", "y"})
public class TemplatePosition {

	
	private String x;
	
	private String y;

	@XmlElement
	public String getY ()
	{
		return y;
	}

	public void setY (String y)
	{
		this.y = y;
	}

	@XmlElement
	public String getX ()
	{
		return x;
	}

	public void setX (String x)
	{
		this.x = x;
	}


}
