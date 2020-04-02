package com.suntecgroup.nifi.template.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "config")
@XmlType(propOrder = { "bulletinLevel", "comments",
		"concurrentlySchedulableTaskCount", "descriptors", "executionNode",
		"lossTolerant", "penaltyDuration", "properties", "runDurationMillis",
		"schedulingPeriod", "schedulingStrategy", "yieldDuration" })
public class TemplateConfig {
	// private List<String> Props;

	private String schedulingPeriod;

	private String concurrentlySchedulableTaskCount;

	private String executionNode;

	private String lossTolerant;

	private String bulletinLevel;

	private String penaltyDuration;

	private TemplateProperties properties;

	private TemplateDescriptors descriptors;

	private String comments;

	private String schedulingStrategy;

	private String yieldDuration;

	private String runDurationMillis;

	@XmlElement
	public String getSchedulingPeriod() {
		return schedulingPeriod;
	}

	public void setSchedulingPeriod(String schedulingPeriod) {
		this.schedulingPeriod = schedulingPeriod;
	}

	@XmlElement
	public String getConcurrentlySchedulableTaskCount() {
		return concurrentlySchedulableTaskCount;
	}

	public void setConcurrentlySchedulableTaskCount(
			String concurrentlySchedulableTaskCount) {
		this.concurrentlySchedulableTaskCount = concurrentlySchedulableTaskCount;
	}

	@XmlElement
	public String getExecutionNode() {
		return executionNode;
	}

	public void setExecutionNode(String executionNode) {
		this.executionNode = executionNode;
	}

	@XmlElement
	public String getLossTolerant() {
		return lossTolerant;
	}

	public void setLossTolerant(String lossTolerant) {
		this.lossTolerant = lossTolerant;
	}

	@XmlElement
	public String getBulletinLevel() {
		return bulletinLevel;
	}

	public void setBulletinLevel(String bulletinLevel) {
		this.bulletinLevel = bulletinLevel;
	}

	@XmlElement
	public String getPenaltyDuration() {
		return penaltyDuration;
	}

	public void setPenaltyDuration(String penaltyDuration) {
		this.penaltyDuration = penaltyDuration;
	}

	@XmlElement
	public TemplateProperties getProperties() {
		return properties;
	}

	public void setProperties(TemplateProperties properties) {
		this.properties = properties;
	}

	@XmlElement
	public TemplateDescriptors getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(TemplateDescriptors descriptors) {
		this.descriptors = descriptors;
	}

	@XmlElement
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@XmlElement
	public String getSchedulingStrategy() {
		return schedulingStrategy;
	}

	public void setSchedulingStrategy(String schedulingStrategy) {
		this.schedulingStrategy = schedulingStrategy;
	}

	@XmlElement
	public String getYieldDuration() {
		return yieldDuration;
	}

	public void setYieldDuration(String yieldDuration) {
		this.yieldDuration = yieldDuration;
	}

	@XmlElement
	public String getRunDurationMillis() {
		return runDurationMillis;
	}

	public void setRunDurationMillis(String runDurationMillis) {
		this.runDurationMillis = runDurationMillis;
	}
	//
	// public List<String> getProps() {
	// return Props;
	// }
	//
	// public void setProps(List<String> props) {
	// Props = props;
	// }

}
