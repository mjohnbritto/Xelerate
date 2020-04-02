package com.suntecgroup.eventlog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.suntecgroup.eventlog.beans.Buk;
import com.suntecgroup.eventlog.beans.EventBean;

/**
 * @author madala.s
 *
 */
@Repository
public class EventLogRespositoryImpl implements EventLogRespository {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventLogRespositoryImpl.class);

	@Autowired
	Environment environment;

	@Autowired
	private RestTemplate restTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.suntecgroup.EventLog.DAO.EventLogDAO#createEventLog(com.suntecgroup.
	 * EventLog.Beans.EventBean,
	 * com.suntecgroup.EventLog.Repository.EventRepository)
	 */
	public String createEventLog(EventBean eventBean) {
		JSONObject customizedRequest = new JSONObject();
		Gson gson = new GsonBuilder().create();
		try {
			String jsonString = gson.toJson(eventBean);
			JSONObject evetBeanObject = new JSONObject(jsonString);
			evetBeanObject.remove("buk");
			HashMap<String, String> constructBukNamesValues = constructBukNamesValues(eventBean);
			evetBeanObject.put("bukNames", constructBukNamesValues.get("bukNames"));
			evetBeanObject.put("bukValues", constructBukNamesValues.get("bukValues"));
			JSONArray eventsList = new JSONArray();
			eventsList.put(evetBeanObject);
			customizedRequest.put("eventBean", eventsList);
			String url = environment.getProperty("xbmc.datastore.bs.EventBeanBS.save");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
		} catch (Exception exception) {
			LOGGER.error("Save/Update may failed");
			LOGGER.error("Exception occurred::", exception);
		}

		return "Success";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.suntecgroup.EventLog.DAO.EventLogDAO#getBySessionId(com.suntecgroup.
	 * EventLog.Repository.EventRepository,
	 * com.suntecgroup.EventLog.Beans.EventBean)
	 */
	public String getFlowFileUUIDBySessionId(EventBean eventBean) {
		try {
			JSONObject contextParameter = new JSONObject();
			contextParameter.put("a_sessionId", eventBean.getSessionId());
			contextParameter.put("a_runNumber", eventBean.getRunNumber());
			contextParameter.put("a_logType", eventBean.getLogType());
			HashMap<String, String> constructBukNamesValues = constructBukNamesValues(eventBean);
			String bukValues = constructBukNamesValues.get("bukValues");
			String isDummyAvailable = constructBukNamesValues.get("isDummyAvailable");
			contextParameter.put("a_bukValues", bukValues);

			JSONObject context = new JSONObject();
			context.put("context-parameters", contextParameter);

			JSONObject customizedRequest = new JSONObject();
			customizedRequest.put("context", context);

			String url = "";
			if ("true".equalsIgnoreCase(isDummyAvailable)) {
				url = environment.getProperty("xbmc.datastore.bs.EventBeanBS.startswithCondMatchingEntries");
			} else {
				url = environment.getProperty("xbmc.datastore.bs.EventBeanBS.equalsConditionMatchingEntries");
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject response = new JSONObject(responseEntity.getBody().toString());
				JSONArray jsonArray = response.getJSONArray("eventBean");
				Gson gson = new Gson();
				String jsonString = jsonArray.getJSONObject(0).toString();
				EventBean result = gson.fromJson(jsonString, EventBean.class);
				if (null != result && !StringUtils.isBlank(result.getFlowfileUUID())) {
					return result.getFlowfileUUID();
				}
			}

		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.suntecgroup.EventLog.DAO.EventLogDAO#getWholeSession(com.suntecgroup.
	 * EventLog.Repository.EventRepository,
	 * com.suntecgroup.EventLog.Beans.EventBean)
	 */
	public List<EventBean> getWholeSession(EventBean eventBean) {
		List<EventBean> eventList = new ArrayList<EventBean>();

		try {
			JSONObject contextParameter = new JSONObject();
			contextParameter.put("a_sessionId", eventBean.getSessionId());
			contextParameter.put("a_runNumber", eventBean.getRunNumber());
			contextParameter.put("a_logType", eventBean.getLogType());
			HashMap<String, String> constructBukNamesValues = constructBukNamesValues(eventBean);
			String bukValues = constructBukNamesValues.get("bukValues");
			String isDummyAvailable = constructBukNamesValues.get("isDummyAvailable");
			contextParameter.put("a_bukValues", bukValues);

			JSONObject context = new JSONObject();
			context.put("context-parameters", contextParameter);

			JSONObject customizedRequest = new JSONObject();
			customizedRequest.put("context", context);

			String url = "";
			if ("true".equalsIgnoreCase(isDummyAvailable)) {
				url = environment.getProperty("xbmc.datastore.bs.EventBeanBS.startswithCondMatchingEntries");
			} else {
				url = environment.getProperty("xbmc.datastore.bs.EventBeanBS.equalsConditionMatchingEntries");
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject response = new JSONObject(responseEntity.getBody().toString());
				JSONArray jsonArray = response.getJSONArray("eventBean");
				Gson gson = new Gson();
				for (int index = 0; index < jsonArray.length(); index++) {
					String jsonString = jsonArray.getJSONObject(index).toString();
					JSONObject evetBeanObject = new JSONObject(jsonString);
					EventBean result = gson.fromJson(jsonString, EventBean.class);
					result.setBuk(rePositionTheBUK(evetBeanObject.getString("bukNames"),
							evetBeanObject.getString("bukValues")));

					eventList.add(result);
				}
				if (null != eventList && !eventList.isEmpty()) {
					return eventList;
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
		}
		return null;
	}

	@Override
	public String getClusterNodeIDBySessionId(EventBean eventBean) {
		try {
			JSONObject contextParameter = new JSONObject();
			contextParameter.put("a_sessionId", eventBean.getSessionId());
			contextParameter.put("a_runNumber", eventBean.getRunNumber());
			contextParameter.put("a_logType", eventBean.getLogType());
			HashMap<String, String> constructBukNamesValues = constructBukNamesValues(eventBean);
			String bukValues = constructBukNamesValues.get("bukValues");
			String isDummyAvailable = constructBukNamesValues.get("isDummyAvailable");
			contextParameter.put("a_bukValues", bukValues);

			JSONObject context = new JSONObject();
			context.put("context-parameters", contextParameter);

			JSONObject customizedRequest = new JSONObject();
			customizedRequest.put("context", context);

			String url = "";
			if ("true".equalsIgnoreCase(isDummyAvailable)) {
				url = environment.getProperty("xbmc.datastore.bs.EventBeanBS.startswithCondMatchingEntries");
			} else {
				url = environment.getProperty("xbmc.datastore.bs.EventBeanBS.equalsConditionMatchingEntries");
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject response = new JSONObject(responseEntity.getBody().toString());
				JSONArray jsonArray = response.getJSONArray("eventBean");
				Gson gson = new Gson();
				String jsonString = jsonArray.getJSONObject(0).toString();
				EventBean result = gson.fromJson(jsonString, EventBean.class);
				if (null != result && !StringUtils.isBlank(result.getClusterNodeId())) {
					return result.getClusterNodeId();
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
		}
		return null;
	}

	private HashMap<String, String> constructBukNamesValues(EventBean eventBean) {
		HashMap<String, String> responseMap = new HashMap<String, String>();
		String isDummyAvailable = "false";
		Buk[] buks = eventBean.getBuk();
		List<String> attrList = new ArrayList<String>();
		for (Buk buk : buks) {
			attrList.add(buk.getAttributeName());
		}
		Collections.sort(attrList);
		String bukNames = "";
		String bukValues = "";
		Iterator<String> iterator = attrList.iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			String currentBukValue = "";
			for (Buk buk : buks) {
				if (buk.getAttributeName().equals(name)) {
					currentBukValue = buk.getAttributeValue();
					break;
				}
			}
			if (!StringUtils.isBlank(currentBukValue)) {
				bukNames += name;
				bukValues += currentBukValue;
				if (iterator.hasNext()) {
					bukNames += "$";
					bukValues += "$";
				}
			} else {
				isDummyAvailable = "true";
			}

		}
		responseMap.put("isDummyAvailable", isDummyAvailable);
		responseMap.put("bukNames", bukNames);
		responseMap.put("bukValues", bukValues);

		return responseMap;
	}

	private Buk[] rePositionTheBUK(String bukNames, String bukValues) {
		String[] names = bukNames.split("\\$");
		String[] values = bukValues.split("\\$");

		List<Buk> listBuk = new ArrayList<Buk>();

		for (int index = 0; index < names.length; index++) {
			if (!StringUtils.isBlank(names[index])) {
				Buk buk = new Buk();
				buk.setAttributeName(names[index]);
				buk.setAttributeValue(values[index]);
				listBuk.add(buk);
			}
		}
		Buk[] bukArray = new Buk[listBuk.size()];
		for (int index = 0; index < listBuk.size(); index++) {
			bukArray[index] = listBuk.get(index);
		}
		return bukArray;
	}

}
