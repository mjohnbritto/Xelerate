package com.suntecgroup.bp.designer.review.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.frontend.beans.BusinessSettings;
import com.suntecgroup.bp.designer.frontend.beans.Connection;
import com.suntecgroup.bp.designer.frontend.beans.LinkProperties;
import com.suntecgroup.bp.designer.frontend.beans.UIAttributes;
import com.suntecgroup.bp.designer.model.Fields;
import com.suntecgroup.bp.designer.model.ReviewReportResponse;
import com.suntecgroup.bp.designer.services.impl.BPDesignerServiceImpl;
import com.suntecgroup.bp.util.BPConstant;

public class SmartConnectorReport {
	private static final Logger LOGGER = LoggerFactory.getLogger(SmartConnectorReport.class);

	/**
	 * This method will check for addtion/deletion/modifications of smart
	 * connector
	 */
	public static List<ReviewReportResponse> smartConnectorComparision(List<Connection> sourceConnections,
			List<Connection> targetConnections) {
		List<ReviewReportResponse> operatorList = new ArrayList<>();
		List<Fields> fieldsList = new ArrayList<>();
		try {
			ArrayList<Connection> deletedSmartConnector = new ArrayList<>();
			boolean isConnectionDeleted;
			boolean isConnectionAdded;
			for (Connection sourceConnection : sourceConnections) {
				isConnectionDeleted = true;
				if (StringUtils.equalsIgnoreCase(sourceConnection.getUiAttributes().getType(), BPConstant.SMART)) {
					for (Connection targetConnection : targetConnections) {
						if (StringUtils.equalsIgnoreCase(sourceConnection.getUiAttributes().getKey(),
								targetConnection.getUiAttributes().getKey())) {
							isConnectionDeleted = false;
							break;
						}
					}
					if (isConnectionDeleted) {
						deletedSmartConnector.add(sourceConnection);
					}
				}
			}
			if (!deletedSmartConnector.isEmpty()) {
				for (Connection sourceConnection : deletedSmartConnector) {
					fieldsList = smartConnectorLinkPropertyCheck(sourceConnection.getLinkProperties(),
							new LinkProperties(), false, true);
					smartConnectorAttributeCheck(sourceConnection.getUiAttributes(), new UIAttributes(), false, true,
							fieldsList);
					if (!fieldsList.isEmpty()) {
						ReviewReportResponse operators = new ReviewReportResponse();
						operators.setKey(sourceConnection.getUiAttributes().getKey());
						operators.setName(BPConstant.CONNECTIONS);
						operators.setType(sourceConnection.getUiAttributes().getType());
						operators.setStatus(BPConstant.DELETED);
						operators.setFields(fieldsList);
						operatorList.add(operators);
					}
				}
			}
			for (Connection targetConnection : targetConnections) {
				if (StringUtils.equalsIgnoreCase(targetConnection.getUiAttributes().getType(), BPConstant.SMART)) {
					isConnectionAdded = true;
					for (Connection sourceConnection : sourceConnections) {
						if (StringUtils.equalsIgnoreCase(sourceConnection.getUiAttributes().getType(),
								BPConstant.SMART)) {
							if (StringUtils.equalsIgnoreCase(targetConnection.getUiAttributes().getKey(),
									sourceConnection.getUiAttributes().getKey())) {
								isConnectionAdded = false;
								fieldsList = smartConnectorLinkPropertyCheck(sourceConnection.getLinkProperties(),
										targetConnection.getLinkProperties(), false, false);
								smartConnectorAttributeCheck(sourceConnection.getUiAttributes(),
										targetConnection.getUiAttributes(), false, false, fieldsList);
								if (!fieldsList.isEmpty()) {
									ReviewReportResponse operators = new ReviewReportResponse();
									operators.setKey(targetConnection.getUiAttributes().getKey());
									operators.setName(BPConstant.CONNECTIONS);
									operators.setType(targetConnection.getUiAttributes().getType());
									operators.setStatus(BPConstant.MODIFIED);
									operators.setFields(fieldsList);
									operatorList.add(operators);
								}
							}
						}
					}
					if (isConnectionAdded) {
						fieldsList = smartConnectorLinkPropertyCheck(new LinkProperties(),
								targetConnection.getLinkProperties(), true, false);
						smartConnectorAttributeCheck(new UIAttributes(), targetConnection.getUiAttributes(), true,
								false, fieldsList);
						if (!fieldsList.isEmpty()) {
							ReviewReportResponse operators = new ReviewReportResponse();
							operators.setKey(targetConnection.getUiAttributes().getKey());
							operators.setName(BPConstant.CONNECTIONS);
							operators.setType(targetConnection.getUiAttributes().getType());
							operators.setStatus(BPConstant.ADDED);
							operators.setFields(fieldsList);
							operatorList.add(operators);
						}
					}
				}
			}
			return operatorList;
		} catch (Exception exception) {
			LOGGER.error("Error occurred in smart connector"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will compare UI attributes for smart connector.
	 */
	private static void smartConnectorAttributeCheck(UIAttributes sourceUIAttributes, UIAttributes targetUIAttributes,
			boolean isAdded, boolean isDeleted, List<Fields> fieldsList) {
		try {

			if (isAdded) {
				if (!StringUtils.isEmpty(targetUIAttributes.getName()))
					fieldsList.add(
							BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.UI_ATTRIBUTES,
									BPConstant.NAME, BPConstant.EMPTY_STRING, targetUIAttributes.getName()));
				if (!StringUtils.isEmpty(targetUIAttributes.getDecisionName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.UI_ATTRIBUTES, BPConstant.DECISION_NAME, BPConstant.EMPTY_STRING,
							targetUIAttributes.getDecisionName()));
			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceUIAttributes.getName()))
					fieldsList.add(
							BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.UI_ATTRIBUTES,
									BPConstant.NAME, sourceUIAttributes.getName(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceUIAttributes.getDecisionName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.UI_ATTRIBUTES, BPConstant.DECISION_NAME, sourceUIAttributes.getDecisionName(),
							BPConstant.EMPTY_STRING));
			} else {
				if (!StringUtils.equalsIgnoreCase(sourceUIAttributes.getDecisionName(),
						targetUIAttributes.getDecisionName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.UI_ATTRIBUTES, BPConstant.DECISION_NAME, sourceUIAttributes.getDecisionName(),
							targetUIAttributes.getDecisionName()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceUIAttributes.getName(), targetUIAttributes.getName())) {
					fieldsList.add(
							BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.UI_ATTRIBUTES,
									BPConstant.NAME, sourceUIAttributes.getName(), targetUIAttributes.getName()));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Smart connector ui attributes error"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will compare linkProperties for smart connector.
	 */
	private static List<Fields> smartConnectorLinkPropertyCheck(LinkProperties sourceLinkProperty,
			LinkProperties targetLinkProperty, boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			BusinessSettings sourceBusinessSettings = sourceLinkProperty.getBusinessSettings();
			BusinessSettings targetBusinessSettings = targetLinkProperty.getBusinessSettings();
			if (isAdded) {
				if (!StringUtils.isEmpty(targetBusinessSettings.getInputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
							targetBusinessSettings.getInputBeType()));
				if (!StringUtils.isEmpty(targetBusinessSettings.getOutputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE, BPConstant.EMPTY_STRING,
							targetBusinessSettings.getOutputBeType()));

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceLinkProperty.getProperties(),
						targetLinkProperty.getProperties(), fieldsList, true, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceLinkProperty.getComments(),
						targetLinkProperty.getComments(), fieldsList, true, false);
			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceBusinessSettings.getInputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceBusinessSettings.getInputBeType(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceBusinessSettings.getOutputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceBusinessSettings.getOutputBeType(), BPConstant.EMPTY_STRING));

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceLinkProperty.getProperties(),
						targetLinkProperty.getProperties(), fieldsList, false, true);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceLinkProperty.getComments(),
						targetLinkProperty.getComments(), fieldsList, false, true);
			} else {
				if (!StringUtils.equalsIgnoreCase(sourceBusinessSettings.getInputBeType(),
						targetBusinessSettings.getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceBusinessSettings.getInputBeType(), targetBusinessSettings.getInputBeType()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceBusinessSettings.getOutputBeType(),
						targetBusinessSettings.getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceBusinessSettings.getOutputBeType(), targetBusinessSettings.getOutputBeType()));
				}

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceLinkProperty.getProperties(),
						targetLinkProperty.getProperties(), fieldsList, false, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceLinkProperty.getComments(),
						targetLinkProperty.getComments(), fieldsList, false, false);
			}
			return fieldsList;
		} catch (Exception exception) {
			LOGGER.error("Smart connector link property error"+exception);
			throw new BPException(exception.getMessage());
		}
	}

}
