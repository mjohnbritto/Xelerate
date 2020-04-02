package com.suntecgroup.bp.designer.review.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.frontend.beans.Operators;
import com.suntecgroup.bp.designer.model.Fields;
import com.suntecgroup.bp.designer.services.impl.BPDesignerServiceImpl;
import com.suntecgroup.bp.util.BPConstant;

public class InvokeBSExternalReport {
	private static final Logger LOGGER = LoggerFactory.getLogger(InvokeBSExternalReport.class);

	/**
	 * This method will compare the ibs external operator fields.
	 */
	public static List<Fields> invokeBSExternalOperatorComparision(Operators sourceVersionOperator,
			Operators targetVersionOperator, boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetVersionOperator.getName())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, BPConstant.EMPTY_STRING, targetVersionOperator.getName()));
				}

				// Business Settings
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getHttpMethod())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.HTTP_METHOD, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getHttpMethod()));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
						BPConstant.BUSINESS_SETTINGS, BPConstant.BATCHABLE, BPConstant.EMPTY_STRING,
						targetVersionOperator.getBusinessSettings().isBatchable()));
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getInputBeType()));
				}
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getOutputBeType()));
				}
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getSelectedInputOption())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.SELECTED_INPUT_OPTION, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getSelectedInputOption()));
				}
				// Properties
				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(), targetVersionOperator.getProperties(),
						fieldsList, true, false);
				// Comments
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, true, false);
			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceVersionOperator.getName())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, sourceVersionOperator.getName(), BPConstant.EMPTY_STRING));
				}
				// Business Settings
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getHttpMethod())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.HTTP_METHOD,
							sourceVersionOperator.getBusinessSettings().getHttpMethod(), BPConstant.EMPTY_STRING));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
						BPConstant.BUSINESS_SETTINGS, BPConstant.BATCHABLE,
						sourceVersionOperator.getBusinessSettings().isBatchable(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getInputBeType(), BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getOutputBeType(), BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getSelectedInputOption())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.SELECTED_INPUT_OPTION,
							sourceVersionOperator.getBusinessSettings().getSelectedInputOption(),
							BPConstant.EMPTY_STRING));
				}
				// Properties
				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, true);
				// Comments
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, true);
			} else {

				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getName(), targetVersionOperator.getName())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, sourceVersionOperator.getName(), targetVersionOperator.getName()));
				}
				// Business Settings
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getHttpMethod(),
						targetVersionOperator.getBusinessSettings().getHttpMethod())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.HTTP_METHOD,
							sourceVersionOperator.getBusinessSettings().getHttpMethod(),
							targetVersionOperator.getBusinessSettings().getHttpMethod()));
				}
				if (sourceVersionOperator.getBusinessSettings().isBatchable() != targetVersionOperator
						.getBusinessSettings().isBatchable()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BATCHABLE,
							sourceVersionOperator.getBusinessSettings().isBatchable(), BPConstant.EMPTY_STRING));
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
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getSelectedInputOption(),
						targetVersionOperator.getBusinessSettings().getSelectedInputOption())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.SELECTED_INPUT_OPTION,
							sourceVersionOperator.getBusinessSettings().getSelectedInputOption(),
							targetVersionOperator.getBusinessSettings().getSelectedInputOption()));
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
			LOGGER.error("Error occurred while comparing ibs external operator"+exception);
			throw new BPException(exception.getMessage());
		}
	}

}
