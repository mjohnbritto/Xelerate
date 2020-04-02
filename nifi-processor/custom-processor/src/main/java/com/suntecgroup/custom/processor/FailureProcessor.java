/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.model.buk.EventBuk;
import com.suntecgroup.custom.processor.utils.CommonUtils;
import com.suntecgroup.custom.processor.utils.Constants;

/**
 * This class is for creating a custom NiFi processor to handle the failure
 * operator
 * 
 * @version 04 Dec 2018
 * @author Sravya P
 */
@SideEffectFree
@Tags({ "failure,suntec,fail" })
@CapabilityDescription("Failure processor in the business process")
public class FailureProcessor extends AbstractProcessor {
	private ComponentLog LOGGER;
	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;
	private Gson gson = null;

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();

	public static final Relationship REL_UPDATE_STATUS = new Relationship.Builder().name("UpdateStatus")
			.description("Updating the transaction counts").build();

	@Override
	public void init(final ProcessorInitializationContext context) {
		List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
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

		String sessionId;
		int runNumber;
		String transactionId;
		try {
			CommonUtils.validateSessionId(context, session, flowfile, SESSION_ID, LOGGER);
			CommonUtils.validateRunNumber(context, session, flowfile, RUN_NUMBER, LOGGER);
			CommonUtils.validateTransactionId(flowfile);

			sessionId = context.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
			sessionId = sessionId.trim();
			String strRunNumber = context.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
			strRunNumber = strRunNumber.trim();
			runNumber = Integer.parseInt(strRunNumber);
			transactionId = flowfile.getAttribute("transactionId").toString();
		} catch (NifiCustomException nifiCustomException) {
			LOGGER.error("Error occurred ::" + nifiCustomException.getMessage(), nifiCustomException);
			session.transfer(flowfile, REL_FAILURE);
			session.commit();
			return;
		} catch (Exception exception) {
			LOGGER.error("Error occurred ::" + exception.getMessage(), exception);
			session.transfer(flowfile, REL_FAILURE);
			session.commit();
			return;
		}

		// Update Counts & Failure details starts here
		FlowFile tranactionStatusFlowFile = null;
		try {
			int eventsCount;
			if (null != flowfile.getAttribute("originalEventsCount")) {
				eventsCount = Integer.parseInt(flowfile.getAttribute("originalEventsCount"));
			} else {
				eventsCount = 0;
			}
			tranactionStatusFlowFile = session.create();
			OutputStream outputStream = session.write(tranactionStatusFlowFile);
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			JSONObject jsonObject = new JSONObject();
			// data for updating transaction count
			jsonObject.put(Constants.ATTR_SESSION_ID, sessionId);
			jsonObject.put(Constants.ATTR_RUN_NUMBER, runNumber);
			jsonObject.put("transactionId", transactionId);
			jsonObject.put("source", "failure");
			jsonObject.put("eventsCount", eventsCount);

			// data for updating failed transaction
			String operatorName = (null != flowfile.getAttribute(Constants.OPERATORNAME))
					? flowfile.getAttribute(Constants.OPERATORNAME).trim() : "";
			String errorType = (null != flowfile.getAttribute(Constants.ERRORTYPE))
					? flowfile.getAttribute(Constants.ERRORTYPE).trim() : "";
			String bukString = (null != flowfile.getAttribute(Constants.INPUT_BUK))
					? flowfile.getAttribute(Constants.INPUT_BUK).trim() : "[]";
			List<EventBuk> eventBuk = gson.fromJson(bukString, List.class);
			String errorMessage = (null != flowfile.getAttribute(Constants.ERRORMESSAGE))
					? flowfile.getAttribute(Constants.ERRORMESSAGE).trim() : "";
			List<String> errorMessages = new ArrayList<String>();
			if (errorMessage.indexOf('|') > -1) {
				errorMessages = Arrays.asList(errorMessage.split("\\|"));
			} else {
				errorMessages.add(errorMessage);
			}

			jsonObject.put("flowfileUUID", flowfile.getAttribute("uuid"));
			jsonObject.put("buk", eventBuk);
			jsonObject.put(Constants.BENAME, flowfile.getAttribute(Constants.BENAME));
			jsonObject.put(Constants.OPERATORNAME, operatorName);
			jsonObject.put(Constants.ERRORTYPE, errorType);
			jsonObject.put(Constants.ERRORMESSAGE, errorMessages);

			JsonElement jsonElement = gson.fromJson(jsonObject.toString(), JsonElement.class);
			gson.toJson(jsonElement, writer);
			writer.close();
			outputStream.close();
			session.transfer(tranactionStatusFlowFile, REL_UPDATE_STATUS);
			// Update Counts & Failure details end here
		} catch (Exception exception) {
			session.remove(tranactionStatusFlowFile);
			LOGGER.error("Exception occurred while updating transaction status :: " + exception.getMessage(), exception);
		}
		session.transfer(flowfile, REL_FAILURE);
		session.commit();
	}

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	@Override
	public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}
}
