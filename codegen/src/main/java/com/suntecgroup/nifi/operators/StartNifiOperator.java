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
 * StartNifiOperator - A class for implementing from StartOperator.
 * 
 */
@Component
public class StartNifiOperator implements NifiProcessorInterface {

	private final Logger LOGGER = LoggerFactory.getLogger(StartNifiOperator.class);
	@Autowired
	private NifiInvokeHttpProcessor nifiInvokeHttpProcessor;
	@Autowired
	private NifiCustomStartProcessor nifiCustomStartProcessor;
	@Autowired
	private NifiCustomPreEventLoggerProcessor nifiCustomPreEventLoggerProcessor;
	@Autowired
	private NifiCustomPostEventLoggerProcessor nifiCustomPostEventLoggerProcessor;
	@Autowired
	private NifiFailureProcessor nifiFailureProcessor;
	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;

	/*
	 * Generate all processors, connections and relationships required for the
	 * START Suntec Operator and populate them in SuntecOperatorModel and return
	 * the created object.
	 */
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
	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator) throws CGException {

		SuntecOperatorModel som = new SuntecOperatorModel();

		LOGGER.info("Create Start Operator Request");
		TemplateProcessor invokeHttpProcessor = null;
		TemplateProcessor customStartProcessor = null;
		TemplateProcessor updateTxninvokeHttpProcessor = null;

		TemplateProcessor customFailureProcessor = null;
		TemplateProcessor customPreEventLoggerProcessor = null;
		TemplateProcessor customPostEventLoggerProcessor = null;
		String relationship[] = { "Response" };
		String openRelationship[] = { CGConstants.ORIGINAL, CGConstants.FAILURE, CGConstants.RETRY_RELATIONSHIP,
				CGConstants.NO_RETRY_RELATIONSHIP };

		try {
			BPCanvasUtils.updateCanvasXMargin();

			customStartProcessor = nifiCustomStartProcessor.generateCustomStartReq(bpFlowRequest, theTarget,
					currentOperator, CGConstants.AUTO_TERMINATE_FALSE, 2);
			som.getNifiProcessorList().add(customStartProcessor);

			invokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bpFlowRequest, theTarget,
					currentOperator, relationship, openRelationship, CGConstants.NIFI_INVOKE_HTTP, 4);
			som.getNifiProcessorList().add(invokeHttpProcessor);

			customPreEventLoggerProcessor = nifiCustomPreEventLoggerProcessor.generateCustomPreEventLoggerReq(
					bpFlowRequest, theTarget, currentOperator, CGConstants.AUTO_TERMINATE_FALSE, 3);
			som.getNifiProcessorList().add(customPreEventLoggerProcessor);

			customPostEventLoggerProcessor = nifiCustomPostEventLoggerProcessor.generateCustomPostEventLoggerReq(
					bpFlowRequest, theTarget, currentOperator, CGConstants.AUTO_TERMINATE_FALSE, 5);
			som.getNifiProcessorList().add(customPostEventLoggerProcessor);

			customFailureProcessor = nifiFailureProcessor.generateFailureProcessorReq(bpFlowRequest, theTarget,
					currentOperator, relationship, openRelationship, 14);
			som.getNifiProcessorList().add(customFailureProcessor);

			updateTxninvokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bpFlowRequest, theTarget,
					currentOperator, relationship, openRelationship, CGConstants.NIFI_UPDATE_START_IHTTP, 12);
			som.getNifiProcessorList().add(updateTxninvokeHttpProcessor);

			// custom start to invoke http
			TemplateConnection conn_cs_to_preevtlog = connectionsNifiOperator.generateNiFiConnection(
					customStartProcessor, customPreEventLoggerProcessor, CGConstants.SUCCESS,
					"CustomStart to StartCustomPreEvntLog", theTarget);
			som.getNifiConnectionList().add(conn_cs_to_preevtlog);

			TemplateConnection conn_cs_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
					customStartProcessor, updateTxninvokeHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP,
					"CustomStart to UpdateTxnInvokeHttp", theTarget);
			som.getNifiConnectionList().add(conn_cs_to_updateTxnihttp);

			// custom preevntlog to invoke http
			TemplateConnection conn_postevtlog_to_ihttp = connectionsNifiOperator.generateNiFiConnection(
					customPreEventLoggerProcessor, invokeHttpProcessor, CGConstants.SUCCESS,
					"StartCustomPreEvntLog to Start_InvokeHttp", theTarget);
			som.getNifiConnectionList().add(conn_postevtlog_to_ihttp);

			// ihttp to postevent log
			TemplateConnection conn_ihttp_to_postevtlog = connectionsNifiOperator.generateNiFiConnection(
					invokeHttpProcessor, customPostEventLoggerProcessor, CGConstants.ORIGINAL,
					"Start_InvokeHttp to StartCustomPostEvntLog", theTarget);
			som.getNifiConnectionList().add(conn_ihttp_to_postevtlog);

			TemplateConnection conn_fail_nr_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
					invokeHttpProcessor, customFailureProcessor, "No Retry", "NoRetryInvokeHttp to Failure", theTarget);
			som.getNifiConnectionList().add(conn_fail_nr_to_updateTxnihttp);

			TemplateConnection conn_fail_to_R_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
					invokeHttpProcessor, customFailureProcessor, "Retry", "RetryInvokeHttp to Failure", theTarget);
			som.getNifiConnectionList().add(conn_fail_to_R_updateTxnihttp);
			// custom start to failure
			TemplateConnection conn_cs_to_fail = connectionsNifiOperator.generateNiFiConnection(customStartProcessor,
					customFailureProcessor, "Failure", " CustomStart to Failure", theTarget);
			som.getNifiConnectionList().add(conn_cs_to_fail);

			// custom preeventlogger to failure
			TemplateConnection conn_preevtlog_to_fail = connectionsNifiOperator.generateNiFiConnection(
					customPreEventLoggerProcessor, customFailureProcessor, CGConstants.FAILURE,
					"StartCustomPreEvntLog to Failure", theTarget);
			som.getNifiConnectionList().add(conn_preevtlog_to_fail);

			// custom posteventlogger to failure
			TemplateConnection conn_postevtlog_to_fail = connectionsNifiOperator.generateNiFiConnection(
					customPostEventLoggerProcessor, customFailureProcessor, CGConstants.FAILURE,
					"StartCustomPostEvntLog to Failure", theTarget);
			som.getNifiConnectionList().add(conn_postevtlog_to_fail);

			// ihttp to failure
			TemplateConnection conn_ihttp_to_fail = connectionsNifiOperator.generateNiFiConnection(invokeHttpProcessor,
					customFailureProcessor, CGConstants.FAILURE, "InvokeHttp to Failure", theTarget);
			som.getNifiConnectionList().add(conn_ihttp_to_fail);

			// Failure to UpdateTxnInvokeHttp
			TemplateConnection conn_failure_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
					customFailureProcessor, updateTxninvokeHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP,
					"Failure to UpdateTxnInvokehttp", theTarget);
			som.getNifiConnectionList().add(conn_failure_to_updateTxnihttp);

			som.setMyFirstProcessor(customStartProcessor);

			som.setMyLastProcessor(customPostEventLoggerProcessor);
			som.setOutgoingRelationshipName(CGConstants.SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Exception while Creating START Operator::", e);
			throw new CGException("Exception while Creating START Operator::", e);
		}
		LOGGER.info("Start Operator Request generated");

		return som;

	}

}
