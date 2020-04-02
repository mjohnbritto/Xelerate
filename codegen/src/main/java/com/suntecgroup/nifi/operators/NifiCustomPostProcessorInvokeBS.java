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

/***
 * NifiCustomPostProcessorInvokeBS - A class having the implementing
 * NifiCustomPostProcessorInvokeBS.
 */
@org.springframework.stereotype.Component
public class NifiCustomPostProcessorInvokeBS {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiCustomPostProcessorInvokeBS.class);
	private final String procName = CGConstants.NIFI_CUSTOM_POST_INVOKEBS;

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private MetaConfigClient metaConfigClient;

	/***
	 * generateCustomPostProcessorReq - This method having logic to generate
	 * nifi generateCustomPostProcessor.
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
	public TemplateProcessor generateCustomPostProcessorReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String autoTerminateFlag, int processorPosition, boolean isAlternativePath) throws CGException {
		LOGGER.info("Create CustomPostProcessorIBS Request ::");
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();
		List<TemplateRelationships> relationShipList = new ArrayList<TemplateRelationships>();
		TemplateRelationships relationships = new TemplateRelationships();
		relationships.setName(CGConstants.SUCCESS);
		relationships.setAutoTerminate(autoTerminateFlag);
		relationShipList.add(relationships);

		Map<String, Float> metaConfigScale = metaConfigClient.getMetaConfig(currentOperator.getType(), procName);
		Map<String, String> metaConfigDefault = metaConfigClient.getDefaultPropertyValues();

		CGUtils.setPropertyValue(config, currentOperator, metaConfigScale, metaConfigDefault);

		TemplateProperties properties = new TemplateProperties();
		TemplateDescriptors descriptors = new TemplateDescriptors();
		List<TemplateEntry> entryPropList = new ArrayList<TemplateEntry>();
		List<TemplateEntryDesc> entryDescList = new ArrayList<TemplateEntryDesc>();
		Map<String, String> entryMap = createEntryMap(currentOperator, isAlternativePath);
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

		processor.setType(property.getCustomPostProcessorIBSType());
		processor.setName(currentOperator.getName() + "-" + currentOperator.getType() + "-"
				+ property.getCustomPostProcessorIBSAppName());
		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipList);
		processor.setState("STOPPED");
		processor.setStyle("");

		LOGGER.info("CustomPostProcessorIBS Request is generated Successfully::");
		return processor;
	}

	private Map<String, String> createEntryMap(Operators operators, boolean isAlternativePath) {
		LOGGER.debug("Mapping Entry Values in CustomPostProcessorIBS ::");
		Gson gson = new Gson();
		Map<String, String> entryMap = new LinkedHashMap<String, String>();
		Map<String, List<OutputParam>> outputMapp = new HashMap<String, List<OutputParam>>();
		Map<String, Object> outputBeMapp = new HashMap<>();

		Map<String, BusinessEntity> outputBe = new HashMap<String, BusinessEntity>();

		if (operators.getType().equals(CGConstants.INVOKE_BS_EXTERNAL)) {
			outputBeMapp.put("outputMapping", operators.getBusinessSettings().getOutputParametersMapping());
			outputBeMapp.put("pvMapping",  operators.getBusinessSettings().getPvParametersMapping());
			entryMap.put("Operator Name", "InvokeExternal_" + operators.getName());
			entryMap.put("Output Mapping Parameter",gson.toJson(outputBeMapp));
		} else {
			entryMap.put("Operator Name", operators.getName());
			outputMapp.put("outputMapping", operators.getOutputMapping());
			entryMap.put("Output Mapping Parameter", gson.toJson(outputMapp));

		}
		
		if (operators.getBusinessSettings().isBusinessFailureFlowExist() && isAlternativePath) {
			entryMap.put("Output Business Entity", operators.getBusinessSettings().getInputBeType());
			outputBe.put("outputBe", operators.getBusinessSettings().getInputBe());
			entryMap.put("OutputBE Definition", gson.toJson(outputBe));
			entryMap.put("Output BE BUK Attributes",
					gson.toJson(operators.getBusinessSettings().getInputBEBUKAttributes()));
		} else {
			entryMap.put("Output Business Entity", operators.getBusinessSettings().getOutputBeType());
			outputBe.put("outputBe", operators.getBusinessSettings().getOutputBe());
			entryMap.put("OutputBE Definition", gson.toJson(outputBe));
			entryMap.put("Output BE BUK Attributes",
					gson.toJson(operators.getBusinessSettings().getOutputBEBUKAttributes()));
		}
		

		if (operators.getType().equalsIgnoreCase(CGConstants.INVOKE_BS)) {
			entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
		} else {
			entryMap.put(CGConstants.HTTP_METHOD, operators.getBusinessSettings().getHttpMethod());
		}
		return entryMap;
	}
}
