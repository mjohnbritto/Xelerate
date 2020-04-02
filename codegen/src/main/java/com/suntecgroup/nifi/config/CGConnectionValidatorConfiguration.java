package com.suntecgroup.nifi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "connection")
@PropertySource("${app.config.location}validation.properties")
@Component
public class CGConnectionValidatorConfiguration {

	// connection

	private String noConnection;

	private String connectionEmptySource;

	private String connectionNoOperatorFound;

	private String connectionEmptyDestination;

	private String connectionPresentAtSourceDestination;

	private String connectionInvalidDestination;

	private String connectionInvalidSource;

	private String startConnectionNotFound;

	private String endConnectionNotFound;
	
	private String endInvalidConnection;

	private String multipleJoinConnection;

	private String joinOutgoingConnection;

	private String joinIncomingConnection;

	private String connectionKey;

	private String connectionSmartOutputBEParameter;

	private String connectionSmartOutputBEType;

	private String connectionSmartUpdateSource;

	private String connectionSmartOutputParameterProcessVariable;

	private String connectionSmartNoProcessVariable;

	private String connectionSmartProcessVariableEmptyString;

	private String connectionSmartProcessVariableEmptyInit;

	private String connectionSmartPrecision_one;

	private String connectionSmartScale_one;

	private String connectionSmartInitValue_one;

	private String connectionSmartEmptyDate;

	private String connectionSmartOutputParameterInputBE;

	private String connectionSmartNoMapping;

	private String connectionSmartStringOutputParameter;

	private String connectionSmartInitOutputParameter;

	private String connectionSmartEmptyPrecision;

	private String connectionSmartEmptyScale;

	private String connectionSmartMismatchMetric;

	private String connectionSmartEmptyLinkProperties;

	private String connectionDecisionInputBeMisMatch_one;

	private String connectionDecisionNamesNotConnected;

	private String connectionDecisionNoDecisionNames;

	private String connectionDecisionNoIncomingConnection;

	private String connectionDecisionNoOutgoingConnection;

	private String connectionDecisionOnlyOneIncomingConnection;
	
	private String connectionInvokeBSMultipleSourceConnection;

	private String connectionInvokeBSMultipleDestinationConnection;

	private String connectionInvokeBSNotConnectedSource;

	private String connectionInvokeBSNotConnectedDestination;
	
	private String multipleMergeConnection;

	private String mergeOutgoingConnection;

	private String mergeIncomingConnection;
	
	private String mergeWithIBSStreaming;
	
	public String getNoConnection() {
		return noConnection;

	}

	public void setNoConnection(String noConnection) {
		this.noConnection = noConnection;
	}

	public String getConnectionEmptySource() {
		return connectionEmptySource;
	}

	public void setConnectionEmptySource(String connectionEmptySource) {
		this.connectionEmptySource = connectionEmptySource;
	}

	public String getConnectionNoOperatorFound() {
		return connectionNoOperatorFound;
	}

	public void setConnectionNoOperatorFound(String connectionNoOperatorFound) {
		this.connectionNoOperatorFound = connectionNoOperatorFound;
	}

	public String getConnectionEmptyDestination() {
		return connectionEmptyDestination;
	}

	public void setConnectionEmptyDestination(String connectionEmptyDestination) {
		this.connectionEmptyDestination = connectionEmptyDestination;
	}

	public String getConnectionPresentAtSourceDestination() {
		return connectionPresentAtSourceDestination;
	}

	public void setConnectionPresentAtSourceDestination(
			String connectionPresentAtSourceDestination) {
		this.connectionPresentAtSourceDestination = connectionPresentAtSourceDestination;
	}

	public String getConnectionInvalidDestination() {
		return connectionInvalidDestination;
	}

	public void setConnectionInvalidDestination(
			String connectionInvalidDestination) {
		this.connectionInvalidDestination = connectionInvalidDestination;
	}

	public String getConnectionInvalidSource() {
		return connectionInvalidSource;
	}

