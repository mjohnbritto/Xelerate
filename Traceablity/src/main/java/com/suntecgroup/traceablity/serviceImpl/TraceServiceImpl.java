package com.suntecgroup.traceablity.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.suntecgroup.traceablity.Beans.Buk;
import com.suntecgroup.traceablity.Beans.Constants;
import com.suntecgroup.traceablity.Beans.EventBean;
import com.suntecgroup.traceablity.service.NifiService;
import com.suntecgroup.traceablity.service.TraceService;

@Service
public class TraceServiceImpl implements TraceService {

	private static Logger logger = Logger.getLogger(TraceServiceImpl.class.getName());

	@Autowired
	NifiService nifiService;
	@Autowired
	Environment env;
	@Autowired
	private RestTemplate restTemplate;
	HashMap<String, JSONArray> operatortoSecondAPIMapping;
	HashMap<String, String> operatorNameMapping;
	HashMap<String, JSONArray> operatorToBukMapping;

	@Override
	public ResponseEntity<String> searchBUK(EventBean eventBean, boolean tryOnce) {
		try {
			String url = "http://localhost:" + env.getProperty("server.port") + env.getProperty("eventLogSearchBUK");
			HttpEntity<String> response = null;
			HttpEntity<String> requestEntity = null;
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			eventBean.setRunNumber("1");
			requestEntity = new HttpEntity<String>(eventBean.toJsonString(), headers);
			response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			if (null != response && null != response.getBody()) {
				return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>("No matching buk found!", HttpStatus.OK);
			}
		} catch (ResourceAccessException resourceAccessException) {
			try {
				if (tryOnce) {
					System.out.println("ResourceAccessException error happended");
					Thread.sleep(1000);
					return searchBUK(eventBean, false);
				} else {
					logger.error("Exception occurred! ", resourceAccessException);
					return new ResponseEntity<>("Something went wrong, Please try again later!", HttpStatus.OK);
				}
			} catch (InterruptedException exception) {
				logger.error("Exception occurred:" + exception.getMessage(), exception);
				return new ResponseEntity<>("Something went wrong, Please try again later!", HttpStatus.OK);
			}
		} catch (Exception exception) {
			logger.error("Exception occurred:" + exception.getMessage(), exception);
			return new ResponseEntity<>("Something went wrong, Please try again later!", HttpStatus.OK);
		}
	}

	public String getBukData(EventBean eventBean, HashMap<String, JSONArray> operatortoSecondAPIMapping,
			HashMap<String, String> operatorNameMapping, HashMap<String, JSONArray> operatorToBukMapping) {

		JSONArray jsonFinalConversion = new JSONArray();
		JSONArray jsonArrayTemp = new JSONArray();
		JSONObject responseTOUI = new JSONObject();
		JSONObject objectToSaveData = null;
		Buk[] bukList = eventBean.getBuk();
		JSONArray bukJSONArray = new JSONArray();
		JSONObject bukEntry = null;
		String attributeName = null;
		String attributeValue = null;
		try {
			for (int iCount = 0; iCount < bukList.length; iCount++) {
				Buk buk = bukList[iCount];
				attributeName = buk.getAttributeName();
				attributeValue = buk.getAttributeValue();
				bukEntry = new JSONObject();
				bukEntry.put(Constants.attributeValuec, attributeValue);
				bukEntry.put(Constants.attributeNamec, attributeName);
				bukJSONArray.put(bukEntry);
			}
			String fromOperator = eventBean.getfrom();
			String path = "";
			String toOperator = eventBean.getto();
			String concatOperator = "";
			String operatorNameHashMap = operatorNameMapping.get(fromOperator);
			if (operatorNameHashMap.contains("decision_matrix")) {
				path = eventBean.getPath();
				concatOperator = fromOperator + "-->" + toOperator + "-->" + path;
			} else {
				concatOperator = fromOperator + "-->" + toOperator;
			}
			if (operatorToBukMapping.containsKey(concatOperator)) {
				JSONArray bukJSONArrayObject = new JSONArray();
				bukJSONArrayObject = operatorToBukMapping.get(concatOperator);
				if (bukJSONArrayObject.toString().equals(bukJSONArray.toString())) {
					jsonFinalConversion = operatortoSecondAPIMapping.get(concatOperator);
					for (int jCount = 0; jCount < jsonFinalConversion.length(); jCount++) {
						objectToSaveData = jsonFinalConversion.getJSONObject(jCount);
						objectToSaveData.put("fromOperator", fromOperator);
						objectToSaveData.put("toOperator", toOperator);
					}
				}
				jsonArrayTemp.put(objectToSaveData);
				responseTOUI.put("transactionData", jsonArrayTemp);
			} else {
				return Constants.noDataFound;
			}
		} catch (Exception exception) {
			logger.error("Exception occurred:" + exception.getMessage(), exception);
			return "Please run traceData API first";
		}
		return responseTOUI.toString();
	}

