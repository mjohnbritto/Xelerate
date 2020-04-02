package com.suntecgroup.nifi.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.datamodel.SuntecOperatorModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.operators.JoinNifiOperator;
/**
 * NifiFunnelFactory - A class having the logic for implementing NifiFunnelFactory.
 */
@Component
public class NifiFunnelFactory {

	@Autowired
	private JoinNifiOperator joinNifiOperator;

	public void generateNiFiFunnels(BPFlowUI bPFlowRequest, SuntecNiFiModel theTarget) throws CGException {
		if (null != bPFlowRequest) {
			SuntecOperatorModel som;
	
			for (Operators op : bPFlowRequest.getOperators()) {
				if (op.getType().equals(CGConstants.JOIN)) {
					som = joinNifiOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
				} else {
					som = null;
				}
				som.setSuntec_operator(op);
				// store the original operator reference.
				theTarget.getSofList().add(som);
			}
		}
	}

}
