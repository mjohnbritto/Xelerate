package com.suntecgroup.nifi.operators;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.suntecgroup.nifi.config.CGConfigurationProperty;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.metaconfig.client.MetaConfigClient;
import com.suntecgroup.nifi.template.beans.TemplateBundle;
import com.suntecgroup.nifi.template.beans.TemplateConfig;
import com.suntecgroup.nifi.template.beans.TemplateDescriptors;
import com.suntecgroup.nifi.template.beans.TemplateEntry;
import com.suntecgroup.nifi.template.beans.TemplateEntryDesc;
import com.suntecgroup.nifi.template.beans.TemplatePosition;
import com.suntecgroup.nifi.template.beans.TemplateProcessor;
import com.suntecgroup.nifi.template.beans.TemplateProperties;
import com.suntecgroup.nifi.template.beans.TemplateRelationships;
import com.suntecgroup.nifi.template.beans.TemplateValue;
import com.suntecgroup.nifi.util.BPCanvasUtils;
import com.suntecgroup.nifi.util.CGUtils;

/**
 * NifiSmartConnectorProcessor - A class having the logic for implementing
 * NifiSmartConnectorProcessor.
 */
@org.springframework.stereotype.Component
public class NifiSmartConnectorProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiSmartConnectorProcessor.class);
	private final String procName = CGConstants.NIFI_SMART_CONNECTOR;

	@Autowired
	private CGConfigurationProperty confProperty;

	@Autowired
	private MetaConfigClient metaConfigClient;

	public TemplateProcessor generateSmartConnectorProcessorReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String[] relationship, String[] openRelationship, int processorPosition)
			throws CGException {
		LOGGER.info("Create SmartConnector  Request ::");
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();
		List<TemplateRelationships> relationShipList = new ArrayList<TemplateRelationships>();
		TemplateRelationships relationships = new TemplateRelationships();
		TemplateRelationships relationshipsFailure = new TemplateRelationships();

		relationships.setName(CGConstants.SUCCESS_LOWERCASE);
		relationships.setAutoTerminate("false");
		relationshipsFailure.setName(CGConstants.FAILURE_LOWERCASE);
		relationshipsFailure.setAutoTerminate("true");
		relationShipList.add(relationships);
		relationShipList.add(relationshipsFailure);

		Map<String, Float> metaConfigScale = metaConfigClient.getMetaConfig(currentOperator.getType(), procName);
		Map<String, String> metaConfigDefault = metaConfigClient.getDefaultPropertyValues();

		CGUtils.setPropertyValue(config, currentOperator, metaConfigScale, metaConfigDefault);

		TemplateProperties properties = new TemplateProperties();
		TemplateDescriptors descriptors = new TemplateDescriptors();
		List<TemplateEntry> entryPropList = new ArrayList<TemplateEntry>();
		List<TemplateEntryDesc> entryDescList = new ArrayList<TemplateEntryDesc>();
		Map<String, String> entryMap = createEntryMap(currentOperator);
		for (Map.Entry<String, String> entry : entryMap.entrySet()) {
			TemplateEntryDesc descEntry = new TemplateEntryDesc();
			TemplateValue value = new TemplateValue();
			value.setName(entry.getKey());
			descEntry.setKey(entry.getKey());
			descEntry.setValue(value);
			entryDescList.add(descEntry);

			TemplateEntry propertyEntry = new TemplateEntry();
			propertyEntry.setKey(entry.getKey());
			if (!StringUtils.isBlank(entry.getValue())) {
				propertyEntry.setValue(entry.getValue());
			}
			entryPropList.add(propertyEntry);
		}
		descriptors.setEntryDescList(entryDescList);
		config.setDescriptors(descriptors);
		properties.setEntryList(entryPropList);
		config.setProperties(properties);

		TemplateBundle bundle = new TemplateBundle();
		bundle.setGroup(confProperty.getCustomBundleGroup());
		bundle.setArtifact(confProperty.getCustomBundleArtifact());
		bundle.setVersion(confProperty.getCustomBundleVersion());

		position.setX(String.valueOf(BPCanvasUtils.getXByPosition(processorPosition)));
		position.setY(String.valueOf(BPCanvasUtils.getYByPosition(processorPosition)));
		processor.setRelationshipsList(relationShipList);

		processor.setId(clientID);
		processor.setParentGroupId(theTarget.getProcessGroupID());
		processor.setPosition(position);

		processor.setType(confProperty.getCustomProcessorConnectorProcessorType());
		processor.setName(currentOperator.getName() + "-" + currentOperator.getType() + "-"
				+ confProperty.getCustomProcessorConnectorProcessorAppName());
		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipList);
		processor.setState("STOPPED");
		processor.setStyle("");

		LOGGER.info("SmartConnector processor Request is generated Successfully::");
		return processor;
	}

	private Map<String, String> createEntryMap(Operators currentOperator) {
		LOGGER.debug("Mapping Entry Values in SmartConnector processor ::");
		Gson gson = new Gson();

		Map<String, String> entryMap = new LinkedHashMap<String, String>();
		entryMap.put("MAPPING_PARAMETER", gson.toJson(currentOperator.getSmartConnectorMapping()));
		entryMap.put("OUTPUTBE_DEFINITION", gson.toJson(currentOperator.getBusinessSettings().getOutputBe()));
		entryMap.put("CONNECTOR_NAME", currentOperator.getName());
		entryMap.put("INPUT BUSINESS ENTITY", currentOperator.getBusinessSettings().getInputBeType());
		entryMap.put("OUTPUT BUSINESS ENTITY", currentOperator.getBusinessSettings().getOutputBeType());
		entryMap.put("Input BE BUK Attributes",
				String.valueOf(currentOperator.getBusinessSettings().getInputBEBUKAttributes()));
		entryMap.put("Output BE BUK Attributes",
				String.valueOf(currentOperator.getBusinessSettings().getOutputBEBUKAttributes()));

		return entryMap;
	}
}
