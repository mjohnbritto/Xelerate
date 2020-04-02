package com.suntecgroup.bpruntime.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.Asset;
import com.suntecgroup.bpruntime.bean.adminconsole.BPDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.BPState;
import com.suntecgroup.bpruntime.bean.adminconsole.DeployedTemplate;
import com.suntecgroup.bpruntime.bean.adminconsole.OperatorProperties;
import com.suntecgroup.bpruntime.bean.adminconsole.OperatorStatisticsData;
import com.suntecgroup.bpruntime.bean.adminconsole.OperatorType;
import com.suntecgroup.bpruntime.bean.adminconsole.Status;
import com.suntecgroup.bpruntime.constant.Constant;
import com.suntecgroup.bpruntime.dao.DeploymentDAO;
import com.suntecgroup.bpruntime.dao.SessionManagerDao;
import com.suntecgroup.bpruntime.service.NifiService;
import com.suntecgroup.bpruntime.service.ProcessInitiateService;
import com.suntecgroup.bpruntime.service.SessionManagerService;

@Service
public class ProcessInitiateServiceImpl implements ProcessInitiateService {

	private String bpFlowJson;

	@Autowired
	NifiService nifiService;

	@Autowired
	private Environment env;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SessionManagerDao sessionManagerDAO;

	@Autowired
	private DeploymentDAO deploymentDAO;

