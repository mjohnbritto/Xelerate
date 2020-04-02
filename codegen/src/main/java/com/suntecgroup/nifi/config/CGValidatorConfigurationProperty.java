package com.suntecgroup.nifi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "validation")
@PropertySource("${app.config.location}validation.properties")
@Component
public class CGValidatorConfigurationProperty {

	private String businessProcessEmpty;

	private String businessProcessName;

	private String businessProcessDescription;

	// process variable

	private String processVariableArray;

	private String processVariableName;

	private String processVariableDescription;

	private String processVariableType;

	private String processVariableMandatoryStringValue;

	private String processVariableMandatoryDateValue;

	private String processVariableMandatoryInitValue;

	private String processVariableInitValueError;

	// operators

	private String noOperator;

	private String multipleStart;

	private String operatorName;

	private String operatorKey;

	private String operatorType;

	private String inputBeType;

	private String startEventLogging;

	private String startInputBUK;

	private String invokebsBusinessServiceName;

	private String invokebsApiName;

	private String outputBeType;

	// private String bufferSize;

	private String invokeContextVariable;

	private String invokebsApiServiceContext;

	private String invokebsContextVariableName;

	private String invokebsSelectedKey;

	private String invokebsProcessVariableName;

	private String invokebsEnterValueString;

	private String invokebsEnterValueDate;

	private String invokebsEnterValueNumber;

	private String invokebsInitValue;

	private String outputBUK;

	// InputMapping

	private String invokebsIPMContextParameterType;

	private String invokebsIPMType;

	private String invokebsIPMEmptyContextParameter;

	private String invokebsIPMMultipleContextParameter;

	private String invokebsIPMEmptyInputMapping;

	private String invokebsIPMProcessVariable;

	private String invokebsIPMProcessVariableNotFound;

	private String invokebsIPMProcessVariableStringValue;

	private String invokebsProcessVariableNumberPrecision_one;

	private String invokebsProcessVariableNumberPrecision_two;

	private String invokebsProcessVariableNumberScale_one;

	private String invokebsProcessVariableNumberScale_two;

	private String invokebsIPMProcessVariableInitValue;

	private String invokebsIPMProcessVariableInitValue_one;

	private String invokebsIPMProcessVariableInitValue_two;

	private String invokebsProcessVariableDateValue;

	private String invokebsIPMMismatchedType_one;

	private String invokebsIPMMismatchedType_two;

	private String invokebsIPMMismatchedType_three;

	private String invokebsIPMMismatchedType_four;

	private String invokebsIPMEnterValue;

	private String invokebsIPMEnterValuePrecision_one;

	private String invokebsIPMEnterValuePrecision_two;

	private String invokebsIPMEnterValueScale_one;

	private String invokebsIPMEnterValueScale_two;

	private String invokebsIPMEnterValueInitValue;

	private String invokebsIPMEnterValueInitValueError;

	// private String invokebsIPMEnterValueString;
	// private String invokebsIPMEnterValueDate;

	private String invokebsIPMEnterValueMismatched;

	private String invokebsErrorCodeBusinessFailure;

	// OutputMapping

	private String invokebsOPMProcessVariableName;

	private String invokebsOPMType;

	private String invokebsOPMSelectedKey;

	private String invokebsEmptyOutputMapping;

	private String invokebsOPMContextVariable;

	private String invokebsOPMProcessVariableNotFound;

	private String invokebsOPMProcessVariableType;

	private String invokebsOPMContextParameterType;

	private String invokebsOPMOutputAttributeType;

	private String invokebsOPMOutputAttributeNotFound;

	private String invokebsOPMResponseVariableName;

	private String invokebsOPMOutputAttrType;

	private String mergeExpectedNoInputBE;
	
	private String mergeExpectedInputChannelMismatch;
	
	private String mergeWithFileInputBatchingError;

	private String mergeUnassignedContextVariables;

	public String getInvokebsErrorCodeBusinessFailure() {
		return invokebsErrorCodeBusinessFailure;
	}

	public void setInvokebsErrorCodeBusinessFailure(String invokebsErrorCodeBusinessFailure) {
		this.invokebsErrorCodeBusinessFailure = invokebsErrorCodeBusinessFailure;
	}

	public String getInvokebsOPMOutputAttrType() {
		return invokebsOPMOutputAttrType;
	}

	public void setInvokebsOPMOutputAttrType(String invokebsOPMOutputAttrType) {
		this.invokebsOPMOutputAttrType = invokebsOPMOutputAttrType;
	}

	public String getInvokebsOPMResponseVariableName() {
		return invokebsOPMResponseVariableName;
	}

	public void setInvokebsOPMResponseVariableName(String invokebsOPMResponseVariableName) {
		this.invokebsOPMResponseVariableName = invokebsOPMResponseVariableName;
	}

	public String getInvokebsOPMOutputAttributeNotFound() {
		return invokebsOPMOutputAttributeNotFound;
	}

	public void setInvokebsOPMOutputAttributeNotFound(String invokebsOPMOutputAttributeNotFound) {
		this.invokebsOPMOutputAttributeNotFound = invokebsOPMOutputAttributeNotFound;
	}

	public String getInvokebsOPMOutputAttributeType() {
		return invokebsOPMOutputAttributeType;
	}

	public void setInvokebsOPMOutputAttributeType(String invokebsOPMOutputAttributeType) {
		this.invokebsOPMOutputAttributeType = invokebsOPMOutputAttributeType;
	}

	private String invokebsOPMMismatchedType_onePv;

	public String getInvokebsOPMMismatchedType_onePv() {
		return invokebsOPMMismatchedType_onePv;
	}

	public void setInvokebsOPMMismatchedType_onePv(String invokebsOPMMismatchedType_onePv) {
		this.invokebsOPMMismatchedType_onePv = invokebsOPMMismatchedType_onePv;
	}

	private String invokebsOPMContextParameterString;

	private String invokebsOPMMismatchedType_one;

	private String invokebsOPMMismatchedType_two;

	private String invokebsOPMMismatchedType_three;

	private String invokebsOPMMismatchedType_four;

	private String invokebsOPMEnterValue;

	private String invokebsOPMEnterValueType;

	private String invokebsOPMEnterValuePrecision_one;

	private String invokebsOPMEnterValuePrecision_two;

	private String invokebsOPMEnterValueScale_one;

	private String invokebsOPMEnterValueScale_two;

	private String invokebsOPMEnterValueInitValue;

	private String invokebsOPMEnterValueInitMismatch;

