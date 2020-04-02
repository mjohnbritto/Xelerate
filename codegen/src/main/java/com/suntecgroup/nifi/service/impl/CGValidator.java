package com.suntecgroup.nifi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.suntecgroup.nifi.config.CGConnectionValidatorConfiguration;
import com.suntecgroup.nifi.config.CGValidatorConfigurationProperty;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.BPValidation;
import com.suntecgroup.nifi.frontend.bean.BusinessEntity;
import com.suntecgroup.nifi.frontend.bean.BusinessEntityAttributeProperty;
import com.suntecgroup.nifi.frontend.bean.BusinessProcessSetup;
import com.suntecgroup.nifi.frontend.bean.BusinessSettings;
import com.suntecgroup.nifi.frontend.bean.ContextParameters;
import com.suntecgroup.nifi.frontend.bean.DataType;
import com.suntecgroup.nifi.frontend.bean.Decisions;
import com.suntecgroup.nifi.frontend.bean.Functional;
import com.suntecgroup.nifi.frontend.bean.InputConnection;
import com.suntecgroup.nifi.frontend.bean.InputParam;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.frontend.bean.OutputParam;
import com.suntecgroup.nifi.frontend.bean.PVValue;
import com.suntecgroup.nifi.frontend.bean.ProcessVariables;
import com.suntecgroup.nifi.frontend.bean.Property;
import com.suntecgroup.nifi.frontend.bean.Type;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.Attribute;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.Content;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.FixedWidth;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.Footer;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.Header;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.OutputFileName;
import com.suntecgroup.nifi.service.CGValidatorIntf;

@Service
@Component
public class CGValidator implements CGValidatorIntf {

	private final Logger logger = LoggerFactory.getLogger(CGValidator.class);

	@Autowired
	CGValidatorConfigurationProperty validatorProperty;

	@Autowired
	CGConnectionValidatorConfiguration connValidatorProperty;

	/**
	 * 
	 * This method would validate the incoming diagram json and return the
	 * BPValidation object with list of errors.
	 * 
	 * @param bpFlowRequest
	 *            - object of BPFLowUI
	 * 
	 * @return BPValidation object with list of errors
	 * 
	 */
	public BPValidation validateInputJson(final BPFlowUI bpFlowRequest) {

		BPValidation bpValidationObj = new BPValidation();
		List<Map<String, String>> errorList = new ArrayList<Map<String, String>>();

		try {
			if (validatorProperty == null || connValidatorProperty == null) {
				throw new CGException("Exception due to validation.properties file not loaded!!!");
			} else {
				bpValidationObj = beginValidate(bpFlowRequest, errorList);
				logger.info("Validation completed!");
			}
		} catch (CGException ex) {
			logger.error("Exceptiion occured: " + ex.getMessage(), ex);

		}

		return bpValidationObj;

	}

	private BPValidation beginValidate(final BPFlowUI bpFlowRequest, List<Map<String, String>> errorList) {

		BPValidation bpValidationObj = new BPValidation();
		Map<String, String> operatorKeyType = new LinkedHashMap<String, String>();
		List<Map<String, String>> connectionErrorLst = null;

		try {
			validateBusinessProcessSetup(bpFlowRequest.getConfigureBusinessProcess().getFunctional(), errorList);
			validateOperators(bpFlowRequest, bpFlowRequest.getOperators(), errorList, operatorKeyType);

			if (bpFlowRequest.getConnections() != null && bpFlowRequest.getConnections().size() > 0) {
				CGConnectionValidator connValidator = new CGConnectionValidator();
				connectionErrorLst = connValidator.validateConnections(bpFlowRequest, operatorKeyType,
						validatorProperty, connValidatorProperty);
				if (connectionErrorLst != null && connectionErrorLst.size() > 0) {
					errorList.addAll(connectionErrorLst);
				}
			}

			else {
				errorList.add(addError(CGConstants.CONNECTIONS, CGConstants.CONNECTIONS,
						connValidatorProperty.getNoConnection()));
			}
		} catch (Exception ex) {
			logger.error("Exception occured at validation: ", ex);
		} finally {
			if (errorList != null && errorList.size() > 0) {
				bpValidationObj.setValidationError(errorList);
			}
		}

		return bpValidationObj;
	}

	private Map<String, String> addError(final String category, final String source, final String description) {
		Map<String, String> errorMap = new LinkedHashMap<String, String>();
		errorMap.put(CGConstants.ERROR_CATEGORY, category);
		errorMap.put(CGConstants.ERROR_SOURCE, source);
		errorMap.put(CGConstants.ERROR_DESCRIPTION, description);
		return errorMap;
	}

	private void validateBusinessProcessSetup(final Functional bpSetup, List<Map<String, String>> errorList) {

		if (bpSetup.getBusinessProcessSetup() != null) {
			BusinessProcessSetup bpObj = bpSetup.getBusinessProcessSetup();

			if (isEmpty(bpObj.getProcessName())) {
				errorList.add(addError(CGConstants.BUSINESS_PROCESS_SETUP, CGConstants.BUSINESS_PROCESS_SETUP,
						validatorProperty.getBusinessProcessName()));
			}

			if (isEmpty(bpObj.getProcessDescription())) {
				errorList.add(addError(CGConstants.BUSINESS_PROCESS_SETUP, CGConstants.BUSINESS_PROCESS_SETUP,
						validatorProperty.getBusinessProcessDescription()));
			}
		} else {

			errorList.add(addError(CGConstants.BUSINESS_PROCESS_SETUP, CGConstants.BUSINESS_PROCESS_SETUP,
					validatorProperty.getBusinessProcessEmpty()));
		}

		List<ProcessVariables> processVariablesLst = bpSetup.getProcessVariables();

		if (processVariablesLst != null) {
			List<String> processVariableNames = new ArrayList<String>();
			for (ProcessVariables processVariables : processVariablesLst) {
				boolean isMandatory = isProcessVariableMandatory(processVariables);

				if (processVariables.getName() != null && processVariables.getName().length() > 0) {
					processVariableNames.add(processVariables.getName());
				}

				if (isEmpty(processVariables.getName())) {
					errorList.add(addError(CGConstants.BUSINESS_PROCESS_SETUP,
							CGConstants.PROCESS_VARIABLE + " " + CGConstants.NAME,
							validatorProperty.getProcessVariableName()));
				}

				if (isMandatory && isEmpty(processVariables.getDescription())) {

					errorList.add(addError(CGConstants.BUSINESS_PROCESS_SETUP,
							CGConstants.PROCESS_VARIABLE + " " + CGConstants.DESCRIPTION,
							validatorProperty.getProcessVariableDescription()));
				}

				if (isMandatory && isEmpty(processVariables.getType().getTypeName())) {

					errorList.add(addError(CGConstants.BUSINESS_PROCESS_SETUP,
							CGConstants.PROCESS_VARIABLE + " " + CGConstants.TYPE,
							validatorProperty.getProcessVariableType()));

				}

				if (isMandatory && "STRING".equals(processVariables.getType().getTypeName().toUpperCase())
						&& isEmpty(processVariables.getValue().getStringValue())) {

					errorList.add(addError(CGConstants.PROCESS_VARIABLE, CGConstants.VALUE,
							validatorProperty.getProcessVariableMandatoryStringValue()));
				}

				if (isMandatory && "NUMBER".equals(processVariables.getType().getTypeName().toUpperCase())) {

					// can contain float value as well.

					if (NumberUtils.isCreatable(processVariables.getValue().getIntValue())) {

						if (validateNumberforPrecisionScale(processVariables.getValue().getIntValue(),
								processVariables.getValue().getPrecision(), processVariables.getValue().getScale())) {

							errorList.add(addError(CGConstants.PROCESS_VARIABLE, CGConstants.VALUE,
									validatorProperty.getProcessVariableInitValueError() + " "
											+ processVariables.getName()));
						}
					} else {
						errorList.add(addError(CGConstants.PROCESS_VARIABLE, CGConstants.VALUE,
								validatorProperty.getProcessVariableMandatoryInitValue()));

					}
				}

				if (isMandatory && "DATETIME".equals(processVariables.getType().getTypeName().toUpperCase())
						&& isEmpty(processVariables.getValue().getDateValue())) {

					errorList.add(addError(CGConstants.PROCESS_VARIABLE, CGConstants.VALUE,
							validatorProperty.getProcessVariableMandatoryDateValue()));
				}
			}

			if (processVariableNames != null && processVariableNames.size() > 0) {
				validateDuplicateNames(processVariableNames, CGConstants.BUSINESS_PROCESS_SETUP,
						CGConstants.PROCESS_VARIABLE, errorList);
			}
		} else {

			errorList.add(addError("Business Process Setup", CGConstants.PROCESS_VARIABLE,
					validatorProperty.getProcessVariableArray()));
		}
	}

	private boolean isProcessVariableMandatory(ProcessVariables processVariables) {
		boolean isMandatory = processVariables.getFlags().isIsMandatory();
		boolean isProfilableSolution = processVariables.getFlags().isIsProfileableAtSolutions();
		boolean isProfilableOperation = processVariables.getFlags().isIsProfileableAtOperation();

		/*
		 * If Profileable Solution or Profileable Operation is true, then skip
		 * the mandatory check even if, Mandatory flag has the value true.
		 */
		if (isProfilableSolution || isProfilableOperation) {
			isMandatory = false;
		}
		return isMandatory;
	}

