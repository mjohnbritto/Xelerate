package com.suntecgroup.nifi.service;

import com.suntecgroup.nifi.frontend.bean.BPFlowResponseXml;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.BPValidation;

public interface CGServiceInterface {

	public abstract BPFlowResponseXml createNifiTemplate(String department, String module, String release,
			String assetType, String assetName);

	public abstract BPValidation validateInputJson(BPFlowUI bpFlowRequest);
}
