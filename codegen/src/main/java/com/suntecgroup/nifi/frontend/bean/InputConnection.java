package com.suntecgroup.nifi.frontend.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InputConnection {

	@SerializedName("key")
	@Expose
	private String key;

	@SerializedName("connectionName")
	@Expose
	private String connectionName;

	@SerializedName("inputBeType")
	@Expose
	private String inputBeType;

	@SerializedName("contextVariable")
	@Expose
	private String contextVariable;
	
	@SerializedName("fromOperatorKey")
	@Expose
	private String fromOperatorKey;
	
	@SerializedName("aliasName")
	@Expose
	private String aliasName;
	
	@SerializedName("compositeBE")
	@Expose
	private boolean compositeBE;
	
	@SerializedName("mandatoryCV")
	@Expose
	private boolean mandatoryCV;
	
	private int fileCount;

	public InputConnection() {
		super();
		this.mandatoryCV = false;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getInputBeType() {
		return inputBeType;
	}

	public void setInputBeType(String inputBeType) {
		this.inputBeType = inputBeType;
	}

	public String getContextVariable() {
		return contextVariable;
	}

	public void setContextVariable(String contextVariable) {
		this.contextVariable = contextVariable;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getFromOperatorKey() {
		return fromOperatorKey;
	}

	public void setFromOperatorKey(String fromOperatorKey) {
		this.fromOperatorKey = fromOperatorKey;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public boolean isCompositeBE() {
		return compositeBE;
	}

	public void setCompositeBE(boolean compositeBE) {
		this.compositeBE = compositeBE;
	}

	public boolean isMandatoryCV() {
		return mandatoryCV;
	}

	public void setMandatoryCV(boolean mandatoryCV) {
		this.mandatoryCV = mandatoryCV;
	}

}
