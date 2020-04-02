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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.suntecgroup.nifi.config.CGConfigurationProperty;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.InputParam;
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
 * NifiInvokeHttpProcessor - A class having the logic for implementing
 * NifiInvokeHttpProcessor.
 */
@Component
public class NifiInvokeHttpProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiInvokeHttpProcessor.class);

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private MetaConfigClient metaConfigClient;

	@Autowired
	private Environment env;

	/**
	 * generateInvokeHttpReq - This method having logic to generate nifi
	 * InvokeHttpProcessor.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @param currentOperator
	 *            - holds the currentOperator data information of Operators type
	 * @param relationship
	 *            - holds the relationship data of String type
	 * @param OpenRelationship
	 *            -
	 * @return - returns Processor object response
	 */
	public TemplateProcessor generateInvokeHttpReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String[] relationship, String[] OpenRelationship, String processorName,
			int processorPostion) throws CGException {
		LOGGER.info("Create NifiInvokeHttp Request for ::" + currentOperator.getName() + "of the Operator :"
				+ currentOperator.getType());
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();
		int canvasPosition = 1;
		List<TemplateRelationships> relationShipList = new ArrayList<TemplateRelationships>();
		if (processorName.equals(CGConstants.NIFI_UPDATE_START_IHTTP)
				|| processorName.equals(CGConstants.NIFI_UPDATE_END_IHTTP)
				|| processorName.equals(CGConstants.NIFI_UPDATE_FAILURE_IHTTP)) {
			for (String x : relationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(x);
				relationShipList.add(r);
				r.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
			}
			for (String y : OpenRelationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(y);
				r.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
				relationShipList.add(r);
			}
			relationShipList
					.add(new TemplateRelationships(CGConstants.BUSINESS_ERRORS, CGConstants.AUTO_TERMINATE_TRUE));
		} else if (processorName.equals(CGConstants.NIFI_PP_INVOKE_HTTP)) {
			for (String x : relationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(x);
				r.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
				if (CGConstants.ORIGINAL.equals(x)) {
					r.setAutoTerminate(CGConstants.FALSE_LOWERCASE);
				} else if (CGConstants.RESPONSE.equals(x)) {
					r.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
				}
				relationShipList.add(r);
			}
			for (String y : OpenRelationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(y);
				r.setAutoTerminate(CGConstants.FALSE_LOWERCASE);
				if (CGConstants.ORIGINAL.equals(y)) {
					r.setAutoTerminate(CGConstants.FALSE_LOWERCASE);
				} else if (CGConstants.RESPONSE.equals(y)) {
					r.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
				}
				relationShipList.add(r);
			}
			relationShipList
			.add(new TemplateRelationships(CGConstants.BUSINESS_ERRORS, CGConstants.AUTO_TERMINATE_TRUE));
		} else {
			if (processorName.equals(CGConstants.NIFI_INVOKE_HTTP)) {
				relationShipList
						.add(new TemplateRelationships(CGConstants.BUSINESS_ERRORS, CGConstants.AUTO_TERMINATE_TRUE));
			}
			for (String x : relationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(x);
				r.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
				relationShipList.add(r);
			}
			for (String y : OpenRelationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(y);
				r.setAutoTerminate(CGConstants.FALSE_LOWERCASE);
				relationShipList.add(r);
			}
		}

		Map<String, Float> metaConfigScale = metaConfigClient.getMetaConfig(currentOperator.getType(), processorName);
		Map<String, String> metaConfigDefault = metaConfigClient.getDefaultPropertyValues();

		CGUtils.setPropertyValue(config, currentOperator, metaConfigScale, metaConfigDefault);

		TemplateProperties properties = new TemplateProperties();
		TemplateDescriptors descriptors = new TemplateDescriptors();

		List<TemplateEntry> entryPropList = new ArrayList<TemplateEntry>();
		List<TemplateEntryDesc> entryDescList = new ArrayList<TemplateEntryDesc>();

		Map<String, String> entryMap = createEntryMap(bpFlowRequest, currentOperator, processorName);
		for (Map.Entry<String, String> entry : entryMap.entrySet()) {
			TemplateEntryDesc descEntry = new TemplateEntryDesc();
			descEntry.setKey(entry.getKey());

			TemplateValue value = new TemplateValue();
			value.setName(entry.getKey());
			descEntry.setValue(value);
			entryDescList.add(descEntry);

			TemplateEntry propertyEntry = new TemplateEntry();
			propertyEntry.setKey(entry.getKey());
			if (!StringUtils.isBlank(entry.getValue())) {
				propertyEntry.setValue(entry.getValue());
			}
			Property property = new Property();

			if (null != currentOperator.getProperties()) {

				if (property.isMandatory() == true && property.getName().equals(CGConstants.HTTP_METHOD)) {
					propertyEntry.setKey(entry.getKey());
					propertyEntry.setValue(property.getValue());
				}
				if (property.isMandatory() == true && property.getName().equals(CGConstants.REMOTE_URL)) {
					propertyEntry.setKey(entry.getKey());
					propertyEntry.setValue(property.getValue());
				}

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
		processor.setType(property.getCustomInvokeHttpComponentType());
		processor.setName(currentOperator.getName() + "-" + currentOperator.getType() + "-"
				+ property.getCustominvokeHttpAppName());

		if (CGConstants.START.equalsIgnoreCase(currentOperator.getType())) {
			if (CGConstants.NIFI_UPDATE_START_IHTTP.equals(processorName)) {
				canvasPosition = 12;
			} else {
				canvasPosition = 4;
			}
		} else if (CGConstants.INVOKE_BS_EXTERNAL.equalsIgnoreCase(currentOperator.getType())) {
			if (CGConstants.NIFI_INVOKE_HTTP.equals(processorName)) {
				canvasPosition = 2;
			} else if (CGConstants.NIFI_UPDATE_FAILURE_IHTTP.equals(processorName)) {
				canvasPosition = 22;
			} else {
				canvasPosition = processorPostion;
			}
		} else if (CGConstants.END.equalsIgnoreCase(currentOperator.getType())) {
			if (CGConstants.NIFI_UPDATE_END_IHTTP.equals(processorName)) {
				canvasPosition = 11;
			} else {
				canvasPosition = 3;
			}
		} else if (CGConstants.FILE_CHANNEL_INTEGRATION_INPUT.equalsIgnoreCase(currentOperator.getType())) {
			canvasPosition = 22;
		} else if (CGConstants.MERGE.equalsIgnoreCase(currentOperator.getType())) {
			canvasPosition = 22;
		} else if (CGConstants.INVOKE_BS.equalsIgnoreCase(currentOperator.getType())) {
			canvasPosition = processorPostion;
		} else if (processorPostion > 0) {
			canvasPosition = processorPostion;
		} else {
			canvasPosition = 21;
		}

		position.setX(String.valueOf(BPCanvasUtils.getXByPosition(canvasPosition)));
		position.setY(String.valueOf(BPCanvasUtils.getYByPosition(canvasPosition)));
		processor.setId(clientID);
		processor.setParentGroupId(theTarget.getProcessGroupID());
		processor.setPosition(position);

		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipList);
		processor.setState("STOPPED");
		processor.setStyle("");
		LOGGER.info("NifiInvokeHttp Request is generated Successfully for ::" + currentOperator.getName() + "of "
				+ currentOperator.getType());
		return processor;
	}

	private Map<String, String> createEntryMap(BPFlowUI bpFlowRequest, Operators currentOperator,
			String ProcessorName) {
		
		LOGGER.debug("Mapping Entry Values in InvokeHttp Processor for ::" + currentOperator.getName());
		LOGGER.info("Operator Type name :: " + currentOperator.getType());
		String bpName = bpFlowRequest.getConfigureBusinessProcess().getFunctional().getBusinessProcessSetup()
				.getProcessName();

		String method = "";
		Map<String, String> entryMap = new LinkedHashMap<String, String>();

		if (currentOperator.getType().equals(CGConstants.START) || currentOperator.getType().equals(CGConstants.END)) {
			if (CGConstants.NIFI_UPDATE_START_IHTTP.equals(ProcessorName)
					|| (CGConstants.NIFI_UPDATE_END_IHTTP.equals(ProcessorName))) {
				entryMap.put(CGConstants.REMOTE_URL,
						"${suntec.transactions.status.protocol}${suntec.transactions.status.host}${suntec.transactions.status.port}${suntec.transactions.status.url}");
				entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.PROCESSOR_NAME_PROP_VALUE);
				entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
			} else {
				entryMap.put(CGConstants.REMOTE_URL,
						"${suntec.eventlog.protocol}${suntec.eventlog.host}${suntec.eventlog.port}${suntec.eventlog.url}");
				entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.PROCESSOR_NAME_PROP_VALUE);
				entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
			}
			method = CGConstants.POST_HTTPMETHOD;
			entryMap.put(CGConstants.TRANSACTION_URL,
					"${suntec.transactions.status.protocol}${suntec.transactions.status.host}${suntec.transactions.status.port}");
		} else if (CGConstants.NIFI_PP_INVOKE_HTTP.equals(ProcessorName)) {
			entryMap.put(CGConstants.REMOTE_URL,
					"${suntec.eventlog.protocol}${suntec.eventlog.host}${suntec.eventlog.port}${suntec.eventlog.url}");
			entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.PROCESSOR_NAME_PROP_VALUE);
			entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
		} else {
			if (CGConstants.NIFI_UPDATE_FAILURE_IHTTP.equals(ProcessorName)) {
				entryMap.put(CGConstants.REMOTE_URL,
						"${suntec.transactions.status.protocol}${suntec.transactions.status.host}${suntec.transactions.status.port}${suntec.transactions.status.url}");
				entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.PROCESSOR_NAME_PROP_VALUE);
				entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
				method = CGConstants.POST_HTTPMETHOD;
			} else {
				// Constructing Service URL
				String serviceName = currentOperator.getBusinessSettings().getBusinessServiceName();
				String apiName = currentOperator.getBusinessSettings().getApiName();
				String uri = serviceName + CGConstants.CHARECTER_URLPATH_SPLIT + apiName;
				if (CGConstants.NIFI_INVOKE_HTTP.equals(ProcessorName)) {

					String ibs_url = CGConstants.BLANK;
					String params_final = CGConstants.BLANK;

					for (InputParam mapp : currentOperator.getInputMapping()) {

						String attributeName = mapp.getName();
						String paramSplit = CGConstants.CHARECTER_URLPATH_SPLIT;
						String querySplit = CGConstants.CHARECTER_URLQUERY_SPLIT;
						String constructPathParam = "${" + attributeName + "}";
						String constructQueryParam = attributeName + "=${" + attributeName + "}";
						String constructQueryPathParams = CGConstants.BLANK;

						if (mapp.getTypeParameter().equals(CGConstants.PATH_PARAM)) {
							params_final = params_final.concat(paramSplit).concat(constructPathParam);
						}
						if (mapp.getTypeParameter().equals(CGConstants.QUERY_PARAM)) {
							params_final = params_final.concat(querySplit).concat(constructQueryParam);
						}
						if (mapp.getTypeParameter().equals(CGConstants.PATH_PARAM)
								&& mapp.getTypeParameter().equals(CGConstants.QUERY_PARAM)) {
							constructQueryPathParams = paramSplit.concat(constructPathParam).concat(querySplit)
									.concat(constructQueryParam);
							params_final = params_final.concat(constructQueryPathParams);
						}
					}
					for (Property prop : currentOperator.getProperties()) {
						if (prop.getName().equals(CGConstants.URL_PROPERTY)) {
							ibs_url = prop.getValue();
						}
					}
					if (null != params_final) {
						ibs_url = ibs_url.concat(params_final);
					}

					LOGGER.info("IBS External Url  :: ".concat(ibs_url));
					String proxyHost = env.getProperty("proxy_host");
					String proxyPort = env.getProperty("proxy_port");
					entryMap.put(CGConstants.HTTP_METHOD, currentOperator.getBusinessSettings().getHttpMethod());
					entryMap.put(CGConstants.REMOTE_URL, ibs_url);
					entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.BLANK);
					entryMap.put(CGConstants.PROXY_HOST, proxyHost);
					entryMap.put(CGConstants.PROXY_PORT, proxyPort);

				} else {
					if(currentOperator.getType().equals(CGConstants.INVOKE_BS_EXTERNAL)){
						entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, "");
						entryMap.put(CGConstants.REMOTE_URL,
								"${suntec.eventlog.protocol}${suntec.eventlog.host}${suntec.eventlog.port}${suntec.eventlog.url}");
						entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
					}else{
					entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, "");
					entryMap.put(CGConstants.REMOTE_URL,
							"${suntec.bs.protocol}${suntec.bs.host}${suntec.bs.port}/".concat(uri));
					entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
					}
					method = CGConstants.POST_HTTPMETHOD;
				}
			}

		}
		if (null != currentOperator.getBusinessSettings() && CGConstants.HTTP_GET_METHOD
				.equalsIgnoreCase(currentOperator.getBusinessSettings().getHttpMethod())) {
			entryMap.put(CGConstants.CONTENT_TYPE, CGConstants.CONTENTTYPE_DEFAULT);
		}
		if (CGConstants.POST_HTTPMETHOD.equals(method)) {
			entryMap.put(CGConstants.CONTENT_TYPE, CGConstants.CONTENTTYPE_APPLICATION_JSON);
		}
		
		entryMap.put(CGConstants.IDLE_CONNECTION_MAXPOOL_SIZE,
				"${" + bpName + "." + currentOperator.getKey() + "." + CGConstants.IDLE_CONNECTION_MAXPOOL_SIZE + "}");
		entryMap.put(CGConstants.IDLE_CONNECTION_ALIVE_DURATION, "${" + bpName + "." + currentOperator.getKey() + "."
				+ CGConstants.IDLE_CONNECTION_ALIVE_DURATION + "}");

		LOGGER.debug("Entry values are mapped in InvokeHttpOperators ::");
		return entryMap;
	}

}
