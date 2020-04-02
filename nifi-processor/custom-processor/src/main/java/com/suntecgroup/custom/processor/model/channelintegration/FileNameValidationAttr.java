package com.suntecgroup.custom.processor.model.channelintegration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "condition", "rules" })
public class FileNameValidationAttr {

	@JsonProperty("condition")
    private String condition;

    @JsonProperty("rules")
    private List<RuleAttributes> rules;

    @JsonProperty("condition")
    public String getCondition() {
        return condition;
    }

    @JsonProperty("condition")
    public void setCondition(String condition) {
        this.condition = condition;
    }

    @JsonProperty("rules")
    public List<RuleAttributes> getRules() {
        return rules;
    }

    @JsonProperty("rules")
    public void setRules(List<RuleAttributes> rules) {
        this.rules = rules;
    }
	
	
}
