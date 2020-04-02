package com.suntecgroup.nifi.constants;

public class CGConstants {

	public static final String SCHEDULABLE_TASK_COUNT = "1";
	public static final String SCHEDULING_PERIOD = "0 sec";
	public static final String EXECUTION_NODE = "ALL";
	public static final String PENALTY_DURATION = "30 sec";
	public static final String YIELD_DURATION = "1 sec";
	public static final String BULLETIN_LEVEL = "WARN";

	public static final String SUCCESS = "Success";
	public static final String REL_STOPPED = "Stopped";
	public static final String SUCCESS_LOWERCASE = "success";
	public static final String FAILURE_LOWERCASE = "failure";
	public static final String BACKUP = "Backup";
	public static final String REL_MERGED = "merged";
	public static final String ORIGINAL_LOWERCASE = "original";
	public static final String ORIGINAL = "Original";
	public static final String RETRY_RELATIONSHIP = "Retry";
	public static final String NO_RETRY_RELATIONSHIP = "No Retry";
	public static final String UPDATE_STATUS_RELATIONSHIP = "UpdateStatus";
	public static final String RESPONSE = "Response";

	public static final String SESSION_ID = "Session Id";
	public static final String RUN_NUMBER = "Run Number";

	public static final int NEGETIVE_VALUE = -1;
	public static final int DEFAULT_VALUE = 1;

	public static final String IDLE_CONNECTION_MAXPOOL_SIZE = "idleConnectionMaxPoolSize";
	public static final String IDLE_CONNECTION_ALIVE_DURATION = "idleConnectionAliveDuration";
	public static final String SCHEDULING_STRATEGY = "TIMER_DRIVEN";
	public static final String RUN_DURATION = "0 sec";
	public static final String LOSS_TOLERANT = "false";
	public static final String AUTO_TERMINATE_FALSE = "false";
	public static final String AUTO_TERMINATE_TRUE = "true";
	public static final String SSL_CONTEXT_SERVICE = "SSL Context Service";
	public static final String SSL_CONTEXT_SERVICE_VAL = "org.apache.nifi.ssl.SSLContextService";

	public static final String FALSE = "false";
	public static final String TRUE = "True";

	public static final String FALSE_LOWERCASE = "false";
	public static final String TRUE_LOWERCASE = "true";

	public static final String DATETIME = "DateTime";
	// IBS_External
	public static final String INVOKE_BS_EXTERNAL = "invoke_bs_external";
	
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String CHARECTER_URLPATH_SPLIT = "/";
	public static final String CHARECTER_URLQUERY_SPLIT = "?";

	public static final String START = "start";
	public static final String INVOKE_BS = "invoke_bs";
	public static final String JOIN = "join";
	public static final String DECISION_MATRIX_EXCLUSIVE = "decision_matrix_exclusive";
	public static final String DECISION_MATRIX_INCLUSIVE = "decision_matrix_inclusive";
	public static final String FILE_CHANNEL_INTEGRATION_INPUT = "input_channel_integration";
	public static final String FILE_CHANNEL_INTEGRATION_OUTPUT = "output_channel_integration";
	public static final String REST_INPUT_CHANNEL_INTEGRATION = "rest_input_channel_integration";
	public static final String REST_OUTPUT_CHANNEL_INTEGRATION = "output_rest_channel_integration";

	public static final String MERGE = "merge";
	public static final String DECISION_MATRIX_PROCESSOR = "DecisionMatrixProcessor";
	public static final String SMART_CONNECTOR_CATEGORY = "Smart Connector";
	public static final String SMARTCONNECTOR = "smart";
	public static final String END = "end";

