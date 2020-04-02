package com.suntecgroup.traceablity.serviceImpl;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.suntecgroup.traceablity.Beans.Constants;
import com.suntecgroup.traceablity.service.NifiService;

@Service
public class TraceNifiServiceImpl implements NifiService {

	private static Logger logger = Logger.getLogger(TraceNifiServiceImpl.class.getName());

	@Autowired
	@Qualifier("nifiBean")
	private RestTemplate restTemplate;

	@Autowired
	Environment env;

	@Override
	public String createLineageReqeust(String flowFileUUID, String clusterNodeID) {
		try {
			JsonObject HTTPRequestData = new JsonObject();
			JsonObject lineageData = new JsonObject();
			JsonObject lineageRequestData = new JsonObject();
			String requestPayload = null;
			Gson gson = new Gson();
			HttpEntity<String> httpResponse = null;
			HttpEntity<String> requestEntity = null;
			HttpHeaders headers = new HttpHeaders();
			if (null != flowFileUUID && !StringUtils.isEmpty(flowFileUUID)) {
				headers.set(Constants.contentType, Constants.applicationJson);
				lineageRequestData.addProperty(Constants.eventId, Constants.eventStartType);
				lineageRequestData.addProperty(Constants.lineageRequestType, Constants.flowFile);
				lineageRequestData.addProperty(Constants.uuid, flowFileUUID);
				if(!"".equals(clusterNodeID)) {
					lineageRequestData.addProperty(Constants.clusterNodeId, clusterNodeID);
				}
				lineageData.add(Constants.request, lineageRequestData);
				HTTPRequestData.add(Constants.lineage, lineageData);
				requestPayload = gson.toJson(HTTPRequestData);
				logger.info("requestPayload ::" + requestPayload);
				requestEntity = new HttpEntity<String>(requestPayload, headers);
				String url = env.getProperty("nifi.instance.url") + env.getProperty("provenanceLineage");

				httpResponse = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

				return httpResponse.getBody();
			} else {
				logger.info("Invalid FlowFIle UUID");
				return null;
			}
		} catch (Exception exception) {
			logger.error("Exception occurred:" + exception.getMessage(), exception);
			return null;
		}
	}

	@Override
	public String getLineageData(String getLineageURL, String clusterNodeID) {
		ResponseEntity<String> lineageResponse = null;
		JSONObject lineageDetailsJson = null;
		JSONObject lineageData = null;
		boolean finished = false;
		
		if(!"".equals(clusterNodeID)) {
			getLineageURL = getLineageURL + "?clusterNodeId=" + clusterNodeID;
		}

		try {
			do {
				Thread.sleep(100);
				lineageResponse = restTemplate.getForEntity(getLineageURL, String.class);

				if (lineageResponse.getBody() != null) {
					lineageDetailsJson = new JSONObject(lineageResponse.getBody());
					if (null != lineageDetailsJson && lineageDetailsJson.has("lineage")) {
						lineageData = lineageDetailsJson.getJSONObject("lineage");
						finished = Boolean.parseBoolean(lineageData.getString("finished"));
					}
				}
			} while (!finished);
		} catch (Exception exception) {
			logger.error("Exception in getLineageData()-" + exception.getMessage());
			return null;
		}
		return lineageDetailsJson.toString();
	}

	@Override
	public void deteleLineageReqeust(String deleteLineageURL, String clusterNodeID) {
		if(!"".equals(clusterNodeID)) {
			deleteLineageURL = deleteLineageURL + "?clusterNodeId=" + clusterNodeID;
		}
		restTemplate.delete(deleteLineageURL);
	}

	@Override
	public JSONObject provenanceEventData(String Eventno, String clusterNodeID) {
		JSONObject myresponse1 = null;
		String url = env.getProperty("nifi.instance.url") + env.getProperty("provenanceEvents") + Eventno;
		if(!"".equals(clusterNodeID)) {
			url = url + "?clusterNodeId=" + clusterNodeID;
		}
		try {
			ResponseEntity<String> httpresponse1 = restTemplate.getForEntity(url, String.class);
			myresponse1 = new JSONObject(httpresponse1.getBody());
		} catch (JSONException exception) {
			logger.error("Exception occurred:" + exception.getMessage(), exception);
		}
		return myresponse1;
	}
}