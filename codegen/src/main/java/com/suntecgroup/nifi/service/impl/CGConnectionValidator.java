package com.suntecgroup.nifi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.nifi.config.CGConnectionValidatorConfiguration;
import com.suntecgroup.nifi.config.CGValidatorConfigurationProperty;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.frontend.bean.BusinessEntity;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.BusinessSettings;
import com.suntecgroup.nifi.frontend.bean.Connection;
import com.suntecgroup.nifi.frontend.bean.Decisions;
import com.suntecgroup.nifi.frontend.bean.LinkProperties;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.frontend.bean.Property;
import com.suntecgroup.nifi.frontend.bean.VisualMapping;

public class CGConnectionValidator {

	private final Logger logger = LoggerFactory.getLogger(CGConnectionValidator.class);

	CGValidatorConfigurationProperty validatorProperty;
	CGConnectionValidatorConfiguration connValidatorProperty;

	private Map<String, String> addError(final String category, final String source, final String description) {

		Map<String, String> errorMap = new LinkedHashMap<String, String>();
		errorMap.put(CGConstants.ERROR_CATEGORY, category);
		errorMap.put(CGConstants.ERROR_SOURCE, source);
		errorMap.put(CGConstants.ERROR_DESCRIPTION, description);
		return errorMap;
	}

	public List<Map<String, String>> validateConnections(final BPFlowUI bpflowUI, Map<String, String> operatorKeyType,
			CGValidatorConfigurationProperty validatorProperty,
			CGConnectionValidatorConfiguration connValidatorProperty) {

		this.validatorProperty = validatorProperty;
		this.connValidatorProperty = connValidatorProperty;

		List<Map<String, String>> errorList = new LinkedList<Map<String, String>>();
		Map<String, String> opKeyNameMap = new HashMap<String, String>();
		List<Connection> connectorsObject = bpflowUI.getConnections();
		int noSrcCount = 0;
		int endCount = 0;
		List<String> smartConnectorNames = new ArrayList<String>();

		for (Operators op : bpflowUI.getOperators()) {
			opKeyNameMap.put(op.getKey(), op.getName());
		}

		try {
			if (connectorsObject != null) {
				for (int i = 0; i < connectorsObject.size(); i++) {

					Connection connectorsObj = connectorsObject.get(i);
					String sourceName = null;
					String destinationName = null;
					String sourceType = null;
					String desType = null;

					if (isEmpty(connectorsObj.getUi_attributes().getSourceName())) {
						errorList.add(addError(CGConstants.CONNECTIONS, connectorsObj.getUi_attributes().getKey(),
								"Connection '" + connectorsObj.getUi_attributes().getKey() + "' "
										+ connValidatorProperty.getConnectionEmptySource()));
					} else {
						sourceName = connectorsObj.getUi_attributes().getSourceName().toUpperCase();
						sourceType = operatorKeyType.get(sourceName);
						if (isEmpty(sourceType)) {
							errorList.add(addError(CGConstants.CONNECTIONS, opKeyNameMap.get(sourceName),
									connValidatorProperty.getConnectionNoOperatorFound() + " "
											+ opKeyNameMap.get(sourceName)));
						} else {
							if (sourceType.contains("output")) {
								errorList.add(addError(CGConstants.CONNECTIONS, opKeyNameMap.get(sourceName),
										opKeyNameMap.get(sourceName) + " "
												+ connValidatorProperty.getConnectionInvalidSource()));
								errorList.add(addError(CGConstants.OPERATOR, opKeyNameMap.get(sourceName),
										opKeyNameMap.get(sourceName) + " "
												+ connValidatorProperty.getConnectionInvalidSource()));
							}
						}
					}

					if (isEmpty(connectorsObj.getUi_attributes().getDestinationName())) {
						errorList.add(addError(CGConstants.CONNECTIONS, connectorsObj.getUi_attributes().getKey(),
								"Connection '" + connectorsObj.getUi_attributes().getKey() + "' "
										+ connValidatorProperty.getConnectionEmptyDestination()));

					} else {
						destinationName = connectorsObj.getUi_attributes().getDestinationName().toUpperCase();
						desType = operatorKeyType.get(destinationName);
						if (isEmpty(desType)) {
							errorList.add(addError(CGConstants.CONNECTIONS, opKeyNameMap.get(destinationName),
									connValidatorProperty.getConnectionNoOperatorFound() + " "
											+ opKeyNameMap.get(destinationName)));
						} else {
							if (desType.contains("input")) {
								errorList.add(addError(CGConstants.CONNECTIONS, opKeyNameMap.get(destinationName),
										opKeyNameMap.get(destinationName) + " "
												+ connValidatorProperty.getConnectionInvalidDestination()));
								errorList.add(addError(CGConstants.OPERATOR, opKeyNameMap.get(destinationName),
										opKeyNameMap.get(destinationName) + " "
												+ connValidatorProperty.getConnectionInvalidDestination()));
							}
						}
					}

					// Start

					noSrcCount = validateStartConnection(noSrcCount, destinationName, sourceType, desType, errorList);

					// End
					endCount = validateEndConnection(endCount, sourceName, destinationName, sourceType, desType,
							errorList);

					// Smart Connector Validation
					if (isEmpty(connectorsObj.getUi_attributes().getKey())) {
						errorList.add(
								addError(CGConstants.CONNECTIONS, "Key", connValidatorProperty.getConnectionKey()));

					} else {
						if (CGConstants.SMARTCONNECTOR.equals(connectorsObj.getUi_attributes().getType())) {
							if (smartConnectorNames.contains(connectorsObj.getUi_attributes().getKey())) {
								errorList.add(addError(CGConstants.CONNECTIONS, CGConstants.SMART_CONNECTOR_CATEGORY,
										"Smart Connector with duplicate name \""
												+ connectorsObj.getUi_attributes().getKey() + "\" found"));
							} else {
								smartConnectorNames.add(connectorsObj.getUi_attributes().getKey());
							}
							smartConnectorValidation(connectorsObj, bpflowUI, errorList);
						}
					}

				}

				// Start
				if (noSrcCount < 1) {
					errorList.add(addError(CGConstants.CONNECTIONS, CGConstants.START_CONNECTION,
							connValidatorProperty.getStartConnectionNotFound()));
				}

				// End
				if (endCount < 1) {
					errorList.add(addError(CGConstants.CONNECTIONS, CGConstants.END_CONNECTION,
							connValidatorProperty.getEndConnectionNotFound()));
				}

				// Join
				validateJoinConnection(bpflowUI, connectorsObject, operatorKeyType, errorList);

				// DECISION MATRIX
				validateDecisionMatrix(bpflowUI, connectorsObject, operatorKeyType, errorList);

				// InvokeConnection
				invokeConnectionValidation(connectorsObject, operatorKeyType, errorList);

				// Merge
				validateMergeConnection(bpflowUI, connectorsObject, operatorKeyType, errorList);

			}

		} catch (Exception e) {
			logger.error("Exception occured while  validating connections ", e.getMessage(), e);
		}
		return errorList;

	}