	public static final String FUNNEL = "Funnel";
	public static final String DELETED_RECORD_SUCCESS_CODE = "0002";
	public static final String DELETED_RECORD_SUCCESS_DESC = "Deleted record from DB";
	public static final String NO_RECORDS_FOUND_CODE = "0003";
	public static final String NO_RECORDS_FOUND_DESC = "No Records found";
	public static final String UPDATED_RECORD_SUCCESS_CODE = "0004";
	public static final String UPDATED_RECORD_SUCCESS_DESC = "Updated Successfully";
	public static final String CREATE_SUCCESS_CODE = "100";
	public static final String CREATE_SUCCESS_DESC = "Data Inserted Successfully in DB";
	public static final String NULL_MODEL_BS_DETAILS_CODE = "101";
	public static final String NULL_MODEL_BS_DETAILS_DESC = "ModelData is null";
	public static final String NULL_OPERATIONS_CONNECTIONS_CODE = "102";
	public static final String NULL_OPERATIONS_CONNECTIONS_DESC = "DepartmentName/ModuleName/BeName are null::";
	public static final String EMPTY_OPERATIONS_CONNECTIONS_CODE = "103";
	public static final String EMPTY_OPERATIONS_CONNECTIONS_DESC = "Operations/Connectios are empty";
	public static final String EMPTY_BUSINESS_FLOW_LIST = "104";
	public static final String EMPTY_MODIFIED_DATE_CODE = "105";
	public static final String EMPTY_MODIFIED_DATE_DESC = "Modified Date is Empty/Null";
	public static final String BUSINESS_FLOW_LIST_SUCCESS_CODE = "001";
	public static final String BUSINESS_FLOW_LIST_SUCCESS_DESC = "BPFlow List are retrievd successfully";
	public static final String BPEXCEPTION_STATUS_CODE = "110";
	public static final String DBEXCEPTION_STATUS_CODE = "111";
	public static final String EXCEPTION_STATUS_CODE = "101";
	public static final String BPEXCEPTION_STATUS_DESC = "Application Exception";
	public static final String DBEXCEPTION_STATUS_DESC = "DB Exception occured in the application";
	public static final String EXCEPTION_STATUS_DESC = "Application Error.Please try after sometime/Wait for a while to complete the server start process";
	public static final String PROPERTIES_EMPTY_EXCEPTION = "Properties values are empty";
	public static final String BUSINESS_FLOW_LIST_FAILURE_CODE = "002";
	public static final String BUSINESS_FLOW_LIST_FAILURE_DESC = "BPFlow List does not exist. Please verify the given details";
	public static final double X = 231.1534550967972;
	public static final double Y = 691.8751087132823;

	public static final String ADMIN = "ADMIN";
	public static final String RELEASE = "1.1";
	public static final String TIMESTAMP = "2018-09-08";

	public static final String ERROR_CATEGORY = "errorCategory";
	public static final String ERROR_SOURCE = "errorSource";
	public static final String ERROR_DESCRIPTION = "errorDescription";

	public static final String BUSINESS_PROCESS_SETUP = "Business Process Setup";
	public static final String PROCESS_VARIABLE = "Process Variable";
	public static final String NAME = "Name";
	public static final String KEY = "Key";
	public static final String DESCRIPTION = "Description";
	public static final String VALUE = "Value";
	public static final String TYPE = "Type";
	public static final String ATTRIBUTE_TYPE = "Output Attribute";
	public static final String OPERATOR_NAMES = "Operator Names";
	public static final String SELECTED_KEY = "Selected Key";
	public static final String OPERATOR = "Operator";
	public static final String START_OPERATOR = "Start Operator";
	public static final String INVOKEBS_OPERATOR = "InvokeBS Operator";
	public static final String END_OPERATOR = "End Operator";
	public static final String DECISION_MATRIX_EXCLUSIVE_CATEGORY = "Decision Matrix Exclusive";
	public static final String DECISION_MATRIX_INCLUSIVE_CATEGORY = "Decision Matrix Inclusive";
	public static final String INPUT_FILE_CHANNEL_INTEGRATION = "Input File Channel Integration";
	public static final String INPUT_REST_CHANNEL_INTEGRATION = "Input Rest Channel Integration";
	public static final String OUTPUT_FILE_CHANNEL_INTEGRATION = "Output File Channel Integration";
	public static final String OUTPUT_REST_CHANNEL_INTEGRATION = "Output Rest Channel Integration";
	public static final String MERGE_CATEGORY = "Merge";
	public static final String Update_TxnIhttpProcessor = "updateTxnIhttp";
	public static final String BUSINESS_SETTINGS = "Business Settings";
	public static final String INPUTBE_TYPE = "Input BE Type";
	public static final String OUTPUTBE_TYPE = "Output BE Type";
	public static final String PROPERTY = "Property";
	public static final String HEADER = "Header";
	public static final String FOOTER = "Footer";
	public static final String MAPPING = "Mapping";
	public static final String CONTENT = "Content";
	public static final String CONNECTIONS = "Connections";
	public static final String SOURCE = "Source";
	public static final String DESTINATION = "Destination";
	public static final String START_CONNECTION = "Start";
	public static final String INVOKEBS_CONNECTION = "InvokeBS";
	public static final String JOIN_CATEGORY = "Join";
	public static final String END_CONNECTION = "End";
	public static final String INPUT_MAPPING = "Input Mapping";
	public static final String OUTPUT_MAPPING = "Output Mapping";
	public static final String CONTEXT_VARIABLE = "Context Variable";
	public static final String CONTEXT_PARAMETER = "Context Parameter";
	public static final String RATIO = "ratio";
	public static final String DECISIONS = "Decisions";
	public static final String URL_PROPERTY = "url";
	public static final String formatEncodeDecode = "UTF-8";
	public static final String FIXED_WIDTH = "fixedWidth";