	private String invokebsOPMEnterValueString;

	private String invokebsOPMEnterValueDate;

	private String invokebsOPMEnterValueMismatched;
	
	private String invokebsExternalURL;

	private String invokebsOPMEnterValueMismatchedAttribute;

	// external
	private String invokebsOPMEnterValueOutputAttrPrecision_one;
	private String invokebsOPMEnterValueOutputAttrPrecision_two;
	private String invokebsOPMEnterValueOutputAttrScale_one;
	private String invokebsOPMEnterValueOutputAttrScale_two;

	private String invokebsOPMEnterValueOutputAttrInitValue;
	private String invokebsOPMEnterValueOutputAttrInitMismatch;
	private String invokebsOPMEnterValueOutputAttrString;
	private String invokebsOPMEnterValueOutputAttrDate;

	public String getInvokebsOPMEnterValueOutputAttrPrecision_one() {
		return invokebsOPMEnterValueOutputAttrPrecision_one;
	}

	public void setInvokebsOPMEnterValueOutputAttrPrecision_one(String invokebsOPMEnterValueOutputAttrPrecision_one) {
		this.invokebsOPMEnterValueOutputAttrPrecision_one = invokebsOPMEnterValueOutputAttrPrecision_one;
	}

	public String getInvokebsOPMEnterValueOutputAttrPrecision_two() {
		return invokebsOPMEnterValueOutputAttrPrecision_two;
	}

	public void setInvokebsOPMEnterValueOutputAttrPrecision_two(String invokebsOPMEnterValueOutputAttrPrecision_two) {
		this.invokebsOPMEnterValueOutputAttrPrecision_two = invokebsOPMEnterValueOutputAttrPrecision_two;
	}

	public String getInvokebsOPMEnterValueOutputAttrScale_one() {
		return invokebsOPMEnterValueOutputAttrScale_one;
	}

	public void setInvokebsOPMEnterValueOutputAttrScale_one(String invokebsOPMEnterValueOutputAttrScale_one) {
		this.invokebsOPMEnterValueOutputAttrScale_one = invokebsOPMEnterValueOutputAttrScale_one;
	}

	public String getInvokebsOPMEnterValueOutputAttrScale_two() {
		return invokebsOPMEnterValueOutputAttrScale_two;
	}

	public void setInvokebsOPMEnterValueOutputAttrScale_two(String invokebsOPMEnterValueOutputAttrScale_two) {
		this.invokebsOPMEnterValueOutputAttrScale_two = invokebsOPMEnterValueOutputAttrScale_two;
	}

	public String getInvokebsOPMEnterValueOutputAttrInitValue() {
		return invokebsOPMEnterValueOutputAttrInitValue;
	}

	public void setInvokebsOPMEnterValueOutputAttrInitValue(String invokebsOPMEnterValueOutputAttrInitValue) {
		this.invokebsOPMEnterValueOutputAttrInitValue = invokebsOPMEnterValueOutputAttrInitValue;
	}

	public String getInvokebsOPMEnterValueOutputAttrInitMismatch() {
		return invokebsOPMEnterValueOutputAttrInitMismatch;
	}

	public void setInvokebsOPMEnterValueOutputAttrInitMismatch(String invokebsOPMEnterValueOutputAttrInitMismatch) {
		this.invokebsOPMEnterValueOutputAttrInitMismatch = invokebsOPMEnterValueOutputAttrInitMismatch;
	}

	public String getInvokebsOPMEnterValueOutputAttrString() {
		return invokebsOPMEnterValueOutputAttrString;
	}

	public void setInvokebsOPMEnterValueOutputAttrString(String invokebsOPMEnterValueOutputAttrString) {
		this.invokebsOPMEnterValueOutputAttrString = invokebsOPMEnterValueOutputAttrString;
	}

	public String getInvokebsOPMEnterValueOutputAttrDate() {
		return invokebsOPMEnterValueOutputAttrDate;
	}

	public void setInvokebsOPMEnterValueOutputAttrDate(String invokebsOPMEnterValueOutputAttrDate) {
		this.invokebsOPMEnterValueOutputAttrDate = invokebsOPMEnterValueOutputAttrDate;
	}

	public String getInvokebsOPMEnterValueMismatchedAttribute() {
		return invokebsOPMEnterValueMismatchedAttribute;
	}

	public void setInvokebsOPMEnterValueMismatchedAttribute(String invokebsOPMEnterValueMismatchedAttribute) {
		this.invokebsOPMEnterValueMismatchedAttribute = invokebsOPMEnterValueMismatchedAttribute;
	}

	private String dmExclusiveDecisions;

	private String dmExclusiveDecisionName;

	private String dmExclusiveflagExclusive;

	private String dmExclusiveBusinessSetting;

	// BE Definition

	private String beDefinitionArtifactId;

	private String beDefinitionName;

	private String beDefinitionDepartment;

	private String beDefinitionModule;

	private String beDefinitionRelease;

	private String beDefinitionBusinessUniqueKey;

	private String beDefinitionBeAttributeProperty;

	private String beDefinitionAttributeId;

	private String beDefinitionAttributeName;

	private String beDefinitionDataType;

	private String beDefinitionDataTypeType;

	private String beDefinitionDataTypePrecisionScale;

	// property

	private String noPropertyFound;
	private String propertyName;
	private String propertyPenaltyDuration;
	private String propertyYieldDuration;
	private String propertyConcurrentTasks;
	private String inputFileLocation;
	private String addressed;
	private String recordReject;
	private String fileReject;
	private String backup;

	private String outputFileLocation;
	private String restApiBasepath;
	private String restApiPort;
	private String methodType;

	private String propertyUsername;
	private String propertyPassword;

	private String fileChannelIntegrationHeaderLines;
	private String fileChannelIntegrationHeaderLinesMismatching;
	private String fileChannelIntegrationHeaderDelimitedAttributeNotFound;
	private String fileChannelIntegrationHeaderDelimitedMissingSegmentPos;
	private String fileChannelIntegrationHeaderDelimitedMissingLineNumber;

	private String fileChannelIntegrationFooterLines;
	private String fileChannelIntegrationFooterLinesMismatching;
	private String fileChannelIntegrationFooterDelimitedAttributeNotFound;
	private String fileChannelIntegrationFooterDelimitedMissingSegmentPos;
	private String fileChannelIntegrationFooterDelimitedMissingLineNumber;

