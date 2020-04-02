package com.suntecgroup.custom.processor.model.channelintegration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "operator", "customValue", "type", "selectedValue", "processVariable", "beAttribute" })
public class RuleAttributes {
	@JsonProperty("operator")
    private String operator;

    @JsonProperty("customValue")
    private String customValue;

    @JsonProperty("type")
    private String type;

    @JsonProperty("selectedValue")
    private String selectedValue;

    @JsonProperty("processVariable")
    private String processVariable;
    
    @JsonProperty("beAttribute")
    private String beAttribute;

    @JsonProperty("operator")
    public String getOperator() {
        return operator;
    }

    @JsonProperty("operator")
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @JsonProperty("customValue")
    public String getCustomValue() {
        return customValue;
    }

    @JsonProperty("customValue")
    public void setCustomValue(String customValue) {
        this.customValue = customValue;
    }
    
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("selectedValue")
    public String getSelectedValue() {
        return selectedValue;
    }

    @JsonProperty("selectedValue")
    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }

    @JsonProperty("processVariable")
    public String getProcessVariable() {
        return processVariable;
    }

    @JsonProperty("processVariable")
    public void setProcessVariable(String processVariable) {
        this.processVariable = processVariable;
    }

    @JsonProperty("beAttribute")
    public String getBeAttribute() {
        return beAttribute;
    }

    @JsonProperty("beAttribute")
    public void setBeAttribute(String beAttribute) {
        this.beAttribute = beAttribute;
    }
	
	

}
