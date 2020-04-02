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
import com.suntecgroup.nifi.template.beans.TemplateConnection;
import com.suntecgroup.nifi.template.beans.TemplateProcessor;
import com.suntecgroup.nifi.util.BPCanvasUtils;

/**
 * DecisionMatrixInclusiveOperator - A class for implementing from
 * DecisionMatrixInclusiveOperator.
 * 
 */
@Component
public class DecisionMatrixInclusiveOperator implements NifiProcessorInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionMatrixInclusiveOperator.class);

	@Autowired
	private NifiFailureProcessor nifiFailureProcessor;
	@Autowired
	private NifiInvokeHttpProcessor nifiInvokeHttpProcessor;
	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;

	@Autowired
	private NifiDecisionMatrixInclusiveProcessor nifiDecisionMatrixInclusiveProcessor;

	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bPFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator) throws CGException {

		LOGGER.info("Create DecisionMatrixInclusive Operator Request ::");

		SuntecOperatorModel som = new SuntecOperatorModel();

		TemplateProcessor customFailureProcessor = null;
		TemplateProcessor decisionMatrixInclusiveProcessor = null;
		TemplateProcessor updateTxnInvokeHttpProcessor = null;
		String relationship[] = {};
		String openRelationship[] = { CGConstants.RESPONSE, CGConstants.RETRY_RELATIONSHIP,
				CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.FAILURE, CGConstants.ORIGINAL };
		try {
			BPCanvasUtils.updateCanvasXMargin();
			decisionMatrixInclusiveProcessor = nifiDecisionMatrixInclusiveProcessor
					.generateDecisionMatrixInclusiveProcessorReq(bPFlowRequest, theTarget, currentOperator,1);
			som.getNifiProcessorList().add(decisionMatrixInclusiveProcessor);

			customFailureProcessor = nifiFailureProcessor.generateFailureProcessorReq(bPFlowRequest, theTarget,
					currentOperator, relationship, openRelationship, 11);
			som.getNifiProcessorList().add(customFailureProcessor);

			updateTxnInvokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bPFlowRequest, theTarget,
					currentOperator, relationship, openRelationship, CGConstants.NIFI_UPDATE_FAILURE_IHTTP, 21);
			som.getNifiProcessorList().add(updateTxnInvokeHttpProcessor);

			TemplateConnection conn_DM_In_to_failure = connectionsNifiOperator.generateNiFiConnection(
					decisionMatrixInclusiveProcessor, customFailureProcessor, CGConstants.FAILURE,
					"DecisionMatrix_Inclusive to Failure", theTarget);
			som.getNifiConnectionList().add(conn_DM_In_to_failure);

			TemplateConnection conn_cs_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
					customFailureProcessor, updateTxnInvokeHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP,
					"Failure to UpdateTxnIhttp", theTarget);
			som.getNifiConnectionList().add(conn_cs_to_updateTxnihttp);

			som.setMyFirstProcessor(decisionMatrixInclusiveProcessor);
			som.setMyLastProcessor(decisionMatrixInclusiveProcessor);

		} catch (Exception e) {
			LOGGER.error("Exception while Creating DecisionMatrixInclusive Operator::", e);
			throw new CGException("Exception while Creating DecisionMatrixInclusive Operator::", e);
		}
		LOGGER.info(" DecisionMatrixInclusive Operator Request generated");

		return som;
	}

}
