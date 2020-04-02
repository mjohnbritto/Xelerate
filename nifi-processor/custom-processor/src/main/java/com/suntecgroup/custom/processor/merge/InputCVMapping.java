/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor.merge;

public class InputCVMapping {

	private String connectionName;
	private String inputBeType;
	private String contextVariable;
	private int fileCount;
	private String aliasName;
	private boolean compositeBE;
	private String key;
	private String fromOperatorKey;
	private boolean mandatoryCV;

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

	public boolean isMandatoryCV() {
		return mandatoryCV;
	}

	public void setMandatoryCV(boolean mandatoryCV) {
		this.mandatoryCV = mandatoryCV;
	}

}
