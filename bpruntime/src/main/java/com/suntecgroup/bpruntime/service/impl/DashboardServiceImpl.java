package com.suntecgroup.bpruntime.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.suntecgroup.bpruntime.bean.adminconsole.CompositeKey;
import com.suntecgroup.bpruntime.bean.adminconsole.FailedTransaction;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplayData;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplayRequestEntity;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplayResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplaySessionRequest;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplaySessionResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.Status;
import com.suntecgroup.bpruntime.constant.Constant;
import com.suntecgroup.bpruntime.dao.SessionManagerDao;
import com.suntecgroup.bpruntime.dao.TransactionDAO;
import com.suntecgroup.bpruntime.service.DashboardService;
import com.suntecgroup.bpruntime.service.NifiService;

/*
 * This class Resume functionality to re process failed events
 * 
 * @version 1.0 - December 2018
 * @author Ramesh B
 */
@Service
public class DashboardServiceImpl implements DashboardService {
	@Autowired
	NifiService nifiService;

	@Autowired
	private Environment env;

	@Autowired
	SessionManagerServiceImpl sessionManagerServiceImpl;

	@Autowired
	SessionManagerDao sessionManagerDao;

	@Autowired
	TransactionServiceImpl transactionServiceImpl;

	@Autowired
	TransactionDAO transactionDAO;

	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardServiceImpl.class);

	public static final List<ReplayData> queuedDataForReplay = new ArrayList<ReplayData>();

	/**
	 * callReplayApi @param ReplayRequestEntity @return ReplayResponse @throws
	 */
	private ReplayResponse callReplayApi(ReplayRequestEntity replayRequestEntity) {
		ResponseEntity<String> nifiApiResponse = null;
		ReplayResponse replayResponse = new ReplayResponse();
		try {
			nifiApiResponse = nifiService.replayEvent(replayRequestEntity);
			LOGGER.info("callReplayApi completed with response:" + nifiApiResponse);
		} catch (HttpStatusCodeException e) {
			LOGGER.error("Exception occurs callReplayApi",e);
			int statusCode = e.getStatusCode().value();
			replayResponse.setStatusCode(Integer.toString(e.getRawStatusCode()));
			replayResponse.setStatusDescription(
					"for event id " + replayRequestEntity.getEventId() + " some exception occured.Please try again.");// TODO:add
																														// logger

			if (statusCode == 404) {
				replayResponse.setStatusDescription(
						"for event id " + replayRequestEntity.getEventId() + " unable to find the specified event.");// TODO:add
																														// logger
			}
			if (statusCode == 400) {
				replayResponse.setStatusDescription("for event id " + replayRequestEntity.getEventId()
						+ " ,correct id of the event must be specified.");// TODO:add
																			// logger
			}
		}
		if (nifiApiResponse != null) {
			if (nifiApiResponse.getStatusCode().equals(HttpStatus.CREATED)) {
				replayResponse.setStatusCode(HttpStatus.CREATED.toString());
				replayResponse.setStatusDescription(
						"for event id " + replayRequestEntity.getEventId() + " Successful Resume Operation. ");// TODO:add
																												// logger
			}
		}
		return replayResponse;
	}

	@Override
	public String getDashboardSessionDetails(String sessionId, String runNumber) {
		String response;
		String responseMessage = "";
		String processGroupId = "";
		String nifiApiPayload = "";
		int flowFilesQueued = 0;
		List<SessionDetails> sessionDetails = null;

		Map<String, Object> responseMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();

		try {
			sessionDetails = sessionManagerDao.getBySesIdRunNum(sessionId, runNumber);
			if (sessionDetails.size() == 0) {
				responseMessage = "No matching session detail found.";
			} else {
				responseMap.put("sessionDetails", sessionDetails);
				processGroupId = sessionDetails.get(0).getSessionProcessGroupId();
				if (!StringUtils.isEmpty(processGroupId)) {
					nifiApiPayload = nifiService.getSessionDetails(processGroupId);
					JSONObject responseObject = new JSONObject(nifiApiPayload);
					if (responseObject.get("status") != null) {
						responseObject = new JSONObject(responseObject.get("status").toString());
						if (responseObject.get("aggregateSnapshot") != null) {
							responseObject = new JSONObject(responseObject.get("aggregateSnapshot").toString());
							if (responseObject.get("flowFilesQueued") != null) {
								flowFilesQueued = Integer.parseInt(responseObject.get("flowFilesQueued").toString());
							}
						}
					}
				}
				responseMessage = "Fetch successful.";
				responseMap.put("statusCode", Status.SUCCESS.getStatusCode());
				responseMap.put("statusMessage", Status.SUCCESS + " : " + responseMessage);
			}
			responseMap.put("flowFilesQueued", Integer.valueOf(flowFilesQueued));
		} catch (JSONException je) {
			LOGGER.error("Json Parse Exception getDashboardSessionDetails() -" + je);
			responseMap.put("statusCode", Status.FAILURE.getStatusCode());
			responseMap.put("statusMessage", Status.FAILURE + " : " + je.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in getDashboardSessionDetails() -" + e);
			responseMap.put("statusCode", Status.FAILURE.getStatusCode());
			responseMap.put("statusMessage", Status.FAILURE + " : " + e.getMessage());
		}
		try {
			response = mapper.writeValueAsString(responseMap);
		} catch (IOException e) {
			LOGGER.error("Exception in getDashboardSessionDetails() -" + e);
			return "";
		}
		return response;
	}

	/*
	 * This is method is to place the Technical errors in replay queue
	 */
	public ReplaySessionResponse<List> replayTechnicalError(ReplaySessionRequest replaySessionRequest) {

		try {
			// Check if there's any active session for this template
			List<SessionDetails> listSession = sessionManagerDao
					.getActiveSessionDetails(replaySessionRequest.getSessionTemplateId());
			if (null != listSession && listSession.size() > 0) {
				return new ReplaySessionResponse<List>(Status.SUCCESS.getStatusCode(), Status.FAILURE,
						"Cannot replay session, since already a session is running", null);
			}

			// Check if there's any data available for replay
			List<FailedTransaction> failedTransactionDetails = transactionServiceImpl.getFailedTransactionDetails(
					replaySessionRequest.getSessionId(), replaySessionRequest.getRunNumber(), Constant.TECHNICAL_ERROR,
					false);
			if (null == failedTransactionDetails || failedTransactionDetails.size() < 1) {
				return new ReplaySessionResponse<List>(Status.SUCCESS.getStatusCode(), Status.FAILURE,
						"No data found for replaying", null);
			}

			ReplayData replayData = new ReplayData();
			replayData.setTemplateId(replaySessionRequest.getSessionTemplateId());
			replayData.setSessionId(failedTransactionDetails.get(0).getSessionId());
			replayData.setRunNumber(failedTransactionDetails.get(0).getRunNumber());
			replayData.setErrorType(Constant.TECHNICAL_ERROR);
			ArrayList<String> flowfileUUIDs = new ArrayList<String>();
			for (FailedTransaction failedTransaction : failedTransactionDetails) {
				// Get flowfileUUID for replay
				flowfileUUIDs.add(failedTransaction.getFlowfileUUID());
				// Address the failed transaction
				failedTransaction.setAddressed(true);
				failedTransaction.setAction(Constant.ACTION_REPLAY);
				transactionDAO.saveFailedTransaction(failedTransaction);
			}
			replayData.setFlowfileUUIDs(flowfileUUIDs);

			List<SessionDetails> sessionDetailsList = sessionManagerDao
					.getBySesIdRunNum(replaySessionRequest.getSessionId(), replaySessionRequest.getRunNumber());
			SessionDetails sessionDtl = sessionDetailsList.get(0);
			sessionDtl.setIsTechnicalFailureReplayed(true);
			sessionManagerDao.updateSession(sessionDtl);

			// Queueing the data for replay
			queuedDataForReplay.add(replayData);

			return new ReplaySessionResponse<List>(Status.SUCCESS.getStatusCode(), Status.SUCCESS,
					"Failed events are queued for replaying", null);
		} catch (Exception exception) {
			LOGGER.error("Exception in replayTechnicalError() -" + exception);
			return new ReplaySessionResponse<List>(Status.FAILURE.getStatusCode(), Status.FAILURE,
					exception.getMessage(), null);
		}
	}

	/*
	 * This is a scheduled method for replaying the errors both technical and
	 * business errors
	 */
	@Scheduled(cron = "${cronExpShutDown}")
	public void replayFailedEvents() {

		try {
			// Check the queue for data
			if (null == queuedDataForReplay || queuedDataForReplay.size() < 1) {
				return;
			}

			ReplayData queueData = queuedDataForReplay.get(0);

			// Check if there's any active session for this template
			List<SessionDetails> listSession = sessionManagerDao.getActiveSessionDetails(queueData.getTemplateId());
			if (null != listSession && listSession.size() > 0) {
				// Cannot replay session, since already a session is running
				return;
			}
			
			// Replay logic starts here
			
			//Remove the entry from the map as we are going to replay that now
			queuedDataForReplay.remove(0);
			
			String getProvenanceDataUrl = null;
			List<Integer> listEvent = new ArrayList<Integer>();
			List<String> listClusterNodeID = new ArrayList<String>();
			String requestPayload = null;
			JsonObject nifiApiRequest = new JsonObject();
			Gson gson = new Gson();
			HttpEntity<String> nifiProvenanceResponse = null;
			HttpEntity<String> requestEntity = null;
			HttpHeaders headers = new HttpHeaders();
			JsonObject searchTerms = new JsonObject();
			JSONObject provenanceDetail = null;
			JSONObject eventDetailsJson = null;
			JSONObject provenanceEventDetailsJson = null;
			JSONObject results = null;
			JSONObject eventDetail = null;
			JSONArray provenanceEvents = null;
			ResponseEntity<String> provenanceEventsData = null;
			try {

				searchTerms.addProperty(Constant.SESSION_ID, queueData.getSessionId());
				searchTerms.addProperty(Constant.RUN_NUMBER, queueData.getRunNumber());

				for (String flowFileUUID : queueData.getFlowfileUUIDs()) {
					searchTerms.addProperty(Constant.FLOWFILEUUID, flowFileUUID);
					JsonObject request = new JsonObject();
					request.add("searchTerms", searchTerms);
					request.addProperty("maxResults", 1000);
					request.addProperty("summarize", false);
					request.addProperty("incrementalResults", true);
					JsonObject provenance = new JsonObject();
					provenance.add("request", request);
					nifiApiRequest.add("provenance", provenance);
					headers.set("Content-Type", "application/json");
					requestPayload = gson.toJson(nifiApiRequest);
					requestEntity = new HttpEntity<String>(requestPayload, headers);

					nifiProvenanceResponse = nifiService.generateProvenanceDataId(requestEntity);

					if (nifiProvenanceResponse != null) {

						JSONObject provenanceDetailsJson = new JSONObject(nifiProvenanceResponse.getBody());
						provenanceDetail = provenanceDetailsJson.getJSONObject("provenance");
						getProvenanceDataUrl = provenanceDetail.getString("uri");
						if (provenanceDetail.getBoolean("finished")) {
							provenanceEventDetailsJson = provenanceDetail;
						} else {
							provenanceEventsData = nifiService.getProvenanceData(getProvenanceDataUrl);
							eventDetailsJson = new JSONObject(provenanceEventsData.getBody());
							provenanceEventDetailsJson = eventDetailsJson.getJSONObject("provenance");
						}
						// call delete query to avoid 409 error
						nifiService.deleteProvenanceDataId(getProvenanceDataUrl);

						if (provenanceEventDetailsJson != null) {
							results = provenanceEventDetailsJson.getJSONObject("results");
						}
						if (results != null) {
							provenanceEvents = results.getJSONArray("provenanceEvents");
						}
						boolean isCluster = "true".equals(env.getProperty("deployment.iscluster"));
						if (provenanceEvents != null && provenanceEvents.length() > 0) {
							for (int index = (provenanceEvents.length() - 1); index > -1; index--) {
								eventDetail = provenanceEvents.getJSONObject(index);
								if (eventDetail.getBoolean("replayAvailable")) {
									listEvent.add(eventDetail.getInt("eventId"));
									if (isCluster) {
										listClusterNodeID.add(eventDetail.getString("clusterNodeId"));
									} else {
										listClusterNodeID.add("");
									}
									break;
								}
							}
						}else{
               LOGGER.info("provenance event is empty");
						}
					} else {
						// Some exception occured. Please try again after some
						// time
						LOGGER.info("provenance data is empty");
						return;
					}
				}

				// call replay API
				if (listEvent.size() > 0) {

					SessionDetails sessionObject = sessionManagerDao
							.getLatestSessionRunForThisSession(queueData.getSessionId());
					if (null != sessionObject) {
						Date date = new Date();
						int runNumber = Integer.parseInt(sessionObject.getRunNumber()) + 1;
						sessionObject.setStatus(Constant.SHUTTING_DOWN);
						sessionObject.setRunNumber(Integer.toString(runNumber));
						sessionObject.setStartTime(sessionManagerServiceImpl.dateFormat.format(date));
						sessionObject.setLastUpdatedTime(sessionManagerServiceImpl.dateFormat.format(date));
						sessionObject.setEndTime("");
						sessionObject.setEventTotalCount(0);
						sessionObject.setEventTechnicalFailureCount(0);
						sessionObject.setEventBusinessFailureCount(0);
						sessionObject.setEventAddressedFailureCount(0);
						sessionObject.setEventUnAddressedFailureCount(0);
						sessionObject.setEventSuccessCount(0);
						sessionObject.setTransactionTotalCount(0);
						sessionObject.setTransactionSuccessCount(0);
						sessionObject.setTransactionTechnicalFailureCount(0);
						sessionObject.setTransactionBusinessFailureCount(0);
						sessionObject.setTransactionAddressedFailureCount(0);
						sessionObject.setTransactionUnAddressedFailureCount(0);
						sessionObject.setInactiveSince(sessionManagerServiceImpl.dateFormat.format(date));
						sessionObject.setIsSessionInUse(true);
						sessionObject.setDependentRunNumber(queueData.getRunNumber());
						sessionObject.setRunType(queueData.getErrorType());
						sessionObject.setIsTechnicalFailureReplayed(false);
						sessionManagerDao.saveSession(sessionObject);
						nifiService.updateVariableRegistry(sessionObject, true);
					}

					nifiService.startStopFlow(sessionObject.getSessionProcessGroupId(), Constant.RUNNING);
					List<String> firstProcessorList = sessionManagerServiceImpl
							.getFirstProcessors(sessionObject.getSessionProcessGroupId());
					nifiService.stopProcessor(firstProcessorList);
					List<String> listResponse = new ArrayList<String>();
					for (int index = 0; index < listEvent.size(); index++) {
						LOGGER.info("Failure Event " + listEvent.get(index).toString() + " sent to replay api");
						ReplayRequestEntity replayRequest = new ReplayRequestEntity();
						replayRequest.setEventId(listEvent.get(index).toString());
						replayRequest.setClusterNodeId(listClusterNodeID.get(index));
						ReplayResponse replayApiResponse = new ReplayResponse();
						replayApiResponse = callReplayApi(replayRequest);
						listResponse.add("Status code:" + replayApiResponse.getStatusCode() + " Status Desc:"
								+ replayApiResponse.getStatusDescription());
					}
				}

			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
				return;
			}

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			return;
		}
	}

}
