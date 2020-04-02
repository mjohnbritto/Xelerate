/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.apache.nifi.processor.util.StandardValidators;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.utils.Constants;

/*
 * This class is for creating a custom NiFi processor to handle the end operator
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@SideEffectFree
@Tags({ "end,suntec" })
@CapabilityDescription("End processor in the business process")
public class EndProcessor extends AbstractProcessor {

	private ComponentLog LOGGER;
	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;
	private Gson gson = null;
	private boolean eventLogFlag = false;
	private String outputBEType = null;
	

	public static final PropertyDescriptor OUTPUT_BE_TYPE = new PropertyDescriptor.Builder()
			.name("Output Business Entity").description("Set output BE type").required(false)
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

	public static final Relationship REL_LOGEVENT = new Relationship.Builder().name("LogEvent")
			.description("Log Event relationship").build();

	public static final Relationship REL_UPDATE_STATUS = new Relationship.Builder().name("UpdateStatus")
			.description("Updating the transaction counts").build();

	@Override
	public void init(final ProcessorInitializationContext context) {
		List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(OUTPUT_BE_TYPE);
		properties.add(EVENT_LOGGING);
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		relationships.add(REL_LOGEVENT);
		relationships.add(REL_UPDATE_STATUS);
		this.relationships = Collections.unmodifiableSet(relationships);
		LOGGER = context.getLogger();
		gson = new GsonBuilder().create();
	}
	
	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		outputBEType = context.getProperty(OUTPUT_BE_TYPE).evaluateAttributeExpressions().getValue();
		if (outputBEType != null) {
			outputBEType = outputBEType.trim();
		}
		eventLogFlag = Boolean
				.parseBoolean(context.getProperty(EVENT_LOGGING).evaluateAttributeExpressions().getValue());
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
		FlowFile flowfile = session.get();
		
		if (null == flowfile) {
			return;
		}
		
		boolean isMarker = Boolean.parseBoolean(flowfile.getAttribute(Constants.IS_MARKER));
		if (isMarker) {
			session.remove(flowfile);
			return;
		}

		session.putAttribute(flowfile, "outputBEName", outputBEType);
		String sessionId = context.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
		sessionId = sessionId.trim();
		session.putAttribute(flowfile, Constants.ATTR_SESSION_ID, sessionId);
		String strRunNumber = context.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
		int runNumber = Integer.parseInt(strRunNumber);
		session.putAttribute(flowfile, Constants.ATTR_RUN_NUMBER, String.valueOf(runNumber));

		// Update Counts starts here
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
			jsonObject.put(Constants.ATTR_SESSION_ID, sessionId);
			jsonObject.put(Constants.ATTR_RUN_NUMBER, runNumber);
			jsonObject.put("transactionId", flowfile.getAttribute("transactionId").toString());
			jsonObject.put("source", "success");
			jsonObject.put("eventsCount", eventsCount);
			JsonElement jsonElement = gson.fromJson(jsonObject.toString(), JsonElement.class);
			gson.toJson(jsonElement, writer);
			writer.close();
			outputStream.close();
			session.transfer(tranactionStatusFlowFile, REL_UPDATE_STATUS);
		} catch (IOException exception) {
			session.remove(tranactionStatusFlowFile);
			LOGGER.error("Exception occurred while updating transaction status :: " + exception.getMessage(), exception);
		}
		// Update Counts end here

		// Write empty data when there's no BE configured - Starts here
		if (StringUtils.isEmpty(outputBEType)) {
			try {
				OutputStream outputStream = session.write(flowfile);
				JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
				writer.beginArray();
				writer.endArray();
				writer.close();
				outputStream.close();
			} catch (Exception exception) {
				LOGGER.error("Exception occurred while writing empty data in flowfile :: " + exception.getMessage(), exception);
			}
		}
		// Write empty data when there's no BE configured - Ends here

		
		if (eventLogFlag) {
			FlowFile logFlowfile = session.clone(flowfile);
			session.transfer(logFlowfile, REL_LOGEVENT);
		}
		session.transfer(flowfile, REL_SUCCESS);

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
