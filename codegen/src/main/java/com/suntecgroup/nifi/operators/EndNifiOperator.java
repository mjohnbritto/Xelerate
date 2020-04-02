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
 * EndNifiOperator - A class for implementing from EndOperator.
 * 
 */
@Component
public class EndNifiOperator implements NifiProcessorInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(EndNifiOperator.class);
	@Autowired
	private NifiInvokeHttpProcessor nifiInvokeHttpProcessor;

	@Autowired
	private NifiCustomEndProcessor nifiCustomEndProcessor;
	@Autowired
	private NifiCustomPreEventLoggerProcessor nifiCustomPreEventLoggerProcessor;
	@Autowired
	private NifiFailureProcessor nifiFailureProcessor;

	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;

	/**
	 * generateNifiProcessors - This method having logic generate nifi processor
	 * for each processors.
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
		LOGGER.info(" Create END Operator Request ::");

		SuntecOperatorModel som = new SuntecOperatorModel();

		TemplateProcessor invokeHttpProcessor = null;
		TemplateProcessor customEndProcessor = null;
		TemplateProcessor customPreEventLoggerProcessor = null;
		TemplateProcessor customFailureProcessor = null;
		TemplateProcessor updateTxninvokeHttpProcessor = null;

		String relationship[] = { CGConstants.ORIGINAL, CGConstants.RESPONSE, CGConstants.RETRY_RELATIONSHIP,
				CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.FAILURE };
		String openRelationship[] = {};
		try {
			BPCanvasUtils.updateCanvasXMargin();
			// UpdateStatusInvokeHttpProcessor
			updateTxninvokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bPFlowRequest, theTarget,
					currentOperator, relationship, openRelationship, CGConstants.NIFI_UPDATE_END_IHTTP, 11);
			som.getNifiProcessorList().add(updateTxninvokeHttpProcessor);

			if (currentOperator.getBusinessSettings().isEventLogging()) {

				// End Processor
				customEndProcessor = nifiCustomEndProcessor.generateCustomEndReq(bPFlowRequest, theTarget,
						currentOperator, CGConstants.AUTO_TERMINATE_FALSE, 1);
				som.getNifiProcessorList().add(customEndProcessor);

				// CustomPreEventLogger Processor
				customPreEventLoggerProcessor = nifiCustomPreEventLoggerProcessor.generateCustomPreEventLoggerReq(
						bPFlowRequest, theTarget, currentOperator, CGConstants.AUTO_TERMINATE_FALSE, 2);
				som.getNifiProcessorList().add(customPreEventLoggerProcessor);

				// InvokeHttp Processor
				invokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bPFlowRequest, theTarget,
						currentOperator, relationship, openRelationship, CGConstants.NIFI_INVOKE_HTTP, 3);
				som.getNifiProcessorList().add(invokeHttpProcessor);

				// Failure Processor
				customFailureProcessor = nifiFailureProcessor.generateFailureProcessorReq(bPFlowRequest, theTarget,
						currentOperator, relationship, openRelationship, 12);
				som.getNifiProcessorList().add(customFailureProcessor);

				// End to customPreEventLoggerProcessor
				TemplateConnection conn_end_to_eventlog = connectionsNifiOperator.generateNiFiConnection(
						customEndProcessor, customPreEventLoggerProcessor, "LogEvent", "End to CustomPreEventLog",
						theTarget);
				som.getNifiConnectionList().add(conn_end_to_eventlog);

				// custom preevntlog to invoke http
				TemplateConnection conn_preevtlog_to_ihttp = connectionsNifiOperator.generateNiFiConnection(
						customPreEventLoggerProcessor, invokeHttpProcessor, CGConstants.SUCCESS,
						"CustomPreEventLog to InvokeHttp", theTarget);
				som.getNifiConnectionList().add(conn_preevtlog_to_ihttp);

				// custom preeventlogger to failure
				TemplateConnection conn_preevtlog_to_fail = connectionsNifiOperator.generateNiFiConnection(
						customPreEventLoggerProcessor, customFailureProcessor, CGConstants.FAILURE,
						"CustomPreEventLog to Failure", theTarget);
				som.getNifiConnectionList().add(conn_preevtlog_to_fail);

				// ihttp to failure
				TemplateConnection conn_ihttp_to_fail = connectionsNifiOperator.generateNiFiConnection(
						invokeHttpProcessor, customFailureProcessor, CGConstants.FAILURE, "InvokeHttp to Failure",
						theTarget);
				som.getNifiConnectionList().add(conn_ihttp_to_fail);

				// Failure to UpdateTxnInvokeHttp
				TemplateConnection conn_failure_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
						customFailureProcessor, updateTxninvokeHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP,
						"Failure to UpdateTxnInvokehttp", theTarget);
				som.getNifiConnectionList().add(conn_failure_to_updateTxnihttp);

				TemplateConnection conn_fail_nr_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
						invokeHttpProcessor, customFailureProcessor, CGConstants.NO_RETRY_RELATIONSHIP,
						"Invokehttp_No_Retry to Failure", theTarget);
				som.getNifiConnectionList().add(conn_fail_nr_to_updateTxnihttp);

				TemplateConnection conn_fail_to_R_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
						invokeHttpProcessor, customFailureProcessor, CGConstants.RETRY_RELATIONSHIP,
						"Invokehttp_Retry to Failure", theTarget);
				som.getNifiConnectionList().add(conn_fail_to_R_updateTxnihttp);

				som.setMyFirstProcessor(customEndProcessor);
				som.setMyLastProcessor(customEndProcessor);
				som.setOutgoingRelationshipName(CGConstants.SUCCESS);
			} else {
				customEndProcessor = nifiCustomEndProcessor.generateCustomEndReq(bPFlowRequest, theTarget,
						currentOperator, CGConstants.AUTO_TERMINATE_TRUE, 1);
				som.getNifiProcessorList().add(customEndProcessor);

				som.setMyFirstProcessor(customEndProcessor);
				som.setMyLastProcessor(customEndProcessor);
				som.setOutgoingRelationshipName(CGConstants.SUCCESS);
			}
			// End to UpdateTxnInvokehttp
			TemplateConnection conn_end_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
					customEndProcessor, updateTxninvokeHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP,
					"CustomEnd to UpdateTxnInvokehttp", theTarget);
			som.getNifiConnectionList().add(conn_end_to_updateTxnihttp);

		} catch (CGException e) {
			throw new CGException(e.getMessage(), e);
		}
		return som;
	}

}
