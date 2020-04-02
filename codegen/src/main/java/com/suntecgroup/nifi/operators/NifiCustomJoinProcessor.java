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
import com.suntecgroup.nifi.frontend.bean.Property;
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
 * NifiCustomJoinProcessor - A class having the logic for implementing
 * NifiCustomJoinProcessor.
 */
@org.springframework.stereotype.Component
public class NifiCustomJoinProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiCustomJoinProcessor.class);
	private final String procName = CGConstants.NIFI_CUSTOM_JOIN;

	@Autowired
	private CGConfigurationProperty property;
	@Autowired
	private MetaConfigClient metaConfigClient;

	/**
	 * generateCustomJoinReq - This method having logic to generate nifi
	 * generateCustomJoinProcessor.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @param currentOperator
	 *            - holds the currentOperator data information of Operators type
	 * @param autoTerminateFlag
	 *            - holds the autoTerminateFlag data information of String type
	 * @return - returns Processor object response
	 */
	public TemplateProcessor generateCustomJoinReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String autoTerminateFlag) throws CGException {

		LOGGER.info("Create CustomJoinNifiProcessors Request :: for " + currentOperator.getName());
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();
		List<TemplateRelationships> relationShipList = new ArrayList<TemplateRelationships>();
		TemplateRelationships relationships = new TemplateRelationships();
		relationships.setName(CGConstants.SUCCESS);
		relationships.setAutoTerminate(CGConstants.AUTO_TERMINATE_FALSE);
		relationShipList.add(relationships);

		Map<String, Float> metaConfigScale = metaConfigClient.getMetaConfig(currentOperator.getType(), procName);
		Map<String, String> metaConfigDefault = metaConfigClient.getDefaultPropertyValues();
		CGUtils.setPropertyValue(config, currentOperator, metaConfigScale, metaConfigDefault);

		TemplateProperties properties = new TemplateProperties();
		TemplateDescriptors descriptors = new TemplateDescriptors();
		List<TemplateEntry> entryPropList = new ArrayList<TemplateEntry>();
		List<TemplateEntryDesc> entryDescList = new ArrayList<TemplateEntryDesc>();
		Map<String, String> entryMap = createEntryMap(bpFlowRequest, currentOperator);
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
		bundle.setGroup(property.getCustomBundleGroup());
		bundle.setArtifact(property.getCustomBundleArtifact());
		bundle.setVersion(property.getCustomBundleOneVersion());

		position.setX(String.valueOf(BPCanvasUtils.getXByPosition(1)));
		position.setY(String.valueOf(BPCanvasUtils.getYByPosition(1)));
		processor.setId(clientID);
		processor.setParentGroupId(theTarget.getProcessGroupID());
		processor.setPosition(position);

		processor.setType(property.getCustomJoinComponentType());
		processor.setName(currentOperator.getName().concat("-").concat(currentOperator.getType()).concat("-")
				.concat(property.getCustomJoinAppName()));

		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipList);
		processor.setState(CGConstants.STOPPED);
		processor.setStyle(CGConstants.BLANK);

		LOGGER.info("CustomJoinNifiProcessors Request is generated Successfully::");
		return processor;
	}

	private Map<String, String> createEntryMap(BPFlowUI bpFlowRequest, Operators currentOperator) {
		Gson gson = new Gson();
		LOGGER.debug("Mapping Entry Values in CustomJoinNifiProcessors ::");
		Map<String, String> entryMap = new LinkedHashMap<String, String>();
		entryMap.put(CGConstants.INPUT_BUSINESS_ENTITY_PROPERTY,
				currentOperator.getBusinessSettings().getInputBeType());
		entryMap.put("Input BE BUK Attributes",
				gson.toJson(currentOperator.getBusinessSettings().getInputBEBUKAttributes()));

		entryMap.put(CGConstants.SESSION_ID, "${sessionId}");
		entryMap.put(CGConstants.RUN_NUMBER, "${runNumber}");
		LOGGER.debug("Entry values are mapped in CustomJoinNifiProcessors ::");
		return entryMap;
	}

}
