package com.suntecgroup.bp.designer.review.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.frontend.beans.BPsetUpProperties;
import com.suntecgroup.bp.designer.frontend.beans.BusinessProcessSetup;
import com.suntecgroup.bp.designer.frontend.beans.ConfigureBusinessProcess;
import com.suntecgroup.bp.designer.frontend.beans.PVValue;
import com.suntecgroup.bp.designer.frontend.beans.ProcessVariables;
import com.suntecgroup.bp.designer.model.Fields;
import com.suntecgroup.bp.designer.model.ReviewReportResponse;
import com.suntecgroup.bp.designer.services.impl.BPDesignerServiceImpl;
import com.suntecgroup.bp.util.BPConstant;

public class ConfigBPReport {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigBPReport.class);

	/**
	 * compareBPConfigure - Required to compare the config bp and get the
	 * difference
	 * 
	 * @param config
	 *            bp
	 * @return response
	 */
	public static List<ReviewReportResponse> configureBusinessProcessComparision(
			ConfigureBusinessProcess sourceConfigureBusinessProcess,
			ConfigureBusinessProcess targetConfigureBusinessProcess) {
		List<ReviewReportResponse> operatorList = new ArrayList<>();
		ReviewReportResponse operators = new ReviewReportResponse();
		List<Fields> fieldsList = new ArrayList<>();
		try {
			fieldsList = bpConfigFieldCheck(sourceConfigureBusinessProcess, targetConfigureBusinessProcess);
			if (!fieldsList.isEmpty()) {
				operators.setKey(BPConstant.BP_CONFIG);
				operators.setName(BPConstant.BP_CONFIG);
				operators.setType(BPConstant.BP_CONFIG);
				operators.setStatus(BPConstant.MODIFIED);
				operators.setFields(fieldsList);
				operatorList.add(operators);
			}
			return operatorList;
		} catch (Exception exception) {
			LOGGER.error("Configuration BP comparision error"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check Functional,Properties fields inside configuration
	 * BP
	 */
	private static List<Fields> bpConfigFieldCheck(ConfigureBusinessProcess sourceconfigureBusinessProcess,
			ConfigureBusinessProcess targetconfigureBusinessProcess) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			BusinessProcessSetup sourceBusinessSetUP = sourceconfigureBusinessProcess.getFunctional()
					.getBusinessProcessSetup();
			BusinessProcessSetup targetBusinessSetUP = targetconfigureBusinessProcess.getFunctional()
					.getBusinessProcessSetup();
			if (!StringUtils.equalsIgnoreCase(sourceBusinessSetUP.getProcessDescription(),
					targetBusinessSetUP.getProcessDescription())) {
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FUNCTIONAL,
						BPConstant.PROCESS_DESCRIPTION, sourceBusinessSetUP.getProcessDescription(),
						targetBusinessSetUP.getProcessDescription()));
			}
			if (sourceBusinessSetUP.isEnableBoundedExecution() != targetBusinessSetUP.isEnableBoundedExecution()) {
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FUNCTIONAL,
						BPConstant.ENABLE_BOUNDED_EXECUTION, sourceBusinessSetUP.isEnableBoundedExecution(),
						targetBusinessSetUP.isEnableBoundedExecution()));
			}
			if (sourceBusinessSetUP.isProfileable() != targetBusinessSetUP.isProfileable()) {
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FUNCTIONAL,
						BPConstant.PROFILEABLE, sourceBusinessSetUP.isProfileable(),
						targetBusinessSetUP.isProfileable()));
			}
			processVariableComparator(sourceconfigureBusinessProcess.getFunctional().getProcessVariables(),
					targetconfigureBusinessProcess.getFunctional().getProcessVariables(), fieldsList);
			
			return fieldsList;
		} catch (Exception exception) {
			LOGGER.error("functional check error in config BP"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check for addition /deletion/modification of process
	 * variable in config BP.
	 */
	private static void processVariableComparator(List<ProcessVariables> sourceProcessVariableList,
			List<ProcessVariables> targetProcessVariableList, List<Fields> fieldList) {
		try {
			ArrayList<ProcessVariables> deletedPVList = new ArrayList<>();
			boolean isPVDeleted;
			boolean isAdded;
			for (ProcessVariables sourceProcessVariable : sourceProcessVariableList) {
				isPVDeleted = true;
				for (ProcessVariables targetProcessVariable : targetProcessVariableList) {
					if (StringUtils.equalsIgnoreCase(targetProcessVariable.getKey(), sourceProcessVariable.getKey())) {
						isPVDeleted = false;
						break;
					}
				}
				if (isPVDeleted) {
					deletedPVList.add(sourceProcessVariable);
				}
			}
			if (!deletedPVList.isEmpty()) {
				for (ProcessVariables processVariablesdeleted : deletedPVList) {
					processVariableFieldCheck(processVariablesdeleted, new ProcessVariables(), fieldList, false, true);
				}
			}
			for (ProcessVariables targetProcessVariable : targetProcessVariableList) {
				isAdded = true;
				for (ProcessVariables sourceProcessVariable : sourceProcessVariableList) {
					if (StringUtils.equalsIgnoreCase(sourceProcessVariable.getKey(), targetProcessVariable.getKey())) {
						isAdded = false;
						processVariableFieldCheck(sourceProcessVariable, targetProcessVariable, fieldList, false,
								false);
					}
				}
				if (isAdded) {
					processVariableFieldCheck(new ProcessVariables(), targetProcessVariable, fieldList, true, false);

				}
			}
		} catch (Exception exception) {
			LOGGER.error("Process variable comparision"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will compare the fields for Process variables.
	 */
	private static void processVariableFieldCheck(ProcessVariables sourceProcessVariable,
			ProcessVariables targetProcessVariable, List<Fields> fieldsList, boolean isAdded, boolean isDeleted) {
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetProcessVariable.getName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.NAME, BPConstant.EMPTY_STRING,
							targetProcessVariable.getName()));
				if (!StringUtils.isEmpty(targetProcessVariable.getDescription()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.DESCRIPTION, BPConstant.EMPTY_STRING,
							targetProcessVariable.getDescription()));
				if (!StringUtils.isEmpty(targetProcessVariable.getType().getTypeCategory()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.TYPE_CATEGORY, BPConstant.EMPTY_STRING,
							targetProcessVariable.getType().getTypeCategory()));
				if (!StringUtils.isEmpty(targetProcessVariable.getType().getTypeName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.TYPE_NAME, BPConstant.EMPTY_STRING,
							targetProcessVariable.getType().getTypeName()));
				compareProcessVariableDefaultValues(targetProcessVariable.getKey(), new PVValue(),
						targetProcessVariable.getValue(), fieldsList, true, false);
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
						BPConstant.PROCESS_VARIABLE, BPConstant.IS_MANDATORY, BPConstant.EMPTY_STRING,
						targetProcessVariable.getFlags().isIsMandatory()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
						BPConstant.PROCESS_VARIABLE, BPConstant.ISPROFILEABLEAT_SOLUTIONS, BPConstant.EMPTY_STRING,
						targetProcessVariable.getFlags().isIsProfileableAtSolutions()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
						BPConstant.PROCESS_VARIABLE, BPConstant.ISPROFILEABLEAT_OPERATION, BPConstant.EMPTY_STRING,
						targetProcessVariable.getFlags().isIsProfileableAtOperation()));
			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceProcessVariable.getName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.NAME, sourceProcessVariable.getName(),
							BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceProcessVariable.getDescription()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.DESCRIPTION, sourceProcessVariable.getDescription(),
							BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceProcessVariable.getType().getTypeCategory()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.TYPE_CATEGORY,
							sourceProcessVariable.getType().getTypeCategory(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceProcessVariable.getType().getTypeName()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.TYPE_NAME,
							sourceProcessVariable.getType().getTypeName(), BPConstant.EMPTY_STRING));
				compareProcessVariableDefaultValues(sourceProcessVariable.getKey(), sourceProcessVariable.getValue(),
						new PVValue(), fieldsList, false, true);
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
						BPConstant.PROCESS_VARIABLE, BPConstant.IS_MANDATORY,
						sourceProcessVariable.getFlags().isIsMandatory(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
						BPConstant.PROCESS_VARIABLE, BPConstant.ISPROFILEABLEAT_SOLUTIONS,
						sourceProcessVariable.getFlags().isIsProfileableAtSolutions(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
						BPConstant.PROCESS_VARIABLE, BPConstant.ISPROFILEABLEAT_OPERATION,
						sourceProcessVariable.getFlags().isIsProfileableAtOperation(), BPConstant.EMPTY_STRING));
			} else {
				if (!StringUtils.equalsIgnoreCase(targetProcessVariable.getName(), sourceProcessVariable.getName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.NAME, sourceProcessVariable.getName(),
							targetProcessVariable.getName()));
				}
				if (!StringUtils.equalsIgnoreCase(targetProcessVariable.getDescription(),
						sourceProcessVariable.getDescription())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.DESCRIPTION, sourceProcessVariable.getDescription(),
							targetProcessVariable.getDescription()));
				}
				if (!StringUtils.equalsIgnoreCase(targetProcessVariable.getType().getTypeCategory(),
						sourceProcessVariable.getType().getTypeCategory())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.TYPE_CATEGORY,
							sourceProcessVariable.getType().getTypeCategory(),
							targetProcessVariable.getType().getTypeCategory()));
				}
				if (!StringUtils.equalsIgnoreCase(targetProcessVariable.getType().getTypeName(),
						sourceProcessVariable.getType().getTypeName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.TYPE_NAME,
							sourceProcessVariable.getType().getTypeName(),
							targetProcessVariable.getType().getTypeName()));
				}
				compareProcessVariableDefaultValues(sourceProcessVariable.getKey(), sourceProcessVariable.getValue(),
						targetProcessVariable.getValue(), fieldsList, false, false);
				if (!targetProcessVariable.getFlags().isIsMandatory() == sourceProcessVariable.getFlags()
						.isIsMandatory()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.IS_MANDATORY,
							sourceProcessVariable.getFlags().isIsMandatory(),
							targetProcessVariable.getFlags().isIsMandatory()));
				}
				if (!targetProcessVariable.getFlags().isIsProfileableAtSolutions() == sourceProcessVariable.getFlags()
						.isIsProfileableAtSolutions()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.ISPROFILEABLEAT_SOLUTIONS,
							sourceProcessVariable.getFlags().isIsProfileableAtSolutions(),
							targetProcessVariable.getFlags().isIsProfileableAtSolutions()));
				}
				if (!targetProcessVariable.getFlags().isIsProfileableAtOperation() == sourceProcessVariable.getFlags()
						.isIsProfileableAtOperation()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
							BPConstant.PROCESS_VARIABLE, BPConstant.ISPROFILEABLEAT_OPERATION,
							sourceProcessVariable.getFlags().isIsProfileableAtOperation(),
							targetProcessVariable.getFlags().isIsProfileableAtOperation()));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Process Variable configured for BP config"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check for PV default value.
	 */
	private static void compareProcessVariableDefaultValues(String identifier, PVValue sourcePVValue,
			PVValue targetPVValue, List<Fields> fieldsList, boolean isAdded, boolean isDeleted) {
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetPVValue.getPrecision()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.PRECISION, BPConstant.EMPTY_STRING, targetPVValue.getPrecision()));
				if (!StringUtils.isEmpty(targetPVValue.getScale()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.SCALE, BPConstant.EMPTY_STRING, targetPVValue.getScale()));
				if (!StringUtils.isEmpty(targetPVValue.getIntValue()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.INT_VALUE, BPConstant.EMPTY_STRING, targetPVValue.getIntValue()));
				if (!StringUtils.isEmpty(targetPVValue.getStringValue()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.STRING_VALUE, BPConstant.EMPTY_STRING, targetPVValue.getStringValue()));
				if (!StringUtils.isEmpty(targetPVValue.getBooleanValue()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.BOOLEAN_VALUE, BPConstant.EMPTY_STRING, targetPVValue.getBooleanValue()));
				if (!StringUtils.isEmpty(targetPVValue.getDateValue()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.DATE_VALUE, BPConstant.EMPTY_STRING, targetPVValue.getDateValue()));
			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourcePVValue.getPrecision()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.PRECISION, sourcePVValue.getPrecision(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourcePVValue.getScale()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.SCALE, sourcePVValue.getScale(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourcePVValue.getIntValue()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.INT_VALUE, sourcePVValue.getIntValue(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourcePVValue.getStringValue()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.STRING_VALUE, sourcePVValue.getStringValue(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourcePVValue.getBooleanValue()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.BOOLEAN_VALUE, sourcePVValue.getBooleanValue(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourcePVValue.getDateValue()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.DATE_VALUE, sourcePVValue.getDateValue(), BPConstant.EMPTY_STRING));
			} else {
				if (!StringUtils.equalsIgnoreCase(targetPVValue.getPrecision(), sourcePVValue.getPrecision())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.PRECISION, sourcePVValue.getPrecision(), targetPVValue.getPrecision()));
				}
				if (!StringUtils.equalsIgnoreCase(targetPVValue.getScale(), sourcePVValue.getScale())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.SCALE, sourcePVValue.getScale(), targetPVValue.getScale()));
				}
				if (!StringUtils.equalsIgnoreCase(targetPVValue.getIntValue(), sourcePVValue.getIntValue())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.INT_VALUE, sourcePVValue.getIntValue(), targetPVValue.getIntValue()));
				}
				if (!StringUtils.equalsIgnoreCase(targetPVValue.getStringValue(), sourcePVValue.getStringValue())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.STRING_VALUE, sourcePVValue.getStringValue(), targetPVValue.getStringValue()));
				}
				if (!StringUtils.equalsIgnoreCase(targetPVValue.getBooleanValue(), sourcePVValue.getBooleanValue())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.BOOLEAN_VALUE, sourcePVValue.getBooleanValue(),
							targetPVValue.getBooleanValue()));
				}
				if (!StringUtils.equalsIgnoreCase(targetPVValue.getDateValue(), sourcePVValue.getDateValue())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(identifier, BPConstant.PROCESS_VARIABLE,
							BPConstant.DATE_VALUE, sourcePVValue.getDateValue(), targetPVValue.getDateValue()));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Default values error in Process variable"+exception);
			throw new BPException(exception.getMessage());
		}
	}
}
