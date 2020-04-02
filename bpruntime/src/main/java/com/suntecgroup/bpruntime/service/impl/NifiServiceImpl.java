package com.suntecgroup.bpruntime.service.impl;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.BPState;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplayRequestEntity;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.Status;
import com.suntecgroup.bpruntime.bean.nifi.ProcessGroupRevision;
import com.suntecgroup.bpruntime.bean.nifi.Property;
import com.suntecgroup.bpruntime.bean.nifi.UpdateNiFiRegistryRequest;
import com.suntecgroup.bpruntime.bean.nifi.Variable;
import com.suntecgroup.bpruntime.bean.nifi.VariableRegistry;
import com.suntecgroup.bpruntime.constant.Constant;
import com.suntecgroup.bpruntime.service.NifiService;
import com.suntecgroup.bpruntime.service.SessionManagerService;

@Service
public class NifiServiceImpl implements NifiService {

	@Autowired
	private Environment env;

	@Autowired
	SessionManagerService sessionManagerService;

	@Autowired
	@Qualifier("nifiBean")
	private RestTemplate restTemplate;

	private String rootGroupId = "root";

	private static final Logger LOGGER = LoggerFactory.getLogger(NifiServiceImpl.class);

	/**
	 * updateBpState
	 * 
	 * @param String
	 */
	@Override
	public String updateBpState(BPState bpState) {
		String url = null;
		JsonObject request = new JsonObject();
		Random random = new Random();
		int originX = 850;
		String requestPayload = null;
		Gson gson = new Gson();
		HttpEntity<String> response = null;
		HttpEntity<String> requestEntity = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		if (bpState.getTemplateId().equals("")) {
			request.addProperty("id", bpState.getBpGroupId());
			request.addProperty("state", bpState.getBpState());
			url = env.getProperty("nifi.instance.url") + env.getProperty("update.process.groups")
					+ bpState.getBpGroupId();

			requestPayload = gson.toJson(request);
			requestEntity = new HttpEntity<String>(requestPayload, headers);
			response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
		} else {
			originX = random.nextInt(850);
			request.addProperty("templateId", bpState.getTemplateId());
			request.addProperty("originX", originX);
			request.addProperty("originY", "305.00");
			url = env.getProperty("nifi.instance.url") + env.getProperty("process.groups") + rootGroupId
					+ "/template-instance";

			requestPayload = gson.toJson(request);
			requestEntity = new HttpEntity<String>(requestPayload, headers);
			response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		}

		return response.getBody();
	}

	/**
	 * getSessionList
	 * 
	 * @param String
	 */
	@Override
	public String instantiateTemplate(String templateId) {
		int originX = 850;
		String requestPayload = null;
		String processGroupId = "";
		Random random = new Random();
		Gson gson = new Gson();
		JsonObject request = new JsonObject();

		HttpEntity<String> response = null;
		HttpEntity<String> requestEntity = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		originX = random.nextInt(850);
		request.addProperty("templateId", templateId);
		request.addProperty("originX", originX);
		request.addProperty("originY", "305.00");
		String url = env.getProperty("template.instantiate");

		requestPayload = gson.toJson(request);
		requestEntity = new HttpEntity<String>(requestPayload, headers);

		response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		JSONObject nifiResponse;
		try {
			nifiResponse = new JSONObject(response.getBody());
			JSONObject flow = nifiResponse.getJSONObject("flow");
			JSONArray processGroups = flow.getJSONArray("processGroups");
			JSONObject processGroup = processGroups.getJSONObject(0);
			processGroupId = processGroup.getString("id");
		} catch (JSONException e) {
			LOGGER.error("Unable to Parse Json Exception in instantiateTemplate()" + e);
		} catch (Exception e) {
			LOGGER.error("Exception in instantiateTemplate()" + e);
		}

		return processGroupId;
	}

	/**
	 * getSessionList
	 * 
	 * @param String
	 */
	@Override
	public boolean isInstantiated(String processGroupId) {
		boolean isProcessGroupAvailable = false;
		ResponseEntity<String> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		String url = env.getProperty("nifi.instance.url") + env.getProperty("process.groups") + processGroupId;
		HttpStatus statusCode;
		try {
			response = restTemplate.getForEntity(url, String.class);
			statusCode = response.getStatusCode();
			if (statusCode == HttpStatus.OK) {
				isProcessGroupAvailable = true;
			}
		} catch (RestClientException rce) {
			LOGGER.error("Exception occur", rce);
			isProcessGroupAvailable = false;
		}
		return isProcessGroupAvailable;
	}