	private int validateStartConnection(int noSrcCount, String destinationName, String sourceType, String desType,
			List<Map<String, String>> errorList) {

		isBothSourceDestination(destinationName, sourceType, desType, "START", CGConstants.START_CONNECTION, errorList);

		if (sourceType != null && CGConstants.START.equals(sourceType.toLowerCase().trim())) {
			noSrcCount++;
		}
		return noSrcCount;
	}

	private int validateEndConnection(int endCount, String sourceName, String destinationName, String sourceType,
			String desType, List<Map<String, String>> errorList) {
		isBothSourceDestination(destinationName, sourceType, desType, "END", CGConstants.END_CONNECTION, errorList);

		if (desType != null) {
			if (CGConstants.END.equals(desType.toLowerCase().trim())) {
				endCount++;
			}

			if (!isEmpty(sourceType) && CGConstants.END.equals(sourceType)) {
				int count = 0;
				for (String channel : CGConstants.CHANNEL_OPERATORS_TYPES) {
					if (channel.equals(desType.toLowerCase())) {
						count++;
						break;
					}
				}
				if (count == 0) {
					errorList.add(addError(CGConstants.CONNECTIONS, CGConstants.END_CONNECTION,
							connValidatorProperty.getEndInvalidConnection()));
				}
			}
		}

		return endCount;
	}

