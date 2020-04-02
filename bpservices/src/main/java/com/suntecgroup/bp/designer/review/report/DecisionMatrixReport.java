package com.suntecgroup.bp.designer.review.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.frontend.beans.Decisions;
import com.suntecgroup.bp.designer.frontend.beans.Operators;
import com.suntecgroup.bp.designer.frontend.beans.ProcessVariable;
import com.suntecgroup.bp.designer.frontend.beans.ProcessVariables;
import com.suntecgroup.bp.designer.model.Fields;
import com.suntecgroup.bp.designer.services.impl.BPDesignerServiceImpl;
import com.suntecgroup.bp.util.BPConstant;

public class DecisionMatrixReport {
	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionMatrixReport.class);

	/**
	 * This method will compare the decision matrix operator fields.
	 */
	public static List<Fields> decisionMatrixOperatorComparision(Operators sourceVersionOperator,
			Operators targetVersionOperator, boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {

			if (isAdded) {
				if (!StringUtils.isEmpty(targetVersionOperator.getName()))
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, BPConstant.EMPTY_STRING, targetVersionOperator.getName()));
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getInputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getInputBeType()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.DECISIONS,
						BPConstant.EXCLUSIVE, BPConstant.EMPTY_STRING,
						targetVersionOperator.getBusinessSettings().isExclusive()));
				for (Decisions targetDecision : targetVersionOperator.getBusinessSettings().getDecisions()) {
					decisionListComparision(new Decisions(), targetDecision, true, false, fieldsList);
				}
				for (ProcessVariable targetProcessVariable : targetVersionOperator.getProcessVariable()) {
					if (!StringUtils.isEmpty(targetProcessVariable.getDecisionName()))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
								BPConstant.DECISIONS, BPConstant.PROCESS_VARIABLE, BPConstant.EMPTY_STRING,
								targetProcessVariable.getDecisionName()));
					for (ProcessVariables processVariable : targetProcessVariable.getProcessVariable()) {
						if (!StringUtils.isEmpty(checkProcessVariableType(processVariable)))
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
									BPConstant.PROCESS_VARIABLE, processVariable.getName(), BPConstant.EMPTY_STRING,
									checkProcessVariableType(processVariable)));
					}
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
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getInputBeType()))
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getInputBeType(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.DECISIONS,
						BPConstant.EXCLUSIVE, sourceVersionOperator.getBusinessSettings().isExclusive(),
						BPConstant.EMPTY_STRING));
				for (Decisions sourceDecision : sourceVersionOperator.getBusinessSettings().getDecisions()) {
					decisionListComparision(sourceDecision, new Decisions(), false, true, fieldsList);
				}
				for (ProcessVariable sourceProcessVariable : sourceVersionOperator.getProcessVariable()) {
					if (!StringUtils.isEmpty(sourceProcessVariable.getDecisionName()))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
								BPConstant.DECISIONS, BPConstant.PROCESS_VARIABLE,
								sourceProcessVariable.getDecisionName(), BPConstant.EMPTY_STRING));
					for (ProcessVariables processVariable : sourceProcessVariable.getProcessVariable()) {
						if (!StringUtils.isEmpty(checkProcessVariableType(processVariable)))
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(processVariable.getKey(),
									BPConstant.PROCESS_VARIABLE, processVariable.getName(),
									checkProcessVariableType(processVariable), BPConstant.EMPTY_STRING));
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
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getInputBeType(),
						targetVersionOperator.getBusinessSettings().getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getInputBeType(),
							targetVersionOperator.getBusinessSettings().getInputBeType()));
				}
				if (!sourceVersionOperator.getBusinessSettings().isExclusive() == (targetVersionOperator
						.getBusinessSettings().isExclusive())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.EXCLUSIVE,
							sourceVersionOperator.getBusinessSettings().isExclusive(),
							targetVersionOperator.getBusinessSettings().isExclusive()));
				}
				decisionsComparator(sourceVersionOperator.getBusinessSettings().getDecisions(),
						targetVersionOperator.getBusinessSettings().getDecisions(), fieldsList);
				decisionProcessVariableComparision(sourceVersionOperator.getProcessVariable(),
						targetVersionOperator.getProcessVariable(), fieldsList);
				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, false);
			}
			return fieldsList;
		} catch (Exception exception) {
			LOGGER.error("Decision matrix comparision error"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	private static void decisionProcessVariableComparision(List<ProcessVariable> sourceDecisionProcessVariableList,
			List<ProcessVariable> targetDecisionProcessVariableList, List<Fields> fieldsList) {
		boolean isAdded;
		boolean isDeleted;
		try {
			ArrayList<ProcessVariable> deletedList = new ArrayList<>();
			for (ProcessVariable sourceDecisionProcessVariable : sourceDecisionProcessVariableList) {
				isDeleted = true;
				for (ProcessVariable targetDecisionProcessVariable : targetDecisionProcessVariableList) {
					if (StringUtils.equalsIgnoreCase(targetDecisionProcessVariable.getKey(),
							sourceDecisionProcessVariable.getKey())) {
						isDeleted = false;
					}
				}
				if (isDeleted) {
					deletedList.add(sourceDecisionProcessVariable);
				}
			}
			if (!deletedList.isEmpty()) {
				for (ProcessVariable sourceProcessVariable : deletedList) {
					for (ProcessVariables processVariable : sourceProcessVariable.getProcessVariable()) {
						if (!StringUtils.isEmpty(checkProcessVariableType(processVariable)))
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(processVariable.getKey(),
									BPConstant.PROCESS_VARIABLE, processVariable.getName(),
									checkProcessVariableType(processVariable), BPConstant.EMPTY_STRING));
					}
				}
			}

			for (ProcessVariable targetDecisionProcessVariable : targetDecisionProcessVariableList) {
				isAdded = true;
				for (ProcessVariable sourceDecisionProcessVariable : sourceDecisionProcessVariableList) {
					if (StringUtils.equalsIgnoreCase(sourceDecisionProcessVariable.getKey(),
							targetDecisionProcessVariable.getKey())) {
						isAdded = false;
						processVariableValueCheck(sourceDecisionProcessVariable.getProcessVariable(),
								targetDecisionProcessVariable.getProcessVariable(), fieldsList);
					}
				}
				if (isAdded) {
					for (ProcessVariables targetProcessVariable : targetDecisionProcessVariable.getProcessVariable()) {
						if (!StringUtils.isEmpty(checkProcessVariableType(targetProcessVariable)))
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
									BPConstant.PROCESS_VARIABLE, targetProcessVariable.getName(),
									BPConstant.EMPTY_STRING, checkProcessVariableType(targetProcessVariable)));
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Decision Process variable error"+exception);
			throw new BPException(exception.getMessage());
		}

	}

	private static void processVariableValueCheck(List<ProcessVariables> sourceProcessVariableList,
			List<ProcessVariables> targetProcessVariableList, List<Fields> fieldsList) {
		boolean isAdded;
		boolean isDeleted;
		try {
			ArrayList<ProcessVariables> deletedList = new ArrayList<>();
			if (sourceProcessVariableList != null) {
				for (ProcessVariables sourceProcessVariable : sourceProcessVariableList) {
					isDeleted = true;
					if (targetProcessVariableList != null) {
						for (ProcessVariables targetProcessVariable : targetProcessVariableList) {
							if (StringUtils.equalsIgnoreCase(targetProcessVariable.getKey(),
									sourceProcessVariable.getKey())) {
								isDeleted = false;
							}
						}
					}
					if (isDeleted) {
						deletedList.add(sourceProcessVariable);
					}
				}
			}
			if (!deletedList.isEmpty()) {
				for (ProcessVariables sourceProcessVariable : deletedList) {
					String oldValue = checkProcessVariableType(sourceProcessVariable);
					if (!StringUtils.isEmpty(oldValue))
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
								BPConstant.PROCESS_VARIABLE, BPConstant.VALUE, oldValue, BPConstant.EMPTY_STRING));
				}
			}
			if (targetProcessVariableList != null) {
				for (ProcessVariables targetProcessVariable : targetProcessVariableList) {
					isAdded = true;
					if (sourceProcessVariableList != null) {
						for (ProcessVariables sourceProcessVariable : sourceProcessVariableList) {
							if (StringUtils.equalsIgnoreCase(targetProcessVariable.getKey(),
									sourceProcessVariable.getKey())) {
								isAdded = false;
								String oldValue = checkProcessVariableType(sourceProcessVariable);
								String newValue = checkProcessVariableType(targetProcessVariable);
								if (!StringUtils.equalsIgnoreCase(oldValue, newValue)) {
									fieldsList
											.add(BPDesignerServiceImpl.buildResponseJson(sourceProcessVariable.getKey(),
													BPConstant.PROCESS_VARIABLE, BPConstant.VALUE, oldValue, newValue));
								}
							}
						}
						if (isAdded) {
							String newValue = checkProcessVariableType(targetProcessVariable);
							if (!StringUtils.isEmpty(newValue))
								fieldsList.add(BPDesignerServiceImpl.buildResponseJson(targetProcessVariable.getKey(),
										BPConstant.PROCESS_VARIABLE, BPConstant.VALUE, BPConstant.EMPTY_STRING,
										newValue));
						}
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Decision Process variable value error"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check the output param type.
	 */
	private static String checkProcessVariableType(ProcessVariables processVariable) {
		String type = processVariable.getType().getTypeName();
		String value = null;
		switch (type) {
		case BPConstant.STRING:
			value = processVariable.getValue().getStringValue();
			break;
		case BPConstant.BOOLEAN:
			value = processVariable.getValue().getBooleanValue();
			break;
		case BPConstant.DATE_TIME:
			value = processVariable.getValue().getDateValue();
			break;
		case BPConstant.NUMBER:
			value = processVariable.getValue().getIntValue();
			break;
		default:
			value = BPConstant.EMPTY_STRING;
		}
		return value;
	}

	/**
	 * This method will check for addition/deletion or modification of
	 * decisions.
	 */
	private static void decisionsComparator(List<Decisions> sourceDecisions, List<Decisions> targetDecisions,
			List<Fields> fieldList) {
		try {
			ArrayList<Decisions> deletedDecisionList = new ArrayList<>();
			for (Decisions sourceDecision : sourceDecisions) {
				boolean isDecisiondeleted = true;
				for (Decisions targetDecision : targetDecisions) {
					if (StringUtils.equalsIgnoreCase(targetDecision.getKey(), sourceDecision.getKey())) {
						isDecisiondeleted = false;
					}
				}
				if (isDecisiondeleted) {
					deletedDecisionList.add(sourceDecision);
				}
			}
			for (Decisions targetDecision : targetDecisions) {
				boolean isAdded = true;
				for (Decisions sourceDecision : sourceDecisions) {
					if (StringUtils.equalsIgnoreCase(targetDecision.getKey(), sourceDecision.getKey())) {
						isAdded = false;
						decisionListComparision(sourceDecision, targetDecision, false, false, fieldList);
					}
				}
				if (isAdded) {
					decisionListComparision(new Decisions(), targetDecision, true, false, fieldList);
				}
			}
			if (deletedDecisionList.size() > 0) {
				for (Decisions deletedDecision : deletedDecisionList) {
					decisionListComparision(deletedDecision, new Decisions(), false, true, fieldList);
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Error occurred while comparing decisions"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will compare the decision fields.
	 */
	private static void decisionListComparision(Decisions sourceDecision, Decisions targetDecision, boolean isAdded,
			boolean isDeleted, List<Fields> fieldList) {
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetDecision.getDecisionName()))
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(targetDecision.getKey(), BPConstant.DECISIONS,
							BPConstant.DECISION_NAME, BPConstant.EMPTY_STRING, targetDecision.getDecisionName()));
				if (!StringUtils.isEmpty(targetDecision.getExpression()))
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(targetDecision.getKey(), BPConstant.DECISIONS,
							BPConstant.EXPRESSION, BPConstant.EMPTY_STRING, targetDecision.getExpression()));

			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceDecision.getDecisionName()))
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(sourceDecision.getKey(), BPConstant.DECISIONS,
							BPConstant.DECISION_NAME, sourceDecision.getDecisionName(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceDecision.getExpression()))
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(sourceDecision.getKey(), BPConstant.DECISIONS,
							BPConstant.EXPRESSION, sourceDecision.getExpression(), BPConstant.EMPTY_STRING));

			} else {
				if (!StringUtils.equalsIgnoreCase(targetDecision.getDecisionName(), sourceDecision.getDecisionName())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(sourceDecision.getKey(), BPConstant.DECISIONS,
							BPConstant.DECISION_NAME, sourceDecision.getDecisionName(),
							targetDecision.getDecisionName()));
				}
				if (!StringUtils.equalsIgnoreCase(targetDecision.getExpression(), sourceDecision.getExpression())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(sourceDecision.getKey(), BPConstant.DECISIONS,
							BPConstant.EXPRESSION, sourceDecision.getExpression(), targetDecision.getExpression()));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Error occurred while comparing decision fields"+exception);
			throw new BPException(exception.getMessage());
		}
	}
}
