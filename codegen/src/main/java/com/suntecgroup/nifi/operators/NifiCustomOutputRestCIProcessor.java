package com.suntecgroup.nifi.operators;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.suntecgroup.nifi.config.CGConfigurationProperty;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.BusinessEntity;
import com.suntecgroup.nifi.frontend.bean.ContextParameters;
import com.suntecgroup.nifi.frontend.bean.InputParametersMapping;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.metaconfig.client.MetaConfigClient;
import com.suntecgroup.nifi.template.beans.DataType;
import com.suntecgroup.nifi.template.beans.TemplateBundle;
import com.suntecgroup.nifi.template.beans.TemplateConfig;
import com.suntecgroup.nifi.template.beans.TemplateContextParameter;
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
 * NifiCustomOutputRestCIProcessor - A class having the logic for implementing
 * NifiCustomOutputRestCIProcessor.
 */
@org.springframework.stereotype.Component
public class NifiCustomOutputRestCIProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiCustomOutputRestCIProcessor.class);
	private final String procName = CGConstants.NIFI_CUSTOM_REST_OUTPUT_CI;

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private MetaConfigClient metaConfigClient;

	public TemplateProcessor generateNifiCustomOutputRestCIProcessor(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String autoTerminateFlag) throws CGException {

		LOGGER.info("Create NifiCustomOutputRestCIProcessor Request ::");
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

		position.setX(String.valueOf(BPCanvasUtils.getXByPosition(1)));
		position.setY(String.valueOf(BPCanvasUtils.getYByPosition(1)));
		processor.setId(clientID);
		processor.setParentGroupId(theTarget.getProcessGroupID());
		processor.setPosition(position);

		processor.setType(property.getCustomPreProcessorIBSType());
		processor.setName(currentOperator.getName() + "-" + currentOperator.getType() + "-"
				+ property.getCustomPreProcessorIBSAppName());
		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipList);
		processor.setState(CGConstants.STOPPED);
		processor.setStyle("");
		return processor;
	}

	private Map<String, String> createEntryMap(Operators operator) {
		LOGGER.debug("Mapping Entry Values in NifiCustomOutputRestCIProcessor ::");

		Gson gson = new Gson();
		Map<String, String> entryMap = new LinkedHashMap<String, String>();

		List<ContextParameters> contextParamslist = new ArrayList<ContextParameters>();

		if (operator.getBusinessSettings().getServiceContextParameters() != null) {
			contextParamslist.addAll(operator.getBusinessSettings().getServiceContextParameters());
		}
		if (operator.getBusinessSettings().getApiContextParameters() != null) {
			contextParamslist.addAll(operator.getBusinessSettings().getApiContextParameters());
		}

		List<TemplateContextParameter> templateContxtParm = new ArrayList<TemplateContextParameter>();
		int i = 0;
		
		for (ContextParameters conObj : contextParamslist) {
			i++;
			TemplateContextParameter tempC = new TemplateContextParameter();
			tempC.setName(conObj.getName());
			DataType dataType = new DataType();
			dataType.setType(conObj.getType());
			dataType.setScale(Integer.parseInt(conObj.getValue().getScale()));
			dataType.setPrecision(Integer.parseInt(conObj.getValue().getPrecision()));
			dataType.setDateFormat(conObj.getValue().getDateValue());
			tempC.setDataType(dataType);
			tempC.setContextParamId(i);
			templateContxtParm.add(tempC);
		}
		
		Map<String, List<TemplateContextParameter>> contxtParams = new HashMap<String, List<TemplateContextParameter>>();
		contxtParams.put("contextParameters", templateContxtParm);
		entryMap.put("Context Parameter", gson.toJson(contxtParams));

		Map<String, BusinessEntity> inputBe = new HashMap<String, BusinessEntity>();
		inputBe.put("inputBe", operator.getBusinessSettings().getInputBe());
		
		Map<String, Object> inputMapping = new HashMap<String, Object>();
		Map<String, Object> testInputMapping = new HashMap<String, Object>();

		if ("InternalAPI".equals(operator.getBusinessSettings().getAPIInput())) {
			entryMap.put("Service Name", operator.getBusinessSettings().getBusinessServiceName());
			entryMap.put("Api Name", operator.getBusinessSettings().getApiName());
			entryMap.put("BS Category", "internal");
			inputMapping.put("inputMapping", operator.getInputMapping());
			entryMap.put("Input Mapping Parameter", gson.toJson(inputMapping));
		} else {
			entryMap.put("Service Name", "Service Name");
			entryMap.put("Api Name", "Api Name");
			entryMap.put("BS Category", "external");

			InputParametersMapping ipmParamMapping = null;
			ObjectMapper mapper = new ObjectMapper();
			try {
				if (!StringUtils.isAllEmpty(operator.getBusinessSettings().getInputParametersMapping())) {
					ipmParamMapping = mapper.readValue(operator.getBusinessSettings().getInputParametersMapping(),
							InputParametersMapping.class);
					setExternalAPIOperatorProperties(ipmParamMapping, entryMap, testInputMapping);
				}
				entryMap.put("Security", operator.getBusinessSettings().getSecurity());
			} catch (JsonParseException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				entryMap.put("Input Mapping Parameter", gson.toJson(testInputMapping));
			}
		}
		
		entryMap.put("Operator Name", operator.getName());
		entryMap.put("Input Business Entity", operator.getBusinessSettings().getInputBeType());
		entryMap.put("InputBE Definition", gson.toJson(inputBe));
		entryMap.put("Input BE BUK Attributes", gson.toJson(operator.getBusinessSettings().getInputBEBUKAttributes()));
		entryMap.put("isBatchable", "false");
		if ("InternalAPI".equals(operator.getBusinessSettings().getAPIInput())) {
		entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
		}else{
			entryMap.put(CGConstants.HTTP_METHOD, operator.getBusinessSettings().getHttpMethod());
		}
		return entryMap;
	}

	private void setExternalAPIOperatorProperties(InputParametersMapping ipmParamMapping, Map<String, String> entryMap,
			Map<String, Object> testInputMapping) {
		testInputMapping.put("inputJSON", ipmParamMapping.getContentParam());
		testInputMapping.put("pathParam", ipmParamMapping.getPathParam());
		testInputMapping.put("queryParam", ipmParamMapping.getQueryParam());
		entryMap.put("Headers", ipmParamMapping.getHeaderParam());
	}
}
