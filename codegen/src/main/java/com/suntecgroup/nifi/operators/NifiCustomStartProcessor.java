package com.suntecgroup.nifi.operators;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.suntecgroup.nifi.frontend.bean.ProcessVariables;
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
 * NifiCustomStartProcessor - A class having the logic for implementing
 * NifiCustomStartProcessor.
 */
@org.springframework.stereotype.Component
public class NifiCustomStartProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiCustomStartProcessor.class);
	private final String procName = CGConstants.NIFI_CUSTOM_START;

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private MetaConfigClient metaConfigClient;

	/**
	 * generateCustomStartReq - This method having logic to generate nifi
	 * generateCustomStartProcessors.
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
	public TemplateProcessor generateCustomStartReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String autoTerminateFlag, int processorPosition) throws CGException {
		LOGGER.info("Create CustomStartProcessors Request ::");
		TemplateProcessor processor = new TemplateProcessor();

		String clientID = UUID.randomUUID().toString();
		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();
		List<TemplateRelationships> relationShipList = new ArrayList<TemplateRelationships>();
		TemplateRelationships relationships = new TemplateRelationships();
		TemplateRelationships relationshipsFailure = new TemplateRelationships();

		relationships.setName(CGConstants.SUCCESS);
		relationships.setAutoTerminate("false");
		relationshipsFailure.setName(CGConstants.FAILURE);
		relationshipsFailure.setAutoTerminate("false");
		relationShipList.add(relationships);
		relationShipList.add(relationshipsFailure);
		
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

		processor.setType(property.getCustomStartComponentType());
		processor.setName(
				currentOperator.getName() + "-" + currentOperator.getType() + "-" + property.getCustomStartAppName());
		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipList);
		processor.setState("STOPPED");
		processor.setStyle("");

		LOGGER.info("CustomStartProcessor Request is generated Successfully::");
		return processor;
	}

	private Map<String, String> createEntryMap(BPFlowUI bPFlowRequest, Operators operators) {
		LOGGER.debug("Mapping Entry Values in CustomStartProcessors ::");
		String bpname = bPFlowRequest.getConfigureBusinessProcess().getFunctional().getBusinessProcessSetup()
				.getProcessName();
		Gson gson = new Gson();
		Map<String, String> entryMap = new LinkedHashMap<String, String>();
		List<ProcessVariables> listProcessVariables = bPFlowRequest.getConfigureBusinessProcess().getFunctional()
				.getProcessVariables();
		String value = "[";
		for (int index = 0; index < listProcessVariables.size(); index++) {
			String valueString = bpname + ".processvariable." + listProcessVariables.get(index).getName();
			try {
				valueString = URLEncoder.encode(valueString, CGConstants.formatEncodeDecode);
				value += "${\"" + valueString + "\"}";
				if ((index + 1) < listProcessVariables.size()) {
					value += ",";
				}
			} catch (UnsupportedEncodingException e) {
				throw new CGException("Exception at creating map at NifiCustomStartProcessor.", e);
			}
		}
		value += "]";

		entryMap.put("Process Variables", value);
		entryMap.put("Input Business Entity", operators.getBusinessSettings().getInputBeType());
		entryMap.put("Event Logging", String.valueOf(operators.getBusinessSettings().isEventLogging()));
		entryMap.put("Input BE BUK Attributes", gson.toJson(operators.getBusinessSettings().getInputBEBUKAttributes()));

		LOGGER.debug("Entry values are mapped in CustomStartProcessors ::");
		return entryMap;
	}

}