	public void setConnectionInvalidSource(String connectionInvalidSource) {
		this.connectionInvalidSource = connectionInvalidSource;
	}

	public String getStartConnectionNotFound() {
		return startConnectionNotFound;
	}

	public void setStartConnectionNotFound(String startConnectionNotFound) {
		this.startConnectionNotFound = startConnectionNotFound;
	}

	public String getEndConnectionNotFound() {
		return endConnectionNotFound;
	}

	public void setEndConnectionNotFound(String endConnectionNotFound) {
		this.endConnectionNotFound = endConnectionNotFound;
	}

	public String getMultipleJoinConnection() {
		return multipleJoinConnection;
	}

	public void setMultipleJoinConnection(String multipleJoinConnection) {
		this.multipleJoinConnection = multipleJoinConnection;
	}

	public String getJoinOutgoingConnection() {
		return joinOutgoingConnection;
	}

	public void setJoinOutgoingConnection(String joinOutgoingConnection) {
		this.joinOutgoingConnection = joinOutgoingConnection;
	}

	public String getJoinIncomingConnection() {
		return joinIncomingConnection;
	}

	public void setJoinIncomingConnection(String joinIncomingConnection) {
		this.joinIncomingConnection = joinIncomingConnection;
	}

	public String getConnectionKey() {
		return connectionKey;
	}

	public void setConnectionKey(String connectionKey) {
		this.connectionKey = connectionKey;
	}

	public String getConnectionSmartOutputBEParameter() {
		return connectionSmartOutputBEParameter;
	}

	public void setConnectionSmartOutputBEParameter(
			String connectionSmartOutputBEParameter) {
		this.connectionSmartOutputBEParameter = connectionSmartOutputBEParameter;
	}

	public String getConnectionSmartOutputBEType() {
		return connectionSmartOutputBEType;
	}

	public void setConnectionSmartOutputBEType(
			String connectionSmartOutputBEType) {
		this.connectionSmartOutputBEType = connectionSmartOutputBEType;
	}

	public String getConnectionSmartUpdateSource() {
		return connectionSmartUpdateSource;
	}

	public void setConnectionSmartUpdateSource(
			String connectionSmartUpdateSource) {
		this.connectionSmartUpdateSource = connectionSmartUpdateSource;
	}

	public String getConnectionSmartOutputParameterProcessVariable() {
		return connectionSmartOutputParameterProcessVariable;
	}

	public void setConnectionSmartOutputParameterProcessVariable(
			String connectionSmartOutputParameterProcessVariable) {
		this.connectionSmartOutputParameterProcessVariable = connectionSmartOutputParameterProcessVariable;
	}

	public String getConnectionSmartNoProcessVariable() {
		return connectionSmartNoProcessVariable;
	}

	public void setConnectionSmartNoProcessVariable(
			String connectionSmartNoProcessVariable) {
		this.connectionSmartNoProcessVariable = connectionSmartNoProcessVariable;
	}

	public String getConnectionSmartProcessVariableEmptyString() {
		return connectionSmartProcessVariableEmptyString;
	}

	public void setConnectionSmartProcessVariableEmptyString(
			String connectionSmartProcessVariableEmptyString) {
		this.connectionSmartProcessVariableEmptyString = connectionSmartProcessVariableEmptyString;
	}

	public String getConnectionSmartProcessVariableEmptyInit() {
		return connectionSmartProcessVariableEmptyInit;
	}

	public void setConnectionSmartProcessVariableEmptyInit(
			String connectionSmartProcessVariableEmptyInit) {
		this.connectionSmartProcessVariableEmptyInit = connectionSmartProcessVariableEmptyInit;
	}

	public String getConnectionSmartPrecision_one() {
		return connectionSmartPrecision_one;
	}

	public void setConnectionSmartPrecision_one(
			String connectionSmartPrecision_one) {
		this.connectionSmartPrecision_one = connectionSmartPrecision_one;
	}

