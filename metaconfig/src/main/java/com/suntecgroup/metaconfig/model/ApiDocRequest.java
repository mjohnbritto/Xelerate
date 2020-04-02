package com.suntecgroup.metaconfig.model;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public class ApiDocRequest {
	private String apiDocUrl;
	@NotNull
	private String contentType;
	private String operation;
	private MultipartFile file;
	
	public String getApiDocUrl() {
		return apiDocUrl;
	}
	public void setApiDocUrl(String apiDocUrl) {
		this.apiDocUrl = apiDocUrl;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
}
