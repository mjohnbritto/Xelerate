/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2019
 */
package com.suntecgroup.custom.processor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.buk.Buk;
import com.suntecgroup.custom.processor.model.buk.EventBuk;
import com.suntecgroup.custom.processor.model.channelintegration.CIMapping;
import com.suntecgroup.custom.processor.model.smartconnector.TypeConversion;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;

/*
 * This class contains the common utility functionalities required by NiFi custom processor
 * 
 * @version 1.0 - June 2019
 * @author John Britto
 */
public class CommonUtils {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Gson GSON = new GsonBuilder().create();
	private static final TypeReference<List<ProcessVariable>> TYPE_REF = new TypeReference<List<ProcessVariable>>() {};
	private static final Type MAP_TYPE_OBJ = new TypeToken<Map<String, Object>>() {}.getType();
	
	public static List<ProcessVariable> convertJsonStringToJava(final String processVarJsonStr,
			final ComponentLog LOGGER) throws NifiCustomException {

		List<ProcessVariable> processVariables = null;
		try {
			processVariables = MAPPER.readValue(processVarJsonStr, TYPE_REF);
		} catch (JsonParseException jsonParseException) {
			LOGGER.error("Exception occurred :: " + jsonParseException.getMessage(), jsonParseException);
			throw new NifiCustomException("JSON parsing exception occured!");
		} catch (JsonMappingException jsonMappingException) {
			LOGGER.error("Exception occurred :: " + jsonMappingException.getMessage(), jsonMappingException);
			throw new NifiCustomException("JSON mapping exception occured!");
		} catch (IOException ioException) {
			LOGGER.error("Exception occurred :: " + ioException.getMessage(), ioException);
			throw new NifiCustomException("JSON parsing failed!");
		}
		return processVariables;
	}

