package com.suntecgroup.bpruntime.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.suntecgroup.bpruntime.bean.adminconsole.FailedTransaction;
import com.suntecgroup.bpruntime.dao.TransactionDAO;

@Repository
public class TransactionDAOImpl implements TransactionDAO {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(TransactionDAOImpl.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	Environment environment;

	@Override
	public FailedTransaction saveFailedTransaction(FailedTransaction failedTransaction) {
		String url = environment.getProperty("xbmc.datastore.bs.FailedTransactionBS.save");
		JSONObject customizedRequest = new JSONObject();
		Gson gson = new GsonBuilder().create();
		
		try {
			String jsonString = gson.toJson(failedTransaction);
			JSONObject operatorStatsObj = new JSONObject(jsonString);
			JSONArray failedTransactionsList = new JSONArray();
			failedTransactionsList.put(operatorStatsObj);
			customizedRequest.put("failedTransaction", failedTransactionsList);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity,
					String.class);
			if (!responseEntity.getStatusCode().is2xxSuccessful()) {
				throw new Exception("failed to Save the Data");
			}

			return failedTransaction;

		} catch (Exception exception) {
			LOGGER.error("Save/Update may failed");
			LOGGER.error("Exception occurred::", exception);
		}
		return null;

	}

	@Override
	public List<FailedTransaction> getFailedTransactionDetails(String sessionId, String runNumber, String errorType,
			boolean isAddressed, String action) {

		final HttpHeaders headers = new HttpHeaders();
		List<FailedTransaction> resultList = new ArrayList<>();
		// Constructing request
		JSONObject requestObject = new JSONObject();
		JSONObject context = new JSONObject();
		JSONObject context_parameters = new JSONObject();
		String url = "";
		try {
			if (StringUtils.isBlank(action)) {
				context_parameters.put("a_sessionId", sessionId);
				context_parameters.put("a_runNo", runNumber);
				context_parameters.put("a_errort", errorType);
				context_parameters.put("a_add", isAddressed);
				url = environment.getProperty("xbmc.datastore.bs.FailedTransactionBS.sessionIdandrunNoand3more");
			} else {
				context_parameters.put("a_sessionId", sessionId);
				context_parameters.put("a_runNumber", runNumber);
				context_parameters.put("a_errorType", errorType);
				context_parameters.put("a_add", isAddressed);
				context_parameters.put("a_act", action);
				url = environment.getProperty("xbmc.datastore.bs.FailedTransactionBS.sessionIdandrunNoandError3more");
			}
			
			context.put("context-parameters", context_parameters);
			requestObject.put("context", context);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestObject.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
			String deployedTemplate = (String) responseEntity.getBody();
			JsonObject jsonObj = new Gson().fromJson(deployedTemplate, JsonObject.class);
		
			if (jsonObj.has("failedTransaction")) {
				JsonArray failedTransactionList = jsonObj.getAsJsonArray("failedTransaction");
				for (int i = 0; i < failedTransactionList.size(); i++) {
					FailedTransaction res = new Gson().fromJson(failedTransactionList.get(i).toString(),FailedTransaction.class);
					double eventCount = Double.parseDouble(res.getEventsCount());
					String eventCountStr = String.valueOf(((int)eventCount));
					res.setEventsCount(eventCountStr);
					List<String> errorMessages = new ArrayList<>();
					for(String message: res.getNormalisedErrorMessage().split("   ")){
						errorMessages.add(message);
					}
					res.setErrorMessage(errorMessages);

					resultList.add(res);
				}
			}
		} catch (JSONException e) {
			LOGGER.error("Failed to fetch any failedTransaction for the given sessionId and RunNumber: Unexpected Error");
		}
		return resultList;
	}

	@Override
	public List<FailedTransaction> getFailedTransaction(String sessionId, String runNumber, String trasactionId) {
		
		final HttpHeaders headers = new HttpHeaders();
		
		List<FailedTransaction> resultList = new ArrayList<>();
		// Constructing request
		JSONObject requestObject = new JSONObject();
		JSONObject context = new JSONObject();
		JSONObject context_parameters = new JSONObject();
		String url = environment.getProperty("xbmc.datastore.bs.FailedTransactionBS.sessionIdandrunNumberandtransa");
		try {
			context_parameters.put("a_runNum", runNumber);
			context_parameters.put("a_session", sessionId);
			context_parameters.put("a_transact", trasactionId);

			context.put("context-parameters", context_parameters);
			requestObject.put("context", context);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestObject.toString(), headers);
			ResponseEntity<?> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
			String deployedTemplate = (String) responseEntity.getBody();
			JsonObject jsonObj = new Gson().fromJson(deployedTemplate, JsonObject.class);

			if (jsonObj.has("failedTransaction")) {
				JsonArray failedTransactionList = jsonObj.getAsJsonArray("failedTransaction");
				for (int i = 0; i < failedTransactionList.size(); i++) {
					FailedTransaction trans = new Gson().fromJson(failedTransactionList.get(i).toString(),
							FailedTransaction.class);
					double eventCount = Double.parseDouble(trans.getEventsCount());
					String eventCountStr = String.valueOf(((int)eventCount));
					trans.setEventsCount(eventCountStr);
					List<String> errorMessages = new ArrayList<>();
					for (String message : trans.getNormalisedErrorMessage().split("   ")) {
						errorMessages.add(message);
					}
					
					trans.setErrorMessage(errorMessages);

					resultList.add(trans);
				}
			}
		} catch (JSONException e) {
			LOGGER.error("Failed to fetch any failedTransaction for the given sessionId and RunNumber: Unexpected Error");
		}
		return resultList;
	}

}
