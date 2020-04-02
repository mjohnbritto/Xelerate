
package com.suntecgroup.bpruntime.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContextParameters {

    @JsonProperty("a_assetName")
    private String aAssetName;
    @JsonProperty("a_assetname")
    private String aAssetname;
    @JsonProperty("a_assettype")
    private String aAssettype;
    @JsonProperty("a_depart")
    private String aDepart;
    @JsonProperty("a_mod")
    private String aMod;
    @JsonProperty("a_release")
    private String aRelease;
    @JsonProperty("a_regularexpression")
    private String aRegularexpression;    
    @JsonProperty("a_assetDepartment")
    private String aAssetDepartment;    
    @JsonProperty("a_assetModule")
    private String aAssetModule;
    @JsonProperty("a_assetRelease")
    private String aAssetRelease;
    @JsonProperty("a_assetversion")
    private String aAssetversion;
    
    @JsonProperty("a_assetName")
    public String getAAssetName() {
        return aAssetName;
    }

    @JsonProperty("a_assetName")
    public void setAAssetName(String aAssetName) {
        this.aAssetName = aAssetName;
    }

    @JsonProperty("a_assettype")
    public String getAAssettype() {
        return aAssettype;
    }

    @JsonProperty("a_assettype")
    public void setAAssettype(String aAssettype) {
        this.aAssettype = aAssettype;
    }

    @JsonProperty("a_depart")
    public String getADepart() {
        return aDepart;
    }

    @JsonProperty("a_depart")
    public void setADepart(String aDepart) {
        this.aDepart = aDepart;
    }

    @JsonProperty("a_mod")
    public String getAMod() {
        return aMod;
    }

    @JsonProperty("a_mod")
    public void setAMod(String aMod) {
        this.aMod = aMod;
    }

    @JsonProperty("a_release")
    public String getARelease() {
        return aRelease;
    }

    @JsonProperty("a_release")
    public void setARelease(String aRelease) {
        this.aRelease = aRelease;
    }

    @JsonProperty("a_regularexpression")
    public String getARegularexpression() {
        return aRegularexpression;
    }

    @JsonProperty("a_regularexpression")
    public void setARegularexpression(String aRegularexpression) {
        this.aRegularexpression = aRegularexpression;
    }
    
    @JsonProperty("a_assetDepartment")
	public String getAAssetDepartment() {
		return aAssetDepartment;
	}

    @JsonProperty("a_assetDepartment")
	public void setAAssetDepartment(String aAssetDepartment) {
		this.aAssetDepartment = aAssetDepartment;
	}
    
    @JsonProperty("a_assetModule")
	public String getAAssetModule() {
		return aAssetModule;
	}
    
    @JsonProperty("a_assetModule")
	public void setAAssetModule(String aAssetModule) {
		this.aAssetModule = aAssetModule;
	}

    @JsonProperty("a_assetversion")
	public String getAAssetversion() {
		return aAssetversion;
	}
	
	@JsonProperty("a_assetversion")
	public void setAAssetversion(String aAssetversion) {
		this.aAssetversion = aAssetversion;
	}
	
	@JsonProperty("a_assetRelease")
	public String getAAssetRelease() {
		return aAssetRelease;
	}
	
	@JsonProperty("a_assetRelease")
	public void setAAssetRelease(String aAssetRelease) {
		this.aAssetRelease = aAssetRelease;
	}
	
	@JsonProperty("a_assetname")
	public String getAAssetname() {
		return aAssetname;
	}
	
	@JsonProperty("a_assetname")
	public void setAAssetname(String aAssetname) {
		this.aAssetname = aAssetname;
	}

}
