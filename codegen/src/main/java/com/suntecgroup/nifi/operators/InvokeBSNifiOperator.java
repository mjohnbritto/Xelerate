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
 * InvokeBSNifiOperator - A class having the logic for implementing
 * InvokeBSprocessors.
 */
@Component
public class InvokeBSNifiOperator implements NifiProcessorInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvokeBSNifiOperator.class);

	@Autowired
	private NifiCustomPreProcessorIBS nifiCustomPreProcessorIBS;

	@Autowired
	private NifiInvokeHttpProcessor nifiInvokeHttpProcessor;
	@Autowired
	private NifiCustomInvokeHttpProcessor nifiCustomInvokeHttpProcessor;
	@Autowired
	private NifiCustomPostProcessorInvokeBS nifiCustomPostProcessorIBS;
	@Autowired
	private NifiFailureProcessor nifiFailureProcessor;
	@Autowired
	private NifiCustomPreEventLoggerProcessor nifiCustomPreEventLoggerProcessor;
	@Autowired
	private NifiCustomPostEventLoggerProcessor nifiCustomPostEventLoggerProcessor;
	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;
	
	@Autowired
	private CustomKafkaConsumer customKafkaConsumer;

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
	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bPFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator) {

		LOGGER.info("Create InvokeBS Operator Request ::");

		SuntecOperatorModel som = new SuntecOperatorModel();

		TemplateProcessor invokeHttpProcessor = null;
		TemplateProcessor customInvokeHttpProcessor = null;
		
		TemplateProcessor customPreEventLoggerProcessor = null;
		TemplateProcessor customPostEventLoggerProcessor = null;
		TemplateProcessor prepostinvokeHttpProcessor = null;

		TemplateProcessor customPreProcessorIBS = null;
		TemplateProcessor customKafkaConsumerIBS = null;
		TemplateProcessor customPostProcessorIBS = null;
		TemplateProcessor customFailureProcessor = null;
		TemplateProcessor invokeHttp = null;

		TemplateProcessor updateTxnInvokeHttpProcessor = null;

		TemplateProcessor bf_CustomPostProcessorIBS = null;
		TemplateProcessor bf_CustomPreEventLoggerProcessor = null;
		TemplateProcessor bf_CustomPostEventLoggerProcessor = null;
		TemplateProcessor bf_PrepostinvokeHttpProcessor = null;

		String relationship[] = { CGConstants.ORIGINAL };
		String openRelationship[] = { CGConstants.RESPONSE, CGConstants.RETRY_RELATIONSHIP,	CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.FAILURE };
		
		try {
			BPCanvasUtils.updateCanvasXMargin();
			
			customPreProcessorIBS = nifiCustomPreProcessorIBS.generateCustomPreProcessorReq(bPFlowRequest, theTarget,currentOperator, CGConstants.AUTO_TERMINATE_FALSE);

			customPostProcessorIBS = nifiCustomPostProcessorIBS.generateCustomPostProcessorReq(bPFlowRequest, theTarget,currentOperator, CGConstants.AUTO_TERMINATE_FALSE, 3, false);

			customFailureProcessor = nifiFailureProcessor.generateFailureProcessorReq(bPFlowRequest, theTarget,currentOperator, relationship, openRelationship, 22);

			updateTxnInvokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bPFlowRequest, theTarget,currentOperator, relationship, openRelationship, CGConstants.NIFI_UPDATE_FAILURE_IHTTP,32);
			
			som.getNifiProcessorList().add(customPreProcessorIBS);
			som.getNifiProcessorList().add(customPostProcessorIBS);
			
			som.getNifiProcessorList().add(updateTxnInvokeHttpProcessor);
			som.getNifiProcessorList().add(customFailureProcessor);
			
			if (currentOperator.isEventLogFlag()) {

				// PreEventLog
				customPreEventLoggerProcessor = nifiCustomPreEventLoggerProcessor.generateCustomPreEventLoggerReq(bPFlowRequest, theTarget, currentOperator, CGConstants.AUTO_TERMINATE_FALSE,5);
				som.getNifiProcessorList().add(customPreEventLoggerProcessor);
				
				// InvokeHttp
				prepostinvokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bPFlowRequest, theTarget,currentOperator, relationship, openRelationship, CGConstants.NIFI_PP_INVOKE_HTTP,6);
				som.getNifiProcessorList().add(prepostinvokeHttpProcessor);
				// PostEventLog
				customPostEventLoggerProcessor = nifiCustomPostEventLoggerProcessor	.generateCustomPostEventLoggerReq(bPFlowRequest, theTarget, currentOperator,CGConstants.AUTO_TERMINATE_FALSE,7);
				som.getNifiProcessorList().add(customPostEventLoggerProcessor);

				TemplateConnection conn_postevtlog_to_ihttp = connectionsNifiOperator.generateNiFiConnection(customPreEventLoggerProcessor, prepostinvokeHttpProcessor, CGConstants.SUCCESS, "PreEvntLog to PP_InvokeHttp", theTarget);
				som.getNifiConnectionList().add(conn_postevtlog_to_ihttp);

				// ihttp to post eventlog
				TemplateConnection conn_pihttp_to_postevtlog = connectionsNifiOperator.generateNiFiConnection(prepostinvokeHttpProcessor, customPostEventLoggerProcessor, CGConstants.ORIGINAL, "PP_InvokeHttp to PostEvntLog", theTarget);
				som.getNifiConnectionList().add(conn_pihttp_to_postevtlog);

				// custom preeventlogger to failure
				TemplateConnection conn_preevtlog_to_fail = connectionsNifiOperator.generateNiFiConnection(customPreEventLoggerProcessor, customFailureProcessor, CGConstants.FAILURE, "PreEvntLog to Failure", theTarget);
				som.getNifiConnectionList().add(conn_preevtlog_to_fail);

				// custom posteventlogger to failure - LAST PROCESSOR
				TemplateConnection conn_postevtlog_to_fail = connectionsNifiOperator.generateNiFiConnection(customPostEventLoggerProcessor, customFailureProcessor, CGConstants.FAILURE, "PostEvntLog to Failure", theTarget);
				som.getNifiConnectionList().add(conn_postevtlog_to_fail);

				// ihttp to failure
				TemplateConnection conn_ihttp_to_fail = connectionsNifiOperator.generateNiFiConnection(prepostinvokeHttpProcessor, customFailureProcessor, CGConstants.FAILURE, "PP_InvokeHttp to Failure", theTarget);
				som.getNifiConnectionList().add(conn_ihttp_to_fail);

				TemplateConnection conn_fail_nr_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(prepostinvokeHttpProcessor, customFailureProcessor,"No Retry", "NoRetry_PP_InvokeHttp to Failure", theTarget);
				som.getNifiConnectionList().add(conn_fail_nr_to_updateTxnihttp);

				TemplateConnection conn_fail_to_R_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(prepostinvokeHttpProcessor, customFailureProcessor, "Retry", "Retry_PP_InvokeHttp to Failure", theTarget);
				som.getNifiConnectionList().add(conn_fail_to_R_updateTxnihttp);

			}

			if (currentOperator.getType().equals(CGConstants.INVOKE_BS)) {

				// CustomInvokehttpProcessorGeneration
				customInvokeHttpProcessor = nifiCustomInvokeHttpProcessor.generateInvokeHttpReq(bPFlowRequest, theTarget, currentOperator, relationship, openRelationship, CGConstants.NIFI_INVOKE_HTTP, 2);
				invokeHttp = customInvokeHttpProcessor;

				if (currentOperator.getBusinessSettings().isBusinessFailureFlowExist()) {
					// IBS Post processor for Business error flow(alternative path)
					bf_CustomPostProcessorIBS = nifiCustomPostProcessorIBS.generateCustomPostProcessorReq(bPFlowRequest, theTarget, currentOperator, CGConstants.AUTO_TERMINATE_FALSE, 13, true);
					som.getNifiProcessorList().add(bf_CustomPostProcessorIBS);

					// IBS UpdateAttribute processor for Business error flow(alternative path)
					TemplateConnection conn_ihttp_to_bf_postprocessor = connectionsNifiOperator.generateNiFiConnection( invokeHttp, bf_CustomPostProcessorIBS, CGConstants.BUSINESS_ERRORS, "ibs_bf_business_errors", theTarget);
					som.getNifiConnectionList().add(conn_ihttp_to_bf_postprocessor);

					TemplateConnection conn_bf_postprocessor_to_failure = connectionsNifiOperator.generateNiFiConnection(bf_CustomPostProcessorIBS, customFailureProcessor, CGConstants.FAILURE, "ibs_Post_failure", theTarget);
					som.getNifiConnectionList().add(conn_bf_postprocessor_to_failure);

					if (currentOperator.isEventLogFlag()&&currentOperator.getBusinessSettings().isBusinessFailureFlowExist()) {
						
						bf_CustomPreEventLoggerProcessor = nifiCustomPreEventLoggerProcessor.generateCustomPreEventLoggerReq(bPFlowRequest, theTarget, currentOperator,	CGConstants.AUTO_TERMINATE_FALSE, 24);
						som.getNifiProcessorList().add(bf_CustomPreEventLoggerProcessor);
						
						bf_PrepostinvokeHttpProcessor = nifiInvokeHttpProcessor.generateInvokeHttpReq(bPFlowRequest, theTarget, currentOperator, relationship, openRelationship, CGConstants.NIFI_PP_INVOKE_HTTP, 25);
						som.getNifiProcessorList().add(bf_PrepostinvokeHttpProcessor);
						
						bf_CustomPostEventLoggerProcessor = nifiCustomPostEventLoggerProcessor.generateCustomPostEventLoggerReq(bPFlowRequest, theTarget, currentOperator,CGConstants.AUTO_TERMINATE_FALSE, 26);
						som.getNifiProcessorList().add(bf_CustomPostEventLoggerProcessor);

						//BF CONNECTIONS - EVENT LOG EXISTS 
						
						TemplateConnection bfconn_postevtlog_to_ihttp = connectionsNifiOperator.generateNiFiConnection(bf_CustomPreEventLoggerProcessor, bf_PrepostinvokeHttpProcessor, CGConstants.SUCCESS, "BF_PreEvntLog to BF_PPInvokeHttp", theTarget);
						som.getNifiConnectionList().add(bfconn_postevtlog_to_ihttp);

						TemplateConnection bfconn_ppihttp_to_postevtlog = connectionsNifiOperator.generateNiFiConnection(bf_PrepostinvokeHttpProcessor, bf_CustomPostEventLoggerProcessor, CGConstants.ORIGINAL,"BF_PPInvokeHttp to BF_PostEvntLog", theTarget);
						som.getNifiConnectionList().add(bfconn_ppihttp_to_postevtlog);

						//PROCESSOR TO FAILURE CONNECTIONS
						TemplateConnection bfconn_preevtlog_to_fail = connectionsNifiOperator.generateNiFiConnection(bf_CustomPreEventLoggerProcessor, customFailureProcessor, CGConstants.FAILURE,"BF_PreEvntLog to Failure", theTarget);
						som.getNifiConnectionList().add(bfconn_preevtlog_to_fail);

						TemplateConnection bfconn_postevtlog_to_fail = connectionsNifiOperator.generateNiFiConnection(bf_CustomPostEventLoggerProcessor, customFailureProcessor, CGConstants.FAILURE, "BF_PostEvntLog to Failure", theTarget);
						som.getNifiConnectionList().add(bfconn_postevtlog_to_fail);

						TemplateConnection bfconn_ihttp_to_fail = connectionsNifiOperator.generateNiFiConnection(bf_PrepostinvokeHttpProcessor, customFailureProcessor, CGConstants.FAILURE, "BF_PPInvokeHttp to Failure", theTarget);
						som.getNifiConnectionList().add(bfconn_ihttp_to_fail);

						TemplateConnection bfconn_fail_nr_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(bf_PrepostinvokeHttpProcessor, customFailureProcessor, "No Retry", "BF_PP_NoRetry_PPInvokeHttp to Failure", theTarget);
						som.getNifiConnectionList().add(bfconn_fail_nr_to_updateTxnihttp);

						TemplateConnection bfconn_fail_to_R_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(bf_PrepostinvokeHttpProcessor, customFailureProcessor, "Retry",	"BF_Retry_PPInvokeHttp to Failure", theTarget);
						som.getNifiConnectionList().add(bfconn_fail_to_R_updateTxnihttp);
					}

				}

				// ihttp to failure
				TemplateConnection conn_ihttp_to_fail = connectionsNifiOperator.generateNiFiConnection(invokeHttp,	customFailureProcessor, CGConstants.FAILURE, "IBS_Service_Invokehttp to Failure", theTarget);
				som.getNifiConnectionList().add(conn_ihttp_to_fail);

				// duplicate of line 293
				/*TemplateConnection conn_bf_to_txihttp = connectionsNifiOperator.generateNiFiConnection(	customFailureProcessor, updateTxnInvokeHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP, "FAILURE TO UPDATE_TXN", theTarget);
				som.getNifiConnectionList().add(conn_bf_to_txihttp);*/

			} else {
				invokeHttpProcessor = nifiCustomInvokeHttpProcessor.generateInvokeHttpReq(bPFlowRequest, theTarget, currentOperator, relationship, openRelationship, CGConstants.NIFI_INVOKE_HTTP,2);
				invokeHttp = invokeHttpProcessor;

				// ihttp to failure
				TemplateConnection conn_ihttp_to_fail = connectionsNifiOperator.generateNiFiConnection(invokeHttp, customFailureProcessor, "Failure", "IBS_Invokehttp to Failure", theTarget);
				som.getNifiConnectionList().add(conn_ihttp_to_fail);
			}

			som.getNifiProcessorList().add(invokeHttp);

			// preprocessoribs to ihttp
			TemplateConnection conn_pre_to_ihttp = connectionsNifiOperator.generateNiFiConnection(customPreProcessorIBS, invokeHttp, CGConstants.SUCCESS, "pre2ihttp", theTarget);
			som.getNifiConnectionList().add(conn_pre_to_ihttp);

			//Added for Support streaming true
			if(currentOperator.getBusinessSettings().isSupportStreaming()){
				customKafkaConsumerIBS = customKafkaConsumer.generateCustomKafkaConsumerReq(bPFlowRequest, theTarget, currentOperator, CGConstants.AUTO_TERMINATE_FALSE,12);
				som.getNifiProcessorList().add(customKafkaConsumerIBS);
				
				// invoke http to kafka consumer
				TemplateConnection conn_invokeHttp_to_kafka = connectionsNifiOperator.generateNiFiConnection(invokeHttp, customKafkaConsumerIBS, CGConstants.RESPONSE, "InvokeHTTP to kafka consumer", theTarget);
				som.getNifiConnectionList().add(conn_invokeHttp_to_kafka);
				
				// kafka consumer to postprocessoribs
				TemplateConnection conn_kafka_to_postIBS = connectionsNifiOperator.generateNiFiConnection(customKafkaConsumerIBS, customPostProcessorIBS, CGConstants.SUCCESS, "Kafka consumer to post IBS", theTarget);
				som.getNifiConnectionList().add(conn_kafka_to_postIBS);
				
				// kafka consumer to failure
				TemplateConnection conn_kafka_to_fail = connectionsNifiOperator.generateNiFiConnection(customKafkaConsumerIBS, customFailureProcessor, CGConstants.FAILURE, "Kafka_Consumer to Failure", theTarget);
				som.getNifiConnectionList().add(conn_kafka_to_fail);
				
			} else {
				// ihttp to postprocessoribs
				TemplateConnection conn_ihttp_to_post = connectionsNifiOperator.generateNiFiConnection(invokeHttp,
						customPostProcessorIBS, "Response", "InvokeHttp to CustomPost", theTarget);
				som.getNifiConnectionList().add(conn_ihttp_to_post);
			}

			// preprocessor to failure
			TemplateConnection conn_pre_to_fail = connectionsNifiOperator.generateNiFiConnection(customPreProcessorIBS, customFailureProcessor, CGConstants.FAILURE, "PreIBS to Failure", theTarget);
			som.getNifiConnectionList().add(conn_pre_to_fail);

			// ihttp to failure
			TemplateConnection conn_ihttp_NoRetry_fail = connectionsNifiOperator.generateNiFiConnection(invokeHttp, customFailureProcessor, "No Retry", "IBS_InvokeHttp_No_Retry to Failure", theTarget);
			som.getNifiConnectionList().add(conn_ihttp_NoRetry_fail);

			TemplateConnection conn_ihttp_Retry_fail = connectionsNifiOperator.generateNiFiConnection(invokeHttp, customFailureProcessor, "Retry", "IBS_InvokeHttp_No_Retry to Failure", theTarget);
			som.getNifiConnectionList().add(conn_ihttp_Retry_fail);

			// postprocessoribs to failure
			TemplateConnection conn_post_to_fail = connectionsNifiOperator.generateNiFiConnection(customPostProcessorIBS, customFailureProcessor, CGConstants.FAILURE, "IBS_Post to Failure", theTarget);
			som.getNifiConnectionList().add(conn_post_to_fail);

			TemplateConnection conn_cs_to_updateTxnihttp = connectionsNifiOperator.generateNiFiConnection(customFailureProcessor, updateTxnInvokeHttpProcessor, CGConstants.UPDATE_STATUS_RELATIONSHIP, "CustomStart to UpdateTxnInvokeHttp", theTarget);
			som.getNifiConnectionList().add(conn_cs_to_updateTxnihttp);

			if (currentOperator.getBusinessSettings().isBusinessFailureFlowExist() && currentOperator.getType().equals(CGConstants.INVOKE_BS)) {
				som.setMyFirstProcessor(customPreProcessorIBS);
				if (currentOperator.isEventLogFlag()) {
					som.setMyLastProcessor(customPostEventLoggerProcessor);
					som.setMyLastAlternateProcessor(bf_CustomPostEventLoggerProcessor);
					som.setOutgoingAlternateRelationshipName(CGConstants.SUCCESS);
					som.setOutgoingRelationshipName(CGConstants.SUCCESS);
				} else {
					som.setMyLastProcessor(customPostProcessorIBS);
					som.setMyLastAlternateProcessor(bf_CustomPostProcessorIBS);
					som.setOutgoingAlternateRelationshipName(CGConstants.SUCCESS);
					som.setOutgoingRelationshipName(CGConstants.SUCCESS);
				}
			} else {
				som.setMyFirstProcessor(customPreProcessorIBS);
				if (currentOperator.isEventLogFlag()) {
					som.setMyLastProcessor(customPostEventLoggerProcessor);
					som.setOutgoingRelationshipName(CGConstants.SUCCESS);
				} else {
					som.setMyLastProcessor(customPostProcessorIBS);
					som.setOutgoingRelationshipName(CGConstants.SUCCESS);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception while Creating InvokeBS Operator::", e);
			throw new CGException("Exception while Creating InvokeBS Operator::", e);
		}
		LOGGER.info("InvokeBS Operator Request generated");

		return som;
	}
}
