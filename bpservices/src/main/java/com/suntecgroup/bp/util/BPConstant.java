package com.suntecgroup.bp.util;

/**
 * Constant file
 */
public interface BPConstant {

	public static final String SUCCESS_MSG = "Success";
	public static final String FAILURE_MSG = "Failure";
	public static final String PASS = "Pass";
	public static final String FAIL = "Fail";
	public static final String FLOW = "Flow";
	public static final String BP_TYPE = "bp";
	public static final String BP_DELETED_MSG = "BP Flow Deleted Succesfully";
	public static final String SERVICE_NOT_AVL = "Service not avilable.Please try again!";
	public static final String ASSET_SAVED = "Asset Saved successfully";
	public static final String DATA_NOT_FOUND = "No data found!";
	public static final String CONTENT_TYPE = "application/json";
	public static final String TOKEN_EMPTY = "Token is empty!. Pass valid token";
	public static final String VALIDATION_SUCCESS = "validated successfully";
	public static final String VALIDATION_FAILED = "validation failed!";
	public static final String REVIEW_DETAILS_EMPTY = "Review details are empty!";
	public static final String USER_DETAILS_EMPTY = "User details are empty!";
	public static final String ASSET_DETAILS_EMPTY = "Asset details are empty!";
	public static final String BP_COMMIT_DONE = "Committed successfully!";

	// BPConfig Constants
	public static final String BP_CONFIG = "ConfigBP";
	public static final String FUNCTIONAL = "Functional";
	public static final String PROCESS_DESCRIPTION = "ProcessDescription";
	public static final String ENABLE_BOUNDED_EXECUTION = "isEnableBoundedExecution";
	public static final String PROFILEABLE = "isProfileable";
	public static final String HOST = "Host";
	public static final String PORT = "Port";
	public static final String RESTCHANNELENABLED = "RestChannelEnabled";

	// ProcessVariableConstants
	public static final String PROCESS_VARIABLE = "Process Variable";
	public static final String ENTER_VALUE = "Enter Value";
	public static final String NAME = "Name";
	public static final String DESCRIPTION = "Description";
	public static final String TYPE_CATEGORY = "Type_Category";
	public static final String TYPE_NAME = "Type_Name";
	public static final String PRECISION = "Precision";
	public static final String SCALE = "Scale";
	public static final String INT_VALUE = "IntValue";
	public static final String BOOLEAN_VALUE = "BooleanValue";
	public static final String DATE_VALUE = "DateValue";
	public static final String STRING_VALUE = "StringValue";
	public static final String IS_MANDATORY = "IsMandatory";
	public static final String ISPROFILEABLEAT_SOLUTIONS = "isProfileableAtSolutions";
	public static final String ISPROFILEABLEAT_OPERATION = "isProfileableAtOperation";

	// Operator Constants
	public static final String OPERATORS = "Operators";
	public static final String BUSINESS_SETTINGS = "Business Settings";
	public static final String INPUTBETYPE = "InputBEType";
	public static final String OUTPUTBETYPE = "OutputBEType";
	public static final String EVENT_LOGGING = "Event Logging";
	public static final String INPUT_MAPPING = "Input Mapping";
	public static final String TYPE = "Type";
	public static final String OUTPUT_MAPPING = "Output Mapping";
	public static final String DECISIONS = "Decisions";
	public static final String EXCLUSIVE = "Exclusive";
	public static final String DECISION_NAME = "Decision Name";
	public static final String EXPRESSION = "Expression";
	public static final String BUSINESSERRORCODES = "businessErrorCodes";
	public static final String BUSINESSFAILUREFLOWEXIST = "businessFailureFlowExist";

	// Connection Constants
	public static final String CONNECTIONS = "Connections";
	public static final String SMART = "SMART";
	public static final String LINK_PROPERTIES = "LinkProperties";
	public static final String MAPPING = "Mapping";

	// Common
	public static final String MODIFIED = "Modified";
	public static final String ADDED = "Added";
	public static final String DELETED = "Deleted";
	public static final String EMPTY_STRING = "";
	public static final String PROPERTIES = "Properties";
	public static final String VALUE = "Value";
	public static final String COMMENTS = "Comments";
	public static final String BUSINESS_SERVICE_NAME = "Business Service name";
	public static final String API_NAME = "APIName";
	public static final String UI_ATTRIBUTES = "UI Attributes";
	public static final String SELECTED_KEY = "SelectedKey";
	public static final String CONTEXT_VARIABLE = "Context Variable";

	public static final String START = "START";
	public static final String INVOKE_BS = "INVOKE_BS";
	public static final String END = "END";
	public static final String JOIN = "JOIN";
	public static final String DECISION_MATRIX_EXCLUSIVE = "DECISION_MATRIX_EXCLUSIVE";
	public static final String DECISION_MATRIX_INCLUSIVE = "DECISION_MATRIX_INCLUSIVE";
	public static final String INPUT_CHANNEL_INTEGRATION = "INPUT_CHANNEL_INTEGRATION";
	public static final String OUTPUT_CHANNEL_INTEGRATION = "OUTPUT_CHANNEL_INTEGRATION";