	public String getConnectionSmartScale_one() {
		return connectionSmartScale_one;
	}

	public void setConnectionSmartScale_one(String connectionSmartScale_one) {
		this.connectionSmartScale_one = connectionSmartScale_one;
	}

	public String getConnectionSmartInitValue_one() {
		return connectionSmartInitValue_one;
	}

	public void setConnectionSmartInitValue_one(
			String connectionSmartInitValue_one) {
		this.connectionSmartInitValue_one = connectionSmartInitValue_one;
	}

	public String getConnectionSmartEmptyDate() {
		return connectionSmartEmptyDate;
	}

	public void setConnectionSmartEmptyDate(String connectionSmartEmptyDate) {
		this.connectionSmartEmptyDate = connectionSmartEmptyDate;
	}

	public String getConnectionSmartOutputParameterInputBE() {
		return connectionSmartOutputParameterInputBE;
	}

	public void setConnectionSmartOutputParameterInputBE(
			String connectionSmartOutputParameterInputBE) {
		this.connectionSmartOutputParameterInputBE = connectionSmartOutputParameterInputBE;
	}

	public String getConnectionSmartNoMapping() {
		return connectionSmartNoMapping;
	}

	public void setConnectionSmartNoMapping(String connectionSmartNoMapping) {
		this.connectionSmartNoMapping = connectionSmartNoMapping;
	}

	public String getConnectionSmartStringOutputParameter() {
		return connectionSmartStringOutputParameter;
	}

	public void setConnectionSmartStringOutputParameter(
			String connectionSmartStringOutputParameter) {
		this.connectionSmartStringOutputParameter = connectionSmartStringOutputParameter;
	}

	public String getConnectionSmartInitOutputParameter() {
		return connectionSmartInitOutputParameter;
	}

	public void setConnectionSmartInitOutputParameter(
			String connectionSmartInitOutputParameter) {
		this.connectionSmartInitOutputParameter = connectionSmartInitOutputParameter;
	}

	public String getConnectionSmartEmptyPrecision() {
		return connectionSmartEmptyPrecision;
	}

	public void setConnectionSmartEmptyPrecision(
			String connectionSmartEmptyPrecision) {
		this.connectionSmartEmptyPrecision = connectionSmartEmptyPrecision;
	}

	public String getConnectionSmartEmptyScale() {
		return connectionSmartEmptyScale;
	}

	public void setConnectionSmartEmptyScale(String connectionSmartEmptyScale) {
		this.connectionSmartEmptyScale = connectionSmartEmptyScale;
	}

	public String getConnectionSmartMismatchMetric() {
		return connectionSmartMismatchMetric;
	}

	public void setConnectionSmartMismatchMetric(
			String connectionSmartMismatchMetric) {
		this.connectionSmartMismatchMetric = connectionSmartMismatchMetric;
	}

	public String getConnectionSmartEmptyLinkProperties() {
		return connectionSmartEmptyLinkProperties;
	}

	public void setConnectionSmartEmptyLinkProperties(
			String connectionSmartEmptyLinkProperties) {
		this.connectionSmartEmptyLinkProperties = connectionSmartEmptyLinkProperties;
	}

	public String getConnectionDecisionInputBeMisMatch_one() {
		return connectionDecisionInputBeMisMatch_one;
	}

	public void setConnectionDecisionInputBeMisMatch_one(
			String connectionDecisionInputBeMisMatch_one) {
		this.connectionDecisionInputBeMisMatch_one = connectionDecisionInputBeMisMatch_one;
	}

	public String getConnectionDecisionNamesNotConnected() {
		return connectionDecisionNamesNotConnected;
	}

	public void setConnectionDecisionNamesNotConnected(
			String connectionDecisionNamesNotConnected) {
		this.connectionDecisionNamesNotConnected = connectionDecisionNamesNotConnected;
	}

	public String getConnectionDecisionNoDecisionNames() {
		return connectionDecisionNoDecisionNames;
	}

