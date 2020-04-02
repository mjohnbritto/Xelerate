package com.suntecgroup.nifi.template.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "connections")
@XmlType(propOrder = { "id", "parentGroupId", "backPressureDataSizeThreshold", "backPressureObjectThreshold",
		"destination", "flowFileExpiration", "labelIndex", "name", "selectedRelationships", "source", "zIndex" })

/*
 * <connections> <id>230fac09-a43d-3b55-0000-000000000000</id>
 * <parentGroupId>bb6835d8-7b81-3267-0000-000000000000</parentGroupId>
 * <backPressureDataSizeThreshold>1 GB</backPressureDataSizeThreshold>
 * <backPressureObjectThreshold>10000</backPressureObjectThreshold>
 * <destination> <groupId>bb6835d8-7b81-3267-0000-000000000000</groupId>
 * <id>02b77be0-17ce-3d49-0000-000000000000</id> <type>PROCESSOR</type>
 * </destination> <flowFileExpiration>0 sec</flowFileExpiration>
 * <labelIndex>1</labelIndex> <name></name>
 * <selectedRelationships>success</selectedRelationships> <source>
 * <groupId>bb6835d8-7b81-3267-0000-000000000000</groupId>
 * <id>9073532e-3803-33de-0000-000000000000</id> <type>PROCESSOR</type>
 * </source> <zIndex>0</zIndex> </connections>
 */

public class TemplateConnection {

	private String id;

	private String parentGroupId;

	private String backPressureObjectThreshold;

	private String backPressureDataSizeThreshold;

	private TemplateDestination destination;

	private String flowFileExpiration;

	private String labelIndex;

	private String zIndex;
//	private TemplatePosition bends;

	private List<String> selectedRelationships;

	private TemplateSource source;

	private String name;

	@XmlElement(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "parentGroupId")
	public String getParentGroupId() {
		return parentGroupId;
	}

	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	@XmlElement(name = "selectedRelationships")
	public List<String> getSelectedRelationships() {
		return selectedRelationships;
	}

	public void setSelectedRelationships(List<String> selectedRelationships) {
		this.selectedRelationships = selectedRelationships;
	}

	@XmlElement(name = "source")
	public TemplateSource getSource() {
		return source;
	}

	public void setSource(TemplateSource source) {
		this.source = source;
	}

	@XmlElement(name = "flowFileExpiration")
	public String getFlowFileExpiration() {
		return flowFileExpiration;
	}

	public void setFlowFileExpiration(String flowFileExpiration) {
		this.flowFileExpiration = flowFileExpiration;
	}

	@XmlElement(name = "labelIndex")
	public String getLabelIndex() {
		return labelIndex;
	}

	public void setLabelIndex(String labelIndex) {
		this.labelIndex = labelIndex;
	}

	@XmlElement(name = "backPressureObjectThreshold")
	public String getBackPressureObjectThreshold() {
		return backPressureObjectThreshold;
	}

	public void setBackPressureObjectThreshold(String backPressureObjectThreshold) {
		this.backPressureObjectThreshold = backPressureObjectThreshold;
	}

	@XmlElement(name = "backPressureDataSizeThreshold")
	public String getBackPressureDataSizeThreshold() {
		return backPressureDataSizeThreshold;
	}

	public void setBackPressureDataSizeThreshold(String backPressureDataSizeThreshold) {
		this.backPressureDataSizeThreshold = backPressureDataSizeThreshold;
	}

	@XmlElement(name = "destination")
	public TemplateDestination getDestination() {
		return destination;
	}

	public void setDestination(TemplateDestination destination) {
		this.destination = destination;
	}

	@XmlElement(name = "zIndex")
	public String getzIndex() {
		return zIndex;
	}

	public void setzIndex(String zIndex) {
		this.zIndex = zIndex;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

/*	public TemplatePosition getBends() {
		return bends;
	}

	public void setBends(TemplatePosition bends) {
		this.bends = bends;
	}
*/
}
