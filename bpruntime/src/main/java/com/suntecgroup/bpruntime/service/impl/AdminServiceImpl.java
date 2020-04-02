package com.suntecgroup.bpruntime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.BPDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.BPState;
import com.suntecgroup.bpruntime.dao.SessionManagerDao;
import com.suntecgroup.bpruntime.service.AdminService;
import com.suntecgroup.bpruntime.service.ProcessInitiateService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	SessionManagerDao dao;
	@Autowired
	ProcessInitiateService processInitiateService;

	@Override
	public String getBpList() {
		String response = processInitiateService.getBpList(false);
		return response;
	}

	@Override
	public String updateBpState(BPState bpState) {
		String response = processInitiateService.updateBpState(bpState);
		return response;
	}

	@Override
	public String getBpDetails(String department, String module, String release, int artifact_id, String assetType,
			String assetName) {
		String response = processInitiateService.getBpDetails(department, module, release, artifact_id, assetType,
				assetName);
		return response;
	}

	@Override
	public String getProcessVariable(String department, String module, String release, int artifact_id,
			String assetType, String assetName) {
		String response = processInitiateService.getProcessVariable(department, module, release, artifact_id, assetType,
				assetName);
		return response;
	}

	@Override
	public String getOperatorProperty(String department, String module, String release, int artifact_id,
			String assetType, String bpName, String operatorKey) {
		String response = processInitiateService.getOperatorProperty(department, module, release, artifact_id,
				assetType, bpName, operatorKey);
		return response;
	}

	@Override
	public ApiResponse<Object> updateBpDetails(BPDetails bpDetails) {
		ApiResponse<Object> response = processInitiateService.updateBpDetails(bpDetails);
		return response;
	}

	@Override
	public ApiResponse<?> getOperatorData(String operatorName, String operatorType, String processGroupId) {
		return processInitiateService.getOperatorData(operatorName, operatorType, processGroupId);
	}

	/*
	 * @Override public ApiResponse<?> getBPAsset(String assetName) {
	 * ApiResponse<?> bpResponse = dao.getBPAsset( assetName); return
	 * bpResponse; }
	 */

	@Override
	public ApiResponse<?> getDeployedBPAsset(String department, String module, String release, int artifact_id,
			String assetType, String assetName, Object version) {
		ApiResponse<?> bpResponse = dao.getBPAsset(department, module, release, artifact_id, assetType, assetName,
				version);
		return bpResponse;
	}

}
