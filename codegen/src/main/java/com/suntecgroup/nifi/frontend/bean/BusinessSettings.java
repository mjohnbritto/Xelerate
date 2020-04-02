
package com.suntecgroup.nifi.frontend.bean;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessSettings {

	@SerializedName("businessServiceName")
	@Expose
	private String businessServiceName;

	@SerializedName("businessFailureFlowExist")
	@Expose
	private boolean businessFailureFlowExist;

	@SerializedName("businessErrorCodes")
	@Expose
	private List<String> businessErrorCodes;

	// IBS External Properties

	@SerializedName("inputParametersMapping")
	@Expose
	private String inputParametersMapping;

	@SerializedName("headers")
	@Expose
	private String headers;
	@SerializedName("security")
	@Expose
	private String security;

	@SerializedName("outputParametersMapping")
	@Expose
	private String outputParametersMapping;

	@SerializedName("pvParametersMapping")
	@Expose
	private String pvParametersMapping;

	// Merge Operator Properties

	@SerializedName("expectedInputChannel")
	@Expose
	private List<String> expectedInputChannel;

	@SerializedName("securedAPI")
	@Expose
	private boolean securedAPI;

	@SerializedName("APIInput")
	private String APIInput;
	
	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	//
	@SerializedName("pageSize")
	@Expose
	private int pageSize;

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

	@SerializedName("contentType")
	@Expose
	private String contentType;

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

	@SerializedName("inputConnections")
	@Expose
	private List<InputConnection> inputConnections;

	@SerializedName("requestPayload")
	@Expose
	private String requestPayload;

	@SerializedName("supportStreaming")
	@Expose
	private boolean supportStreaming;
	
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
		expectedInputChannel = null;
		securedAPI = false;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getRequestPayload() {
		return requestPayload;
	}

	public void setRequestPayload(String requestPayload) {
		this.requestPayload = requestPayload;
	}

	public List<String> getBusinessErrorCodes() {
		return businessErrorCodes;
	}

	public void setBusinessErrorCodes(List<String> businessErrorCodes) {
		this.businessErrorCodes = businessErrorCodes;
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

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isBatchable() {
		return batchable;
	}

	public void setBatchable(boolean batchable) {
		this.batchable = batchable;
	}

	public String getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;

	}

	public boolean isBusinessFailureFlowExist() {
		return businessFailureFlowExist;
	}

	public void setBusinessFailureFlowExist(boolean businessFailureFlowExist) {
		this.businessFailureFlowExist = businessFailureFlowExist;
	}

	public List<InputConnection> getInputConnections() {
		return inputConnections;
	}

	public void setInputConnections(List<InputConnection> inputConnections) {
		this.inputConnections = inputConnections;
	}

	public String getInputParametersMapping() {
		return inputParametersMapping;
	}

	public void setInputParametersMapping(String inputParametersMapping) {
		this.inputParametersMapping = inputParametersMapping;
	}

	public String getOutputParametersMapping() {
		return outputParametersMapping;
	}

	public void setOutputParametersMapping(String outputParametersMapping) {
		this.outputParametersMapping = outputParametersMapping;
	}

	public String getPvParametersMapping() {
		return pvParametersMapping;
	}

	public void setPvParametersMapping(String pvParametersMapping) {
		this.pvParametersMapping = pvParametersMapping;
	}

	public List<String> getExpectedInputChannel() {
		return expectedInputChannel;
	}

	public void setExpectedInputChannel(List<String> expectedInputChannel) {
		this.expectedInputChannel = expectedInputChannel;
	}

	public boolean isSupportStreaming() {
		return supportStreaming;
	}

	public void setSupportStreaming(boolean supportStreaming) {
		this.supportStreaming = supportStreaming;
	}

	public String getAPIInput() {
		return APIInput;
	}

	public void setAPIInput(String aPIInput) {
		APIInput = aPIInput;
	}

	public boolean isSecuredAPI() {
		return securedAPI;
	}

	public void setSecuredAPI(boolean securedAPI) {
		this.securedAPI = securedAPI;
	}
}