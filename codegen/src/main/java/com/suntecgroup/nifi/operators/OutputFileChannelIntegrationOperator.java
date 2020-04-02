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

@Component
public class OutputFileChannelIntegrationOperator implements NifiProcessorInterface {
	private final Logger LOGGER = LoggerFactory.getLogger(OutputFileChannelIntegrationOperator.class);

	@Autowired
	private NifiCustomOutputFileCIProcessor nifiCustomOutputFileCIProcessorFactory;
	@Autowired
	private NifiFailureProcessor nifiFailureProcessor;
	@Autowired
	private NifiInvokeHttpProcessor nifiInvokeHttpProcessor;
	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;

	@Override
	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bpFlowRequest, SuntecNiFiModel target,
			Operators currentOperator) throws CGException {

		LOGGER.info("Create OutputFileChannelIntegration Request");

		SuntecOperatorModel som = new SuntecOperatorModel();

		TemplateProcessor nifiCustomOutputFileCIProcessor = null;
		TemplateProcessor customFailureProcessor = null;
		TemplateProcessor updateFailureHttpProcessor = null;

		String relationship[] = {};
		String openRelationship[] = { CGConstants.RESPONSE, CGConstants.RETRY_RELATIONSHIP,
				CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.FAILURE, CGConstants.ORIGINAL };

		try {
			BPCanvasUtils.updateCanvasXMargin();

			nifiCustomOutputFileCIProcessor = nifiCustomOutputFileCIProcessorFactory
					.generateNifiCustomOutputFileCIProcessor(bpFlowRequest, target, currentOperator,
							CGConstants.AUTO_TERMINATE_TRUE);
			som.getNifiProcessorList().add(nifiCustomOutputFileCIProcessor);

			customFailureProcessor = nifiFailureProcessor.generateFailureProcessorReq(bpFlowRequest, target,
					currentOperator, relationship, openRelationship, 11);
			som.getNifiProcessorList().add(customFailureProcessor);

			updateFailureHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bpFlowRequest, target,
					currentOperator, relationship, openRelationship, CGConstants.NIFI_UPDATE_FAILURE_IHTTP, 21);
			som.getNifiProcessorList().add(updateFailureHttpProcessor);

			TemplateConnection conn_cust_proc_to_fail = connectionsNifiOperator.generateNiFiConnection(
					nifiCustomOutputFileCIProcessor, customFailureProcessor, CGConstants.FAILURE,
					"OutputChannelIntegration to FAILURE", target);
			som.getNifiConnectionList().add(conn_cust_proc_to_fail);

			TemplateConnection conn_cust_proc_to_cust_proc = connectionsNifiOperator.generateNiFiConnection(
					nifiCustomOutputFileCIProcessor, nifiCustomOutputFileCIProcessor, CGConstants.REL_STOPPED,
					"OutputChannelIntegration to OutputChannelIntegration", target);
			som.getNifiConnectionList().add(conn_cust_proc_to_cust_proc);

			TemplateConnection conn_failure_to_ihttp = connectionsNifiOperator.generateNiFiConnection(
					customFailureProcessor, updateFailureHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP,
					"FAILURE TO UPDATE_TXN", target);
			som.getNifiConnectionList().add(conn_failure_to_ihttp);

			som.setMyFirstProcessor(nifiCustomOutputFileCIProcessor);
			som.setOutgoingRelationshipName(CGConstants.SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Exception while Creating OutputFileChannelIntegration Operator::", e);
			throw new CGException("Exception while Creating OutputFileChannelIntegration Operator::", e);
		}
		LOGGER.info("OutputFileChannelIntegration Operator Request generated");

		return som;

	}

}
