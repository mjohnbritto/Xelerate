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
 * SmartConnector - A class for implementing from SmartConnectorProcessor.
 * 
 */
@Component
public class SmartConnectorOperator implements NifiProcessorInterface {

	private final Logger LOGGER = LoggerFactory.getLogger(SmartConnectorOperator.class);

	@Autowired
	private NifiSmartConnectorProcessor nifiSmartConnectorProcessor;
	@Autowired
	private NifiFailureProcessor nifiFailureProcessor;
	@Autowired
	private NifiInvokeHttpProcessor nifiInvokeHttpProcessor;
	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;

	/**
	 * generateNifiProessors - This method having logic to generate nifi
	 * Processors.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @param currentOperator
	 *            - holds the currentOperator data information of Operators type
	 * @return - returns Processor object list response
	 */
	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bpFlowRequest, SuntecNiFiModel target,
			Operators theOperator) throws CGException {
		LOGGER.info("Create Smartconnector Operator Request ::");

		SuntecOperatorModel som = new SuntecOperatorModel(); // fill this.

		TemplateProcessor smartConnectorProcessor = null;
		TemplateProcessor customFailureProcessor = null;
		TemplateProcessor updateTxnInvokeHttpProcessor = null;

		String relationship[] = { CGConstants.ORIGINAL };
		String openRelationship[] = { CGConstants.RESPONSE, CGConstants.RETRY_RELATIONSHIP,
				CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.FAILURE };

		try {
			BPCanvasUtils.updateCanvasXMargin();
			smartConnectorProcessor = nifiSmartConnectorProcessor.generateSmartConnectorProcessorReq(bpFlowRequest,
					target, theOperator, relationship, openRelationship, 1);
			som.getNifiProcessorList().add(smartConnectorProcessor);

			customFailureProcessor = nifiFailureProcessor.generateFailureProcessorReq(bpFlowRequest, target,
					theOperator, relationship, openRelationship, 11);
			som.getNifiProcessorList().add(customFailureProcessor);

			updateTxnInvokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bpFlowRequest, target,
					theOperator, relationship, openRelationship, CGConstants.NIFI_UPDATE_FAILURE_IHTTP, 22);
			som.getNifiProcessorList().add(updateTxnInvokeHttpProcessor);

			TemplateConnection conn_cs_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
					customFailureProcessor, updateTxnInvokeHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP,
					"Smart_Failure to UpdateTxnIhttp", target);
			som.getNifiConnectionList().add(conn_cs_to_updateTxnihttp);

			TemplateConnection conn_sc_to_failure = connectionsNifiOperator.generateNiFiConnection(
					smartConnectorProcessor, customFailureProcessor, CGConstants.FAILURE_LOWERCASE,
					"SmartConnector to Failure", target);
			som.getNifiConnectionList().add(conn_sc_to_failure);

			som.setMyFirstProcessor(smartConnectorProcessor);
			som.setMyLastProcessor(smartConnectorProcessor);
			som.setOutgoingRelationshipName(CGConstants.SUCCESS_LOWERCASE);

		} catch (Exception e) {
			LOGGER.error("Exception while Creating SmartConnector Operator::", e);
			throw new CGException("Exception while Creating SmartConnector Operator::", e);
		}
		LOGGER.info("SmartConnector Operator Request generated");

		return som;
	}

}
