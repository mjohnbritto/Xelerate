package com.suntecgroup.bp.designer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReviewReportResponse {
	private String key;
	private String name;
	private String type;
	private String status;
	private List<Fields> fields;
	
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Fields> getFields() {
		return fields;
	}
	public void setFields(List<Fields> fieldsList) {
		this.fields = fieldsList;
	}
	
	
}