	@Override
	public void updateVariableRegistry(SessionDetails session, Boolean isSessionStarted) throws Exception {
		LOGGER.info(":::Start of updateVariableRegistry method:::");
		String url = null;
		String version = null;
		HttpEntity<UpdateNiFiRegistryRequest> requestEntity = null;
		HttpEntity<String> response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		ResponseEntity<String> getVariableRegistryResponse = restTemplate
				.getForEntity(env.getProperty("nifi.instance.url") + env.getProperty("process.groups")
						+ session.getSessionProcessGroupId() + "/variable-registry", String.class);
		JSONObject getVariableRegistryResponseJson;
		try {
			getVariableRegistryResponseJson = new JSONObject(getVariableRegistryResponse.getBody());
			JSONObject processGroupRevision = getVariableRegistryResponseJson.getJSONObject("processGroupRevision");
			version = processGroupRevision.getString("version");
			UpdateNiFiRegistryRequest updateNiFiRegistryRequest = new UpdateNiFiRegistryRequest();
			ProcessGroupRevision nifiReqProcessGroupRevision = new ProcessGroupRevision();
			nifiReqProcessGroupRevision.setVersion(Integer.valueOf(version));
			List<Variable> nifiReqVariableList = new ArrayList<Variable>();
			VariableRegistry nifiReqVariableRegistry = new VariableRegistry();

			Variable nifiVariableSessionId = new Variable();
			Variable nifiVariableRunNumber = new Variable();

			Property sessionIdProperty = new Property();
			Property runNumberProperty = new Property();
			sessionIdProperty.setName("sessionId");
			runNumberProperty.setName("runNumber");
			if (isSessionStarted) {
				sessionIdProperty.setValue(session.getSessionId());
				runNumberProperty.setValue(session.getRunNumber());
			} else {
				sessionIdProperty.setValue("0");
				runNumberProperty.setValue("0");
			}

			LOGGER.info("Updating the following values");
			LOGGER.info("Session ID: " + sessionIdProperty.getValue());
			LOGGER.info("Run Number: " + runNumberProperty.getValue());

			nifiVariableSessionId.setVariable(sessionIdProperty);
			nifiVariableRunNumber.setVariable(runNumberProperty);

			nifiReqVariableList.add(nifiVariableSessionId);
			nifiReqVariableList.add(nifiVariableRunNumber);

			nifiReqVariableRegistry.setProcessGroupId(session.getSessionProcessGroupId());
			nifiReqVariableRegistry.setVariables(nifiReqVariableList);
			updateNiFiRegistryRequest.setVariableRegistry(nifiReqVariableRegistry);

			updateNiFiRegistryRequest.setProcessGroupRevision(nifiReqProcessGroupRevision);

			url = env.getProperty("nifi.instance.url") + env.getProperty("process.groups")
					+ session.getSessionProcessGroupId() + "/variable-registry/update-requests";
			requestEntity = new HttpEntity<UpdateNiFiRegistryRequest>(updateNiFiRegistryRequest, headers);
			response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			getVariableRegistryResponseJson = new JSONObject(response.getBody());
			JSONObject resRequest = getVariableRegistryResponseJson.getJSONObject("request");
			// check whether the request is fulfilled
			LOGGER.info("Update request tracking URI: " + resRequest.getString("uri"));
			boolean isUpdated = checkVariableUpdateReqStatus(resRequest.getString("uri"));
			if (!isUpdated) {
				throw new Exception("Failed to update variable registry for process group ID: "
						+ session.getSessionProcessGroupId());
			}
		} catch (JSONException e) {
			LOGGER.error("Unable to Parse Json Exception in updateVariableRegistry()", e);
			throw new Exception(
					"Failed to update variable registry for process group ID: " + session.getSessionProcessGroupId());
		} catch (Exception e) {
			LOGGER.error("Exception in updateVariableRegistry():", e);
			throw new Exception(
					"Failed to update variable registry for process group ID: " + session.getSessionProcessGroupId());
		} finally {
			LOGGER.info(":::End of updateVariableRegistry method:::");
		}

	}