	public String getTraceReport(EventBean eventBean, HashMap<String, JSONArray> operatortoSecondAPIMapping,
			HashMap<String, String> operatorNameMapping, HashMap<String, JSONArray> operatorToBukMapping) {

		boolean failedAtStart = false;
		JSONArray bukJSONArrayCopy;
		boolean isNonBEScenario = false;
		JSONArray connectionArray = new JSONArray();
		JSONArray componentArray = new JSONArray();
		JSONArray operatorArray = new JSONArray();
		JSONArray componentArrayName = new JSONArray();
		Queue<String> componentQueue = new LinkedList<>();
		HashMap<String, JSONArray> decisonBuk = new HashMap<>();
		HashMap<String, String> splitUUIDMapping = new HashMap<>();
		JSONArray attributesArrayfinal = new JSONArray();
		try {
			eventBean.setRunNumber("1");
			JSONArray finalBukArray = new JSONArray();
			Buk[] bukList = eventBean.getBuk();
			JSONObject bukJSON = new JSONObject();
			JSONArray bukJSONArray = new JSONArray();
			JSONArray bukJSONArrayObject = new JSONArray();
			JSONObject bukEntry = null;
			String attributeName = null;
			String attributeValue = null;
			JSONArray finalJson;
			JSONObject jsonFinalConversion = new JSONObject();
			List<JSONObject> responseToConsole = new ArrayList<>();
			for (int iCount = 0; iCount < bukList.length; iCount++) {
				Buk buk = bukList[iCount];
				attributeName = buk.getAttributeName();
				attributeValue = buk.getAttributeValue();
				bukEntry = new JSONObject();
				bukEntry.put(Constants.attributeNamec, attributeName);
				bukEntry.put(Constants.attributeValuec, attributeValue);
				bukJSONArray.put(bukEntry);
			}
			bukJSONArrayCopy = bukJSONArray;
			bukJSON.put(Constants.buk, bukJSONArray);
			bukJSONArrayObject.put(bukJSON);
			finalJson = bukJSONArrayObject;
			String flowFileUUID = getFlowFileUUID(eventBean);
			String clusterNodeID = "";
			if ("true".equals(env.getProperty("deployment.iscluster"))) {
				clusterNodeID = getClusterNodeID(eventBean);
			}
			logger.info("flowFileUUID=" + flowFileUUID);
			if (!StringUtils.isEmpty(flowFileUUID)) {
				Stack<String> uuidQueue = new Stack<>();
				Stack<String> clusterNodeIdQueue = new Stack<>();
				uuidQueue.add(flowFileUUID);
				clusterNodeIdQueue.add(clusterNodeID);
				do {
					flowFileUUID = uuidQueue.pop();
					String lineageResponse = nifiService.createLineageReqeust(flowFileUUID, clusterNodeID);
					if (null != lineageResponse) {
						JSONObject createLineageResponse = new JSONObject(lineageResponse);
						createLineageResponse = createLineageResponse.getJSONObject("lineage");
						if (null != createLineageResponse) {
							String getLineageDataUrl = createLineageResponse.getString("uri");
							lineageResponse = nifiService.getLineageData(getLineageDataUrl, clusterNodeID);
							nifiService.deteleLineageReqeust(getLineageDataUrl, clusterNodeID);
							if (null != lineageResponse) {
								finalBukArray = trace(lineageResponse, uuidQueue, clusterNodeIdQueue, finalJson,
										finalBukArray, finalJson, componentQueue, decisonBuk, attributesArrayfinal,
										connectionArray, operatorArray, bukJSONArrayCopy, isNonBEScenario,
										failedAtStart, componentArray, operatortoSecondAPIMapping, operatorNameMapping,
										operatorToBukMapping, componentArrayName, splitUUIDMapping);
							}
						}
					} else {
						return "Something went wrong! Please try again later.";
					}
				} while (!uuidQueue.isEmpty());
			} else {
				return "No Data found in Event logger OR problem in connecting target server";
			}
			System.out.println("operatortoSecondAPIMapping:" + operatortoSecondAPIMapping);
			System.out.println("componentArray" + componentArray);
			JSONObject tempObject2 = new JSONObject();
			operatorArray = new JSONArray();
			while (!componentQueue.isEmpty()) {
				tempObject2.put("key", componentQueue.poll());
				operatorArray.put(tempObject2);
				tempObject2 = new JSONObject();
			}
			if (failedAtStart == true) {
				return "Packet Failed at Start Operator";
			}
			if (operatorArray.length() == 0) {
				return "No data Exist for Given BUK";
			}
			if (null != finalBukArray) {
				jsonFinalConversion.put("traceReport", finalBukArray);
				jsonFinalConversion.put("connections", connectionArray);
				jsonFinalConversion.put("operators", operatorArray);
				responseToConsole.add(jsonFinalConversion);
			} else {
				jsonFinalConversion = null;
			}
			return jsonFinalConversion.toString();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			return exception.getMessage().toString();
		}
	}