	private String fileChannelIntegrationInputMappingNotFound;
	private String fileChannelIntegrationOutputMappingNotFound;

	private String fileChannelIntegrationContentDelimitedAttributeNotFound;
	private String fileChannelIntegrationContentDelimitedMissingSegmentPos;
	private String fileChannelIntegrationInputFileDuplicationDurationUnits;
	private String fileChannelIntegrationOutputFileName;
	private String invalidBatchSize;
	
	private String undefinedOperator_one;
	private String undefinedOperator_two;
	
	private String noInputChannelOperator;

	public String getBusinessProcessEmpty() {
		return businessProcessEmpty;
	}

	public void setBusinessProcessEmpty(String businessProcessEmpty) {
		this.businessProcessEmpty = businessProcessEmpty;
	}

	public String getBusinessProcessName() {
		return businessProcessName;
	}

	public void setBusinessProcessName(String businessProcessName) {
		this.businessProcessName = businessProcessName;
	}

	public String getBusinessProcessDescription() {
		return businessProcessDescription;
	}

	public void setBusinessProcessDescription(String businessProcessDescription) {
		this.businessProcessDescription = businessProcessDescription;
	}

	public String getProcessVariableArray() {
		return processVariableArray;
	}

	public void setProcessVariableArray(String processVariableArray) {
		this.processVariableArray = processVariableArray;
	}

	public String getProcessVariableName() {
		return processVariableName;
	}

	public void setProcessVariableName(String processVariableName) {
		this.processVariableName = processVariableName;
	}

	public String getProcessVariableDescription() {
		return processVariableDescription;
	}

	public void setProcessVariableDescription(String processVariableDescription) {
		this.processVariableDescription = processVariableDescription;
	}

	public String getProcessVariableType() {
		return processVariableType;
	}

	public void setProcessVariableType(String processVariableType) {
		this.processVariableType = processVariableType;
	}

	public String getProcessVariableMandatoryStringValue() {
		return processVariableMandatoryStringValue;
	}

	public void setProcessVariableMandatoryStringValue(String processVariableMandatoryStringValue) {
		this.processVariableMandatoryStringValue = processVariableMandatoryStringValue;
	}

	public String getProcessVariableMandatoryDateValue() {
		return processVariableMandatoryDateValue;
	}

	public void setProcessVariableMandatoryDateValue(String processVariableMandatoryDateValue) {
		this.processVariableMandatoryDateValue = processVariableMandatoryDateValue;
	}

	public String getProcessVariableMandatoryInitValue() {
		return processVariableMandatoryInitValue;
	}

	public void setProcessVariableMandatoryInitValue(String processVariableMandatoryInitValue) {
		this.processVariableMandatoryInitValue = processVariableMandatoryInitValue;
	}

	public String getProcessVariableInitValueError() {
		return processVariableInitValueError;
	}

	public void setProcessVariableInitValueError(String processVariableInitValueError) {
		this.processVariableInitValueError = processVariableInitValueError;
	}

	public String getNoOperator() {
		return noOperator;
	}

	public void setNoOperator(String noOperator) {
		this.noOperator = noOperator;
	}

	public String getMultipleStart() {
		return multipleStart;
	}

	public void setMultipleStart(String multipleStart) {
		this.multipleStart = multipleStart;
	}

	public String getOperatorName() {
		return operatorName;

	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}

	public String getInputBeType() {
		return inputBeType;
	}

	public void setInputBeType(String inputBeType) {
		this.inputBeType = inputBeType;
	}

	public String getStartEventLogging() {
		return startEventLogging;
	}

	public void setStartEventLogging(String startEventLogging) {
		this.startEventLogging = startEventLogging;
	}

	public String getStartInputBUK() {
		return startInputBUK;
	}

	public void setStartInputBUK(String startInputBUK) {
		this.startInputBUK = startInputBUK;
	}

	public String getInvokebsBusinessServiceName() {
		return invokebsBusinessServiceName;
	}

	public void setInvokebsBusinessServiceName(String invokebsBusinessServiceName) {
		this.invokebsBusinessServiceName = invokebsBusinessServiceName;
	}

	public String getInvokebsApiName() {
		return invokebsApiName;
	}

	public void setInvokebsApiName(String invokebsApiName) {
		this.invokebsApiName = invokebsApiName;
	}

	public String getOutputBeType() {
		return outputBeType;
	}

	public void setOutputBeType(String outputBeType) {
		this.outputBeType = outputBeType;
	}

	public String getInvokeContextVariable() {
		return invokeContextVariable;
	}

	public void setInvokeContextVariable(String invokeContextVariable) {
		this.invokeContextVariable = invokeContextVariable;
	}

	public String getInvokebsApiServiceContext() {
		return invokebsApiServiceContext;
	}

	public void setInvokebsApiServiceContext(String invokebsApiServiceContext) {
		this.invokebsApiServiceContext = invokebsApiServiceContext;
	}

	public String getInvokebsContextVariableName() {
		return invokebsContextVariableName;
	}

	public void setInvokebsContextVariableName(String invokebsContextVariableName) {
		this.invokebsContextVariableName = invokebsContextVariableName;
	}

	public String getInvokebsSelectedKey() {
		return invokebsSelectedKey;
	}

	public void setInvokebsSelectedKey(String invokebsSelectedKey) {
		this.invokebsSelectedKey = invokebsSelectedKey;
	}

	public String getInvokebsProcessVariableName() {
		return invokebsProcessVariableName;
	}

	public void setInvokebsProcessVariableName(String invokebsProcessVariableName) {
		this.invokebsProcessVariableName = invokebsProcessVariableName;
	}

	public String getInvokebsEnterValueString() {
		return invokebsEnterValueString;
	}

	public void setInvokebsEnterValueString(String invokebsEnterValueString) {
		this.invokebsEnterValueString = invokebsEnterValueString;
	}

	public String getInvokebsEnterValueDate() {
		return invokebsEnterValueDate;
	}

	public void setInvokebsEnterValueDate(String invokebsEnterValueDate) {
		this.invokebsEnterValueDate = invokebsEnterValueDate;
	}

	public String getInvokebsEnterValueNumber() {
		return invokebsEnterValueNumber;
	}

	public void setInvokebsEnterValueNumber(String invokebsEnterValueNumber) {
		this.invokebsEnterValueNumber = invokebsEnterValueNumber;
	}

	public String getInvokebsInitValue() {
		return invokebsInitValue;
	}

	public void setInvokebsInitValue(String invokebsInitValue) {
		this.invokebsInitValue = invokebsInitValue;
	}

