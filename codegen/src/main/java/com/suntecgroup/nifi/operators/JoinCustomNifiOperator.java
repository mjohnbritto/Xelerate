package com.suntecgroup.nifi.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.datamodel.SuntecOperatorModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.service.NifiProcessorInterface;
import com.suntecgroup.nifi.template.beans.TemplateProcessor;
import com.suntecgroup.nifi.util.BPCanvasUtils;

/**
 * JoinCustomNifiOperator - A class for implementing from EndOperator.
 * 
 */
@Component
public class JoinCustomNifiOperator implements NifiProcessorInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(JoinCustomNifiOperator.class);

	@Autowired
	private NifiCustomJoinProcessor nifiCustomJoinProcessor;

	/**
	 * generateNifiJoinProcessors - This method having logic generate nifi Join
	 * processor for each processors.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @param currentOperator
	 *            - holds the currentOperator data information of Operators type
	 * @return - returns som object list response
	 */
	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bPFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator) {
		LOGGER.info(" Create JoinCustomNifiOperator Request ::");

		SuntecOperatorModel som = new SuntecOperatorModel();

		TemplateProcessor customJoinProcessor = null;

		try {
			BPCanvasUtils.updateCanvasXMargin();
			
			// Join Processor generation
			customJoinProcessor = nifiCustomJoinProcessor.generateCustomJoinReq(bPFlowRequest, theTarget,
					currentOperator, CGConstants.AUTO_TERMINATE_FALSE);
			som.getNifiProcessorList().add(customJoinProcessor);
			
			som.setMyFirstProcessor(customJoinProcessor);
			som.setMyLastProcessor(customJoinProcessor);
			som.setOutgoingRelationshipName(CGConstants.SUCCESS);

		} catch (CGException e) {
			throw new CGException(e.getMessage(), e);
		}
		return som;
	}

}
