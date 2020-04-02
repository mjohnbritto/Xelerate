
package com.suntecgroup.bpruntime.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.bpruntime.bean.adminconsole.DeployedTemplate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "context"
})
public class DeployedTemplateRequest {

    @JsonProperty("context")
    private Context context;
    
    @JsonProperty("deployedTemplate")
    private List<DeployedTemplate> deployedTemplate = null;

    @JsonProperty("context")
    public Context getContext() {
        return context;
    }

    @JsonProperty("context")
    public void setContext(Context context) {
        this.context = context;
    }
    
    @JsonProperty("deployedTemplate")
    public List<DeployedTemplate> getDeployedTemplate() {
        return deployedTemplate;
    }

    @JsonProperty("deployedTemplate")
    public void setDeployedTemplate(List<DeployedTemplate> deployedTemplate) {
        this.deployedTemplate = deployedTemplate;
    }
}
