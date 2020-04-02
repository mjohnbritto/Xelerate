package com.suntecgroup.nifi.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
public class NifiCustomInvokeHttpProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiCustomInvokeHttpProcessor.class);

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
	 * @throws JSONException
	 */
	public TemplateProcessor generateInvokeHttpReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String[] relationship, String[] OpenRelationship, String processorName,
			int processorPosition) throws CGException, JSONException {
		LOGGER.info("Create NifiInvokeHttp Request for ::" + currentOperator.getName() + "of the Operator :"
				+ currentOperator.getType());
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();
		int canvasPosition = 1;
		List<TemplateRelationships> relationshipList = new ArrayList<TemplateRelationships>();
		// Processor Relationships
		if (processorName.equals(CGConstants.NIFI_UPDATE_START_IHTTP)
				|| processorName.equals(CGConstants.NIFI_UPDATE_END_IHTTP)
				|| processorName.equals(CGConstants.NIFI_UPDATE_FAILURE_IHTTP)) {
			for (String x : relationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(x);
				r.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
				relationshipList.add(r);
			}
			for (String y : OpenRelationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(y);
				r.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
				relationshipList.add(r);
			}
		} else if (CGConstants.MERGE.equalsIgnoreCase(currentOperator.getType())) {
			relationshipList
					.add(new TemplateRelationships(CGConstants.BUSINESS_ERRORS, CGConstants.AUTO_TERMINATE_TRUE));
			relationshipList.add(new TemplateRelationships(CGConstants.ORIGINAL, CGConstants.AUTO_TERMINATE_TRUE));
			relationshipList
					.add(new TemplateRelationships(CGConstants.RETRY_RELATIONSHIP, CGConstants.AUTO_TERMINATE_TRUE));
			relationshipList
					.add(new TemplateRelationships(CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.AUTO_TERMINATE_TRUE));
		} else if (CGConstants.REST_OUTPUT_CHANNEL_INTEGRATION.equalsIgnoreCase(currentOperator.getType())) {
			relationshipList
					.add(new TemplateRelationships(CGConstants.BUSINESS_ERRORS, CGConstants.AUTO_TERMINATE_TRUE));
			relationshipList.add(new TemplateRelationships(CGConstants.ORIGINAL, CGConstants.AUTO_TERMINATE_TRUE));
			relationshipList
					.add(new TemplateRelationships(CGConstants.RETRY_RELATIONSHIP, CGConstants.AUTO_TERMINATE_TRUE));
			relationshipList
					.add(new TemplateRelationships(CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.AUTO_TERMINATE_TRUE));
			relationshipList.add(new TemplateRelationships(CGConstants.RESPONSE, CGConstants.AUTO_TERMINATE_TRUE));
		} else {
			for (String x : relationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(x);
				r.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
				relationshipList.add(r);
			}
			for (String y : OpenRelationship) {
				TemplateRelationships r = new TemplateRelationships();
				r.setName(y);
				r.setAutoTerminate(CGConstants.FALSE_LOWERCASE);
				relationshipList.add(r);
			}
			TemplateRelationships business_errors = new TemplateRelationships();
			business_errors.setName(CGConstants.BUSINESS_ERRORS);

			if (currentOperator.getBusinessSettings().isBusinessFailureFlowExist()) {
				business_errors.setAutoTerminate(CGConstants.FALSE_LOWERCASE);
			} else {
				business_errors.setAutoTerminate(CGConstants.TRUE_LOWERCASE);
			}
			relationshipList.add(business_errors);

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
			entryPropList.add(propertyEntry);
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
		} else if (CGConstants.INVOKE_BS.equalsIgnoreCase(currentOperator.getType())
				|| CGConstants.INVOKE_BS_EXTERNAL.equalsIgnoreCase(currentOperator.getType())
				|| CGConstants.REST_OUTPUT_CHANNEL_INTEGRATION.equalsIgnoreCase(currentOperator.getType())) {
			if (CGConstants.NIFI_INVOKE_HTTP.equals(processorName)) {
				canvasPosition = 2;
			} else if (CGConstants.NIFI_UPDATE_FAILURE_IHTTP.equals(processorName)) {
				canvasPosition = 22;
			}
		} else if (CGConstants.END.equalsIgnoreCase(currentOperator.getType())) {
			if (CGConstants.NIFI_UPDATE_END_IHTTP.equals(processorName)) {
				canvasPosition = 11;
			} else {
				canvasPosition = 3;
			}
		} else if (CGConstants.MERGE.equalsIgnoreCase(currentOperator.getType())) {
			canvasPosition = 2;
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
		processor.setRelationshipsList(relationshipList);
		processor.setState(CGConstants.STOPPED);
		processor.setStyle(CGConstants.BLANK);
		LOGGER.info("NifiInvokeHttp is generated Successfully for ::" + currentOperator.getName() + "of "
				+ currentOperator.getType());
		return processor;
	}

	// Adding all operator (processor nifi) properties
	private Map<String, String> createEntryMap(BPFlowUI bpFlowRequest, Operators currentOperator, String processorName)
			throws JSONException {
		LOGGER.debug("Mapping Entry Values in InvokeHttp Processor for ::" + currentOperator.getName());
		Map<String, String> entryMap = new LinkedHashMap<String, String>();

		// bpName and operatorKey
		String operatorKey = currentOperator.getKey();
		String bpName = bpFlowRequest.getConfigureBusinessProcess().getFunctional().getBusinessProcessSetup()
				.getProcessName();

		// Constructing Service URL
		String serviceName = currentOperator.getBusinessSettings().getBusinessServiceName();
		String apiName = currentOperator.getBusinessSettings().getApiName();
		String uri = serviceName + CGConstants.CHARECTER_URLPATH_SPLIT + apiName;

		if (currentOperator.getType().equals(CGConstants.START) || currentOperator.getType().equals(CGConstants.END)) {
			entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.PROCESSOR_NAME_PROP_VALUE);
			entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);

			if (CGConstants.NIFI_UPDATE_START_IHTTP.equals(processorName)
					|| (CGConstants.NIFI_UPDATE_END_IHTTP.equals(processorName))) {
				entryMap.put(CGConstants.REMOTE_URL,
						"${suntec.transactions.status.protocol}${suntec.transactions.status.host}${suntec.transactions.status.port}${suntec.transactions.status.url}");
			} else {
				entryMap.put(CGConstants.REMOTE_URL,
						"${suntec.eventlog.protocol}${suntec.eventlog.host}${suntec.eventlog.port}${suntec.eventlog.url}");
			}
		} else if (CGConstants.MERGE.equals(currentOperator.getType())) {
			entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, "");
			entryMap.put(CGConstants.REMOTE_URL, "${suntec.bs." + serviceName + "}" + CGConstants.CHARECTER_URLPATH_SPLIT + uri);
			entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
		} else if (currentOperator.getType().equalsIgnoreCase(CGConstants.INVOKE_BS_EXTERNAL)) {
			setIBSExtOperatorProperties(entryMap, bpFlowRequest, currentOperator, processorName);
		} else {
			if (CGConstants.NIFI_UPDATE_FAILURE_IHTTP.equals(processorName)) {
				entryMap.put(CGConstants.REMOTE_URL,
						"${suntec.transactions.status.protocol}${suntec.transactions.status.host}${suntec.transactions.status.port}${suntec.transactions.status.url}");
				entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.PROCESSOR_NAME_PROP_VALUE);
				entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
			} else {
				entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.BLANK);
				if (CGConstants.REST_OUTPUT_CHANNEL_INTEGRATION.equals(currentOperator.getType())) {
					for (Property prop : currentOperator.getProperties()) {
						if (prop.getName().equals(CGConstants.URL_PROPERTY)) {
							if ("InternalAPI".equals(currentOperator.getBusinessSettings().getAPIInput())) {
								entryMap.put(CGConstants.REMOTE_URL, "${suntec.bs." + serviceName + "}" + CGConstants.CHARECTER_URLPATH_SPLIT + uri);
								entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
							} else {
								setRestOutputOperatorProperties(entryMap, bpFlowRequest, currentOperator, processorName);
							}
						}
					}
				} else {
					entryMap.put(CGConstants.REMOTE_URL, "${suntec.bs." + serviceName + "}" + CGConstants.CHARECTER_URLPATH_SPLIT + uri);
					entryMap.put(CGConstants.HTTP_METHOD, CGConstants.POST_HTTPMETHOD);
					entryMap.put(CGConstants.BUSINESS_ERROR_CODES,
							"[${" + bpName + "." + operatorKey + "." + CGConstants.BUSINESS_ERRORCODES + "}]");
				}
			}
		}

		entryMap.put(CGConstants.TRANSACTION_URL,
				"${suntec.transactions.status.protocol}${suntec.transactions.status.host}${suntec.transactions.status.port}");

		entryMap.put(CGConstants.BUSINESS_ERROR_CODES,
				"[${" + bpName + "." + operatorKey + "." + CGConstants.BUSINESS_ERRORCODES + "}]");
		entryMap.put(CGConstants.CONTENT_TYPE, CGConstants.CONTENTTYPE_APPLICATION_JSON);
		entryMap.put(CGConstants.IDLE_CONNECTION_MAXPOOL_SIZE,
				"${" + bpName + "." + operatorKey + "." + CGConstants.IDLE_CONNECTION_MAXPOOL_SIZE + "}");
		entryMap.put(CGConstants.IDLE_CONNECTION_ALIVE_DURATION,
				"${" + bpName + "." + operatorKey + "." + CGConstants.IDLE_CONNECTION_ALIVE_DURATION + "}");
		LOGGER.debug("Entry values are mapped in InvokeHttpOperators ::");
		return entryMap;
	}

	// IBS EXTERNAL PROPERTIES SETTING TO ENTRY MAP
	private void setIBSExtOperatorProperties(Map<String, String> entryMap, BPFlowUI bpFlowRequest,
			Operators currentOperator, String processorName) throws JSONException {
		JSONParser parser = new JSONParser();
		String ibs_url = CGConstants.BLANK;
		String security = currentOperator.getBusinessSettings().getSecurity();
		JSONObject securityJson = null;
		try {

			for (Property prop : currentOperator.getProperties()) {
				if (prop.getName().equals(CGConstants.URL_PROPERTY)) {
					ibs_url = prop.getValue();
					if (StringUtils.containsAny("/{", ibs_url)) {
						ibs_url = ibs_url.replace("/{", "/${");
					}
				}
			}

			if (!StringUtils.isEmpty(currentOperator.getBusinessSettings().getInputParametersMapping())) {
				JSONObject inputParams = (JSONObject) parser
						.parse(currentOperator.getBusinessSettings().getInputParametersMapping());
				JSONObject queryParam = (JSONObject) parser.parse(inputParams.get("queryParam").toString());
				boolean isChanged = false;
				if (queryParam != null) {
					Iterator<?> queryIterator = queryParam.keySet().iterator();
					while (queryIterator.hasNext()) {
						String queryP = (String) queryIterator.next();
						if (queryP != null) {
							if (!isChanged) {
								isChanged = true;
								ibs_url = ibs_url.concat("?" + queryP + "=${" + queryP + "}");
							} else {
								ibs_url = ibs_url.concat("&" + queryP + "=${" + queryP + "}");
							}
						}
					}
				}
				/*securityJson = (JSONObject) parser.parse(security);
				if (null != securityJson) {
					if (null != securityJson.get("username")) {
						entryMap.put(CGConstants.BASIC_AUTHENTICATION_USERNAME,
								securityJson.get("username").toString());
					}
					if (null != securityJson.get("password")) {
						entryMap.put(CGConstants.BASIC_AUTHENTICATION_PASSWORD,
								securityJson.get("password").toString());
					}
				}*/
			}
			if(null != security){
				securityJson = (JSONObject) parser.parse(security);
				if (null != securityJson) {
					if (null != securityJson.get("username")) {
						entryMap.put(CGConstants.BASIC_AUTHENTICATION_USERNAME,
							securityJson.get("username").toString());
					}
					if (null != securityJson.get("password")) {
						entryMap.put(CGConstants.BASIC_AUTHENTICATION_PASSWORD,
							securityJson.get("password").toString());
					}
				}
			}
		} catch (ParseException e) {
			LOGGER.error("Exception at IBS External - setIBSExtOperatorProperties :: ", e);
		}
		LOGGER.info("IBS External Url  :: ".concat(ibs_url));


		String proxyHost = env.getProperty("proxy_host");
		String proxyPort = env.getProperty("proxy_port");
		entryMap.put(CGConstants.HTTP_METHOD, currentOperator.getBusinessSettings().getHttpMethod());
		entryMap.put(CGConstants.REMOTE_URL, ibs_url);
		entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.BLANK);
		entryMap.put(CGConstants.PROXY_HOST, proxyHost);
		entryMap.put(CGConstants.PROXY_PORT, proxyPort);
		entryMap.put(CGConstants.BUSINESS_ERROR_CODES, "[]");

	}

	// REST Output Channel PROPERTIES SETTING TO ENTRY MAP
	private void setRestOutputOperatorProperties(Map<String, String> entryMap, BPFlowUI bpFlowRequest,
			Operators currentOperator, String processorName) throws JSONException {
		JSONParser parser = new JSONParser();
		String ext_url = CGConstants.BLANK;
		String security = currentOperator.getBusinessSettings().getSecurity();
		JSONObject securityJson = null;
		try {

			for (Property prop : currentOperator.getProperties()) {
				if (prop.getName().equals(CGConstants.URL_PROPERTY)) {
					ext_url = prop.getValue();
					if (StringUtils.containsAny("/{", ext_url)) {
						ext_url = ext_url.replace("/{", "/${");
					}
				}
			}

			if (!StringUtils.isEmpty(currentOperator.getBusinessSettings().getInputParametersMapping())) {
				JSONObject inputParams = (JSONObject) parser
						.parse(currentOperator.getBusinessSettings().getInputParametersMapping());
				JSONObject queryParam = (JSONObject) parser.parse(inputParams.get("queryParam").toString());
				boolean isChanged = false;
				if (queryParam != null) {
					Iterator<?> queryIterator = queryParam.keySet().iterator();
					while (queryIterator.hasNext()) {
						String queryP = (String) queryIterator.next();
						if (queryP != null) {
							if (!isChanged) {
								isChanged = true;
								ext_url = ext_url.concat("?" + queryP + "=${" + queryP + "}");
							} else {
								ext_url = ext_url.concat("&" + queryP + "=${" + queryP + "}");
							}
						}
					}
				}
				securityJson = (JSONObject) parser.parse(security);
				if (null != securityJson) {
					if (null != securityJson.get("username")) {
						entryMap.put(CGConstants.BASIC_AUTHENTICATION_USERNAME,
								securityJson.get("username").toString());
					}
					if (null != securityJson.get("password")) {
						entryMap.put(CGConstants.BASIC_AUTHENTICATION_PASSWORD,
								securityJson.get("password").toString());
					}
				}
			}
		} catch (ParseException e) {
			LOGGER.error("Exception at IBS External - setRestOutputOperatorProperties :: ", e);
		}
		LOGGER.info("REST output URL :: ".concat(ext_url));

		String proxyHost = env.getProperty("proxy_host");
		String proxyPort = env.getProperty("proxy_port");
		entryMap.put(CGConstants.HTTP_METHOD, currentOperator.getBusinessSettings().getHttpMethod());
		entryMap.put(CGConstants.REMOTE_URL, ext_url);
		entryMap.put(CGConstants.ATTRIBUTES_TO_SEND, CGConstants.BLANK);
		entryMap.put(CGConstants.PROXY_HOST, proxyHost);
		entryMap.put(CGConstants.PROXY_PORT, proxyPort);

	}

}