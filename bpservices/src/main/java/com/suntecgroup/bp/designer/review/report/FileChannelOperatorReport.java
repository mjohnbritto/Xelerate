package com.suntecgroup.bp.designer.review.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Attribute;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Content;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.FixedWidth;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Footer;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Header;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.output.Eviction;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.output.OFCIContent;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.output.OFCIFooter;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.output.OFCIHeader;
import com.suntecgroup.bp.designer.frontend.beans.Operators;
import com.suntecgroup.bp.designer.model.Fields;
import com.suntecgroup.bp.designer.services.impl.BPDesignerServiceImpl;
import com.suntecgroup.bp.util.BPConstant;

/**
 * Implementation class File I/O operator comparison for Form review
 *
 */
public class FileChannelOperatorReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileChannelOperatorReport.class);

	/**
	 * This method will check for file input operator changes.
	 */
	public static List<Fields> fileInputOperatorComparision(Operators sourceVersionOperator,
			Operators targetVersionOperator, boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetVersionOperator.getName())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, BPConstant.EMPTY_STRING, targetVersionOperator.getName()));
				}
				if (!StringUtils.isEmpty(targetVersionOperator.getSelected())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.SELECTED, BPConstant.EMPTY_STRING, targetVersionOperator.getSelected()));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
						BPConstant.AUTO_GENERATION, BPConstant.EMPTY_STRING, targetVersionOperator.isAutoGeneration()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
						BPConstant.AUTO_GENERATE_MAPPING, BPConstant.EMPTY_STRING,
						targetVersionOperator.isAutoGenerateMapping()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
						BPConstant.CONTINUOUS, BPConstant.EMPTY_STRING, targetVersionOperator.getContinuous()));

				// Business settings
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getOutputBeType()));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
						BPConstant.BUSINESS_SETTINGS, BPConstant.BATCHABLE, BPConstant.EMPTY_STRING,
						targetVersionOperator.getBusinessSettings().isBatchable()));
				if (!StringUtils.isEmpty(targetVersionOperator.getBusinessSettings().getBatchSize())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BATCHSIZE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getBusinessSettings().getBatchSize()));
				}

				// header
				headerComparisionForFileChannel(null, targetVersionOperator.getHeader(), true, false, fieldsList);
				// footer
				footerComparisionForFileChannel(null, targetVersionOperator.getFooter(), true, false, fieldsList);
				// content
				contentComparisionForFileChannel(null, targetVersionOperator.getContent(), true, false, fieldsList);

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
				if (!StringUtils.isEmpty(sourceVersionOperator.getSelected())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.SELECTED, sourceVersionOperator.getSelected(), BPConstant.EMPTY_STRING));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
						BPConstant.AUTO_GENERATION, sourceVersionOperator.isAutoGeneration(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
						BPConstant.AUTO_GENERATE_MAPPING, sourceVersionOperator.isAutoGenerateMapping(),
						BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
						BPConstant.CONTINUOUS, sourceVersionOperator.getContinuous(), BPConstant.EMPTY_STRING));

				// Business settings
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getOutputBeType(), BPConstant.EMPTY_STRING));
				}
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
						BPConstant.BUSINESS_SETTINGS, BPConstant.BATCHABLE,
						sourceVersionOperator.getBusinessSettings().isBatchable(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(sourceVersionOperator.getBusinessSettings().getBatchSize())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BATCHSIZE,
							sourceVersionOperator.getBusinessSettings().getBatchSize(), BPConstant.EMPTY_STRING));
				}

				// header
				headerComparisionForFileChannel(sourceVersionOperator.getHeader(), null, false, true, fieldsList);
				// footer
				footerComparisionForFileChannel(sourceVersionOperator.getFooter(), null, false, true, fieldsList);
				// content
				contentComparisionForFileChannel(sourceVersionOperator.getContent(), null, false, true, fieldsList);

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
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getSelected(),
						targetVersionOperator.getSelected())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OPERATORS, BPConstant.SELECTED, sourceVersionOperator.getSelected(),
							targetVersionOperator.getSelected()));
				}
				if (sourceVersionOperator.isAutoGeneration() != targetVersionOperator.isAutoGeneration()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OPERATORS, BPConstant.AUTO_GENERATION, sourceVersionOperator.isAutoGeneration(),
							targetVersionOperator.isAutoGeneration()));
				}
				if (sourceVersionOperator.isAutoGenerateMapping() != targetVersionOperator.isAutoGenerateMapping()) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.AUTO_GENERATE_MAPPING, sourceVersionOperator.isAutoGenerateMapping(),
									targetVersionOperator.isAutoGenerateMapping()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getContinuous(),
						targetVersionOperator.getContinuous())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OPERATORS, BPConstant.CONTINUOUS, sourceVersionOperator.getContinuous(),
							targetVersionOperator.getContinuous()));
				}

				// Business settings
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getOutputBeType(),
						targetVersionOperator.getBusinessSettings().getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceVersionOperator.getBusinessSettings().getOutputBeType(),
							targetVersionOperator.getBusinessSettings().getOutputBeType()));
				}
				if (sourceVersionOperator.getBusinessSettings().isBatchable() != targetVersionOperator
						.getBusinessSettings().isBatchable()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BATCHABLE,
							sourceVersionOperator.getBusinessSettings().isBatchable(),
							targetVersionOperator.getBusinessSettings().isBatchable()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getBusinessSettings().getBatchSize(),
						targetVersionOperator.getBusinessSettings().getBatchSize())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.BATCHSIZE,
							sourceVersionOperator.getBusinessSettings().getBatchSize(),
							targetVersionOperator.getBusinessSettings().getBatchSize()));
				}

				// header
				headerComparisionForFileChannel(sourceVersionOperator.getHeader(), targetVersionOperator.getHeader(),
						false, false, fieldsList);
				// footer
				footerComparisionForFileChannel(sourceVersionOperator.getFooter(), targetVersionOperator.getFooter(),
						false, false, fieldsList);
				// content
				contentComparisionForFileChannel(sourceVersionOperator.getContent(), targetVersionOperator.getContent(),
						false, false, fieldsList);

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, false);
				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, false);
			}
			return fieldsList;
		} catch (Exception exception) {
			LOGGER.error("File Input channel Integration comparision error" + exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check for Header in file input operator changes.
	 */
	private static void headerComparisionForFileChannel(Header sourceHeader, Header targetHeader, boolean isAdded,
			boolean isDeleted, List<Fields> fieldsList) {
		try {
			if (isAdded) {
				// COMMON HEADER
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
						BPConstant.HAS_HEADER, BPConstant.EMPTY_STRING, targetHeader.isHasHeader()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
						BPConstant.HEADER_LINES, BPConstant.EMPTY_STRING, targetHeader.getHeaderLines()));
				// FIXED WIDTH
				compareFixedWidth(null, targetHeader.getFixedWidth(), isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(targetHeader.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, BPConstant.EMPTY_STRING,
							targetHeader.getDelimited().getRecord()));
				}
				if (!StringUtils.isBlank(targetHeader.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER, BPConstant.EMPTY_STRING,
							targetHeader.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(null, targetHeader.getDelimited().getAttributes(), isAdded, isDeleted,
						fieldsList);
			} else if (isDeleted) {
				// COMMON HEADER
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
						BPConstant.HAS_HEADER, sourceHeader.isHasHeader(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
						BPConstant.HEADER_LINES, sourceHeader.getHeaderLines(), BPConstant.EMPTY_STRING));
				// FIXED WIDTH
				compareFixedWidth(sourceHeader.getFixedWidth(), null, isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(sourceHeader.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceHeader.getDelimited().getRecord(),
							BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isBlank(sourceHeader.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceHeader.getDelimited().getAttribute(), BPConstant.EMPTY_STRING));
				}
				compareDelimittedAttribute(sourceHeader.getDelimited().getAttributes(), null, isAdded, isDeleted,
						fieldsList);
			} else {
				// COMMON HEADER
				if (sourceHeader.isHasHeader() != targetHeader.isHasHeader()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
							BPConstant.HAS_HEADER, sourceHeader.isHasHeader(), targetHeader.isHasHeader()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceHeader.getHeaderLines(), targetHeader.getHeaderLines())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
							BPConstant.HEADER_LINES, sourceHeader.getHeaderLines(), targetHeader.getHeaderLines()));
				}
				// FIXED WIDTH
				compareFixedWidth(sourceHeader.getFixedWidth(), targetHeader.getFixedWidth(), isAdded, isDeleted,
						fieldsList);
				// DELIMTTED
				if (!StringUtils.equalsIgnoreCase(sourceHeader.getDelimited().getRecord(),
						targetHeader.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceHeader.getDelimited().getRecord(),
							targetHeader.getDelimited().getRecord()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceHeader.getDelimited().getAttribute(),
						targetHeader.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceHeader.getDelimited().getAttribute(), targetHeader.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(sourceHeader.getDelimited().getAttributes(),
						targetHeader.getDelimited().getAttributes(), isAdded, isDeleted, fieldsList);
			}
		} catch (Exception exception) {
			LOGGER.error("File input channel Integration Header comparision error" + exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check for Footer in file input operator changes.
	 */
	private static void footerComparisionForFileChannel(Footer sourceFooter, Footer targetFooter, boolean isAdded,
			boolean isDeleted, List<Fields> fieldsList) {
		try {
			if (isAdded) {
				// COMMON FOOTER
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
						BPConstant.HAS_FOOTER, BPConstant.EMPTY_STRING, targetFooter.isHasFooter()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
						BPConstant.FOOTER_LINES, BPConstant.EMPTY_STRING, targetFooter.getFooterLines()));
				// FIXED WIDTH
				compareFixedWidth(null, targetFooter.getFixedWidth(), isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(targetFooter.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, BPConstant.EMPTY_STRING,
							targetFooter.getDelimited().getRecord()));
				}
				if (!StringUtils.isBlank(targetFooter.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER, BPConstant.EMPTY_STRING,
							targetFooter.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(null, targetFooter.getDelimited().getAttributes(), isAdded, isDeleted,
						fieldsList);
			} else if (isDeleted) {
				// COMMON FOOTER
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
						BPConstant.HAS_FOOTER, sourceFooter.isHasFooter(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
						BPConstant.FOOTER_LINES, sourceFooter.getFooterLines(), BPConstant.EMPTY_STRING));
				// FIXED WIDTH
				compareFixedWidth(sourceFooter.getFixedWidth(), null, isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(sourceFooter.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceFooter.getDelimited().getRecord(),
							BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isBlank(sourceFooter.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceFooter.getDelimited().getAttribute(), BPConstant.EMPTY_STRING));
				}
				compareDelimittedAttribute(sourceFooter.getDelimited().getAttributes(), null, isAdded, isDeleted,
						fieldsList);
			} else {
				// COMMON FOOTER
				if (sourceFooter.isHasFooter() != targetFooter.isHasFooter()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
							BPConstant.HAS_FOOTER, sourceFooter.isHasFooter(), targetFooter.isHasFooter()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceFooter.getFooterLines(), targetFooter.getFooterLines())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
							BPConstant.FOOTER_LINES, sourceFooter.getFooterLines(), targetFooter.getFooterLines()));
				}
				// FIXED WIDTH
				compareFixedWidth(sourceFooter.getFixedWidth(), targetFooter.getFixedWidth(), isAdded, isDeleted,
						fieldsList);
				// DELIMTTED
				if (!StringUtils.equalsIgnoreCase(sourceFooter.getDelimited().getRecord(),
						targetFooter.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceFooter.getDelimited().getRecord(),
							targetFooter.getDelimited().getRecord()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceFooter.getDelimited().getAttribute(),
						targetFooter.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceFooter.getDelimited().getAttribute(), targetFooter.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(sourceFooter.getDelimited().getAttributes(),
						targetFooter.getDelimited().getAttributes(), isAdded, isDeleted, fieldsList);
			}
		} catch (Exception exception) {
			LOGGER.error("File input channel Integration Footer comparision error" + exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check for Content in file input operator changes.
	 */
	private static void contentComparisionForFileChannel(Content sourceContent, Content targetContent, boolean isAdded,
			boolean isDeleted, List<Fields> fieldsList) {
		try {
			if (isAdded) {
				// FIXED WIDTH
				compareFixedWidth(null, targetContent.getFixedWidth(), isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(targetContent.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, BPConstant.EMPTY_STRING,
							targetContent.getDelimited().getRecord()));
				}
				if (!StringUtils.isBlank(targetContent.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER, BPConstant.EMPTY_STRING,
							targetContent.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(null, targetContent.getDelimited().getAttributes(), isAdded, isDeleted,
						fieldsList);
			} else if (isDeleted) {
				// FIXED WIDTH
				compareFixedWidth(sourceContent.getFixedWidth(), null, isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(sourceContent.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceContent.getDelimited().getRecord(),
							BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isBlank(sourceContent.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceContent.getDelimited().getAttribute(), BPConstant.EMPTY_STRING));
				}
				compareDelimittedAttribute(sourceContent.getDelimited().getAttributes(), null, isAdded, isDeleted,
						fieldsList);
			} else {
				// FIXED WIDTH
				compareFixedWidth(sourceContent.getFixedWidth(), targetContent.getFixedWidth(), isAdded, isDeleted,
						fieldsList);
				// DELIMTTED
				if (!StringUtils.equalsIgnoreCase(sourceContent.getDelimited().getRecord(),
						targetContent.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceContent.getDelimited().getRecord(),
							targetContent.getDelimited().getRecord()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceContent.getDelimited().getAttribute(),
						targetContent.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceContent.getDelimited().getAttribute(), targetContent.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(sourceContent.getDelimited().getAttributes(),
						targetContent.getDelimited().getAttributes(), isAdded, isDeleted, fieldsList);
			}
		} catch (Exception exception) {
			LOGGER.error("File input channel Integration Content comparision error" + exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check for file output operator changes.
	 */
	public static List<Fields> fileOutputOperatorComparision(Operators sourceVersionOperator,
			Operators targetVersionOperator, boolean isAdded, boolean isDeleted) {
		List<Fields> fieldsList = new ArrayList<>();
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetVersionOperator.getName())) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.NAME, BPConstant.EMPTY_STRING, targetVersionOperator.getName()));
				}

				if (!StringUtils.isEmpty(targetVersionOperator.getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE, BPConstant.EMPTY_STRING,
							targetVersionOperator.getOutputBeType()));
				}

				if (!StringUtils.isEmpty(targetVersionOperator.getContinuous())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OPERATORS, BPConstant.CONTINUOUS, BPConstant.EMPTY_STRING,
							targetVersionOperator.getContinuous()));
				}

				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
						BPConstant.AUTO_GENERATION, BPConstant.EMPTY_STRING, targetVersionOperator.isAutoGeneration()));

				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
						BPConstant.BUSINESS_SETTINGS, BPConstant.AUTO_GENERATE_MAPPING, BPConstant.EMPTY_STRING,
						targetVersionOperator.isAutoGenerateMapping()));

				if (!StringUtils.isEmpty(targetVersionOperator.getOutputFileName().getStaticName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OUTPUTFILENAME, BPConstant.STATIC_NAME, BPConstant.EMPTY_STRING,
							targetVersionOperator.getOutputFileName().getStaticName()));
				}

				if (!StringUtils.isEmpty(targetVersionOperator.getOutputFileName().getDynamicName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OUTPUTFILENAME, BPConstant.DYNAMIC_NAME, BPConstant.EMPTY_STRING,
							targetVersionOperator.getOutputFileName().getDynamicName()));
				}
				// eviction
				compareEviction(new Eviction(), targetVersionOperator.getEviction(), true, false, fieldsList);
				// header
				headerComparisionForFileChannelOutput(new OFCIHeader(), targetVersionOperator.getOFCIHeader(), true,
						false, fieldsList);
				// footer
				footerComparisionForFileChannelOutput(new OFCIFooter(), targetVersionOperator.getOFCIFooter(), true,
						false, fieldsList);
				// content
				contentComparisionForFileChannelOutput(new OFCIContent(), targetVersionOperator.getOFCIContent(), true,
						false, fieldsList);

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

				if (!StringUtils.isEmpty(sourceVersionOperator.getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceVersionOperator.getOutputBeType(), BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(targetVersionOperator.getContinuous())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OPERATORS, BPConstant.CONTINUOUS, sourceVersionOperator.getContinuous(),
							BPConstant.EMPTY_STRING));
				}

				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
						BPConstant.AUTO_GENERATION, sourceVersionOperator.isAutoGeneration(), BPConstant.EMPTY_STRING));

				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
						BPConstant.AUTO_GENERATE_MAPPING, sourceVersionOperator.isAutoGenerateMapping(),
						BPConstant.EMPTY_STRING));

				if (!StringUtils.isEmpty(sourceVersionOperator.getOutputFileName().getStaticName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OUTPUTFILENAME, BPConstant.STATIC_NAME,
							sourceVersionOperator.getOutputFileName().getStaticName(), BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(sourceVersionOperator.getOutputFileName().getDynamicName())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OUTPUTFILENAME, BPConstant.DYNAMIC_NAME,
							sourceVersionOperator.getOutputFileName().getDynamicName(), BPConstant.EMPTY_STRING));
				}
				// eviction
				compareEviction(sourceVersionOperator.getEviction(), new Eviction(), false, true, fieldsList);
				// header
				headerComparisionForFileChannelOutput(sourceVersionOperator.getOFCIHeader(), new OFCIHeader(), false,
						true, fieldsList);
				// footer
				footerComparisionForFileChannelOutput(sourceVersionOperator.getOFCIFooter(), new OFCIFooter(), false,
						true, fieldsList);
				// content
				contentComparisionForFileChannelOutput(sourceVersionOperator.getOFCIContent(), new OFCIContent(), false,
						true, fieldsList);

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

				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getOutputBeType(),
						targetVersionOperator.getOutputBeType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.BUSINESS_SETTINGS, BPConstant.OUTPUTBETYPE,
							sourceVersionOperator.getOutputBeType(), targetVersionOperator.getOutputBeType()));
				}

				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getContinuous(),
						targetVersionOperator.getContinuous())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OPERATORS, BPConstant.CONTINUOUS, sourceVersionOperator.getContinuous(),
							targetVersionOperator.getContinuous()));
				}

				if (sourceVersionOperator.isAutoGeneration() != targetVersionOperator.isAutoGeneration()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.OPERATORS, BPConstant.AUTO_GENERATION, sourceVersionOperator.isAutoGeneration(),
							targetVersionOperator.isAutoGeneration()));
				}

				if (sourceVersionOperator.isAutoGenerateMapping() != targetVersionOperator.isAutoGenerateMapping()) {
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OPERATORS,
									BPConstant.AUTO_GENERATE_MAPPING, sourceVersionOperator.isAutoGenerateMapping(),
									targetVersionOperator.isAutoGenerateMapping()));
				}

				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getOutputFileName().getStaticName(),
						targetVersionOperator.getOutputFileName().getStaticName())) {
					fieldsList.add(
							BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OUTPUTFILENAME,
									BPConstant.STATIC_NAME, sourceVersionOperator.getOutputFileName().getStaticName(),
									targetVersionOperator.getOutputFileName().getStaticName()));
				}

				if (!StringUtils.equalsIgnoreCase(sourceVersionOperator.getOutputFileName().getDynamicName(),
						targetVersionOperator.getOutputFileName().getDynamicName())) {
					fieldsList.add(
							BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.OUTPUTFILENAME,
									BPConstant.DYNAMIC_NAME, sourceVersionOperator.getOutputFileName().getDynamicName(),
									targetVersionOperator.getOutputFileName().getDynamicName()));
				}
				// eviction
				compareEviction(sourceVersionOperator.getEviction(), targetVersionOperator.getEviction(), false, false,
						fieldsList);
				// header
				headerComparisionForFileChannelOutput(sourceVersionOperator.getOFCIHeader(),
						targetVersionOperator.getOFCIHeader(), false, false, fieldsList);
				// footer
				footerComparisionForFileChannelOutput(sourceVersionOperator.getOFCIFooter(),
						targetVersionOperator.getOFCIFooter(), false, false, fieldsList);
				// content
				contentComparisionForFileChannelOutput(sourceVersionOperator.getOFCIContent(),
						targetVersionOperator.getOFCIContent(), false, false, fieldsList);

				BPDesignerServiceImpl.operatorPropertiesComparision(sourceVersionOperator.getProperties(),
						targetVersionOperator.getProperties(), fieldsList, false, false);

				BPDesignerServiceImpl.operatorCommentsCheck(sourceVersionOperator.getComments(),
						targetVersionOperator.getComments(), fieldsList, false, false);
			}
		} catch (Exception exception) {
			LOGGER.error("File Output channel Integration comparision error" + exception);
			throw new BPException(exception.getMessage());
		}
		return fieldsList;
	}

	/**
	 * This method will check for Eviction in file output operator changes.
	 */
	private static void compareEviction(Eviction sourceEviction, Eviction targetEviction, boolean isAdded,
			boolean isDeleted, List<Fields> fieldList) {
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetEviction.getEventBased())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.EVICTION,
							BPConstant.EVENT_BASED, BPConstant.EMPTY_STRING, targetEviction.getEventBased()));
				}

				if (!StringUtils.isEmpty(String.valueOf(targetEviction.getRecordCountBased()))) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.EVICTION,
							BPConstant.RECORD_COUNT_BASED, BPConstant.EMPTY_STRING,
							targetEviction.getRecordCountBased()));
				}

				if (!StringUtils.isEmpty(targetEviction.getTimeBased().getDuration())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.TIME_BASED, BPConstant.DURATION, BPConstant.EMPTY_STRING,
							targetEviction.getTimeBased().getDuration()));
				}

				if (!StringUtils.isEmpty(String.valueOf(targetEviction.getTimeBased().getCount()))) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.TIME_BASED, BPConstant.COUNT, BPConstant.EMPTY_STRING,
							targetEviction.getTimeBased().getCount()));
				}

				if (!StringUtils.isEmpty(targetEviction.getIdleTime().getDuration())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.IDLE_TIME,
							BPConstant.DURATION, BPConstant.EMPTY_STRING, targetEviction.getIdleTime().getDuration()));
				}

				if (!StringUtils.isEmpty(String.valueOf(targetEviction.getIdleTime().getCount()))) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.IDLE_TIME,
							BPConstant.COUNT, BPConstant.EMPTY_STRING, targetEviction.getIdleTime().getCount()));
				}

			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceEviction.getEventBased())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.EVICTION,
							BPConstant.EVENT_BASED, sourceEviction.getEventBased(), BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(String.valueOf(sourceEviction.getRecordCountBased()))) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.EVICTION,
							BPConstant.RECORD_COUNT_BASED, sourceEviction.getRecordCountBased(),
							BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(sourceEviction.getTimeBased().getDuration())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.TIME_BASED, BPConstant.DURATION, sourceEviction.getTimeBased().getDuration(),
							BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(String.valueOf(sourceEviction.getTimeBased().getCount()))) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.TIME_BASED, BPConstant.COUNT, sourceEviction.getTimeBased().getCount(),
							BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(sourceEviction.getIdleTime().getDuration())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.IDLE_TIME,
							BPConstant.DURATION, sourceEviction.getIdleTime().getDuration(), BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(String.valueOf(sourceEviction.getIdleTime().getCount()))) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.IDLE_TIME,
							BPConstant.COUNT, sourceEviction.getIdleTime().getCount(), BPConstant.EMPTY_STRING));
				}

			} else {
				if (!StringUtils.equalsIgnoreCase(targetEviction.getEventBased(), sourceEviction.getEventBased())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.EVICTION,
							BPConstant.EVENT_BASED, sourceEviction.getEventBased(), targetEviction.getEventBased()));
				}
				if (targetEviction.getRecordCountBased() != sourceEviction.getRecordCountBased()) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.EVICTION,
							BPConstant.RECORD_COUNT_BASED, sourceEviction.getRecordCountBased(),
							targetEviction.getRecordCountBased()));
				}
				if (!StringUtils.equalsIgnoreCase(targetEviction.getTimeBased().getDuration(),
						sourceEviction.getTimeBased().getDuration())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.TIME_BASED, BPConstant.DURATION, sourceEviction.getTimeBased().getDuration(),
							targetEviction.getTimeBased().getDuration()));
				}
				if (targetEviction.getTimeBased().getCount() != sourceEviction.getTimeBased().getCount()) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.TIME_BASED, BPConstant.COUNT, sourceEviction.getTimeBased().getCount(),
							targetEviction.getTimeBased().getCount()));
				}
				if (!StringUtils.equalsIgnoreCase(targetEviction.getIdleTime().getDuration(),
						sourceEviction.getIdleTime().getDuration())) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.IDLE_TIME,
							BPConstant.DURATION, sourceEviction.getIdleTime().getDuration(),
							targetEviction.getIdleTime().getDuration()));
				}
				if (targetEviction.getIdleTime().getCount() != sourceEviction.getIdleTime().getCount()) {
					fieldList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.IDLE_TIME,
							BPConstant.COUNT, sourceEviction.getIdleTime().getCount(),
							targetEviction.getIdleTime().getCount()));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("File Output channel Integration Eviction comparision error" + exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check for Header in file output operator changes.
	 */
	private static void headerComparisionForFileChannelOutput(OFCIHeader sourceHeader, OFCIHeader targetHeader,
			boolean isAdded, boolean isDeleted, List<Fields> fieldsList) {
		try {
			if (isAdded) {
				// COMMON HEADER
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
						BPConstant.HAS_HEADER, BPConstant.EMPTY_STRING, targetHeader.isHasHeader()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
						BPConstant.HEADER_LINES, BPConstant.EMPTY_STRING, targetHeader.getHeaderLines()));
				// FIXED WIDTH
				compareFixedWidth(null, targetHeader.getFixedWidth(), isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(targetHeader.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, BPConstant.EMPTY_STRING,
							targetHeader.getDelimited().getRecord()));
				}
				if (!StringUtils.isBlank(targetHeader.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER, BPConstant.EMPTY_STRING,
							targetHeader.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(null, targetHeader.getDelimited().getAttributes(), isAdded, isDeleted,
						fieldsList);
			} else if (isDeleted) {
				// COMMON HEADER
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
						BPConstant.HAS_HEADER, sourceHeader.isHasHeader(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
						BPConstant.HEADER_LINES, sourceHeader.getHeaderLines(), BPConstant.EMPTY_STRING));
				// FIXED WIDTH
				compareFixedWidth(sourceHeader.getFixedWidth(), null, isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(sourceHeader.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceHeader.getDelimited().getRecord(),
							BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isBlank(sourceHeader.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceHeader.getDelimited().getAttribute(), BPConstant.EMPTY_STRING));
				}
				compareDelimittedAttribute(sourceHeader.getDelimited().getAttributes(), null, isAdded, isDeleted,
						fieldsList);
			} else {
				// COMMON HEADER
				if (sourceHeader.isHasHeader() != targetHeader.isHasHeader()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
							BPConstant.HAS_HEADER, sourceHeader.isHasHeader(), targetHeader.isHasHeader()));
				}
				if (sourceHeader.getHeaderLines() != targetHeader.getHeaderLines()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.HEADER,
							BPConstant.HEADER_LINES, sourceHeader.getHeaderLines(), targetHeader.getHeaderLines()));
				}
				// FIXED WIDTH
				compareFixedWidth(sourceHeader.getFixedWidth(), targetHeader.getFixedWidth(), isAdded, isDeleted,
						fieldsList);
				// DELIMTTED
				if (!StringUtils.equalsIgnoreCase(sourceHeader.getDelimited().getRecord(),
						targetHeader.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceHeader.getDelimited().getRecord(),
							targetHeader.getDelimited().getRecord()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceHeader.getDelimited().getAttribute(),
						targetHeader.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceHeader.getDelimited().getAttribute(), targetHeader.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(sourceHeader.getDelimited().getAttributes(),
						targetHeader.getDelimited().getAttributes(), isAdded, isDeleted, fieldsList);
			}
		} catch (Exception exception) {
			LOGGER.error("File input channel Integration Header comparision error" + exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check for Footer in file output operator changes.
	 */
	private static void footerComparisionForFileChannelOutput(OFCIFooter sourceFooter, OFCIFooter targetFooter,
			boolean isAdded, boolean isDeleted, List<Fields> fieldsList) {
		try {
			if (isAdded) {
				// COMMON FOOTER
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
						BPConstant.HAS_FOOTER, BPConstant.EMPTY_STRING, targetFooter.isHasFooter()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
						BPConstant.FOOTER_LINES, BPConstant.EMPTY_STRING, targetFooter.getFooterLines()));
				// FIXED WIDTH
				compareFixedWidth(null, targetFooter.getFixedWidth(), isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(targetFooter.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, BPConstant.EMPTY_STRING,
							targetFooter.getDelimited().getRecord()));
				}
				if (!StringUtils.isBlank(targetFooter.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER, BPConstant.EMPTY_STRING,
							targetFooter.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(null, targetFooter.getDelimited().getAttributes(), isAdded, isDeleted,
						fieldsList);
			} else if (isDeleted) {
				// COMMON FOOTER
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
						BPConstant.HAS_FOOTER, sourceFooter.isHasFooter(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
						BPConstant.FOOTER_LINES, sourceFooter.getFooterLines(), BPConstant.EMPTY_STRING));
				// FIXED WIDTH
				compareFixedWidth(sourceFooter.getFixedWidth(), null, isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(sourceFooter.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceFooter.getDelimited().getRecord(),
							BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isBlank(sourceFooter.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceFooter.getDelimited().getAttribute(), BPConstant.EMPTY_STRING));
				}
				compareDelimittedAttribute(sourceFooter.getDelimited().getAttributes(), null, isAdded, isDeleted,
						fieldsList);
			} else {
				// COMMON FOOTER
				if (sourceFooter.isHasFooter() != targetFooter.isHasFooter()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
							BPConstant.HAS_FOOTER, sourceFooter.isHasFooter(), targetFooter.isHasFooter()));
				}
				if (sourceFooter.getFooterLines() != targetFooter.getFooterLines()) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.FOOTER,
							BPConstant.FOOTER_LINES, sourceFooter.getFooterLines(), targetFooter.getFooterLines()));
				}
				// FIXED WIDTH
				compareFixedWidth(sourceFooter.getFixedWidth(), targetFooter.getFixedWidth(), isAdded, isDeleted,
						fieldsList);
				// DELIMTTED
				if (!StringUtils.equalsIgnoreCase(sourceFooter.getDelimited().getRecord(),
						targetFooter.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceFooter.getDelimited().getRecord(),
							targetFooter.getDelimited().getRecord()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceFooter.getDelimited().getAttribute(),
						targetFooter.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceFooter.getDelimited().getAttribute(), targetFooter.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(sourceFooter.getDelimited().getAttributes(),
						targetFooter.getDelimited().getAttributes(), isAdded, isDeleted, fieldsList);
			}
		} catch (Exception exception) {
			LOGGER.error("File input channel Integration Footer comparision error" + exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will check for Content in file output operator changes.
	 */
	private static void contentComparisionForFileChannelOutput(OFCIContent sourceContent, OFCIContent targetContent,
			boolean isAdded, boolean isDeleted, List<Fields> fieldsList) {
		try {
			if (isAdded) {
				// FIXED WIDTH
				compareFixedWidth(null, targetContent.getFixedWidth(), isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(targetContent.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, BPConstant.EMPTY_STRING,
							targetContent.getDelimited().getRecord()));
				}
				if (!StringUtils.isBlank(targetContent.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER, BPConstant.EMPTY_STRING,
							targetContent.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(null, targetContent.getDelimited().getAttributes(), isAdded, isDeleted,
						fieldsList);
			} else if (isDeleted) {
				// FIXED WIDTH
				compareFixedWidth(sourceContent.getFixedWidth(), null, isAdded, isDeleted, fieldsList);
				// DELIMTTED
				if (!StringUtils.isBlank(sourceContent.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceContent.getDelimited().getRecord(),
							BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isBlank(sourceContent.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceContent.getDelimited().getAttribute(), BPConstant.EMPTY_STRING));
				}
				compareDelimittedAttribute(sourceContent.getDelimited().getAttributes(), null, isAdded, isDeleted,
						fieldsList);
			} else {
				// FIXED WIDTH
				compareFixedWidth(sourceContent.getFixedWidth(), targetContent.getFixedWidth(), isAdded, isDeleted,
						fieldsList);
				// DELIMTTED
				if (!StringUtils.equalsIgnoreCase(sourceContent.getDelimited().getRecord(),
						targetContent.getDelimited().getRecord())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.RECORD_DELIMITER, sourceContent.getDelimited().getRecord(),
							targetContent.getDelimited().getRecord()));
				}
				if (!StringUtils.equalsIgnoreCase(sourceContent.getDelimited().getAttribute(),
						targetContent.getDelimited().getAttribute())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(BPConstant.EMPTY_STRING,
							BPConstant.DELIMITED, BPConstant.ATTRIBUTE_DELIMTER,
							sourceContent.getDelimited().getAttribute(), targetContent.getDelimited().getAttribute()));
				}
				compareDelimittedAttribute(sourceContent.getDelimited().getAttributes(),
						targetContent.getDelimited().getAttributes(), isAdded, isDeleted, fieldsList);
			}
		} catch (Exception exception) {
			LOGGER.error("File input channel Integration Content comparision error" + exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * Generic method to compare the fixedWidth values
	 * 
	 * @param sourceFixedWidthList
	 * @param targetFixedWidthList
	 * @param isAdded
	 * @param isDeleted
	 * @param fieldsList
	 */
	private static void compareFixedWidth(List<FixedWidth> sourceFixedWidthList, List<FixedWidth> targetFixedWidthList,
			boolean isAdded, boolean isDeleted, List<Fields> fieldsList) {
		if (isAdded) {
			for (FixedWidth fixedWidth : targetFixedWidthList) {
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
						BPConstant.ATTRIBUTE_NAME, BPConstant.EMPTY_STRING, fixedWidth.getAttributeName()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
						BPConstant.STARTING_POSITION, BPConstant.EMPTY_STRING, fixedWidth.getStartingPoint()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
						BPConstant.WIDTH, BPConstant.EMPTY_STRING, fixedWidth.getWidth()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
						BPConstant.LINE_NUMBER, BPConstant.EMPTY_STRING, fixedWidth.getLineNumber()));
				if (!StringUtils.isEmpty(fixedWidth.getType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
							BPConstant.TYPE, BPConstant.EMPTY_STRING, fixedWidth.getType()));
				}

				if (!StringUtils.isEmpty(fixedWidth.getValue())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
							BPConstant.VALUE, BPConstant.EMPTY_STRING, fixedWidth.getValue()));
				}
				if (!StringUtils.isEmpty(fixedWidth.getDataType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
							BPConstant.DATA_TYPE, BPConstant.EMPTY_STRING, fixedWidth.getDataType()));

					switch (fixedWidth.getDataType()) {
					case BPConstant.NUMBER:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.PRECISION_SMALL, BPConstant.EMPTY_STRING,
								fixedWidth.getFormat().getPrecision()));
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.SCALE_SMALL, BPConstant.EMPTY_STRING,
								fixedWidth.getFormat().getScale()));
						break;
					case BPConstant.BOOLEAN:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.TRUE_VALUE, BPConstant.EMPTY_STRING,
								fixedWidth.getFormat().getTrueValue()));
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.FALSE_VALUE, BPConstant.EMPTY_STRING,
								fixedWidth.getFormat().getFalseValue()));
						break;
					case BPConstant.DATE_TIME:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.DATE_TIME_SMALL, BPConstant.EMPTY_STRING,
								fixedWidth.getFormat().getDateTime()));
						break;
					default:
						break;
					}
				}
			}
		} else if (isDeleted) {
			for (FixedWidth fixedWidth : sourceFixedWidthList) {
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
						BPConstant.ATTRIBUTE_NAME, fixedWidth.getAttributeName(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
						BPConstant.STARTING_POSITION, fixedWidth.getStartingPoint(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
						BPConstant.WIDTH, fixedWidth.getWidth(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
						BPConstant.LINE_NUMBER, fixedWidth.getLineNumber(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(fixedWidth.getType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
							BPConstant.TYPE, fixedWidth.getType(), BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(fixedWidth.getValue())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
							BPConstant.VALUE, fixedWidth.getValue(), BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isEmpty(fixedWidth.getDataType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(), BPConstant.FIXED_WIDTH,
							BPConstant.DATA_TYPE, fixedWidth.getDataType(), BPConstant.EMPTY_STRING));

					switch (fixedWidth.getDataType()) {
					case BPConstant.NUMBER:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.PRECISION_SMALL,
								fixedWidth.getFormat().getPrecision(), BPConstant.EMPTY_STRING));
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.SCALE_SMALL, fixedWidth.getFormat().getScale(),
								BPConstant.EMPTY_STRING));
						break;
					case BPConstant.BOOLEAN:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.TRUE_VALUE, fixedWidth.getFormat().getTrueValue(),
								BPConstant.EMPTY_STRING));
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.FALSE_VALUE, fixedWidth.getFormat().getFalseValue(),
								BPConstant.EMPTY_STRING));
						break;
					case BPConstant.DATE_TIME:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(fixedWidth.getKey(),
								BPConstant.FIXED_WIDTH, BPConstant.DATE_TIME_SMALL,
								fixedWidth.getFormat().getDateTime(), BPConstant.EMPTY_STRING));
						break;
					default:
						break;
					}
				}
			}
		} else {
			// CASE1.IF BOTH LHS&RHS CONTAINS THE OBJECT
			for (FixedWidth lhsFixedWidth : sourceFixedWidthList) {
				for (FixedWidth rhsFixedWidth : targetFixedWidthList) {
					if (StringUtils.equalsIgnoreCase(lhsFixedWidth.getKey(), rhsFixedWidth.getKey())) {
						if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getAttributeName(),
								rhsFixedWidth.getAttributeName())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.ATTRIBUTE_NAME, lhsFixedWidth.getAttributeName(),
									rhsFixedWidth.getAttributeName()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getStartingPoint(),
								rhsFixedWidth.getStartingPoint())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.STARTING_POSITION,
									lhsFixedWidth.getStartingPoint(), rhsFixedWidth.getStartingPoint()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getWidth(), rhsFixedWidth.getWidth())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.WIDTH, lhsFixedWidth.getWidth(),
									rhsFixedWidth.getWidth()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getLineNumber(),
								rhsFixedWidth.getLineNumber())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.LINE_NUMBER, lhsFixedWidth.getLineNumber(),
									rhsFixedWidth.getLineNumber()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getDataType(), rhsFixedWidth.getDataType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.DATA_TYPE, lhsFixedWidth.getDataType(),
									rhsFixedWidth.getDataType()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getType(), rhsFixedWidth.getType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.TYPE, lhsFixedWidth.getType(),
									rhsFixedWidth.getType()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getValue(), rhsFixedWidth.getValue())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.VALUE, lhsFixedWidth.getValue(),
									rhsFixedWidth.getValue()));
						}
						if (!StringUtils.isEmpty(rhsFixedWidth.getDataType())) {
							switch (rhsFixedWidth.getDataType()) {
							case BPConstant.NUMBER:
								if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getFormat().getPrecision(),
										rhsFixedWidth.getFormat().getPrecision())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
											BPConstant.FIXED_WIDTH, BPConstant.PRECISION_SMALL,
											lhsFixedWidth.getFormat().getPrecision(),
											rhsFixedWidth.getFormat().getPrecision()));
								}
								if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getFormat().getScale(),
										rhsFixedWidth.getFormat().getScale())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
											BPConstant.FIXED_WIDTH, BPConstant.SCALE_SMALL,
											lhsFixedWidth.getFormat().getScale(),
											rhsFixedWidth.getFormat().getScale()));
								}
								break;
							case BPConstant.BOOLEAN:
								if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getFormat().getTrueValue(),
										rhsFixedWidth.getFormat().getTrueValue())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
											BPConstant.FIXED_WIDTH, BPConstant.TRUE_VALUE,
											lhsFixedWidth.getFormat().getTrueValue(),
											rhsFixedWidth.getFormat().getTrueValue()));
								}
								if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getFormat().getFalseValue(),
										rhsFixedWidth.getFormat().getFalseValue())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
											BPConstant.FIXED_WIDTH, BPConstant.FALSE_VALUE,
											lhsFixedWidth.getFormat().getFalseValue(),
											rhsFixedWidth.getFormat().getFalseValue()));
								}
								break;
							case BPConstant.DATE_TIME:
								if (!StringUtils.equalsIgnoreCase(lhsFixedWidth.getFormat().getDateTime(),
										rhsFixedWidth.getFormat().getDateTime())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
											BPConstant.FIXED_WIDTH, BPConstant.DATE_TIME_SMALL,
											lhsFixedWidth.getFormat().getDateTime(),
											rhsFixedWidth.getFormat().getDateTime()));
								}
								break;
							default:
								break;
							}
						}
					}
				}
			}
			// CASE2.IF LHS ONLY CONTAINS THE OBJECT
			for (FixedWidth lhsFixedWidth : sourceFixedWidthList) {
				boolean isKeyAvailable = false;
				for (FixedWidth rhsFixedWidth : targetFixedWidthList) {
					if (StringUtils.equalsIgnoreCase(lhsFixedWidth.getKey(), rhsFixedWidth.getKey())) {
						isKeyAvailable = true;
						break;
					}
				}
				if (!isKeyAvailable) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
							BPConstant.FIXED_WIDTH, BPConstant.ATTRIBUTE_NAME, lhsFixedWidth.getAttributeName(),
							BPConstant.EMPTY_STRING));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
							BPConstant.FIXED_WIDTH, BPConstant.STARTING_POSITION, lhsFixedWidth.getStartingPoint(),
							BPConstant.EMPTY_STRING));
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
									BPConstant.WIDTH, lhsFixedWidth.getWidth(), BPConstant.EMPTY_STRING));
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
									BPConstant.LINE_NUMBER, lhsFixedWidth.getLineNumber(), BPConstant.EMPTY_STRING));
					if (!StringUtils.isEmpty(lhsFixedWidth.getType())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
										BPConstant.TYPE, lhsFixedWidth.getType(), BPConstant.EMPTY_STRING));
					}

					if (!StringUtils.isEmpty(lhsFixedWidth.getValue())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
										BPConstant.VALUE, lhsFixedWidth.getValue(), BPConstant.EMPTY_STRING));
					}
					if (!StringUtils.isEmpty(lhsFixedWidth.getDataType())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
										BPConstant.DATA_TYPE, lhsFixedWidth.getDataType(), BPConstant.EMPTY_STRING));

						switch (lhsFixedWidth.getDataType()) {
						case BPConstant.NUMBER:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.PRECISION_SMALL,
									lhsFixedWidth.getFormat().getPrecision(), BPConstant.EMPTY_STRING));
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.SCALE_SMALL,
									lhsFixedWidth.getFormat().getScale(), BPConstant.EMPTY_STRING));
							break;
						case BPConstant.BOOLEAN:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.TRUE_VALUE,
									lhsFixedWidth.getFormat().getTrueValue(), BPConstant.EMPTY_STRING));
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.FALSE_VALUE,
									lhsFixedWidth.getFormat().getFalseValue(), BPConstant.EMPTY_STRING));
							break;
						case BPConstant.DATE_TIME:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.DATE_TIME_SMALL,
									lhsFixedWidth.getFormat().getDateTime(), BPConstant.EMPTY_STRING));
							break;
						default:
							break;
						}
					}
				}
			}
			// CASE3.IF RHS ONLY CONTAINS THE OBJECT
			for (FixedWidth rhsFixedWidth : targetFixedWidthList) {
				boolean isKeyAvailable = false;
				for (FixedWidth lhsFixedWidth : sourceFixedWidthList) {
					if (StringUtils.equalsIgnoreCase(lhsFixedWidth.getKey(), rhsFixedWidth.getKey())) {
						isKeyAvailable = true;
						break;
					}
				}
				if (!isKeyAvailable) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(),
							BPConstant.FIXED_WIDTH, BPConstant.ATTRIBUTE_NAME, BPConstant.EMPTY_STRING,
							rhsFixedWidth.getAttributeName()));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(),
							BPConstant.FIXED_WIDTH, BPConstant.STARTING_POSITION, BPConstant.EMPTY_STRING,
							rhsFixedWidth.getStartingPoint()));
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
									BPConstant.WIDTH, BPConstant.EMPTY_STRING, rhsFixedWidth.getWidth()));
					fieldsList
							.add(BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
									BPConstant.LINE_NUMBER, BPConstant.EMPTY_STRING, rhsFixedWidth.getLineNumber()));
					if (!StringUtils.isEmpty(rhsFixedWidth.getType())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
										BPConstant.TYPE, BPConstant.EMPTY_STRING, rhsFixedWidth.getType()));
					}

					if (!StringUtils.isEmpty(rhsFixedWidth.getValue())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
										BPConstant.VALUE, BPConstant.EMPTY_STRING, rhsFixedWidth.getValue()));
					}
					if (!StringUtils.isEmpty(rhsFixedWidth.getDataType())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(), BPConstant.FIXED_WIDTH,
										BPConstant.DATA_TYPE, BPConstant.EMPTY_STRING, rhsFixedWidth.getDataType()));

						switch (rhsFixedWidth.getDataType()) {
						case BPConstant.NUMBER:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.PRECISION_SMALL, BPConstant.EMPTY_STRING,
									rhsFixedWidth.getFormat().getPrecision()));
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.SCALE_SMALL, BPConstant.EMPTY_STRING,
									rhsFixedWidth.getFormat().getScale()));
							break;
						case BPConstant.BOOLEAN:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.TRUE_VALUE, BPConstant.EMPTY_STRING,
									rhsFixedWidth.getFormat().getTrueValue()));
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.FALSE_VALUE, BPConstant.EMPTY_STRING,
									rhsFixedWidth.getFormat().getFalseValue()));
							break;
						case BPConstant.DATE_TIME:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsFixedWidth.getKey(),
									BPConstant.FIXED_WIDTH, BPConstant.DATE_TIME_SMALL, BPConstant.EMPTY_STRING,
									rhsFixedWidth.getFormat().getDateTime()));
							break;
						default:
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Generic method to compare the delimitted values
	 * 
	 * @param sourceAttributesList
	 * @param targetAttributesList
	 * @param isAdded
	 * @param isDeleted
	 * @param fieldsList
	 */
	private static void compareDelimittedAttribute(List<Attribute> sourceAttributesList,
			List<Attribute> targetAttributesList, boolean isAdded, boolean isDeleted, List<Fields> fieldsList) {
		if (isAdded) {
			for (Attribute attributes : targetAttributesList) {
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.ATTRIBUTE_NAME, BPConstant.EMPTY_STRING, attributes.getAttributeName()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.SEGMENT_POSITION, BPConstant.EMPTY_STRING, attributes.getSegmentPosition()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.BUK, BPConstant.EMPTY_STRING, attributes.isBuk()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.LINE_NUMBER, BPConstant.EMPTY_STRING, attributes.getLineNumber()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.DATA_TYPE, BPConstant.EMPTY_STRING, attributes.getDataType()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.PARENT, BPConstant.EMPTY_STRING, attributes.getParent()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.PARENT_NODE, BPConstant.EMPTY_STRING, attributes.getParentNode()));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.CURRENT_NODE, BPConstant.EMPTY_STRING, attributes.getCurrentNode()));
				if (!StringUtils.isEmpty(String.valueOf(attributes.isDisabled()))) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
							BPConstant.DISABLED, BPConstant.EMPTY_STRING, attributes.isDisabled()));
				}

				if (!StringUtils.isEmpty(attributes.getType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
							BPConstant.TYPE, BPConstant.EMPTY_STRING, attributes.getType()));
				}

				if (!StringUtils.isEmpty(attributes.getValue())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
							BPConstant.VALUE, BPConstant.EMPTY_STRING, attributes.getValue()));
				}
				if (!StringUtils.isEmpty(attributes.getDataType())) {
					switch (attributes.getDataType()) {
					case BPConstant.NUMBER:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.PRECISION_SMALL, BPConstant.EMPTY_STRING,
								attributes.getFormat().getPrecision()));
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.SCALE_SMALL, BPConstant.EMPTY_STRING,
								attributes.getFormat().getScale()));
						break;
					case BPConstant.BOOLEAN:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.TRUE_VALUE, BPConstant.EMPTY_STRING,
								attributes.getFormat().getTrueValue()));
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.FALSE_VALUE, BPConstant.EMPTY_STRING,
								attributes.getFormat().getFalseValue()));
						break;
					case BPConstant.DATE_TIME:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.DATE_TIME_SMALL, BPConstant.EMPTY_STRING,
								attributes.getFormat().getDateTime()));
						break;
					default:
						break;
					}
				}
			}
		} else if (isDeleted) {
			for (Attribute attributes : sourceAttributesList) {
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.ATTRIBUTE_NAME, attributes.getAttributeName(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.SEGMENT_POSITION, attributes.getSegmentPosition(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.BUK, attributes.isBuk(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.LINE_NUMBER, attributes.getLineNumber(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.DATA_TYPE, attributes.getDataType(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.PARENT, attributes.getParent(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.PARENT_NODE, attributes.getParentNode(), BPConstant.EMPTY_STRING));
				fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
						BPConstant.CURRENT_NODE, attributes.getCurrentNode(), BPConstant.EMPTY_STRING));
				if (!StringUtils.isEmpty(String.valueOf(attributes.isDisabled()))) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
							BPConstant.DISABLED, attributes.isDisabled(), BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(attributes.getType())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
							BPConstant.TYPE, attributes.getType(), BPConstant.EMPTY_STRING));
				}

				if (!StringUtils.isEmpty(attributes.getValue())) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(), BPConstant.DELIMITED,
							BPConstant.VALUE, attributes.getValue(), BPConstant.EMPTY_STRING));
				}
				if (!StringUtils.isEmpty(attributes.getDataType())) {
					switch (attributes.getDataType()) {
					case BPConstant.NUMBER:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.PRECISION_SMALL, attributes.getFormat().getPrecision(),
								BPConstant.EMPTY_STRING));
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.SCALE_SMALL, attributes.getFormat().getScale(),
								BPConstant.EMPTY_STRING));
						break;
					case BPConstant.BOOLEAN:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.TRUE_VALUE, attributes.getFormat().getTrueValue(),
								BPConstant.EMPTY_STRING));
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.FALSE_VALUE, attributes.getFormat().getFalseValue(),
								BPConstant.EMPTY_STRING));
						break;
					case BPConstant.DATE_TIME:
						fieldsList.add(BPDesignerServiceImpl.buildResponseJson(attributes.getKey(),
								BPConstant.DELIMITED, BPConstant.DATE_TIME_SMALL, attributes.getFormat().getDateTime(),
								BPConstant.EMPTY_STRING));
						break;
					default:
						break;
					}
				}
			}
		} else {
			// CASE1.IF BOTH LHS&RHS CONTAINS THE OBJECT
			for (Attribute lhsAttributes : sourceAttributesList) {
				for (Attribute rhsAttributes : targetAttributesList) {
					if (StringUtils.equalsIgnoreCase(lhsAttributes.getKey(), rhsAttributes.getKey())) {
						if (!StringUtils.equalsIgnoreCase(lhsAttributes.getAttributeName(),
								rhsAttributes.getAttributeName())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.ATTRIBUTE_NAME, lhsAttributes.getAttributeName(),
									rhsAttributes.getAttributeName()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsAttributes.getSegmentPosition(),
								rhsAttributes.getSegmentPosition())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.SEGMENT_POSITION,
									lhsAttributes.getSegmentPosition(), rhsAttributes.getSegmentPosition()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsAttributes.isBuk(), rhsAttributes.isBuk())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.BUK, lhsAttributes.isBuk(),
									rhsAttributes.isBuk()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsAttributes.getLineNumber(),
								rhsAttributes.getLineNumber())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.LINE_NUMBER, lhsAttributes.getLineNumber(),
									rhsAttributes.getLineNumber()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsAttributes.getDataType(), rhsAttributes.getDataType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.DATA_TYPE, lhsAttributes.getDataType(),
									rhsAttributes.getDataType()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsAttributes.getParent(), rhsAttributes.getParent())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.PARENT, lhsAttributes.getParent(),
									rhsAttributes.getParent()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsAttributes.getParentNode(),
								rhsAttributes.getParentNode())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.PARENT_NODE, lhsAttributes.getParentNode(),
									rhsAttributes.getParentNode()));
						}
						if (!StringUtils.equalsIgnoreCase(lhsAttributes.getCurrentNode(),
								rhsAttributes.getCurrentNode())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.CURRENT_NODE, lhsAttributes.getCurrentNode(),
									rhsAttributes.getCurrentNode()));
						}
						if (rhsAttributes.isDisabled() != lhsAttributes.isDisabled()) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.DISABLED, lhsAttributes.isDisabled(),
									rhsAttributes.isDisabled()));
						}

						if (!StringUtils.equalsIgnoreCase(rhsAttributes.getType(), lhsAttributes.getType())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.TYPE, lhsAttributes.getType(),
									rhsAttributes.getType()));
						}

						if (!StringUtils.equalsIgnoreCase(rhsAttributes.getValue(), lhsAttributes.getValue())) {
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.VALUE, lhsAttributes.getValue(),
									rhsAttributes.getValue()));
						}
						if (!StringUtils.isEmpty(rhsAttributes.getDataType())) {
							switch (rhsAttributes.getDataType()) {
							case BPConstant.NUMBER:
								if (!StringUtils.equalsIgnoreCase(lhsAttributes.getFormat().getPrecision(),
										rhsAttributes.getFormat().getPrecision())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
											BPConstant.DELIMITED, BPConstant.PRECISION_SMALL,
											lhsAttributes.getFormat().getPrecision(),
											rhsAttributes.getFormat().getPrecision()));
								}
								if (!StringUtils.equalsIgnoreCase(lhsAttributes.getFormat().getScale(),
										rhsAttributes.getFormat().getScale())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
											BPConstant.DELIMITED, BPConstant.SCALE_SMALL,
											lhsAttributes.getFormat().getScale(),
											rhsAttributes.getFormat().getScale()));
								}
								break;
							case BPConstant.BOOLEAN:
								if (!StringUtils.equalsIgnoreCase(lhsAttributes.getFormat().getTrueValue(),
										rhsAttributes.getFormat().getTrueValue())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
											BPConstant.DELIMITED, BPConstant.TRUE_VALUE,
											lhsAttributes.getFormat().getTrueValue(),
											rhsAttributes.getFormat().getTrueValue()));
								}
								if (!StringUtils.equalsIgnoreCase(lhsAttributes.getFormat().getFalseValue(),
										rhsAttributes.getFormat().getFalseValue())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
											BPConstant.DELIMITED, BPConstant.FALSE_VALUE,
											lhsAttributes.getFormat().getFalseValue(),
											rhsAttributes.getFormat().getFalseValue()));
								}
								break;
							case BPConstant.DATE_TIME:
								if (!StringUtils.equalsIgnoreCase(lhsAttributes.getFormat().getDateTime(),
										rhsAttributes.getFormat().getDateTime())) {
									fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
											BPConstant.DELIMITED, BPConstant.DATE_TIME_SMALL,
											lhsAttributes.getFormat().getDateTime(),
											rhsAttributes.getFormat().getDateTime()));
								}
								break;
							default:
								break;
							}
						}
					}
				}
			}
			// CASE2.IF LHS ONLY CONTAINS THE OBJECT
			for (Attribute lhsAttributes : sourceAttributesList) {
				boolean isKeyAvailable = false;
				for (Attribute rhsAttributes : targetAttributesList) {
					if (StringUtils.equalsIgnoreCase(lhsAttributes.getKey(), rhsAttributes.getKey())) {
						isKeyAvailable = true;
						break;
					}
				}
				if (!isKeyAvailable) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.ATTRIBUTE_NAME, lhsAttributes.getAttributeName(), BPConstant.EMPTY_STRING));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.SEGMENT_POSITION, lhsAttributes.getSegmentPosition(), BPConstant.EMPTY_STRING));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.BUK, lhsAttributes.isBuk(), BPConstant.EMPTY_STRING));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.LINE_NUMBER, lhsAttributes.getLineNumber(), BPConstant.EMPTY_STRING));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.DATA_TYPE, lhsAttributes.getDataType(), BPConstant.EMPTY_STRING));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.PARENT, lhsAttributes.getParent(), BPConstant.EMPTY_STRING));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.PARENT_NODE, lhsAttributes.getParentNode(), BPConstant.EMPTY_STRING));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.CURRENT_NODE, lhsAttributes.getCurrentNode(), BPConstant.EMPTY_STRING));
					if (!StringUtils.isEmpty(String.valueOf(lhsAttributes.isDisabled()))) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
										BPConstant.DISABLED, BPConstant.EMPTY_STRING, lhsAttributes.isDisabled()));
					}

					if (!StringUtils.isEmpty(lhsAttributes.getType())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
										BPConstant.TYPE, BPConstant.EMPTY_STRING, lhsAttributes.getType()));
					}

					if (!StringUtils.isEmpty(lhsAttributes.getValue())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(), BPConstant.DELIMITED,
										BPConstant.VALUE, BPConstant.EMPTY_STRING, lhsAttributes.getValue()));
					}
					if (!StringUtils.isEmpty(lhsAttributes.getDataType())) {
						switch (lhsAttributes.getDataType()) {
						case BPConstant.NUMBER:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.PRECISION_SMALL,
									lhsAttributes.getFormat().getPrecision(), BPConstant.EMPTY_STRING));
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.SCALE_SMALL, lhsAttributes.getFormat().getScale(),
									BPConstant.EMPTY_STRING));
							break;
						case BPConstant.BOOLEAN:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.TRUE_VALUE,
									lhsAttributes.getFormat().getTrueValue(), BPConstant.EMPTY_STRING));
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.FALSE_VALUE,
									lhsAttributes.getFormat().getFalseValue(), BPConstant.EMPTY_STRING));
							break;
						case BPConstant.DATE_TIME:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(lhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.DATE_TIME_SMALL,
									lhsAttributes.getFormat().getDateTime(), BPConstant.EMPTY_STRING));
							break;
						default:
							break;
						}
					}
				}
			}
			// CASE3.IF RHS ONLY CONTAINS THE OBJECT
			for (Attribute rhsAttributes : targetAttributesList) {
				boolean isKeyAvailable = false;
				for (Attribute lhsAttributes : sourceAttributesList) {
					if (StringUtils.equalsIgnoreCase(lhsAttributes.getKey(), rhsAttributes.getKey())) {
						isKeyAvailable = true;
						break;
					}
				}
				if (!isKeyAvailable) {
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.ATTRIBUTE_NAME, BPConstant.EMPTY_STRING, rhsAttributes.getAttributeName()));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.SEGMENT_POSITION, BPConstant.EMPTY_STRING, rhsAttributes.getSegmentPosition()));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.BUK, BPConstant.EMPTY_STRING, rhsAttributes.isBuk()));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.LINE_NUMBER, BPConstant.EMPTY_STRING, rhsAttributes.getLineNumber()));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.DATA_TYPE, BPConstant.EMPTY_STRING, rhsAttributes.getDataType()));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.PARENT, BPConstant.EMPTY_STRING, rhsAttributes.getParent()));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.PARENT_NODE, BPConstant.EMPTY_STRING, rhsAttributes.getParentNode()));
					fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
							BPConstant.CURRENT_NODE, BPConstant.EMPTY_STRING, rhsAttributes.getCurrentNode()));
					if (!StringUtils.isEmpty(String.valueOf(rhsAttributes.isDisabled()))) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
										BPConstant.DISABLED, BPConstant.EMPTY_STRING, rhsAttributes.isDisabled()));
					}

					if (!StringUtils.isEmpty(rhsAttributes.getType())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
										BPConstant.TYPE, BPConstant.EMPTY_STRING, rhsAttributes.getType()));
					}

					if (!StringUtils.isEmpty(rhsAttributes.getValue())) {
						fieldsList.add(
								BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(), BPConstant.DELIMITED,
										BPConstant.VALUE, BPConstant.EMPTY_STRING, rhsAttributes.getValue()));
					}
					if (!StringUtils.isEmpty(rhsAttributes.getDataType())) {
						switch (rhsAttributes.getDataType()) {
						case BPConstant.NUMBER:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.PRECISION_SMALL, BPConstant.EMPTY_STRING,
									rhsAttributes.getFormat().getPrecision()));
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.SCALE_SMALL, BPConstant.EMPTY_STRING,
									rhsAttributes.getFormat().getScale()));
							break;
						case BPConstant.BOOLEAN:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.TRUE_VALUE, BPConstant.EMPTY_STRING,
									rhsAttributes.getFormat().getTrueValue()));
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.FALSE_VALUE, BPConstant.EMPTY_STRING,
									rhsAttributes.getFormat().getFalseValue()));
							break;
						case BPConstant.DATE_TIME:
							fieldsList.add(BPDesignerServiceImpl.buildResponseJson(rhsAttributes.getKey(),
									BPConstant.DELIMITED, BPConstant.DATE_TIME_SMALL, BPConstant.EMPTY_STRING,
									rhsAttributes.getFormat().getDateTime()));
							break;
						default:
							break;
						}
					}
				}
			}
		}
	}
}