	public static void validateSessionId(final ProcessContext context, final ProcessSession session,
			final FlowFile flowFile, final PropertyDescriptor pdSessionId, final ComponentLog LOGGER)
			throws NifiCustomException {
		try {
			String sessionId = context.getProperty(pdSessionId).evaluateAttributeExpressions().getValue();
			if (StringUtils.isBlank(sessionId) || "0".equals(sessionId)) {
				throw new NifiCustomException("Invalid session Id");
			} else if (null != flowFile) {
				sessionId = sessionId.trim();
				session.putAttribute(flowFile, Constants.ATTR_SESSION_ID, sessionId);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred while validating sessionId :: " + exception.getMessage(), exception);
			throw new NifiCustomException("Invalid session Id");
		}
	}

	public static void validateRunNumber(final ProcessContext context, final ProcessSession session,
			final FlowFile flowFile, final PropertyDescriptor pdRunNumber, final ComponentLog LOGGER)
			throws NifiCustomException {
		try {
			String strRunNumber = context.getProperty(pdRunNumber).evaluateAttributeExpressions().getValue();
			int runNumber = Integer.parseInt(strRunNumber);
			if (runNumber < 1) {
				throw new NifiCustomException("Invalid run number");
			} else if (null != flowFile) {
				strRunNumber = strRunNumber.trim();
				session.putAttribute(flowFile, Constants.ATTR_RUN_NUMBER, strRunNumber);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred while validating run number :: " + exception.getMessage(), exception);
			throw new NifiCustomException("Invalid run number");
		}
	}

	public static void validateTransactionId(final FlowFile flowFile) throws NifiCustomException {
		if (null == flowFile.getAttribute("transactionId")
				|| StringUtils.isBlank(flowFile.getAttribute("transactionId"))) {
			throw new NifiCustomException("Invalid Transaction Id");
		}
	}

	public static List<ProcessVariable> readRuntimeProcessVariable(List<ProcessVariable> runtimeProcessVariableList,
			Map<String, String> flowfileAttributes, String inputChannelName, String transactionId,
			final ComponentLog LOGGER) throws NifiCustomException {
		try {
			for (ProcessVariable processVariable : runtimeProcessVariableList) {

				// Update implicit process variable
				if (StringUtils.equalsIgnoreCase(Constants.PV_CHANNEL_NAME, processVariable.getName())) {
					processVariable.getValue().setStringValue(inputChannelName);
				} else if (StringUtils.equalsIgnoreCase(Constants.PV_TRANSACTION_ID, processVariable.getName())) {
					processVariable.getValue().setStringValue(transactionId);
				} else if (processVariable.getFlags().getIsProfileableAtOperation()
						&& flowfileAttributes.containsKey(processVariable.getName())) {
					if (StringUtils.isBlank(flowfileAttributes.get(processVariable.getName()))) {
						throw new NifiCustomException("Process variable validation failed due to invalid data");
					}
					String value = flowfileAttributes.get(processVariable.getName());
					value = value.trim();

					if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_PRIMITIVE,
							processVariable.getType().getTypeCategory())) {
						value = value.replaceAll("\"", "");
						switch (processVariable.getType().getTypeName().toLowerCase()) {
						case Constants.dataTypeNumber:
							processVariable.getValue().setIntValue(new BigDecimal(value));
							if (value.contains(".")) {
								processVariable.getValue().setPrecision(
										(value.split("\\.")[0].length() + value.split("\\.")[1].length()));
								processVariable.getValue().setScale(value.split("\\.")[1].length());
							} else {
								processVariable.getValue().setPrecision(value.length());
								processVariable.getValue().setScale(0);
							}
							break;
						case Constants.dataTypeString:
							processVariable.getValue().setStringValue(value);
							break;
						case Constants.dataTypeBoolean:
							if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
								processVariable.getValue().setBooleanValue(Boolean.parseBoolean(value));
							} else {
								throw new NifiCustomException(
										"Process variable validation failed due to invalid type of data");
							}
							break;
						case Constants.dataTypeDate:
							if (validateDateFormat(value, Constants.DATE_FORMAT)) {
								processVariable.getValue().setDateValue(value);
							} else {
								throw new NifiCustomException(
										"Process variable validation failed due to invalid type of data");
							}
							break;
						default:
							break;
						}
					} else if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_BE,
							processVariable.getType().getTypeCategory())) {
						if (isDataValidJsonObject(value, LOGGER)) {
							JSONObject jsonObject = new JSONObject(value);
							processVariable.getValue().setBeValue(jsonObject.toString());
						} else {
							throw new NifiCustomException(
									"Process variable validation failed due to invalid format of data");
						}
					}

				}
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:" + exception.getMessage(), exception);
			throw new NifiCustomException(exception.getMessage());
		}
		return runtimeProcessVariableList;
	}

	private static boolean validateDateFormat(String dateValue, String dateFormat) {
		if (StringUtils.isEmpty(dateValue) || StringUtils.isEmpty(dateFormat)) {
			return false;
		}
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			formatter.setLenient(false);
			formatter.parse(dateValue);
		} catch (ParseException exception) {
			return false;
		}
		return true;
	}

	public static boolean validateProcessVariable(List<ProcessVariable> processVariables, final ComponentLog LOGGER) {
		boolean processVarValStatus = true;
		try {
			for (ProcessVariable processVariable : processVariables) {
				if (processVariable.getFlags().getIsMandatory()) {

					if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_PRIMITIVE,
							processVariable.getType().getTypeCategory())) {
						switch (processVariable.getType().getTypeName().toLowerCase()) {
						case Constants.dataTypeString:
							if (StringUtils.isEmpty(processVariable.getValue().getStringValue().trim())) {
								processVarValStatus = false;
							}
							break;
						case Constants.dataTypeNumber:
							if (StringUtils.isEmpty(String.valueOf(processVariable.getValue().getIntValue()).trim())
									|| StringUtils
											.isEmpty(String.valueOf(processVariable.getValue().getPrecision()).trim())
									|| StringUtils
											.isEmpty(String.valueOf(processVariable.getValue().getScale()).trim())) {
								processVarValStatus = false;
							}
							break;
						case Constants.dataTypeBoolean:
							if (StringUtils.isEmpty("" + processVariable.getValue().getBooleanValue())) {
								processVarValStatus = false;
							}
							break;
						case Constants.dataTypeDate:
							if (StringUtils.isEmpty(processVariable.getValue().getDateValue().trim())) {
								processVarValStatus = false;
							}
							break;
						default:
							break;
						}
					} else if (StringUtils.equalsIgnoreCase(Constants.PV_TYPE_CATEGORY_BE,
							processVariable.getType().getTypeCategory())) {
						if (StringUtils.isBlank(processVariable.getValue().getBeValue())
								|| processVariable.getValue().getBukAttributes().length < 1) {
							processVarValStatus = false;
						} else {
							try {
								validateBUK(processVariable.getValue().getBeValue(),
										processVariable.getValue().getBukAttributes(), LOGGER);
							} catch (NifiCustomException nifiCustomException) {
								LOGGER.debug("Exception occurred:" + nifiCustomException.getMessage(),
										nifiCustomException);
								processVarValStatus = false;
							} catch (Exception exception) {
								LOGGER.debug("Exception occurred:" + exception.getMessage(), exception);
								processVarValStatus = false;
							}
						}
					}

					if (!processVarValStatus) {
						break;// break the for loop
					}

				}
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:" + exception.getMessage(), exception);
			processVarValStatus = false;
		}
		return processVarValStatus;
	}

	public static boolean validateBUK(final ProcessSession session, final FlowFile flowfile, final String bukAttributes,
			final ComponentLog LOGGER) throws NifiCustomException {

		if (StringUtils.isBlank(bukAttributes)) {
			throw new NifiCustomException("BUK attributes are empty!");
		}

		InputStream inputStream = null;
		JsonReader reader = null;

		try {
			JSONArray arrayBukAttributes = new JSONArray(bukAttributes);
			inputStream = session.read(flowfile);
			reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			reader.setLenient(true);
			reader.beginArray();

			EventBuk invalidEventBuk = new EventBuk();
			while (reader.hasNext()) {
				Map<String, Object> thisRow = GSON.fromJson(reader, MAP_TYPE_OBJ);
				for (int index = 0; index < arrayBukAttributes.length(); index++) {
					String key = arrayBukAttributes.getString(index);
					Object value = thisRow.get(key);
					if (!thisRow.containsKey(key) || null == value || StringUtils.isBlank(value.toString())) {
						// BUK attribute is not available/value is empty
						invalidEventBuk.addBuk(new Buk(key, ""));
					}
				}
			}
			reader.endArray();
			reader.close();

			if (invalidEventBuk.getBuk() != null && invalidEventBuk.getBuk().size() > 0) {
				throw new NifiCustomException("Failed due to Invalid BUK!");
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:" + exception.getMessage(), exception);
			throw new NifiCustomException("Failed due to Invalid BUK!");
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (IOException ioException) {
				LOGGER.error("Exception occurred:" + ioException.getMessage(), ioException);
			}
		}
		return true;
	}

	public static boolean validateBUK(final String inputBEData, final String[] bukAttributes, final ComponentLog LOGGER)
			throws NifiCustomException {

		if (bukAttributes.length < 1) {
			throw new NifiCustomException("Failed due to Invalid BUK!");
		}

		JsonReader reader = null;

		try {
			JSONArray arrayBukAttributes = new JSONArray(bukAttributes);
			JSONObject jsonObject = new JSONObject(inputBEData);
			reader = new JsonReader(new StringReader(jsonObject.toString()));
			reader.setLenient(true);

			EventBuk invalidEventBuk = new EventBuk();
			Map<String, Object> thisRow = GSON.fromJson(reader, MAP_TYPE_OBJ);
			for (int index = 0; index < arrayBukAttributes.length(); index++) {
				String key = arrayBukAttributes.getString(index);
				Object value = thisRow.get(key);
				if (!thisRow.containsKey(key) || null == value || StringUtils.isBlank(value.toString())) {
					// BUK attribute is not available/value is empty
					invalidEventBuk.addBuk(new Buk(key, ""));
				}
			}

			reader.close();

			if (invalidEventBuk.getBuk() != null && invalidEventBuk.getBuk().size() > 0) {
				throw new NifiCustomException("Failed due to Invalid BUK!");
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:" + exception.getMessage(), exception);
			throw new NifiCustomException("Failed due to Invalid BUK!");
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
			} catch (IOException ioException) {
				LOGGER.error("Exception occurred:" + ioException.getMessage(), ioException);
			}
		}
		return true;
	}

	public static int getEventsCount(final ProcessSession session, final FlowFile flowfile, final ComponentLog LOGGER)
			throws NifiCustomException {
		InputStream inputStream = null;
		JsonReader reader = null;
		int eventsCount = 0;
		try {
			inputStream = session.read(flowfile);
			reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			reader.setLenient(true);
			reader.beginArray();

			while (reader.hasNext()) {
				eventsCount++;
				reader.skipValue();
			}
			reader.endArray();
			reader.close();
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:" + exception.getMessage(), exception);
			throw new NifiCustomException("Error while counting events : " + exception.getMessage());
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (IOException ioException) {
				LOGGER.error("Exception occurred:" + ioException.getMessage(), ioException);
			}
		}
		return eventsCount;
	}

	public static boolean isDataValidJsonArray(String inputData, final ComponentLog LOGGER) {
		try {
			new JSONArray(inputData);
		} catch (JSONException jsonArrayException) {
			LOGGER.debug("Exception occurred:" + jsonArrayException.getMessage(), jsonArrayException);
			return false;
		}
		return true;
	}

	public static boolean isDataValidJsonObject(String inputData, final ComponentLog LOGGER) {
		try {
			new JSONObject(inputData);
		} catch (JSONException jsonObjectException) {
			LOGGER.debug("Exception occurred:" + jsonObjectException.getMessage(), jsonObjectException);
			return false;
		}
		return true;
	}

	public static boolean extractAndValidateBEMetaData(FlowFile flowfile, String bpMetaDataChannelName) {
		String bpMetaDataChannelId = flowfile.getAttribute("channelId") != null ? flowfile.getAttribute("channelId")
				: "";
		return !(StringUtils.isEmpty(bpMetaDataChannelId) || StringUtils.isEmpty(bpMetaDataChannelName));
	}
	
	public static Object ApplyConvertFunction(Object modifiableVal, String function) throws NifiCustomException {
		try {
			if (function.startsWith(Constants.SUBSTRING)) {

				int openBrace = function.indexOf('(') + 1;
				int closeBrace = function.indexOf(')');
				String res = null;
				String[] subStrIndexes = function.substring(openBrace, closeBrace).split(",");

				if (subStrIndexes.length == 2) {
					res = (modifiableVal.toString().substring(Integer.parseInt(subStrIndexes[0]),
							Integer.parseInt(subStrIndexes[1])));
				}
				return res;

			} else if (function.startsWith(Constants.TO_NUMBER)) {

				try {
					Number number = NumberFormat.getInstance().parse(modifiableVal.toString());
					return number;
				} catch (Exception ex) {
					throw new NifiCustomException(function + " : function called on non string/number instance", ex);
				}

			} else if (Constants.TO_DATE_TIME.equals(function)) {
				try {
					Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(modifiableVal.toString());
					return modifiableVal.toString();
				} catch (Exception ex) {
					throw new NifiCustomException(function + " To date function called on non-string instance", ex);
				}
			} else if (function.startsWith(Constants.TO_DATE)) {
				try {
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(modifiableVal.toString());
					return modifiableVal.toString();
				} catch (Exception ex) {
					throw new NifiCustomException(function + " To date function called on non-string instance", ex);
				}
			} else if (function.startsWith(Constants.TO_STRING)) {
				return modifiableVal.toString();
			} else if (function.startsWith(Constants.IS_DATE)) {

			} else if (function.startsWith(Constants.ROUND)) {
				try {
					int openBrace = function.indexOf('(') + 1;
					int closeBrace = function.indexOf(')');
					String[] subStrIndexes = function.substring(openBrace, closeBrace).split(",");
					int roundToDigits = Integer.parseInt(subStrIndexes[0]);

					if (modifiableVal instanceof Number) {
						int scale = (int) Math.pow(10, roundToDigits);
						return (double) Math.round(Double.valueOf(modifiableVal.toString()) * scale) / scale;
					}
				} catch (Exception ex) {
					throw new NifiCustomException("Error occured while Rounding the Number", ex);
				}

			}
		} catch (Exception ex) {
			throw new NifiCustomException("Error occured while applying the type conversion function", ex);
		}
		return modifiableVal;
	}

	public static Object typeConvertResult(Object result, ArrayList<TypeConversion> typeConversionArray)
			throws NifiCustomException {
		if (null != result && !(result instanceof Map) && !(result instanceof List)) {
			for (TypeConversion conversionData : typeConversionArray) {
				String function = modifydataToFunctionName(conversionData);
				result = ApplyConvertFunction(result, function);
			}
		}
		return result;
	}

	private static String modifydataToFunctionName(TypeConversion conversionData) {
		String functionName = conversionData.getDataType();
		if (functionName.equalsIgnoreCase(Constants.SUBSTRING)) {
			functionName = functionName + "(" + conversionData.getInputValue().get(0).getValue() + ","
					+ conversionData.getInputValue().get(1).getValue() + ")";
		} else if (functionName.equalsIgnoreCase(Constants.ROUND)) {
			functionName = functionName + "(" + conversionData.getInputValue().get(0).getValue() + ")";
		}
		return functionName;
	}

	public static String fetchHFFieldValuePrimitive(String fieldName, String[] fromPath, List<Map<String, Object>> resultList,
			String source) throws NifiCustomException {

		// resultList contains header or footer data which maps with PV
		String attrValueHF = "";
		try {

			for (Map<String, Object> currentData : resultList) {
				Iterator<Entry<String, Object>> mapIterator = currentData.entrySet().iterator();
				while (mapIterator.hasNext()) {
					Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();
					int attrPos = 2;
					String root = fromPath[fromPath.length - attrPos];
					if (fieldName.equalsIgnoreCase(root)) {
						// get straigh value
						if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(fieldName)) {
							attrValueHF = (String) ev.getValue();
							if (attrValueHF != null) {
								return attrValueHF;
							}
						}
					} else if (ev.getKey().equalsIgnoreCase(root)) {
						Map<String, Object> rootObject = (Map<String, Object>) ev.getValue();
						attrValueHF = MappingUtils.fetchSubObject(fromPath, fieldName, rootObject, attrPos);
					}
				}

			}

		}

		catch (Exception ex) {
			throw new NifiCustomException("Error while fetching attribute " + fieldName + " value from " + source
					+ " due to required number value is null or empty");
		}

		return attrValueHF;
	}

	public static Map<String, Object> fetchFeildValueFromRootObject(String pvNameObj, List<Map<String, Object>> resultList,
			List<CIMapping> pvMappingList, String source) throws NifiCustomException {
		Map<String, Object> value = new HashMap<>();
		String pvValue = "";
		List<Map<String, Object>> footerAttrValueLstObj = null;

		for (CIMapping obj : pvMappingList) {
			if (null != obj.getToCurrentNode() && obj.getToCurrentNode().equalsIgnoreCase(pvNameObj)) {
				if (null != obj.getDataType() && !obj.getDataType().equalsIgnoreCase("array")
						&& !obj.getDataType().equalsIgnoreCase("object")) {
					String pvName = obj.getToValue();
					String attrName = obj.getFromValue();
					String[] fromPath = obj.getFromPath().split("-");
					try {
						// Search value from HF of PV type BE's primitive type
						pvValue = fetchHFFieldValuePrimitive(attrName, fromPath, resultList, source);
						if (null != pvValue)
							value.put(pvName, pvValue);
					} catch (Exception ex) {
						throw new NifiCustomException("Error while fetching attribute " + pvNameObj + " value from "
								+ source + " due to required number value is null or empty");
					}
				} else if (null != obj.getDataType() && obj.getDataType().equalsIgnoreCase("object")) {
					Map<String, Object> footerAttrValueSubObj = new HashMap<String, Object>();
					String pvNameSubobj = obj.getToValue();
					footerAttrValueSubObj = fetchFeildValueFromRootObject(pvNameSubobj, resultList, pvMappingList,
							source);
					value.put(pvNameSubobj, footerAttrValueSubObj);
				} else if (null != obj.getDataType() && obj.getDataType().equalsIgnoreCase("array")) {
					String pvNameArray = obj.getToValue();
					String attrNameArray = obj.getFromValue();
					footerAttrValueLstObj = fetchFeildValueFromRootArray(pvNameArray, attrNameArray, resultList,
							pvMappingList, "footer");
					value.put(pvNameArray, footerAttrValueLstObj);
				}
			}

		}
		return value;
	}

	public static List<Map<String, Object>> fetchFeildValueFromRootArray(String pvNameArray, String attrNameArray,
			List<Map<String, Object>> resultList, List<CIMapping> pvMappingList, String source)
			throws NifiCustomException {
		List<Map<String, Object>> attrArray = new ArrayList<>();
		List<Map<String, Object>> attrValueLstObj = new ArrayList<>();
		for (Map<String, Object> resultListObj : resultList) {
			Iterator<Entry<String, Object>> mapIterator = resultListObj.entrySet().iterator();
			while (mapIterator.hasNext()) {
				Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();

				if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(attrNameArray)) {
					attrArray = (List) ev.getValue();
					break;
				}
			}
		}
		for (Map<String, Object> attrArrayObj : attrArray) {
			Map<String, Object> sample = fetchArrayObj(pvNameArray, attrArrayObj, resultList, pvMappingList, source);
			attrValueLstObj.add(sample);
		}
		
		return attrValueLstObj;
	}

	private static Map<String, Object> fetchArrayObj(String pvNameArray, Map<String, Object> attrArrayObj,
			List<Map<String, Object>> resultList, List<CIMapping> pvMappingList, String source)
			throws NifiCustomException {
		Map<String, Object> attrValueObj = new HashMap<>();
		Map<String, Object> responseObj = new HashMap<>();
		Map<String, Object> attrArraySubObj = new HashMap<>();
		List<Map<String, Object>> attrValueLstObj = new ArrayList<>();
		for (CIMapping obj : pvMappingList) {
			if (obj.getToCurrentNode().equalsIgnoreCase(pvNameArray)) {
				if (null != obj.getDataType() && !obj.getDataType().equalsIgnoreCase("array")
						&& !obj.getDataType().equalsIgnoreCase("object")) {
					String pvName = obj.getToValue();
					String attrName = obj.getFromValue();
					String attrValue = fetchFeildValueFromObject(attrName, attrArrayObj, source);
					responseObj.put(pvName, attrValue);
				} else if (null != obj.getDataType() && obj.getDataType().equalsIgnoreCase("object")) {
					String pvName = obj.getToValue();
					String attrName = obj.getFromValue();
					Iterator<Entry<String, Object>> mapIterator = attrArrayObj.entrySet().iterator();
					while (mapIterator.hasNext()) {
						Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();
						if (ev.getKey().equalsIgnoreCase(attrName)) {
							attrArraySubObj = (Map) ev.getValue();
						}
					}
					attrValueObj = fetchArrayObj(pvName, attrArraySubObj, resultList, pvMappingList, source);
					responseObj.put(pvName, attrValueObj);
				} else if (null != obj.getDataType() && obj.getDataType().equalsIgnoreCase("array")) {
					String pvName = obj.getToValue();
					String attrName = obj.getFromValue();
					attrValueLstObj = CommonUtils.fetchFeildValueFromRootArray(pvName, attrName, resultList, pvMappingList, source);
					responseObj.put(pvName, attrValueLstObj);
				}
			}
		}
		return responseObj;
	}

	private static String fetchFeildValueFromObject(String fieldName, Map<String, Object> tempObj, String source)
			throws NifiCustomException {
		String attrValueHF = "";

		try {
			Iterator<Entry<String, Object>> mapIterator = tempObj.entrySet().iterator();
			while (mapIterator.hasNext()) {
				Entry<String, Object> ev = (Entry<String, Object>) mapIterator.next();
				// if the object is primitive
				if (ev.getKey() != null && ev.getKey().trim().equalsIgnoreCase(fieldName)) {

					attrValueHF = (String) ev.getValue();
					if (attrValueHF != null) {
						return attrValueHF;
					}
				}
			}
		} catch (Exception e) {
			throw new NifiCustomException("Error while fetching attribute " + fieldName + " value from " + source
					+ " due to required number value is null or empty");
		}
		return attrValueHF;
	}

	public static String convertDate(String value, String srcFormat, String desFormat) {
		SimpleDateFormat srcFormatter = new SimpleDateFormat(srcFormat);
		SimpleDateFormat desFormatter = new SimpleDateFormat(desFormat);
		Date date = new Date();
		try {
			date = srcFormatter.parse(value.trim());
		} catch (ParseException e) {
		}

		return desFormatter.format(date);
	}
	
	public static String getCurrentTimeStampFormatted(String timeStampFormat) {
		if (StringUtils.isBlank(timeStampFormat)) {
			return "" + System.currentTimeMillis();
		} else {
			String formattedDate = "" + System.currentTimeMillis();
			try {
				formattedDate = new SimpleDateFormat(timeStampFormat).format(new Date());
			} catch (IllegalArgumentException iae) {
			}
			return formattedDate;
		}
	}

}
