/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2019
 */

package com.suntecgroup.custom.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import com.suntecgroup.custom.processor.utils.Constants;

/*
 * This class is for creating a custom NiFi processor to handle the join operator
 * 
 * @version 1.0 - April 2019
 * @author John Britto
 */
@SideEffectFree
@Tags({ "join,suntec" })
@CapabilityDescription("Join processor to comibine data from multiple connections to single connection")
public class JoinProcessor extends AbstractProcessor {

	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;

	public static final PropertyDescriptor INPUT_BE_TYPE = new PropertyDescriptor.Builder()
			.name("Input Business Entity").description("Set input BE Type").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor INPUT_BE_BUK_ATTRIBUTES = new PropertyDescriptor.Builder()
			.name("Input BE BUK Attributes").description("Input BE BUK attributes array").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description("Success relationship").build();

	public static final PropertyDescriptor MERGE_SOURCE = new PropertyDescriptor.Builder().name("Merge Source")
			.description("flag for defining the processor as source processor of merge")
			.allowableValues("true", "false").required(true).defaultValue("false")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor PATH_NAME = new PropertyDescriptor.Builder().name("Path_Name")
			.description("path name for the merge processor").addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.required(false).build();

	@Override
	public void init(final ProcessorInitializationContext context) {
		List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(INPUT_BE_TYPE);
		properties.add(INPUT_BE_BUK_ATTRIBUTES);
		properties.add(SESSION_ID);
		properties.add(RUN_NUMBER);
		properties.add(MERGE_SOURCE);
		properties.add(PATH_NAME);
		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		this.relationships = Collections.unmodifiableSet(relationships);
	}
	
	boolean isMergeSource = false;
	String pathName;
		
	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		isMergeSource = context.getProperty(MERGE_SOURCE).evaluateAttributeExpressions().asBoolean();
		if (isMergeSource) {
			pathName = context.getProperty(PATH_NAME).evaluateAttributeExpressions().getValue();
		}
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {

		FlowFile inputFlowfile = session.get();
		if (null == inputFlowfile) {
			return;
		}
		FlowFile flowfile = session.clone(inputFlowfile);

		String sessionId = context.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
		sessionId = sessionId.trim();
		session.putAttribute(flowfile, Constants.ATTR_SESSION_ID, sessionId);
		String runNumber = context.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
		runNumber = runNumber.trim();
		session.putAttribute(flowfile, Constants.ATTR_RUN_NUMBER, runNumber);
		
		if (isMergeSource) {
			session.putAttribute(flowfile, Constants.ROUTE, pathName);
		}

		session.getProvenanceReporter().modifyAttributes(flowfile);
		session.transfer(flowfile, REL_SUCCESS);
		session.remove(inputFlowfile);
		session.commit();
	}// close of OnTrigger method

	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	@Override
	public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}
}
