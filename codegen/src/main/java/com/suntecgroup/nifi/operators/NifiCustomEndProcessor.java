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
 * NifiCustomEndProcessor - A class having the logic for implementing
 * NifiCustomEndProcessor.
 */
@org.springframework.stereotype.Component
public class NifiCustomEndProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(NifiCustomEndProcessor.class);
	private final String procName = CGConstants.NIFI_CUSTOM_END;

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private MetaConfigClient metaConfigClient;

	/**
	 * generateCustomEndReq - This method having logic to generate nifi
	 * generateCustomEndProcessor.
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
	public TemplateProcessor generateCustomEndReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String autoTerminateFlag, int processorPosition) throws CGException {

		LOGGER.info("Create CustomEndProcessors Request ::");
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();
		List<TemplateRelationships> relationShipList = new ArrayList<TemplateRelationships>();
		TemplateRelationships relationships = new TemplateRelationships();
		relationships.setName(CGConstants.SUCCESS);
		relationships.setAutoTerminate("true");
		TemplateRelationships relationshipsFailure = new TemplateRelationships();
		relationshipsFailure.setName("LogEvent");
		relationshipsFailure.setAutoTerminate(autoTerminateFlag);
		TemplateRelationships relationshipsUpdateTxn = new TemplateRelationships();
		relationshipsUpdateTxn.setName("UpdateStatus");
		relationshipsUpdateTxn.setAutoTerminate("false");

		relationShipList.add(relationships);
		relationShipList.add(relationshipsFailure);
		relationShipList.add(relationshipsUpdateTxn);
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
		bundle.setVersion(property.getCustomBundleVersion());
	
		position.setX(String.valueOf(BPCanvasUtils.getXByPosition(processorPosition)));
		position.setY(String.valueOf(BPCanvasUtils.getYByPosition(processorPosition)));
		processor.setId(clientID);
		processor.setParentGroupId(theTarget.getProcessGroupID());
		processor.setPosition(position);


		processor.setType(property.getCustomEndComponentType());
		processor.setName(
				currentOperator.getName() + "-" + currentOperator.getType() + "-" + property.getCustomEndAppName());
		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipList);
		processor.setState("STOPPED");
		processor.setStyle("");

		LOGGER.info("CustomEndProcessor Request is generated Successfully::");
		return processor;
	}

	private Map<String, String> createEntryMap(BPFlowUI bpFlowRequest, Operators currentOperator) {
		LOGGER.debug("Mapping Entry Values in CustomEndProcessors ::");
		Map<String, String> entryMap = new LinkedHashMap<String, String>();
		entryMap.put("Output Business Entity", currentOperator.getBusinessSettings().getOutputBeType());
		entryMap.put("Event Logging", String.valueOf(currentOperator.getBusinessSettings().isEventLogging()));
		LOGGER.debug("Entry values are mapped in CustomEndProcessors ::");
		return entryMap;
	}

}
