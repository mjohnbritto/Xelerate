package com.suntecgroup.bp.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suntecgroup.bp.designer.services.impl.BPDesignerServiceImpl;

/**
 * Utility class
 */
public class BPUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(BPDesignerServiceImpl.class);

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


}
