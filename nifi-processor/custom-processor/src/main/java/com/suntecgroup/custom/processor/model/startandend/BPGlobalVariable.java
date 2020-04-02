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
public class BPGlobalVariable {
	
    private ProcessGroupRevision processGroupRevision;

    private VariableRegistry variableRegistry;

    public ProcessGroupRevision getProcessGroupRevision ()
    {
        return processGroupRevision;
    }

    public void setProcessGroupRevision (ProcessGroupRevision processGroupRevision)
    {
        this.processGroupRevision = processGroupRevision;
    }

    public VariableRegistry getVariableRegistry ()
    {
        return variableRegistry;
    }

    public void setVariableRegistry (VariableRegistry variableRegistry)
    {
        this.variableRegistry = variableRegistry;
    }   
}