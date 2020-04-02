package com.suntecgroup.bpruntime.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.BPState;
import com.suntecgroup.bpruntime.bean.adminconsole.CompositeKey;
import com.suntecgroup.bpruntime.bean.adminconsole.CounterDetailsBean;
import com.suntecgroup.bpruntime.bean.adminconsole.FileNameDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.OperatorStats;
import com.suntecgroup.bpruntime.bean.adminconsole.OperatorStatsCompKey;
import com.suntecgroup.bpruntime.bean.adminconsole.RunNumberCounterDtl;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionState;
import com.suntecgroup.bpruntime.bean.adminconsole.Status;
import com.suntecgroup.bpruntime.bean.adminconsole.TransactionDetail;
import com.suntecgroup.bpruntime.constant.Constant;
import com.suntecgroup.bpruntime.dao.SessionManagerDao;
import com.suntecgroup.bpruntime.service.NifiService;
import com.suntecgroup.bpruntime.service.SessionManagerService;
import com.suntecgroup.bpruntime.service.TransactionService;

@Service
public class SessionManagerServiceImpl implements SessionManagerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionManagerServiceImpl.class);
	/*
	 * @Autowired PiService piService;
	 */

	@Autowired
	NifiService nifiService;

	@Autowired
	SessionManagerDao sessionManagerDao;
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private TransactionService transactionService;

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS zzz");

	private static Map<String, RunNumberCounterDtl> sessionDtlsMap = new HashMap<String, RunNumberCounterDtl>();

	@Override
	public String getSessionList(String templateId) {
		List<SessionDetails> list = sessionManagerDao.getSessionDetails(templateId);
		Gson gson = new Gson();
		String response = gson.toJson(list);
		return response;
	}

	/**
	 * startSession
	 */
	@Override
	public ApiResponse<String> startSession(BPState bpState) {

		List<SessionDetails> listSession = sessionManagerDao.getSessionDetails(bpState.getTemplateId());
		ApiResponse<String> response = null;

		try {
			if (listSession.size() > 0) {

				// To stop allowing more than one session running parallelly
				for (SessionDetails sessionDetails : listSession) {
					if (sessionDetails.getStatus().equalsIgnoreCase(Constant.INPROGRESS)
							|| sessionDetails.getStatus().equalsIgnoreCase(Constant.SHUTTING_DOWN)) {
						response = new ApiResponse<String>("101", Status.FAILURE,
								"Cannot create a new session, since already a session is running",
								"Cannot create a new session, since already a session is running");
						return response;
					}
				}

				for (SessionDetails sessionDetails : listSession) {
					// check if a session exist for the template with stopped
					// status
					if (sessionDetails.getStatus().equalsIgnoreCase(Constant.STOPPED)) {
						createSession(bpState.getTemplateId(), sessionDetails.getSessionProcessGroupId(),
								bpState.getBpName(), false);
						break;
					}
				}

			} else {
				// create a new session since there's no previous session
				// available
				createSession(bpState.getTemplateId(), bpState.getBpGroupId(), bpState.getBpName(), true);
			}
		} catch (Exception e) {
			LOGGER.error("Create session failed.", e);
			response = new ApiResponse<String>("101", Status.FAILURE, "Create session failed.",
					"Create session failed.");
			return response;
		}

		response = new ApiResponse<String>("200", Status.SUCCESS, "session created successfully",
				"session created successfully");
		return response;
	}

	/**
	 * createSession
	 * 
	 * @param templateId
	 */
	private void createSession(String templateId, String processGroupId, String bpName,
			Boolean needToInstantiateTemplate) throws Exception {
		Date date = new Date();
		String sessionID = UUID.randomUUID().toString();
		SessionDetails session = new SessionDetails();
		CompositeKey compositeKey = new CompositeKey();

		try {
			compositeKey.setSessionId(sessionID);
			compositeKey.setSessionTemplateId(templateId);
			compositeKey.setRunNumber("1");
			session.setCompositeKey(compositeKey);
			session.setSessionId(sessionID);
			session.setBpName(bpName);
			session.setSessionProcessGroupId(processGroupId);
			session.setSessionTemplateId(templateId);
			session.setStatus(Constant.INPROGRESS);
			session.setStartTime(dateFormat.format(date));
			session.setLastUpdatedTime(dateFormat.format(date));
			session.setEndTime("");
			session.setEventTotalCount(0);
			session.setEventTechnicalFailureCount(0);
			session.setEventBusinessFailureCount(0);
			session.setEventAddressedFailureCount(0);
			session.setEventUnAddressedFailureCount(0);
			session.setEventSuccessCount(0);
			session.setTransactionTotalCount(0);
			session.setTransactionSuccessCount(0);
			session.setTransactionTechnicalFailureCount(0);
			session.setTransactionBusinessFailureCount(0);
			session.setTransactionAddressedFailureCount(0);
			session.setTransactionUnAddressedFailureCount(0);
			session.setInactiveSince(dateFormat.format(date));
			session.setIsSessionInUse(true);
			session.setReceivedFilesCount(0);
			session.setAcceptedFilesCount(0);
			session.setReceivedFilesCount(0);
			session.setTotalRecordsCount(0);
			session.setAcceptedRecordsCount(0);
			session.setRejectedRecordsCount(0);
			session.setRunNumber("1");
			session.setDependentRunNumber("");
			session.setRunType("Root");

			// create an attribute in nifi registry for session id
			nifiService.updateVariableRegistry(session, true);
			nifiService.startStopFlow(processGroupId, Constant.RUNNING);

			sessionManagerDao.saveSession(session);
		} catch (Exception e) {
			LOGGER.error("Execption in creating a session", e);
			throw e;
		}
	}

	@Override
	public ApiResponse<String> stopSession(SessionState sessionState) {
		String status = "";
		ApiResponse<String> apiResponse = null;
		try {
			List<String> firstProcessorList = getFirstProcessors(sessionState.getSessionProcessGroupId());
			boolean response = nifiService.stopProcessor(firstProcessorList);

			if (response) {
				// status="Processor stopped successfully";
				// update session details in DB
				List<SessionDetails> sessionDetailsList = sessionManagerDao
						.getBySesIdRunNum(sessionState.getSessionId(), sessionState.getRunNumber());
				if (sessionDetailsList != null && sessionDetailsList.size() > 0) {
					SessionDetails sessionObject = sessionDetailsList.get(0);
					Date date = new Date();
					sessionObject.setStatus(Constant.SHUTTING_DOWN);
					sessionObject.setLastUpdatedTime(dateFormat.format(date));
					sessionManagerDao.updateSession(sessionObject);
				}
				status = "Session status changed to Shutting Down successfully";
				apiResponse = new ApiResponse<String>("200", Status.SUCCESS, status, status);
			} else {
				status = "Failed to stop session, please contact support team";
				apiResponse = new ApiResponse<String>("101", Status.FAILURE, status, status);
			}
		} catch (JSONException e) {
			LOGGER.error("Json parsing exception occurred", e);
			status = "Failed to stop session, please contact support team";
			apiResponse = new ApiResponse<String>("101", Status.FAILURE, status, status);
		} catch (Exception e) {
			LOGGER.error("Session status changed to Shutting Down successfully but failed to update in DB-", e);
			status = "Failed to stop session, please contact support team";
			apiResponse = new ApiResponse<String>("101", Status.FAILURE, status, status);
		}

		return apiResponse;
	}

	public List<String> getFirstProcessors(String processGroupId) throws JSONException {
		List<String> sourceList = new ArrayList<>();
		List<String> destinationList = new ArrayList<>();
		List<String> firstProcessorList = new ArrayList<>();
		try {
			String processGroupResponse = nifiService.getConnectionDetails(processGroupId);
			JSONObject connectionsObj = new JSONObject(processGroupResponse);
			JSONArray connectionArray = connectionsObj.getJSONArray("connections");
			for (int i = 0; i < connectionArray.length(); i++) {
				JSONObject obj = connectionArray.getJSONObject(i);
				if (obj.getString("sourceId") != null) {
					sourceList.add(obj.getString("sourceId"));
				}
				if (obj.getString("destinationId") != null) {
					destinationList.add(obj.getString("destinationId"));
				}
			}
			for (int i = 0; i < sourceList.size(); i++) {
				if (!destinationList.contains(sourceList.get(i))) {
					firstProcessorList.add(sourceList.get(i));
				}
			}
		} catch (JSONException e) {
			LOGGER.error("Json parsing exception in getFirstProcessors() - " + e.getMessage());
			throw e;
		}
		return firstProcessorList;
	}

	@Override
	public String getBpStatus(String templateId) {
		String bpStatus = "InActive";
		List<SessionDetails> list = sessionManagerDao.getSessionDetails(templateId);
		if (list.size() > 0) {
			for (SessionDetails sessionDetails : list) {
				if (sessionDetails.getStatus().equalsIgnoreCase(Constant.INPROGRESS)
						|| sessionDetails.getStatus().equalsIgnoreCase(Constant.SHUTTING_DOWN)) {
					bpStatus = "Active";
					break;
				}
			}

		}

		return bpStatus;
	}

	@Override
	public int getSessionCountForBp(String templateId) {
		List<SessionDetails> list = sessionManagerDao.getSessionDetails(templateId);
		int sessionCount = 0;
		if (list != null) {
			for (SessionDetails sessionDetails : list) {
				if (sessionDetails.getIsSessionInUse()) {
					sessionCount++;
				}
			}
			return sessionCount;
		}
		return 0;
	}

	// Schedule to stop processor group
	@Scheduled(cron = "${cronExpShutDown}")
	public void scheduledJob() throws Exception {
		List<SessionDetails> sessionsList = sessionManagerDao.getSessionDetailsByStatus(Constant.SHUTTING_DOWN);
		for (SessionDetails list : sessionsList) {

			try {
				String sessionDetails = nifiService.getSessionDetails(list.getSessionProcessGroupId());
				JSONObject responseObject = new JSONObject(sessionDetails);
				if (responseObject.get("status") != null) {
					responseObject = new JSONObject(responseObject.get("status").toString());
					if (responseObject.get("aggregateSnapshot") != null) {
						responseObject = new JSONObject(responseObject.get("aggregateSnapshot").toString());
						if (responseObject.get("flowFilesQueued") != null
								&& 0 == Integer.parseInt(responseObject.get("flowFilesQueued").toString())) {
							nifiService.startStopFlow(list.getSessionProcessGroupId(), Constant.STOPPED);
							LOGGER.info("Session completed for BusinessProcess: " + list.getBpName() + "sessionId: "
									+ list.getSessionId());
							// update session details in DB
							List<SessionDetails> sessionDetailsList = sessionManagerDao
									.getBySesIdRunNum(list.getSessionId(), list.getRunNumber());
							if (sessionDetailsList != null && sessionDetailsList.size() > 0) {
								SessionDetails sessionObject = sessionDetailsList.get(0);
								Date date = new Date();
								sessionObject.setStatus(Constant.STOPPED);
								// sessionObject.setSessionId("0");
								sessionObject.setEndTime(dateFormat.format(date));
								sessionObject.setLastUpdatedTime(dateFormat.format(date));
								sessionObject.setInactiveSince(dateFormat.format(date));
								sessionObject.setIsSessionInUse(false);
								sessionManagerDao.updateSession(sessionObject);
								// create an attribute in nifi registry for
								// session id with value 0
								nifiService.updateVariableRegistry(sessionObject, false);

								// Updating session count status to database
								// and clearing a respective session entry from
								// session counter map
								String sessionId = list.getSessionId();
								String runNumber = list.getRunNumber();
								updateSessionCountStatus(sessionId, runNumber);
								RunNumberCounterDtl runNumberBean = sessionDtlsMap.get(sessionId);
								Map<String, CounterDetailsBean> runNumberMap = runNumberBean.getRunNumberDtlsMap();
								runNumberMap.remove(runNumber);
								if (runNumberMap.size() == 0) {
									sessionDtlsMap.remove(sessionId);
								}
							}
						}
					}
				}

			} catch (JSONException e) {
				LOGGER.error("Json parsing exception occurred when stopped BP:" + list.getBpName(), e);
			} catch (Exception e) {
				LOGGER.error("Session is stopped but not updated in DB for BP:" + list.getBpName(), e);
			}

		}
	}

	@Override
	public String updateTransactionStatus(TransactionDetail transactionDtl) {

		String status = "";
		int sessionInputCount = 0;
		try {
			String sessionId = transactionDtl.getSessionId();
			String runNumber = transactionDtl.getRunNumber();
			String transactionId = transactionDtl.getTransactionId();

			if (transactionId.isEmpty()) {
				status = "Transaction Id is empty.";
			} else {
				if (sessionDtlsMap.containsKey(sessionId)) {

					RunNumberCounterDtl runNumberBean = sessionDtlsMap.get(sessionId);
					Map<String, CounterDetailsBean> sesRunNoDtlsMap = runNumberBean.getRunNumberDtlsMap();

					if (sesRunNoDtlsMap.containsKey(runNumber)) {
						CounterDetailsBean counterDtlsBean = sesRunNoDtlsMap.get(runNumber);
						status = updateCounterValues(counterDtlsBean, transactionDtl);
						if (status.isEmpty()) {
							sessionInputCount = counterDtlsBean.getMasterCounter().getTransactionTotalCount();
						}
					} else {
						// Adding new entry for new runNumber
						CounterDetailsBean countDtlsBean = new CounterDetailsBean();
						status = updateCounterValues(countDtlsBean, transactionDtl);
						if (status.isEmpty()) {
							sesRunNoDtlsMap.put(transactionDtl.getRunNumber(), countDtlsBean);
							sessionInputCount = countDtlsBean.getMasterCounter().getTransactionTotalCount();
						}
					}
				} else {
					// Adding new entry for new session
					CounterDetailsBean countDtlsBean = new CounterDetailsBean();
					Map<String, CounterDetailsBean> sesRunNoDtlsMap = new HashMap<String, CounterDetailsBean>();
					sesRunNoDtlsMap.put(transactionDtl.getRunNumber(), countDtlsBean);

					RunNumberCounterDtl runNumberBean = new RunNumberCounterDtl();
					runNumberBean.setRunNumberDtlsMap(sesRunNoDtlsMap);

					status = updateCounterValues(countDtlsBean, transactionDtl);
					if (status.isEmpty()) {
						sessionDtlsMap.put(transactionDtl.getSessionId(), runNumberBean);
						sessionInputCount = countDtlsBean.getMasterCounter().getTransactionTotalCount();
					}
				}

				if (status.isEmpty()) {
					status = "Transaction status is updated successfully";
					// Updating session count status to database
					// if ((sessionInputCount % Constant.SESSION_UPDATE_LIMIT)
					// == 0) {
					updateSessionCountStatus(sessionId, runNumber);
					// }
				}
			}

		} catch (Exception ex) {
			status = "Unexpected error";
			LOGGER.error("Exception occured in updateTransactionStatus()" + ex);
			return status;
		}
		return status;
	}

	private String updateCounterValues(CounterDetailsBean countDtlsBean, TransactionDetail transactionDtl) {
		String status = "";
		int eventsCount = Integer.parseInt(transactionDtl.getEventsCount());
		boolean transIdExist = countDtlsBean.getTransactionsMap().containsKey(transactionDtl.getTransactionId());
		if (Constant.ACTION_ADD.equalsIgnoreCase(transactionDtl.getSource())) {
			if (transIdExist) {
				status = "Transaction Id is already exist";
			} else {
				countDtlsBean.addTransaction(transactionDtl.getTransactionId(), eventsCount);
			}
		} else {
			if (!transIdExist) {
				countDtlsBean.addTransaction(transactionDtl.getTransactionId(), eventsCount);
			}
			if (Constant.ACTION_SUCCESS.equalsIgnoreCase(transactionDtl.getSource())) {
				countDtlsBean.markTransactionSuccess(transactionDtl.getTransactionId());
			} else if (Constant.ACTION_FAILURE.equalsIgnoreCase(transactionDtl.getSource())) {
				boolean masterUpdate = false;
				if (transactionDtl.getErrorType().equalsIgnoreCase(Constant.TECHNICAL_ERROR)) {
					masterUpdate = countDtlsBean.markTransactionTechnicalFailure(transactionDtl.getTransactionId());
				} else if (transactionDtl.getErrorType().equalsIgnoreCase(Constant.BUSINESS_ERROR)) {
					masterUpdate = countDtlsBean.markTransactionBusinessFailure(transactionDtl.getTransactionId());
				}
				// To update the failed transaction details in DB
				transactionService.saveFailedTransaction(transactionDtl);
			}
		}
		return status;
	}

	private String updateSessionCountStatus(String sessionId, String runNumber) {

		String status = "";

		try {
			CounterDetailsBean countDtlsBean = null;
			if (sessionDtlsMap.containsKey(sessionId)) {

				RunNumberCounterDtl runNumberBean = sessionDtlsMap.get(sessionId);
				Map<String, CounterDetailsBean> sesRunNoDtlsMap = runNumberBean.getRunNumberDtlsMap();

				if (sesRunNoDtlsMap.containsKey(runNumber)) {
					countDtlsBean = sesRunNoDtlsMap.get(runNumber);
					// Updating session count status details to DB
					List<SessionDetails> sessionDetailsList = sessionManagerDao.getBySesIdRunNum(sessionId, runNumber);
					if (sessionDetailsList != null && sessionDetailsList.size() > 0) {
						SessionDetails sessionObject = sessionDetailsList.get(0);
						Date date = new Date();
						sessionObject.setLastUpdatedTime(dateFormat.format(date));
						sessionObject
								.setTransactionTotalCount(countDtlsBean.getMasterCounter().getTransactionTotalCount());
						sessionObject.setTransactionSuccessCount(
								countDtlsBean.getMasterCounter().getTransactionSuccessCount());
						sessionObject.setTransactionTechnicalFailureCount(
								countDtlsBean.getMasterCounter().getTransactionTechnicalFailureCount());
						sessionObject.setTransactionBusinessFailureCount(
								countDtlsBean.getMasterCounter().getTransactionBusinessFailureCount());
						sessionObject.setTransactionUnAddressedFailureCount(
								countDtlsBean.getMasterCounter().getTransactionBusinessFailureCount());
						sessionObject.setEventTotalCount(countDtlsBean.getMasterCounter().getEventTotalCount());
						sessionObject.setEventSuccessCount(countDtlsBean.getMasterCounter().getEventSuccessCount());
						sessionObject.setEventTechnicalFailureCount(
								countDtlsBean.getMasterCounter().getEventTechnicalFailureCount());
						sessionObject.setEventBusinessFailureCount(
								countDtlsBean.getMasterCounter().getEventBusinessFailureCount());
						sessionObject.setEventUnAddressedFailureCount(
								countDtlsBean.getMasterCounter().getEventBusinessFailureCount());
						sessionManagerDao.updateSession(sessionObject);
						status = "Session count status updated successfully";
					} else {
						status = "There is no record in DB with given sessionId/runNumber";
					}
				} else {
					status = "There is no active session with given runNumber";
				}
			} else {
				status = "There is no active session with given sessionId";
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected Error. Session count status is not updated", e);
			status = "Unexpected Error. Session count status is not updated";
		}
		return status;
	}

	// Update Channel Integration Files and Records Counts in DB
	public String updateFilesRecStatus(TransactionDetail transactionDtl) {
		String sessionId = transactionDtl.getSessionId();
		String runNumber = transactionDtl.getRunNumber();
		String fileName = transactionDtl.getFileName();
		String status = "";
		Date date = new Date();
		try {
			FileNameDetails fileNameDetails = new FileNameDetails();
			fileNameDetails.setSessionId(sessionId);
			fileNameDetails.setRunNumber(runNumber);
			fileNameDetails.setFileName(fileName);
			String dateStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(date);
			fileNameDetails.setReceivedTime(dateStr);
			fileNameDetails.setLastModifiedTime(dateStr);
			sessionManagerDao.saveFileNameDtl(fileNameDetails);
		} catch (Exception e) {
			LOGGER.error("Exception in updateFilesRecStatus in DB" + e);
		}

		status = updateOperatorStats(transactionDtl);
		/*
		 * try{ List<SessionDetails> sessionDetailsList =
		 * sessionManagerDao.getBySesIdRunNum(sessionId, runNumber); if
		 * (sessionDetailsList != null && sessionDetailsList.size() > 0) {
		 * SessionDetails sessionObject = sessionDetailsList.get(0);
		 * sessionObject.setReceivedFilesCount(transactionDtl.
		 * getReceivedFilesCount()+sessionObject.getReceivedFilesCount());
		 * sessionObject.setAcceptedFilesCount(transactionDtl.
		 * getAcceptedFilesCount()+sessionObject.getAcceptedFilesCount());
		 * sessionObject.setRejectedFilesCount(transactionDtl.
		 * getRejectedFilesCount()+sessionObject.getRejectedFilesCount());
		 * sessionObject.setTotalRecordsCount(transactionDtl.
		 * getTotalRecordsCount()+sessionObject.getTotalRecordsCount());
		 * sessionObject.setAcceptedRecordsCount(transactionDtl.
		 * getAcceptedRecordsCount()+sessionObject.getAcceptedRecordsCount());
		 * sessionObject.setRejectedRecordsCount(transactionDtl.
		 * getRejectedRecordsCount()+sessionObject.getRejectedRecordsCount());
		 * 
		 * sessionManagerDao.updateSession(sessionObject); status =
		 * "Session count status updated successfully"; } else { status =
		 * "There is no record in DB with given sessionId/runNumber"; }
		 * }catch(Exception e) { LOGGER.
		 * error("Unexpected Error. Channel Integration Files and Records count status is not updated"
		 * ); status =
		 * "Unexpected Error. Channel Integration Files and Records count status is not updated"
		 * ; }
		 */
		return status;
	}

	public String getFilesNameStatus(TransactionDetail transactionDtl) {

		return sessionManagerDao.getFileNameStatus(transactionDtl);

	}

	public String updateOperatorStats(TransactionDetail operatorStats) {
		String sessionId = operatorStats.getSessionId();
		String runNumber = operatorStats.getRunNumber();
		String operatorName = operatorStats.getOperatorName();
		String status = "";
		Date date = new Date();

		try {
			JsonArray operatorStatsList = sessionManagerDao.getBySesIdRunNumOperatorName(sessionId, runNumber,
					operatorName);
			if (operatorStatsList != null && operatorStatsList.size() > 0) {
				
				Gson gson = new GsonBuilder().create();
				JsonElement je = operatorStatsList.get(0);
				OperatorStats operatorStatsObject = gson.fromJson(je, OperatorStats.class);
				
				// File Input Channel
				operatorStatsObject.setReceivedFilesCount(
						operatorStats.getReceivedFilesCount() + operatorStatsObject.getReceivedFilesCount());
				operatorStatsObject.setAcceptedFilesCount(
						operatorStats.getAcceptedFilesCount() + operatorStatsObject.getAcceptedFilesCount());
				operatorStatsObject.setRejectedFilesCount(
						operatorStats.getRejectedFilesCount() + operatorStatsObject.getRejectedFilesCount());
				operatorStatsObject.setTotalRecordsCount(
						operatorStats.getTotalRecordsCount() + operatorStatsObject.getTotalRecordsCount());
				operatorStatsObject.setAcceptedRecordsCount(
						operatorStats.getAcceptedRecordsCount() + operatorStatsObject.getAcceptedRecordsCount());
				operatorStatsObject.setRejectedRecordsCount(
						operatorStats.getRejectedRecordsCount() + operatorStatsObject.getRejectedRecordsCount());
				// File Output Channel
				operatorStatsObject.setTotalFilesWritten(
						operatorStats.getTotalFilesWritten() + operatorStatsObject.getTotalFilesWritten());
				operatorStatsObject.setTotalRecordsReceived(
						operatorStats.getTotalRecordsReceived() + operatorStatsObject.getTotalRecordsReceived());
				operatorStatsObject.setTotalWrittenRecords(
						operatorStats.getTotalWrittenRecords() + operatorStatsObject.getTotalWrittenRecords());
				operatorStatsObject.setTotalUnwrittenRecords(
						operatorStats.getTotalUnwrittenRecords() + operatorStatsObject.getTotalUnwrittenRecords());
				// Rest Input Channel
				operatorStatsObject.setTotalRequestsCount(
						operatorStats.getTotalRequestsCount() + operatorStatsObject.getTotalRequestsCount());
				// Rest Output Channel
				operatorStatsObject.setTotalFailureRequestsCount(operatorStats.getTotalFailureRequestsCount()
						+ operatorStatsObject.getTotalFailureRequestsCount());
				operatorStatsObject.setTotalSuccessRequestsCount(operatorStats.getTotalSuccessRequestsCount()
						+ operatorStatsObject.getTotalSuccessRequestsCount());
				operatorStatsObject.setTotalFailureRecordsCount(operatorStats.getTotalFailureRecordsCount()
						+ operatorStatsObject.getTotalFailureRecordsCount());
				operatorStatsObject.setTotalSuccessRecordsCount(operatorStats.getTotalSuccessRecordsCount()
						+ operatorStatsObject.getTotalSuccessRecordsCount());
				String lastModifiedTime = null;
				if(operatorStats.getLastModifiedTime() != null){
					lastModifiedTime= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(operatorStats.getLastModifiedTime());
				}
				operatorStatsObject.setLastModifiedTime(lastModifiedTime);
				sessionManagerDao.updateOperatorStats(operatorStatsObject);
				status = "Operator Statastics counts updated successfully";
			} else {
				OperatorStats operatorStatsObject = new OperatorStats();
				OperatorStatsCompKey operatorStatsCompKey = new OperatorStatsCompKey();
				operatorStatsCompKey.setSessionId(sessionId);
				operatorStatsCompKey.setRunNumber(runNumber);
				operatorStatsCompKey.setOperatorName(operatorName);

				//operatorStatsObject.setOperatorStatsCompKey(operatorStatsCompKey);
				operatorStatsObject.setSessionId(sessionId);
				operatorStatsObject.setRunNumber(runNumber);
				operatorStatsObject.setOperatorName(operatorName);
				// File Input Channel
				operatorStatsObject.setReceivedFilesCount(operatorStats.getReceivedFilesCount());
				operatorStatsObject.setAcceptedFilesCount(operatorStats.getAcceptedFilesCount());
				operatorStatsObject.setRejectedFilesCount(operatorStats.getRejectedFilesCount());
				operatorStatsObject.setTotalRecordsCount(operatorStats.getTotalRecordsCount());
				operatorStatsObject.setAcceptedRecordsCount(operatorStats.getAcceptedRecordsCount());
				operatorStatsObject.setRejectedRecordsCount(operatorStats.getRejectedRecordsCount());
				// File Output Channel
				operatorStatsObject.setTotalFilesWritten(operatorStats.getTotalFilesWritten());
				operatorStatsObject.setTotalRecordsReceived(operatorStats.getTotalRecordsReceived());
				operatorStatsObject.setTotalWrittenRecords(operatorStats.getTotalWrittenRecords());
				operatorStatsObject.setTotalUnwrittenRecords(operatorStats.getTotalUnwrittenRecords());
				// Rest Input Channel
				operatorStatsObject.setTotalRequestsCount(operatorStats.getTotalRequestsCount());
				// Rest Output Channel
				operatorStatsObject.setTotalFailureRequestsCount(operatorStats.getTotalFailureRequestsCount());
				operatorStatsObject.setTotalSuccessRequestsCount(operatorStats.getTotalSuccessRequestsCount());
				operatorStatsObject.setTotalFailureRecordsCount(operatorStats.getTotalFailureRecordsCount());
				operatorStatsObject.setTotalSuccessRecordsCount(operatorStats.getTotalSuccessRecordsCount());
				String lastModifiedTime = null;
				String receivedTime = null;
				try {
					if (operatorStats.getLastModifiedTime() != null) {
						lastModifiedTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
								.format(operatorStats.getLastModifiedTime());
						receivedTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
								.format(operatorStats.getLastModifiedTime());
					}
				}catch(Exception ex){
					LOGGER.error("Unexpected Error. Date not updated properly.");
				}
				operatorStatsObject.setLastModifiedTime(lastModifiedTime);
				operatorStatsObject.setReceivedTime(receivedTime);
				sessionManagerDao.updateOperatorStats(operatorStatsObject);
				status = "Operator Statastics counts saved successfully";
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected Error. Channel Integration Files and Records count status is not updated");
			status = "Unexpected Error:" + e.getMessage()
					+ ". Channel Integration Files and Records count status is not updated";
		}
		return status;
	}

	public String getOperatorStats(TransactionDetail operatorDtl) {
		
		String sessionId = operatorDtl.getSessionId();
		String runNumber = operatorDtl.getRunNumber();
		String operatorName = operatorDtl.getOperatorName();
		String response = "";

		JsonArray list = sessionManagerDao.getBySesIdRunNumOperatorName(sessionId, runNumber, operatorName);
		Gson gson = new Gson();
		response = gson.toJson(list);
		return response;
	}
}