	@Override
	public ResponseEntity<String> getTraceability(EventBean eventBean) {

		String fromOperator = eventBean.getfrom();
		if (fromOperator == null) {
			operatortoSecondAPIMapping = new HashMap<>();
			operatorNameMapping = new HashMap<>();
			operatorToBukMapping = new HashMap<>();
			String response = getTraceReport(eventBean, operatortoSecondAPIMapping, operatorNameMapping,
					operatorToBukMapping);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			String response = getBukData(eventBean, operatortoSecondAPIMapping, operatorNameMapping,
					operatorToBukMapping);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	public String getFlowFileUUID(EventBean eventBean) {
		try {
			String url = "http://localhost:" + env.getProperty("server.port") + env.getProperty("eventLogGetFlowFileUUID");
			HttpEntity<String> response = null;
			HttpEntity<String> requestEntity = null;
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			requestEntity = new HttpEntity<String>(eventBean.toJsonString(), headers);
			response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			JSONObject responseData = new JSONObject(response.getBody());
			if (null != responseData && responseData.has("successData") && null != responseData.get("successData")
					&& !"null".equalsIgnoreCase(responseData.getString("successData"))
					&& !StringUtils.isEmpty(responseData.getString("successData"))) {
				return responseData.getString("successData");
			} else {
				return null;
			}
		} catch (Exception exception) {
			logger.error("Exception occurred:" + exception.getMessage(), exception);
			return null;
		}
	}

	public String getClusterNodeID(EventBean eventBean) {
		try {
			String url = "http://localhost:" + env.getProperty("server.port") + env.getProperty("eventLogGetClusterNodeID");
			HttpEntity<String> response = null;
			HttpEntity<String> requestEntity = null;
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			requestEntity = new HttpEntity<String>(eventBean.toJsonString(), headers);
			response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			JSONObject responseData = new JSONObject(response.getBody());
			if (null != responseData && responseData.has("successData")
					&& !StringUtils.isEmpty(responseData.getString("successData"))) {
				return responseData.getString("successData");
			} else {
				return null;
			}
		} catch (Exception exception) {
			logger.error("Exception occurred:" + exception.getMessage(), exception);
			return null;
		}
	}

	public JSONArray trace(String response, Stack<String> uuidQueue, Stack<String> clusterNodeIdQueue,
			JSONArray bukJSON, JSONArray finalBukArray, JSONArray finalJson, Queue<String> componentQueue,
			HashMap<String, JSONArray> decisonBuk, JSONArray attributesArrayfinal, JSONArray connectionArray,
			JSONArray operatorArray, JSONArray bukJSONArrayCopy, boolean isNonBEScenario, boolean failedAtStart,
			JSONArray componentArray, HashMap<String, JSONArray> operatortoSecondAPIMapping,
			HashMap<String, String> operatorNameMapping, HashMap<String, JSONArray> operatorToBukMapping,
			JSONArray componentArrayName, HashMap<String, String> splitUUIDMapping) {
		JSONArray currentBuk = bukJSON;
		String Nextevent = null;
		String Firstevent = null;
		JSONObject eventData = null;
		JSONObject lineageResponse;
		try {
			HashMap<String, String> connectingLinks = new HashMap<>();
			lineageResponse = new JSONObject(response.toString());
			JSONObject lineage_object = new JSONObject(lineageResponse.getJSONObject(Constants.lineage).toString());
			JSONObject result_object = new JSONObject(lineage_object.getJSONObject(Constants.results).toString());
			JSONArray nodesArray = result_object.getJSONArray(Constants.nodes);
			JSONObject request_object = new JSONObject(lineage_object.getJSONObject(Constants.request).toString());
			String flowFileUuid = request_object.getString(Constants.uuid);
			Firstevent = flowFileUuid;
			Nextevent = flowFileUuid;
			String componentName = "";
			JSONArray linksArray = result_object.getJSONArray(Constants.links);
			for (int iCount = 0; iCount < linksArray.length(); iCount++) {
				JSONObject linkObject = linksArray.getJSONObject(iCount);
				String sourceidLinks = linkObject.getString(Constants.sourceId);
				String targetIdLinks = linkObject.getString(Constants.targetId);
				connectingLinks.put(sourceidLinks, targetIdLinks);
			}
			while (Nextevent != null) {
				Nextevent = connectingLinks.get(Firstevent);
				for (int icount = 0; icount < nodesArray.length(); icount++) {
					JSONObject currentNodeObject = nodesArray.getJSONObject(icount);
					String nodeId = currentNodeObject.getString(Constants.id);
					if (nodeId.equals(Nextevent)) {
						String clusterNodeIdentifier = "";
						if (currentNodeObject.has(Constants.clusterNodeIdentifier)) {
							clusterNodeIdentifier = currentNodeObject.getString(Constants.clusterNodeIdentifier);
						}
						eventData = nifiService.provenanceEventData(Nextevent, clusterNodeIdentifier);
						currentBuk = findBuk(eventData, currentBuk, isNonBEScenario);
						Nextevent = connectingLinks.get(Nextevent);
						icount = -1;
						if (currentNodeObject.length() >= 8) {
							JSONArray childUuid = currentNodeObject.getJSONArray(Constants.childUuids);
							if (childUuid.length() == 0) {
								JSONObject evendtDataResponse = new JSONObject(eventData.toString());
								JSONObject provenance_object = new JSONObject(
										evendtDataResponse.getJSONObject(Constants.provenanceEvent).toString());
								if (provenance_object.has(Constants.componentNameConst)) {
									componentName = provenance_object.getString(Constants.componentNameConst);
								}
								String eventType = provenance_object.getString(Constants.eventTypeConst);
								if (componentName.contains(Constants.decisionMatrix)
										&& eventType.equals(Constants.attributesModified)) {
									if (decisonBuk.containsKey(componentName)) {
										currentBuk = decisonBuk.get(componentName);
									} else {
										decisonBuk.put(componentName, currentBuk);
									}
								}
							} else {
								for (int jCount = 0; jCount < childUuid.length(); jCount++) {
									String childUuids = (String) childUuid.getString(jCount);
									if (uuidQueue.contains(childUuids) == false) {
										JSONObject evendtDataResponse = new JSONObject(eventData.toString());
										JSONObject provenance_object = new JSONObject(
												evendtDataResponse.getJSONObject(Constants.provenanceEvent).toString());
										if (provenance_object.has(Constants.componentNameConst)) {
											componentName = provenance_object.getString(Constants.componentNameConst);
										}
										if (componentName.contains(Constants.decisionMatrix)) {
											boolean isApplicableForTrace = decisionTracing(childUuids, eventData,
													currentBuk, isNonBEScenario);
											if (isApplicableForTrace == true) {
												splitUUIDMapping.put(childUuids, componentName);
												uuidQueue.add(childUuids);
												clusterNodeIdQueue.add(clusterNodeIdentifier);
											}
										} else {
											uuidQueue.add(childUuids);
											clusterNodeIdQueue.add(clusterNodeIdentifier);
										}
									}
								}
							}
						}
						finalBukArray = findEnd(eventData, currentBuk, finalBukArray, componentQueue,
								attributesArrayfinal, connectionArray, operatorArray, bukJSONArrayCopy, isNonBEScenario,
								failedAtStart, componentArray, operatortoSecondAPIMapping, operatorNameMapping,
								operatorToBukMapping, componentArrayName, splitUUIDMapping);
					}
				}
				finalJson = currentBuk;
			}
		} catch (JSONException exception) {
			logger.error("Exception Occurred:" + exception.getMessage(), exception);
		}
		return finalBukArray;
	}

	public JSONArray findBuk(JSONObject eventData, JSONArray initialBuk, boolean isNonBEScenario) {
		try {
			JSONObject evendtDataResponse = new JSONObject(eventData.toString());
			HashMap<Integer, Integer> connectingLinks1 = new HashMap<>();
			String eventType = null;
			JSONObject provenance_object = new JSONObject(
					evendtDataResponse.getJSONObject(Constants.provenanceEvent).toString());
			String bukValue = null;
			JSONArray attributesArray = provenance_object.getJSONArray(Constants.attributes);
			String componentName = "";
			int pickBuk = 0;
			int mappingCount = 0;
			if (provenance_object.has(Constants.componentNameConst)) {
				componentName = provenance_object.getString(Constants.componentNameConst);
			}
			eventType = provenance_object.getString(Constants.eventTypeConst);
			String[] value_split = null;
			if (eventType.equals(Constants.attributesModified)) {
				for (int iCount = 0; iCount < attributesArray.length(); iCount++) {
					JSONObject attributeObject = attributesArray.getJSONObject(iCount);
					if (componentName.contains(Constants.invokeBs)
							|| componentName.contains(Constants.smartUpdateAttribute)) {
						if (attributeObject.getString(Constants.name).equals(Constants.ioMapping)) {
							String hgf = componentName.contains(Constants.invokeBs)
									? attributeObject.getString(Constants.previousValue)
									: attributeObject.getString(Constants.value);
							if (!hgf.isEmpty()) {
								value_split = hgf.split("\\|");
							} else {
								value_split = new String[0];
							}
							if (value_split.length == 0) {
								isNonBEScenario = true;
								break;
							}
							for (int jCount = 0; jCount < value_split.length; jCount++) {
								String value = value_split[jCount];
								Integer iValue = Integer.valueOf(value);
								connectingLinks1.put(jCount, iValue);
								++mappingCount;
							}
							break;
						}
					}
				}
				if (isNonBEScenario == false) {
					for (int iCount = 0; iCount < attributesArray.length(); iCount++) {
						JSONObject attributeObject = attributesArray.getJSONObject(iCount);
						if (componentName.contains(Constants.invokeBs)
								|| componentName.contains(Constants.smartUpdateAttribute)) {
							if (attributeObject.getString(Constants.name).equals(Constants.inputBuk)) {
								bukValue = componentName.contains(Constants.invokeBs)
										? attributeObject.getString(Constants.previousValue)
										: attributeObject.getString(Constants.value);
								JSONArray jsonArrayTemp1 = new JSONArray(bukValue);
								for (int zvalue = 0; zvalue < jsonArrayTemp1.length(); zvalue++) {
									JSONObject jsonObject = jsonArrayTemp1.getJSONObject(zvalue);
									JSONArray jsonArrayTemp = new JSONArray();
									jsonArrayTemp.put(jsonObject);

									if (jsonArrayTemp.toString().equals(initialBuk.toString())) {
										pickBuk = connectingLinks1.get(zvalue);
									}
								}
								break;
							}
						}
					}
					for (int iCount = 0; iCount < attributesArray.length(); iCount++) {
						JSONObject attributeObject = attributesArray.getJSONObject(iCount);
						if (componentName.contains(Constants.invokeBs)
								|| componentName.contains(Constants.smartUpdateAttribute)) {
							if (attributeObject.getString(Constants.name).equals(Constants.outputBuk)) {

								bukValue = componentName.contains(Constants.invokeBs)
										? attributeObject.getString(Constants.previousValue)
										: attributeObject.getString(Constants.value);
								JSONArray jsonArrayTemp1 = new JSONArray(bukValue);
								for (int zvalue = 0; zvalue < jsonArrayTemp1.length(); zvalue++) {
									JSONObject jsonObject = jsonArrayTemp1.getJSONObject(zvalue);
									if (zvalue == pickBuk && mappingCount > 0) {
										JSONArray jsonArrayTemp11 = new JSONArray();
										jsonArrayTemp11.put(jsonObject);
										initialBuk = jsonArrayTemp11;
										break;
									}
								}
								break;
							}
						}
					}
				}
			}
		} catch (Exception exception) {
			logger.error("Exception Occurred:" + exception.getMessage(), exception);
		}
		return initialBuk;
	}

	public boolean decisionTracing(String childUuid, JSONObject eventData, JSONArray currentBuk,
			boolean isNonBEScenario) throws JSONException {
		boolean isApplicableForTrace = false;
		String tempString;
		String comma = ",";
		String[] comma_split = null;
		try {
			JSONObject evendtDataResponse = new JSONObject(eventData.toString());
			HashMap<Integer, String> uuidHashMap = new HashMap<>();
			String eventType = null;
			JSONObject provenance_object = new JSONObject(
					evendtDataResponse.getJSONObject(Constants.provenanceEvent).toString());
			int bukIndex;
			String bukIndexSting = "";
			JSONArray attributesArray = provenance_object.getJSONArray(Constants.attributes);
			String componentName = "";
			if (provenance_object.has(Constants.componentNameConst)) {
				componentName = provenance_object.getString(Constants.componentNameConst);
			}
			eventType = provenance_object.getString(Constants.eventTypeConst);
			if (eventType.equals("FORK") && componentName.contains("decision_matrix")) {
				for (int iCount = 0; iCount < attributesArray.length(); iCount++) {
					JSONObject attributeObject = attributesArray.getJSONObject(iCount);
					if (attributeObject.getString(Constants.name).equals(Constants.uuidMapping)) {
						tempString = attributeObject.getString(Constants.value);
						String[] value_split1 = tempString.split("\\|");
						for (int jCount = 0; jCount < value_split1.length; jCount++) {
							String value = value_split1[jCount];
							uuidHashMap.put(jCount, value);
						}
					}
					if (attributeObject.getString(Constants.name).equals(Constants.inputBuk)) {
						tempString = attributeObject.getString(Constants.value);
						JSONArray jsonArrayTemp1 = new JSONArray(tempString);
						for (int kCount = 0; kCount < jsonArrayTemp1.length(); kCount++) {
							JSONObject jsonObject = jsonArrayTemp1.getJSONObject(kCount);
							JSONArray jsonArrayTemp = new JSONArray();
							jsonArrayTemp.put(jsonObject);
							if (jsonArrayTemp.toString().equals(currentBuk.toString())) {
								bukIndex = kCount;
								bukIndexSting = Integer.toString(bukIndex);
							}
						}
					}
				}
				for (int iCount = 0; iCount < attributesArray.length(); iCount++) {
					JSONObject attributeObject = attributesArray.getJSONObject(iCount);
					if (attributeObject.getString(Constants.name).equals(Constants.ioMapping)) {
						tempString = attributeObject.getString(Constants.value);
						String[] value_split = tempString.split("\\|");
						for (int jCount = 0; jCount < value_split.length; jCount++) {
							String value = value_split[jCount];
							if (value.contains(comma)) {
								comma_split = value.split("\\,");
								for (int kCount = 0; kCount < comma_split.length; kCount++) {
									String value1 = comma_split[kCount];

									if (value1.equals(bukIndexSting)) {
										String uuidValue = uuidHashMap.get(jCount);
										if (uuidValue.equals(childUuid)) {
											isApplicableForTrace = true;
											break;
										}
									}
								}
							} else {
								if (value.contains(bukIndexSting)) {
									String uuidValue = uuidHashMap.get(jCount);
									if (uuidValue.equals(childUuid)) {
										isApplicableForTrace = true;
										break;
									}
								}
							}
						}
						if (value_split.length == 0) {
							isApplicableForTrace = true;
							isNonBEScenario = true;
							break;
						}
					}
				}
			}

		} catch (Exception exception) {
			logger.error("Exception Occurred:" + exception.getMessage(), exception);
		}
		return isApplicableForTrace;
	}

	public JSONArray findEnd(JSONObject eventData, JSONArray currentBuk, JSONArray finalBukArray,
			Queue<String> componentQueue, JSONArray attributesArrayfinal, JSONArray connectionArray,
			JSONArray operatorArray, JSONArray bukJSONArrayCopy, boolean isNonBEScenario, boolean failedAtStart,
			JSONArray componentArray, HashMap<String, JSONArray> operatortoSecondAPIMapping,
			HashMap<String, String> operatorNameMapping, HashMap<String, JSONArray> operatorToBukMapping,
			JSONArray componentArrayName, HashMap<String, String> splitUUIDMapping) throws JSONException {
		JSONArray jsonArray = currentBuk;
		String component = null;
		String concatOperator;
		try {
			String path = "";
			JSONObject evendtDataResponse = new JSONObject(eventData.toString());
			String eventType = null;
			String componentName = "";
			String componentPrevious = "";
			String componentNamePrevious = "";
			ArrayList<String> operatorsList = new ArrayList<>();
			operatorsList.add("");
			JSONObject provenance_object = new JSONObject(
					evendtDataResponse.getJSONObject(Constants.provenanceEvent).toString());
			eventType = provenance_object.getString(Constants.eventTypeConst);
			if (provenance_object.has(Constants.componentNameConst)) {
				componentName = provenance_object.getString(Constants.componentNameConst);
			}
			String flowFileUuid = provenance_object.getString("flowFileUuid");
			JSONArray attributesArray = provenance_object.getJSONArray(Constants.attributes);
			for (int iCount = 0; iCount < attributesArray.length(); iCount++) {
				JSONObject attributeObject = attributesArray.getJSONObject(iCount);
				if (attributeObject.getString(Constants.name).equals("DecisionMatrixProcessor.Route")) {
					path = attributeObject.getString(Constants.value);
				}
			}
			String[] value_split = null;
			if (componentName.equals("JoinProcessor")) {
				component = "JOIN";
			} else {
				value_split = componentName.split("\\-");
				component = value_split[0];
			}
			if (componentQueue.contains(component) == false) {
				if (value_split.length > 1) {
					componentQueue.add(component);
					componentArray.put(component);
					componentArrayName.put(componentName);
				}
				if (component.contains(componentArray.get(0).toString())) {
					operatorNameMapping.put(component, componentName);

				} else {
					if (provenance_object.has(Constants.componentNameConst) && value_split.length > 1) {
						componentPrevious = componentArray.getString(componentArray.length() - 2);
						componentNamePrevious = componentArrayName.getString(componentArrayName.length() - 2);
						if (splitUUIDMapping.containsKey(flowFileUuid)) {
							componentNamePrevious = splitUUIDMapping.get(flowFileUuid);
							value_split = componentNamePrevious.split("\\-");
							componentPrevious = value_split[0];
						}
						// Fix for XPB-128 starts here
						// Check the type for last operator
						String operatorType = componentArrayName.getString(componentArray.length() - 1).split("\\-")[1];
						if (operatorType.indexOf("output") > -1) {
							// Since last operator is some output channel, find
							// the immediate previous end operator
							for (int index = (componentArrayName.length() - 2); index > -1; index--) {
								operatorType = componentArrayName.getString(index).split("\\-")[1];
								if (operatorType.indexOf("end") > -1) {
									componentPrevious = componentArrayName.getString(index).split("\\-")[0];
									break;
								}
							}
						}
						// Fix for XPB-128 ends here

						JSONObject tempObject = new JSONObject();
						JSONObject tempObject2 = new JSONObject();
						JSONObject tempObjectConnectionArray = new JSONObject();
						tempObject.put("transactionID", flowFileUuid);
						if (isNonBEScenario == true) {
							JSONArray nullArray = new JSONArray();
							tempObject.put("bukList", nullArray);
							tempObjectConnectionArray.put("isNonBE", true);
						} else {
							tempObject.put("bukList", currentBuk);
							tempObjectConnectionArray.put("isNonBE", false);
						}
						attributesArrayfinal = new JSONArray();
						attributesArrayfinal.put(tempObject);
						tempObjectConnectionArray.put("from", componentPrevious);
						tempObjectConnectionArray.put("to", component);
						if (componentNamePrevious.contains("decision_matrix")) {
							tempObjectConnectionArray.put("path", path);
						}
						tempObject2.put("key", component);
						operatorArray.put(tempObject2);
						connectionArray.put(tempObjectConnectionArray);
						if (componentNamePrevious.contains("decision_matrix")) {
							concatOperator = componentPrevious + "-->" + component + "-->" + path;
						} else {
							concatOperator = componentPrevious + "-->" + component;
						}
						operatorNameMapping.put(component, componentName);
						operatortoSecondAPIMapping.put(concatOperator, attributesArrayfinal);
						operatorToBukMapping.put(concatOperator, bukJSONArrayCopy);
					}
				}
			} else {
				if (componentNamePrevious.contains("decision_matrix")) {
					int connectionArrayLength = connectionArray.length();
					for (int jCount = 0; jCount < connectionArrayLength; jCount++) {
						JSONObject jc = connectionArray.getJSONObject(jCount);
						String from = jc.getString("from");
						String to = jc.getString("to");
						componentPrevious = componentArray.getString(componentArray.length() - 1);
						if (from.equalsIgnoreCase(componentPrevious) && to.equalsIgnoreCase(component)) {
							attributesArray = provenance_object.getJSONArray(Constants.attributes);
							for (int iCount = 0; iCount < attributesArray.length(); iCount++) {
								JSONObject attributeObject = attributesArray.getJSONObject(iCount);
								if (attributeObject.getString(Constants.name).equals("DecisionMatrixProcessor.Route")) {
									path = attributeObject.getString(Constants.value);
								}
							}
							JSONObject tempObjectConnectionArray = new JSONObject();
							tempObjectConnectionArray.put("from", componentPrevious);
							tempObjectConnectionArray.put("to", component);
							tempObjectConnectionArray.put("path", path);
							connectionArray.put(tempObjectConnectionArray);
							JSONObject tempObject = new JSONObject();
							tempObject.put("transactionID", flowFileUuid);
							tempObject.put("bukList", currentBuk);
							attributesArrayfinal = new JSONArray();
							attributesArrayfinal.put(tempObject);
							concatOperator = componentPrevious + "-->" + component + "-->" + path;
							operatortoSecondAPIMapping.put(concatOperator, attributesArrayfinal);
							operatorToBukMapping.put(concatOperator, bukJSONArrayCopy);
						}
					}
				}
			}
			attributesArray = provenance_object.getJSONArray(Constants.attributes);
			logger.info("componentName=" + componentName + "  =>>> eventType =" + eventType);
			if (componentName.contains(Constants.customEnd)
					&& Constants.attributesModified.equalsIgnoreCase(eventType)) {
				logger.info("End processor found");
				finalBukArray = outputBUK(finalBukArray, jsonArray, attributesArray);
			}
			if (componentName.contains("FailureProcessor")) {
				if (componentName.contains("start")) {
					failedAtStart = true;
				} else {
					logger.info("Failure processor found");
					finalBukArray = outputBUK(finalBukArray, jsonArray, attributesArray);
				}
			}
		} catch (Exception exception) {
			logger.error("Exception Occurred:" + exception.getMessage(), exception);
		}
		return finalBukArray;
	}

	public JSONArray outputBUK(JSONArray finalBukArray, JSONArray jsonArray, JSONArray attributesArray)
			throws JSONException {

		int jsonArrayLength = jsonArray.length();
		String beName = null;
		JSONObject jsonConversion = new JSONObject();
		for (int iCount = 0; iCount < attributesArray.length(); iCount++) {
			JSONObject attributeObject = attributesArray.getJSONObject(iCount);
			if (attributeObject.getString(Constants.name).equals(Constants.outputBEName)) {
				beName = attributeObject.getString(Constants.value);
			}
		}
		for (int iCount = 0; iCount < jsonArrayLength; iCount++) {
			JSONObject jsonObject = jsonArray.getJSONObject(iCount);
			JSONArray jsonArrayTemp = jsonObject.getJSONArray(Constants.buk);
			JSONArray bukJSONArrayTemp = new JSONArray();
			for (int jCount = 0; jCount < jsonArrayTemp.length(); jCount++) {
				jsonConversion = new JSONObject();
				String attributeValue = jsonArrayTemp.getJSONObject(jCount).getString(Constants.attributeValuec)
						.toString();
				String attributeName = jsonArrayTemp.getJSONObject(jCount).getString(Constants.attributeNamec)
						.toString();
				jsonConversion.putOpt(Constants.name, attributeName);
				jsonConversion.putOpt(Constants.value, attributeValue);
				bukJSONArrayTemp.put(jsonConversion);
			}
			jsonObject = new JSONObject();
			jsonObject.put(Constants.beName, beName);
			jsonObject.put(Constants.buk, bukJSONArrayTemp);
			finalBukArray.put(jsonObject);
		}
		return finalBukArray;
	}
}