	// Data type constants
	public static final String STRING = "String";
	public static final String BOOLEAN = "Boolean";
	public static final String NUMBER = "Number";
	public static final String INPUT_BE_ATTRIBUTE = "I/P BE Attribute";
	public static final String DATE_TIME = "DateTime";

	// DAO Constants
	public static final String ASSET_NAME = "assetName";
	public static final String ASSET_TYPE = "assetType";
	public static final String DEPARTMENT = "department";
	public static final String MODULE = "module";
	public static final String RELEASE = "release";
	public static final String PMS = "pms";
	public static final String STATUS = "status";
	public static final String ARTIFACT_ID = "artifact_id";

	public static final String VERSION = "version";
	public static final String CHECK_OUT_USER = "checkOutUser";
	public static final String IN_REVIEW = "In Review";
	public static final String IN_PROGRESS = "In Progress";
	public static final String REVIEWER_COMMENTS = "reviewerComments";
	public static final String APPROVER_COMMENTS = "approverComments";
	public static final String DESIGNER_COMMENTS = "designerComments";
	public static final String APPROVED = "Approved";
	public static final String REJECTED = "Rejected";
	public static final String ASSET_DETAIL = "assetDetail";

	// File input/output operator
	public static final String BATCHABLE = "batchable";
	public static final String BATCHSIZE = "batchSize";
	public static final String SELECTED = "selected";
	public static final String AUTO_GENERATION = "autoGeneration";
	public static final String AUTO_GENERATE_MAPPING = "autoGenerateMapping";
	public static final String CONTINUOUS = "continuous";
	public static final String HEADER = "header";
	public static final String HAS_HEADER = "hasHeader";
	public static final String HEADER_LINES = "headerLines";
	public static final String FIXED_WIDTH = "fixedWidth";
	public static final String DELIMITED = "delimited";
	public static final String FOOTER = "footer";
	public static final String HAS_FOOTER = "hasFooter";
	public static final String FOOTER_LINES = "footerLines";
	public static final String EVICTION = "Eviction";
	public static final String EVENT_BASED = "eventBased";
	public static final String RECORD_COUNT_BASED = "recordCountBased";
	public static final String TIME_BASED = "timeBased";
	public static final String IDLE_TIME = "idleTime";
	public static final String DURATION = "duration";
	public static final String COUNT = "count";
	public static final String OUTPUTFILENAME = "outputFileName";
	public static final String STATIC_NAME = "staticName";
	public static final String DYNAMIC_NAME = "dynamicName";
	public static final String OFIC_HEADER = "OFCIHeader";
	public static final String ATTRIBUTE_NAME = "attributeName";
	public static final String STARTING_POSITION = "startingPosition";
	public static final String WIDTH = "width";
	public static final String LINE_NUMBER = "lineNumber";
	public static final String DATA_TYPE = "dataType";
	public static final String TRUE_VALUE = "trueValue";
	public static final String FALSE_VALUE = "falseValue";
	public static final String PRECISION_SMALL = "precision";
	public static final String SCALE_SMALL = "scale";
	public static final String DATE_TIME_SMALL = "dateTime";
	public static final String RECORD_DELIMITER = "recordDelimiter";
	public static final String ATTRIBUTE_DELIMTER = "attributeDelimiter";
	public static final String SEGMENT_POSITION = "segmentPosition";
	public static final String BUK = "buk";
	public static final String PARENT = "parent";
	public static final String CURRENT_NODE = "currentNode";
	public static final String PARENT_NODE = "parentNode";
	public static final String DISABLED = "disabled";
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	// Rest input
	public static final String REST_INPUT_CHANNEL_INTEGRATION = "REST_INPUT_CHANNEL_INTEGRATION";
	public static final String REST_OUTPUT_CHANNEL_INTEGRATION = "OUTPUT_REST_CHANNEL_INTEGRATION";
	public static final String REQUEST_PAYLOAD = "payload";

	// Merge
	public static final String MERGE = "MERGE";
	public static final String CONNECTION_NAME = "connectionName";
	public static final String FROM_OPERATOR_KEY = "fromOperatorKey";
	public static final String EXPECTED_INPUT_CHANNEL = "expectedInputChannel";

	// External
	public static final String INVOKE_BS_EXTERNAL = "INVOKE_BS_EXTERNAL";
	public static final String HTTP_METHOD = "httpMethod";
	public static final String SELECTED_INPUT_OPTION = "selectedInputOption";

	// Rest Output
	public static final String API_INPUT = "APIInput";
	public static final String CONTENT_TYPE_EXTERNAL = "ContentType";

}
