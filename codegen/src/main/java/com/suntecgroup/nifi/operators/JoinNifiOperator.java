package com.suntecgroup.nifi.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.datamodel.SuntecOperatorModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.service.NifiProcessorInterface;
import com.suntecgroup.nifi.template.beans.TemplateFunnels;
import com.suntecgroup.nifi.util.BPCanvasUtils;
/**
 * JoinNifiOperator - A class having the logic for implementing JoinFunnel.
 */
@Component
public  class JoinNifiOperator implements NifiProcessorInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(JoinNifiOperator.class);
	@Autowired
	private FunnelNifiOperator joinNififunneloperator;
	/**
	 * generateNifiProessors - This method having logic to generate nifi Processors.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @param currentOperator
	 *            - holds the currentOperator data information of Operators type
	 * @return - returns Processor object list response
	 */
	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bPFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator) throws CGException {
		LOGGER.info(" Create Funnel Processor - Join Operator r Request ::");

		SuntecOperatorModel som = new SuntecOperatorModel();
		TemplateFunnels nifiFunnel = null;

		try {
			BPCanvasUtils.updateCanvasXMargin();
			nifiFunnel = joinNififunneloperator.generateFunnelRequest(bPFlowRequest, theTarget, currentOperator);
			som.getNifiFunnelList().add(nifiFunnel);
			som.setMyFirstConnection(nifiFunnel);
			som.setMyLastConnection(nifiFunnel);
			// som.setOutgoingRelationshipName("Success");

		} catch (Exception e) {
			LOGGER.error("Exception while Creating Funnel Processor - Join Operator ::", e);
			throw new CGException("Exception while Creating Funnel Processor - Join Operator ::", e);
		}

		return som;
	}

	
}