	private void validateOperators(BPFlowUI bpFlowRequest, List<Operators> operatorsList,
			List<Map<String, String>> errorList, Map<String, String> operatorKeyType) {
		int inputChannelOpCounter = 0;
		int startCounter = 0;
		int operatorPosition = 0;
		int endCounter = 0;
		boolean inputFileBatchingEnabled = false;
		//boolean hasMerge = false;
		List<String> operatorNames = new ArrayList<String>();

		if (operatorsList != null && operatorsList.size() > 0) {
			for (Operators operator : operatorsList) {
				if (operator.getName() != null && operator.getName().length() > 0) {
					operatorNames.add(operator.getName());
				}
				switch (operator.getType()) {

				case CGConstants.FILE_CHANNEL_INTEGRATION_INPUT: {
					validateFileChannelIntegration(operator, operatorPosition, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					inputChannelOpCounter++;
					if (!inputFileBatchingEnabled) {
						inputFileBatchingEnabled = operator.getBusinessSettings().isBatchable();
					}
					break;
				}

				case CGConstants.REST_INPUT_CHANNEL_INTEGRATION: {
					validateRestChannelIntegration(operator, operatorPosition, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					inputChannelOpCounter++;
					break;
				}

				case CGConstants.START: {
					startCounter = startCounter + 1;
					validateStartOperator(operator, startCounter, operatorPosition, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					break;
				}

				case CGConstants.INVOKE_BS: {
					validateInvokeBSOperator(bpFlowRequest, operator, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					break;
				}

				case CGConstants.INVOKE_BS_EXTERNAL: {
					validateInvokeBSExOperator(bpFlowRequest, operator, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					break;
				}

				case CGConstants.DECISION_MATRIX_EXCLUSIVE: {
					validateDecisionMatrix(operator, CGConstants.DECISION_MATRIX_EXCLUSIVE,
							CGConstants.DECISION_MATRIX_EXCLUSIVE_CATEGORY, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					break;
				}

				case CGConstants.DECISION_MATRIX_INCLUSIVE: {
					validateDecisionMatrix(operator, CGConstants.DECISION_MATRIX_INCLUSIVE,
							CGConstants.DECISION_MATRIX_INCLUSIVE_CATEGORY, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					break;
				}

				case CGConstants.JOIN: {
					validateJoinOperator(operator, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					break;
				}

				case CGConstants.MERGE: {
					validateMerge(bpFlowRequest, operator, operatorPosition, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					//hasMerge = true;
					break;
				}

				case CGConstants.END: {
					endCounter = endCounter + 1;
					validateEndOperator(operator, endCounter, operatorPosition, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					break;
				}

				case CGConstants.FILE_CHANNEL_INTEGRATION_OUTPUT: {
					validateFileChannelIntegration(operator, operatorPosition, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					break;
				}

				case CGConstants.REST_OUTPUT_CHANNEL_INTEGRATION: {
					validateRestChannelIntegration(operator, operatorPosition, errorList, operatorKeyType);
					operatorPosition = operatorPosition + 1;
					break;
				}
				}
			}

			if (inputChannelOpCounter == 0) {
				errorList.add(addError(CGConstants.OPERATOR, CGConstants.OPERATOR,
						validatorProperty.getNoInputChannelOperator()));
			}
			/*if (inputFileBatchingEnabled && hasMerge) {
				errorList.add(addError(CGConstants.OPERATOR, CGConstants.INPUT_FILE_CHANNEL_INTEGRATION,
						validatorProperty.getMergeWithFileInputBatchingError()));
			}*/
			validateDuplicateNames(operatorNames, CGConstants.OPERATOR, CGConstants.OPERATOR, errorList);
		} else {
			errorList.add(addError(CGConstants.OPERATOR, CGConstants.OPERATOR, validatorProperty.getNoOperator()));
		}
	}

	private void validateMerge(BPFlowUI bpFlowRequest, Operators operator, int operatorPosition,
			List<Map<String, String>> errorList, Map<String, String> operatorKeyType) {

		String category = CGConstants.MERGE_CATEGORY;

		getOperatorKeyType(operator, operatorKeyType);

		if (isEmpty(operator.getName())) {
			errorList.add(addError(category, CGConstants.NAME, validatorProperty.getOperatorName()));
			return;
		}

		if (isEmpty(operator.getKey())) {
			errorList.add(
					addError(category, CGConstants.KEY, validatorProperty.getOperatorKey() + " " + operator.getName()));
		}

		if (isEmpty(operator.getType())) {
			errorList.add(addError(category, CGConstants.TYPE,
					validatorProperty.getOperatorType() + " " + operator.getName()));
		}

		// input BE
		if (!isEmpty(operator.getBusinessSettings().getInputBeType())) {
			errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
					validatorProperty.getMergeExpectedNoInputBE() + " " + operator.getName()));
		}

		// output BE and if present, BUK
		if (!isEmpty(operator.getBusinessSettings().getOutputBeType())) {
			List<String> outputBukAttributes = operator.getBusinessSettings().getOutputBEBUKAttributes();
			if (outputBukAttributes == null || outputBukAttributes.size() < 1) {
				errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
						validatorProperty.getOutputBUK() + " " + operator.getName()));
			}
		}

		if (isEmpty(operator.getBusinessSettings().getBusinessServiceName())) {
			errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
					operator.getName() + " " + validatorProperty.getInvokebsBusinessServiceName()));
		}

		if (isEmpty(operator.getBusinessSettings().getApiName())) {
			errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
					operator.getName() + " " + validatorProperty.getInvokebsApiName()));
		}

		List<String> cvNames = operator.getBusinessSettings().getExpectedInputChannel();

		if (cvNames != null && cvNames.size() > 0) {
			for (String cvName : cvNames) {
				boolean foundCVAssignment = false;
				for (InputConnection iConn : operator.getBusinessSettings().getInputConnections()) {
					if (cvName.equals(iConn.getContextVariable())) {
						foundCVAssignment = true;
						break;
					}
				}
				if (!foundCVAssignment) {
					errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
							operator.getName() + " " + validatorProperty.getMergeExpectedInputChannelMismatch()));
					break;
				}
			}
		}

		for (InputConnection iConn : operator.getBusinessSettings().getInputConnections()) {
			if (isEmpty(iConn.getContextVariable())) {
				errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
						operator.getName() + " " + validatorProperty.getMergeUnassignedContextVariables()));
				break;
			}
		}

		// properties validation
		List<Property> propObj = operator.getProperties();

		if (propObj != null && propObj.size() > 0) {
			for (Property property : propObj) {
				validateProperty(property, operator.getName(), category, errorList);
			}
		} else {
			errorList.add(addError(CGConstants.OPERATOR, category,
					validatorProperty.getNoPropertyFound() + " " + operator.getName()));
		}

