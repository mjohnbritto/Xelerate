package com.suntecgroup.nifi.service;

import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.BPValidation;

public interface CGValidatorIntf {

	public BPValidation validateInputJson(final BPFlowUI bpFlowRequest);
}