	private boolean checkVariableUpdateReqStatus(String uri) throws Exception {
		ResponseEntity<String> getVariableRegistryResponse;
		JSONObject getVariableRegistryResponseJson;
		JSONObject resRequest;
		JSONArray updateSteps;
		JSONObject updateStep;
		boolean isAllCleared = false;
		int clearedCount = 0;
		int totalSteps = 0;

		for (int i = 0; i < 10; i++) {
			Thread.sleep(1000L);
			getVariableRegistryResponse = restTemplate.getForEntity(uri, String.class);
			getVariableRegistryResponseJson = new JSONObject(getVariableRegistryResponse.getBody());
			resRequest = getVariableRegistryResponseJson.getJSONObject("request");
			updateSteps = resRequest.getJSONArray("updateSteps");
			totalSteps = updateSteps.length();
			clearedCount = 0;
			for (int j = 0; j < totalSteps; j++) {
				updateStep = (JSONObject) updateSteps.get(j);
				if (updateStep.getBoolean("complete")) {
					clearedCount++;
				}
			}
			if (clearedCount == totalSteps) {
				isAllCleared = true;
				break;
			}
		}
		return isAllCleared;
	}

	@Override
	public String getSessionDetails(String processGroupId) {
		ResponseEntity<String> processGroups = restTemplate.getForEntity(
				env.getProperty("nifi.instance.url") + env.getProperty("process.groups") + processGroupId,
				String.class);
		return processGroups.getBody();
	}

	@Override
	public String getConnectionDetails(String processGroupId) {
		ResponseEntity<String> connectionGroups = restTemplate.getForEntity(env.getProperty("nifi.instance.url")
				+ env.getProperty("process.groups") + processGroupId + env.getProperty("connection.groups"),
				String.class);
		return connectionGroups.getBody();
	}

	public boolean stopProcessor(List<String> firstProcessorslist) {
		boolean status = true;
		String runningStatus = Constant.STOPPED;
		String version = "";
		String processorId = "";
		ResponseEntity<String> response = null;
		HttpEntity<String> requestEntity = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		String requestPayload = null;
		for (int i = 0; i < firstProcessorslist.size(); i++) {

			try {
				processorId = firstProcessorslist.get(i);
				// String url=http://sbstjvmlx808:8888/nifi-api/processors/
				ResponseEntity<String> processorDetails = restTemplate.getForEntity(
						env.getProperty("nifi.instance.url") + env.getProperty("processor.group") + processorId,
						String.class);
				JSONObject obj = new JSONObject(processorDetails.getBody());
				JSONObject revisionObj = obj.getJSONObject("revision");
				version = revisionObj.getString("version");

				String url = env.getProperty("nifi.instance.url") + env.getProperty("processor.group") + processorId;
				JSONObject requestObj = new JSONObject();
				JSONObject componentObj = new JSONObject();
				componentObj.put("state", runningStatus);
				componentObj.put("id", processorId);
				requestObj.put("revision", new JSONObject().put("version", version));
				requestObj.put("component", componentObj);
				requestObj.put("status", new JSONObject().put("runStatus", runningStatus));
				requestObj.put("id", processorId);

				requestPayload = requestObj.toString();
				requestEntity = new HttpEntity<String>(requestPayload, headers);

				response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

			} catch (Exception e) {
				status = false;
				LOGGER.error("Exception in stop Processor()-" + e.getMessage());

			}

		}
		return status;
	}

	@Override
	public void startStopFlow(String processGroupId, String status) {
		String requestPayload = null;
		String url = null;
		Gson gson = new Gson();
		JsonObject request = new JsonObject();
		HttpEntity<String> response = null;
		HttpEntity<String> requestEntity = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		request = new JsonObject();
		request.addProperty("id", processGroupId);
		request.addProperty("state", status);
		url = env.getProperty("nifi.instance.url") + env.getProperty("update.process.groups") + processGroupId;

		requestPayload = gson.toJson(request);
		requestEntity = new HttpEntity<String>(requestPayload, headers);
		response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
	}