	public String getOutputBUK() {
		return outputBUK;
	}

	public void setOutputBUK(String outputBUK) {
		this.outputBUK = outputBUK;
	}

	public String getInvokebsIPMContextParameterType() {
		return invokebsIPMContextParameterType;
	}

	public void setInvokebsIPMContextParameterType(String invokebsIPMContextParameterType) {
		this.invokebsIPMContextParameterType = invokebsIPMContextParameterType;
	}

	public String getInvokebsIPMType() {
		return invokebsIPMType;
	}

	public void setInvokebsIPMType(String invokebsIPMType) {
		this.invokebsIPMType = invokebsIPMType;
	}

	public String getInvokebsIPMEmptyContextParameter() {
		return invokebsIPMEmptyContextParameter;
	}

	public void setInvokebsIPMEmptyContextParameter(String invokebsIPMEmptyContextParameter) {
		this.invokebsIPMEmptyContextParameter = invokebsIPMEmptyContextParameter;
	}

	public String getInvokebsIPMMultipleContextParameter() {
		return invokebsIPMMultipleContextParameter;
	}

	public void setInvokebsIPMMultipleContextParameter(String invokebsIPMMultipleContextParameter) {
		this.invokebsIPMMultipleContextParameter = invokebsIPMMultipleContextParameter;
	}

	public String getInvokebsIPMEmptyInputMapping() {
		return invokebsIPMEmptyInputMapping;
	}

	public void setInvokebsIPMEmptyInputMapping(String invokebsIPMEmptyInputMapping) {
		this.invokebsIPMEmptyInputMapping = invokebsIPMEmptyInputMapping;
	}

	public String getInvokebsIPMProcessVariable() {
		return invokebsIPMProcessVariable;
	}

	public void setInvokebsIPMProcessVariable(String invokebsIPMProcessVariable) {
		this.invokebsIPMProcessVariable = invokebsIPMProcessVariable;
	}

	public String getInvokebsIPMProcessVariableNotFound() {
		return invokebsIPMProcessVariableNotFound;
	}

	public void setInvokebsIPMProcessVariableNotFound(String invokebsIPMProcessVariableNotFound) {
		this.invokebsIPMProcessVariableNotFound = invokebsIPMProcessVariableNotFound;
	}

	public String getInvokebsIPMProcessVariableStringValue() {
		return invokebsIPMProcessVariableStringValue;
	}

	public void setInvokebsIPMProcessVariableStringValue(String invokebsIPMProcessVariableStringValue) {
		this.invokebsIPMProcessVariableStringValue = invokebsIPMProcessVariableStringValue;
	}

	public String getInvokebsProcessVariableNumberPrecision_one() {
		return invokebsProcessVariableNumberPrecision_one;
	}

	public void setInvokebsProcessVariableNumberPrecision_one(String invokebsProcessVariableNumberPrecision_one) {
		this.invokebsProcessVariableNumberPrecision_one = invokebsProcessVariableNumberPrecision_one;
	}

	public String getInvokebsProcessVariableNumberPrecision_two() {
		return invokebsProcessVariableNumberPrecision_two;
	}

	public void setInvokebsProcessVariableNumberPrecision_two(String invokebsProcessVariableNumberPrecision_two) {
		this.invokebsProcessVariableNumberPrecision_two = invokebsProcessVariableNumberPrecision_two;
	}

	public String getInvokebsProcessVariableNumberScale_one() {
		return invokebsProcessVariableNumberScale_one;
	}

	public String getInvokebsProcessVariableNumberScale_two() {
		return invokebsProcessVariableNumberScale_two;
	}

	public void setInvokebsProcessVariableNumberScale_two(String invokebsProcessVariableNumberScale_two) {
		this.invokebsProcessVariableNumberScale_two = invokebsProcessVariableNumberScale_two;
	}

	public String getInvokebsIPMProcessVariableInitValue() {
		return invokebsIPMProcessVariableInitValue;
	}

	public void setInvokebsIPMProcessVariableInitValue(String invokebsIPMProcessVariableInitValue) {
		this.invokebsIPMProcessVariableInitValue = invokebsIPMProcessVariableInitValue;
	}

	public String getInvokebsIPMProcessVariableInitValue_one() {
		return invokebsIPMProcessVariableInitValue_one;
	}

	public void setInvokebsIPMProcessVariableInitValue_one(String invokebsIPMProcessVariableInitValue_one) {
		this.invokebsIPMProcessVariableInitValue_one = invokebsIPMProcessVariableInitValue_one;
	}

	public String getInvokebsIPMProcessVariableInitValue_two() {
		return invokebsIPMProcessVariableInitValue_two;
	}

	public void setInvokebsIPMProcessVariableInitValue_two(String invokebsIPMProcessVariableInitValue_two) {
		this.invokebsIPMProcessVariableInitValue_two = invokebsIPMProcessVariableInitValue_two;
	}

	public String getInvokebsProcessVariableDateValue() {
		return invokebsProcessVariableDateValue;
	}

	public void setInvokebsProcessVariableDateValue(String invokebsProcessVariableDateValue) {
		this.invokebsProcessVariableDateValue = invokebsProcessVariableDateValue;
	}

	public String getInvokebsIPMMismatchedType_one() {
		return invokebsIPMMismatchedType_one;
	}

	public void setInvokebsIPMMismatchedType_one(String invokebsIPMMismatchedType_one) {
		this.invokebsIPMMismatchedType_one = invokebsIPMMismatchedType_one;
	}

	public String getInvokebsIPMMismatchedType_two() {
		return invokebsIPMMismatchedType_two;
	}

	public void setInvokebsIPMMismatchedType_two(String invokebsIPMMismatchedType_two) {
		this.invokebsIPMMismatchedType_two = invokebsIPMMismatchedType_two;
	}

	public String getInvokebsIPMMismatchedType_three() {
		return invokebsIPMMismatchedType_three;
	}

	public void setInvokebsIPMMismatchedType_three(String invokebsIPMMismatchedType_three) {
		this.invokebsIPMMismatchedType_three = invokebsIPMMismatchedType_three;
	}

	public String getInvokebsIPMMismatchedType_four() {
		return invokebsIPMMismatchedType_four;
	}

	public void setInvokebsIPMMismatchedType_four(String invokebsIPMMismatchedType_four) {
		this.invokebsIPMMismatchedType_four = invokebsIPMMismatchedType_four;
	}

