package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "processGroups")
@XmlType(propOrder={"id", "parentGroupID", "position","comments","contents","name","variables"})
public class TemplateProcessGroups {

	private String id;

	private String parentGroupID;

	private TemplatePosition position;

	private String comments;
	private String name;
	private TemplateContents contents;		// ----sravya
	private TemplateVariables variables;

	@XmlElement(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "parentGroupID")
	public String getParentGroupID() {
		return parentGroupID;
	}

	public void setParentGroupID(String parentGroupID) {
		this.parentGroupID = parentGroupID;
	}

	@XmlElement(name = "position")
	public TemplatePosition getPosition() {
		return position;
	}

	public void setPosition(TemplatePosition position) {
		this.position = position;
	}

	@XmlElement(name = "comments")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@XmlElement(name = "contents")			//-------->sravya
	public TemplateContents getContents() {
		return contents;
	}

	public void setContents(TemplateContents contents) {
		this.contents = contents;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "variables")
	public TemplateVariables getVariables() {
		return variables;
	}

	public void setVariables(TemplateVariables variables) {
		this.variables = variables;
	}

}
