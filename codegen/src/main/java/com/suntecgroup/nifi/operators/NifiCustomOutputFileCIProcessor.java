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
 * NifiCustomOutputFileCIProcessor - A class having the logic for implementing
 * NifiCustomOutputFileCIProcessor.
 */
@org.springframework.stereotype.Component
public class NifiCustomOutputFileCIProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiCustomOutputFileCIProcessor.class);
	private final String procName = CGConstants.NIFI_CUSTOM_FILE_OUTPUT_CI;

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private MetaConfigClient metaConfigClient;

	public TemplateProcessor generateNifiCustomOutputFileCIProcessor(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String autoTerminateFlag) throws CGException {

		LOGGER.info("Create NifiCustomOutputFileCIProcessor Request ::");
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		int canvasPosition = 1;

		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();

		List<TemplateRelationships> relationShipsList = new ArrayList<TemplateRelationships>();
		List<String> selectedrelationShips = new ArrayList<String>();

		TemplateRelationships relationships = new TemplateRelationships();
		relationships.setName(CGConstants.SUCCESS);
		relationships.setAutoTerminate(autoTerminateFlag);
		relationShipsList.add(relationships);

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
		if (CGConstants.FIXED_WIDTH.equalsIgnoreCase(currentOperator.getSelected())) {
			processor.setType(property.getCustomFixedWidthOutputFileChannelComponentType());
			processor.setName(currentOperator.getName() + "-" + currentOperator.getType() + "-"
					+ property.getCustomFileChannelIntegrationOutputAppName());
		} else {
			processor.setType(property.getCustomDelimitedOutputFileChannelComponentType());
			processor.setName(currentOperator.getName() + "-" + currentOperator.getType() + "-"
					+ property.getCustomFileChannelIntegrationOutputAppName());
		}
		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipsList);
		processor.setState("STOPPED");
		processor.setStyle("");

		LOGGER.info("NifiCustomOutputFileCIProcessor Request is generated Successfully::");
		return processor;
	}

	private Map<String, String> createEntryMap(BPFlowUI bpFlowRequest, Operators operator) {
		LOGGER.debug("Mapping Entry Values in NifiCustomOutputFileCIProcessor ::");
		Gson gson = new Gson();
		String operatorName = operator.getName();
		String operatorKey = operator.getKey();
		Map<String, String> entryMap = new LinkedHashMap<String, String>();
		String bpName = bpFlowRequest.getConfigureBusinessProcess().getFunctional().getBusinessProcessSetup()
				.getProcessName();

		entryMap.put("Header", gson.toJson(operator.getOFCIHeader()));
		entryMap.put("Footer", gson.toJson(operator.getOFCIFooter()));
		entryMap.put("Content", gson.toJson(operator.getOFCIContent()));
		entryMap.put("CI Name", operatorName);
		entryMap.put("Mapping", gson.toJson(operator.getMapping()));
		entryMap.put("Eviction", gson.toJson(operator.getEviction()));
		entryMap.put("Output FilePath", "${" + bpName + "." + operator.getKey() + ".outputFileLocation}");
		entryMap.put("Output FileName", gson.toJson(operator.getOutputFileName()));
		entryMap.put(CGConstants.REMOTE_URL, "${suntec.transactions.status.protocol}"
				+ "${suntec.transactions.status.host}" + "${suntec.transactions.status.port}");
		entryMap.put(CGConstants.IDLE_CONNECTION_MAXPOOL_SIZE,
				"${" + bpName + "." + operatorKey + "." + CGConstants.IDLE_CONNECTION_MAXPOOL_SIZE + "}");
		entryMap.put(CGConstants.IDLE_CONNECTION_ALIVE_DURATION,
				"${" + bpName + "." + operatorKey + "." + CGConstants.IDLE_CONNECTION_ALIVE_DURATION + "}");

		return entryMap;
	}

}
