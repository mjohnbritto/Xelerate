package com.suntecgroup.bpruntime.dao;

import java.util.List;

import org.json.JSONArray;

import com.google.gson.JsonArray;
import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.FileNameDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.OperatorStats;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.TransactionDetail;

public interface SessionManagerDao {

	public void saveSession(SessionDetails sessionDetails);

	public void saveFileNameDtl(FileNameDetails fileNameDetails);

	public List<SessionDetails> getSessionDetails(String templateId);

	public void updateSession(SessionDetails sessionDetails);

	public List<SessionDetails> getSessionDetailsByPGI(String processGroupId);

	public List<SessionDetails> getSessionDetailsByStatus(String status);

	public List<SessionDetails> getBySesIdRunNum(String sessionId, String runNumber);

	public ApiResponse<?> getBPAsset(String department, String module, String release, int artifact_id,
			String assetType, String assetName, Object version);

	public List<SessionDetails> getActiveSessionDetails(String templateId);

	public SessionDetails getLatestSessionRunForThisSession(String sessionId);

	public String getFileNameStatus(TransactionDetail transactionDetail);
	
	public JsonArray getBySesIdRunNumOperatorName(String sessionId, String runNumber,String operatorName);
	
	public void updateOperatorStats(OperatorStats operatorstats) throws Exception;
	
}