	public String getInvokebsIPMEnterValue() {
		return invokebsIPMEnterValue;
	}

	public void setInvokebsIPMEnterValue(String invokebsIPMEnterValue) {
		this.invokebsIPMEnterValue = invokebsIPMEnterValue;
	}

	public String getInvokebsIPMEnterValuePrecision_one() {
		return invokebsIPMEnterValuePrecision_one;
	}

	public void setInvokebsIPMEnterValuePrecision_one(String invokebsIPMEnterValuePrecision_one) {
		this.invokebsIPMEnterValuePrecision_one = invokebsIPMEnterValuePrecision_one;
	}

	public String getInvokebsIPMEnterValuePrecision_two() {
		return invokebsIPMEnterValuePrecision_two;
	}

	public void setInvokebsIPMEnterValuePrecision_two(String invokebsIPMEnterValuePrecision_two) {
		this.invokebsIPMEnterValuePrecision_two = invokebsIPMEnterValuePrecision_two;
	}

	public String getInvokebsIPMEnterValueScale_one() {
		return invokebsIPMEnterValueScale_one;
	}

	public void setInvokebsIPMEnterValueScale_one(String invokebsIPMEnterValueScale_one) {
		this.invokebsIPMEnterValueScale_one = invokebsIPMEnterValueScale_one;
	}

	public String getInvokebsIPMEnterValueScale_two() {
		return invokebsIPMEnterValueScale_two;
	}

	public void setInvokebsIPMEnterValueScale_two(String invokebsIPMEnterValueScale_two) {
		this.invokebsIPMEnterValueScale_two = invokebsIPMEnterValueScale_two;
	}

	public String getInvokebsIPMEnterValueInitValue() {
		return invokebsIPMEnterValueInitValue;
	}

	public void setInvokebsIPMEnterValueInitValue(String invokebsIPMEnterValueInitValue) {
		this.invokebsIPMEnterValueInitValue = invokebsIPMEnterValueInitValue;
	}

	public String getInvokebsIPMEnterValueInitValueError() {
		return invokebsIPMEnterValueInitValueError;
	}

	public void setInvokebsIPMEnterValueInitValueError(String invokebsIPMEnterValueInitValueError) {
		this.invokebsIPMEnterValueInitValueError = invokebsIPMEnterValueInitValueError;
	}

	public String getInvokebsIPMEnterValueMismatched() {
		return invokebsIPMEnterValueMismatched;
	}

	public void setInvokebsIPMEnterValueMismatched(String invokebsIPMEnterValueMismatched) {
		this.invokebsIPMEnterValueMismatched = invokebsIPMEnterValueMismatched;
	}

	public String getInvokebsOPMProcessVariableName() {
		return invokebsOPMProcessVariableName;
	}

	public void setInvokebsOPMProcessVariableName(String invokebsOPMProcessVariableName) {
		this.invokebsOPMProcessVariableName = invokebsOPMProcessVariableName;
	}

	public String getInvokebsOPMType() {
		return invokebsOPMType;
	}

	public void setInvokebsOPMType(String invokebsOPMType) {
		this.invokebsOPMType = invokebsOPMType;
	}

	public String getInvokebsOPMSelectedKey() {
		return invokebsOPMSelectedKey;
	}

	public void setInvokebsOPMSelectedKey(String invokebsOPMSelectedKey) {
		this.invokebsOPMSelectedKey = invokebsOPMSelectedKey;
	}

	public String getInvokebsEmptyOutputMapping() {
		return invokebsEmptyOutputMapping;
	}

	public void setInvokebsEmptyOutputMapping(String invokebsEmptyOutputMapping) {
		this.invokebsEmptyOutputMapping = invokebsEmptyOutputMapping;
	}

	public String getInvokebsOPMContextVariable() {
		return invokebsOPMContextVariable;
	}

	public void setInvokebsOPMContextVariable(String invokebsOPMContextVariable) {
		this.invokebsOPMContextVariable = invokebsOPMContextVariable;
	}

	public String getInvokebsOPMProcessVariableNotFound() {
		return invokebsOPMProcessVariableNotFound;
	}

	public void setInvokebsOPMProcessVariableNotFound(String invokebsOPMProcessVariableNotFound) {
		this.invokebsOPMProcessVariableNotFound = invokebsOPMProcessVariableNotFound;
	}

	public String getInvokebsOPMProcessVariableType() {
		return invokebsOPMProcessVariableType;
	}

	public void setInvokebsOPMProcessVariableType(String invokebsOPMProcessVariableType) {
		this.invokebsOPMProcessVariableType = invokebsOPMProcessVariableType;
	}

	public String getInvokebsOPMContextParameterType() {
		return invokebsOPMContextParameterType;
	}

	public void setInvokebsOPMContextParameterType(String invokebsOPMContextParameterType) {
		this.invokebsOPMContextParameterType = invokebsOPMContextParameterType;
	}

	public String getInvokebsOPMContextParameterString() {
		return invokebsOPMContextParameterString;
	}

	public void setInvokebsOPMContextParameterString(String invokebsOPMContextParameterString) {
		this.invokebsOPMContextParameterString = invokebsOPMContextParameterString;
	}

	public String getInvokebsOPMMismatchedType_one() {
		return invokebsOPMMismatchedType_one;
	}

	public void setInvokebsOPMMismatchedType_one(String invokebsOPMMismatchedType_one) {
		this.invokebsOPMMismatchedType_one = invokebsOPMMismatchedType_one;
	}

	public String getInvokebsOPMMismatchedType_two() {
		return invokebsOPMMismatchedType_two;
	}

	public void setInvokebsOPMMismatchedType_two(String invokebsOPMMismatchedType_two) {
		this.invokebsOPMMismatchedType_two = invokebsOPMMismatchedType_two;
	}

	public String getInvokebsOPMMismatchedType_three() {
		return invokebsOPMMismatchedType_three;
	}

	public void setInvokebsOPMMismatchedType_three(String invokebsOPMMismatchedType_three) {
		this.invokebsOPMMismatchedType_three = invokebsOPMMismatchedType_three;
	}

	public String getInvokebsOPMMismatchedType_four() {
		return invokebsOPMMismatchedType_four;
	}

	public void setInvokebsOPMMismatchedType_four(String invokebsOPMMismatchedType_four) {
		this.invokebsOPMMismatchedType_four = invokebsOPMMismatchedType_four;
	}

	public String getInvokebsOPMEnterValue() {
		return invokebsOPMEnterValue;
	}

