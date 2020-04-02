package com.suntecgroup.nifi.service;

import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.datamodel.SuntecOperatorModel;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Operators;

public interface NifiProcessorInterface {


	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bpFlowRequest, SuntecNiFiModel target, Operators theOperator) ;

}

