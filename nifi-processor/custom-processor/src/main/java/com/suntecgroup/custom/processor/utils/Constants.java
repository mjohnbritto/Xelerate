/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */

package com.suntecgroup.custom.processor.utils;

/*
 * This class contains the constants that are required in the whole project
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */

public class Constants {
	public static final String processVariable = "Process Variable";
	public static final String contextVariable = "Context Variable";
	public static final String dataTypeNumber = "number";
	public static final String dataTypeString = "string";
	public static final String dataTypeBoolean = "boolean";
	public static final String dataTypeDate = "datetime";
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
	public static final String INPUT_ATTRIBUTE = "I/P BE ATTRIBUTE";
	public static final String ENTERVALUE = "Enter Value";
	public static final String INPUT_BUK = "Input_BUK";
	public static final String OUTPUT_BUK = "Output_BUK";
	public static final String INPUT_OUTPUT_MAPPING = "Input_Output_Mapping";
	public static final String OUTPUT_FLOWFILEUUID_MAPPING = "Output_FlowfileUUID_Mapping";
	public static final String UTF_ENCODING = "UTF-8";
	public static final String IO_CORRELATION = "in-out-correlation";
	public static final String INVOKE_EXTERNAL = "InvokeExternal";
	public static final String JSON_OBJECT = "JSONObject";
	public static final String JSON_ARRAY = "JSONArray";
	public static final String ATTR_SESSION_ID = "sessionId";
	public static final String ATTR_RUN_NUMBER = "runNumber";

	// Decision matrix processor
	public static final String PROCESS_VARIABLE = "processVariable";
	public static final String BE_ATTRIBUTE = "beAttribute";
	public static final String CUSTOM_VALUE = "customValue";
	public static final String BETWEEN_OPERATOR = "Between";
	public static final String DEFAULT = "default";
	public static final String CONDITION = "condition";
	public static final String RULES = "rules";
	public static final String OR = "or";
	public static final String AND = "and";

	// Invoke BS processor
	public static final String contextParameters = "context-parameters";
	public static final String context = "context";
	public static final String validationContext = "validationContext";
	public static final String failFast = "failFast";
	public static final String QUERY_CONTEXT = "queryContext";
	public static final String PAGE_CONTEXT = "pageContext";
	public static final String PAGE_NUMBER = "pageNumber";
	public static final String PAGE_SIZE = "pageSize";
	public static final String RESULT_CONTEXT = "resultContext";
	public static final String HAS_NEXT = "hasNext";
	public static final String ERROR_LIST = "errorList";
	public static final String SPLIT_AT_COMMA = ",";
	public static final String CODE = "code";
	public static final String MESSAGE = "message";
	public static final String entities = "entities";
	public static final String sessionId = "session-id";

	public static final String OPERATORNAME = "operatorName";
	public static final String BENAME = "beName";
	public static final String ERRORTYPE = "errorType";
	public static final String ERRORMESSAGE = "errorMessage";
	public static final String TECHNICALERROR = "TechnicalError";
	public static final String BUSINESSERROR = "BusinessError";

	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String INTEGER = "Integer";
	public static final String EV = "EV_";
	public static final String PV = "PV_";
	public static final String String = "String";
	public static final String BOOLEAN = "Boolean";

	public static final String IS_MARKER = "markerFile";
	public static final String FAILURE = "Failure";
	public final static String TRANSACTION_ID = "transactionId";
	public static final String ROUTE = "Path_Name";
	public static final String SOURCE_OPERATOR = "SourceOperator";
	public static final String MARKER_TYPE = "markerType";
	public static final String BUSINESS_FAIL = "Business_Fail";
	public static final String MULTI_OUTPUT_COMPONENTS = "Multi_Output_Components";
	public static final String EVENT_COUNT= "originalEventsCount";
	public static final String GROUP_INDEX= "Group_Index";

	// default process variable
	public static final String PV_CHANNEL_NAME = "InputChannelName";
	public static final String PV_TECHNICAL_ERROR_CODE = "TechnicalErrorCode";
	public static final String PV_BUSINESS_ERROR_CODE = "BusinessErrorCode";
	public static final String PV_TRANSACTION_ID = "TransactionId";

	public static final String PV_TYPE_CATEGORY_PRIMITIVE = "Primitive";
	public static final String PV_TYPE_CATEGORY_BE = "BE";

	public static final String IS_SPLITTED = "Splitted";
	
	public static final String OUTPUT_REST_CHANNEL = "Rest Output";
	public static final String OUTPUT_REST_CHANNEL_INTEGRATION_OP = "Rest Output Channel Integration";
	
	public static final String AUTH_HEADER = "Authorization";
	
	//Composite BE Properties
	public static final String PROPERTIES = "properties";
	public static final String TYPE = "type";
	public static final String SCALE = "scale";
	public static final String TYPE_VALUE_DATETIME = "DateTime";
	public static final String TYPE_VALUE_NUMBER = "Number";
	public static final String REQUIRED = "required";
	public static final String ITEMS = "items";
	public static final String ATTRIBUTE_DATE_FORMAT = "dateFormat";
	//public static final String NAME = "aliasName";
	public static final String INPUTBE = "inputBe";
	public static final String OUTPUTBE = "outputBe";
	public static final String ALIASNAME = "aliasName";
	public static final String ARRAY_TYPE = "array";
	public static final String OBJECT_TYPE = "object";
	public static final String SUBSTRING = "SubString";
	public static final String TO_NUMBER = "to_Number";
	public static final String TO_DATE = "to_Date";
	public static final String TO_DATE_TIME = "to_DateTime";
	public static final String TO_STRING = "to_String";
	public static final String IS_DATE = "isDate";
	public static final String ROUND = "round";

	
}
