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
public class FileChannelIntegrationOperator implements NifiProcessorInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileChannelIntegrationOperator.class);

	@Autowired
	private NifiGetFileProcessor nifiGetFileProcessorFactory;
	@Autowired
	private NifiPutFileProcessor nifiPutFileProcessorFactory;
	@Autowired
	private NifiCustomInputFileCIProcessor nifiCustomInputFileCIProcessorFactory;
	@Autowired
	private NifiFailureProcessor nifiFailureProcessor;
	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;

	@Override
	public SuntecOperatorModel generateNifiProcessors(BPFlowUI bpFlowRequest, SuntecNiFiModel target,
			Operators currentOperator) throws CGException {

		LOGGER.info("Create FileChannelIntegrationOperator Request");

		SuntecOperatorModel som = new SuntecOperatorModel();

		TemplateProcessor nifiGetFileProcessor = null;
		TemplateProcessor nifiPutFileProcessor = null;
		TemplateProcessor nifiCustomInputFileCIProcessor = null;
		TemplateProcessor customFailureProcessor = null;
		String relationship[] = {};
		String openRelationship[] = { CGConstants.RESPONSE, CGConstants.RETRY_RELATIONSHIP,
				CGConstants.NO_RETRY_RELATIONSHIP, CGConstants.FAILURE, CGConstants.ORIGINAL };
		try {
			BPCanvasUtils.updateCanvasXMargin();

			nifiGetFileProcessor = nifiGetFileProcessorFactory.generateNifiGetFileProcessor(bpFlowRequest, target,
					currentOperator, CGConstants.AUTO_TERMINATE_FALSE);
			som.getNifiProcessorList().add(nifiGetFileProcessor);

			nifiPutFileProcessor = nifiPutFileProcessorFactory.generateNifiPutFileProcessor(bpFlowRequest, target,
					currentOperator, CGConstants.AUTO_TERMINATE_TRUE);
			som.getNifiProcessorList().add(nifiPutFileProcessor);

			nifiCustomInputFileCIProcessor = nifiCustomInputFileCIProcessorFactory
					.generateNifiCustomInputFileCIProcessor(bpFlowRequest, target, currentOperator,
							CGConstants.AUTO_TERMINATE_FALSE);
			som.getNifiProcessorList().add(nifiCustomInputFileCIProcessor);

			customFailureProcessor = nifiFailureProcessor.generateFailureProcessorReq(bpFlowRequest, target,
					currentOperator, relationship, openRelationship, 12);
			som.getNifiProcessorList().add(customFailureProcessor);

			TemplateConnection connGetFileToPutFile = connectionsNifiOperator.generateNiFiConnection(
					nifiGetFileProcessor, nifiPutFileProcessor, CGConstants.BACKUP, "GetFile to PutFile",
					target);
			som.getNifiConnectionList().add(connGetFileToPutFile);

			TemplateConnection connGetFileToCustomFileProcessor = connectionsNifiOperator.generateNiFiConnection(
					nifiGetFileProcessor, nifiCustomInputFileCIProcessor, CGConstants.SUCCESS_LOWERCASE,
					"GetFile to CustomInputFileCIProcessor", target);
			som.getNifiConnectionList().add(connGetFileToCustomFileProcessor);

			TemplateConnection connCustomFileProcessorToFailureProcessor = connectionsNifiOperator
					.generateNiFiConnection(nifiCustomInputFileCIProcessor, customFailureProcessor, CGConstants.FAILURE,
							"CustomInputFileCIProcessor to Failure", target);
			som.getNifiConnectionList().add(connCustomFileProcessorToFailureProcessor);

			som.setMyFirstProcessor(nifiGetFileProcessor);
			som.setMyLastProcessor(nifiCustomInputFileCIProcessor);
			som.setOutgoingRelationshipName(CGConstants.SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Exception while Creating FileChannelIntegration Operator::", e);
			throw new CGException("Exception while Creating FileChannelIntegration Operator::", e);
		}
		LOGGER.info("FileChannelIntegration Operator Request generated");

		return som;

	}

}
