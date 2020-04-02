package com.suntecgroup.custom.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.channelintegration.OperatorStats;
import com.suntecgroup.custom.processor.utils.CommonUtils;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * This class is for creating a custom NiFi processor to handle the Rest Input
 * Channel Integration operator
 * 
 * @version 13 May 2019
 * @author Ramesh Kumar B
 */
@SideEffectFree
@Tags({ "RestInput, Channel Integration" })
@CapabilityDescription("Rest Input Processor for Channel Integration")
public class InputRestChannelProcessor extends AbstractProcessor {

	private ComponentLog logger;
	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;
	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	private final AtomicReference<OkHttpClient> okHttpClientAtomicReference = new AtomicReference<>();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor OUTPUT_BE_NAME = new PropertyDescriptor.Builder().name("Output BE Name")
			.description("Output BE Name").required(false).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CI_NAME = new PropertyDescriptor.Builder().name("CI Name")
			.description("Channel Integration Name").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor PROCESS_VARIABLE = new PropertyDescriptor.Builder().name("Process Variables")
			.description("Set Process variables").required(false).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor REMOTE_URL = new PropertyDescriptor.Builder().name("Remote URL")
			.description("Remote URL").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description("Success relationship").build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();
	
	public static final PropertyDescriptor IDLE_CONNECTION_MAXPOOL_SIZE = new PropertyDescriptor.Builder().name("idleConnectionMaxPoolSize")
			.description("Http connection pool size").required(true)
			.expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();
	
