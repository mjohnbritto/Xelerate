package com.suntecgroup.nifi.operators;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.suntecgroup.nifi.frontend.bean.InputParam;
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
 * NifiCustomPreProcessorIBS - A class having the logic for implementing
 * NifiCustomPreProcessorIBS.
 */
@org.springframework.stereotype.Component
public class NifiCustomPreProcessorIBS {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiCustomPreProcessorIBS.class);
	private final String procName = CGConstants.NIFI_CUSTOM_PRE_IBS;

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private MetaConfigClient metaConfigClient;

	/**
	 * generateCustomPreProcessorReq - This method having logic to generate nifi
	 * generateCustomPreProcessors.
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
	public TemplateProcessor generateCustomPreProcessorReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String autoTerminateFlag) throws CGException {
		LOGGER.info("Cretae CustomPreProcessorIBS Request ::");
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

	private Map<String, String> createEntryMap(Operators operators) {
		LOGGER.debug("Mapping Entry Values in CustomPreProcessorIBS ::");
		Gson gson = new Gson();
		Map<String, String> entryMap = new LinkedHashMap<String, String>();

		List<ContextParameters> contextParamslist = new ArrayList<ContextParameters>();

		if (operators.getBusinessSettings().getServiceContextParameters() != null) {
			contextParamslist.addAll(operators.getBusinessSettings().getServiceContextParameters());
		}
		if (operators.getBusinessSettings().getApiContextParameters() != null) {
			contextParamslist.addAll(operators.getBusinessSettings().getApiContextParameters());
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
			tempC.setCollection(conObj.isCollection());
			templateContxtParm.add(tempC);
		}
		
		Map<String, List<TemplateContextParameter>> contxtParams = new HashMap<String, List<TemplateContextParameter>>();
		contxtParams.put("contextParameters", templateContxtParm);
		entryMap.put("Context Parameter", gson.toJson(contxtParams));
		
		Map<String, BusinessEntity> inputBe = new HashMap<String, BusinessEntity>();
		inputBe.put("inputBe", operators.getBusinessSettings().getInputBe());


		Map<String, Object> testInputMapping = new HashMap<String, Object>();
		
		if (operators.getType().equals(CGConstants.INVOKE_BS)) {
			entryMap.put("Service Name", operators.getBusinessSettings().getBusinessServiceName());
			entryMap.put("Api Name", operators.getBusinessSettings().getApiName());
			entryMap.put("Operator Name", operators.getName());
			List<InputParam> ipList = operators.getInputMapping();
			String dateFormat;
			String newDateString;
			for (InputParam ipMap : ipList) {
				if ("Enter Value".equals(ipMap.getSelectedKey()) && CGConstants.DATETIME.equals(ipMap.getType())) {
					dateFormat = ipMap.getCvDateFormat();
					newDateString = ipMap.getInputParamvalue().getDateValue();
					try {
						newDateString = new SimpleDateFormat(dateFormat)
								.format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
										.parse(ipMap.getInputParamvalue().getDateValue()));
					} catch (ParseException e) {
						LOGGER.info("ParseException occurred while creating CustomPreProcessorIBS :: ", e);
					}
					ipMap.getInputParamvalue().setDateValue(newDateString);
				}
			}
			testInputMapping.put("inputMapping", operators.getInputMapping());

		} else if (operators.getType().equals(CGConstants.INVOKE_BS_EXTERNAL)) {
			InputParametersMapping ipmParamMapping = null;
			ObjectMapper mapper = new ObjectMapper();
			try {
				if(!StringUtils.isAllEmpty(operators.getBusinessSettings().getInputParametersMapping())){
				ipmParamMapping = mapper.readValue(operators.getBusinessSettings().getInputParametersMapping(),
						InputParametersMapping.class);
				setIBSExtOperatorProperties(ipmParamMapping, entryMap, testInputMapping);}
				entryMap.put("Operator Name", "InvokeExternal_" + operators.getName());
				entryMap.put("Service Name", "serviceName");
				entryMap.put("Api Name", "ApiName");
				entryMap.put("Security", operators.getBusinessSettings().getSecurity());
			} catch (JsonParseException e) {
				LOGGER.info("JsonParseException occurred while creating CustomPreProcessorIBS :: ", e);
			} catch (JsonMappingException e) {
				LOGGER.info("JsonMappingException occurred while creating CustomPreProcessorIBS :: ", e);
			} catch (IOException e) {
				LOGGER.info("IOException occurred while creating CustomPreProcessorIBS :: ", e);
			}
		}
		entryMap.put("Input Mapping Parameter", gson.toJson(testInputMapping));
		entryMap.put("Input Business Entity", operators.getBusinessSettings().getInputBeType());
		entryMap.put("InputBE Definition", gson.toJson(inputBe));
		entryMap.put("Input BE BUK Attributes", gson.toJson(operators.getBusinessSettings().getInputBEBUKAttributes()));
		if (operators.getType().equalsIgnoreCase(CGConstants.INVOKE_BS)) {
			entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
		} else {
			entryMap.put(CGConstants.HTTP_METHOD, operators.getBusinessSettings().getHttpMethod());
		}
		if (operators.getType().equalsIgnoreCase(CGConstants.INVOKE_BS)) {
			entryMap.put("isBatchable", "false");
		} else {
			entryMap.put("isBatchable",String.valueOf(operators.getBusinessSettings().isBatchable()));
		}
		return entryMap;
	}

	private void setIBSExtOperatorProperties(InputParametersMapping ipmParamMapping, Map<String, String> entryMap,
			Map<String, Object> testInputMapping) {
		testInputMapping.put("inputJSON", ipmParamMapping.getContentParam());
		testInputMapping.put("pathParam", ipmParamMapping.getPathParam());
		testInputMapping.put("queryParam", ipmParamMapping.getQueryParam());
		entryMap.put("Headers", ipmParamMapping.getHeaderParam());
			
	}
}