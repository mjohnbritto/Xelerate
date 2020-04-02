package com.suntecgroup.bpruntime.service;

import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.BPDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.BPState;

public interface ProcessInitiateService {
	public String getBpList(boolean tryOnce);

	public String updateBpState(BPState bpState);

	public String getBpDetails(String department, String module, String release, int artifact_id, String assetType,
			String assetName);

	public String getProcessVariable(String department, String module, String release, int artifact_id,
			String assetType, String assetName);

	public ApiResponse<Object> updateBpDetails(BPDetails bpDetails);

	public String getOperatorProperty(String department, String module, String release, int artifact_id,
			String assetType, String assetName, String operatorKey);

	public ApiResponse<?> getOperatorData(String operatorName, String operatorType, String processGroupId);
}