	public static final String propertyPenaltyDuration = "penaltyDuration";
	public static final String propertyYieldDuration = "yieldDuration";
	public static final String propertyBulletinLevel = "bulletinLevel";
	public static final String propertySchedulingStrategy = "schedulingStrategy";
	public static final String propertyConcurrentTasks = "concurrentTasks";
	public static final String propertyRunSchedule = "runSchedule";
	public static final String propertyExecution = "Execution";
	public static final String propertyRunDuration = "runDuration";

	public static final String NIFI_CUSTOM_END = "NifiCustomEnd";

	public static final String NIFI_CUSTOM_JOIN = "NifiCustomJoin";
	public static final String NIFI_CUSTOM_POST_INVOKEBS = "NifiCustomPostInvokeBS";
	public static final String NIFI_CUSTOM_PRE_IBS = "NifiCustomPreIBS";
	public static final String NIFI_CUSTOM_START = "NifiCustomStart";
	public static final String NIFI_DECISION_MATRIX_INCLUSIVE = "NifiDecisionMatrixInclusive";
	public static final String NIFI_DECISION_MATRIX_EXCLUSIVE = "NifiDecisionMatrixExclusive";
	public static final String NIFI_INVOKE_HTTP = "NifiInvokeHttp";
	public static final String NIFI_PP_INVOKE_HTTP = "NifiPPInvokeHttp";
	public static final String NIFI_CUSTOM_INVOKE_HTTP = "NifiCustomInvokeHttp";

	public static final String NIFI_LISTEN_HTTP = "NifiListenHTTP";
	public static final String NIFI_SMART_CONNECTOR = "NifiSmartConnector";
	public static final String NIFI_FAILURE = "NifiFailure";
	public static final String NIFI_PRE_EVENT_LOG = "NifiPreEventLog";
	public static final String NIFI_POST_EVENT_LOG = "NifiPostEventLog";
	public static final String NIFI_UPDATE_ATTR = "NifiUpdateAttr";
	public static final String NIFI_UPDATE_START_IHTTP = "NifiStartUpdateIHttp";
	public static final String NIFI_UPDATE_FAILURE_IHTTP = "NifiFailureUpdateIHttp";
	public static final String NIFI_UPDATE_END_IHTTP = "NifiEndUpdateIHttp";
	public static final String NIFI_GET_FILE = "NifiGetFile";
	public static final String NIFI_PUT_FILE = "NifiPutFile";
	public static final String NIFI_CUSTOM_FILE_INPUT_CI = "NifiCustomFileChannelIntegrationInput";
	public static final String NIFI_CUSTOM_FILE_OUTPUT_CI = "NifiCustomFileChannelIntegrationOutput";
	public static final String NIFI_CUSTOM_REST_INPUT_CI = "NifiCustomRestChannelIntegrationInput";
	public static final String NIFI_CUSTOM_REST_OUTPUT_CI = "NifiCustomRestChannelIntegrationOutput";

	public static final String NIFI_CUSTOM_PRE_MERGE = "NifiCustomPreMerge";
	public static final String NIFI_CUSTOM_POST_MERGE = "NifiCustomPostMerge";
	
	public static final String NIFI_CUSTOM_KAFKA_CONSUMER = "NifiCustomKafkaConsumer";

	public static final String[] OPERATORS_IN_METACONFIG = { "CODEGEN_OP_DECISION_MATRIX_INCLUSIVE",
			"CODEGEN_OP_DECISION_MATRIX_EXCLUSIVE", "CODEGEN_OP_END", "CODEGEN_OP_INVOKE_BS", "CODEGEN_OP_SMART",
			"CODEGEN_OP_START", "CODEGEN_OP_INPUT_CHANNEL_INTEGRATION", "CODEGEN_OP_OUTPUT_CHANNEL_INTEGRATION",
			"CODEGEN_OP_INVOKE_BS_EXTERNAL", "CODEGEN_OP_MERGE", "CODEGEN_OP_JOIN",
			"CODEGEN_OP_REST_INPUT_CHANNEL_INTEGRATION", "CODEGEN_OP_OUTPUT_REST_CHANNEL_INTEGRATION" };
	public static final String OPERATOR_NAME_PREPEND = "CODEGEN_OP_";
	
