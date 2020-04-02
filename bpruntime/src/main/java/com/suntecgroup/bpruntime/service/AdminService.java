package com.suntecgroup.bpruntime.service;

import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.BPDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.BPState;

public interface AdminService {

	public abstract String getBpList();

	public abstract String updateBpState(BPState bpState);

	public abstract String getBpDetails(String department, String module, String release, int artifact_id,
			String assetType, String assetName);

	public abstract String getProcessVariable(String department, String module, String release, int artifact_id,
			String assetType, String bpName);

	public abstract String getOperatorProperty(String department, String module, String release, int artifact_id,
			String assetType, String bpName, String operatorKey);

	public abstract ApiResponse<Object> updateBpDetails(BPDetails bpDetails);

	public ApiResponse<?> getOperatorData(String operatorName, String operatorType, String processGroupId);

	public abstract ApiResponse<?> getDeployedBPAsset(String department, String module, String release, int artifact_id,
			String assetType, String assetName, Object version);

}