	/**
	 * replayEvent
	 * 
	 * @param ReplayRequestEntity
	 * @return ResponseEntity
	 * @throws HttpStatusCodeException
	 */
	public ResponseEntity<String> replayEvent(ReplayRequestEntity replayRequestEntity) throws HttpStatusCodeException {
		String url = env.getProperty("nifi.instance.url") + env.getProperty("events.replay");
		JsonObject request = new JsonObject();
		String requestPayload = null;
		ResponseEntity<String> response = null;
		HttpEntity<String> requestEntity = null;
		HttpHeaders headers = new HttpHeaders();
		Gson gson = new Gson();
		headers.set("Content-Type", "application/json");
		request.addProperty("eventId", replayRequestEntity.getEventId());
		if (!StringUtils.isBlank(replayRequestEntity.getClusterNodeId())) {
			request.addProperty("clusterNodeId", replayRequestEntity.getClusterNodeId());
		}
		requestPayload = gson.toJson(request);
		requestEntity = new HttpEntity<String>(requestPayload, headers);
		response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		return response;
	}

	/**
	 * generateProvenanceDataId @param String @param HttpEntity @return
	 * HttpEntity @throws
	 */
	public HttpEntity<String> generateProvenanceDataId(HttpEntity<String> requestEntity) {
		HttpEntity<String> nifiProvenanceResponse = null;
		String url = env.getProperty("nifi.instance.url") + env.getProperty("provenance");
		try {
			nifiProvenanceResponse = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		} catch (Exception e) {
			LOGGER.error("Exception in generateProvenanceDataId()-" + e.getMessage());

		}
		return nifiProvenanceResponse;
	}

	/**
	 * deleteProvenanceDataId @param String @return @throws
	 */
	public void deleteProvenanceDataId(String url) {
		restTemplate.delete(url);

	}

	/**
	 * getProvenanceData @param String @return ResponseEntity @throws
	 */
	public ResponseEntity<String> getProvenanceData(String url) {
		ResponseEntity<String> provenanceEventsData = null;
		JSONObject eventDetailsJson = null;
		JSONObject provenanceEventDetailsJson = null;
		boolean finished = false;

		try {
			do {
				Thread.sleep(500);
				provenanceEventsData = restTemplate.getForEntity(url, String.class);

				if (provenanceEventsData.getBody() != null) {
					eventDetailsJson = new JSONObject(provenanceEventsData.getBody());
					if (null != eventDetailsJson && eventDetailsJson.has("provenance")) {
						provenanceEventDetailsJson = eventDetailsJson.getJSONObject("provenance");
						finished = Boolean.parseBoolean(provenanceEventDetailsJson.getString("finished"));
					}
				}
			} while (!finished);

		} catch (Exception e) {
			LOGGER.error("Exception in getProvenanceData()-" + e.getMessage());
		}
		return provenanceEventsData;
	}

	/**
	 * getData @param String @return ResponseEntity @throws
	 */
	public ResponseEntity<String> getData(String url) {
		ResponseEntity<String> nifiData = null;
		try {
			nifiData = restTemplate.getForEntity(url, String.class);
		} catch (Exception e) {
			LOGGER.error("Exception in getData()-" + e.getMessage());
		}
		return nifiData;
	}

	@Override
	public Object deployTemplate(String xmlContent) {
		HttpEntity<MultiValueMap<String, Object>> requestEntity = null;
		HttpHeaders headers = new HttpHeaders();

		try {
			String url = env.getProperty("template.upload");
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
			parts.add("template", xmlContent);
			requestEntity = new HttpEntity<MultiValueMap<String, Object>>(parts, headers);
			ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			if (HttpStatus.CREATED == exchange.getStatusCode()) {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(exchange.getBody().getBytes("UTF-8"));
				Document document = builder.parse(inputStream);
				NodeList nodeList = document.getElementsByTagName("id");
				String templateId = nodeList.item(0).getTextContent();

				JSONObject responseJson = new JSONObject();
				responseJson.put("templateId", templateId);

				return new ResponseEntity<ApiResponse<String>>(new ApiResponse<String>(Status.SUCCESS.getStatusCode(),
						Status.SUCCESS, null, responseJson.toString()), HttpStatus.OK);
			} else {
				return new ResponseEntity<ApiResponse<Object>>(new ApiResponse<Object>(Status.FAILURE.getStatusCode(),
						Status.FAILURE, "Something went wrong!", null), HttpStatus.CONFLICT);
			}

		} catch (HttpClientErrorException httpClientErrorException) {
			LOGGER.error("Exceptino occurred::", httpClientErrorException);
			return new ResponseEntity<ApiResponse<Object>>(new ApiResponse<Object>(Status.FAILURE.getStatusCode(),
					Status.FAILURE, "Template already available!", null), HttpStatus.CONFLICT);
		} catch (Exception exception) {
			LOGGER.error("Exceptino occurred::", exception);
			return new ResponseEntity<ApiResponse<Object>>(new ApiResponse<Object>(Status.FAILURE.getStatusCode(),
					Status.FAILURE, "Something went wrong!", null), HttpStatus.CONFLICT);
		}
	}

