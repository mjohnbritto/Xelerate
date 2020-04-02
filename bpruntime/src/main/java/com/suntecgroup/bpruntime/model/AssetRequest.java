
package com.suntecgroup.bpruntime.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.bpruntime.bean.adminconsole.Asset;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "context"
})
public class AssetRequest {

    @JsonProperty("context")
    private Context context;
    
    @JsonProperty("deployedAsset")
    private List<Asset> deployedAsset = null;

    @JsonProperty("context")
    public Context getContext() {
        return context;
    }

    @JsonProperty("context")
    public void setContext(Context context) {
        this.context = context;
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
