package com.suntecgroup.nifi.operators;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.suntecgroup.nifi.frontend.bean.BusinessEntity;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.frontend.bean.OutputParam;
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
 * NifiCustomPostMergeProcessor - A class having the logic for implementing
 * NifiCustomPostMergeProcessor.
 */
@org.springframework.stereotype.Component
public class NifiCustomPostMergeProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiCustomPostMergeProcessor.class);
	private final String procName = CGConstants.NIFI_CUSTOM_POST_MERGE;

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private MetaConfigClient metaConfigClient;

	public TemplateProcessor generateNifiCustomPostMergeProcessor(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String autoTerminateFlag) throws CGException {

		LOGGER.info("Create NifiCustomPostMergeProcessor Request ::");
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		int canvasPosition = 3;

		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();

		List<TemplateRelationships> relationShipsList = new ArrayList<TemplateRelationships>();
		List<String> selectedrelationShips = new ArrayList<String>();

		relationShipsList.add(new TemplateRelationships(CGConstants.SUCCESS, CGConstants.AUTO_TERMINATE_FALSE));
		relationShipsList.add(new TemplateRelationships(CGConstants.FAILURE, CGConstants.AUTO_TERMINATE_FALSE));

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

		position.setX(String.valueOf(BPCanvasUtils.getXByPosition(canvasPosition)));
		position.setY(String.valueOf(BPCanvasUtils.getYByPosition(canvasPosition)));

		processor.setSelectedRelationShip(selectedrelationShips);
		processor.setId(clientID);
		processor.setParentGroupId(theTarget.getProcessGroupID());
		processor.setPosition(position);
		processor.setType(property.getCustomPostMergeComponentType());
		processor.setName(currentOperator.getName() + "-" + currentOperator.getType() + "-PostProcessorMerge");
		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipsList);
		processor.setState("STOPPED");
		processor.setStyle("");

		LOGGER.info("NifiCustomPostMergeProcessor Request is generated Successfully::");
		return processor;
	}

	private Map<String, String> createEntryMap(BPFlowUI bpFlowRequest, Operators operator) {
		LOGGER.debug("Mapping Entry Values in NifiCustomPostMergeProcessor ::");
		Gson gson = new Gson();
		Map<String, String> entryMap = new LinkedHashMap<String, String>();
		Map<String, List<OutputParam>> outputMapp = new HashMap<String, List<OutputParam>>();
		Map<String, BusinessEntity> outputBe = new HashMap<String, BusinessEntity>();

		entryMap.put("Output Mapping Parameter", gson.toJson(operator.getOutputMapping()));
		entryMap.put("Output Business Entity", operator.getBusinessSettings().getOutputBeType());
		entryMap.put("Output BE BUK Attributes",
				gson.toJson(operator.getBusinessSettings().getOutputBEBUKAttributes()));
		outputMapp.put("outputMapping", operator.getOutputMapping());
		entryMap.put("Output Mapping Parameter", gson.toJson(outputMapp));
		entryMap.put("Operator Name", operator.getName());
		outputMapp.put("outputMapping", operator.getOutputMapping());

		outputBe.put("outputBe", operator.getBusinessSettings().getOutputBe());
		entryMap.put("OutputBE Definition", gson.toJson(outputBe));

		return entryMap;
	}

}
