/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.startandend;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * This is a model/pojo class to the request/response to data
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VariableRegistry {

	private String processGroupId;
	
	private Variables[] variables;

	public String getProcessGroupId() {
		return processGroupId;
	}

	public void setProcessGroupId(String processGroupId) {
		this.processGroupId = processGroupId;
	}

	public Variables[] getVariables() {
		return variables;
	}

	public void setVariables(Variables[] variables) {
		this.variables = variables;
	}
}
