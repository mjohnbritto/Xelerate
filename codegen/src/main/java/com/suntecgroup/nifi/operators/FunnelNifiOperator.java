package com.suntecgroup.nifi.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suntecgroup.nifi.config.CGConfigurationProperty;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.template.beans.TemplateFunnels;
import com.suntecgroup.nifi.template.beans.TemplatePosition;
import com.suntecgroup.nifi.util.BPCanvasUtils;

/**
 * FunnelNifiOperator - A class having the logic for implementing funnel.
 */
@Component
public class FunnelNifiOperator {

	private static final Logger LOGGER = LoggerFactory.getLogger(FunnelNifiOperator.class);

	@Autowired
	private CGConfigurationProperty property;

	/**
	 * generateFunnelOpr - This method having logic to generate nifi funnels.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @param currentOperator
	 *            - holds the currentOperator data information of Operators type
	 * @return - returns funnel object list response
	 */
	public List<TemplateFunnels> generateFunnelOpr(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator) throws CGException {

		LOGGER.info(" Create Funnel Processor - Join Operator Request");
		List<TemplateFunnels> funnelsList = new ArrayList<TemplateFunnels>();
		try {
			TemplateFunnels nifiFunnelRequest = generateFunnelRequest(bpFlowRequest, theTarget, currentOperator);
			funnelsList.add(nifiFunnelRequest);
		} catch (Exception e) {
			LOGGER.error("Exception while Creating Funnel Processor - Join Operator::", e);
			throw new CGException("Exception while Creating Funnel Processor - Join Operator::", e);
		}
		LOGGER.info(" Created Funnel Processor - Join Operator Request");
		return funnelsList;
	}

	/**
	 * generateFunnelRequest - This method having logic to generate each nifi
	 * funnel.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @param operations
	 *            - holds the operations data information of Operators type
	 * @return - returns funnel object response
	 */
	public TemplateFunnels generateFunnelRequest(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators operations) throws CGException {
		TemplateFunnels nifiFunnelReq = new TemplateFunnels();
		TemplatePosition position = new TemplatePosition();
		String clientID = UUID.randomUUID().toString();
		if (null != property) {
			nifiFunnelReq.setId(clientID);
			nifiFunnelReq.setParentGroupId(theTarget.getProcessGroupID());
			position.setX(String.valueOf(BPCanvasUtils.getXByPosition(1)));
			position.setY(String.valueOf(BPCanvasUtils.getYByPosition(1)));
			nifiFunnelReq.setPosition(position);
		} else {
			LOGGER.info("Property Values are null for Funnel Processor - Join Operator ::");
			throw new CGException("Property Values are null for Funnel Processor - Join Operator ::");
		}
		return nifiFunnelReq;
	}

}