	public void setInvokebsOPMEnterValue(String invokebsOPMEnterValue) {
		this.invokebsOPMEnterValue = invokebsOPMEnterValue;
	}

	public String getInvokebsOPMEnterValueType() {
		return invokebsOPMEnterValueType;
	}

	public void setInvokebsOPMEnterValueType(String invokebsOPMEnterValueType) {
		this.invokebsOPMEnterValueType = invokebsOPMEnterValueType;
	}

	public String getInvokebsOPMEnterValuePrecision_one() {
		return invokebsOPMEnterValuePrecision_one;
	}

	public void setInvokebsOPMEnterValuePrecision_one(String invokebsOPMEnterValuePrecision_one) {
		this.invokebsOPMEnterValuePrecision_one = invokebsOPMEnterValuePrecision_one;
	}

	public String getInvokebsOPMEnterValuePrecision_two() {
		return invokebsOPMEnterValuePrecision_two;
	}

	public void setInvokebsOPMEnterValuePrecision_two(String invokebsOPMEnterValuePrecision_two) {
		this.invokebsOPMEnterValuePrecision_two = invokebsOPMEnterValuePrecision_two;
	}

	public String getInvokebsOPMEnterValueScale_one() {
		return invokebsOPMEnterValueScale_one;
	}

	public void setInvokebsOPMEnterValueScale_one(String invokebsOPMEnterValueScale_one) {
		this.invokebsOPMEnterValueScale_one = invokebsOPMEnterValueScale_one;
	}

	public String getInvokebsOPMEnterValueScale_two() {
		return invokebsOPMEnterValueScale_two;
	}

	public void setInvokebsOPMEnterValueScale_two(String invokebsOPMEnterValueScale_two) {
		this.invokebsOPMEnterValueScale_two = invokebsOPMEnterValueScale_two;
	}

	public String getInvokebsOPMEnterValueInitValue() {
		return invokebsOPMEnterValueInitValue;
	}

	public void setInvokebsOPMEnterValueInitValue(String invokebsOPMEnterValueInitValue) {
		this.invokebsOPMEnterValueInitValue = invokebsOPMEnterValueInitValue;
	}

	public String getInvokebsOPMEnterValueInitMismatch() {
		return invokebsOPMEnterValueInitMismatch;
	}

	public void setInvokebsOPMEnterValueInitMismatch(String invokebsOPMEnterValueInitMismatch) {
		this.invokebsOPMEnterValueInitMismatch = invokebsOPMEnterValueInitMismatch;
	}

	public String getInvokebsOPMEnterValueString() {
		return invokebsOPMEnterValueString;
	}

	public void setInvokebsOPMEnterValueString(String invokebsOPMEnterValueString) {
		this.invokebsOPMEnterValueString = invokebsOPMEnterValueString;
	}

	public String getInvokebsOPMEnterValueDate() {
		return invokebsOPMEnterValueDate;
	}

	public void setInvokebsOPMEnterValueDate(String invokebsOPMEnterValueDate) {
		this.invokebsOPMEnterValueDate = invokebsOPMEnterValueDate;
	}

	public String getInvokebsOPMEnterValueMismatched() {
		return invokebsOPMEnterValueMismatched;
	}

	public void setInvokebsOPMEnterValueMismatched(String invokebsOPMEnterValueMismatched) {
		this.invokebsOPMEnterValueMismatched = invokebsOPMEnterValueMismatched;
	}

	public String getDmExclusiveDecisions() {
		return dmExclusiveDecisions;
	}

	public void setDmExclusiveDecisions(String dmExclusiveDecisions) {
		this.dmExclusiveDecisions = dmExclusiveDecisions;
	}

	public String getDmExclusiveDecisionName() {
		return dmExclusiveDecisionName;
	}

	public void setDmExclusiveDecisionName(String dmExclusiveDecisionName) {
		this.dmExclusiveDecisionName = dmExclusiveDecisionName;
	}

	public String getDmExclusiveflagExclusive() {
		return dmExclusiveflagExclusive;
	}

	public void setDmExclusiveflagExclusive(String dmExclusiveflagExclusive) {
		this.dmExclusiveflagExclusive = dmExclusiveflagExclusive;
	}

	public String getDmExclusiveBusinessSetting() {
		return dmExclusiveBusinessSetting;
	}

	public void setDmExclusiveBusinessSetting(String dmExclusiveBusinessSetting) {
		this.dmExclusiveBusinessSetting = dmExclusiveBusinessSetting;
	}

	public String getBeDefinitionArtifactId() {
		return beDefinitionArtifactId;
	}

	public void setBeDefinitionArtifactId(String beDefinitionArtifactId) {
		this.beDefinitionArtifactId = beDefinitionArtifactId;
	}

	public String getBeDefinitionName() {
		return beDefinitionName;
	}

	public void setBeDefinitionName(String beDefinitionName) {
		this.beDefinitionName = beDefinitionName;
	}

	public String getBeDefinitionDepartment() {
		return beDefinitionDepartment;
	}

	public void setBeDefinitionDepartment(String beDefinitionDepartment) {
		this.beDefinitionDepartment = beDefinitionDepartment;
	}

	public String getBeDefinitionModule() {
		return beDefinitionModule;
	}

	public void setBeDefinitionModule(String beDefinitionModule) {
		this.beDefinitionModule = beDefinitionModule;
	}

	public String getBeDefinitionRelease() {
		return beDefinitionRelease;
	}

	public void setBeDefinitionRelease(String beDefinitionRelease) {
		this.beDefinitionRelease = beDefinitionRelease;
	}

	public String getBeDefinitionBusinessUniqueKey() {
		return beDefinitionBusinessUniqueKey;
	}

	public void setBeDefinitionBusinessUniqueKey(String beDefinitionBusinessUniqueKey) {
		this.beDefinitionBusinessUniqueKey = beDefinitionBusinessUniqueKey;
	}

	public String getBeDefinitionBeAttributeProperty() {
		return beDefinitionBeAttributeProperty;
	}

	public void setBeDefinitionBeAttributeProperty(String beDefinitionBeAttributeProperty) {
		this.beDefinitionBeAttributeProperty = beDefinitionBeAttributeProperty;
	}

	public String getBeDefinitionAttributeId() {
		return beDefinitionAttributeId;
	}

	public void setBeDefinitionAttributeId(String beDefinitionAttributeId) {
		this.beDefinitionAttributeId = beDefinitionAttributeId;
	}

