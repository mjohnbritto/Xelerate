/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.apache.nifi.processor.util.StandardValidators;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.startandend.ProcessVariable;
import com.suntecgroup.custom.processor.utils.CommonUtils;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.NifiUtils;

/*
 * This class is for creating a custom NiFi processor to handle the start operator
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@SideEffectFree
@Tags({ "start,suntec" })
@CapabilityDescription("Start processor for business process")
public class StartProcessor extends AbstractProcessor {

	private ComponentLog LOGGER;
	private String bpMetaDataBEName;
	private String bpMetaDataChannelName;
	private String startOprInputBEType;
	private Gson gson = null;
	private String bukAttributes;

	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;

	public static final PropertyDescriptor PROCESS_VARIABLE = new PropertyDescriptor.Builder().name("Process Variables")
			.description("Set Process variables").required(true).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor INPUT_BE_TYPE = new PropertyDescriptor.Builder()
			.name("Input Business Entity").description("Set input BE Type").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor INPUT_BE_BUK_ATTRIBUTES = new PropertyDescriptor.Builder()
			.name("Input BE BUK Attributes").description("Input BE BUK attributes array").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor EVENT_LOGGING = new PropertyDescriptor.Builder().name("Event Logging")
			.description("Log the payload and file attribute in remote server.").required(true).defaultValue("true")
			.allowableValues("true", "false").addValidator(StandardValidators.BOOLEAN_VALIDATOR).build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description("Success relationship").build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();

	public static final Relationship REL_UPDATE_STATUS = new Relationship.Builder().name("UpdateStatus")
			.description("Updating the transaction counts").build();

	@Override
	public void init(final ProcessorInitializationContext context) {
		List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(PROCESS_VARIABLE);
		properties.add(INPUT_BE_TYPE);
		properties.add(INPUT_BE_BUK_ATTRIBUTES);
		properties.add(EVENT_LOGGING);
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		relationships.add(REL_UPDATE_STATUS);
		this.relationships = Collections.unmodifiableSet(relationships);
		LOGGER = context.getLogger();
		gson = new GsonBuilder().create();
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
		FlowFile flowfile = session.get();
		if (null == flowfile) {
			return;
		}

		
		List<ProcessVariable> defaultProcessVariableList = null;
		try {
			final String processVarJsonStr = context.getProperty(PROCESS_VARIABLE).evaluateAttributeExpressions()
					.getValue();
			defaultProcessVariableList = CommonUtils.convertJsonStringToJava(processVarJsonStr, LOGGER);
			if (defaultProcessVariableList == null || defaultProcessVariableList.size() < 1) {
				throw new NifiCustomException("default process variable is null");
			}
		} catch (NifiCustomException nifiCustomException) {
			LOGGER.error("Error occurred :: " + nifiCustomException.getMessage(), nifiCustomException);
			route(flowfile, REL_FAILURE, context, session, startOprInputBEType, Constants.TECHNICALERROR,
					nifiCustomException.getMessage());
			return;
		} catch (NumberFormatException exception) {
			LOGGER.error("Error occurred :: " + exception.getMessage(), exception);
			route(flowfile, REL_FAILURE, context, session, startOprInputBEType, Constants.TECHNICALERROR,
					exception.getMessage());
			return;
		} catch (Exception exception) {
			LOGGER.error("Error occurred :: " + exception.getMessage(), exception);
			route(flowfile, REL_FAILURE, context, session, startOprInputBEType, Constants.TECHNICALERROR,
					exception.getMessage());
			return;
		}

		// Validation starts here
		int eventsCount = 0;
		String transactionId;
		try {
			CommonUtils.validateSessionId(context, session, flowfile, SESSION_ID, LOGGER);
			CommonUtils.validateRunNumber(context, session, flowfile, RUN_NUMBER, LOGGER);

			String sessionId = context.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
			sessionId = sessionId.trim();
			String strRunNumber = context.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
			strRunNumber = strRunNumber.trim();
			// If the flow file already contains the transaction id then start
			// operator should not generate new one.
			transactionId = flowfile.getAttribute(Constants.TRANSACTION_ID);
			if (StringUtils.isEmpty(transactionId)) {
				transactionId = generateTransactionId();
				session.putAttribute(flowfile, Constants.TRANSACTION_ID, transactionId);
			}

			// BE metadata validation
			bpMetaDataBEName = flowfile.getAttribute("beName") != null ? flowfile.getAttribute("beName") : "";
			bpMetaDataChannelName = flowfile.getAttribute("channelName") != null ? flowfile.getAttribute("channelName")
					: "";
			if (!CommonUtils.extractAndValidateBEMetaData(flowfile, bpMetaDataChannelName)) {
				throw new NifiCustomException("Business entity metadata validation failed!");
			}

			// BE validation
			if (!StringUtils.isEmpty(startOprInputBEType)
					&& !(validateInputBEType(bpMetaDataBEName, startOprInputBEType))) {
				LOGGER.error("Failed due to input BE type mismatched!");
				route(flowfile, REL_FAILURE, context, session, startOprInputBEType, Constants.TECHNICALERROR,
						"Failed due to input BE type mismatched");
				return;
			} else if (!isBEDataValidJsonArray(session, flowfile)) {
				LOGGER.error("Failed due to invalid input BE format!");
				route(flowfile, REL_FAILURE, context, session, startOprInputBEType, Constants.TECHNICALERROR,
						"Failed due to invalid input BE format");
				return;
			}

			FlowFile tranactionStatusFlowFile = null;

			// Only If BE is configured do the BUK validation
			if (!StringUtils.isBlank(startOprInputBEType)) {
				CommonUtils.validateBUK(session, flowfile, bukAttributes, LOGGER);
				eventsCount = CommonUtils.getEventsCount(session, flowfile, LOGGER);
			}

			session.putAttribute(flowfile, "originalEventsCount", Integer.toString(eventsCount));
			// Update Counts starts here
			try {
				tranactionStatusFlowFile = session.create();
				OutputStream outputStream = session.write(tranactionStatusFlowFile);
				JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.ATTR_SESSION_ID, sessionId);
				jsonObject.put(Constants.ATTR_RUN_NUMBER, strRunNumber);
				jsonObject.put("transactionId", transactionId);
				jsonObject.put("source", "add");
				jsonObject.put("eventsCount", eventsCount);
				JsonElement jsonElement = gson.fromJson(jsonObject.toString(), JsonElement.class);
				gson.toJson(jsonElement, writer);
				writer.close();
				outputStream.close();
				session.transfer(tranactionStatusFlowFile, REL_UPDATE_STATUS);
			} catch (Exception exception) {
				session.remove(tranactionStatusFlowFile);
				LOGGER.error("Exception occurred while updating transaction status :: " + exception.getMessage(),
						exception);
			}
			// Update Counts end here

		} catch (NifiCustomException nifiCustomException) {
			LOGGER.error("Error occurred :: " + nifiCustomException.getMessage(), nifiCustomException);
			route(flowfile, REL_FAILURE, context, session, startOprInputBEType, Constants.TECHNICALERROR,
					nifiCustomException.getMessage());
			return;
		} catch (Exception exception) {
			LOGGER.error("Error occurred :: " + exception.getMessage(), exception);
			route(flowfile, REL_FAILURE, context, session, startOprInputBEType, Constants.TECHNICALERROR,
					exception.getMessage());
			return;
		}

		// Process variable validation
		// START: Update the process variable values at runtime from input data
		List<ProcessVariable> runtimeProcessVariableList = defaultProcessVariableList;
		Map<String, String> flowfileAttributes = flowfile.getAttributes();
		try {
			runtimeProcessVariableList = CommonUtils.readRuntimeProcessVariable(runtimeProcessVariableList,
					flowfileAttributes, bpMetaDataChannelName, transactionId, LOGGER);

			if (!CommonUtils.validateProcessVariable(runtimeProcessVariableList, LOGGER)) {
				throw new NifiCustomException(
						"Process variable validation failed due to mandatory process variable is empty!");
			}
		} catch (NifiCustomException nifiCustomException) {
			LOGGER.error("Error occurred :: " + nifiCustomException.getMessage(), nifiCustomException);
			route(flowfile, REL_FAILURE, context, session, startOprInputBEType, Constants.TECHNICALERROR,
					nifiCustomException.getMessage());
			return;
		} catch (Exception exception) {
			LOGGER.error("Error occurred ::" + exception.getMessage(), exception);
			route(flowfile, REL_FAILURE, context, session, startOprInputBEType, Constants.TECHNICALERROR,
					exception.getMessage());
			return;
		}
		// END: Update the process variable values at runtime from input data

		// Success flow starts here
		for (ProcessVariable singleProcessVariable : runtimeProcessVariableList) {
			session.putAttribute(flowfile, singleProcessVariable.getName(), singleProcessVariable.toJsonString());
		}
		// Write empty data when there's no BE configured - Starts here
		if (StringUtils.isEmpty(startOprInputBEType)) {
			try {
				OutputStream outputStream = session.write(flowfile);
				JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
				writer.beginArray();
				writer.endArray();
				writer.close();
				outputStream.close();
			} catch (Exception exception) {
				LOGGER.error("Exception occurred while writing empty data in flowfile :: " + exception.getMessage(),
						exception);
			}
		}
		// Write empty data when there's no BE configured - Ends here

		route(flowfile, REL_SUCCESS, context, session, null, null, null);

	}// close of OnTrigger method

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	@Override
	public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		startOprInputBEType = context.getProperty(INPUT_BE_TYPE).evaluateAttributeExpressions().getValue();
		if (startOprInputBEType != null) {
			startOprInputBEType = startOprInputBEType.trim();
		}
		// Only If BE is configured do the BUK validation
		if (!StringUtils.isBlank(startOprInputBEType)) {
			bukAttributes = context.getProperty(INPUT_BE_BUK_ATTRIBUTES).evaluateAttributeExpressions()
					.getValue();
		}
	}

	private boolean validateInputBEType(String bpInputBEType, String srtopInputBEType) {
		if (bpInputBEType.equalsIgnoreCase(srtopInputBEType)) {
			return true;
		}
		return false;
	}

	private boolean isBEDataValidJsonArray(final ProcessSession session, final FlowFile flowfile) {
		StringWriter writer = new StringWriter();
		session.read(flowfile, new InputStreamCallback() {
			@Override
			public void process(final InputStream in) throws IOException {
				IOUtils.copy(in, writer, "UTF-8");
			}
		});
		String inputBEContent = writer.toString();
		return CommonUtils.isDataValidJsonArray(inputBEContent, LOGGER);
	}

	private String generateTransactionId() {
		return UUID.randomUUID().toString();
	}

	/*
	 * This method is to route the flow on specific relationship
	 */
	private void route(FlowFile flowfile, Relationship relationship, final ProcessContext context,
			final ProcessSession session, String beName, String errorType, String errorMessage) {
		if (StringUtils.equalsIgnoreCase(REL_FAILURE.getName(), relationship.getName())) {
			// Update failure details in flowfile attributes
			flowfile = NifiUtils.updateFailureDetails(context, session, flowfile, beName, errorType, errorMessage);
		}
		// remove unwanted attributes
		session.removeAttribute(flowfile, "channelId");
		session.removeAttribute(flowfile, "channelName");
		session.transfer(flowfile, relationship);
		session.commit();
	}
}
