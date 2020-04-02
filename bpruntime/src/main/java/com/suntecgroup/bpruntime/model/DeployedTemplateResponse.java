
package com.suntecgroup.bpruntime.model;

import java.util.List;
import com.suntecgroup.bpruntime.bean.adminconsole.DeployedTemplate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "deployedTemplate",
    "errorList"
})
public class DeployedTemplateResponse {

    @JsonProperty("deployedTemplate")
    private List<DeployedTemplate> deployedTemplate = null;
    @JsonProperty("errorList")
    private List<ErrorList> errorList = null;
    
    @JsonProperty("deployedTemplate")
    public List<DeployedTemplate> getDeployedTemplate() {
        return deployedTemplate;
    }

    @JsonProperty("deployedTemplate")
    public void setDeployedTemplate(List<DeployedTemplate> deployedTemplate) {
        this.deployedTemplate = deployedTemplate;
    }

    @JsonProperty("errorList")
    public List<ErrorList> getErrorList() {
        return errorList;
    }

    @JsonProperty("errorList")
    public void setErrorList(List<ErrorList> errorList) {
        this.errorList = errorList;
    }
}
