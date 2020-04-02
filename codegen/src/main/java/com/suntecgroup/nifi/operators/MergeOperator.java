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
public class MergeOperator implements NifiProcessorInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(MergeOperator.class);

	@Autowired
	private NifiCustomPreMergeProcessor nifiCustomPreMergeProcessorFactory;
	@Autowired
	private NifiCustomInvokeHttpProcessor nifiCustomInvokeHttpProcessor;
	@Autowired
	private NifiCustomPostMergeProcessor nifiCustomPostMergeProcessorFactory;
	@Autowired
	private NifiFailureProcessor nifiFailureProcessor;
	@Autowired
	private NifiInvokeHttpProcessor nifiInvokeHttpProcessor;
	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;

	@Override
	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bpFlowRequest, SuntecNiFiModel target,
			Operators currentOperator) throws CGException {

		LOGGER.info("Create MergeOperator Request");

		SuntecOperatorModel som = new SuntecOperatorModel();

		TemplateProcessor nifiCustomPreMergeProcessor = null;
		TemplateProcessor customInvokeHttpProcessor = null;
		TemplateProcessor nifiCustomPostMergeProcessor = null;
		TemplateProcessor customFailureProcessor = null;
		TemplateProcessor updateTxnInvokeHttpProcessor = null;
		String relationship[] = {};
		String openRelationship[] = { CGConstants.RESPONSE, CGConstants.RETRY_RELATIONSHIP,
				CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.FAILURE, CGConstants.ORIGINAL };
		try {
			BPCanvasUtils.updateCanvasXMargin();

			nifiCustomPreMergeProcessor = nifiCustomPreMergeProcessorFactory.generateNifiCustomPreMergeProcessor(
					bpFlowRequest, target, currentOperator, CGConstants.AUTO_TERMINATE_FALSE);
			som.getNifiProcessorList().add(nifiCustomPreMergeProcessor);

			customInvokeHttpProcessor = nifiCustomInvokeHttpProcessor.generateInvokeHttpReq(bpFlowRequest, target,
					currentOperator, relationship, openRelationship, CGConstants.NIFI_INVOKE_HTTP, 2);
			som.getNifiProcessorList().add(customInvokeHttpProcessor);

			nifiCustomPostMergeProcessor = nifiCustomPostMergeProcessorFactory.generateNifiCustomPostMergeProcessor(
					bpFlowRequest, target, currentOperator, CGConstants.AUTO_TERMINATE_FALSE);
			som.getNifiProcessorList().add(nifiCustomPostMergeProcessor);

			customFailureProcessor = nifiFailureProcessor.generateFailureProcessorReq(bpFlowRequest, target,
					currentOperator, relationship, openRelationship, 12);
			som.getNifiProcessorList().add(customFailureProcessor);

			updateTxnInvokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bpFlowRequest, target,
					currentOperator, relationship, openRelationship, CGConstants.NIFI_UPDATE_FAILURE_IHTTP, 22);
			som.getNifiProcessorList().add(updateTxnInvokeHttpProcessor);

			TemplateConnection connPreMergeToInvokeHTTP = connectionsNifiOperator.generateNiFiConnection(
					nifiCustomPreMergeProcessor, customInvokeHttpProcessor, CGConstants.REL_MERGED,
					"PreMerge To InvokeHTTP", target);
			som.getNifiConnectionList().add(connPreMergeToInvokeHTTP);

			TemplateConnection connInvokeHTTPToPostMerge = connectionsNifiOperator.generateNiFiConnection(
					customInvokeHttpProcessor, nifiCustomPostMergeProcessor, CGConstants.RESPONSE,
					"InvokeHTTP to PostMerge", target);
			som.getNifiConnectionList().add(connInvokeHTTPToPostMerge);

			TemplateConnection connPreMergeToFailureProcessor = connectionsNifiOperator.generateNiFiConnection(
					nifiCustomPreMergeProcessor, customFailureProcessor, CGConstants.FAILURE_LOWERCASE,
					"PreMerge to Failure", target);
			som.getNifiConnectionList().add(connPreMergeToFailureProcessor);

			TemplateConnection connInvokeHTTPToFailureProcessor = connectionsNifiOperator.generateNiFiConnection(
					customInvokeHttpProcessor, customFailureProcessor, CGConstants.FAILURE, "InvokeHTTP to Failure",
					target);
			som.getNifiConnectionList().add(connInvokeHTTPToFailureProcessor);

			TemplateConnection connPostMergeToFailureProcessor = connectionsNifiOperator.generateNiFiConnection(
					nifiCustomPostMergeProcessor, customFailureProcessor, CGConstants.FAILURE, "PostMerge to Failure",
					target);
			som.getNifiConnectionList().add(connPostMergeToFailureProcessor);

			TemplateConnection connFailureToUpdateTxnihttp = connectionsNifiOperator.generateNiFiConnection(
					customFailureProcessor, updateTxnInvokeHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP,
					"Failure to UpdateTxnIhttp", target);
			som.getNifiConnectionList().add(connFailureToUpdateTxnihttp);

			som.setMyFirstProcessor(nifiCustomPreMergeProcessor);
			som.setMyLastProcessor(nifiCustomPostMergeProcessor);
			som.setOutgoingRelationshipName(CGConstants.SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Exception while Creating Merge Operator::", e);
			throw new CGException("Exception while Creating Merge Operator::", e);
		}
		LOGGER.info("Merge Operator Request generated");

		return som;

	}

}
