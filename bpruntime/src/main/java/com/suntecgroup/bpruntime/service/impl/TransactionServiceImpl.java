package com.suntecgroup.bpruntime.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suntecgroup.bpruntime.bean.adminconsole.FailedTransaction;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplayData;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.TransactionDetail;
import com.suntecgroup.bpruntime.constant.Constant;
import com.suntecgroup.bpruntime.dao.SessionManagerDao;
import com.suntecgroup.bpruntime.dao.TransactionDAO;
import com.suntecgroup.bpruntime.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Autowired
	TransactionDAO transactionDAO;

	@Autowired
	SessionManagerDao sessionManagerDao;

	@Override
	public void saveFailedTransaction(TransactionDetail transactionDetail) {
		try {
			
			//converting error message to string and convert back to array while fetching
			StringBuilder sb =  new StringBuilder();
			for (String errorMessasge : transactionDetail.getErrorMessage()) {
				sb.append(errorMessasge + "   ");
			}
			
			FailedTransaction failedTransaction = new FailedTransaction();
			failedTransaction.setSessionId(transactionDetail.getSessionId());
			failedTransaction.setRunNumber(transactionDetail.getRunNumber());
			failedTransaction.setTransactionId(transactionDetail.getTransactionId());
			failedTransaction.setFlowfileUUID(transactionDetail.getFlowfileUUID());
			failedTransaction.setOperatorName(transactionDetail.getOperatorName());
			failedTransaction.setBeName(transactionDetail.getBeName());
			failedTransaction.setErrorType(transactionDetail.getErrorType());
			failedTransaction.setErrorMessage(transactionDetail.getErrorMessage());
			failedTransaction.setNormalisedErrorMessage(sb.toString());
			//failedTransaction.setBuk(transactionDetail.getBuk());
			failedTransaction.setEventsCount(transactionDetail.getEventsCount());
			failedTransaction.setAddressed(false);
			failedTransaction.setAction(Constant.ACTION_NONE);

			transactionDAO.saveFailedTransaction(failedTransaction);
		} catch (Exception exception) {
			LOGGER.error("Exception occurred :: " + exception.getMessage(), exception);
		}
	}

	@Override
	public Long getFailedTransactionCount(String sessionId, String runNumber, String errorType, boolean isAddressed,
			String action) {
		long transactionCount = 0;
		try {
			List<FailedTransaction> failedTransactions = transactionDAO.getFailedTransactionDetails(sessionId,
					runNumber, errorType, isAddressed, action);
			// Events count
			for (FailedTransaction failedTransaction : failedTransactions) {
				transactionCount += Integer.parseInt(failedTransaction.getEventsCount());
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred while fetching the addressed business failure details :: "
					+ exception.getMessage(), exception);
		}
		return transactionCount;
	}

	@Override
	public List<FailedTransaction> getFailedTransactionDetails(String sessionId, String runNumber, String errorType,
			boolean isAddressed) {
		List<FailedTransaction> failedTransactionDetails = transactionDAO.getFailedTransactionDetails(sessionId,
				runNumber, errorType, isAddressed, "");
		if (null != failedTransactionDetails && failedTransactionDetails.size() > 0) {
			return failedTransactionDetails;
		} else {
			return null;
		}
	}

	@Override
	public String addressBusinessFailure(List<TransactionDetail> transactionList) {

		String status = "";
		try {
			Map<String, ReplayData> replayMap = new HashMap<String, ReplayData>();
			for (TransactionDetail transaction : transactionList) {
				String sessionId = transaction.getSessionId().trim();
				String runNumber = transaction.getRunNumber().trim();
				String transactionId = transaction.getTransactionId().trim();
				String flowfileUUID = transaction.getFlowfileUUID().trim();
				String templateId = transaction.getTemplateId().trim();
				int eventCount = Integer.parseInt(transaction.getEventsCount().trim());
				String action = transaction.getAction().trim();

				// Update addressed failure status
				List<FailedTransaction> failedTransactionList = transactionDAO.getFailedTransaction(sessionId,
						runNumber, transactionId);

				if (failedTransactionList.size() > 0) {
					FailedTransaction failedTransaction = failedTransactionList.get(0);
					failedTransaction.setAddressed(true);
					failedTransaction.setAction(transaction.getAction());
					transactionDAO.saveFailedTransaction(failedTransaction);

					// Update addressed failure count

					List<SessionDetails> sessionDetailsList = sessionManagerDao.getBySesIdRunNum(sessionId, runNumber);
					SessionDetails sessionDtl = sessionDetailsList.get(0);
					if (Constant.ACTION_REPLAY.equalsIgnoreCase(action)) {
						sessionDtl.setEventUnAddressedFailureCount(
								sessionDtl.getEventUnAddressedFailureCount() - eventCount);
						sessionDtl
								.setEventAddressedFailureCount(sessionDtl.getEventAddressedFailureCount() + eventCount);

						sessionDtl.setTransactionUnAddressedFailureCount(
								sessionDtl.getTransactionUnAddressedFailureCount() - 1);
						sessionDtl.setTransactionAddressedFailureCount(
								sessionDtl.getTransactionAddressedFailureCount() + 1);

						String replayKey = sessionId + "|" + runNumber + "|" + templateId;
						if (replayMap.containsKey(replayKey)) {
							ReplayData replayData = replayMap.get(replayKey);
							replayData.getFlowfileUUIDs().add(flowfileUUID);
						} else {
							ReplayData replayData = new ReplayData();
							replayData.setSessionId(sessionId);
							replayData.setRunNumber(runNumber);
							replayData.setTemplateId(templateId);
							replayData.setErrorType(Constant.BUSINESS_ERROR);
							replayData.setFlowfileUUIDs(new ArrayList<String>());
							replayData.getFlowfileUUIDs().add(flowfileUUID);
							replayMap.put(replayKey, replayData);
						}
					} else if (Constant.ACTION_PERMANENT_ERROR.equalsIgnoreCase(action)
							|| Constant.ACTION_BACKOUT.equalsIgnoreCase(action)) {
						sessionDtl
								.setEventAddressedFailureCount(sessionDtl.getEventAddressedFailureCount() + eventCount);
						sessionDtl.setEventUnAddressedFailureCount(
								sessionDtl.getEventUnAddressedFailureCount() - eventCount);

						sessionDtl.setTransactionAddressedFailureCount(
								sessionDtl.getTransactionAddressedFailureCount() + 1);
						sessionDtl.setTransactionUnAddressedFailureCount(
								sessionDtl.getTransactionUnAddressedFailureCount() - 1);
					}
					sessionManagerDao.updateSession(sessionDtl);
				}
			}
			// Add data packet to replay queue
			if (replayMap.size() > 0) {
				DashboardServiceImpl.queuedDataForReplay.addAll(replayMap.values());
			}

			if (StringUtils.isBlank(status)) {
				status = "Transaction(s) addressed successfully";
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred in addressBusinessFailure() :: " + exception.getMessage(), exception);
			status = "Un Expected Error";
		}

		return status;
	}

}
