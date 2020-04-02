package com.suntecgroup.bpruntime.dao.impl;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.Asset;
import com.suntecgroup.bpruntime.bean.adminconsole.FileNameDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.OperatorStats;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.Status;
import com.suntecgroup.bpruntime.bean.adminconsole.TransactionDetail;
import com.suntecgroup.bpruntime.constant.Constant;
import com.suntecgroup.bpruntime.dao.SessionManagerDao;
import com.suntecgroup.bpruntime.model.AssetRequest;
import com.suntecgroup.bpruntime.model.AssetResponse;
import com.suntecgroup.bpruntime.model.Context;
import com.suntecgroup.bpruntime.model.ContextParameters;

@Repository
public class SessionManagerDaoImpl implements SessionManagerDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionManagerDaoImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	Environment environment;

	@Override
	public void saveSession(SessionDetails sessionDetails) {
		saveSessionDetailsToXBMCDataStore(sessionDetails);
	}

	@Override
	public List<SessionDetails> getSessionDetails(String templateId) {
		List<SessionDetails> sessionList = new ArrayList<SessionDetails>();
		try {
			JSONObject contextParameter = new JSONObject();
			contextParameter.put("a_sessionTemplateId", templateId);

			JSONObject context = new JSONObject();
			context.put("context-parameters", contextParameter);

			JSONObject customizedRequest = new JSONObject();
			customizedRequest.put("context", context);

			String url = environment.getProperty("xbmc.datastore.bs.SessionDetailsBS.getSessionDetailsByTemplateId");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject response = new JSONObject(responseEntity.getBody().toString());
				JSONArray jsonArray = response.getJSONArray("sessionDetails");
				Gson gson = new Gson();
				for (int index = 0; index < jsonArray.length(); index++) {
					String jsonString = jsonArray.getJSONObject(index).toString();
					SessionDetails sessionDetails = gson.fromJson(jsonString, SessionDetails.class);
					sessionList.add(sessionDetails);
				}
			}

		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
		}
		return sessionList;
	}

	@Override
	public void updateSession(SessionDetails sessionDetails) {
		saveSessionDetailsToXBMCDataStore(sessionDetails);
	}

	@Override
	public List<SessionDetails> getSessionDetailsByPGI(String processGroupId) {
		List<SessionDetails> sessionList = new ArrayList<SessionDetails>();
		try {
			JSONObject contextParameter = new JSONObject();
			contextParameter.put("a_sessionProcessGroupId", processGroupId);

			JSONObject context = new JSONObject();
			context.put("context-parameters", contextParameter);

			JSONObject customizedRequest = new JSONObject();
			customizedRequest.put("context", context);

			String url = environment.getProperty("xbmc.datastore.bs.SessionDetailsBS.getSessionDetailsByPGI");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject response = new JSONObject(responseEntity.getBody().toString());
				JSONArray jsonArray = response.getJSONArray("sessionDetails");
				Gson gson = new Gson();
				for (int index = 0; index < jsonArray.length(); index++) {
					String jsonString = jsonArray.getJSONObject(index).toString();
					SessionDetails sessionDetails = gson.fromJson(jsonString, SessionDetails.class);
					sessionList.add(sessionDetails);
				}
			}

		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
		}
		return sessionList;
	}

	@Override
	public List<SessionDetails> getBySesIdRunNum(String sessionId, String runNumber) {
		List<SessionDetails> sessionList = new ArrayList<SessionDetails>();
		try {
			JSONObject contextParameter = new JSONObject();
			contextParameter.put("a_runNumber", runNumber);
			contextParameter.put("a_sessionId", sessionId);

			JSONObject context = new JSONObject();
			context.put("context-parameters", contextParameter);

			JSONObject customizedRequest = new JSONObject();
			customizedRequest.put("context", context);

			String url = environment.getProperty("xbmc.datastore.bs.SessionDetailsBS.getBySesIdRunNum");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject response = new JSONObject(responseEntity.getBody().toString());
				JSONArray jsonArray = response.getJSONArray("sessionDetails");
				Gson gson = new Gson();
				for (int index = 0; index < jsonArray.length(); index++) {
					String jsonString = jsonArray.getJSONObject(index).toString();
					SessionDetails sessionDetails = gson.fromJson(jsonString, SessionDetails.class);
					sessionList.add(sessionDetails);
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
		}
		return sessionList;
	}

	@Override
	public List<SessionDetails> getSessionDetailsByStatus(String status) {
		List<SessionDetails> sessionList = new ArrayList<SessionDetails>();
		try {
			JSONObject contextParameter = new JSONObject();
			contextParameter.put("a_status", status);

			JSONObject context = new JSONObject();
			context.put("context-parameters", contextParameter);

			JSONObject customizedRequest = new JSONObject();
			customizedRequest.put("context", context);

			String url = environment.getProperty("xbmc.datastore.bs.SessionDetailsBS.getSessionDetailsByStatus");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject response = new JSONObject(responseEntity.getBody().toString());
				JSONArray jsonArray = response.getJSONArray("sessionDetails");
				Gson gson = new Gson();
				for (int index = 0; index < jsonArray.length(); index++) {
					String jsonString = jsonArray.getJSONObject(index).toString();
					SessionDetails sessionDetails = gson.fromJson(jsonString, SessionDetails.class);
					sessionList.add(sessionDetails);
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
		}
		return sessionList;
	}

	@Override
	public List<SessionDetails> getActiveSessionDetails(String templateId) {
		List<SessionDetails> sessionList = new ArrayList<SessionDetails>();

		try {
			JSONObject contextParameter = new JSONObject();
			contextParameter.put("a_sessionTemplateId", templateId);
			contextParameter.put("a_status", Constant.INPROGRESS);

			JSONObject context = new JSONObject();
			context.put("context-parameters", contextParameter);

			JSONObject customizedRequest = new JSONObject();
			customizedRequest.put("context", context);

			String url = environment.getProperty("xbmc.datastore.bs.SessionDetailsBS.getActiveSessionDetails");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject response = new JSONObject(responseEntity.getBody().toString());
				JSONArray jsonArray = response.getJSONArray("sessionDetails");
				Gson gson = new Gson();
				for (int index = 0; index < jsonArray.length(); index++) {
					String jsonString = jsonArray.getJSONObject(index).toString();
					SessionDetails sessionDetails = gson.fromJson(jsonString, SessionDetails.class);
					sessionList.add(sessionDetails);
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
		}

		if (null != sessionList && sessionList.size() > 0) {
			return sessionList;
		} else {
			return null;
		}
	}

	@Override
	public SessionDetails getLatestSessionRunForThisSession(String sessionId) {
		List<SessionDetails> sessionList = new ArrayList<SessionDetails>();

		try {
			JSONObject contextParameter = new JSONObject();
			contextParameter.put("a_sessionId", sessionId);

			JSONObject context = new JSONObject();
			context.put("context-parameters", contextParameter);

			JSONObject customizedRequest = new JSONObject();
			customizedRequest.put("context", context);

			String url = environment.getProperty("xbmc.datastore.bs.SessionDetailsBS.getLatestSessionRunForThisSess");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject response = new JSONObject(responseEntity.getBody().toString());
				JSONArray jsonArray = response.getJSONArray("sessionDetails");
				Gson gson = new Gson();
				String jsonString = jsonArray.getJSONObject(0).toString();
				SessionDetails sessionDetails = gson.fromJson(jsonString, SessionDetails.class);
				sessionList.add(sessionDetails);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred::", exception);
		}

		if (null != sessionList && sessionList.size() > 0) {
			return sessionList.get(0);
		} else {
			return null;
		}
	}

	private void saveSessionDetailsToXBMCDataStore(SessionDetails sessionDetails) {
		JSONObject customizedRequest = new JSONObject();
		Gson gson = new GsonBuilder().create();
		try {
			String jsonString = gson.toJson(sessionDetails);
			JSONObject sessionObject = new JSONObject(jsonString);
			JSONArray sessionList = new JSONArray();
			sessionList.put(sessionObject);
			customizedRequest.put("sessionDetails", sessionList);
			String url = environment.getProperty("xbmc.datastore.bs.SessionDetailsBS.save");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
		} catch (Exception exception) {
			LOGGER.error("Save/Update may failed");
			LOGGER.error("Exception occurred::", exception);
		}
	}

	// BP Asset
	@Override
	public ApiResponse<Asset> getBPAsset(String department, String module, String release, int artifact_id,
			String assetType, String assetName, Object version) {

		ApiResponse<Asset> bpApiResponse = null;
		Asset data = null;
		ResponseEntity<AssetResponse> assetResponse = null;

		try {
			AssetRequest assetRequest = new AssetRequest();
			Context context = new Context();
			ContextParameters contextParameters = new ContextParameters();
			contextParameters.setAAssetDepartment(department);
			contextParameters.setAAssetModule(module);
			contextParameters.setAAssetRelease(release);
			contextParameters.setAAssetname(assetName);
			contextParameters.setAAssettype(assetType);
			context.setContextParameters(contextParameters);
			assetRequest.setContext(context);
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			final HttpEntity<?> request = new HttpEntity<>(assetRequest, headers);
			// Get latest version
			if (version == null || version.toString().isEmpty()) {
				String url = environment.getProperty("xbmc.datastore.bs.DeployedAssetBS.sortByRunNumber");
				assetResponse = restTemplate.postForEntity(url, request, AssetResponse.class);
			} else {
				contextParameters.setAAssetversion(version.toString());
				String url = environment.getProperty("xbmc.datastore.bs.DeployedAssetBS.getByVersion");
				assetResponse = restTemplate.postForEntity(url, request, AssetResponse.class);
			}
			List<Asset> assetList = assetResponse.getBody().getDeployedAsset();
			if (assetList != null && assetList.size() > 0) {
				data = assetList.get(0);
				data.setVersion(data.getAssetVersion());
			}
			if (data == null) {
				throw new Exception(Constant.DATA_NOT_FOUND);
			} else {
				bpApiResponse = new ApiResponse<Asset>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null, data);
			}
		} catch (Exception e) {
			LOGGER.error("Execption in getBPAsset", e);
			bpApiResponse = new ApiResponse<Asset>(Status.FAILURE.getStatusCode(), Status.FAILURE, e.getMessage(),
					data);
		}
		return bpApiResponse;
	}

	// OperatorStats
	/*@Override
	public List<OperatorStats> getBySesIdRunNumOperatorName(String sessionId, String runNumber, String operatorName) {

		List<SessionDetails> sessionList = mongoTemplate.find(query, SessionDetails.class);
		if (null != sessionList && sessionList.size() > 0) {
			return sessionList.get(0);
		} else {
			return null;
		}
	}*/
	
	@Override
	public JsonArray getBySesIdRunNumOperatorName(String sessionId, String runNumber, String operatorName) {
		String url = environment.getProperty("xbmc.datastore.bs.OperatorStatsBS.queryBySessionRunAndOpName");
		JsonArray result = null;
		try {
			final HttpHeaders headers = new HttpHeaders();
			// Constructing request
			JSONObject requestObject = new JSONObject();
			JSONObject context = new JSONObject();
			JSONObject context_parameters = new JSONObject();
			context_parameters.put("a_operatorName", operatorName);
			context_parameters.put("a_runNumber", runNumber);
			context_parameters.put("a_sessionId", sessionId);
			context.put("context-parameters", context_parameters);
			requestObject.put("context", context);

			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestObject.toString(), headers);

			ResponseEntity<?> responseEntity = restTemplate.postForEntity(
					url, requestEntity, String.class);
			String deployedTemplate = (String) responseEntity.getBody();
			JsonObject jsonObj = new Gson().fromJson(deployedTemplate, JsonObject.class);
			// .new JsonObject(deployedTemplate);
			if (jsonObj.has("operatorStats")) {
				result = jsonObj.getAsJsonArray("operatorStats");
			}

			return result;
		} catch (Exception ex) {
			LOGGER.error("Unable to fetch the operator statistics for the given session id and run number");
		}
		return result;
	}

	@Override
	public void updateOperatorStats(OperatorStats operatorstats) {
		JSONObject customizedRequest = new JSONObject();
		Gson gson = new GsonBuilder().create();
		String url = environment.getProperty("xbmc.datastore.bs.OperatorStatsBS.save");
		try {
			String jsonString = gson.toJson(operatorstats);
			JSONObject operatorStatsObj = new JSONObject(jsonString);
			JSONArray opStatsList = new JSONArray();
			opStatsList.put(operatorStatsObj);
			customizedRequest.put("operatorStats", opStatsList);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity,
					String.class);
			if (!responseEntity.getStatusCode().is2xxSuccessful()) {
				throw new Exception("failed to Save the Data");
			}

		} catch (Exception exception) {
			LOGGER.error("Save/Update may failed");
			LOGGER.error("Exception occurred::", exception);
		}
	}

	// FileNameDetails
	@Override
	public void saveFileNameDtl(FileNameDetails fileNameDetails) {
		String url = environment.getProperty("xbmc.datastore.bs.FileNameDetailsBS.save");
		JSONObject customizedRequest = new JSONObject();
		Gson gson = new GsonBuilder().create();

		try {
			String jsonString = gson.toJson(fileNameDetails);
			JSONObject fileNameDetailsObj = new JSONObject(jsonString);
			JSONArray fileNameArr = new JSONArray();
			fileNameArr.put(fileNameDetailsObj);
			customizedRequest.put("fileNameDetails", fileNameArr);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(customizedRequest.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity,
					String.class);
			if (!responseEntity.getStatusCode().is2xxSuccessful()) {
				throw new Exception("failed to Save the Data");
			}

		} catch (Exception exception) {
			LOGGER.error("Save/Update may failed");
			LOGGER.error("Exception occurred::", exception);
		}

	}

	@Override
	public String getFileNameStatus(TransactionDetail transactionDtl) {
		String url = environment.getProperty("xbmc.datastore.bs.FileNameDetailsBS.sessionIdandrunNumberand3more");
		int count = 0;
		String duration = "";
		String status = "Success";
		if (transactionDtl.getDuration() != null && transactionDtl.getDuration() != "")
			duration = transactionDtl.getDuration();
		if (transactionDtl.getCount() != null && transactionDtl.getCount() != "")
			count = Integer.parseInt(transactionDtl.getCount());
		Calendar currentDate = new GregorianCalendar();
		Calendar pastDate = (Calendar) currentDate.clone();
		 switch (duration) {
		case "mins":
			pastDate.add(Calendar.MINUTE, -count);
			break;
		case "hours":
			pastDate.add(Calendar.HOUR, -count);
			break;
		case "days":
			pastDate.add(Calendar.DATE, -count);
			break;
		case "month":
			pastDate.add(Calendar.MONTH, -count);
			break;
		case "year":
			pastDate.add(Calendar.YEAR, -count);
			break;
		}
		Date fromDate = pastDate.getTime();
		Date toDate = currentDate.getTime();
		String fromDateStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(fromDate);
		String toDateStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(toDate);
		List<FileNameDetails> fileNameStatusDtl = new ArrayList<>();
		try {
			JsonArray result = null;
			final HttpHeaders headers = new HttpHeaders();
			// Constructing request
			JSONObject requestObject = new JSONObject();
			JSONObject context = new JSONObject();
			JSONObject context_parameters = new JSONObject();
			context_parameters.put("a_file", transactionDtl.getFileName());
			context_parameters.put("a_runNo", transactionDtl.getRunNumber());
			context_parameters.put("a_session", transactionDtl.getSessionId());
			context_parameters.put("a_ter", fromDateStr);
			context_parameters.put("a_teri", toDateStr);
			context.put("context-parameters", context_parameters);
			requestObject.put("context", context);

			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestObject.toString(), headers);

			ResponseEntity<?> responseEntity = restTemplate.postForEntity(
					url, requestEntity,
					String.class);
			String deployedTemplate = (String) responseEntity.getBody();
			JsonObject jsonObj = new Gson().fromJson(deployedTemplate, JsonObject.class);
			// .new JsonObject(deployedTemplate);
			if (jsonObj.has("fileNameDetails")) {
				result = jsonObj.getAsJsonArray("fileNameDetails");
			}

			for (JsonElement jo : result) {
				FileNameDetails fileNameDetails = new Gson().fromJson(jo, FileNameDetails.class);
				fileNameStatusDtl.add(fileNameDetails);
			}
		} catch (Exception ex) {
			LOGGER.error("Unable to fetch the file name details for the given key");
		}

		if (!fileNameStatusDtl.isEmpty()) {
			status = "FileName Duplication Check Failed";
		}
		return status;
	}

}
