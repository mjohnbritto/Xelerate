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
 * NifiListenHTTPProcessor - A class having the logic for implementing NifiListenHTTPProcessor.
 */
@org.springframework.stereotype.Component
public class NifiListenHTTPProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiListenHTTPProcessor.class);
	private final String procName = CGConstants.NIFI_LISTEN_HTTP;
	
	@Autowired
	private CGConfigurationProperty property;
	
	@Autowired
	private MetaConfigClient metaConfigClient;

	public TemplateProcessor generateCustomListenHttpReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String autoTerminateFlag, int processorPosition) throws CGException {

		LOGGER.info("Create CustomListenHttpProcessor Request ::");
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();

		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();

		List<TemplateRelationships> relationShipsList = new ArrayList<TemplateRelationships>();
		List<String> selectedrelationShips = new ArrayList<String>();

		TemplateRelationships relationships = new TemplateRelationships();
		relationships.setName(CGConstants.SUCCESS_LOWERCASE);
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

		bundle.setGroup(property.getInvokeHttpBundleGroup());
		bundle.setArtifact(property.getInvokeHttpBundleArtifact());
		bundle.setVersion(property.getInvokeHttpBundleVersion());
		position.setX(String.valueOf(BPCanvasUtils.getXByPosition(processorPosition)));
		position.setY(String.valueOf(BPCanvasUtils.getYByPosition(processorPosition)));
		processor.setSelectedRelationShip(selectedrelationShips);
		processor.setId(clientID);
		processor.setParentGroupId(theTarget.getProcessGroupID());
		processor.setPosition(position);
		processor.setType(property.getCustomListenHttpComponentType());
		processor.setName(currentOperator.getName()+"-"+currentOperator.getType()+"-"+property.getCustomListenHttpAppName());
		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipsList);
		processor.setState("STOPPED");
		processor.setStyle("");

		LOGGER.info("CustomListenHttpProcessor Request is generated Successfully::");
		return processor;
	}

	private Map<String, String> createEntryMap(BPFlowUI bpFlowRequest, Operators operator) {
		
		LOGGER.debug("Mapping Entry Values in CustomListenHttpProcessor ::");
		
		Map<String, String> entryMap = new LinkedHashMap<String, String>();
		String bpName=bpFlowRequest.getConfigureBusinessProcess().getFunctional().getBusinessProcessSetup().getProcessName();
		if (CGConstants.REST_INPUT_CHANNEL_INTEGRATION.equals(operator.getType())) {
			entryMap.put("Base Path", "${" + bpName + "." + operator.getKey() + ".restApiBasepath}");
			entryMap.put("Listening Port", "${" + bpName + "." + operator.getKey() + ".restApiPort}");
			entryMap.put("OAuth Introspect URL", "${" + bpName + "." + operator.getKey() + ".oAuthIntrospectURL}");
			entryMap.put("Client Id", "${" + bpName + "." + operator.getKey() + ".oAuthClientId}");
			entryMap.put("Client Secret", "${" + bpName + "." + operator.getKey() + ".oAuthClientSecret}");
		} else {
			entryMap.put("Base Path", "${"+bpName+".SETTINGS.restApiBasepath}");
			entryMap.put("Listening Port", "${"+bpName+".SETTINGS.restApiPort}");
			entryMap.put("HTTP Headers to receive as Attributes (Regex)", "beName|channelId|channelName");
		}
		
		return entryMap;
	}

}
