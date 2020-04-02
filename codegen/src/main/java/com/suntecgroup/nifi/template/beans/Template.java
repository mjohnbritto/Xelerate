package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "template")
//@XmlType(propOrder={"encoding-version","description", "groupId", "name", "snippet", "timestamp"})


/* venu - template trials */
public class Template {

	private String encodingVersion;

	private String timestamp;

	private String groupId;

	private String description;

	private String name;

	private TemplateSnippet snippet;

	@XmlElement
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@XmlElement
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@XmlElement
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public TemplateSnippet getSnippet() {
		return snippet;
	}

	public void setSnippet(TemplateSnippet snippet) {
		this.snippet = snippet;
	}

	@XmlAttribute(name = "encoding-version")
	public String getEncodingVersion() {
		return encodingVersion;
	}

	public void setEncodingVersion(String encodingVersion) {
		this.encodingVersion = encodingVersion;
	}
	
}
