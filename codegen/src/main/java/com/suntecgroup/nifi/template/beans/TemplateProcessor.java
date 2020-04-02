package com.suntecgroup.nifi.template.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "processors")
@XmlType(propOrder = { "id", "parentGroupId", "position", "bundle", "config", "executionNodeRestricted", "name",
		"relationships",
		// "selectedRelationShip",
		"state", "style", "type" })

/*
 * <processors> <id>02b77be0-17ce-3d49-0000-000000000000</id>
 * <parentGroupId>bb6835d8-7b81-3267-0000-000000000000</parentGroupId>
 * <position> <x>-776.1453429782462</x> <y>1408.0037780120738</y> </position>
 * <bundle> <artifact>nar</artifact> <group>com.suntecgroup.nifi</group>
 * <version>1.0-SNAPSHOT</version> </bundle> <config> </config>
 * <executionNodeRestricted>false</executionNodeRestricted>
 * <name>EndProcessor</name> <relationships> <autoTerminate>true</autoTerminate>
 * <name>Failure</name> </relationships> <relationships>
 * <autoTerminate>false</autoTerminate> <name>success</name> </relationships>
 * <state>STOPPED</state> <style/>
 * <type>com.suntecgroup.custom.processor.EndProcessor</type> </processors>
 */

public class TemplateProcessor {

	private String id;

	private String parentGroupId;

	private TemplatePosition position;

	private String style;

	private String name;

	private String state;

	private TemplateBundle bundle;

	private TemplateConfig config;

	private boolean executionNodeRestricted;

	private String type;

	private List<TemplateRelationships> relationships;

	private List<String> selectedRelationShip;
	

	@XmlElement
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement
	public TemplatePosition getPosition() {
		return position;
	}

	public void setPosition(TemplatePosition position) {
		this.position = position;
	}

	@XmlElement
	public String getParentGroupId() {
		return parentGroupId;
	}

	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	@XmlElement
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@XmlElement
	public TemplateBundle getBundle() {
		return bundle;
	}

	public void setBundle(TemplateBundle bundle) {
		this.bundle = bundle;
	}

	@XmlElement
	public TemplateConfig getConfig() {
		return config;
	}

	public void setConfig(TemplateConfig config) {
		this.config = config;
	}

	@XmlElement
	public boolean isExecutionNodeRestricted() {
		return executionNodeRestricted;
	}

	public void setExecutionNodeRestricted(boolean executionNodeRestricted) {
		this.executionNodeRestricted = executionNodeRestricted;
	}

	@XmlElement
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement
	public List<TemplateRelationships> getRelationships() {
		return relationships;
	}

	public void setRelationshipsList(List<TemplateRelationships> relationshipsList) {
		this.relationships = relationshipsList;
	}

	@XmlTransient
	// @XmlElement
	public List<String> getSelectedRelationShip() {
		return selectedRelationShip;
	}

	public void setSelectedRelationShip(List<String> selectedRelationShip) {
		this.selectedRelationShip = selectedRelationShip;
	}

}