	public String getBeDefinitionAttributeName() {
		return beDefinitionAttributeName;
	}

	public void setBeDefinitionAttributeName(String beDefinitionAttributeName) {
		this.beDefinitionAttributeName = beDefinitionAttributeName;
	}

	public String getBeDefinitionDataType() {
		return beDefinitionDataType;
	}

	public void setBeDefinitionDataType(String beDefinitionDataType) {
		this.beDefinitionDataType = beDefinitionDataType;
	}

	public String getBeDefinitionDataTypeType() {
		return beDefinitionDataTypeType;
	}

	public void setBeDefinitionDataTypeType(String beDefinitionDataTypeType) {
		this.beDefinitionDataTypeType = beDefinitionDataTypeType;
	}

	public String getBeDefinitionDataTypePrecisionScale() {
		return beDefinitionDataTypePrecisionScale;
	}

	public void setBeDefinitionDataTypePrecisionScale(String beDefinitionDataTypePrecisionScale) {
		this.beDefinitionDataTypePrecisionScale = beDefinitionDataTypePrecisionScale;
	}

	public String getNoPropertyFound() {
		return noPropertyFound;
	}

	public void setNoPropertyFound(String noPropertyFound) {
		this.noPropertyFound = noPropertyFound;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyPenaltyDuration() {
		return propertyPenaltyDuration;
	}

	public void setPropertyPenaltyDuration(String propertyPenaltyDuration) {
		this.propertyPenaltyDuration = propertyPenaltyDuration;
	}

	public String getPropertyYieldDuration() {
		return propertyYieldDuration;
	}

	public void setPropertyYieldDuration(String propertyYieldDuration) {
		this.propertyYieldDuration = propertyYieldDuration;
	}

	public String getPropertyConcurrentTasks() {
		return propertyConcurrentTasks;
	}

	public void setPropertyConcurrentTasks(String propertyConcurrentTasks) {
		this.propertyConcurrentTasks = propertyConcurrentTasks;
	}

	public String getInputFileLocation() {
		return inputFileLocation;
	}

	public void setInputFileLocation(String inputFileLocation) {
		this.inputFileLocation = inputFileLocation;
	}

	public String getAddressed() {
		return addressed;
	}

	public void setAddressed(String addressed) {
		this.addressed = addressed;
	}

	public String getRecordReject() {
		return recordReject;
	}

	public void setRecordReject(String recordReject) {
		this.recordReject = recordReject;
	}

	public String getFileReject() {
		return fileReject;
	}

	public void setFileReject(String fileReject) {
		this.fileReject = fileReject;
	}

	public String getBackup() {
		return backup;
	}

	public void setBackup(String backup) {
		this.backup = backup;
	}

	public void setInvokebsProcessVariableNumberScale_one(String invokebsProcessVariableNumberScale_one) {
		this.invokebsProcessVariableNumberScale_one = invokebsProcessVariableNumberScale_one;
	}

	public String getUndefinedOperator_one() {
		return undefinedOperator_one;
	}

	public void setUndefinedOperator_one(String undefinedOperator_one) {
		this.undefinedOperator_one = undefinedOperator_one;
	}

	public String getUndefinedOperator_two() {
		return undefinedOperator_two;
	}

	public void setUndefinedOperator_two(String undefinedOperator_two) {
		this.undefinedOperator_two = undefinedOperator_two;
	}

	public String getFileChannelIntegrationHeaderLines() {
		return fileChannelIntegrationHeaderLines;
	}

	public void setFileChannelIntegrationHeaderLines(String fileChannelIntegrationHeaderLines) {
		this.fileChannelIntegrationHeaderLines = fileChannelIntegrationHeaderLines;
	}

	public String getFileChannelIntegrationHeaderLinesMismatching() {
		return fileChannelIntegrationHeaderLinesMismatching;
	}

	public void setFileChannelIntegrationHeaderLinesMismatching(String fileChannelIntegrationHeaderLinesMismatching) {
		this.fileChannelIntegrationHeaderLinesMismatching = fileChannelIntegrationHeaderLinesMismatching;
	}

	public String getFileChannelIntegrationHeaderDelimitedAttributeNotFound() {
		return fileChannelIntegrationHeaderDelimitedAttributeNotFound;
	}

	public void setFileChannelIntegrationHeaderDelimitedAttributeNotFound(
			String fileChannelIntegrationHeaderDelimitedAttributeNotFound) {
		this.fileChannelIntegrationHeaderDelimitedAttributeNotFound = fileChannelIntegrationHeaderDelimitedAttributeNotFound;
	}

	public String getFileChannelIntegrationHeaderDelimitedMissingSegmentPos() {
		return fileChannelIntegrationHeaderDelimitedMissingSegmentPos;
	}

	public void setFileChannelIntegrationHeaderDelimitedMissingSegmentPos(
			String fileChannelIntegrationHeaderDelimitedMissingSegmentPos) {
		this.fileChannelIntegrationHeaderDelimitedMissingSegmentPos = fileChannelIntegrationHeaderDelimitedMissingSegmentPos;
	}

	public String getFileChannelIntegrationHeaderDelimitedMissingLineNumber() {
		return fileChannelIntegrationHeaderDelimitedMissingLineNumber;
	}

	public void setFileChannelIntegrationHeaderDelimitedMissingLineNumber(
			String fileChannelIntegrationHeaderDelimitedMissingLineNumber) {
		this.fileChannelIntegrationHeaderDelimitedMissingLineNumber = fileChannelIntegrationHeaderDelimitedMissingLineNumber;
	}

	public String getFileChannelIntegrationFooterLines() {
		return fileChannelIntegrationFooterLines;
	}

	public void setFileChannelIntegrationFooterLines(String fileChannelIntegrationFooterLines) {
		this.fileChannelIntegrationFooterLines = fileChannelIntegrationFooterLines;
	}

	public String getFileChannelIntegrationFooterLinesMismatching() {
		return fileChannelIntegrationFooterLinesMismatching;
	}

	public void setFileChannelIntegrationFooterLinesMismatching(String fileChannelIntegrationFooterLinesMismatching) {
		this.fileChannelIntegrationFooterLinesMismatching = fileChannelIntegrationFooterLinesMismatching;
	}

	public String getFileChannelIntegrationFooterDelimitedAttributeNotFound() {
		return fileChannelIntegrationFooterDelimitedAttributeNotFound;
	}

	public void setFileChannelIntegrationFooterDelimitedAttributeNotFound(
			String fileChannelIntegrationFooterDelimitedAttributeNotFound) {
		this.fileChannelIntegrationFooterDelimitedAttributeNotFound = fileChannelIntegrationFooterDelimitedAttributeNotFound;
	}

	public String getFileChannelIntegrationFooterDelimitedMissingSegmentPos() {
		return fileChannelIntegrationFooterDelimitedMissingSegmentPos;
	}

	public void setFileChannelIntegrationFooterDelimitedMissingSegmentPos(
			String fileChannelIntegrationFooterDelimitedMissingSegmentPos) {
		this.fileChannelIntegrationFooterDelimitedMissingSegmentPos = fileChannelIntegrationFooterDelimitedMissingSegmentPos;
	}

	public String getFileChannelIntegrationFooterDelimitedMissingLineNumber() {
		return fileChannelIntegrationFooterDelimitedMissingLineNumber;
	}

	public void setFileChannelIntegrationFooterDelimitedMissingLineNumber(
			String fileChannelIntegrationFooterDelimitedMissingLineNumber) {
		this.fileChannelIntegrationFooterDelimitedMissingLineNumber = fileChannelIntegrationFooterDelimitedMissingLineNumber;
	}

	public String getFileChannelIntegrationContentDelimitedAttributeNotFound() {
		return fileChannelIntegrationContentDelimitedAttributeNotFound;
	}

	public void setFileChannelIntegrationContentDelimitedAttributeNotFound(
			String fileChannelIntegrationContentDelimitedAttributeNotFound) {
		this.fileChannelIntegrationContentDelimitedAttributeNotFound = fileChannelIntegrationContentDelimitedAttributeNotFound;
	}

	public String getFileChannelIntegrationContentDelimitedMissingSegmentPos() {
		return fileChannelIntegrationContentDelimitedMissingSegmentPos;
	}

	public void setFileChannelIntegrationContentDelimitedMissingSegmentPos(
			String fileChannelIntegrationContentDelimitedMissingSegmentPos) {
		this.fileChannelIntegrationContentDelimitedMissingSegmentPos = fileChannelIntegrationContentDelimitedMissingSegmentPos;
	}

	public String getOperatorKey() {
		return operatorKey;
	}

	public void setOperatorKey(String operatorKey) {
		this.operatorKey = operatorKey;
	}

	public String getInvalidBatchSize() {
		return invalidBatchSize;
	}

	public void setInvalidBatchSize(String invalidBatchSize) {
		this.invalidBatchSize = invalidBatchSize;
	}

	public String getInvokebsExternalURL() {
		return invokebsExternalURL;
	}

	public void setInvokebsExternalURL(String invokebsExternalURL) {
		this.invokebsExternalURL = invokebsExternalURL;
	}

	public String getMergeExpectedNoInputBE() {
		return mergeExpectedNoInputBE;
	}

	public void setMergeExpectedNoInputBE(String mergeExpectedNoInputBE) {
		this.mergeExpectedNoInputBE = mergeExpectedNoInputBE;
	}

	public String getMergeExpectedInputChannelMismatch() {
		return mergeExpectedInputChannelMismatch;
	}

	public void setMergeExpectedInputChannelMismatch(String mergeExpectedInputChannelMismatch) {
		this.mergeExpectedInputChannelMismatch = mergeExpectedInputChannelMismatch;
	}

	public String getMergeUnassignedContextVariables() {
		return mergeUnassignedContextVariables;
	}

	public void setMergeUnassignedContextVariables(String mergeUnassignedContextVariables) {
		this.mergeUnassignedContextVariables = mergeUnassignedContextVariables;
	}

	public String getOutputFileLocation() {
		return outputFileLocation;
	}

	public void setOutputFileLocation(String outputFileLocation) {
		this.outputFileLocation = outputFileLocation;
	}

	public String getRestApiBasepath() {
		return restApiBasepath;
	}

	public void setRestApiBasepath(String restApiBasepath) {
		this.restApiBasepath = restApiBasepath;
	}

	public String getRestApiPort() {
		return restApiPort;
	}

	public void setRestApiPort(String restApiPort) {
		this.restApiPort = restApiPort;
	}

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public String getPropertyUsername() {
		return propertyUsername;
	}

	public void setPropertyUsername(String propertyUsername) {
		this.propertyUsername = propertyUsername;
	}

	public String getPropertyPassword() {
		return propertyPassword;
	}

	public void setPropertyPassword(String propertyPassword) {
		this.propertyPassword = propertyPassword;
	}

	public String getFileChannelIntegrationOutputFileName() {
		return fileChannelIntegrationOutputFileName;
	}

	public void setFileChannelIntegrationOutputFileName(String fileChannelIntegrationOutputFileName) {
		this.fileChannelIntegrationOutputFileName = fileChannelIntegrationOutputFileName;
	}

	public String getFileChannelIntegrationInputFileDuplicationDurationUnits() {
		return fileChannelIntegrationInputFileDuplicationDurationUnits;
	}

	public void setFileChannelIntegrationInputFileDuplicationDurationUnits(
			String fileChannelIntegrationInputFileDuplicationDurationUnits) {
		this.fileChannelIntegrationInputFileDuplicationDurationUnits = fileChannelIntegrationInputFileDuplicationDurationUnits;
	}

	public String getNoInputChannelOperator() {
		return noInputChannelOperator;
	}

	public void setNoInputChannelOperator(String noInputChannelOperator) {
		this.noInputChannelOperator = noInputChannelOperator;
	}

	public String getMergeWithFileInputBatchingError() {
		return mergeWithFileInputBatchingError;
	}

	public void setMergeWithFileInputBatchingError(String mergeWithFileInputBatchingError) {
		this.mergeWithFileInputBatchingError = mergeWithFileInputBatchingError;
	}

	public String getFileChannelIntegrationInputMappingNotFound() {
		return fileChannelIntegrationInputMappingNotFound;
	}

	public void setFileChannelIntegrationInputMappingNotFound(String fileChannelIntegrationInputMappingNotFound) {
		this.fileChannelIntegrationInputMappingNotFound = fileChannelIntegrationInputMappingNotFound;
	}

	public String getFileChannelIntegrationOutputMappingNotFound() {
		return fileChannelIntegrationOutputMappingNotFound;
	}

	public void setFileChannelIntegrationOutputMappingNotFound(String fileChannelIntegrationOutputMappingNotFound) {
		this.fileChannelIntegrationOutputMappingNotFound = fileChannelIntegrationOutputMappingNotFound;
	}

}