		validateOutputMapping(bpFlowRequest, operator, errorList, operator.getOutputMapping(), true);
	}

	private void validateFileChannelIntegration(Operators operator, int operatorPosition,
			List<Map<String, String>> errorList, Map<String, String> operatorKeyType) {

		getOperatorKeyType(operator, operatorKeyType);

		String category = CGConstants.FILE_CHANNEL_INTEGRATION_INPUT.equalsIgnoreCase(operator.getType())
				? CGConstants.INPUT_FILE_CHANNEL_INTEGRATION : CGConstants.OUTPUT_FILE_CHANNEL_INTEGRATION;

		if (isEmpty(operator.getName())) {
			errorList.add(addError(category, CGConstants.NAME, validatorProperty.getOperatorName()));
			return;
		}

		if (isEmpty(operator.getKey())) {
			errorList.add(
					addError(category, CGConstants.KEY, validatorProperty.getOperatorKey() + " " + operator.getName()));
		}

		if (isEmpty(operator.getType())) {
			errorList.add(addError(category, CGConstants.TYPE,
					validatorProperty.getOperatorType() + " " + operator.getName()));
		}

		if (null != operator.getBusinessSettings()) {
			// output BE and if present, BUK
			if (!isEmpty(operator.getBusinessSettings().getOutputBeType())) {
				List<String> outputBukAttributes = operator.getBusinessSettings().getOutputBEBUKAttributes();
				if (outputBukAttributes == null || outputBukAttributes.size() < 1) {
					errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
							validatorProperty.getOutputBUK() + " " + operator.getName()));
				}
			}

			if (operator.getBusinessSettings().isBatchable()) {
				if (Integer.parseInt(operator.getBusinessSettings().getBatchSize()) < 1) {
					errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
							validatorProperty.getInvalidBatchSize() + " " + operator.getName()));
				}
			}
		}

		// properties validation
		List<Property> propObj = operator.getProperties();

		if (propObj != null && propObj.size() > 0) {
			for (Property property : propObj) {
				validateProperty(property, operator.getName(), category, errorList);
			}
		} else {
			errorList.add(addError(CGConstants.OPERATOR, category,
					validatorProperty.getNoPropertyFound() + " " + operator.getName()));
		}

		// header validation
		validateFileChannelIntegrationHeader(operator, errorList);

		// footer validation
		validateFileChannelIntegrationFooter(operator, errorList);

		// mapping validation
		validateFileChannelIntegrationMapping(operator, errorList);

		// content validation
		validateFileChannelIntegrationContent(operator, errorList);

		if (CGConstants.INPUT_FILE_CHANNEL_INTEGRATION.equals(category)) {
			if (null != operator.getValidation()) {
				if (null != operator.getValidation().getFileNameDuplication()) {
					if (!StringUtils.isBlank(operator.getValidation().getFileNameDuplication().getCount())) {
						if (StringUtils.isBlank(operator.getValidation().getFileNameDuplication().getDuration())) {
							errorList.add(addError(CGConstants.OPERATOR, category,
									validatorProperty.getFileChannelIntegrationInputFileDuplicationDurationUnits() + " "
											+ operator.getName()));
						}
					}
				}
			}
		} else if (CGConstants.OUTPUT_FILE_CHANNEL_INTEGRATION.equals(category)) {
			OutputFileName fileName = operator.getOutputFileName();

			if (null != fileName) {
				if (StringUtils.isBlank(fileName.getDynamicName()) && StringUtils.isBlank(fileName.getStaticName())) {
					errorList.add(addError(CGConstants.OPERATOR, category,
							validatorProperty.getFileChannelIntegrationOutputFileName() + " " + operator.getName()));
				}
			}
		}

	}

	private void validateRestChannelIntegration(Operators operator, int operatorPosition,
			List<Map<String, String>> errorList, Map<String, String> operatorKeyType) {

		String category = CGConstants.REST_INPUT_CHANNEL_INTEGRATION.equalsIgnoreCase(operator.getType())
				? CGConstants.INPUT_REST_CHANNEL_INTEGRATION : CGConstants.OUTPUT_REST_CHANNEL_INTEGRATION;

		getOperatorKeyType(operator, operatorKeyType);

		if (isEmpty(operator.getName())) {
			errorList.add(addError(category, CGConstants.NAME, validatorProperty.getOperatorName()));
			return;
		}

		if (isEmpty(operator.getKey())) {
			errorList.add(
					addError(category, CGConstants.KEY, validatorProperty.getOperatorKey() + " " + operator.getName()));
		}

		if (isEmpty(operator.getType())) {
			errorList.add(addError(category, CGConstants.TYPE,
					validatorProperty.getOperatorType() + " " + operator.getName()));
		}

		// properties validation
		List<Property> propObj = operator.getProperties();

		if (propObj != null && propObj.size() > 0) {
			for (Property property : propObj) {
				validateProperty(property, operator.getName(), category, errorList);
				if (operator.getBusinessSettings().isSecuredAPI()) {
					if (CGConstants.OUTPUT_REST_CHANNEL_INTEGRATION.equals(category)) {
						if (CGConstants.USERNAME.equals(property.getName())) {
							if (isEmpty(property.getValue())) {
								errorList
										.add(addError(CGConstants.OPERATOR, CGConstants.OUTPUT_REST_CHANNEL_INTEGRATION,
												validatorProperty.getPropertyUsername() + " " + operator.getName()));
							}
						}
						if (CGConstants.PASSWORD.equals(property.getName())) {
							if (isEmpty(property.getValue())) {
								errorList
										.add(addError(CGConstants.OPERATOR, CGConstants.OUTPUT_REST_CHANNEL_INTEGRATION,
												validatorProperty.getPropertyPassword() + " " + operator.getName()));
							}
						}
					}
				}
			}
		} else {
			errorList.add(addError(CGConstants.OPERATOR, category,
					validatorProperty.getNoPropertyFound() + " " + operator.getName()));
		}

	}

	private void validateFileChannelIntegrationHeader(Operators operator, List<Map<String, String>> errorList) {
		try {
			Header header = operator.getHeader();

			String category = CGConstants.FILE_CHANNEL_INTEGRATION_INPUT.equalsIgnoreCase(operator.getType())
					? CGConstants.INPUT_FILE_CHANNEL_INTEGRATION : CGConstants.OUTPUT_FILE_CHANNEL_INTEGRATION;

			if (header.isHasHeader()) {
				int headerLinesCount = Integer.parseInt(header.getHeaderLines());
				if (headerLinesCount < 1) {
					errorList.add(addError(category, CGConstants.HEADER,
							validatorProperty.getFileChannelIntegrationHeaderLines() + " " + operator.getName()));
					return;
				}
				if (header.getFixedWidth() != null && header.getFixedWidth().size() >= 1) {
					// fixed width attributed input file header validation
					Set<Integer> headerLineNumbersDefined = new HashSet<Integer>();
					List<FixedWidth> fixedWidth = header.getFixedWidth();
					boolean dataTypeUndefinedForSomeHeaderLines = false;
					for (int i = 0; i < fixedWidth.size(); i++) {
						if (fixedWidth.get(i) != null) {
							headerLineNumbersDefined.add(Integer.parseInt(fixedWidth.get(i).getLineNumber()));
						}
						if (StringUtils.isBlank(fixedWidth.get(i).getDataType())) {
							dataTypeUndefinedForSomeHeaderLines = true;
						}
					}
					if (dataTypeUndefinedForSomeHeaderLines) {
						errorList.add(addError(category, CGConstants.HEADER,
								"Data type undefined in one or more header line(s) of '" + operator.getName() + "'."));
					}
					if (headerLineNumbersDefined.size() != headerLinesCount) {
						errorList.add(addError(category, CGConstants.HEADER,
								validatorProperty.getFileChannelIntegrationHeaderLinesMismatching() + " "
										+ operator.getName()));
					}
				} else if (header.getDelimited() != null) {
					// delimited input file header validation
					List<Attribute> attributes = header.getDelimited().getAttributes();
					if (attributes.size() < 1) {
						errorList.add(addError(category, CGConstants.HEADER,
								validatorProperty.getFileChannelIntegrationHeaderDelimitedAttributeNotFound() + " "
										+ operator.getName()));
					} else {
						boolean dataTypeUndefinedForSomeHeaderLines = false;
						for (int i = 0; i < attributes.size(); i++) {
							if (attributes.get(i) != null) {
								if (StringUtils.isBlank(attributes.get(i).getSegmentPosition())) {
									errorList.add(addError(category, CGConstants.HEADER,
											validatorProperty
													.getFileChannelIntegrationHeaderDelimitedMissingSegmentPos() + " "
													+ operator.getName()));
								}
								if (StringUtils.isBlank(attributes.get(i).getLineNumber())) {
									errorList.add(addError(category, CGConstants.HEADER,
											validatorProperty
													.getFileChannelIntegrationHeaderDelimitedMissingLineNumber() + " "
													+ operator.getName()));
								}
								if (StringUtils.isBlank(attributes.get(i).getDataType())) {
									dataTypeUndefinedForSomeHeaderLines = true;
								}
							}
						}
						if (dataTypeUndefinedForSomeHeaderLines) {
							errorList.add(addError(category, CGConstants.HEADER,
									"Data type undefined in one or more header line(s) of '" + operator.getName()
											+ "'."));
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	private void validateFileChannelIntegrationFooter(Operators operator, List<Map<String, String>> errorList) {
		try {
			Footer footer = operator.getFooter();

			String category = CGConstants.FILE_CHANNEL_INTEGRATION_INPUT.equalsIgnoreCase(operator.getType())
					? CGConstants.INPUT_FILE_CHANNEL_INTEGRATION : CGConstants.OUTPUT_FILE_CHANNEL_INTEGRATION;

			if (footer.isHasFooter()) {
				int footerLinesCount = Integer.parseInt(footer.getFooterLines());
				if (footerLinesCount < 1) {
					errorList.add(addError(category, CGConstants.FOOTER,
							validatorProperty.getFileChannelIntegrationFooterLines() + " " + operator.getName()));
					return;
				}
				if (footer.getFixedWidth() != null && footer.getFixedWidth().size() >= 1) {
					// fixed width attributed input file footer validation
					Set<Integer> footerLineNumbersDefined = new HashSet<Integer>();
					List<FixedWidth> fixedWidth = footer.getFixedWidth();
					boolean dataTypeUndefinedForSomeFooterLines = false;
					for (int i = 0; i < fixedWidth.size(); i++) {
						if (fixedWidth.get(i) != null) {
							footerLineNumbersDefined.add(Integer.parseInt(fixedWidth.get(i).getLineNumber()));
						}
						if (StringUtils.isBlank(fixedWidth.get(i).getDataType())) {
							dataTypeUndefinedForSomeFooterLines = true;
						}
					}
					if (dataTypeUndefinedForSomeFooterLines) {
						errorList.add(addError(category, CGConstants.FOOTER,
								"Data type undefined in one or more footer line(s) of '" + operator.getName() + "'."));
					}
					if (footerLineNumbersDefined.size() != footerLinesCount) {
						errorList.add(addError(category, CGConstants.FOOTER,
								validatorProperty.getFileChannelIntegrationFooterLinesMismatching() + " "
										+ operator.getName()));
					}
				} else if (footer.getDelimited() != null) {
					// delimited input file footer validation
					List<Attribute> attributes = footer.getDelimited().getAttributes();
					if (attributes.size() < 1) {
						errorList.add(addError(category, CGConstants.FOOTER,
								validatorProperty.getFileChannelIntegrationFooterDelimitedAttributeNotFound() + " "
										+ operator.getName()));
					} else {
						boolean dataTypeUndefinedForSomeFooterLines = false;
						for (int i = 0; i < attributes.size(); i++) {
							if (attributes.get(i) != null) {
								if (StringUtils.isBlank(attributes.get(i).getSegmentPosition())) {
									errorList.add(addError(category, CGConstants.FOOTER,
											validatorProperty
													.getFileChannelIntegrationFooterDelimitedMissingSegmentPos() + " "
													+ operator.getName()));
								}
								if (StringUtils.isBlank(attributes.get(i).getLineNumber())) {
									errorList.add(addError(category, CGConstants.FOOTER,
											validatorProperty
													.getFileChannelIntegrationFooterDelimitedMissingLineNumber() + " "
													+ operator.getName()));
								}
								if (StringUtils.isBlank(attributes.get(i).getDataType())) {
									dataTypeUndefinedForSomeFooterLines = true;
								}
							}
						}
						if (dataTypeUndefinedForSomeFooterLines) {
							errorList.add(addError(category, CGConstants.FOOTER,
									"Data type undefined in one or more footer line(s) of '" + operator.getName()
											+ "'."));
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	private void validateFileChannelIntegrationMapping(Operators operator, List<Map<String, String>> errorList) {
		try {
			List<com.suntecgroup.nifi.frontend.bean.filechannelintegration.Mapping> mapping = operator.getMapping();
			String category = CGConstants.FILE_CHANNEL_INTEGRATION_INPUT.equalsIgnoreCase(operator.getType())
					? CGConstants.INPUT_FILE_CHANNEL_INTEGRATION : CGConstants.OUTPUT_FILE_CHANNEL_INTEGRATION;
			if (null != mapping && mapping.size() < 1) {
				if (CGConstants.INPUT_FILE_CHANNEL_INTEGRATION.equals(category)) {
					errorList.add(addError(category, CGConstants.MAPPING,
							validatorProperty.getFileChannelIntegrationInputMappingNotFound() + " "
									+ operator.getName()));
				} else {
					errorList.add(addError(category, CGConstants.MAPPING,
							validatorProperty.getFileChannelIntegrationOutputMappingNotFound() + " "
									+ operator.getName()));
				}
			}
		} catch (Exception e) {
		}
	}

	private void validateFileChannelIntegrationContent(Operators operator, List<Map<String, String>> errorList) {
		try {
			Content content = operator.getContent();

			String category = CGConstants.FILE_CHANNEL_INTEGRATION_INPUT.equalsIgnoreCase(operator.getType())
					? CGConstants.INPUT_FILE_CHANNEL_INTEGRATION : CGConstants.OUTPUT_FILE_CHANNEL_INTEGRATION;

			if (content.getFixedWidth() != null && content.getFixedWidth().size() >= 1) {
				// fixed width attributed input file header validation
			} else if (content.getDelimited() != null) {
				// delimited input file header validation
				List<Attribute> attributes = content.getDelimited().getAttributes();
				if (attributes.size() < 1) {
					errorList.add(addError(category, CGConstants.CONTENT,
							validatorProperty.getFileChannelIntegrationContentDelimitedAttributeNotFound() + " "
									+ operator.getName()));
				} else {
					for (int i = 0; i < attributes.size(); i++) {
						if (attributes.get(i) != null) {
							if (StringUtils.isBlank(attributes.get(i).getSegmentPosition())) {
								errorList.add(addError(category, CGConstants.CONTENT,
										validatorProperty.getFileChannelIntegrationContentDelimitedMissingSegmentPos()
												+ " " + operator.getName()));
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	private void validateStartOperator(Operators operator, int startOperatorCnt, int startOperatorPosition,
			List<Map<String, String>> errorList, Map<String, String> operatorKeyType) {

		getOperatorKeyType(operator, operatorKeyType);

		if (startOperatorCnt > 1) {
			errorList.add(addError(CGConstants.START_OPERATOR, CGConstants.START_OPERATOR,
					validatorProperty.getMultipleStart()));
		}

		if (isEmpty(operator.getName())) {
			errorList.add(addError(CGConstants.START_OPERATOR, CGConstants.NAME, validatorProperty.getOperatorName()));
			return;
		}

		if (isEmpty(operator.getKey())) {
			errorList.add(addError(CGConstants.START_OPERATOR, CGConstants.KEY,
					validatorProperty.getOperatorKey() + " " + operator.getName()));
		}
		if (isEmpty(operator.getType())) {
			errorList.add(addError(CGConstants.START_OPERATOR, CGConstants.TYPE,
					validatorProperty.getOperatorType() + " " + operator.getName()));
		}

		if (!isEmpty(operator.getBusinessSettings().getInputBeType())) {
			List<String> inputBukAttributes = operator.getBusinessSettings().getInputBEBUKAttributes();
			if (inputBukAttributes == null || inputBukAttributes.size() < 1) {
				errorList.add(addError(CGConstants.START_OPERATOR, CGConstants.BUSINESS_SETTINGS,
						validatorProperty.getStartInputBUK() + " " + operator.getName()));
			}
		}

		if (!operator.getBusinessSettings().isEventLogging()) {
			errorList.add(addError(CGConstants.START_OPERATOR, CGConstants.BUSINESS_SETTINGS,
					validatorProperty.getStartEventLogging() + " " + operator.getName()));
		}

		// storing operatorName and Type for further use.
		List<Property> propObj = operator.getProperties();

		if (propObj != null && propObj.size() > 0) {
			for (Property property : propObj) {
				validateProperty(property, operator.getName(), CGConstants.START_OPERATOR, errorList);
			}
		} else {
			errorList.add(addError(CGConstants.OPERATOR, CGConstants.START_OPERATOR,
					validatorProperty.getNoPropertyFound() + " " + operator.getName()));
		}
	}

	/**
	 * 
	 * This method would validate the input mapping in the requesting json.
	 * example : pv->cv (or) ev->cv
	 * 
	 * @param bpFlowRequest
	 * @param operator
	 * 
	 */

	private void validateInputMapping(final BPFlowUI bpFlowRequest, final Operators operator,
			List<Map<String, String>> errorList) {

		List<ProcessVariables> processVariableList = bpFlowRequest.getConfigureBusinessProcess().getFunctional()
				.getProcessVariables();

		List<ContextParameters> serviceContext = operator.getBusinessSettings().getServiceContextParameters();
		List<ContextParameters> apiContext = operator.getBusinessSettings().getApiContextParameters();

		List<InputParam> inputMappingList = operator.getInputMapping();

		if ((serviceContext != null && serviceContext.size() > 0) || (apiContext != null && apiContext.size() > 0)) {

			if (inputMappingList != null && processVariableList != null) {
				for (InputParam ipObj : inputMappingList) {

					String contextVariableName = ipObj.getName();
					String selectedKey = ipObj.getSelectedKey();
					
					if (!ipObj.isMandatory()) {
						continue;
					}
					
					if (isEmpty(contextVariableName)) {

						errorList.add(addError(CGConstants.INPUT_MAPPING, "Context Variable Name",
								validatorProperty.getInvokebsContextVariableName() + " " + operator.getName()));
					}

					if (isEmpty(ipObj.getType())) {

						errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.TYPE,
								validatorProperty.getOperatorType() + " " + contextVariableName + " for " + operator.getName()));
					}

					if (isEmpty(selectedKey)) {

						errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.SELECTED_KEY,
								validatorProperty.getInvokebsSelectedKey() + " " + contextVariableName + " for " + operator.getName()));
					}

					ContextParameters destContextParameter = getContextParameter(serviceContext, apiContext,
							contextVariableName, CGConstants.INPUT_MAPPING, operator.getName(), errorList);

					// case 1 - If the Input Mapping Context Variable has
					// process
					// variable.

					if (destContextParameter != null && selectedKey != null
							&& selectedKey.equalsIgnoreCase(CGConstants.PROCESS_VARIABLE)) {

						prepareProcessToContext(processVariableList, destContextParameter,
								ipObj.getMappedProcessVariable(), contextVariableName, operator.getName(), errorList);
					}

					// case 2 - If the Input Mapping Context Variable has User
					// entered Value.

					if (destContextParameter != null && selectedKey != null
							&& selectedKey.toUpperCase().equals("ENTER VALUE")) {

						prepareEnteredValueToContext(ipObj.getInputParamvalue(), destContextParameter, ipObj.getType(),
								contextVariableName, operator.getName(), errorList);
					}
				}
			} else {
				errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.INPUT_MAPPING,
						validatorProperty.getInvokebsIPMEmptyInputMapping() + " " + operator.getName()));
			}
		}
	}

	private void prepareProcessToContext(List<ProcessVariables> processVariableList,
			ContextParameters destContextParamater, String mappedProcessVariable, String contextVariableName, String operatorName,
			List<Map<String, String>> errorList) {

		ProcessVariables srcProcessVariable = null;

		if (isEmpty(mappedProcessVariable)) {

			errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.PROCESS_VARIABLE,
					validatorProperty.getInvokebsProcessVariableName() + " " + contextVariableName + " for "
							+ operatorName));
		}

		for (ProcessVariables processVariableObj : processVariableList) {

			if (processVariableObj.getName() != null && processVariableObj.getName().equals(mappedProcessVariable)) {
				srcProcessVariable = processVariableObj;
				break;
			}
		}

		if (srcProcessVariable == null) {

			errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.PROCESS_VARIABLE, mappedProcessVariable + " "
					+ validatorProperty.getInvokebsIPMProcessVariableNotFound() + " for " + operatorName));
		}

		if (destContextParamater != null && srcProcessVariable != null) {
			validateContextAndProcessForIP(destContextParamater, srcProcessVariable, operatorName, errorList);
		}
	}

	private ContextParameters getContextParameter(List<ContextParameters> serviceContext,
			List<ContextParameters> apiContext, String contextVariableName, String category, String operatorName,
			List<Map<String, String>> errorList) {

		boolean contextAdded = false;
		ContextParameters contextParamObj = null;

		// Search the context parameter data from apiContext, if not
		// present, fetch from serviceContext

		if (apiContext != null) {

			for (ContextParameters apiContextObj : apiContext) {
				if (apiContextObj.getName() != null && apiContextObj.getName().equals(contextVariableName)) {
					contextParamObj = apiContextObj;
					contextAdded = true;
					break;
				}
			}
		}

		if (serviceContext != null && !contextAdded) {

			for (ContextParameters serviceContextObj : serviceContext) {
				if (serviceContextObj.getName() != null && serviceContextObj.getName().equals(contextVariableName)) {
					contextParamObj = serviceContextObj;
					contextAdded = true;
					break;
				}
			}
		}

		return contextParamObj;
	}

	private void validateContextAndProcessForIP(ContextParameters destCP, ProcessVariables sourcePV, String operatorName,
			List<Map<String, String>> errorList) {

		boolean isMandatory = isProcessVariableMandatory(sourcePV);
		String destContextParamaterType = destCP.getType();
		Type sourceDataType = sourcePV.getType();

		boolean isValid = isValidType(destContextParamaterType, destCP.getName(), sourceDataType.getTypeName(),
				" Process Variable " + sourcePV.getName(), operatorName, errorList);

		if (isMandatory) {
			if (isValid && destContextParamaterType.equals(sourceDataType.getTypeName())) {
				PVValue destValue = destCP.getValue();

				if (destContextParamaterType.toUpperCase().equals("STRING")) {
					String stringValue = sourcePV.getValue().getStringValue();
					if (isEmpty(stringValue)) {
						errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
								sourcePV.getName() + " " + validatorProperty.getInvokebsIPMProcessVariableStringValue()
										+ " for " + operatorName));
					}
				}

				if (destContextParamaterType.toUpperCase().equals("NUMBER")) {

					String srcInitValue = sourcePV.getValue().getIntValue();
					String srcPrecision = sourcePV.getValue().getPrecision();
					String srcScale = sourcePV.getValue().getScale();
					int destPrecision = Integer.parseInt(destValue.getPrecision());
					int destScale = Integer.parseInt(destValue.getScale());
					if (destPrecision < Integer.parseInt(srcPrecision)) {
						errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
								validatorProperty.getInvokebsProcessVariableNumberPrecision_one() + " "
										+ sourcePV.getName() + " "
										+ validatorProperty.getInvokebsProcessVariableNumberPrecision_two() + " "
										+ destCP.getName() + " for " + operatorName));
					}

					if (destScale < Integer.parseInt(srcScale)) {
						errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
								validatorProperty.getInvokebsProcessVariableNumberScale_one() + " " + sourcePV.getName()
										+ " " + validatorProperty.getInvokebsProcessVariableNumberScale_two() + " "
										+ destCP.getName() + " for " + operatorName));
					}

					if (isEmpty(srcInitValue)) {
						errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
								validatorProperty.getInvokebsIPMProcessVariableInitValue() + " " + sourcePV.getName()
										+ " for " + operatorName));
					}

					if (!isEmpty(srcInitValue) && validateNumberforPrecisionScale(srcInitValue,
							destValue.getPrecision(), destValue.getScale())) {

						errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
								validatorProperty.getInvokebsIPMProcessVariableInitValue_one() + " "
										+ sourcePV.getName() + " "
										+ validatorProperty.getInvokebsIPMProcessVariableInitValue_two() + " "
										+ destCP.getName() + " for " + operatorName));
					}
				}

				if (destContextParamaterType.toUpperCase().equals("DATETIME")) {

					String dateValue = sourcePV.getValue().getDateValue();
					if (isEmpty(dateValue)) {
						errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE, sourcePV.getName() + " "
								+ validatorProperty.getInvokebsProcessVariableDateValue() + " for " + operatorName));
					}
				}

			} else {
				errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.TYPE,
						validatorProperty.getInvokebsIPMMismatchedType_one() + " " + destCP.getName() + " "
								+ validatorProperty.getInvokebsIPMMismatchedType_two() + " " + destContextParamaterType
								+ " " + validatorProperty.getInvokebsIPMMismatchedType_three() + " "
								+ sourcePV.getName() + "  " + validatorProperty.getInvokebsIPMMismatchedType_four()
								+ " " + sourceDataType.getTypeName() + " for " + operatorName));
			}
		}
	}

	private void prepareEnteredValueToContext(PVValue inputMappingValue, ContextParameters destContext, String type,
			String contextVariableName, String operatorName, List<Map<String, String>> errorList) {

		if (inputMappingValue == null) {
			errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
					validatorProperty.getInvokebsIPMEnterValue() + " " + contextVariableName + " for " + operatorName));
		}

		if (destContext != null && inputMappingValue != null) {
			validateContextAndEnteredValueForIP(destContext, inputMappingValue, type, contextVariableName, operatorName, errorList);
		}
	}

	private void validateContextAndEnteredValueForIP(ContextParameters destCP, PVValue inputMappingValue, String type,
			String contextVariableName, String operatorName, List<Map<String, String>> errorList) {

		String srcInitValue = "";
		String srcPrecisionValue = "";
		String srcScaleValue = "";
		String srcStringValue = "";
		String srcDateValue = "";
		String destType = destCP.getType();

		boolean isValid = isValidType(destType, destCP.getName(), type, "Input Mapping Value " + contextVariableName,
				operatorName, errorList);

		if (isValid && destType.toUpperCase().equals(type.toUpperCase())) {

			if (destType.toUpperCase().equals("NUMBER")) {

				srcInitValue = inputMappingValue.getIntValue().toString();
				srcPrecisionValue = inputMappingValue.getPrecision();
				srcScaleValue = inputMappingValue.getScale();

				int destPrecision = Integer.parseInt(destCP.getValue().getPrecision());
				int destScale = Integer.parseInt(destCP.getValue().getScale());

				if (destPrecision < Integer.parseInt(srcPrecisionValue)) {

					errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
							validatorProperty.getInvokebsIPMEnterValuePrecision_one() + " " + srcPrecisionValue + " "
									+ validatorProperty.getInvokebsIPMEnterValuePrecision_two() + " " + destCP.getName()
									+ " for " + operatorName));
				}

				if (destScale < Integer.parseInt(srcScaleValue)) {

					errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
							validatorProperty.getInvokebsIPMEnterValueScale_one() + " " + srcScaleValue + " "
									+ validatorProperty.getInvokebsIPMEnterValueScale_two() + " " + destCP.getName()
									+ " for " + operatorName));
				}

				if (isEmpty(srcInitValue)) {

					errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
							validatorProperty.getInvokebsIPMEnterValueInitValue() + " " + contextVariableName + " for "
									+ operatorName));
				}

				if (!isEmpty(srcInitValue) && validateNumberforPrecisionScale(srcInitValue,
						destCP.getValue().getPrecision(), destCP.getValue().getScale())) {

					errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE,
							validatorProperty.getInvokebsIPMEnterValueInitValueError() + " " + contextVariableName
									+ " for " + operatorName));
				}
			}

			if (destType.toUpperCase().equals("STRING")) {

				srcStringValue = inputMappingValue.getStringValue();

				if (isEmpty(srcStringValue)) {
					errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE, contextVariableName + " "
							+ validatorProperty.getInvokebsIPMProcessVariableStringValue() + " for " + operatorName));
				}
			}

			if (destType.toUpperCase().equals("DATETIME")) {

				srcDateValue = inputMappingValue.getDateValue();

				if (isEmpty(srcDateValue)) {
					errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.VALUE, contextVariableName + " "
							+ validatorProperty.getInvokebsProcessVariableDateValue() + " for " + operatorName));
				}
			}

		} else {

			errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.TYPE,
					validatorProperty.getInvokebsIPMEnterValueMismatched() + " " + contextVariableName + " for "
							+ operatorName));
		}
	}

	private void validateOutputMappingExternal(final BPFlowUI bpFlowRequest, final Operators operators,
			List<Map<String, String>> errorList, List<OutputParam> outputMappingList) {

		List<ProcessVariables> processVariableList = bpFlowRequest.getConfigureBusinessProcess().getFunctional()
				.getProcessVariables();

		List<BusinessEntityAttributeProperty> beAttrProperties = new ArrayList<BusinessEntityAttributeProperty>();

		if (null != operators.getBusinessSettings().getOutputBe()) {
			beAttrProperties = operators.getBusinessSettings().getOutputBe().getBusinessEntityAttributeProperty();
		}

		if (outputMappingList != null) {

			for (OutputParam outputObj : outputMappingList) {

				for (BusinessEntityAttributeProperty attrProp : beAttrProperties) {

					boolean isMandatory = attrProp.isMandatory();

					if (attrProp.getBeAttrName().equalsIgnoreCase(outputObj.getMappedContextVariable())
							&& isMandatory) {

						String processVariableName = outputObj.getProcessVariable();
						String selectedKey = outputObj.getSelectedKey();
						String outputAttrName = outputObj.getMappedContextVariable();
						String responseVar = outputObj.getResponse();

						if (isEmpty(outputObj.getType())) {
							errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
									validatorProperty.getInvokebsOPMType() + " " + processVariableName));
						}

						if (isEmpty(selectedKey)) {
							errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.SELECTED_KEY,
									validatorProperty.getInvokebsOPMSelectedKey() + " Output Attribute "
											+ outputAttrName + " for " + operators.getName()));
						}

						// case 1 - If the output Mapping Variable has process
						// variable.

						if (selectedKey != null && selectedKey.equalsIgnoreCase(CGConstants.PROCESS_VARIABLE)) {

							if (isEmpty(processVariableName)) {
								errorList.add(addError(CGConstants.OUTPUT_MAPPING, "OUTPUT ATTRIBUTE",
										validatorProperty.getInvokebsOPMProcessVariableName() + " " + outputAttrName));
							} else {
								prepareProcessVarToOutputAttr(processVariableList, attrProp, processVariableName,
										outputAttrName, operators.getName(), errorList);
							}
						}

						if (selectedKey != null && selectedKey.equalsIgnoreCase("response")) {

							if (isEmpty(responseVar)) {
								errorList.add(addError(CGConstants.OUTPUT_MAPPING, "OUTPUT ATTRIBUTE",
										validatorProperty.getInvokebsOPMResponseVariableName() + " " + responseVar));
							}
						}

						// case 2 - If the output Mapping has User entered
						// Value.

						if (selectedKey != null && selectedKey.toUpperCase().equals("ENTER VALUE")) {
							if (outputObj.getOutputParamvalue() != null && outputAttrName != null) {
								validateOutputAttrAndEnteredValueForOP(outputObj.getOutputParamvalue(),
										outputObj.getType(), outputAttrName, attrProp, errorList);

							}
						}
					}

				}
			}
		}
	}

	private void validateOutputAttrAndEnteredValueForOP(PVValue srcOutputMappingValue, String type,
			String outputAttrName, BusinessEntityAttributeProperty beAttrProps, List<Map<String, String>> errorList) {

		String srcInitValue = "";
		String srcPrecisionValue = "";
		String srcScaleValue = "";
		String srcStringValue = "";
		String srcDateValue = "";

		DataType dType = beAttrProps.getDataType();
		String srcDataType = dType.getType();
		boolean isValid = true;
		boolean isMandatory = beAttrProps.isMandatory();
		if (beAttrProps.getBeAttrName().equalsIgnoreCase(outputAttrName)) {
			if (isEmpty(type) && isMandatory) {
				isValid = false;
				errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
						validatorProperty.getInvokebsOPMEnterValueType()));
			}
			if (!srcDataType.equalsIgnoreCase(type) && isMandatory) {
				isValid = false;
				errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
						validatorProperty.getInvokebsOPMOutputAttrType() + " " + outputAttrName));
			}

			if (isMandatory) {

				if (isValid && srcDataType.toUpperCase().equals(type.toUpperCase())) {

					if (type.toUpperCase().equals("NUMBER")) {

						srcInitValue = srcOutputMappingValue.getIntValue().toString();
						srcPrecisionValue = srcOutputMappingValue.getPrecision();
						srcScaleValue = srcOutputMappingValue.getScale();

						int destPrecision = Integer.valueOf(beAttrProps.getDataType().getPrecision());
						int destScale = Integer.valueOf(beAttrProps.getDataType().getScale());

						if (destPrecision < Integer.parseInt(srcPrecisionValue)) {
							errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.VALUE,
									validatorProperty.getInvokebsOPMEnterValueOutputAttrPrecision_one() + " "
											+ outputAttrName + " "
											+ validatorProperty.getInvokebsOPMEnterValueOutputAttrPrecision_two() + " "
											+ srcPrecisionValue));
						}

						if (destScale < Integer.parseInt(srcScaleValue)) {

							errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.VALUE,
									validatorProperty.getInvokebsOPMEnterValueOutputAttrScale_one() + " "
											+ outputAttrName + " "
											+ validatorProperty.getInvokebsOPMEnterValueOutputAttrScale_two() + " "
											+ srcScaleValue));
						}

						if (isEmpty(srcInitValue)) {

							errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.VALUE,
									validatorProperty.getInvokebsOPMEnterValueOutputAttrInitValue()));
						}

						if (!isEmpty(srcInitValue) && validateNumberforPrecisionScale(srcInitValue,
								String.valueOf(beAttrProps.getDataType().getPrecision()),
								String.valueOf(beAttrProps.getDataType().getScale()))) {

							errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.VALUE,
									validatorProperty.getInvokebsOPMEnterValueOutputAttrInitMismatch() + " "
											+ outputAttrName));
						}
					}

					if (beAttrProps.getDataType().getType().toUpperCase().equals("STRING")) {

						srcStringValue = srcOutputMappingValue.getStringValue();

						if (isEmpty(srcStringValue)) {
							errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.VALUE,
									validatorProperty.getInvokebsOPMEnterValueOutputAttrString()));
						}
					}

					if (beAttrProps.getDataType().getType().toUpperCase().equals("DATETIME")) {

						srcDateValue = srcOutputMappingValue.getDateValue();

						if (isEmpty(srcDateValue)) {
							errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.VALUE,
									validatorProperty.getInvokebsOPMEnterValueOutputAttrDate()));
						}
					}

				} else {

					errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
							validatorProperty.getInvokebsOPMEnterValueMismatched() + " " + outputAttrName));

				}
			}
		}

	}

	/**
	 * 
	 * This method would validate the output mapping in the requesting json.
	 * example : cv->pv (or) ev->pv
	 * 
	 * @param bpFlowRequest
	 * @param operators
	 * 
	 */

	private void validateOutputMapping(final BPFlowUI bpFlowRequest, final Operators operators,
			List<Map<String, String>> errorList, List<OutputParam> outputMappingList, boolean mandatoryByDefault) {

		List<ProcessVariables> processVariableList = bpFlowRequest.getConfigureBusinessProcess().getFunctional()
				.getProcessVariables();
		List<ContextParameters> serviceContext = operators.getBusinessSettings().getServiceContextParameters();
		List<ContextParameters> apiContext = operators.getBusinessSettings().getApiContextParameters();

		boolean emptyProcessVariableNameReported = false;

		if (outputMappingList != null && processVariableList != null) {

			for (OutputParam outputObj : outputMappingList) {

				String processVariableName = outputObj.getProcessVariable();
				String selectedKey = outputObj.getSelectedKey();
				String contextVariableName = outputObj.getMappedContextVariable();

				if (isEmpty(processVariableName) && !emptyProcessVariableNameReported) {
					emptyProcessVariableNameReported = true;
					errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.PROCESS_VARIABLE,
							validatorProperty.getInvokebsOPMProcessVariableName() + " " + operators.getName()));
				}

				if (isEmpty(outputObj.getType())) {
					errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
							validatorProperty.getInvokebsOPMType() + " " + processVariableName + " for " + operators.getName()));
				}

				if (isEmpty(selectedKey)) {
					errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.SELECTED_KEY,
							validatorProperty.getInvokebsOPMSelectedKey() + " context variable " + contextVariableName
									+ " for " + operators.getName()));
				}

				// case 1 - If the output Mapping process Variable has context
				// variable.

				if (selectedKey != null && selectedKey.equalsIgnoreCase(CGConstants.CONTEXT_VARIABLE)) {

					if (isEmpty(contextVariableName)) {
						errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.CONTEXT_VARIABLE,
								validatorProperty.getInvokebsOPMContextVariable() + " " + processVariableName + " " + operators.getName()));
					} else {
						prepareContextToProcess(processVariableList, serviceContext, apiContext, processVariableName,
								contextVariableName, operators.getName(), errorList);
					}
				}

				// case 2 - If the output Mapping process has User entered
				// Value.

				if (selectedKey != null && selectedKey.toUpperCase().equals("ENTER VALUE")) {
					prepareEnteredValueToProcess(outputObj.getOutputParamvalue(), processVariableList,
							outputObj.getType(), processVariableName, operators.getName(), errorList, mandatoryByDefault);
				}
			}

		}
	}

	private void prepareProcessVarToOutputAttr(List<ProcessVariables> processVariableList,
			BusinessEntityAttributeProperty beEntityAttrProperty, String mappedDesProcessVariable,
			String srcOutputAttrName, String operatorName, List<Map<String, String>> errorList) {

		ProcessVariables destProcessVariable = null;

		String outputBEDefAttrName = beEntityAttrProperty.getBeAttrName();
		DataType srcType = beEntityAttrProperty.getDataType();
		String attrDataType = srcType.getType();

		if (outputBEDefAttrName.equals(srcOutputAttrName)) {
			for (ProcessVariables processVariableObj : processVariableList) {

				if (processVariableObj.getName() != null
						&& processVariableObj.getName().equals(mappedDesProcessVariable)) {
					destProcessVariable = processVariableObj;
					break;
				}
			}

			if (mappedDesProcessVariable != null && srcOutputAttrName != null) {
				validateProcessAndOutputAttrForOP(srcOutputAttrName, attrDataType, destProcessVariable,
						mappedDesProcessVariable, errorList);
			}
			if (destProcessVariable == null) {

				errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.PROCESS_VARIABLE,
						mappedDesProcessVariable + " " + validatorProperty.getInvokebsOPMProcessVariableNotFound()));
			}
		} else {
			errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.OUTPUTBE_TYPE,
					srcOutputAttrName + " " + validatorProperty.getInvokebsOPMOutputAttributeNotFound()));
		}

	}

	private void validateProcessAndOutputAttrForOP(String srcOutputAttrName, String srcAttrDataType,
			ProcessVariables destProcessVariable, String mappedProcessVariable, List<Map<String, String>> errorList) {

		boolean isMandatory = isProcessVariableMandatory(destProcessVariable);

		String srcType = srcAttrDataType;

		Type destDataType = destProcessVariable.getType();

		boolean isValid = true;

		if (isEmpty(destDataType.getTypeName())) {

			isValid = false;
			errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
					validatorProperty.getInvokebsOPMProcessVariableType() + " " + destProcessVariable.getName()));

		} else if (isEmpty(srcType)) {

			isValid = false;
			errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
					validatorProperty.getInvokebsOPMOutputAttributeType() + " " + srcOutputAttrName));
		}

		if (isMandatory) {

			if (isValid && !srcType.equals(destDataType.getTypeName())) {
				errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
						validatorProperty.getInvokebsOPMMismatchedType_onePv() + " " + srcOutputAttrName + " "
								+ validatorProperty.getInvokebsOPMMismatchedType_two() + " " + srcType.toLowerCase()
								+ " " + validatorProperty.getInvokebsOPMMismatchedType_three() + " "
								+ destProcessVariable.getName() + "  "
								+ validatorProperty.getInvokebsOPMMismatchedType_four() + " "
								+ destDataType.getTypeName().toLowerCase()));

			}
		}
	}

	// output mapping => cv->pv (or) ev->pv

	private void prepareContextToProcess(List<ProcessVariables> processVariableList,
			List<ContextParameters> serviceContext, List<ContextParameters> apiContext, String mappedProcessVariable,
			String contextVariableName, String operatorName, List<Map<String, String>> errorList) {

		ProcessVariables destProcessVariable = null;

		ContextParameters sourceContextParameter = getContextParameter(serviceContext, apiContext, contextVariableName,
				CGConstants.OUTPUT_MAPPING, operatorName, errorList);

		for (ProcessVariables processVariableObj : processVariableList) {

			if (processVariableObj.getName() != null && processVariableObj.getName().equals(mappedProcessVariable)) {
				destProcessVariable = processVariableObj;
				break;
			}
		}

		if (destProcessVariable == null) {

			errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.PROCESS_VARIABLE, mappedProcessVariable + " "
					+ validatorProperty.getInvokebsOPMProcessVariableNotFound() + " for " + operatorName));
		}

		if (sourceContextParameter != null && destProcessVariable != null) {
			validateProcessAndContextForOP(sourceContextParameter, destProcessVariable, operatorName, errorList);
		}
	}

	// output mapping => cv->pv (or) ev->pv

	private void validateProcessAndContextForOP(ContextParameters sourceContextParameter,
			ProcessVariables destProcessVariable, String operatorName, List<Map<String, String>> errorList) {

		boolean isMandatory = isProcessVariableMandatory(destProcessVariable);

		String srcType = sourceContextParameter.getType();

		Type destDataType = destProcessVariable.getType();

		boolean isValid = true;

		if (isEmpty(destDataType.getTypeName())) {

			isValid = false;
			errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
					validatorProperty.getInvokebsOPMProcessVariableType() + " " + destProcessVariable.getName()
							+ " for " + operatorName));

		} else if (isEmpty(srcType)) {

			isValid = false;
			errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
					validatorProperty.getInvokebsOPMContextParameterType() + " " + sourceContextParameter.getName()
							+ " for " + operatorName));
		}

		if (isMandatory) {

			if (isValid && !srcType.equals(destDataType.getTypeName())) {
				errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.TYPE,
						validatorProperty.getInvokebsOPMMismatchedType_one() + " " + sourceContextParameter.getName()
								+ " " + validatorProperty.getInvokebsOPMMismatchedType_two() + " "
								+ srcType.toLowerCase() + " " + validatorProperty.getInvokebsOPMMismatchedType_three()
								+ " " + destProcessVariable.getName() + "  "
								+ validatorProperty.getInvokebsOPMMismatchedType_four() + " "
								+ destDataType.getTypeName().toLowerCase() + " for " + operatorName));

			}
		}
	}

	// OutputMapping

	private void prepareEnteredValueToProcess(PVValue sourceValueObj, List<ProcessVariables> processVariableList,
			String type, String processVariableName, String operatorName, List<Map<String, String>> errorList,
			boolean mandatoryByDefault) {

		ProcessVariables destProcessObj = null;

		for (ProcessVariables processVariableObj : processVariableList) {
			if (processVariableObj.getName() != null && processVariableObj.getName().equals(processVariableName)) {
				destProcessObj = processVariableObj;
				break;
			}
		}

		if (sourceValueObj == null) {

			errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.VALUE,
					validatorProperty.getInvokebsOPMEnterValue() + " " + processVariableName + " for " + operatorName));
		}

		if (destProcessObj == null) {

			errorList.add(addError(CGConstants.OUTPUT_MAPPING, CGConstants.PROCESS_VARIABLE, processVariableName + " "
					+ validatorProperty.getInvokebsOPMProcessVariableNotFound() + " for " + operatorName));
		}

		if (destProcessObj != null && sourceValueObj != null) {
		}
		validateProcessAndEnteredValueForOP(destProcessObj, sourceValueObj, type, processVariableName, operatorName, errorList,
				mandatoryByDefault);
	}

	private void validateProcessAndEnteredValueForOP(ProcessVariables destProcessObj, PVValue srcOutputMappingValue,
			String type, String processVariableName, String operatorName, List<Map<String, String>> errorList,
			boolean mandatoryByDefault) {

		String srcInitValue = "";
		String srcPrecisionValue = "";
		String srcScaleValue = "";
		String srcStringValue = "";
		String srcDateValue = "";
		Type desType = destProcessObj.getType();
		boolean isValid = true;
		boolean isMandatory = isProcessVariableMandatory(destProcessObj) || mandatoryByDefault;

		String category = CGConstants.OUTPUT_MAPPING;
		if (mandatoryByDefault) {
			category = CGConstants.MERGE;
		}
		String source = CGConstants.TYPE;

		if (isMandatory && isEmpty(desType.getTypeName())) {

			isValid = false;
			errorList.add(addError(category, source, validatorProperty.getInvokebsOPMProcessVariableType() + " "
					+ processVariableName + " for " + operatorName));

		} else if (isEmpty(type)) {

			isValid = false;
			errorList.add(addError(category, source,
					validatorProperty.getInvokebsOPMEnterValueType() + " for " + operatorName));
		}

		if (isMandatory) {

			if (isValid && desType.getTypeName().toUpperCase().equals(type.toUpperCase())) {

				source = CGConstants.VALUE;

				if (desType.getTypeName().toUpperCase().equals("NUMBER")) {

					srcInitValue = srcOutputMappingValue.getIntValue().toString();
					srcPrecisionValue = srcOutputMappingValue.getPrecision();
					srcScaleValue = srcOutputMappingValue.getScale();

					int destPrecision = Integer.valueOf(destProcessObj.getValue().getPrecision());
					int destScale = Integer.valueOf(destProcessObj.getValue().getScale());

					if (destPrecision < Integer.parseInt(srcPrecisionValue)) {
						errorList.add(addError(category, source,
								validatorProperty.getInvokebsOPMEnterValuePrecision_one() + " " + processVariableName
										+ " " + validatorProperty.getInvokebsOPMEnterValuePrecision_two() + " "
										+ srcPrecisionValue + " for " + operatorName));
					}

					if (destScale < Integer.parseInt(srcScaleValue)) {

						errorList.add(addError(category, source,
								validatorProperty.getInvokebsOPMEnterValueScale_one() + " " + processVariableName + " "
										+ validatorProperty.getInvokebsOPMEnterValueScale_two() + " " + srcScaleValue
										+ " for " + operatorName));
					}

					if (isEmpty(srcInitValue)) {

						errorList.add(addError(category, source,
								validatorProperty.getInvokebsOPMEnterValueInitValue() + " for " + operatorName));
					}

					if (!isEmpty(srcInitValue) && validateNumberforPrecisionScale(srcInitValue,
							destProcessObj.getValue().getPrecision(), destProcessObj.getValue().getScale())) {

						errorList
								.add(addError(category, source, validatorProperty.getInvokebsOPMEnterValueInitMismatch()
										+ " " + processVariableName + " for " + operatorName));
					}
				}

				if (destProcessObj.getType().getTypeName().toUpperCase().equals("STRING")) {

					srcStringValue = srcOutputMappingValue.getStringValue();

					if (isEmpty(srcStringValue)) {
						errorList.add(addError(category, source, validatorProperty.getInvokebsOPMEnterValueString()
								+ " for " + processVariableName + " for " + operatorName));
					}
				}

				if (destProcessObj.getType().getTypeName().toUpperCase().equals("DATETIME")) {

					srcDateValue = srcOutputMappingValue.getDateValue();

					if (isEmpty(srcDateValue)) {
						errorList.add(addError(category, source, validatorProperty.getInvokebsOPMEnterValueDate()
								+ " for " + processVariableName + " for " + operatorName));
					}
				}

			} else {
				source = CGConstants.TYPE;
				errorList.add(addError(category, source, validatorProperty.getInvokebsOPMEnterValueMismatched() + " "
						+ processVariableName + " for " + operatorName));
			}
		}
	}

	/**
	 * 
	 * Checking Duplicate Names.
	 * 
	 * @param operatorNames
	 * 
	 */

	private void validateDuplicateNames(List<String> operatorNames, String category, String source,
			List<Map<String, String>> errorList) {

		if (operatorNames != null && operatorNames.size() > 0) {

			List<String> tempList = new ArrayList<String>();
			List<String> duplicateOperators = new ArrayList<String>();

			for (String str : operatorNames) {

				if (tempList.contains(str.toLowerCase())) {
					duplicateOperators.add(str);
				} else {
					tempList.add(str.toLowerCase());
				}
			}

			if (duplicateOperators != null && duplicateOperators.size() > 0) {

				errorList.add(0, addError(category, source, "Duplicate Names "
						+ Arrays.toString(duplicateOperators.toArray()).replace("[", "").replace("]", "") + " found!"));
			}
		}
	}

	private void validateEndOperator(Operators operatorObject, int endOperatorCnt, int operatorPosition,
			List<Map<String, String>> errorList, Map<String, String> operatorKeyType) {

		getOperatorKeyType(operatorObject, operatorKeyType);

		if (isEmpty(operatorObject.getName())) {
			errorList.add(addError(CGConstants.END_OPERATOR, CGConstants.NAME, validatorProperty.getOperatorName()));
			return;
		}

		if (isEmpty(operatorObject.getKey())) {
			errorList.add(addError(CGConstants.END_OPERATOR, CGConstants.KEY,
					validatorProperty.getOperatorKey() + " " + operatorObject.getName()));
		}

		if (isEmpty(operatorObject.getType())) {
			errorList.add(addError(CGConstants.END_OPERATOR, CGConstants.TYPE,
					validatorProperty.getOperatorType() + " " + operatorObject.getName()));
		}

		if (!isEmpty(operatorObject.getBusinessSettings().getOutputBeType())) {
			List<String> outputBukAttributes = operatorObject.getBusinessSettings().getOutputBEBUKAttributes();
			if (outputBukAttributes == null || outputBukAttributes.size() < 1) {
				errorList.add(addError(CGConstants.END_OPERATOR, CGConstants.BUSINESS_SETTINGS,
						validatorProperty.getOutputBUK() + " " + operatorObject.getName()));
			}
		}

		List<Property> propObj = operatorObject.getProperties();

		if (propObj != null && propObj.size() > 0) {
			for (Property property : propObj) {
				validateProperty(property, operatorObject.getName(), CGConstants.END_OPERATOR, errorList);
			}
		} else {
			errorList.add(addError(CGConstants.OPERATOR, CGConstants.END_OPERATOR,
					validatorProperty.getNoPropertyFound() + " " + operatorObject.getName()));
		}
	}

	private void validateDecisionMatrix(Operators operator, String type, String category,
			List<Map<String, String>> errorList, Map<String, String> operatorKeyType) {

		List<String> decisionNames = new ArrayList<String>();

		getOperatorKeyType(operator, operatorKeyType);

		if (isEmpty(operator.getName())) {
			errorList.add(addError(category, CGConstants.NAME, validatorProperty.getOperatorName()));
			return;
		}
		if (isEmpty(operator.getKey())) {
			errorList.add(
					addError(category, CGConstants.KEY, validatorProperty.getOperatorKey() + " " + operator.getName()));
		}
		if (isEmpty(operator.getType())) {
			errorList.add(addError(category, CGConstants.TYPE,
					validatorProperty.getOperatorType() + " " + operator.getName()));
		}

		if (operator.getBusinessSettings() != null) {

			if (!isEmpty(operator.getBusinessSettings().getInputBeType())) {
				List<String> inputBukAttributes = operator.getBusinessSettings().getInputBEBUKAttributes();
				if (inputBukAttributes == null || inputBukAttributes.size() < 1) {
					errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
							validatorProperty.getStartInputBUK() + " " + operator.getName()));
				}
			}

			if (operator.getBusinessSettings().getDecisions() != null
					&& operator.getBusinessSettings().getDecisions().size() > 0) {

				List<Decisions> decisionList = operator.getBusinessSettings().getDecisions();
				for (Decisions decisionObj : decisionList) {

					if (isEmpty(decisionObj.getDecisionName())) {
						errorList.add(addError(category, CGConstants.DECISIONS,
								validatorProperty.getDmExclusiveDecisionName() + " " + operator.getName()));
					} else {
						decisionNames.add(decisionObj.getDecisionName());
					}
				}
			} else {
				errorList.add(addError(category, CGConstants.DECISIONS,
						validatorProperty.getDmExclusiveDecisions() + " for " + operator.getName()));
			}
			if (type.toUpperCase().equals("DECISION_MATRIX_EXCLUSIVE")
					&& !operator.getBusinessSettings().isExclusive()) {
				errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
						validatorProperty.getDmExclusiveflagExclusive() + " for " + operator.getName()));
			}
		} else {
			errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
					validatorProperty.getDmExclusiveBusinessSetting() + " for " + operator.getName()));
		}

		List<Property> propObj = operator.getProperties();
		if (propObj != null && propObj.size() > 0) {
			for (Property property : propObj) {
				validateProperty(property, operator.getName(), category, errorList);
			}
		} else {
			errorList.add(addError(CGConstants.OPERATOR, category,
					validatorProperty.getNoPropertyFound() + " " + operator.getName()));
		}
	}

	private void validateJoinOperator(Operators operatorObject, List<Map<String, String>> errorList,
			Map<String, String> operatorKeyType) {

		final String category = CGConstants.JOIN_CATEGORY;

		getOperatorKeyType(operatorObject, operatorKeyType);

		if (isEmpty(operatorObject.getName())) {
			errorList.add(addError(category, CGConstants.NAME, validatorProperty.getOperatorName()));
			return;
		}

		if (isEmpty(operatorObject.getType())) {
			errorList.add(addError(category, CGConstants.TYPE,
					validatorProperty.getOperatorType() + " " + operatorObject.getName()));
		}

		if (isEmpty(operatorObject.getBusinessSettings().getInputBeType())) {
			errorList.add(addError(category, CGConstants.BUSINESS_SETTINGS,
					validatorProperty.getInputBeType() + " " + operatorObject.getName()));
		}

		List<Property> propObj = operatorObject.getProperties();

		if (propObj != null && propObj.size() > 0) {
			for (Property property : propObj) {
				validateProperty(property, operatorObject.getName(), category, errorList);
			}
		} else {
			errorList.add(addError(CGConstants.OPERATOR, category,
					validatorProperty.getNoPropertyFound() + " " + operatorObject.getName()));
		}

	}

	private void validateInvokeBSOperator(BPFlowUI bpFlowRequest, Operators operator,
			List<Map<String, String>> errorList, Map<String, String> operatorKeyType) {

		getOperatorKeyType(operator, operatorKeyType);

		try {
			if (isEmpty(operator.getName())) {
				errorList.add(
						addError(CGConstants.INVOKEBS_OPERATOR, CGConstants.NAME, validatorProperty.getOperatorName()));
				return;
			}
			if (isEmpty(operator.getKey())) {
				errorList.add(addError(CGConstants.INVOKEBS_OPERATOR, CGConstants.KEY,
						validatorProperty.getOperatorKey() + " " + operator.getName()));
			}
			if (isEmpty(operator.getType())) {

				errorList.add(addError(CGConstants.INVOKEBS_OPERATOR, CGConstants.TYPE,
						validatorProperty.getOperatorType() + " " + operator.getName()));
			}

			if (isEmpty(operator.getBusinessSettings().getBusinessServiceName())) {

				errorList.add(addError(CGConstants.INVOKEBS_OPERATOR, CGConstants.BUSINESS_SETTINGS,
						operator.getName() + " " + validatorProperty.getInvokebsBusinessServiceName()));
			}

			if (isEmpty(operator.getBusinessSettings().getApiName())) {

				errorList.add(addError(CGConstants.INVOKEBS_OPERATOR, CGConstants.BUSINESS_SETTINGS,
						operator.getName() + " " + validatorProperty.getInvokebsApiName()));
			}

			if (!isEmpty(operator.getBusinessSettings().getInputBeType())) {
				validateInputBEDefinition(operator, CGConstants.INVOKEBS_OPERATOR, "InputBE", errorList);

				List<String> inputBukAttributes = operator.getBusinessSettings().getInputBEBUKAttributes();

				if (inputBukAttributes == null || inputBukAttributes.size() < 1) {

					errorList.add(addError(CGConstants.INVOKEBS_OPERATOR, CGConstants.BUSINESS_SETTINGS,
							validatorProperty.getStartInputBUK() + " " + operator.getName()));
				}
			}

			if (!isEmpty(operator.getBusinessSettings().getOutputBeType())) {
				validateOutputBEDefinition(operator, CGConstants.INVOKEBS_OPERATOR, "OutputBE", errorList);

				List<String> outputBukAttributes = operator.getBusinessSettings().getOutputBEBUKAttributes();

				if (outputBukAttributes == null || outputBukAttributes.size() < 1) {

					errorList.add(addError(CGConstants.INVOKEBS_OPERATOR, CGConstants.BUSINESS_SETTINGS,
							validatorProperty.getOutputBUK() + " " + operator.getName()));
				}
			}

			validateInputMapping(bpFlowRequest, operator, errorList);

			validateOutputMapping(bpFlowRequest, operator, errorList, operator.getOutputMapping(), false);

			List<Property> propObj = operator.getProperties();

			if (propObj != null && propObj.size() > 0) {
				for (Property property : propObj) {
					validateProperty(property, operator.getName(), CGConstants.INVOKEBS_OPERATOR, errorList);

				}
			} else {
				errorList.add(addError(CGConstants.OPERATOR, CGConstants.INVOKEBS_OPERATOR,
						validatorProperty.getNoPropertyFound() + " " + operator.getName()));
			}

			validateBusinessFailure(bpFlowRequest, operator, errorList);

		} catch (Exception e) {
			logger.error("Exception occured: ", e.getMessage(), e);
		}
	}

	private void validateBusinessFailure(BPFlowUI bpFlowRequest, Operators operatorObject,
			List<Map<String, String>> errorList) {
		List<String> errorCodes = operatorObject.getBusinessSettings().getBusinessErrorCodes();
		if (errorCodes.size() > 0) {
			if (!operatorObject.getBusinessSettings().isBusinessFailureFlowExist()) {
				errorList.add(addError(CGConstants.INVOKEBS_OPERATOR, CGConstants.BUSINESS_ERROR_CODES,
						validatorProperty.getInvokebsErrorCodeBusinessFailure()
								.concat(" " + operatorObject.getName() + " Operator")));
			}
		}
	}

	private void validateInvokeBSExOperator(BPFlowUI bpFlowRequest, Operators operator,
			List<Map<String, String>> errorList, Map<String, String> operatorKeyType) {

		BusinessSettings businessSettings = null;

		getOperatorKeyType(operator, operatorKeyType);

		try {
			if (isEmpty(operator.getName())) {
				errorList.add(addError(CGConstants.INVOKE_BS_EXTERNAL, CGConstants.NAME,
						validatorProperty.getOperatorName()));
				return;
			}

			if (isEmpty(operator.getKey())) {
				errorList.add(addError(CGConstants.INVOKE_BS_EXTERNAL, CGConstants.KEY,
						validatorProperty.getOperatorKey() + " " + operator.getName()));
			}

			if (isEmpty(operator.getType())) {
				errorList.add(addError(CGConstants.INVOKE_BS_EXTERNAL, CGConstants.TYPE,
						validatorProperty.getOperatorType() + " " + operator.getName()));
			}

			businessSettings = operator.getBusinessSettings();

			if (null != businessSettings) {

				if (isEmpty(businessSettings.getContentType())
						&& !businessSettings.getHttpMethod().equalsIgnoreCase("GET")) {
					errorList.add(addError(CGConstants.INVOKE_BS_EXTERNAL, CGConstants.BUSINESS_SETTINGS,
							"Content-Type is not specified for" + " " + operator.getName()));
				}
				if (isEmpty(businessSettings.getHttpMethod())) {
					errorList.add(addError(CGConstants.INVOKE_BS_EXTERNAL, CGConstants.BUSINESS_SETTINGS,
							"HTTP method is not specified for" + " " + operator.getName()));
				}

			} else {
				errorList.add(addError(CGConstants.INVOKE_BS_EXTERNAL, CGConstants.BUSINESS_SETTINGS,
						"No business settings were set for" + " " + operator.getName()));
			}

			if (!isEmpty(operator.getBusinessSettings().getInputBeType())) {
				validateInputBEDefinition(operator, CGConstants.INVOKE_BS_EXTERNAL, "InputBE", errorList);
				List<String> inputBukAttributes = operator.getBusinessSettings().getInputBEBUKAttributes();
				if (inputBukAttributes == null || inputBukAttributes.size() < 1) {
					errorList.add(addError(CGConstants.INVOKE_BS_EXTERNAL, CGConstants.BUSINESS_SETTINGS,
							validatorProperty.getStartInputBUK() + " " + operator.getName()));
				}
			}

			if (!isEmpty(operator.getBusinessSettings().getOutputBeType())) {
				validateOutputBEDefinition(operator, CGConstants.INVOKE_BS_EXTERNAL, "OutputBE", errorList);
				List<String> outputBukAttributes = operator.getBusinessSettings().getOutputBEBUKAttributes();
				if (outputBukAttributes == null || outputBukAttributes.size() < 1) {
					errorList.add(addError(CGConstants.INVOKE_BS_EXTERNAL, CGConstants.BUSINESS_SETTINGS,
							validatorProperty.getOutputBUK() + " " + operator.getName()));
				}
				// OutputBE Mapping
				// validateOutputMappingExternal(bpFlowRequest, operator,
				// errorList, operator.getOutputMapping());
			}

			validateInputMapping(bpFlowRequest, operator, errorList);

			List<Property> propObj = operator.getProperties();

			if (propObj != null && propObj.size() > 0) {
				for (Property property : propObj) {
					validateProperty(property, operator.getName(), CGConstants.INVOKE_BS_EXTERNAL, errorList);
					if (operator.getBusinessSettings().isSecuredAPI()) {
						if (CGConstants.USERNAME.equals(property.getName())) {
							if (isEmpty(property.getValue())) {
								errorList.add(addError(CGConstants.OPERATOR, CGConstants.INVOKE_BS_EXTERNAL,
										validatorProperty.getPropertyUsername() + " " + operator.getName()));
							}
						}
						if (CGConstants.PASSWORD.equals(property.getName())) {
							if (isEmpty(property.getValue())) {
								errorList.add(addError(CGConstants.OPERATOR, CGConstants.INVOKE_BS_EXTERNAL,
										validatorProperty.getPropertyPassword() + " " + operator.getName()));
							}
						}
					}
				}
			} else {
				errorList.add(addError(CGConstants.OPERATOR, CGConstants.INVOKE_BS_EXTERNAL,
						validatorProperty.getNoPropertyFound() + " " + operator.getName()));
			}

		} catch (Exception e) {
			logger.error("Exception occured: ", e.getMessage(), e);
		}
	}

	private void validateInputBEDefinition(Operators operatorObject, String category, String source,
			List<Map<String, String>> errorList) {

		BusinessEntity businessEntityObject = (BusinessEntity) operatorObject.getBusinessSettings().getInputBe();
		if (null != businessEntityObject) {
			validateBEDefinition(businessEntityObject, category, source, operatorObject.getName(), errorList);
		}
	}

	private void validateOutputBEDefinition(Operators operatorObject, String category, String source,
			List<Map<String, String>> errorList) {
		BusinessEntity businessEntityObject = (BusinessEntity) operatorObject.getBusinessSettings().getOutputBe();
		if (null != businessEntityObject) {
			validateBEDefinition(businessEntityObject, category, source, operatorObject.getName(), errorList);
		}
	}

	private void validateBEDefinition(BusinessEntity businessEntityObject, String category, String source,
			String operatorName, List<Map<String, String>> errorList) {

		if (isEmpty(String.valueOf(businessEntityObject.getContext().getArtifactId()))
				&& validatorProperty.getBeDefinitionArtifactId() != null) {
			errorList.add(
					addError(category, source, validatorProperty.getBeDefinitionArtifactId() + " " + operatorName));
		}

		if (isEmpty(businessEntityObject.getName()) && validatorProperty.getBeDefinitionName() != null) {
			errorList.add(addError(category, source, validatorProperty.getBeDefinitionName() + " " + operatorName));
		}

		if (isEmpty(businessEntityObject.getContext().getDepartment())
				&& validatorProperty.getBeDefinitionDepartment() != null) {
			errorList.add(
					addError(category, source, validatorProperty.getBeDefinitionDepartment() + " " + operatorName));
		}

		if (isEmpty(businessEntityObject.getContext().getModule())
				&& validatorProperty.getBeDefinitionModule() != null) {
			errorList.add(addError(category, source, validatorProperty.getBeDefinitionModule() + " " + operatorName));
		}

		if (isEmpty(businessEntityObject.getContext().getRelease())
				&& validatorProperty.getBeDefinitionRelease() != null) {
			errorList.add(addError(category, source, validatorProperty.getBeDefinitionRelease() + " " + operatorName));
		}

	}

	private void getOperatorKeyType(Operators operatorObject, Map<String, String> operatorKeyType) {

		if (operatorObject.getKey() != null && operatorObject.getKey().trim().length() > 0) {
			operatorKeyType.put(operatorObject.getKey().toUpperCase(), operatorObject.getType());
		}
	}

	private void validateProperty(Property propertiesObj, String operatorName, String operatorAction,
			List<Map<String, String>> errorList) {

		if (isEmpty(propertiesObj.getName())) {
			errorList.add(addError(CGConstants.OPERATOR, operatorAction,
					validatorProperty.getPropertyName() + " " + operatorName));
		}

		if (isEmpty(propertiesObj.getValue())) {
			if ("penaltyDuration".toLowerCase().equals(propertiesObj.getName().toLowerCase())) {
				errorList.add(addError(CGConstants.OPERATOR, operatorAction,
						validatorProperty.getPropertyPenaltyDuration() + " " + operatorName));
			} else if ("yieldDuration".toLowerCase().equals(propertiesObj.getName().toLowerCase())) {
				errorList.add(addError(CGConstants.OPERATOR, operatorAction,
						validatorProperty.getPropertyYieldDuration() + " " + operatorName));
			} else if ("concurrentTasks".toLowerCase().equals(propertiesObj.getName().toLowerCase())) {
				errorList.add(addError(CGConstants.OPERATOR, operatorAction,
						validatorProperty.getPropertyConcurrentTasks() + " " + operatorName));
			}
		}

		if (propertiesObj.isMandatory() && isEmpty(propertiesObj.getValue())) {

			if (CGConstants.INPUT_FILE_CHANNEL_INTEGRATION.equals(operatorAction)) {
				if ("inputFileLocation".equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getInputFileLocation() + " " + operatorName));
				} else if ("addressedFileLocation".equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getAddressed() + " " + operatorName));
				} else if ("rejectedRecordFileLocation".equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getRecordReject() + " " + operatorName));
				} else if ("rejectedFileLocation".equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getFileReject() + " " + operatorName));
				} else if ("backupFileLocation".equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getBackup() + " " + operatorName));
				}
			} else if (CGConstants.INPUT_REST_CHANNEL_INTEGRATION.equals(operatorAction)) {
				if ("restApiBasepath".equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getRestApiBasepath() + " " + operatorName));
				} else if ("restApiPort".equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getRestApiPort() + " " + operatorName));
				} else if ("methodType".equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getMethodType() + " " + operatorName));
				}
			} else if (CGConstants.OUTPUT_FILE_CHANNEL_INTEGRATION.equals(operatorAction)) {
				if ("outputFileLocation".equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getOutputFileLocation() + " " + operatorName));
				}
			} else if (CGConstants.INVOKE_BS_EXTERNAL.equals(operatorAction)
					|| CGConstants.REST_OUTPUT_CHANNEL_INTEGRATION.equals(operatorAction)) {
				if (CGConstants.URL_PROPERTY.equalsIgnoreCase(propertiesObj.getName())) {
					errorList.add(addError(CGConstants.OPERATOR, operatorAction,
							validatorProperty.getInvokebsExternalURL() + " " + operatorName));
				}
			}
		}

	}

	private boolean validateNumberforPrecisionScale(String num_value, String precisionEntered, String scaleEntered) {

		boolean result = false;
		int numValuePrecision = 0;
		int numValueScale = 0;

		if (num_value != null && num_value.trim().contains(".")) {

			String[] parts = num_value.split("\\.");
			int numValueIntPart = parts[0].length();
			numValueScale = parts[1].length();
			numValuePrecision = numValueIntPart + numValueScale;

			if ((numValuePrecision > (Integer.parseInt(precisionEntered)))
					|| (numValueScale > (Integer.parseInt(scaleEntered)))) {
				result = true;
			}

		} else {

			numValuePrecision = num_value.equals("0") ? 0 : num_value.length();
			if (((numValuePrecision) > (Integer.parseInt(precisionEntered)))) {
				result = true;
			}
		}
		return result;
	}

	private boolean isEmpty(String value) {

		boolean result = false;
		if (value == null || "".equals(value.trim())) {
			result = true;
		}

		return result;
	}

	private boolean isValidType(String desType, String contextVariableName, String sourceType, String processName, String operatorName,
			List<Map<String, String>> errorList) {

		boolean isValid = true;
		if (isEmpty(desType)) {
			isValid = false;
			errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.TYPE,
					validatorProperty.getInvokebsIPMContextParameterType() + " " + contextVariableName + " for "
							+ operatorName));
		} else if (isEmpty(sourceType)) {
			isValid = false;
			errorList.add(addError(CGConstants.INPUT_MAPPING, CGConstants.TYPE,
					validatorProperty.getInvokebsIPMType() + " " + processName + " for " + operatorName));
		}
		return isValid;
	}

}
