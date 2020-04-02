
package com.suntecgroup.bpruntime.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "context-parameters"
})
public class Context {

    @JsonProperty("context-parameters")
    private ContextParameters contextParameters;
    
    @JsonProperty("context-parameters")
    public ContextParameters getContextParameters() {
        return contextParameters;
    }

    @JsonProperty("context-parameters")
    public void setContextParameters(ContextParameters contextParameters) {
        this.contextParameters = contextParameters;
    }

}