	@Override
	public Object getVariableRegistry(String processGroupId) {
		try {
			String url = env.getProperty("update.variable-registry");
			ResponseEntity<String> variableRegistryResponse = restTemplate.getForEntity(url, String.class,
					processGroupId);
			if (HttpStatus.OK == variableRegistryResponse.getStatusCode()) {
				return variableRegistryResponse;
			}
		} catch (Exception exception) {
			LOGGER.error("Exceptino occurred :: ", exception);
		}
		return null;
	}

	@Override
	public Object updateVariableRegistry(String processGroupId, HashMap<String, String> propertiesMap) {

		try {
			ResponseEntity<String> variableRegistryResponse = (ResponseEntity<String>) getVariableRegistry(
					processGroupId);
			if (null == variableRegistryResponse) {
				return new ResponseEntity<ApiResponse<Object>>(new ApiResponse<Object>(Status.FAILURE.getStatusCode(),
						Status.FAILURE, "Couldn't get process group revision!", null), HttpStatus.CONFLICT);
			}
			JSONObject variableRegistryResponseJson = new JSONObject(variableRegistryResponse.getBody());
			JSONObject processGroupRevision = variableRegistryResponseJson.getJSONObject("processGroupRevision");
			if (!StringUtils.isBlank(processGroupRevision.getString("version"))) {
				int version = Integer.valueOf(processGroupRevision.getString("version"));
				// Get the actual process group id for root process group
				JSONObject variableRegistryJSON = variableRegistryResponseJson.getJSONObject("variableRegistry");
				processGroupId = variableRegistryJSON.getString("processGroupId");

				UpdateNiFiRegistryRequest updateNiFiRegistryRequest = new UpdateNiFiRegistryRequest();

				VariableRegistry nifiReqVariableRegistry = new VariableRegistry();
				nifiReqVariableRegistry.setProcessGroupId(processGroupId);
				
				LOGGER.info("Prop size before: " + propertiesMap.size());
				Gson gson = new Gson();
				VariableRegistry nifiResVariableRegistry = null;
				nifiResVariableRegistry = gson.fromJson(variableRegistryJSON.toString(), VariableRegistry.class);
				List<Variable> nifiResVariableList = nifiResVariableRegistry.getVariables();
				LOGGER.info("nifiResVariableList.size()" + nifiResVariableList.size());
				if (nifiResVariableList.size() > 0) {
					for (int i = 0; i < nifiResVariableList.size(); i++) {
						Variable var = nifiResVariableList.get(i);
						if (propertiesMap.containsKey(var.getVariable().getName())) {
							if ((propertiesMap.get(var.getVariable().getName()) + "").equals(var.getVariable().getValue())) {
								propertiesMap.remove(var.getVariable().getName());
							}
						}
					}
				}
				LOGGER.info("Prop size after: " + propertiesMap.size());
				
				List<Variable> nifiReqVariableList = new ArrayList<Variable>();
				Property nifiReqProperty = new Property();
				Variable nifiReqVariable = new Variable();
				for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
					nifiReqProperty = new Property();
					nifiReqProperty.setName(URLEncoder.encode(entry.getKey(), "UTF-8"));
					nifiReqProperty.setValue(entry.getValue());
					nifiReqVariable = new Variable();
					nifiReqVariable.setVariable(nifiReqProperty);
					nifiReqVariableList.add(nifiReqVariable);
					LOGGER.info("Added : " + entry.getKey());
				}

				nifiReqVariableRegistry.setVariables(nifiReqVariableList);
				updateNiFiRegistryRequest.setVariableRegistry(nifiReqVariableRegistry);

				String url = env.getProperty("update.variable-registry") + "/update-requests";
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				ResponseEntity<String> response = null;
				ProcessGroupRevision nifiReqProcessGroupRevision = new ProcessGroupRevision();
				nifiReqProcessGroupRevision.setVersion(version);
				updateNiFiRegistryRequest.setProcessGroupRevision(nifiReqProcessGroupRevision);
				HttpEntity<UpdateNiFiRegistryRequest> requestEntity = new HttpEntity<UpdateNiFiRegistryRequest>(
						updateNiFiRegistryRequest, headers);
				String updateId = "";
				int retry = 2;
				do {
					response = callUpdateVariableRegistryUsingPost(url, requestEntity, processGroupId);
					if (response != null) {
						updateId = ((JSONObject) new JSONObject(response.getBody()).get("request"))
								.getString("requestId");
						break;
					}
					LOGGER.error("Retrying NiFi variable registry update request :: attempt " + (3 - retry));
					Thread.sleep(1000);
					retry--;
				} while (response == null && retry > 0);
				if (!StringUtils.isBlank(updateId)) {
					int timeout = 10;
					do {
						url = env.getProperty("nifi.instance.url") + "/nifi-api/process-groups/" + processGroupId
								+ "/variable-registry/update-requests/" + updateId;
						response = checkUpdateVariableRegistryResponse(url, requestEntity, processGroupId);
						if (response != null) {
							JSONObject res = new JSONObject(response.getBody());
							JSONObject request = (JSONObject) res.get("request");
							if (request.getBoolean("complete")) {
								url = env.getProperty("nifi.instance.url") + "/nifi-api/process-groups/"
										+ processGroupId + "/variable-registry/update-requests/" + updateId;
								deleteUpdateVariableRegistryRequest(url);
								break;
							}
						}
						timeout--;
						LOGGER.info("Awaiting for NiFi variable registry update response status.. Elapsed sec: "
								+ (10 - timeout));
						Thread.sleep(1000);
					} while (response == null && timeout > 0);
				}
				if (response != null && HttpStatus.OK == response.getStatusCode()) {
					return new ResponseEntity<ApiResponse<String>>(
							new ApiResponse<String>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
									response.getBody().toString()),
							HttpStatus.OK);
				} else {
					return new ResponseEntity<ApiResponse<Object>>(
							new ApiResponse<Object>(Status.FAILURE.getStatusCode(), Status.FAILURE,
									"Something went wrong!", null),
							HttpStatus.CONFLICT);
				}
			} else {
				return new ResponseEntity<ApiResponse<Object>>(
						new ApiResponse<Object>(Status.FAILURE.getStatusCode(), Status.FAILURE,
								"processGroupRevision Version is empty, couldn't proceed further!", null),
						HttpStatus.CONFLICT);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurrd::", exception);
			return new ResponseEntity<ApiResponse<Object>>(new ApiResponse<Object>(Status.FAILURE.getStatusCode(),
					Status.FAILURE, "Something went wrong!", null), HttpStatus.CONFLICT);
		}
	}
	
	private ResponseEntity<String> callUpdateVariableRegistryUsingPost(String url,
			HttpEntity<UpdateNiFiRegistryRequest> requestEntity, String processGroupId) {
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class, processGroupId);
		} catch (Exception e) {
			LOGGER.error("::: Exception occurred in callUpdateVariableRegistryUsingPost :::");
		}
		return response;
	}
	
	private ResponseEntity<String> checkUpdateVariableRegistryResponse(String url,
			HttpEntity<UpdateNiFiRegistryRequest> requestEntity, String processGroupId) {
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.getForEntity(url, String.class, processGroupId);
		} catch (Exception e) {
			LOGGER.error("::: Exception occurred in checkUpdateVariableRegistryResponse :::");
		}
		return response;
	}
	
	private ResponseEntity<String> deleteUpdateVariableRegistryRequest(String url) {
		ResponseEntity<String> response = null;
		try {
			restTemplate.delete(url);
		} catch (Exception e) {
			LOGGER.error("::: Exception occurred in deleteUpdateVariableRegistryRequest :::");
		}
		return response;
	}
	
}
