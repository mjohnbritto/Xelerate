package com.suntecgroup.nifi.frontend.bean.filechannelintegration;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "condition", "rules" })
public class FileNameValidation {

	@JsonProperty("condition")
	private String condition;
	@JsonProperty("rules")
	private List<Rule> rules = new ArrayList<Rule>();

	@JsonProperty("condition")
	public String getCondition() {
		return condition;
	}

	@JsonProperty("condition")
	public void setCondition(String condition) {
		this.condition = condition;
	}

	@JsonProperty("rules")
	public List<Rule> getRules() {
		return rules;
	}

	@JsonProperty("rules")
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

}