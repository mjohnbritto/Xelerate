package com.suntecgroup.bp.designer.review.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.frontend.beans.InputParam;
import com.suntecgroup.bp.designer.frontend.beans.Operators;
import com.suntecgroup.bp.designer.model.Fields;
import com.suntecgroup.bp.designer.services.impl.BPDesignerServiceImpl;
import com.suntecgroup.bp.util.BPConstant;

public class RestChannelOperatorReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestChannelOperatorReport.class);

	/**
	 * This method will compare the rest input operator fields.
	 */

	public static List<Fields> restInputOperatorComparision(Operators sourceVersionOperator,
			Operators targetVersionOperator, boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetVersionOperator.getName()))
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, BPConstant.EMPTY_STRING, targetVersionOperator.getName()));
				// InputBEType
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getInputBeType()));
				}
				// Content Type
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getContentType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.CONTENT_TYPE_EXTERNAL, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getContentType()));
				}

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, true, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, true, false);
			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceVersionOperator.getName()))
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, sourceVersionOperator.getName(), BPConstant.EMPTY_STRING));
				// InputBEType
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getInputBeType(), BPConstant.EMPTY_STRING));
				}
				// Content Type
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getContentType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.CONTENT_TYPE_EXTERNAL,
							sourceVersionOperator.getBusinessSettings().getContentType(), BPConstant.EMPTY_STRING));
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
				// InputBEType
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getInputBeType(),
						targetVersionOperator.getBusinessSettings().getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getInputBeType(),
							targetVersionOperator.getBusinessSettings().getInputBeType()));
				}
				// Content Type
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getContentType(),
						targetVersionOperator.getBusinessSettings().getContentType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.CONTENT_TYPE_EXTERNAL,
							sourceVersionOperator.getBusinessSettings().getContentType(),
							targetVersionOperator.getBusinessSettings().getContentType()));
				}

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, false);
			}

			return fieldsList;

		} catch (Exception exception) {
			LOGGER.error("Error occurred while comparing rest channel operator"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will compare the rest output operator fields.
	 */
	public static List<Fields> restOutputOperatorComparision(Operators sourceVersionOperator,
			Operators targetVersionOperator, boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			if (isAdded) {
				// CASE1: If RHS having internal Service
				if (targetVersionOperator.getBusinessSettings().getAPIInput().equalsIgnoreCase("InternalAPI")) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.API_INPUT, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getAPIInput()));
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
					if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getInputBeType())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
								targetVersionOperator.getBusinessSettings().getInputBeType()));
					}

					for (InputParam targetInputParam : targetVersionOperator.getInputMapping()) {
						String newValue = checkInputParamSelectedKey(targetInputParam);
						if (!StringUtils.isEmpty(newValue)) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.INPUT_MAPPING, targetInputParam.getContextVariable(),
									BPConstant.EMPTY_STRING, newValue));
						}
					}
				}
				// CASE2: If RHS having external Service
				if (targetVersionOperator.getBusinessSettings().getAPIInput().equalsIgnoreCase("ExternalAPI")) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.API_INPUT, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getAPIInput()));
					if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getHttpMethod())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.HTTP_METHOD, BPConstant.EMPTY_STRING,
								targetVersionOperator.getBusinessSettings().getHttpMethod()));
					}
					if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getInputBeType())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
								targetVersionOperator.getBusinessSettings().getInputBeType()));
					}
					if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getSelectedInputOption())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.SELECTED_INPUT_OPTION, BPConstant.EMPTY_STRING,
								targetVersionOperator.getBusinessSettings().getSelectedInputOption()));
					}
					if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getContentType())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.CONTENT_TYPE_EXTERNAL, BPConstant.EMPTY_STRING,
								targetVersionOperator.getBusinessSettings().getContentType()));
					}
					if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getApi())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME, BPConstant.EMPTY_STRING,
								targetVersionOperator.getBusinessSettings().getApi()));
					}
				}
				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, true, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, true, false);

			} else if (isDeleted) {
				// CASE1: If LHS having internal Service
				if (sourceVersionOperator.getBusinessSettings().getAPIInput().equalsIgnoreCase("InternalAPI")) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.API_INPUT,
							sourceVersionOperator.getBusinessSettings().getAPIInput(), BPConstant.EMPTY_STRING));
					if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getBusinessServiceName())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESS_SERVICE_NAME,
								sourceVersionOperator.getBusinessSettings().getBusinessServiceName(),
								BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getApiName())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME,
								sourceVersionOperator.getBusinessSettings().getApiName(), BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getInputBeType())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
								sourceVersionOperator.getBusinessSettings().getInputBeType(), BPConstant.EMPTY_STRING));
					}

					for (InputParam sourceInputParam : sourceVersionOperator.getInputMapping()) {
						String oldValue = checkInputParamSelectedKey(sourceInputParam);
						if (!oldValue.isEmpty()) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.INPUT_MAPPING, sourceInputParam.getContextVariable(), oldValue,
									BPConstant.EMPTY_STRING));
						}
					}
				}
				// CASE2: If LHS having external Service
				if (sourceVersionOperator.getBusinessSettings().getAPIInput().equalsIgnoreCase("ExternalAPI")) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.API_INPUT,
							sourceVersionOperator.getBusinessSettings().getAPIInput(), BPConstant.EMPTY_STRING));
					if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getHttpMethod())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.HTTP_METHOD,
								sourceVersionOperator.getBusinessSettings().getHttpMethod(), BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getInputBeType())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
								sourceVersionOperator.getBusinessSettings().getInputBeType(), BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getSelectedInputOption())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.SELECTED_INPUT_OPTION,
								sourceVersionOperator.getBusinessSettings().getSelectedInputOption(),
								BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getContentType())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.CONTENT_TYPE_EXTERNAL,
								sourceVersionOperator.getBusinessSettings().getContentType(), BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getApi())) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME,
								sourceVersionOperator.getBusinessSettings().getApi(), BPConstant.EMPTY_STRING));
					}
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
				// CASE1: If LHS&RHS having same API internal
				if (sourceVersionOperator.getBusinessSettings().getAPIInput()
						.equalsIgnoreCase(targetVersionOperator.getBusinessSettings().getAPIInput())) {

					if (sourceVersionOperator.getBusinessSettings().getAPIInput().equalsIgnoreCase("InternalAPI")) {
						if (!StringUtils.equalsIgnoreCase(
								sourceVersionOperator.getBusinessSettings().getBusinessServiceName(),
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

						inputMappingComparision(sourceVersionOperator.getInputMapping(),
								targetVersionOperator.getInputMapping(), fieldsList);
					}
					// CASE2: If LHS&RHS having same API external
					if (sourceVersionOperator.getBusinessSettings().getAPIInput().equalsIgnoreCase("ExternalAPI")) {
						if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getHttpMethod(),
								targetVersionOperator.getBusinessSettings().getHttpMethod())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.HTTP_METHOD,
									sourceVersionOperator.getBusinessSettings().getHttpMethod(),
									targetVersionOperator.getBusinessSettings().getHttpMethod()));
						}
						if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getInputBeType(),
								targetVersionOperator.getBusinessSettings().getInputBeType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
									sourceVersionOperator.getBusinessSettings().getInputBeType(),
									targetVersionOperator.getBusinessSettings().getInputBeType()));
						}
						if (!StringUtils.equalsIgnoreCase(
								sourceVersionOperator.getBusinessSettings().getSelectedInputOption(),
								targetVersionOperator.getBusinessSettings().getSelectedInputOption())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.SELECTED_INPUT_OPTION,
									sourceVersionOperator.getBusinessSettings().getSelectedInputOption(),
									targetVersionOperator.getBusinessSettings().getSelectedInputOption()));
						}
						if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getContentType(),
								targetVersionOperator.getBusinessSettings().getContentType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.CONTENT_TYPE_EXTERNAL,
									sourceVersionOperator.getBusinessSettings().getContentType(),
									targetVersionOperator.getBusinessSettings().getContentType()));
						}
						if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getApi(),
								targetVersionOperator.getBusinessSettings().getApi())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME,
									sourceVersionOperator.getBusinessSettings().getApi(),
									targetVersionOperator.getBusinessSettings().getApi()));
						}
					}

				} else {
					// CASE3: If RHS having internal service
					if (targetVersionOperator.getBusinessSettings().getAPIInput().equalsIgnoreCase("InternalAPI")) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.API_INPUT,
								sourceVersionOperator.getBusinessSettings().getAPIInput(),
								targetVersionOperator.getBusinessSettings().getAPIInput()));
						if (!StringUtils
								.isEmpty(targetVersionOperator.getBusinessSettings().getBusinessServiceName())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.BUSINESS_SERVICE_NAME,
									BPConstant.EMPTY_STRING,
									targetVersionOperator.getBusinessSettings().getBusinessServiceName()));
						}
						if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getApiName())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME, BPConstant.EMPTY_STRING,
									targetVersionOperator.getBusinessSettings().getApiName()));
						}
						if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getInputBeType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
									targetVersionOperator.getBusinessSettings().getInputBeType()));
						}

						for (InputParam targetInputParam : targetVersionOperator.getInputMapping()) {
							String newValue = checkInputParamSelectedKey(targetInputParam);
							if (!StringUtils.isEmpty(newValue)) {
								fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
										BPConstant.INPUT_MAPPING, targetInputParam.getContextVariable(),
										BPConstant.EMPTY_STRING, newValue));
							}
						}
					}
					// CASE4: If RHS having external service
					if (targetVersionOperator.getBusinessSettings().getAPIInput().equalsIgnoreCase("ExternalAPI")) {
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
								BPConstant.BUSINESS_SETTINGS, BPConstant.API_INPUT,
								sourceVersionOperator.getBusinessSettings().getAPIInput(),
								targetVersionOperator.getBusinessSettings().getAPIInput()));
						if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getHttpMethod())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.HTTP_METHOD, BPConstant.EMPTY_STRING,
									targetVersionOperator.getBusinessSettings().getHttpMethod()));
						}
						if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getInputBeType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
									targetVersionOperator.getBusinessSettings().getInputBeType()));
						}
						if (!StringUtils
								.isEmpty(targetVersionOperator.getBusinessSettings().getSelectedInputOption())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.SELECTED_INPUT_OPTION,
									BPConstant.EMPTY_STRING,
									targetVersionOperator.getBusinessSettings().getSelectedInputOption()));
						}
						if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getContentType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.CONTENT_TYPE_EXTERNAL,
									BPConstant.EMPTY_STRING,
									targetVersionOperator.getBusinessSettings().getContentType()));
						}
						if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getApi())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
									BPConstant.BUSINESS_SETTINGS, BPConstant.API_NAME, BPConstant.EMPTY_STRING,
									targetVersionOperator.getBusinessSettings().getApi()));
						}
					}
				}
				// Properties
				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, false);
				// Comments
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, false);
			}
			return fieldsList;

		} catch (Exception exception) {
			LOGGER.error("Error occurred while comparing rest channel operator"+exception);
			throw new BPException(exception.getMessage());
		}
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
