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
import com.suntecgroup.nifi.frontend.bean.Decisions;
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
 * NifiDecisionMatrixProcessor - A class having the logic for implementing
 * NifiDecisionMatrixProcessor.
 */
@org.springframework.stereotype.Component
public class NifiDecisionMatrixExclusiveProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiDecisionMatrixExclusiveProcessor.class);
	private final String procName = CGConstants.NIFI_DECISION_MATRIX_EXCLUSIVE;

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private MetaConfigClient metaConfigClient;

	/**
	 * generateDecisionMatrixProcessorReq - This method having logic to generate
	 * nifi generateDecisionMatrixProcessor.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @param currentOperator
	 *            - holds the currentOperator data information of Operators type
	 * @return - returns Processor object response
	 */
	public TemplateProcessor generateDecisionMatrixExclusiveProcessorReq(BPFlowUI bpFlowRequest,
			SuntecNiFiModel theTarget, Operators currentOperator, int processorPosition) throws CGException {
		LOGGER.info("Create Decision Matrix Exclusive Request ::");
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();
		List<TemplateRelationships> relationShipList = new ArrayList<TemplateRelationships>();
		List<String> declist = new ArrayList<String>();
		for (Decisions dec : currentOperator.getBusinessSettings().getDecisions()) {
			declist.add(dec.getDecisionName());
		}

		for (String y : declist) {
			TemplateRelationships relationship = new TemplateRelationships();
			relationship.setName(y);
			relationship.setAutoTerminate("false");
			relationShipList.add(relationship);
		}
		TemplateRelationships relationship = new TemplateRelationships();
		relationship.setName(CGConstants.FAILURE);
		relationship.setAutoTerminate("false");
		relationShipList.add(relationship);

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
		bundle.setGroup(property.getCustomBundleGroup());
		bundle.setArtifact(property.getCustomBundleArtifact());
		bundle.setVersion(property.getCustomBundleVersion());

		position.setX(String.valueOf(BPCanvasUtils.getXByPosition(processorPosition)));
		position.setY(String.valueOf(BPCanvasUtils.getYByPosition(processorPosition)));
		processor.setRelationshipsList(relationShipList);

		processor.setId(clientID);
		processor.setParentGroupId(theTarget.getProcessGroupID());
		processor.setPosition(position);

		processor.setType(property.getCustomProcessordecisionMatrixExclusiveType());
		processor.setName(currentOperator.getName() + "-" + currentOperator.getType() + "-"
				+ property.getCustomProcessordecisionMatrixExclusiveAppName());

		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipList);
		processor.setState("STOPPED");
		processor.setStyle("");

		LOGGER.info("Decision Matrix Exclusive processor Request is generated Successfully::");
		return processor;
	}

	private Map<String, String> createEntryMap(Operators operators) {
		LOGGER.debug("Mapping Entry Values in Decision Matrix Exclusive processor ::");
		Gson gson = new Gson();
		Map<String, String> entryMap = new LinkedHashMap<String, String>();
		entryMap.put("Process Variables", gson.toJson(operators.getProcessVariable()));
		entryMap.put("decisions", gson.toJson(operators.getBusinessSettings().getDecisions()));
		entryMap.put("exclusiveFlag", gson.toJson(operators.getBusinessSettings().isExclusive()));
		entryMap.put("Input Business Entity", operators.getBusinessSettings().getInputBeType());
		entryMap.put("Input BE BUK Attributes", gson.toJson(operators.getBusinessSettings().getInputBEBUKAttributes()));
		return entryMap;
	}
}
