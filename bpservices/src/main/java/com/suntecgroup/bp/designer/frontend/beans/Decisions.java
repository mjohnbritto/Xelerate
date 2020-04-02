package com.suntecgroup.bp.designer.frontend.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.suntecgroup.bp.designer.exception.BPException;

public class Decisions {

	@SerializedName("key")
	@Expose
	private String key;
	@SerializedName("decisionName")
	@Expose
	private String decisionName;
	@SerializedName("expression")
	@Expose
	private String expression;

	public String toJsonString(String input) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = mapper.writeValueAsString(input);
		} catch (Exception e) {
			throw new BPException("Exception occurred at Decisions .toJsonString method: ", e);
		}
		return jsonString;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDecisionName() {
		return decisionName;
	}

	public void setDecisionName(String decisionName) {
		this.decisionName = decisionName;
	}

	public String getExpression() {
		return toJsonString(expression);
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

}
