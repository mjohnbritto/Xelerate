
package com.suntecgroup.bp.designer.frontend.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BusinessSettings {

	public BusinessSettings() {
		businessServiceName = "none";
		apiName = "none";
		inputBeType = "none";
		outputBeType = "none";
		bufferSize = "";
		overrideWithInput = false;
		serviceContextParameters = null;
		apiContextParameters = null;
		eventLogging = false;
		overRideWithOutput = false;
		inputBe = null;
		outputBe = null;
		exclusive = false;
		inputBEBUKAttributes = null;
		outputBEBUKAttributes = null;
		batchable = false;
		batchSize = "";
		businessErrorCodes = null;
		inputConnections = null;
		expectedInputChannel = 0;
		selectedInputOption = null;
	}

	@SerializedName("businessServiceName")
	@Expose
	private String businessServiceName;

	@SerializedName("exclusive")
	@Expose
	private boolean exclusive;

	@SerializedName("apiName")
	@Expose
	private String apiName;
	@SerializedName("inputBeType")
	@Expose
	private String inputBeType;
	@SerializedName("outputBeType")
	@Expose
	private String outputBeType;

	@SerializedName("inputBe")
	@Expose
	private BusinessEntity inputBe;
	@SerializedName("outputBe")
	@Expose
	private BusinessEntity outputBe;

	@SerializedName("bufferSize")
	@Expose
	private String bufferSize;
	@SerializedName("overrideWithInput")
	@Expose
	private boolean overrideWithInput;
	@SerializedName("serviceContextVariables")
	@Expose
	private List<ContextParameters> serviceContextParameters;
	@SerializedName("apiContextVariables")
	@Expose
	private List<ContextParameters> apiContextParameters;
	@SerializedName("eventLogging")
	@Expose
	private boolean eventLogging;

	@SerializedName("overRideWithOutput")
	@Expose
	private boolean overRideWithOutput;

	@SerializedName("decisions")
	@Expose
	private List<Decisions> decisions;
	@SerializedName("httpMethod")
	@Expose
	private String httpMethod;
	@SerializedName("remoteURL")
	@Expose
	private String remoteURL;

	@SerializedName("inputBEBUKAttributes")
	@Expose
	private List<String> inputBEBUKAttributes;

	@SerializedName("outputBEBUKAttributes")
	@Expose
	private List<String> outputBEBUKAttributes;

	@SerializedName("batchable")
	@Expose
	private boolean batchable;

	@SerializedName("batchSize")
	@Expose
	private String batchSize;

	@SerializedName("businessFailureFlowExist")
	@Expose
	private boolean businessFailureFlowExist;

	@SerializedName("businessErrorCodes")
	@Expose
	private List<String> businessErrorCodes;

	@SerializedName("contentType")
	@Expose
	private String contentType;

	@SerializedName("inputConnections")
	@Expose
	private List<InputConnection> inputConnections;
	@SerializedName("expectedInputChannel")
	@Expose
	private Integer expectedInputChannel;

	@SerializedName("selectedInputOption")
	@Expose
	private String selectedInputOption;
	
	@SerializedName("APIInput")
	private String APIInput;
	@SerializedName("api")
	private String api;
	
	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getAPIInput() {
		return APIInput;
	}

	public void setAPIInput(String aPIInput) {
		APIInput = aPIInput;
	}

	public String getSelectedInputOption() {
		return selectedInputOption;
	}

	public void setSelectedInputOption(String selectedInputOption) {
		this.selectedInputOption = selectedInputOption;
	}

	public Integer getExpectedInputChannel() {
		return expectedInputChannel;
	}

	public void setExpectedInputChannel(Integer expectedInputChannel) {
		this.expectedInputChannel = expectedInputChannel;
	}

	public List<InputConnection> getInputConnections() {
		return inputConnections;
	}

	public void setInputConnections(List<InputConnection> inputConnections) {
		this.inputConnections = inputConnections;
	}


	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isBusinessFailureFlowExist() {
		return businessFailureFlowExist;
	}

	public void setBusinessFailureFlowExist(boolean businessFailureFlowExist) {
		this.businessFailureFlowExist = businessFailureFlowExist;
	}

	public List<String> getBusinessErrorCodes() {
		return businessErrorCodes;
	}

	public void setBusinessErrorCodes(List<String> businessErrorCodes) {
		this.businessErrorCodes = businessErrorCodes;
	}

	public String getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;
	}

	public boolean isBatchable() {
		return batchable;
	}

	public void setBatchable(boolean batchable) {
		this.batchable = batchable;
	}

	public String getBusinessServiceName() {
		return businessServiceName;
	}

	public void setBusinessServiceName(String businessServiceName) {
		if (businessServiceName != null) {
			this.businessServiceName = businessServiceName;
		}
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		if (apiName != null) {

			this.apiName = apiName;
		}
	}

	public void setInputBeType(String inputBeType) {
		if (inputBeType != null) {

			this.inputBeType = inputBeType;
		}
	}

	public String getOutputBeType() {
		return outputBeType;
	}

	public void setOutputBeType(String outputBeType) {
		if (outputBeType != null) {

			this.outputBeType = outputBeType;
		}
	}

	public String getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(String bufferSize) {
		if (bufferSize != null) {
			this.bufferSize = bufferSize;
		}
	}

	public boolean isOverrideWithInput() {
		return overrideWithInput;
	}

	public void setOverrideWithInput(boolean overrideWithInput) {
		this.overrideWithInput = overrideWithInput;
	}

	public List<ContextParameters> getServiceContextParameters() {
		return serviceContextParameters;
	}

	public void setServiceContextParameters(List<ContextParameters> serviceContextParameters) {
		if (serviceContextParameters != null) {

			this.serviceContextParameters = serviceContextParameters;
		}
	}

	public List<ContextParameters> getApiContextParameters() {
		return apiContextParameters;
	}

	public void setApiContextParameters(List<ContextParameters> apiContextParameters) {
		if (apiContextParameters != null) {
			this.apiContextParameters = apiContextParameters;
		}
	}

	public boolean isEventLogging() {
		return eventLogging;
	}

	public void setEventLogging(boolean eventLogging) {
		this.eventLogging = eventLogging;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("businessServiceName", businessServiceName).append("apiName", apiName)
				.append("inputBeName", inputBeType).append("outputBeName", outputBeType)
				.append("bufferSize", bufferSize).append("overrideWithInput", overrideWithInput)
				.append("globalContextVariables", serviceContextParameters)
				.append("apiContextParameters", apiContextParameters).toString();
	}

	public boolean isOverRideWithOutput() {
		return overRideWithOutput;
	}

	public void setOverRideWithOutput(boolean overRideWithOutput) {
		this.overRideWithOutput = overRideWithOutput;
	}

	public List<Decisions> getDecisions() {
		return decisions;
	}

	public void setDecisions(List<Decisions> decisions) {
		this.decisions = decisions;
	}

	public String getInputBeType() {
		return inputBeType;
	}

	public BusinessEntity getInputBe() {
		return inputBe;
	}

	public void setInputBe(BusinessEntity inputBe) {
		this.inputBe = inputBe;
	}

	public BusinessEntity getOutputBe() {
		return outputBe;
	}

	public void setOutputBe(BusinessEntity outputBe) {
		this.outputBe = outputBe;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getRemoteURL() {
		return remoteURL;
	}

	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

	public List<String> getInputBEBUKAttributes() {
		return inputBEBUKAttributes;
	}

	public void setInputBEBUKAttributes(List<String> inputBEBUKAttributes) {
		this.inputBEBUKAttributes = inputBEBUKAttributes;
	}

	public List<String> getOutputBEBUKAttributes() {
		return outputBEBUKAttributes;
	}

	public void setOutputBEBUKAttributes(List<String> outputBEBUKAttributes) {
		this.outputBEBUKAttributes = outputBEBUKAttributes;
	}

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

}
