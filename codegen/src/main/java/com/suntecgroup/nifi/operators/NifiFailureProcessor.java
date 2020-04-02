package com.suntecgroup.nifi.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suntecgroup.nifi.config.CGConfigurationProperty;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.template.beans.TemplateBundle;
import com.suntecgroup.nifi.template.beans.TemplateConfig;
import com.suntecgroup.nifi.template.beans.TemplateDescriptors;
import com.suntecgroup.nifi.template.beans.TemplatePosition;
import com.suntecgroup.nifi.template.beans.TemplateProcessor;
import com.suntecgroup.nifi.template.beans.TemplateRelationships;
import com.suntecgroup.nifi.util.BPCanvasUtils;

/**
 * NifiInvokeHttpProcessor - A class having the logic for implementing
 * NifiInvokeHttpProcessor.
 */
@Component
public class NifiFailureProcessor {

	private final Logger LOGGER = LoggerFactory.getLogger(NifiFailureProcessor.class);

	@Autowired
	private CGConfigurationProperty property;

	/**
	 * generateFailureProcessorReq - This method having logic to generate nifi
	 * FailureProcessor.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @param currentOperator
	 *            - holds the currentOperator data information of Operators type
	 * @param relationship
	 *            - holds the relationship data of String type
	 * @param OpenRelationship
	 *            -
	 * @return - returns Processor object response
	 */
	public TemplateProcessor generateFailureProcessorReq(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget,
			Operators currentOperator, String[] relationship, String[] OpenRelationship, int canvasPosition) throws CGException {
		LOGGER.info("Create NifiInvokeHttp Request for ::" + currentOperator.getName() + "of the Operator :"
				+ currentOperator.getType());
		TemplateProcessor processor = new TemplateProcessor();
		String clientID = UUID.randomUUID().toString();
		TemplatePosition position = new TemplatePosition();
		TemplateConfig config = new TemplateConfig();
		List<TemplateRelationships> relationShipList = new ArrayList<TemplateRelationships>();
		TemplateRelationships relationships = new TemplateRelationships();
		relationships.setName("Failure");
		relationships.setAutoTerminate("true");
		relationShipList.add(relationships);

		if (CGConstants.START.equals(currentOperator.getType())
				|| (CGConstants.END.equals(currentOperator.getType()))) {
			TemplateRelationships relationships1 = new TemplateRelationships();
			relationships1.setName("UpdateStatus");
			relationships1.setAutoTerminate("false");
			relationShipList.add(relationships1);

		} else {
			TemplateRelationships relationships2 = new TemplateRelationships();
			relationships2.setName("UpdateStatus");
			relationships2.setAutoTerminate("true");
			relationShipList.add(relationships2);
		}

		config.setConcurrentlySchedulableTaskCount(CGConstants.SCHEDULABLE_TASK_COUNT);
		config.setExecutionNode(CGConstants.EXECUTION_NODE);
		config.setPenaltyDuration(CGConstants.PENALTY_DURATION);
		config.setYieldDuration(CGConstants.YIELD_DURATION);
		config.setBulletinLevel(CGConstants.BULLETIN_LEVEL);
		config.setSchedulingStrategy(CGConstants.SCHEDULING_STRATEGY);
		config.setSchedulingPeriod(CGConstants.SCHEDULING_PERIOD);
		config.setRunDurationMillis(CGConstants.RUN_DURATION);

		config.setLossTolerant(CGConstants.LOSS_TOLERANT);
		config.setComments(CGConstants.BLANK);
		TemplateDescriptors descriptors = new TemplateDescriptors();

		descriptors.setEntryDescList(null);
		config.setDescriptors(null);
		config.setProperties(null);

		TemplateBundle bundle = new TemplateBundle();
		bundle.setGroup(property.getCustomBundleGroup());
		bundle.setArtifact(property.getCustomBundleArtifact());
		bundle.setVersion(property.getCustomBundleVersion());

		if (CGConstants.START.equalsIgnoreCase(currentOperator.getType())) {
			canvasPosition = 14;
		} else if (CGConstants.INVOKE_BS_EXTERNAL.equalsIgnoreCase(currentOperator.getType())) {
			canvasPosition = 12;
		} else if (CGConstants.END.equalsIgnoreCase(currentOperator.getType())) {
			canvasPosition = 12;
		} else if (CGConstants.FILE_CHANNEL_INTEGRATION_INPUT.equalsIgnoreCase(currentOperator.getType())) {
			canvasPosition = 12;
		} else if(CGConstants.MERGE.equalsIgnoreCase(currentOperator.getType())) {
			canvasPosition = 12;
		} 

		position.setX(String.valueOf(BPCanvasUtils.getXByPosition(canvasPosition)));
		position.setY(String.valueOf(BPCanvasUtils.getYByPosition(canvasPosition)));
		processor.setId(clientID);
		processor.setParentGroupId(theTarget.getProcessGroupID());
		processor.setPosition(position);
		processor.setType(property.getCustomFailureComponentType());
		processor.setName(currentOperator.getName() + "-" + currentOperator.getType() + "-"
				+ property.getCustomFailureComponentAppName());
		processor.setBundle(bundle);
		processor.setConfig(config);
		processor.setRelationshipsList(relationShipList);
		processor.setState("STOPPED");
		processor.setStyle("");
		LOGGER.info("Failure Request is generated Successfully for ::" + currentOperator.getName() + "of "
				+ currentOperator.getType());
		return processor;
	}

}
