/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */

package com.suntecgroup.bpconf.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suntecgroup.bpconf.model.BpConfiguration;

/*
 * This class contains the utility methods
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
public class BPConfUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(BPConfUtils.class);

	public static BpConfiguration convertJsonStringToJava(final String bpConfJsonString) {

		ObjectMapper mapper = new ObjectMapper();
		BpConfiguration bpConfiguration = null;
		try {
			bpConfiguration = mapper.readValue(bpConfJsonString, new TypeReference<BpConfiguration>() {
			});
		} catch (IOException exception) {
			LOGGER.error("Exception occurred :: " + exception.getMessage(), exception);
		}
		return bpConfiguration;
	}

	public static String concatString(String filePath, String bpName, String fileSuffix, String fileExtension) {
		if (StringUtils.isEmpty(filePath) || StringUtils.isEmpty(bpName) || StringUtils.isEmpty(fileSuffix)
				|| StringUtils.isEmpty(fileExtension)) {
			return "";
		} else {
			return (new StringBuilder()).append(filePath).append(bpName).append(fileSuffix).append(fileExtension)
					.toString();
		}
	}
}
