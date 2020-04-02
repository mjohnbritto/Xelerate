package com.suntecgroup.xbmc.service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.bp.designer.model.Be;


/**
 * API Model class to hold API information.
 *
 */
@JsonPropertyOrder({ "apiName", "inputBe", "outputBe", "contextParameters" })
public class Apis {
	private InputBe inputBe;
	private OutputBe outputBe;

	private List<ContextParameters> contextParameters;
	private String apiName;

	private int apiId;

	public InputBe getInputBe() {
		return inputBe;
	}

	public void setInputBe(InputBe inputBe) {
		this.inputBe = inputBe;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public OutputBe getOutputBe() {
		return outputBe;
	}

	public void setOutputBe(OutputBe outputBe) {
		this.outputBe = outputBe;
	}

	public List<ContextParameters> getContextParameters() {
		return contextParameters;
	}

	public void setContextParameters(List<ContextParameters> contextParameters) {
		this.contextParameters = contextParameters;
	}

	public int getApiId() {
		return apiId;
	}

	public void setApiId(int apiId) {
		this.apiId = apiId;
	}

	@Override
	public String toString() {
		return "ClassPojo [apiId = " + apiId + ", inputBe = " + inputBe + ", apiName = " + apiName
				+ ", contextParameters = " + contextParameters + ", outputBe = " + outputBe + "]";
	}
}
