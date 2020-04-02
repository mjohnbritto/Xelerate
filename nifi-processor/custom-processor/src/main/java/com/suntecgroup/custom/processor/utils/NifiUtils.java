/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.model.channelintegration.OperatorStats;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/*
 * This class is to access the utilities of NiFi, an Intermediator for the custom processors
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */

public class NifiUtils {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	
	public static String convertObjectToJsonString(Object object, ComponentLog logger) {
		String jsonString = null;
		try {
			jsonString = MAPPER.writeValueAsString(object);
			return jsonString;
		} catch (Exception exception) {
			logger.error("Exception occurred:" + exception.getMessage(), exception);
		}
		return null;
	}

	/*
	 * This method is used to update the failure details in flowfile attribute
	 */
	public static FlowFile updateFailureDetails(final ProcessContext context, final ProcessSession session,
			FlowFile flowFile, String beName, String errorType, String errorMessage) {

		HashMap<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put(Constants.OPERATORNAME, context.getName().split("-")[0]);
		attributesMap.put(Constants.BENAME, beName);
		attributesMap.put(Constants.ERRORTYPE, errorType);
		attributesMap.put(Constants.ERRORMESSAGE, errorMessage);

		session.putAllAttributes(flowFile, attributesMap);
		return flowFile;
	}

	public static FlowFile cloneFlowfileWithoutContent(FlowFile ipFlowFile, ProcessSession session,
			ComponentLog logger) {
		FlowFile emptyFlowfile = null;

		try {
			emptyFlowfile = session.create();
			session.putAttribute(emptyFlowfile, Constants.TRANSACTION_ID,
					ipFlowFile.getAttribute(Constants.TRANSACTION_ID));
			OutputStream outputStream = session.write(emptyFlowfile);
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, Constants.UTF_ENCODING));
			writer.beginArray();
			writer.endArray();
			writer.close();
			outputStream.close();

		} catch (Exception exception) {
			logger.error("Exception occured during empty flow file creation :" + exception.getMessage(), exception);
		}
		session.putAttribute(emptyFlowfile, Constants.IS_MARKER, "true");
		return emptyFlowfile;
	}

	public static void updateStats(String url, String sessionId, String runNumber, String operatorName,
			ComponentLog logger, int totalRequestCount, int totalRecordsCount,
			int totalSuccessRecordsCount, int totalSuccessRequestsCount, int totalFailureRecordsCount,
			int totalFailureRequestsCount,OkHttpClient okHttpClient) {
		OperatorStats operatorStats = new OperatorStats();
		operatorStats.setSessionId(sessionId);
		operatorStats.setRunNumber(runNumber);
		operatorStats.setOperatorName(operatorName);
		operatorStats.setTotalRecordsCount(totalRecordsCount);
		operatorStats.setTotalRequestsCount(totalRequestCount);
		operatorStats.setTotalSuccessRecordsCount(totalSuccessRecordsCount);
		operatorStats.setTotalSuccessRequestsCount(totalSuccessRequestsCount);
		operatorStats.setTotalFailureRecordsCount(totalFailureRecordsCount);
		operatorStats.setTotalFailureRequestsCount(totalFailureRequestsCount);
		final String ROOT_URI = url + "/bpruntime/sessionmanager/updateOperatorStats";	
		Request.Builder requestBuilder = new Request.Builder();
		requestBuilder = requestBuilder.url(ROOT_URI);
		try {
			requestBuilder = requestBuilder.post(RequestBody.create(MediaType.parse(DEFAULT_CONTENT_TYPE), MAPPER.writeValueAsString(operatorStats)));
		} catch (JsonMappingException e) {
			logger.error("Error occurred at updateOperatorStatistics:: " + e.getMessage());
		} catch (IOException e) {
			logger.error("Error occurred at updateOperatorStatistics:: " + e.getMessage());
		}
		ResponseBody responseBody = null;
		try (Response responseHttp = okHttpClient.newCall(requestBuilder.build()).execute()) {
			responseBody = responseHttp.body();
			if(responseHttp.code() != 200) {
				logger.error("Exception while updating the status :: {}", new Object[]{responseHttp.code()});
			}
		} catch (IOException e) {
			logger.error("Error occurred at updateOperatorStatistics :: {}", new Object[]{e.getMessage()}, e);
		} finally {
			responseBody.close();
		}
	}
	
	/**
	 * This method will read the incoming flowfile content data and would fetch
	 * the value for attribute mentioned in inputbe definition.
	 * 
	 * @param ffContentMap
	 *            - flowfile content record
	 * @param requiredAttribute
	 *            - attribute for which the value needs to be fetched
	 * @return Object - data fetched from the inputrecord.
	 */
	public static Object getDatafromFlowFileContent(Map<String, Object> ffContentMap, String requiredAttribute) {

		Object resultValue = null;
		if (ffContentMap != null && ffContentMap.size() > 0 && requiredAttribute != null) {
			Iterator<Entry<String, Object>> inputJsonIterator = ffContentMap.entrySet().iterator();
			while (inputJsonIterator.hasNext()) {
				Entry<String, Object> entryObj = (Entry<String, Object>) inputJsonIterator.next();

				if (resultValue == null && entryObj.getKey() != null
						&& entryObj.getKey().trim().toLowerCase().equals(requiredAttribute.trim().toLowerCase())) {
					resultValue = entryObj.getValue();
					if (!(resultValue instanceof String)) {

					}
					break;
				}
			}
		}
		return resultValue;
	}

	/**
	 * This method will read the incoming flowfile content data and would fetch
	 * the value for attribute mentioned in inputbe definition.
	 * 
	 * @param ffContentMap
	 *            - flowfile content record
	 * @param requiredAttribute
	 *            - attribute for which the value needs to be fetched
	 * @return Object - data fetched from the inputrecord.
	 */
	public static Object getDatafromFlowFileContent(Map<String, Object> ffContentMap, String requiredAttribute, boolean isLong) {

		Object resultValue = null;
		if (ffContentMap != null && ffContentMap.size() > 0 && requiredAttribute != null) {
			Iterator<Entry<String, Object>> inputJsonIterator = ffContentMap.entrySet().iterator();
			while (inputJsonIterator.hasNext()) {
				Entry<String, Object> entryObj = (Entry<String, Object>) inputJsonIterator.next();
				if (resultValue == null && entryObj.getKey() != null
						&& entryObj.getKey().trim().toLowerCase().equals(requiredAttribute.trim().toLowerCase())) {
					if (isLong) {
						resultValue = Long.parseLong(entryObj.getValue().toString().split("\\.")[0]);
					} else {
						resultValue = entryObj.getValue();
					}
					break;
				}
			}
		}
		return resultValue;
	}
	
	/**
	 * 
	 * @param typeConversionString
	 * @param modifiableVal
	 * @param isReturnName
	 * @return
	 * @throws Exception
	 */
	public static Object getTypeConverted(String typeConversionString, Object modifiableVal, boolean isReturnName)
			throws Exception {
		String[] strArr = typeConversionString.split("\\$");
		if (strArr.length > 1) { // only check for Type conversion applicable String, else return
			String[] functionsArr = strArr[0].split("-");
			if (isReturnName)
				return functionsArr[0];
			if (functionsArr.length > 1) {// only allow if it have valid function
				for (int i = 1; i < functionsArr.length; i++) {
					modifiableVal = CommonUtils.ApplyConvertFunction(modifiableVal, functionsArr[i]);
					if (modifiableVal == null)
						break;
				}
				return modifiableVal;
			}
		}
		if (isReturnName) {
			return typeConversionString;
		} else {
			return modifiableVal;
		}

	}
}