	public static final PropertyDescriptor IDLE_CONNECTION_ALIVE_DURATION = new PropertyDescriptor.Builder().name("idleConnectionAliveDuration")
			.description("Http connection alive duration").required(true)
			.expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	@Override
	public void init(final ProcessorInitializationContext context) {

		List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		properties.add(OUTPUT_BE_NAME);
		properties.add(CI_NAME);
		properties.add(PROCESS_VARIABLE);
		properties.add(REMOTE_URL);
		properties.add(IDLE_CONNECTION_MAXPOOL_SIZE);
		properties.add(IDLE_CONNECTION_ALIVE_DURATION);
		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		this.relationships = Collections.unmodifiableSet(relationships);
		logger = context.getLogger();
	};

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}
	
	private String transactionStatusUrl = null;
	private String remoteURL = null;
	private ObjectMapper mapper = null;
    private String ciName = null;
    private String outputBeType = null;

	@OnScheduled
	public void onScheduled(final ProcessContext processContext) {
		okHttpClientAtomicReference.set(null);
		long connectionAliveDuration = processContext.getProperty(IDLE_CONNECTION_ALIVE_DURATION).asLong();
		int maxPoolSize = processContext.getProperty(IDLE_CONNECTION_MAXPOOL_SIZE).asInteger();
		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder();
		okHttpClientBuilder.connectionPool(new ConnectionPool(maxPoolSize, connectionAliveDuration, TimeUnit.SECONDS));
		okHttpClientBuilder.retryOnConnectionFailure(false);
		okHttpClientAtomicReference.set(okHttpClientBuilder.build());
		remoteURL = processContext.getProperty(REMOTE_URL).evaluateAttributeExpressions().getValue();
	    outputBeType = processContext.getProperty(OUTPUT_BE_NAME).evaluateAttributeExpressions().getValue();
	    ciName = processContext.getProperty(CI_NAME).evaluateAttributeExpressions().getValue();
		transactionStatusUrl = remoteURL + "/bpruntime/sessionmanager/updateOperatorStats";
		mapper = new ObjectMapper();
	}
	
	@Override
	public void onTrigger(final ProcessContext processContext, final ProcessSession processSession)
			throws ProcessException {

		FlowFile flowFileObj = processSession.get();
	    if (null == flowFileObj) {
            return;
        }
		FlowFile flowFileOutput = processSession.clone(flowFileObj);
		String entityObjectString = "";
		int totalRequestsCount = 1;
		int totalRecordsCount = 0;

		try {

			CommonUtils.validateSessionId(processContext, processSession, null, SESSION_ID, logger);
			CommonUtils.validateRunNumber(processContext, processSession, null, RUN_NUMBER, logger);

			String sessionId = processContext.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
			sessionId = sessionId.trim();
			String runNumber = processContext.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
			runNumber = runNumber.trim();
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			processSession.exportTo(flowFileObj, bytes);
			final String contents = bytes.toString();
			JSONObject responseObject = new JSONObject(contents);
			JSONArray entitiesArray = null;
			InputStream flowFileInputStream = null;
			String myJsonObject = responseObject.get("context").toString();
			JSONObject pvObject = new JSONObject(myJsonObject);
			
			if (null != responseObject)
				entitiesArray = (JSONArray) responseObject.get("entities");
			entityObjectString = entitiesArray.toString();
			flowFileInputStream = new ByteArrayInputStream(entityObjectString.getBytes());
			processSession.importFrom(flowFileInputStream, flowFileOutput);
			processSession.remove(flowFileObj);
			processSession.putAttribute(flowFileOutput, "beName", outputBeType);
			processSession.putAttribute(flowFileOutput, "channelId", ciName);
			processSession.putAttribute(flowFileOutput, "channelName", ciName);
			
			 for (Object key : pvObject.keySet()) {
			        //based on you key types
			        String keyStr = (String)key;
			        Object keyvalue = pvObject.get(keyStr);
			        processSession.putAttribute(flowFileOutput, keyStr,keyvalue.toString());
			        
			 }
			/*for (Map.Entry<String, Object> processMapObj : processMap.entrySet()) {
				processSession.putAttribute(flowFileOutput, processMapObj.getKey(),
						processMapObj.getValue().toString());
			}*/
			updateOperatorStats(sessionId, runNumber, ciName, totalRequestsCount, totalRecordsCount);
			processSession.transfer(flowFileOutput, REL_SUCCESS);
			processSession.commit();
			// }

		} catch (NifiCustomException nifiCustomException) {
			logger.error("Error occurred at RestChannelInput :: " + nifiCustomException.getMessage(),
					nifiCustomException);
			processSession.remove(flowFileOutput);
			processSession.transfer(flowFileObj, REL_FAILURE);
			processSession.commit();
			return;
		} catch (Exception e) {
			logger.error("Error occurred at RestChannelInput :: " + e.getMessage(), e);
			processSession.remove(flowFileOutput);
			processSession.transfer(flowFileObj, REL_FAILURE);
			processSession.commit();
			return;
		}
	}

	public void updateOperatorStats(String sessionId, String runNumber, String operatorName, int totalRequestsCount,
			int totalRecordsCount) {
	        OperatorStats operatorStats = new OperatorStats();
			operatorStats.setSessionId(sessionId);
			operatorStats.setRunNumber(runNumber);
			operatorStats.setOperatorName(operatorName);
			operatorStats.setTotalRequestsCount(totalRequestsCount);
			operatorStats.setTotalRecordsCount(totalRecordsCount);
			Request.Builder requestBuilder = new Request.Builder();
			requestBuilder = requestBuilder.url(transactionStatusUrl);
			try {
				requestBuilder = requestBuilder.post(RequestBody.create(MediaType.parse(DEFAULT_CONTENT_TYPE), mapper.writeValueAsString(operatorStats)));
			} catch (JsonProcessingException e1) {
				logger.error("Error occurred at parsing operator stats :: {}",new Object[]{e1.getMessage()}, e1);
			}
			OkHttpClient okHttpClient = okHttpClientAtomicReference.get();
			ResponseBody responseBody = null;
			try (Response responseHttp = okHttpClient.newCall(requestBuilder.build()).execute()) {
				responseBody = responseHttp.body();
				if(responseHttp.code() != 200) {
					logger.error("Exception while updating the status :: {}", new Object[]{responseHttp.code()});
				}
			} catch (IOException e) {
				logger.error("Error occurred at updateOperatorStatistics :: {}", new Object[]{e.getMessage()}, e);
			} finally {
				if(responseBody != null) {
					responseBody.close();
				}
			}}

	public Map<String, Object> updatePV(Map<String, Object> processMapBP, Map<String, Object> runtimePV) {

		for (Map.Entry<String, Object> entry : runtimePV.entrySet()) {
			processMapBP.put(entry.getKey(), entry.getValue());

		}
		return processMapBP;
	}

}
