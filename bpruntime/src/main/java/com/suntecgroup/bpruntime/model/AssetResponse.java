
package com.suntecgroup.bpruntime.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.bpruntime.bean.adminconsole.Asset;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "deployedTemplate",
    "errorList"
})
public class AssetResponse {

    @JsonProperty("deployedAsset")
    private List<Asset> deployedAsset = null;
    @JsonProperty("errorList")
    private List<ErrorList> errorList = null;
       

    @JsonProperty("errorList")
    public List<ErrorList> getErrorList() {
        return errorList;
    }

    @JsonProperty("errorList")
    public void setErrorList(List<ErrorList> errorList) {
        this.errorList = errorList;
    }
    
    @JsonProperty("deployedAsset")
	public List<Asset> getDeployedAsset() {
		return deployedAsset;
	}
    
    @JsonProperty("deployedAsset")
	public void setDeployedAsset(List<Asset> deployedAsset) {
		this.deployedAsset = deployedAsset;
	}
}
