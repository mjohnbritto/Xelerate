package com.suntecgroup.bp.designer.review.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.frontend.beans.InputConnection;
import com.suntecgroup.bp.designer.frontend.beans.Operators;
import com.suntecgroup.bp.designer.frontend.beans.OutputParam;
import com.suntecgroup.bp.designer.model.Fields;
import com.suntecgroup.bp.designer.services.impl.BPDesignerServiceImpl;
import com.suntecgroup.bp.util.BPConstant;

public class MergeOperatorReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(MergeOperatorReport.class);

	/**
	 * This method will compare the merge operator fields.
	 */
	public static List<Fields> compareMergeOperator(Operators sourceVersionOperator, Operators targetVersionOperator,
			boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetVersionOperator.getName())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, BPConstant.EMPTY_STRING, targetVersionOperator.getName()));
				}
				// Business Settings
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getBusinessServiceName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESS_SERVICE_NAME, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getBusinessServiceName()));
				}

				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getApiName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getApiName()));
				}

				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getOutputBeType()));
				}

				if (targetVersionOperator.getBusinessSettings().getExpectedInputChannel() != null) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.EXPECTED_INPUT_CHANNEL, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getExpectedInputChannel()));
				}

				// Input Connections
				compareInputConnections(null, targetVersionOperator.getBusinessSettings().getInputConnections(),
						fieldsList, true, false);

				// Mapping
				for (OutputParam targetOutputParam : targetVersionOperator.getOutputMapping()) {
					String newValue = checkOutputParamSelectedKey(targetOutputParam);
					if (!StringUtils.isEmpty(newValue))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.INPUT_MAPPING, targetOutputParam.getProcessVariable(),
								BPConstant.EMPTY_STRING, newValue));
				}

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, true, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, true, false);
			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceVersionOperator.getName())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, sourceVersionOperator.getName(), BPConstant.EMPTY_STRING));
				}
				// Business Settings
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getBusinessServiceName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESS_SERVICE_NAME,
							targetVersionOperator.getBusinessSettings().getBusinessServiceName(),
							BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getApiName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME,
							targetVersionOperator.getBusinessSettings().getApiName(), BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getOutputBeType(), BPConstant.EMPTY_STRING));
				}
				if (sourceVersionOperator.getBusinessSettings().getExpectedInputChannel() != null) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.EXPECTED_INPUT_CHANNEL,
							sourceVersionOperator.getBusinessSettings().getExpectedInputChannel(),
							BPConstant.EMPTY_STRING));
				}
				// Input connections
				compareInputConnections(sourceVersionOperator.getBusinessSettings().getInputConnections(), null,
						fieldsList, false, true);

				// Mapping
				for (OutputParam sourceOutputParam : sourceVersionOperator.getOutputMapping()) {
					String oldValue = checkOutputParamSelectedKey(sourceOutputParam);
					if (!oldValue.isEmpty())
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.INPUT_MAPPING, sourceOutputParam.getProcessVariable(), oldValue,
								BPConstant.EMPTY_STRING));
				}

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, true);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, true);
			} else {
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getName(), targetVersionOperator.getName())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, sourceVersionOperator.getName(), targetVersionOperator.getName()));
				}
				// Business Settings
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getBusinessServiceName(),
						targetVersionOperator.getBusinessSettings().getBusinessServiceName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESS_SERVICE_NAME,
							sourceVersionOperator.getBusinessSettings().getBusinessServiceName(),
							targetVersionOperator.getBusinessSettings().getBusinessServiceName()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getApiName(),
						targetVersionOperator.getBusinessSettings().getApiName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME,
							sourceVersionOperator.getBusinessSettings().getApiName(),
							targetVersionOperator.getBusinessSettings().getApiName()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getOutputBeType(),
						targetVersionOperator.getBusinessSettings().getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getOutputBeType(),
							targetVersionOperator.getBusinessSettings().getOutputBeType()));
				}

				if (sourceVersionOperator.getBusinessSettings().getExpectedInputChannel() != targetVersionOperator
						.getBusinessSettings().getExpectedInputChannel()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.EXPECTED_INPUT_CHANNEL,
							sourceVersionOperator.getBusinessSettings().getExpectedInputChannel(),
							targetVersionOperator.getBusinessSettings().getExpectedInputChannel()));
				}
				// Input connections
				compareInputConnections(sourceVersionOperator.getBusinessSettings().getInputConnections(),
						targetVersionOperator.getBusinessSettings().getInputConnections(), fieldsList, false, false);

				// Mapping
				compareOutputMapping(sourceVersionOperator.getOutputMapping(), targetVersionOperator.getOutputMapping(),
						fieldsList);

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, false);
			}
			return fieldsList;
		} catch (Exception exception) {
			LOGGER.error("Error occurred while comparing join operator"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will give the output mapping comparision result.
	 */
	private static void compareOutputMapping(List<OutputParam> sourceOutputMappingList,
			List<OutputParam> targetOutputMappingList, List<Fields> fieldsList) {
		boolean isAdded;
		boolean isDeleted;
		try {
			ArrayList<OutputParam> deletedList = new ArrayList<>();
			for (OutputParam sourceMapping : sourceOutputMappingList) {
				isDeleted = true;
				for (OutputParam targetMapping : targetOutputMappingList) {
					if (StringUtils.equalsIgnoreCase(targetMapping.getProcessVariable(),
							sourceMapping.getProcessVariable())) {
						isDeleted = false;
					}
				}
				if (isDeleted) {
					deletedList.add(sourceMapping);
				}
			}
			if (!deletedList.isEmpty()) {
				for (OutputParam sourceInputParam : deletedList) {
					String oldValue = checkOutputParamSelectedKey(sourceInputParam);
					if (!StringUtils.isEmpty(oldValue))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.OUTPUT_MAPPING, sourceInputParam.getProcessVariable(), oldValue,
								BPConstant.EMPTY_STRING));
				}
			}
			for (OutputParam targetMapping : targetOutputMappingList) {
				isAdded = true;
				for (OutputParam sourceMapping : sourceOutputMappingList) {
					if (StringUtils.equalsIgnoreCase(targetMapping.getProcessVariable(),
							sourceMapping.getProcessVariable())) {
						isAdded = false;
						if (StringUtils.equalsIgnoreCase(targetMapping.getSelectedKey(),
								sourceMapping.getSelectedKey())) {
							if (StringUtils.equalsIgnoreCase(targetMapping.getSelectedKey(),
									BPConstant.CONTEXT_VARIABLE)) {
								if (!StringUtils.equalsIgnoreCase(targetMapping.getMappedContextVariable(),
										sourceMapping.getMappedContextVariable())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
											BPConstant.OUTPUT_MAPPING, targetMapping.getProcessVariable(),
											sourceMapping.getMappedContextVariable(),
											targetMapping.getMappedContextVariable()));
								}
							} else if (StringUtils.equalsIgnoreCase(targetMapping.getSelectedKey(),
									BPConstant.ENTER_VALUE)) {
								String oldValue = checkOutputParamType(sourceMapping);
								String newValue = checkOutputParamType(targetMapping);
								if (!StringUtils.equalsIgnoreCase(oldValue, newValue)) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
											BPConstant.OUTPUT_MAPPING, targetMapping.getProcessVariable(), oldValue,
											newValue));
								}
							}
						} else {
							String oldValue = checkOutputParamSelectedKey(sourceMapping);
							String newValue = checkOutputParamSelectedKey(targetMapping);
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.OUTPUT_MAPPING, targetMapping.getProcessVariable(), oldValue, newValue));
						}
					}
				}

				if (isAdded) {
					String newValue = checkOutputParamSelectedKey(targetMapping);
					if (!StringUtils.isEmpty(newValue))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.OUTPUT_MAPPING, targetMapping.getProcessVariable(), BPConstant.EMPTY_STRING,
								newValue));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Output Mapping Comparision error"+exception);
			throw new BPException(exception.getMessage());
		}

	}

	/**
	 * This method will check the output param key.
	 */
	private static String checkOutputParamSelectedKey(OutputParam outputParam) {
		String selectedKey = outputParam.getSelectedKey();
		String value = null;
		switch (selectedKey) {
		case BPConstant.CONTEXT_VARIABLE:
			value = outputParam.getMappedContextVariable();
			break;
		case BPConstant.ENTER_VALUE:
			value = checkOutputParamType(outputParam);
			break;
		default:
			value = BPConstant.EMPTY_STRING;
		}
		return value;
	}

	/**
	 * This method will check the output param type.
	 */
	private static String checkOutputParamType(OutputParam outputParam) {
		String type = outputParam.getType();
		String value = null;
		switch (type) {
		case BPConstant.STRING:
			value = outputParam.getOutputParamvalue().getStringValue();
			break;
		case BPConstant.BOOLEAN:
			value = outputParam.getOutputParamvalue().getBooleanValue();
			break;
		case BPConstant.DATE_TIME:
			value = outputParam.getOutputParamvalue().getDateValue();
			break;
		case BPConstant.NUMBER:
			value = outputParam.getOutputParamvalue().getIntValue();
			break;
		default:
			value = BPConstant.EMPTY_STRING;
		}
		return value;
	}

	/**
	 * Generic method to compare the InputConnections values
	 * 
	 * @param sourceInputConnectionList
	 * @param targetInputConnectionList
	 * @param isAdded
	 * @param isDeleted
	 * @param fieldsList
	 */
	private static List<Fields> compareInputConnections(List<InputConnection> sourceInputConnectionList,
			List<InputConnection> targetInputConnectionList, List<Fields> fieldsList, boolean isAdded,
			boolean isDeleted) {
		try {
			if (isAdded) {
				for (InputConnection targetInputConnection : targetInputConnectionList) {
					if (!StringUtils.isEmpty(targetInputConnection.getConnectionName())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetInputConnection.getKey(),
								BPConstant.BUSINESS_SETTINGS, BPConstant.CONNECTION_NAME, BPConstant.EMPTY_STRING,
								targetInputConnection.getConnectionName()));
					}
					if (!StringUtils.isEmpty(targetInputConnection.getInputBeType())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetInputConnection.getKey(),
								BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
								targetInputConnection.getInputBeType()));
					}
					if (!StringUtils.isEmpty(targetInputConnection.getContextVariable())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetInputConnection.getKey(),
								BPConstant.BUSINESS_SETTINGS, BPConstant.CONTEXT_VARIABLE, BPConstant.EMPTY_STRING,
								targetInputConnection.getContextVariable()));
					}
					if (!StringUtils.isEmpty(targetInputConnection.getFromOperatorKey())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetInputConnection.getKey(),
								BPConstant.BUSINESS_SETTINGS, BPConstant.FROM_OPERATOR_KEY, BPConstant.EMPTY_STRING,
								targetInputConnection.getFromOperatorKey()));
					}
				}
			} else if (isDeleted) {
				for (InputConnection sourceInputConnection : sourceInputConnectionList) {
					if (!StringUtils.isEmpty(sourceInputConnection.getConnectionName())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
								BPConstant.BUSINESS_SETTINGS, BPConstant.CONNECTION_NAME,
								sourceInputConnection.getConnectionName(), BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(sourceInputConnection.getInputBeType())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
								BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
								sourceInputConnection.getInputBeType(), BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(sourceInputConnection.getContextVariable())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
								BPConstant.BUSINESS_SETTINGS, BPConstant.CONTEXT_VARIABLE,
								sourceInputConnection.getContextVariable(), BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(sourceInputConnection.getFromOperatorKey())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
								BPConstant.BUSINESS_SETTINGS, BPConstant.FROM_OPERATOR_KEY,
								sourceInputConnection.getFromOperatorKey(), BPConstant.EMPTY_STRING));
					}
				}
			} else {
				// CASE1.IF BOTH LHS&RHS CONTAINS THE OBJECT
				for (InputConnection sourceInputConnection : sourceInputConnectionList) {
					for (InputConnection targetInputConnection : targetInputConnectionList) {
						if (StringUtils.equalsIgnoreCase(targetInputConnection.getKey(),
								sourceInputConnection.getKey())) {
							if (!StringUtils.equalsIgnoreCase(targetInputConnection.getConnectionName(),
									sourceInputConnection.getConnectionName())) {
								fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
										BPConstant.BUSINESS_SETTINGS, BPConstant.CONNECTION_NAME,
										sourceInputConnection.getConnectionName(),
										targetInputConnection.getConnectionName()));
							}
							if (!StringUtils.equalsIgnoreCase(targetInputConnection.getInputBeType(),
									sourceInputConnection.getInputBeType())) {
								fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
										BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
										sourceInputConnection.getInputBeType(),
										targetInputConnection.getInputBeType()));
							}
							if (!StringUtils.equalsIgnoreCase(targetInputConnection.getContextVariable(),
									sourceInputConnection.getContextVariable())) {
								fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
										BPConstant.BUSINESS_SETTINGS, BPConstant.CONTEXT_VARIABLE,
										sourceInputConnection.getContextVariable(),
										targetInputConnection.getContextVariable()));
							}
							if (!StringUtils.equalsIgnoreCase(targetInputConnection.getFromOperatorKey(),
									sourceInputConnection.getFromOperatorKey())) {
								fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
										BPConstant.BUSINESS_SETTINGS, BPConstant.FROM_OPERATOR_KEY,
										sourceInputConnection.getFromOperatorKey(),
										targetInputConnection.getFromOperatorKey()));
							}
						}
					}
				}
				// CASE2.IF LHS ONLY CONTAINS THE OBJECT
				for (InputConnection sourceInputConnection : sourceInputConnectionList) {
					boolean isKeyAvailable = false;
					for (InputConnection targetInputConnection : targetInputConnectionList) {
						if (StringUtils.equalsIgnoreCase(sourceInputConnection.getKey(),
								targetInputConnection.getKey())) {
							isKeyAvailable = true;
							break;
						}
					}
					if (!isKeyAvailable) {
						if (!StringUtils.isEmpty(sourceInputConnection.getConnectionName())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
									BPConstant.BUSINESS_SETTINGS, BPConstant.CONNECTION_NAME,
									sourceInputConnection.getConnectionName(), BPConstant.EMPTY_STRING));
						}
						if (!StringUtils.isEmpty(sourceInputConnection.getInputBeType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
									BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
									sourceInputConnection.getInputBeType(), BPConstant.EMPTY_STRING));
						}
						if (!StringUtils.isEmpty(sourceInputConnection.getContextVariable())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
									BPConstant.BUSINESS_SETTINGS, BPConstant.CONTEXT_VARIABLE,
									sourceInputConnection.getContextVariable(), BPConstant.EMPTY_STRING));
						}
						if (!StringUtils.isEmpty(sourceInputConnection.getFromOperatorKey())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceInputConnection.getKey(),
									BPConstant.BUSINESS_SETTINGS, BPConstant.FROM_OPERATOR_KEY,
									sourceInputConnection.getFromOperatorKey(), BPConstant.EMPTY_STRING));
						}
					}
				}

				// CASE3.IF RHS ONLY CONTAINS THE OBJECT
				for (InputConnection targetInputConnection : targetInputConnectionList) {
					boolean isKeyAvailable = false;
					for (InputConnection sourceInputConnection : sourceInputConnectionList) {
						if (StringUtils.equalsIgnoreCase(sourceInputConnection.getKey(),
								targetInputConnection.getKey())) {
							isKeyAvailable = true;
							break;
						}
					}
					if (!isKeyAvailable) {
						if (!StringUtils.isEmpty(targetInputConnection.getConnectionName())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetInputConnection.getKey(),
									BPConstant.BUSINESS_SETTINGS, BPConstant.CONNECTION_NAME, BPConstant.EMPTY_STRING,
									targetInputConnection.getConnectionName()));
						}
						if (!StringUtils.isEmpty(targetInputConnection.getInputBeType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetInputConnection.getKey(),
									BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
									targetInputConnection.getInputBeType()));
						}
						if (!StringUtils.isEmpty(targetInputConnection.getContextVariable())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetInputConnection.getKey(),
									BPConstant.BUSINESS_SETTINGS, BPConstant.CONTEXT_VARIABLE, BPConstant.EMPTY_STRING,
									targetInputConnection.getContextVariable()));
						}
						if (!StringUtils.isEmpty(targetInputConnection.getFromOperatorKey())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetInputConnection.getKey(),
									BPConstant.BUSINESS_SETTINGS, BPConstant.FROM_OPERATOR_KEY, BPConstant.EMPTY_STRING,
									targetInputConnection.getFromOperatorKey()));
						}
					}
				}
			}
			return fieldsList;

		} catch (Exception exception) {
			LOGGER.error("Error occurred while comparing merge operator"+exception);
			throw new BPException(exception.getMessage());
		}

	}
}
