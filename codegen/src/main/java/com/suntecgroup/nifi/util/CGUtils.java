package com.suntecgroup.nifi.util;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.frontend.bean.Property;
import com.suntecgroup.nifi.template.beans.TemplateConfig;

public class CGUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CGUtils.class);

	public static Integer intNumberExtracter(String number) {
		return Integer.parseInt(number.split(" ")[0]);
	}

	public static Float floatNumberExtracter(String number) {
		return Float.parseFloat(number.split(" ")[0]);
	}

	public static String scaleOperatorProperty(String propValue, float scaleVal) {
		if (scaleVal == 1) {
			return propValue;
		}
		String[] str_list = propValue.split(" ");
		String return_str = "";
		int val = (int) (Integer.parseInt(str_list[0]) * scaleVal);
		if (str_list.length == 2) {
			if (val == 0) {
				return_str = "0 " + str_list[1];
			} else {
				return_str = Integer.toString(val) + " " + str_list[1];
			}
			return return_str;
		} else {
			if (val == 0) {
				return_str = "0";
			} else {
				return_str = Integer.toString(val);
			}
			return return_str;
		}
	}

	public static String convertJaveToJson(final Object obj) {

		ObjectMapper mappoer = new ObjectMapper();
		String jsonStr = "";
		try {
			jsonStr = mappoer.writeValueAsString(obj);
		} catch (IOException e) {
			LOGGER.error("Exception occured ::" + e);
		}
		return jsonStr;
	}

	public static void setPropertyValue(TemplateConfig config, Operators currentOperator,
			Map<String, Float> metaConfigScale, Map<String, String> metaConfigDefault) {
		for (Property property : currentOperator.getProperties()) {
			if (CGConstants.propertyPenaltyDuration.equalsIgnoreCase(property.getName())) {
				config.setPenaltyDuration("" + CGUtils.scaleOperatorProperty(property.getValue(),
						metaConfigScale.get(CGConstants.propertyPenaltyDuration)));
			} else if (CGConstants.propertyYieldDuration.equalsIgnoreCase(property.getName())) {
				config.setYieldDuration("" + CGUtils.scaleOperatorProperty(property.getValue(),
						metaConfigScale.get(CGConstants.propertyYieldDuration)));
			} else if (CGConstants.propertyConcurrentTasks.equalsIgnoreCase(property.getName())) {

				float concurrentTaskValue = Float.parseFloat(CGUtils.scaleOperatorProperty(property.getValue(),
						metaConfigScale.get(CGConstants.propertyConcurrentTasks)));
				if (concurrentTaskValue < CGConstants.DEFAULT_VALUE) {
					concurrentTaskValue = CGConstants.DEFAULT_VALUE;
					LOGGER.info(
							"concurrentTaskValue is less than Default Value and is Adjusted to" + concurrentTaskValue);
				}
				config.setConcurrentlySchedulableTaskCount("" + (int) Math.round(concurrentTaskValue));
			}
		}

		config.setBulletinLevel(metaConfigDefault.get(CGConstants.propertyBulletinLevel));
		config.setExecutionNode(metaConfigDefault.get(CGConstants.propertyExecution));
		config.setRunDurationMillis(metaConfigDefault.get(CGConstants.propertyRunDuration));
		config.setSchedulingStrategy(metaConfigDefault.get(CGConstants.propertySchedulingStrategy));
		config.setSchedulingPeriod(metaConfigDefault.get(CGConstants.propertyRunSchedule));

		config.setLossTolerant(CGConstants.LOSS_TOLERANT);
		config.setComments(CGConstants.BLANK);
	}

}
