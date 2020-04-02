/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */
package com.suntecgroup.bpconf.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/*
 * This class is a Model for accepting the config details in api request
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "bpName",
    "configurations"
})
public class BpConfiguration {

    @JsonProperty("bpName")
    private String bpName;
    @JsonProperty("configurations")
    private List<Configuration> configurations = null;

    @JsonProperty("bpName")
    public String getBpName() {
        return bpName;
    }

    @JsonProperty("bpName")
    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    @JsonProperty("configurations")
    public List<Configuration> getConfigurations() {
        return configurations;
    }

    @JsonProperty("configurations")
    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = configurations;
    }

}
