package com.suntecgroup.bpruntime.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import com.suntecgroup.bpruntime.bean.adminconsole.BPState;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplayRequestEntity;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionDetails;

public interface NifiService {

	public String updateBpState(BPState bpState);

	public String instantiateTemplate(String templateId);

	public String getSessionDetails(String processGroupId);

	public String getConnectionDetails(String processGroupId);

	public boolean stopProcessor(List<String> firstProcessorList);

	public void startStopFlow(String processGroupId, String status);

	public void updateVariableRegistry(SessionDetails session, Boolean isSessionStarted) throws Exception;

	// public String stopRunningSession(String processGroupId);

	public ResponseEntity<String> replayEvent(ReplayRequestEntity replayRequestEntity);

	public HttpEntity<String> generateProvenanceDataId(HttpEntity<String> requestEntity);

	public void deleteProvenanceDataId(String url);

	public ResponseEntity<String> getProvenanceData(String url);

	public ResponseEntity<String> getData(String url); // this method may exist
														// with some different
														// name

	public boolean isInstantiated(String processGroupId);

	public Object deployTemplate(String xmlContent);

	public Object getVariableRegistry(String processGroupId);

	public Object updateVariableRegistry(String processGroupId, HashMap<String, String> propertiesMap);
}