	public static final String[] CHANNEL_OPERATORS_TYPES = { FILE_CHANNEL_INTEGRATION_INPUT,
			FILE_CHANNEL_INTEGRATION_OUTPUT, REST_INPUT_CHANNEL_INTEGRATION, REST_OUTPUT_CHANNEL_INTEGRATION };

	public static final String BLANK = "";
	public static final String STOPPED = "STOPPED";
	public static final String URL = "URL";
	public static final String POST_HTTPMETHOD = "POST";
	public static final String REMOTE_URL = "Remote URL";
	public static final String TRANSACTION_URL = "Transaction URL";
	public static final String HTTP_METHOD = "HTTP Method";
	public static final String HTTP_GET_METHOD = "GET";
	public static final String PROXY_HOST = "Proxy Host";
	public static final String PROXY_PORT = "Proxy Port";
	public static final String MESSAGE_BODY = "Send Message Body";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENTTYPE_APPLICATION_JSON = "application/json";
	public static final String CONTENTTYPE_DEFAULT = "${mime.type}";
	public static final String ATTRIBUTES_TO_SEND = "Attributes to Send";
	public static final String PAGINATION_ENABLED = "Pagination Enabled";
	public static final String TOTAL_NO_OF_PAGES = "Total number of pages";
	public static final String DEFAULT_PAGE_COUNT = "1";
	public static final String PROCESSOR_NAME_PROP_VALUE = "processorName";

	public static final String FAILURE = "Failure";

	public static final String BUSINESS_ERRORS = "Business Errors";

	public static final String PATH_PARAM = "Path param";
	public static final String QUERY_PARAM = "Query param";
	public static final String BUSINESS_ERROR_CODES = "Business Error Codes";
	public static final String BUSINESS_ERRORCODES = "BusinessErrorCodes";
	public static final String IBS_REMOTE_URL = "remoteUrl";
	public static final String MERGE_REMOTE_URL = "remoteUrl";

	public static final String SETTINGS = ".SETTINGS";
	public static final String RESTAPIPORT = ".restApiPort";
	public static final String REST_API_BASEPATH = ".restApiBasepath";
	public static final String REST_CHANNEL_ENABLED = ".restChannelEnabled";
	public static final String EQUALS = "=";

	public static final String CONF_PROPERTIES = ".conf.properties";
	public static final String JSON_FILE_EXTENSION = ".json";

	public static final String CONTEXT_PARAMETER_PARAM = "Context Parameter";
	public static final String INPUT_BE = "inputBe";
	public static final String CONTEXT_PARAMETERS_JSON = "contextParameters";
	public static final String INPUT_MAPPING_PARAM = "inputMapping";
	public static final String INPUT_MAPPING_PARAMETER_PROPERTY = "Input Mapping Parameter";
	public static final String INPUT_BUSINESS_ENTITY_PROPERTY = "Input Business Entity";
	public static final String SERVICE_NAME = "Service Name";
	public static final String API_NAME = "Api Name";
	public static final String OPERATOR_NAME = "Operator Name";
	public static final String MERGE_SOURCE = "Merge Source";
	public static final String PATH_NAME = "Path_Name";
	public static final String INVOKE_BS_EXTERNAL_STARTS = "InvokeExternal_";
	public static final String INPUT_BE_DEFINITION = "InputBE Definition";
	public static final String INPUT_BE_BUK_ATTRIBUTES = " Input BE BUK Attributes";
	public static final String BASIC_AUTHENTICATION_USERNAME = "Basic Authentication Username";
	public static final String BASIC_AUTHENTICATION_PASSWORD = "Basic Authentication Password";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	
	//Kafka consumer processor properties
	public static final String KAFKA_BROKERS = "bootstrap.servers";
	public static final String SECURITY_PROTOCOL = "security.protocol";
	public static final String HONOR_TRANSACTIONS = "honor-transactions";
	public static final String OFFSET_RESET = "auto.offset.reset";
	public static final String KEY_ATTRIBUTE_ENCODING = "key-attribute-encoding";
	public static final String COMMUNICATIONS_TIMEOUT = "Communications Timeout";

}