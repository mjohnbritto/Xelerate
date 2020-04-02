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
public class Variables
{
	private Variable variable;
	
	public Variable getVariable ()
	{
		return variable;
	}

	public void setVariable (Variable variable)
	{
		this.variable = variable;
	}
}

