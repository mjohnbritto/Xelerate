package com.suntecgroup.bp.designer.review.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.frontend.beans.InputParam;
import com.suntecgroup.bp.designer.frontend.beans.Operators;
import com.suntecgroup.bp.designer.frontend.beans.OutputParam;
import com.suntecgroup.bp.designer.model.Fields;
import com.suntecgroup.bp.designer.services.impl.BPDesignerServiceImpl;
import com.suntecgroup.bp.util.BPConstant;

public class InvokeBSInternalReport {
	private static final Logger LOGGER = LoggerFactory.getLogger(InvokeBSInternalReport.class);

	/**
	 * This method will compare the invoke BS operator fields.
	 */
	public static List<Fields> invokeBSOperatorComparision(Operators sourceVersionOperator,
			Operators targetVersionOperator, boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetVersionOperator.getName()))
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, BPConstant.EMPTY_STRING, targetVersionOperator.getName()));
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getBusinessServiceName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESS_SERVICE_NAME, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getBusinessServiceName()));
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getApiName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getApiName()));
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getInputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getInputBeType()));
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getOutputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getOutputBeType()));
				for (InputParam targetInputParam : targetVersionOperator.getInputMapping()) {
					String newValue = checkInputParamSelectedKey(targetInputParam);
					if (!StringUtils.isEmpty(newValue))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.INPUT_MAPPING, targetInputParam.getContextVariable(),
								BPConstant.EMPTY_STRING, newValue));
				}

				for (OutputParam targetOutputParam : targetVersionOperator.getOutputMapping()) {
					String newValue = checkOutputParamSelectedKey(targetOutputParam);
					if (!StringUtils.isEmpty(newValue))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.INPUT_MAPPING, targetOutputParam.getProcessVariable(),
								BPConstant.EMPTY_STRING, newValue));
				}

				for (String targetBusinessErrorCode : targetVersionOperator.getBusinessSettings()
						.getBusinessErrorCodes()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESSERRORCODES, BPConstant.BUSINESSERRORCODES, BPConstant.EMPTY_STRING,
							targetBusinessErrorCode));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
						BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESSFAILUREFLOWEXIST, BPConstant.EMPTY_STRING,
						targetVersionOperator.getBusinessSettings().isBusinessFailureFlowExist()));

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, true, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, true, false);

			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceVersionOperator.getName()))
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, sourceVersionOperator.getName(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getBusinessServiceName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESS_SERVICE_NAME,
							sourceVersionOperator.getBusinessSettings().getBusinessServiceName(),
							BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getApiName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME,
							sourceVersionOperator.getBusinessSettings().getApiName(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getInputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getInputBeType(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getOutputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getOutputBeType(), BPConstant.EMPTY_STRING));
				for (InputParam sourceInputParam : sourceVersionOperator.getInputMapping()) {
					String oldValue = checkInputParamSelectedKey(sourceInputParam);
					if (!oldValue.isEmpty())
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.INPUT_MAPPING, sourceInputParam.getContextVariable(), oldValue,
								BPConstant.EMPTY_STRING));
				}

				for (OutputParam sourceOutputParam : sourceVersionOperator.getOutputMapping()) {
					String oldValue = checkOutputParamSelectedKey(sourceOutputParam);
					if (!oldValue.isEmpty())
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.INPUT_MAPPING, sourceOutputParam.getProcessVariable(), oldValue,
								BPConstant.EMPTY_STRING));
				}

				for (String sourceBusinessErrorCode : sourceVersionOperator.getBusinessSettings()
						.getBusinessErrorCodes()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESSERRORCODES, BPConstant.BUSINESSERRORCODES, sourceBusinessErrorCode,
							BPConstant.EMPTY_STRING));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
						BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESSFAILUREFLOWEXIST,
						sourceVersionOperator.getBusinessSettings().isBusinessFailureFlowExist(),
						BPConstant.EMPTY_STRING));

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
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getInputBeType(),
						targetVersionOperator.getBusinessSettings().getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getInputBeType(),
							targetVersionOperator.getBusinessSettings().getInputBeType()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getOutputBeType(),
						targetVersionOperator.getBusinessSettings().getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getOutputBeType(),
							targetVersionOperator.getBusinessSettings().getOutputBeType()));
				}

				// businessErrorCode comparison
				businessErrorCodeCompare(sourceVersionOperator.getBusinessSettings().getBusinessErrorCodes(),
						targetVersionOperator.getBusinessSettings().getBusinessErrorCodes(), fieldsList);
				if (sourceVersionOperator.getBusinessSettings().isBusinessFailureFlowExist() != targetVersionOperator
						.getBusinessSettings().isBusinessFailureFlowExist()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESSFAILUREFLOWEXIST,
							sourceVersionOperator.getBusinessSettings().isBusinessFailureFlowExist(),
							targetVersionOperator.getBusinessSettings().isBusinessFailureFlowExist()));
				}

				inputMappingComparision(sourceVersionOperator.getInputMapping(),
						targetVersionOperator.getInputMapping(), fieldsList);
				outputMappingComparision(sourceVersionOperator.getOutputMapping(),
						targetVersionOperator.getOutputMapping(), fieldsList);
				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, false);
			}
			return fieldsList;
		} catch (Exception exception) {
			LOGGER.error("Invoke BS comparision error"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will compare the business error codes in invoke BS.
	 */

	private static void businessErrorCodeCompare(List<String> sourceBusinessErrorCodes,
			List<String> targetBusinessErrorCodes, List<Fields> fieldsList) {
		boolean isAdded;
		boolean isDeleted;
		try {
			for (int sourceBusinessCode = 0; sourceBusinessCode < sourceBusinessErrorCodes
					.size(); sourceBusinessCode++) {
				isDeleted = true;
				for (int targetBusinessCode = 0; targetBusinessCode < targetBusinessErrorCodes
						.size(); targetBusinessCode++) {
					if (StringUtils.equalsIgnoreCase(targetBusinessErrorCodes.get(targetBusinessCode),
							sourceBusinessErrorCodes.get(sourceBusinessCode))) {
						isDeleted = false;
					}
				}
				if (isDeleted) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESSERRORCODES, BPConstant.BUSINESSERRORCODES,
							sourceBusinessErrorCodes.get(sourceBusinessCode), BPConstant.EMPTY_STRING));
				}
			}
			for (int targetBusinessCode = 0; targetBusinessCode < targetBusinessErrorCodes
					.size(); targetBusinessCode++) {
				isAdded = true;
				for (int sourceBusinessCode = 0; sourceBusinessCode < sourceBusinessErrorCodes
						.size(); sourceBusinessCode++) {
					if (StringUtils.equalsIgnoreCase(targetBusinessErrorCodes.get(targetBusinessCode),
							sourceBusinessErrorCodes.get(sourceBusinessCode))) {
						isAdded = false;
					}
				}
				if (isAdded) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESSERRORCODES, BPConstant.BUSINESSERRORCODES, BPConstant.EMPTY_STRING,
							targetBusinessErrorCodes.get(targetBusinessCode)));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Invoke BS business error code comparision error"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will compare the output mapping in invoke BS.
	 */
	private static void outputMappingComparision(List<OutputParam> sourceOutputMappingList,
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
	 * This method will compare the input mapping in invoke BS.
	 */
	public static void inputMappingComparision(List<InputParam> sourceInputMappingList,
			List<InputParam> targetInputMappingList, List<Fields> fieldsList) {
		boolean isAdded;
		boolean isDeleted;
		;
		try {
			ArrayList<InputParam> deletedList = new ArrayList<>();
			for (InputParam sourceMapping : sourceInputMappingList) {
				isDeleted = true;
				for (InputParam targetMapping : targetInputMappingList) {
					if (StringUtils.equalsIgnoreCase(targetMapping.getContextVariable(),
							sourceMapping.getContextVariable())) {
						isDeleted = false;
					}
				}
				if (isDeleted) {
					deletedList.add(sourceMapping);
				}
			}
			if (!deletedList.isEmpty()) {
				for (InputParam sourceInputParam : deletedList) {
					String oldValue = checkInputParamSelectedKey(sourceInputParam);
					if (!StringUtils.isEmpty(oldValue))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.INPUT_MAPPING, sourceInputParam.getContextVariable(), oldValue,
								BPConstant.EMPTY_STRING));
				}
			}
			for (InputParam targetMapping : targetInputMappingList) {
				isAdded = true;
				for (InputParam sourceMapping : sourceInputMappingList) {
					if (StringUtils.equalsIgnoreCase(targetMapping.getContextVariable(),
							sourceMapping.getContextVariable())) {
						isAdded = false;
						if (StringUtils.equalsIgnoreCase(targetMapping.getSelectedKey(),
								sourceMapping.getSelectedKey())) {
							if (StringUtils.equalsIgnoreCase(targetMapping.getSelectedKey(),
									BPConstant.PROCESS_VARIABLE)) {
								if (!StringUtils.equalsIgnoreCase(targetMapping.getProcessVariable(),
										sourceMapping.getProcessVariable())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
											BPConstant.INPUT_MAPPING, targetMapping.getContextVariable(),
											sourceMapping.getProcessVariable(), targetMapping.getProcessVariable()));
								}
							} else if (StringUtils.equalsIgnoreCase(targetMapping.getSelectedKey(),
									BPConstant.ENTER_VALUE)) {
								String oldValue = checkInputParamType(sourceMapping);
								String newValue = checkInputParamType(targetMapping);
								if (!StringUtils.equalsIgnoreCase(oldValue, newValue)) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
											BPConstant.INPUT_MAPPING, targetMapping.getContextVariable(), oldValue,
											newValue));
								}
							}
						} else {
							String oldValue = checkInputParamSelectedKey(sourceMapping);
							String newValue = checkInputParamSelectedKey(targetMapping);
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.INPUT_MAPPING, targetMapping.getContextVariable(), oldValue, newValue));
						}
					}
				}
				if (isAdded) {
					String newValue = checkInputParamSelectedKey(targetMapping);
					if (!StringUtils.isEmpty(newValue))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.INPUT_MAPPING, targetMapping.getContextVariable(), BPConstant.EMPTY_STRING,
								newValue));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Input Mapping Comparision error"+exception);
			throw new BPException(exception.getMessage());
		}

	}

	/**
	 * This method will fetch the selected key for input param.
	 */
	private static String checkInputParamSelectedKey(InputParam inputParam) {
		String selectedKey = inputParam.getSelectedKey();
		String value = null;
		switch (selectedKey) {
		case BPConstant.PROCESS_VARIABLE:
			value = inputParam.getProcessVariable();
			break;
		case BPConstant.ENTER_VALUE:
			value = checkInputParamType(inputParam);
			break;
		default:
			value = BPConstant.EMPTY_STRING;
		}
		return value;
	}

	/**
	 * This method will check the input param type.
	 */
	private static String checkInputParamType(InputParam inputParam) {
		String type = inputParam.getType();
		String value = null;
		switch (type) {
		case BPConstant.STRING:
			value = inputParam.getInputParamvalue().getStringValue();
			break;
		case BPConstant.BOOLEAN:
			value = inputParam.getInputParamvalue().getBooleanValue();
			break;
		case BPConstant.DATE_TIME:
			value = inputParam.getInputParamvalue().getDateValue();
			break;
		case BPConstant.NUMBER:
			value = inputParam.getInputParamvalue().getIntValue();
			break;
		default:
			value = BPConstant.EMPTY_STRING;
		}
		return value;
	}
}