	private void validateJoinConnection(BPFlowUI bpflowUI, List<Connection> connectorsObject,
			Map<String, String> operatorKeyType, List<Map<String, String>> errorList) {

		Iterator<Entry<String, String>> iter = operatorKeyType.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry<String, String> nameType = (Entry<String, String>) iter.next();
			String operatorName = nameType.getKey();
			String operatorType = nameType.getValue();

			if (operatorType.toLowerCase().equals(CGConstants.JOIN)) {
				int sourceCnt = 0;
				int destCnt = 0;

				if (connectorsObject != null && connectorsObject.size() > 0) {
					for (Connection con : connectorsObject) {
						String sourceName = con.getUi_attributes().getSourceName();
						String destinationName = con.getUi_attributes().getDestinationName();
						String sourceType = operatorKeyType.get(sourceName);
						String desType = operatorKeyType.get(destinationName);

						if (sourceType != null && CGConstants.JOIN.toLowerCase().equals(sourceType.toLowerCase().trim())
								&& operatorName.toLowerCase().equals(sourceName.toLowerCase())) {
							sourceCnt++;
						}

						if (desType != null && CGConstants.JOIN.toLowerCase().equals(desType.toLowerCase().trim())
								&& operatorName.toLowerCase().equals(destinationName.toLowerCase())) {
							destCnt++;

						}
					}
				}

				if (sourceCnt > 1) {
					errorList.add(addError(CGConstants.CONNECTIONS, "Join",
							operatorName + " " + connValidatorProperty.getMultipleJoinConnection()));
				}
				if (sourceCnt == 0) {
					errorList.add(addError(CGConstants.CONNECTIONS, "Join",
							operatorName + " " + connValidatorProperty.getJoinOutgoingConnection()));
				}

				if (destCnt < 1) {
					errorList.add(addError(CGConstants.CONNECTIONS, "Join",
							operatorName + " " + connValidatorProperty.getJoinIncomingConnection()));
				}
			}
		}
	}

	private void validateMergeConnection(BPFlowUI bpflowUI, List<Connection> connectorsObject,
			Map<String, String> operatorKeyType, List<Map<String, String>> errorList) {

		Iterator<Entry<String, String>> iter = operatorKeyType.entrySet().iterator();
		Map<String, Operators> operatorsMap = new HashMap<String, Operators>();
		boolean hasIBSwithStreaming = false;
		boolean hasMerge = false;

		for (Operators operator : bpflowUI.getOperators()) {
			operatorsMap.put(operator.getKey(), operator);
		}

		while (iter.hasNext()) {
			Map.Entry<String, String> nameType = (Entry<String, String>) iter.next();
			String operatorKey = nameType.getKey();
			String operatorType = nameType.getValue();

			if (operatorType.toLowerCase().equals(CGConstants.MERGE)) {
				int sourceCnt = 0;
				int destCnt = 0;

				if (connectorsObject != null && connectorsObject.size() > 0) {
					for (Connection con : connectorsObject) {
						String sourceName = con.getUi_attributes().getSourceName();
						String destinationName = con.getUi_attributes().getDestinationName();
						String sourceType = operatorKeyType.get(sourceName);
						String desType = operatorKeyType.get(destinationName);

						if (sourceType != null
								&& CGConstants.MERGE.toLowerCase().equals(sourceType.toLowerCase().trim())
								&& operatorKey.toLowerCase().equals(sourceName.toLowerCase())) {
							sourceCnt++;
						}

						if (desType != null && CGConstants.MERGE.toLowerCase().equals(desType.toLowerCase().trim())
								&& operatorKey.toLowerCase().equals(destinationName.toLowerCase())) {
							destCnt++;
						}
					}
				}

				if (sourceCnt > 1) {
					errorList.add(addError(CGConstants.CONNECTIONS, "Merge",
							operatorKey + " " + connValidatorProperty.getMultipleMergeConnection()));
				}
				if (sourceCnt == 0) {
					errorList.add(addError(CGConstants.CONNECTIONS, "Merge",
							operatorKey + " " + connValidatorProperty.getMergeOutgoingConnection()));
				}

				if (destCnt < 2) {
					errorList.add(addError(CGConstants.CONNECTIONS, "Merge",
							operatorKey + " " + connValidatorProperty.getMergeIncomingConnection()));
				}
			}

			// check if IBS internal and merge present on a flow
			if (CGConstants.INVOKE_BS.equals(operatorType)
					&& operatorsMap.get(operatorKey).getBusinessSettings().isSupportStreaming()) {
				hasIBSwithStreaming = true;
			}

			if (CGConstants.MERGE.equals(operatorType)) {
				hasMerge = true;
			}
		}

		if (hasIBSwithStreaming && hasMerge) {
			// pick a merge and check for previous operator until a merge or
			// start operator
			List<Connection> connList = bpflowUI.getConnections();
			Iterator<Entry<String, Operators>> it = operatorsMap.entrySet().iterator();
			List<String> nextStageOp = null;
			List<String> currentStageOp = null;
			String sourceOp;
			boolean _continue = true;

			while (it.hasNext()) {
				Operators operator = it.next().getValue();
				if (CGConstants.MERGE.equals(operator.getType())) {
					nextStageOp = new ArrayList<String>();
					nextStageOp.add(operator.getKey());
					do {
						currentStageOp = new ArrayList<String>(nextStageOp);
						nextStageOp = new ArrayList<String>();

						for (String opKey : currentStageOp) {
							// for each op on that stage
							Operators op = operatorsMap.get(opKey);

							for (Connection conn : connList) {
								if (op.getKey().equals(conn.getUi_attributes().getDestinationName())) {
									sourceOp = conn.getUi_attributes().getSourceName();
									if (!nextStageOp.contains(sourceOp)) {
										if (!CGConstants.START.equals(operatorsMap.get(sourceOp).getType())
												&& !CGConstants.MERGE.equals(operatorsMap.get(sourceOp).getType())) {
											nextStageOp.add(sourceOp);
										}
									}
								}
							}

							if (CGConstants.INVOKE_BS.equals(op.getType())) {
								if (op.getBusinessSettings().isSupportStreaming()) {
									errorList.add(addError(CGConstants.CONNECTIONS, "Merge",
											connValidatorProperty.getMergeWithIBSStreaming()));
									_continue = false;
									break;
								}
							}
						}
					} while (nextStageOp.size() > 0 && _continue);
				}
			}
		}
	}

	private void isBothSourceDestination(String destinationName, String sourceType, String desType,
			String ConnectionType, String source, List<Map<String, String>> errorList) {
		if ((sourceType != null && sourceType.toUpperCase().equals(ConnectionType))
				&& (desType != null && desType.toUpperCase().equals(ConnectionType))) {
			errorList.add(addError(CGConstants.CONNECTIONS, source,
					destinationName + " " + connValidatorProperty.getConnectionPresentAtSourceDestination()));

		}
	}

	private void smartConnectorValidation(Connection connectorObj, BPFlowUI bpflowUI,
			List<Map<String, String>> errorList) {
		LinkProperties linkPropertiesObj = connectorObj.getLink_properties();

		if (linkPropertiesObj != null) {

			validateSmartBusinessSettings(connectorObj, bpflowUI, errorList);
			validateSmartProperty(connectorObj.getUi_attributes().getKey(), linkPropertiesObj.getProperties(),
					errorList);
		} else {
			errorList.add(addError(CGConstants.SMART_CONNECTOR_CATEGORY, "Link Properties",
					connValidatorProperty.getConnectionSmartEmptyLinkProperties() + " "
							+ connectorObj.getUi_attributes().getKey()));
		}
		
		List<VisualMapping> mapping = connectorObj.getLink_properties().getMapping();
		if (mapping.size() < 1) {
			errorList.add(addError(CGConstants.SMART_CONNECTOR_CATEGORY, CGConstants.MAPPING,
					connValidatorProperty.getConnectionSmartNoMapping() + " " + connectorObj.getUi_attributes().getKey()));
		}
	}

	private void validateSmartBusinessSettings(Connection connectorObj, BPFlowUI bpflowUI,
			List<Map<String, String>> errorList) {

		BusinessSettings businessSettingObj = connectorObj.getLink_properties().getBusinessSettings();

		if (isEmpty(businessSettingObj.getInputBeType())) {
			errorList.add(addError(CGConstants.SMART_CONNECTOR_CATEGORY, CGConstants.BUSINESS_SETTINGS,
					validatorProperty.getInputBeType() + " " + connectorObj.getUi_attributes().getKey()));
		} else {
			validateInputBE(connectorObj, bpflowUI, connectorObj.getUi_attributes().getKey(),
					CGConstants.SMART_CONNECTOR_CATEGORY, "Input BE", errorList);
		}
		if (isEmpty(businessSettingObj.getOutputBeType())) {
			errorList.add(addError(CGConstants.SMART_CONNECTOR_CATEGORY, CGConstants.BUSINESS_SETTINGS,
					validatorProperty.getOutputBeType() + " " + connectorObj.getUi_attributes().getKey()));
		} else {
			validateOutputBE(connectorObj, bpflowUI, connectorObj.getUi_attributes().getKey(),
					CGConstants.SMART_CONNECTOR_CATEGORY, "Output BE", errorList);
		}

		List<String> outputBukAttributes = businessSettingObj.getOutputBEBUKAttributes();

		if (outputBukAttributes == null || outputBukAttributes.size() < 1) {
			errorList.add(addError(CGConstants.SMART_CONNECTOR_CATEGORY, CGConstants.BUSINESS_SETTINGS,
					validatorProperty.getOutputBUK() + " " + connectorObj.getUi_attributes().getKey()));
		}

	}

	private void validateInputBE(Connection connectorObj, BPFlowUI bpflowUI, String connectorName, String category,
			String source, List<Map<String, String>> errorList) {
		BusinessEntity businessEntityObject = (BusinessEntity) connectorObj.getLink_properties().getBusinessSettings()
				.getInputBe();
		if (null != businessEntityObject) {
			validateBEDefinition(connectorObj, bpflowUI, businessEntityObject, connectorName, category, source,
					errorList);
		}
	}

	private void validateOutputBE(Connection connectorObj, BPFlowUI bpflowUI, String connectorName, String category,
			String source, List<Map<String, String>> errorList) {
		BusinessEntity businessEntityObject = (BusinessEntity) connectorObj.getLink_properties().getBusinessSettings()
				.getOutputBe();
		if (null != businessEntityObject) {
			validateBEDefinition(connectorObj, bpflowUI, businessEntityObject, connectorName, category, source,
					errorList);
		}
	}

	private void validateBEDefinition(Connection connectorObj, BPFlowUI bpflowUI, BusinessEntity businessEntityObject,
			String connectorName, String category, String source, List<Map<String, String>> errorList) {

		if (isEmpty(String.valueOf(businessEntityObject.getContext().getArtifactId()))) {

			errorList.add(
					addError(category, source, validatorProperty.getBeDefinitionArtifactId() + " " + connectorName));
		}
		if (isEmpty(businessEntityObject.getName())) {

			errorList.add(addError(category, source, validatorProperty.getBeDefinitionName() + " " + connectorName));
		}
		if (isEmpty(businessEntityObject.getContext().getDepartment())) {

			errorList.add(
					addError(category, source, validatorProperty.getBeDefinitionDepartment() + " " + connectorName));
		}
		if (isEmpty(businessEntityObject.getContext().getModule())) {

			errorList.add(addError(category, source, validatorProperty.getBeDefinitionModule() + " " + connectorName));
		}
		if (isEmpty(businessEntityObject.getContext().getRelease())) {

			errorList.add(addError(category, source, validatorProperty.getBeDefinitionRelease() + " " + connectorName));
		}

	}

	private void validateSmartProperty(String connectorName, List<Property> propObj,
			List<Map<String, String>> errorList) {

		if (propObj != null && propObj.size() > 0) {
			for (Property property : propObj) {
				validateProperty(property, connectorName, CGConstants.SMART_CONNECTOR_CATEGORY, errorList);

			}
		} else {
			errorList.add(addError(CGConstants.SMART_CONNECTOR_CATEGORY, CGConstants.PROPERTY,
					validatorProperty.getNoPropertyFound() + " " + connectorName));
		}

	}

	private void validateDecisionMatrix(BPFlowUI bpflowUI, List<Connection> connectorsObject,
			Map<String, String> operatorKeyType, List<Map<String, String>> errorList) {

		List<Operators> operatorObjList = bpflowUI.getOperators();
		// Fetching Decision Names from Decision Matrix Operator
		Map<String, String> operatorInputBEType = new LinkedHashMap<String, String>();
		Map<String, String> operatorOutputBEType = new LinkedHashMap<String, String>();

		// This loop is mandatory. I need to know both decision matrix
		// InputBetype and the
		// rhs operator InputBetype for further validation.

		for (Operators operatorObj : operatorObjList) {

			// Store InputBEType for All Operators

			if (operatorObj.getBusinessSettings() != null
					&& operatorObj.getBusinessSettings().getInputBeType() != null) {
				operatorInputBEType.put(operatorObj.getKey().toUpperCase(),
						operatorObj.getBusinessSettings().getInputBeType());
			}

			if (operatorObj.getBusinessSettings() != null
					&& operatorObj.getBusinessSettings().getOutputBeType() != null) {
				operatorOutputBEType.put(operatorObj.getKey().toUpperCase(),
						operatorObj.getBusinessSettings().getOutputBeType());

			}

		}

		for (Operators operatorObj : operatorObjList) {

			if (operatorObj.getType().equals(CGConstants.DECISION_MATRIX_EXCLUSIVE)) {
				decisionMatrixCheck(connectorsObject, operatorInputBEType, operatorOutputBEType, operatorObj,
						CGConstants.DECISION_MATRIX_EXCLUSIVE_CATEGORY, errorList);
			}

			if (operatorObj.getType().equals(CGConstants.DECISION_MATRIX_INCLUSIVE)) {
				decisionMatrixCheck(connectorsObject, operatorInputBEType, operatorOutputBEType, operatorObj,
						CGConstants.DECISION_MATRIX_INCLUSIVE_CATEGORY, errorList);
			}
		}

	}

	private void decisionMatrixCheck(List<Connection> connectorsObject, Map<String, String> operatorInputBEType,
			Map<String, String> operatorOutputBEType, Operators operatorObj, String source,
			List<Map<String, String>> errorList) {

		String operatorName = operatorObj.getKey().toUpperCase();
		List<Decisions> decisionList = operatorObj.getBusinessSettings().getDecisions();

		String desicionInputBeType = operatorObj.getBusinessSettings().getInputBeType();

		if (decisionList != null && decisionList.size() > 0) {
			List<String> decisionNames = new ArrayList<String>();

			// Lets assume, we have 2 decisionName Decision1 & Decision2
			for (Decisions decOb : decisionList) {
				if (!isEmpty(decOb.getDecisionName())) {
					decisionNames.add(decOb.getDecisionName());
				}
			}

			List<String> decisionNamesTemp = new ArrayList<String>();
			decisionNamesTemp.addAll(decisionNames);
			int count = decisionNames.size();
			int decisionSource = 0;
			int decisionDestination = 0;

			for (Connection con : connectorsObject) {

				if (operatorName.equals(con.getUi_attributes().getSourceName().toUpperCase())) {
					decisionSource++;
				}
				if (operatorName.equals(con.getUi_attributes().getDestinationName().toUpperCase())) {
					decisionDestination++;
				}

				if (!isEmpty(con.getUi_attributes().getSourceName())
						&& con.getUi_attributes().getSourceName().toUpperCase().equals(operatorName)) {

					if (!isEmpty(con.getUi_attributes().getKey())) {
						if (!StringUtils.isBlank(con.getUi_attributes().getType())
								&& con.getUi_attributes().getType().toUpperCase().contains("SMART")) {
							// If SMART Connector, dont add the input BE type
							// check
							for (String decisionName : decisionNames) {
								if (decisionName.equals(con.getUi_attributes().getDecisionName())) {
									decisionNamesTemp.remove(decisionName);
									count--;
								}
							}

						} else {
							// if not smart connector
							for (String decisionName : decisionNames) {
								if (decisionName.equals(con.getUi_attributes().getDecisionName())) {
									decisionNamesTemp.remove(decisionName);
									count--;
								}
							}

							String destinationName = con.getUi_attributes().getDestinationName().toUpperCase();
							String destinationOperatorInputBEType = (String) operatorInputBEType.get(destinationName);
							if (destinationName.trim().contains("END")) {
								destinationOperatorInputBEType = (String) operatorOutputBEType.get(destinationName);
							}

						}
					}

				}

			}

			if (decisionSource < 1) {
				errorList.add(addError(CGConstants.CONNECTIONS, source,
						operatorName + " " + connValidatorProperty.getConnectionDecisionNoOutgoingConnection()));
			}

			if (decisionDestination > 1) {
				errorList.add(addError(CGConstants.CONNECTIONS, source,
						operatorName + " " + connValidatorProperty.getConnectionDecisionOnlyOneIncomingConnection()));
			} else if (decisionDestination < 1) {
				errorList.add(addError(CGConstants.CONNECTIONS, source,
						operatorName + " " + connValidatorProperty.getConnectionDecisionNoIncomingConnection()));
			}

			if (count > 0 && decisionNamesTemp != null) {
				errorList.add(addError(CGConstants.CONNECTIONS, source,
						"Decision name "
								+ Arrays.toString(decisionNamesTemp.toArray()).replace("[", "").replace("]", "") + " "
								+ connValidatorProperty.getConnectionDecisionNamesNotConnected() + "" + operatorName));
			}

		} else {
			errorList.add(addError(CGConstants.CONNECTIONS, source,
					connValidatorProperty.getConnectionDecisionNoDecisionNames() + " " + operatorName));
		}
	}

	private void invokeConnectionValidation(List<Connection> connectorsObject, Map<String, String> operatorKeyType,
			List<Map<String, String>> errorList) {

		Iterator<Entry<String, String>> iter = operatorKeyType.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry<String, String> nameType = (Entry<String, String>) iter.next();
			String operatorName = nameType.getKey();
			String operatorType = nameType.getValue();

			if (operatorType.toLowerCase().equals(CGConstants.INVOKE_BS)) {
				invokeConnection(connectorsObject, operatorName, operatorKeyType, errorList);
			}
		}

	}

	private void invokeConnection(List<Connection> connections, String operatorName,
			Map<String, String> operatorKeyType, List<Map<String, String>> errorList) {
		int sourceCount = 0;
		int destinationCount = 0;

		if (connections != null && connections.size() > 0) {
			for (Connection con : connections) {
				String sourceName = con.getUi_attributes().getSourceName();
				String destinationName = con.getUi_attributes().getDestinationName();
				String sourceType = operatorKeyType.get(sourceName);
				String desType = operatorKeyType.get(destinationName);

				if (sourceType != null && sourceType.equals(CGConstants.INVOKE_BS)
						&& sourceName.toUpperCase().equals(operatorName.toUpperCase())) {
					sourceCount++;
				}

				if (desType != null && desType.equals(CGConstants.INVOKE_BS)
						&& destinationName.toUpperCase().equals(operatorName.toUpperCase())) {
					destinationCount++;
				}

			}
		}

		if (destinationCount > 1) {
			errorList.add(addError(CGConstants.CONNECTIONS, CGConstants.INVOKEBS_CONNECTION,
					operatorName + " " + connValidatorProperty.getConnectionInvokeBSMultipleDestinationConnection()));

		}

		else if (destinationCount == 0) {
			errorList.add(addError(CGConstants.CONNECTIONS, CGConstants.INVOKEBS_CONNECTION,
					operatorName + " " + connValidatorProperty.getConnectionInvokeBSNotConnectedDestination()));

		}

		if (sourceCount > 2) {
			errorList.add(addError(CGConstants.CONNECTIONS, CGConstants.INVOKEBS_CONNECTION,
					operatorName + " " + connValidatorProperty.getConnectionInvokeBSMultipleSourceConnection()));

		} else if (sourceCount == 0) {

			errorList.add(addError(CGConstants.CONNECTIONS, CGConstants.INVOKEBS_CONNECTION,
					operatorName + " " + connValidatorProperty.getConnectionInvokeBSNotConnectedSource()));

		}

	}

	private boolean isEmpty(String value) {
		boolean result = false;
		if (value == null || "".equals(value.trim())) {
			result = true;
		}
		return result;
	}

	private void validateProperty(Property propertiesObj, String operatorName, String operatorAction,
			List<Map<String, String>> errorList) {

		if (isEmpty(propertiesObj.getName())) {
			errorList.add(addError(operatorAction, CGConstants.PROPERTY,
					validatorProperty.getPropertyName() + " " + operatorName));
		}

		if (propertiesObj.isMandatory() && isEmpty(propertiesObj.getValue())) {

			if ("penaltyDuration".toLowerCase().equals(propertiesObj.getName().toLowerCase())) {
				errorList.add(
						addError(operatorAction, CGConstants.PROPERTY, validatorProperty.getPropertyPenaltyDuration()));
			}

			else if ("yieldDuration".toLowerCase().equals(propertiesObj.getName().toLowerCase())) {
				errorList.add(
						addError(operatorAction, CGConstants.PROPERTY, validatorProperty.getPropertyYieldDuration()));
			}

			else if ("concurrentTasks".toLowerCase().equals(propertiesObj.getName().toLowerCase())) {
				errorList.add(
						addError(operatorAction, CGConstants.PROPERTY, validatorProperty.getPropertyConcurrentTasks()));
			}
		}
	}

}