	public void setConnectionDecisionNoDecisionNames(
			String connectionDecisionNoDecisionNames) {
		this.connectionDecisionNoDecisionNames = connectionDecisionNoDecisionNames;
	}

	public String getConnectionDecisionNoIncomingConnection() {
		return connectionDecisionNoIncomingConnection;
	}

	public void setConnectionDecisionNoIncomingConnection(
			String connectionDecisionNoIncomingConnection) {
		this.connectionDecisionNoIncomingConnection = connectionDecisionNoIncomingConnection;
	}

	public String getConnectionDecisionNoOutgoingConnection() {
		return connectionDecisionNoOutgoingConnection;
	}

	public void setConnectionDecisionNoOutgoingConnection(
			String connectionDecisionNoOutgoingConnection) {
		this.connectionDecisionNoOutgoingConnection = connectionDecisionNoOutgoingConnection;
	}

	public String getConnectionInvokeBSMultipleSourceConnection() {
		return connectionInvokeBSMultipleSourceConnection;
	}

	public void setConnectionInvokeBSMultipleSourceConnection(
			String connectionInvokeBSMultipleSourceConnection) {
		this.connectionInvokeBSMultipleSourceConnection = connectionInvokeBSMultipleSourceConnection;
	}

	public String getConnectionInvokeBSMultipleDestinationConnection() {
		return connectionInvokeBSMultipleDestinationConnection;
	}

	public void setConnectionInvokeBSMultipleDestinationConnection(
			String connectionInvokeBSMultipleDestinationConnection) {
		this.connectionInvokeBSMultipleDestinationConnection = connectionInvokeBSMultipleDestinationConnection;
	}

	public String getConnectionInvokeBSNotConnectedSource() {
		return connectionInvokeBSNotConnectedSource;
	}

	public void setConnectionInvokeBSNotConnectedSource(
			String connectionInvokeBSNotConnectedSource) {
		this.connectionInvokeBSNotConnectedSource = connectionInvokeBSNotConnectedSource;
	}

	public String getConnectionInvokeBSNotConnectedDestination() {
		return connectionInvokeBSNotConnectedDestination;
	}

	public void setConnectionInvokeBSNotConnectedDestination(
			String connectionInvokeBSNotConnectedDestination) {
		this.connectionInvokeBSNotConnectedDestination = connectionInvokeBSNotConnectedDestination;
	}

	public String getMultipleMergeConnection() {
		return multipleMergeConnection;
	}

	public void setMultipleMergeConnection(String multipleMergeConnection) {
		this.multipleMergeConnection = multipleMergeConnection;
	}

	public String getMergeOutgoingConnection() {
		return mergeOutgoingConnection;
	}

	public void setMergeOutgoingConnection(String mergeOutgoingConnection) {
		this.mergeOutgoingConnection = mergeOutgoingConnection;
	}

	public String getMergeIncomingConnection() {
		return mergeIncomingConnection;
	}

	public void setMergeIncomingConnection(String mergeIncomingConnection) {
		this.mergeIncomingConnection = mergeIncomingConnection;
	}

	public String getEndInvalidConnection() {
		return endInvalidConnection;
	}

	public void setEndInvalidConnection(String endInvalidConnection) {
		this.endInvalidConnection = endInvalidConnection;
	}

	public String getMergeWithIBSStreaming() {
		return mergeWithIBSStreaming;
	}

	public void setMergeWithIBSStreaming(String mergeWithIBSStreaming) {
		this.mergeWithIBSStreaming = mergeWithIBSStreaming;
	}

	public String getConnectionDecisionOnlyOneIncomingConnection() {
		return connectionDecisionOnlyOneIncomingConnection;
	}

	public void setConnectionDecisionOnlyOneIncomingConnection(String connectionDecisionOnlyOneIncomingConnection) {
		this.connectionDecisionOnlyOneIncomingConnection = connectionDecisionOnlyOneIncomingConnection;
	}

}