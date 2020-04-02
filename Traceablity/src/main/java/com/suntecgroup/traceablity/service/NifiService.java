package com.suntecgroup.traceablity.service;

import org.springframework.boot.configurationprocessor.json.JSONObject;

public interface NifiService {
	public abstract String createLineageReqeust(String flowFileUUID, String clusterNodeID);

	public abstract String getLineageData(String getLineageURL, String clusterNodeID);

	public abstract void deteleLineageReqeust(String deleteLineageURL, String clusterNodeID);

	public abstract JSONObject provenanceEventData(String Eventno, String clusterNodeID);
}
