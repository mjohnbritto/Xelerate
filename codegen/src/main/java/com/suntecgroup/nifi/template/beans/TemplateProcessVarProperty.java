package com.suntecgroup.nifi.template.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class TemplateProcessVarProperty {

	private String name;

	private String description;

	private TemplateType type;
	
	private Value value;
	
	private TemplateFlags flag;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TemplateType getType() {
		return type;
	}

	public void setType(TemplateType type) {
		this.type = type;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public TemplateFlags getFlag() {
		return flag;
	}

	public void setFlag(TemplateFlags flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("description", description).append("value", value)
				.append("flags", flag).toString();
	}

}
