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

public class StartOperatorReport {
	private static final Logger LOGGER = LoggerFactory.getLogger(StartOperatorReport.class);

	/**
	 * This method will compare the start operator fields.
	 */
	public static List<Fields> startOperatorComparision(Operators sourceVersionOperator,
			Operators targetVersionOperator, boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetVersionOperator.getName())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, BPConstant.EMPTY_STRING, targetVersionOperator.getName()));
				}
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getInputBeType()));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
						BPConstant.BUSINESS_SETTINGS, BPConstant.EVENT_LOGGING, BPConstant.EMPTY_STRING,
						targetVersionOperator.getBusinessSettings().isEventLogging()));
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
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getInputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.INPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getInputBeType(), BPConstant.EMPTY_STRING));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
						BPConstant.BUSINESS_SETTINGS, BPConstant.EVENT_LOGGING,
						sourceVersionOperator.getBusinessSettings().isEventLogging(), BPConstant.EMPTY_STRING));
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
							BPConstant.OPERATORS, BPConstant.INPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getInputBeType(),
							targetVersionOperator.getBusinessSettings().getInputBeType()));
				}
				if (sourceVersionOperator.getBusinessSettings().isEventLogging() != targetVersionOperator
						.getBusinessSettings().isEventLogging()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.EVENT_LOGGING,
							sourceVersionOperator.getBusinessSettings().isEventLogging(),
							targetVersionOperator.getBusinessSettings().isEventLogging()));
				}
				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, false);
			}
			return fieldsList;
		} catch (Exception exception) {
			LOGGER.error("Start operator comparision error"+exception);
			throw new BPException(exception.getMessage());
		}
	}

}
