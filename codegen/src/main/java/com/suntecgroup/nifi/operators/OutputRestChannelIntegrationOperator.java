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
public class OutputRestChannelIntegrationOperator implements NifiProcessorInterface {
	private final Logger LOGGER = LoggerFactory.getLogger(OutputRestChannelIntegrationOperator.class);

	@Autowired
	private NifiCustomInvokeHttpProcessor nifiCustomInvokeHttpProcessor;
	@Autowired
	private NifiCustomOutputRestCIProcessor nifiCustomOutputRestCIProcessorFactory;
	@Autowired
	private NifiInvokeHttpProcessor nifiInvokeHttpProcessor;
	@Autowired
	private NifiFailureProcessor nifiFailureProcessor;
	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;

	@Override
	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bpFlowRequest, SuntecNiFiModel target,
			Operators currentOperator) throws CGException {

		LOGGER.info("Create OutputRestChannelIntegrationOperator Request");

		TemplateProcessor customInvokeHttpProcessor = null;
		TemplateProcessor nifiCustomOutputRestCIProcessor = null;
		TemplateProcessor customFailureProcessor = null;
		TemplateProcessor updateFailureHttpProcessor = null;
		SuntecOperatorModel som = new SuntecOperatorModel();
		String relationship[] = {};
		String openRelationship[] = { CGConstants.RESPONSE, CGConstants.RETRY_RELATIONSHIP,
				CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.FAILURE, CGConstants.ORIGINAL };

		try {
			BPCanvasUtils.updateCanvasXMargin();

			nifiCustomOutputRestCIProcessor = nifiCustomOutputRestCIProcessorFactory
					.generateNifiCustomOutputRestCIProcessor(bpFlowRequest, target, currentOperator,
							CGConstants.AUTO_TERMINATE_TRUE);
			som.getNifiProcessorList().add(nifiCustomOutputRestCIProcessor);
			
			customInvokeHttpProcessor = nifiCustomInvokeHttpProcessor.generateInvokeHttpReq(bpFlowRequest, target,
					currentOperator, relationship, openRelationship, CGConstants.NIFI_INVOKE_HTTP, 1);
			som.getNifiProcessorList().add(customInvokeHttpProcessor);

			customFailureProcessor = nifiFailureProcessor.generateFailureProcessorReq(bpFlowRequest, target,
					currentOperator, relationship, openRelationship, 12);
			som.getNifiProcessorList().add(customFailureProcessor);

			updateFailureHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bpFlowRequest, target,
					currentOperator, relationship, openRelationship, CGConstants.NIFI_UPDATE_FAILURE_IHTTP, 22);
			som.getNifiProcessorList().add(updateFailureHttpProcessor);

			TemplateConnection conn_cust_proc_to_listenHTTP = connectionsNifiOperator.generateNiFiConnection(
					nifiCustomOutputRestCIProcessor, customInvokeHttpProcessor, CGConstants.SUCCESS,
					"ListenHTTP to OutputChannelIntegration", target);
			som.getNifiConnectionList().add(conn_cust_proc_to_listenHTTP);

			TemplateConnection conn_cust_proc_to_fail = connectionsNifiOperator.generateNiFiConnection(
					nifiCustomOutputRestCIProcessor, customFailureProcessor, CGConstants.FAILURE,
					"OutputChannelIntegration to FAILURE", target);
			som.getNifiConnectionList().add(conn_cust_proc_to_fail);

			TemplateConnection conn_listenHTTP_to_fail = connectionsNifiOperator.generateNiFiConnection(
					customInvokeHttpProcessor, customFailureProcessor, CGConstants.FAILURE,
					"OutputChannelIntegration to FAILURE", target);
			som.getNifiConnectionList().add(conn_listenHTTP_to_fail);

			TemplateConnection conn_failure_to_ihttp = connectionsNifiOperator.generateNiFiConnection(
					customFailureProcessor, updateFailureHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP,
					"FAILURE TO UPDATE_TXN", target);
			som.getNifiConnectionList().add(conn_failure_to_ihttp);

			som.setMyFirstProcessor(nifiCustomOutputRestCIProcessor);
			som.setMyLastProcessor(customInvokeHttpProcessor);
			som.setOutgoingRelationshipName(CGConstants.SUCCESS);
		} catch (Exception e) {
			LOGGER.error("Exception while Creating OutputRestChannelIntegrationOperator ::", e);
			throw new CGException("Exception while Creating OutputRestChannelIntegrationOperator ::", e);
		}
		LOGGER.info("OutputRestChannelIntegrationOperator Request generated");
		return som;
	}
}