	@Autowired
	SessionManagerService sessionManagerService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInitiateServiceImpl.class);

	@Override
	public String getBpList(boolean tryOnce) {
		List<DeployedTemplate> deployedTemplates = new ArrayList<DeployedTemplate>();
		Gson gson = new Gson();
		try {
			deployedTemplates = deploymentDAO.getAllDeployedTemplates();
			if (null != deployedTemplates && deployedTemplates.size() > 0) {
				for (DeployedTemplate template : deployedTemplates) {
					template.setBpStatus(sessionManagerService.getBpStatus(template.getBpTemplateId()));
					template.setBpSessionCount(sessionManagerService.getSessionCountForBp(template.getBpTemplateId()));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
			if (!tryOnce) {
				try {
					Thread.sleep(1000);
					return getBpList(true);
				} catch (InterruptedException e) {
					LOGGER.error("Exception occurred while attempting to wait and fetch BP list - ", e);
				}
			}
		}
		if (!tryOnce && null == deployedTemplates) {
			try {
				Thread.sleep(1000);
				return getBpList(true);
			} catch (InterruptedException e) {
				LOGGER.error("Exception occurred while attempting to wait and fetch BP list - ", e);
			}
		}
		if (null == deployedTemplates) {
			return gson.toJson(new ArrayList<DeployedTemplate>());
		} else {
			return gson.toJson(deployedTemplates);
		}
	}

	@Override
	public String updateBpState(BPState bpState) {
		String response = nifiService.updateBpState(bpState);
		return response;
	}

	@Override
	public String getBpDetails(String department, String module, String release, int artifact_id, String assetType,
			String assetName) {
		String bpFlowRequest = null;
		Object version = "";
		ApiResponse<?> bpFlowResponse = sessionManagerDAO.getBPAsset(department, module, release, artifact_id,
				assetType, assetName, version);

		try {
			Asset bpAssetData = (Asset) bpFlowResponse.getResponseDetails();
			if (bpAssetData == null) {
				throw new Exception(Constant.DATA_NOT_FOUND);
			} else {
				bpFlowRequest = bpAssetData.getAssetDetail();
				bpFlowJson = bpFlowRequest;
			}

		} catch (Exception e) {
			LOGGER.error("Exception while fetching data from getBpDetails() / data doesn't exits-" + e.getMessage());
		}

		return bpFlowRequest;

	}

	@Override
	public String getProcessVariable(String department, String module, String release, int artifact_id,
			String assetType, String bpName) {

		String processVariables = null;
		JSONArray processVariablesArray = null;
		String bpPropertiesJsonString = null;
		try {
			DeployedTemplate deployedTemplate = deploymentDAO.getDeployedTemplate(department, module, release,
					artifact_id, assetType, bpName);
			if (null != deployedTemplate) {
				ResponseEntity<String> variableRegistryResponse = (ResponseEntity<String>) nifiService
						.getVariableRegistry(deployedTemplate.getBpGroupId());
				if (null != variableRegistryResponse) {
					JSONObject bpPropertiesResponseJson = new JSONObject(variableRegistryResponse.getBody());
					bpPropertiesResponseJson = bpPropertiesResponseJson.getJSONObject("variableRegistry");
					bpPropertiesJsonString = bpPropertiesResponseJson.getString("variables");
				}
			}
		} catch (JSONException e) {
			LOGGER.error("Json parsing exception in getProcessVariable() -" + e.getMessage());
		}

		bpFlowJson = getBpDetails(department, module, release, artifact_id, assetType, bpName);
		if (bpFlowJson != null) {
			try {

				JSONArray confPropertiesArray = new JSONArray(bpPropertiesJsonString);

				JSONObject bpFlowJsonObj = new JSONObject(bpFlowJson);
				JSONObject configureBusinessProcess = bpFlowJsonObj.getJSONObject("configureBusinessProcess");
				JSONObject functional = configureBusinessProcess.getJSONObject("functional");
				processVariablesArray = functional.getJSONArray("processVariables");

				if (processVariablesArray.length() > 0) {
					for (int i = 0; i < processVariablesArray.length(); i++) {
						JSONObject processVariable = processVariablesArray.getJSONObject(i);

						if (confPropertiesArray.length() > 0) {
							for (int j = 0; j < confPropertiesArray.length(); j++) {
								JSONObject confProperty = confPropertiesArray.getJSONObject(j);
								confProperty = confProperty.getJSONObject("variable");
								if (confProperty.getString("name").contains("processvariable."+processVariable.getString("name"))) {
									String processVariableDetailsStr = confProperty.getString("value");
									JSONObject pvDetails = new JSONObject(processVariableDetailsStr);
									JSONObject processVariableDetail = pvDetails.getJSONObject("value");
									JSONObject type = processVariable.getJSONObject("type");
									JSONObject value = processVariable.getJSONObject("value");
									JSONObject flags = processVariable.getJSONObject("flags");
									if (flags.getBoolean("isProfileableAtOperation")) {
										switch (type.getString("typeName")) {
										case "Number":
											value.remove("intValue");
											value.put("intValue", processVariableDetail.get("intValue"));
											break;
										case "String":
											value.remove("stringValue");
											value.put("stringValue", processVariableDetail.get("stringValue"));
											break;
										case "Boolean":
											value.remove("booleanValue");
											value.put("booleanValue", processVariableDetail.get("booleanValue"));
											break;
										case "DateTime":
											value.remove("dateValue");
											value.put("dateValue", processVariableDetail.get("dateValue"));
											break;

										default:
											break;
										}
									}
								}

							}
						}

					}
					processVariables = processVariablesArray.toString();
				}
			} catch (JSONException e) {
				LOGGER.error("Json parsing exception in getProcessVariable() -" + e);
			}

		}
		return processVariables;

	}

	@Override
	public ApiResponse<Object> updateBpDetails(BPDetails bpDetails) {

		HashMap<String, String> propertiesMap = new HashMap<String, String>();
		List<OperatorProperties> operatorProperties = bpDetails.getOperatorProperties();
		for (Iterator iterator = operatorProperties.iterator(); iterator.hasNext();) {
			OperatorProperties operatorProperty = (OperatorProperties) iterator.next();
			
			propertiesMap.put(
					bpDetails.getBpName().split("_")[0] + "." + bpDetails.getOperatorName() + "." + operatorProperty.getProperty(),
					operatorProperty.getValue());
		}
		DeployedTemplate deployedTemplate = deploymentDAO.getDeployedTemplate(bpDetails.getDepartment(),
				bpDetails.getModule(), bpDetails.getRelease(), bpDetails.getArtifactId(), bpDetails.getAssetType(),
				bpDetails.getAssetName());
		ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) nifiService
				.updateVariableRegistry(deployedTemplate.getBpGroupId(), propertiesMap);
		return response.getBody();
	}

	@Override
	public String getOperatorProperty(String department, String module, String release, int artifact_id,
			String assetType, String assetName, String operatorKey) {
		String operatorProperties = null;
		JSONArray operatorArray = null;
		String bpPropertiesJsonString = null;
		JSONArray confPropertiesArray = null;
		JSONArray operatorPropertiesArray = null;
		try {
			DeployedTemplate deployedTemplate = deploymentDAO.getDeployedTemplate(department, module, release,
					artifact_id, assetType, assetName);
			if (null != deployedTemplate) {
				ResponseEntity<String> variableRegistryResponse = (ResponseEntity<String>) nifiService
						.getVariableRegistry(deployedTemplate.getBpGroupId());
				if (null != variableRegistryResponse) {
					JSONObject bpPropertiesResponseJson = new JSONObject(variableRegistryResponse.getBody());
					bpPropertiesResponseJson = bpPropertiesResponseJson.getJSONObject("variableRegistry");
					bpPropertiesJsonString = bpPropertiesResponseJson.getString("variables");
				}
			}
		} catch (JSONException e) {
			LOGGER.error("Json parsing exception in getOperatorProperty() -" + e);
		}

		bpFlowJson = getBpDetails(department, module, release, artifact_id, assetType, assetName);
		if (bpFlowJson != null) {
			try {
				confPropertiesArray = new JSONArray(bpPropertiesJsonString);
				JSONObject bpFlowJsonObj = new JSONObject(bpFlowJson);
				operatorArray = bpFlowJsonObj.getJSONArray("operators");

				// start- Get smart connector's properties
				JSONArray connections = bpFlowJsonObj.getJSONArray("connections");
				for (int index = 0; index < connections.length(); index++) {
					JSONObject connection = connections.getJSONObject(index);
					JSONObject uiAttributes = connection.getJSONObject("ui_attributes");
					JSONObject linkProperties = connection.getJSONObject("link_properties");
					if (uiAttributes.has("type") && "smart".equalsIgnoreCase(uiAttributes.getString("type"))) {
						JSONObject smartOperator = new JSONObject();
						smartOperator.put("name", uiAttributes.getString("key"));
						smartOperator.put("properties", linkProperties.getJSONArray("properties"));
						operatorArray.put(smartOperator);
					}
				}
				// end- Get smart connector's properties

				if (operatorArray.length() > 0) {
					for (int i = 0; i < operatorArray.length(); i++) {
						JSONObject operator = operatorArray.getJSONObject(i);
						if (operator.getString("key").equalsIgnoreCase(operatorKey)) {
							if (operator.has("properties")) {
								operatorPropertiesArray = operator.getJSONArray("properties");
							} else {
								operatorPropertiesArray = null;
							}
							if (operatorPropertiesArray != null && operatorPropertiesArray.length() > 0) {
								for (int j = 0; j < operatorPropertiesArray.length(); j++) {
									JSONObject operatorProperty = operatorPropertiesArray.getJSONObject(j);
									if (operatorProperty.getBoolean("isProfileableAtOperation")) {
										if (confPropertiesArray.length() > 0) {
											for (int k = 0; k < confPropertiesArray.length(); k++) {
												JSONObject confProperty = confPropertiesArray.getJSONObject(k);
												confProperty = confProperty.getJSONObject("variable");
												if (confProperty.getString("name").contains(
														operatorKey + "." + operatorProperty.getString("name"))) {
													operatorProperty.remove("value");
													operatorProperty.put("value", confProperty.getString("value"));
													break;
												}
											}
										}
									}
								}
								operatorProperties = operatorPropertiesArray.toString();
							}
							break;
						}
					}
				}
			} catch (JSONException e) {
				LOGGER.error("Json parsing exception in getOperatorProperty() -" + e);
			}
		}

		return operatorProperties;
	}

	/**
	 * getOperatorData @param String operatorName, String operatorType, String
	 * processGroupId @return ApiResponse @throws
	 */
	public ApiResponse<?> getOperatorData(String operatorName, String operatorType, String processGroupId) {
		JSONObject statusHistoryResponseJson = null;
		JSONObject statusHistory = null;
		JSONObject statusMetrics = null;
		JSONArray aggregateSnapshots = null;
		JSONObject node = null;
		List<JSONArray> statusSnapshots = new ArrayList<JSONArray>();
		JSONArray nodeSnapshots = null;
		OperatorStatisticsData operatorStatisticsData = null;
		List<OperatorStatisticsData> listoperatorStatisticsData = new ArrayList<OperatorStatisticsData>();
		String nifiProcessorId = getNifiProcessor(operatorName, operatorType, processGroupId);
		if (nifiProcessorId != null) {
			ResponseEntity<String> statusHistoryResponse = null;
			statusHistoryResponse = nifiService.getData(env.getProperty("nifi.instance.url")
					+ "/nifi-api/flow/processors/" + nifiProcessorId + "/status/history");
			if (statusHistoryResponse.getBody() != null) {
				try {
					boolean isCluster = false;
					int averageTaskNanos = 0;
					int count = 0;
					statusHistoryResponseJson = new JSONObject(statusHistoryResponse.getBody());
					if (statusHistoryResponseJson != null) {
						statusHistory = statusHistoryResponseJson.getJSONObject("statusHistory");
					}
					if (statusHistory != null) {
						aggregateSnapshots = statusHistory.getJSONArray("aggregateSnapshots");
						isCluster = "true".equals(env.getProperty("deployment.iscluster"));
						if (isCluster) {
							nodeSnapshots = statusHistory.getJSONArray("nodeSnapshots");
							for (int i = 0; i < nodeSnapshots.length(); i++) {
								node = (JSONObject) nodeSnapshots.get(i);
								statusSnapshots.add(node.getJSONArray("statusSnapshots"));
							}
						}
					}
					if (aggregateSnapshots != null && aggregateSnapshots.length() > 0) {
						int totalStatsCount;
						if (aggregateSnapshots.length() >= 10) {
							totalStatsCount = aggregateSnapshots.length() - 11;
						} else {
							totalStatsCount = -1;
						}
						for (int index = aggregateSnapshots.length() - 1; index > totalStatsCount; index--) {
							operatorStatisticsData = new OperatorStatisticsData();
							statusMetrics = aggregateSnapshots.getJSONObject(index).getJSONObject("statusMetrics");
							if (StringUtils.isEmpty(statusMetrics.getString("averageLineageDuration"))) {
								operatorStatisticsData.setAverageLineageDuration("");
							} else {
								operatorStatisticsData
										.setAverageLineageDuration(statusMetrics.getString("averageLineageDuration"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("averageTaskNanos"))) {
								operatorStatisticsData.setAverageTaskDuration("");
							} else {
								if (statusSnapshots.size() > 0) {
									averageTaskNanos = 0;
									count = 0;
									for (int i = 0; i < statusSnapshots.size(); i++) {
										if (((JSONArray) statusSnapshots.get(i)).length() > 0) {
											try {
												averageTaskNanos += Integer.parseInt(
														((JSONObject) ((JSONArray) statusSnapshots.get(i)).get(index))
																.getJSONObject("statusMetrics")
																.getString("averageTaskNanos"));
												count++;
											} catch (Exception e) {
												LOGGER.error("Exception Message", e);
												break;
											}
										}
									}
									if (averageTaskNanos > 0 && count != 0) {
										averageTaskNanos = averageTaskNanos / count;
									} else {
										averageTaskNanos = 0;
									}
									operatorStatisticsData.setAverageTaskDuration("" + averageTaskNanos);
								} else {
									operatorStatisticsData
											.setAverageTaskDuration(statusMetrics.getString("averageTaskNanos"));
								}
							}
							if (StringUtils.isEmpty(statusMetrics.getString("inputBytes"))) {
								operatorStatisticsData.setBytesIn("");
							} else {
								operatorStatisticsData.setBytesIn(statusMetrics.getString("inputBytes"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("outputBytes"))) {
								operatorStatisticsData.setBytesOut("");
							} else {
								operatorStatisticsData.setBytesOut(statusMetrics.getString("outputBytes"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("bytesRead"))) {
								operatorStatisticsData.setBytesRead("");
							} else {
								operatorStatisticsData.setBytesRead(statusMetrics.getString("bytesRead"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("bytesTransferred"))) {
								operatorStatisticsData.setBytesTransferred("");
							} else {
								operatorStatisticsData.setBytesTransferred(statusMetrics.getString("bytesTransferred"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("bytesWritten"))) {
								operatorStatisticsData.setBytesWritten("");
							} else {
								operatorStatisticsData.setBytesWritten(statusMetrics.getString("bytesWritten"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("inputCount"))) {
								operatorStatisticsData.setFlowFilesIn("");
							} else {
								operatorStatisticsData.setFlowFilesIn(statusMetrics.getString("inputCount"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("outputCount"))) {
								operatorStatisticsData.setFlowFilesOut("");
							} else {
								operatorStatisticsData.setFlowFilesOut(statusMetrics.getString("outputCount"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("flowFilesRemoved"))) {
								operatorStatisticsData.setFlowFilesRemoved("");
							} else {
								operatorStatisticsData.setFlowFilesRemoved(statusMetrics.getString("flowFilesRemoved"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("taskCount"))) {
								operatorStatisticsData.setTasks("");
							} else {
								operatorStatisticsData.setTasks(statusMetrics.getString("taskCount"));
							}
							if (StringUtils.isEmpty(statusMetrics.getString("taskMillis"))) {
								operatorStatisticsData.setTotalTaskDuration("");
							} else {
								operatorStatisticsData.setTotalTaskDuration(statusMetrics.getString("taskMillis"));
							}
							if (StringUtils.isEmpty(aggregateSnapshots.getJSONObject(index).getString("timestamp"))) {
								operatorStatisticsData.setTimestamp("");
							} else {
								operatorStatisticsData
										.setTimestamp(aggregateSnapshots.getJSONObject(index).getString("timestamp"));
							}

							listoperatorStatisticsData.add(operatorStatisticsData);
						}
						return new ApiResponse<List<?>>(Status.SUCCESS.getStatusCode(), Status.SUCCESS,
								"Operators statistics are fetched successfully for operator " + operatorName,
								listoperatorStatisticsData);
					} else {
						LOGGER.error(
								"Insufficient history, please try again later. Please try again later- may be the data is not there NiFi");
						return new ApiResponse<String>(Status.FAILURE.getStatusCode(), Status.FAILURE,
								"Insufficient history, please try again later.", null);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					LOGGER.error("Json parsing exception in getOperatorData() -" + e);
					return new ApiResponse<String>(Status.FAILURE.getStatusCode(), Status.FAILURE,
							"Json parsing exception", e.getMessage());
				}
			}
		} else {
			return new ApiResponse<String>(Status.FAILURE.getStatusCode(), Status.FAILURE,
					"Nifi Processor does not exist for given Suntec Operator " + operatorName + ".Please try again.",
					null);
		}

		return null;
	}

	/**
	 * getNifiProcessor @param operatorName, operatorType,
	 * processGroupId @return String @throws
	 */
	public String getNifiProcessor(String operatorName, String operatorType, String processGroupId) {
		String componentName = null;
		String componentId = null;
		JSONObject processorListJson = null;
		JSONObject component = null;
		JSONArray processorArray = null;
		ResponseEntity<String> processorsListResponse = null;
		processorsListResponse = nifiService.getData(
				env.getProperty("nifi.instance.url") + "/nifi-api/process-groups/" + processGroupId + "/processors");
		if (processorsListResponse.getBody() != null) {
			try {
				processorListJson = new JSONObject(processorsListResponse.getBody());
				processorArray = processorListJson.getJSONArray("processors");
				OperatorType operator_type = OperatorType.valueOf(operatorType.toUpperCase());
				if (processorArray.length() > 0) {
					for (int i = 0; i < processorArray.length(); i++) {
						component = new JSONObject();
						component = processorArray.getJSONObject(i).getJSONObject("component");
						componentName = component.getString("name");

						if (componentName.contains(operatorName)) {
							if (componentName.equalsIgnoreCase(
									operatorName + "-" + operatorType + "-" + operator_type.getProcessorType())) {
								componentId = component.getString("id");
								break;
							}
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Json parsing exception in getNifiProcessor() -" + e.getMessage());
				return "Json parsing exception:" + e.getMessage();
			}
		}
		return componentId;
	}

}
