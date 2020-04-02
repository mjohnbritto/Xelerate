/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.model.eventlogger.EventLog;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.NifiUtils;

/*
 * This class is for creating a custom NiFi processor to handle the Event Logger post process
 * 
 * @version 1.0 - Decembar 2018
 * @author Thatchana
 */
@SideEffectFree
@Tags({ "posteventlogger,suntec" })
@CapabilityDescription("PostEventLogger processor for business process")
public class PostEventLogger extends AbstractProcessor {

	private ComponentLog LOGGER;
	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;

	public static final PropertyDescriptor INPUT_BE_TYPE = new PropertyDescriptor.Builder()
			.name("Input Business Entity").description("Set input BE Type").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

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

	private Gson gson = null;
	
	private String inputBEType = null;

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		inputBEType = context.getProperty(INPUT_BE_TYPE).evaluateAttributeExpressions().getValue();
		if (inputBEType != null) {
			inputBEType = inputBEType.trim();
		}
	}
	
	@Override
	public void init(final ProcessorInitializationContext context) {
		List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(INPUT_BE_TYPE);
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		this.relationships = Collections.unmodifiableSet(relationships);
		LOGGER = context.getLogger();
		gson = new GsonBuilder().create();
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {

		FlowFile flowFile = session.get();
		if (null == flowFile) {
			return;
		}

		String sessionId = context.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
		sessionId = sessionId.trim();
		session.putAttribute(flowFile, Constants.ATTR_SESSION_ID, sessionId);
		String runNumber = context.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
		runNumber = runNumber.trim();
		session.putAttribute(flowFile, Constants.ATTR_RUN_NUMBER, runNumber);

		FlowFile newFlowfile = session.clone(flowFile);
		InputStream inputStream = null;
		OutputStream outputStream = null;
		JsonReader reader = null;
		JsonWriter writer = null;
		try {
			
			// Do only If BE is configured
			if (!StringUtils.isEmpty(inputBEType)) {
				inputStream = session.read(flowFile);
				outputStream = session.write(newFlowfile);
				reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
				writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));

				// Reading & Writing
				reader.setLenient(true);
				reader.beginArray();
				writer.beginArray();

				while (reader.hasNext()) {
					EventLog eventLog = gson.fromJson(reader, EventLog.class);
					String payLoad = eventLog.getPayload();
					JSONObject jsonObject = new JSONObject(payLoad);
					JsonElement jsonElement = gson.fromJson(jsonObject.toString(), JsonElement.class);
					gson.toJson(jsonElement, writer);
				}
				reader.endArray();
				writer.endArray();
				reader.close();
				writer.close();
			} else {
				try {
					outputStream = session.write(newFlowfile);
					writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
					writer.beginArray();
					writer.endArray();
					writer.close();
				} catch (Exception exception) {
					LOGGER.error(
							"Exception occurred while writing empty data in flowfile :: " + exception.getMessage(), exception);
				}
			}

			session.remove(flowFile);
			route(newFlowfile, REL_SUCCESS, context, session, null, null, null);
		} catch (Exception exception) {
			LOGGER.error("Error occurred :: " + exception.getMessage(), exception);
			try {
				if (null != reader) {
					reader.close();
				}
				if (null != writer) {
					writer.close();
				}
			} catch (IOException ioException) {
				LOGGER.debug("Exception occured while closing the reader&writer :" + ioException.getMessage(),
						ioException);
			}
			session.remove(newFlowfile);

			route(flowFile, REL_FAILURE, context, session, inputBEType, Constants.TECHNICALERROR,
					exception.getMessage());
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != outputStream) {
					outputStream.close();
				}
			} catch (IOException ioException) {
				LOGGER.debug(
						"Exception occured while closing the inputStream&outputStream :" + ioException.getMessage(),
						ioException);
			}
		}
	}

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	@Override
	public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
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
		session.transfer(flowfile, relationship);
		session.commit();
	}
}
