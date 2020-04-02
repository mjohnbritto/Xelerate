/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */

package com.suntecgroup.bpconf.services.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.suntecgroup.bpconf.model.Configuration;
import com.suntecgroup.bpconf.model.ErrorDetail;
import com.suntecgroup.bpconf.model.Response;
import com.suntecgroup.bpconf.model.Status;
import com.suntecgroup.bpconf.services.BPConfServiceInterface;
import com.suntecgroup.bpconf.util.BPConfConstants;
import com.suntecgroup.bpconf.util.BPConfUtils;

/*
 * This class is implementing the Business service methods defined in the interface BPConfServiceInterface
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@Service
public class BPConfServiceImpl implements BPConfServiceInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(BPConfServiceImpl.class);
	@Autowired
	private Environment env;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Response<HashMap> getConfiguration(String bpName, String confType) {
		String bpConfFilePath = env.getProperty("BusinessProcessConfigFilePath");
		String fileName = BPConfUtils.concatString(bpConfFilePath, bpName, BPConfConstants.bpConfFileSuffix,
				BPConfConstants.bpConfFileExtension);
		LOGGER.debug("the configuation filename =>" + fileName);
		Response response = null;
		BufferedReader bufferedReader = null;
		String entryString;
		String key;
		String value;

		if (!BPConfConstants.confTypeProcessVariable.equalsIgnoreCase(confType.trim())
				&& !BPConfConstants.confTypeProperty.equalsIgnoreCase(confType.trim())) {
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "Invalid configuration type"), null);
			return response;
		}
		try {
			List<Configuration> configurationsList = new ArrayList<Configuration>();
			Configuration configuration = null;

			bufferedReader = new BufferedReader(new FileReader(fileName));
			while ((entryString = bufferedReader.readLine()) != null) {
				entryString = entryString.trim();
				if (!StringUtils.isEmpty(entryString) && entryString.indexOf("=") > -1) {
					key = URLDecoder.decode(entryString.split("=")[0], BPConfConstants.formatEncodeDecode);
					if (entryString.endsWith("=")) {
						value = "";
					} else {
						value = entryString.split("=")[1];
					}
					configuration = new Configuration(key, value);
					if (BPConfConstants.confTypeProcessVariable.equalsIgnoreCase(confType.trim())
							&& key.indexOf(BPConfConstants.confTypeProcessVariable) > -1) {
						configurationsList.add(configuration);
					} else if (BPConfConstants.confTypeProperty.equalsIgnoreCase(confType.trim())
							&& key.indexOf(BPConfConstants.confTypeProcessVariable) < 0) {
						configurationsList.add(configuration);
					}
				}
			}
			HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
			if (BPConfConstants.confTypeProcessVariable.equalsIgnoreCase(confType.trim())) {
				propertiesMap.put("processVariables", configurationsList);
			} else if (BPConfConstants.confTypeProperty.equalsIgnoreCase(confType.trim())) {
				propertiesMap.put("properties", configurationsList);
			}
			response = new Response(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null, propertiesMap);
		} catch (FileNotFoundException exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "File not found!"), null);
		} catch (IOException exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "Could not read the file"), null);
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException exception) {
				LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
				response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
						new ErrorDetail("", "IO Exception"), null);
			}
		}

		return response;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Response<HashMap> getConfigurationForParticularOperator(String bpName, String operatorKey) {
		String bpConfFilePath = env.getProperty("BusinessProcessConfigFilePath");
		String fileName = BPConfUtils.concatString(bpConfFilePath, bpName, BPConfConstants.bpConfFileSuffix,
				BPConfConstants.bpConfFileExtension);
		LOGGER.debug("the configuation filename =>" + fileName);
		Response response = null;
		BufferedReader bufferedReader = null;
		String entryString, key, value;
		operatorKey = operatorKey.trim();

		try {
			List<Object> configurationsList = new ArrayList<Object>();
			Configuration configuration = null;

			bufferedReader = new BufferedReader(new FileReader(fileName));
			while ((entryString = bufferedReader.readLine()) != null) {
				entryString = entryString.trim();
				if (!StringUtils.isEmpty(entryString) && entryString.indexOf("=") > -1) {
					key = URLDecoder.decode(entryString.split("=")[0], BPConfConstants.formatEncodeDecode);
					if (entryString.endsWith("=")) {
						value = "";
					} else {
						value = entryString.split("=")[1];
					}
					configuration = new Configuration(key, value);
					if (key.indexOf(BPConfConstants.confTypeProcessVariable) < 0
							&& key.split("\\.")[1].equalsIgnoreCase(operatorKey)) {
						configurationsList.add(configuration);
					}
				}
			}
			HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("properties", configurationsList);
			response = new Response(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null, propertiesMap);
		} catch (FileNotFoundException exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "File not found!"), null);
		} catch (IOException exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "Could not read the file"), null);
		} catch (Exception exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "Something went wrong! Please try again later"), null);
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException exception) {
				LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
				response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
						new ErrorDetail("", "IO Exception"), null);
			}
		}

		return response;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Response<List> saveConfiguration(String bpName, List<Configuration> configurationsList) {
		String bpConfFilePath = env.getProperty("BusinessProcessConfigFilePath");
		String fileName = BPConfUtils.concatString(bpConfFilePath, bpName, BPConfConstants.bpConfFileSuffix,
				BPConfConstants.bpConfFileExtension);
		LOGGER.debug("the configuation filename =>" + fileName);
		Response response = null;
		BufferedReader bufferedReader = null;
		boolean isUpdated = false;
		HashMap<String, String> propertiesMap = new HashMap<String, String>();
		String entryString, key, value;

		// Read the file and populate the properties into Map
		try {
			bufferedReader = new BufferedReader(new FileReader(fileName));
			while ((entryString = bufferedReader.readLine()) != null) {
				entryString = entryString.trim();
				if (!StringUtils.isEmpty(entryString) && entryString.indexOf("=") > -1) {
					key = entryString.split("=")[0];
					if (entryString.endsWith("=")) {
						value = "";
					} else {
						value = entryString.split("=")[1];
					}
					propertiesMap.put(key, value);
				}
			}
		} catch (FileNotFoundException exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			return new Response(Status.FAILURE.getStatusCode(), Status.FAILURE, new ErrorDetail("", "File not found!"),
					null);
		} catch (IOException exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			return new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "Could not read the file"), null);
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException exception) {
				LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			}
		}

		// Update the property
		List<String> unupdatedProperties = new ArrayList<String>();
		try {
			for (Configuration property : configurationsList) {
				key = URLEncoder.encode(property.getKey(), BPConfConstants.formatEncodeDecode);
				value = property.getValue();
				if (propertiesMap.containsKey(key) && !StringUtils.isEmpty(value)) {
					isUpdated = true;
					propertiesMap.put(key, value);
				} else {
					unupdatedProperties.add(property.getKey());
				}
			}
		} catch (UnsupportedEncodingException exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			return new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "UnsupportedEncodingException"), null);
		}
		// Write the file back
		if (isUpdated) {
			PrintWriter printWriter = null;
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(fileName, false);
				printWriter = new PrintWriter(fileWriter);
				for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
					entryString = entry.getKey().toString() + "=" + entry.getValue().toString();
					printWriter.println(entryString);
				}
			} catch (IOException exception) {
				LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			} finally {
				printWriter.close();
				try {
					if (fileWriter != null)
						fileWriter.close();
				} catch (IOException exception) {
					LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
				}
			}
		} else {
			LOGGER.debug("No changes to update");
		}

		if (unupdatedProperties.size() > 0) {
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "Error occurred while updating the below properties"), unupdatedProperties);
		} else {
			response = new Response(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null, "Properties are updated");
		}

		return response;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response<String> isPropertyExisting(String bpName, String propertyName) {
		String bpConfFilePath = env.getProperty("BusinessProcessConfigFilePath");
		String fileName = BPConfUtils.concatString(bpConfFilePath, bpName, BPConfConstants.bpConfFileSuffix,
				BPConfConstants.bpConfFileExtension);
		LOGGER.debug("the configuation filename =>" + fileName);
		Response response = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(fileName));
			String entry;
			while ((entry = bufferedReader.readLine()) != null && entry.indexOf("=") > -1) {
				String keyPropertyName = URLDecoder.decode(entry.split("=")[0], BPConfConstants.formatEncodeDecode);
				if (propertyName.equalsIgnoreCase(keyPropertyName)) {
					response = new Response(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
							"The property with the given name is available");
					return response;
				}
			}
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "The property with the given name is not available"), null);
		} catch (FileNotFoundException exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "File not found!"), null);
		} catch (IOException exception) {
			LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
			response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
					new ErrorDetail("", "Could not read the file"), null);
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException exception) {
				LOGGER.error("Exception occurred : " + exception.getMessage(), exception);
				response = new Response(Status.FAILURE.getStatusCode(), Status.FAILURE,
						new ErrorDetail("", "IO Exception"), null);
			}
		}
		return response;
	}

}
