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
import com.suntecgroup.nifi.datamodel.SuntecConnectionModel;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.datamodel.SuntecOperatorModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Connection;
import com.suntecgroup.nifi.template.beans.TemplateConnection;
import com.suntecgroup.nifi.template.beans.TemplateDestination;
import com.suntecgroup.nifi.template.beans.TemplateProcessor;
import com.suntecgroup.nifi.template.beans.TemplateSource;

/**
 * ConnectionsNifiOperator - A class for implementing nifi connections.
 * 
 */
@Component
public class ConnectionsNifiOperator {

	private final static Logger LOGGER = LoggerFactory.getLogger(ConnectionsNifiOperator.class);

	@Autowired
	private CGConfigurationProperty property;

	/**
	 * generateNiFiConnections - This method having logic generate connections
	 * from input.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the SuntecNiFiModel data information of
	 *            SuntecNiFiModel type
	 * @return - returns connection list response
	 */
	public void generateNiFiConnections(BPFlowUI bpFlowRequest, SuntecNiFiModel theTarget) throws CGException {
		LOGGER.info(" Create Connection Operator Request");

		try {
			LOGGER.info(" Creating connections :: ");

			for (Connection conn : bpFlowRequest.getConnections()) {
				SuntecOperatorModel src, dest, smart;

				src = theTarget.getOperatorByKey(conn.getUi_attributes().getSourceName());
				dest = theTarget.getOperatorByKey(conn.getUi_attributes().getDestinationName());

				TemplateConnection nificonnection;

				String relationName = src.getOutgoingRelationshipName();
				String connectionName = src.getName() + "2" + dest.getName();
				SuntecConnectionModel cm = new SuntecConnectionModel();
				SuntecConnectionModel cm1 = new SuntecConnectionModel();

				// If SMART connector
				if (null != conn.getUi_attributes().getType()
						&& conn.getUi_attributes().getType().equals(CGConstants.SMARTCONNECTOR)) {

					if (src.getType().equalsIgnoreCase(CGConstants.JOIN)) {
						
						TemplateProcessor last_of_src = src.getMyLastProcessor();
						TemplateProcessor first_of_dest = dest.getMyFirstProcessor();
						relationName = src.getOutgoingRelationshipName();
						smart = theTarget.getOperatorByKey(conn.getUi_attributes().getKey());

						connectionName = src.getName() + "2" + smart.getName();
						nificonnection = generateNiFiConnection(last_of_src, smart.getMyFirstProcessor(), relationName,
								connectionName, theTarget);
						cm.setNifiConnection((nificonnection));
						theTarget.getSconList().add(cm);

						connectionName = smart.getName() + "2" + dest.getName();
						relationName = smart.getOutgoingRelationshipName();
						nificonnection = generateNiFiConnection(smart.getMyLastProcessor(), first_of_dest, relationName,
								connectionName, theTarget);
						cm1.setNifiConnection(nificonnection);
						theTarget.getSconList().add(cm1);
					} else if (dest.getType().equalsIgnoreCase(CGConstants.JOIN)) {
						if (CGConstants.DECISION_MATRIX_EXCLUSIVE.equalsIgnoreCase(src.getType())
								|| CGConstants.DECISION_MATRIX_INCLUSIVE.equalsIgnoreCase(src.getType())) {
							relationName = conn.getUi_attributes().getDecisionName();
						} else {
							relationName = src.getOutgoingRelationshipName();
						}
						TemplateProcessor last_of_src = src.getMyLastProcessor();

						if (conn.getUi_attributes().isIsBusinessFailure()
								&& src.getType().equalsIgnoreCase(CGConstants.INVOKE_BS)) {
							last_of_src = src.getMyLastAlternateProcessor();
							relationName = src.getOutgoingAlternateRelationshipName();
						}

						TemplateProcessor first_of_dest = dest.getMyFirstProcessor();

						smart = theTarget.getOperatorByKey(conn.getUi_attributes().getKey());
						connectionName = src.getName() + "2" + smart.getName();
						nificonnection = generateNiFiConnection(last_of_src, smart.getMyFirstProcessor(), relationName,
								connectionName, theTarget);
						cm.setNifiConnection(nificonnection);
						theTarget.getSconList().add(cm);
						connectionName = smart.getName() + "2" + dest.getName();
						relationName = smart.getOutgoingRelationshipName();
						nificonnection = generateNiFiConnection(smart.getMyLastProcessor(), first_of_dest, relationName,
								connectionName, theTarget);
						cm1.setNifiConnection(nificonnection);
						theTarget.getSconList().add(cm1);
					} else {
						TemplateProcessor last_of_src = src.getMyLastProcessor();
						if (CGConstants.DECISION_MATRIX_EXCLUSIVE.equalsIgnoreCase(src.getType())
								|| CGConstants.DECISION_MATRIX_INCLUSIVE.equalsIgnoreCase(src.getType())) {
							relationName = conn.getUi_attributes().getDecisionName();
						} else {
							relationName = src.getOutgoingRelationshipName();
						}

						if (conn.getUi_attributes().isIsBusinessFailure()
								&& src.getType().equalsIgnoreCase(CGConstants.INVOKE_BS)) {
							last_of_src = src.getMyLastAlternateProcessor();
							relationName = src.getOutgoingAlternateRelationshipName();
						}

						smart = theTarget.getOperatorByKey(conn.getUi_attributes().getKey());
						String connectionName1 = src.getName() + "2-" + smart.getName();

						TemplateConnection nificonnection1 = generateNiFiConnection(last_of_src,
								smart.getMyFirstProcessor(), relationName, connectionName1, theTarget);
						cm.setNifiConnection(nificonnection1);
						theTarget.getSconList().add(cm);

						TemplateProcessor first_of_dest = dest.getMyFirstProcessor();
						connectionName = smart.getName() + "-2-" + dest.getName();
						relationName = smart.getOutgoingRelationshipName();
						TemplateConnection nificonnection2 = generateNiFiConnection(smart.getMyLastProcessor(),
								first_of_dest, relationName, connectionName, theTarget);
						cm1.setNifiConnection(nificonnection2);
						theTarget.getSconList().add(cm1);

					}
				} else {
					// Normal Connector

					if (conn.getUi_attributes().getType() != CGConstants.SMARTCONNECTOR
							&& src.getType().equalsIgnoreCase(CGConstants.JOIN)
							&& !dest.getType().equalsIgnoreCase(CGConstants.JOIN)) {
						TemplateProcessor last_of_src = src.getMyLastProcessor();

						TemplateProcessor first_of_dest = dest.getMyFirstProcessor();
						nificonnection = generateNiFiConnection(last_of_src, first_of_dest, relationName,
								connectionName, theTarget);
					} else if (conn.getUi_attributes().getType() != CGConstants.SMARTCONNECTOR
							&& src.getType().equalsIgnoreCase(CGConstants.JOIN)
							&& dest.getType().equalsIgnoreCase(CGConstants.JOIN)) {

						TemplateProcessor last_of_src = src.getMyLastProcessor();
						TemplateProcessor first_of_dest = dest.getMyFirstProcessor();

						nificonnection = generateNiFiConnection(last_of_src, first_of_dest, relationName,
								connectionName, theTarget);

					} else if (conn.getUi_attributes().getType() != CGConstants.SMARTCONNECTOR
							&& dest.getType().equalsIgnoreCase(CGConstants.JOIN)
							&& !src.getType().equalsIgnoreCase(CGConstants.JOIN)) {

						TemplateProcessor last_of_src = src.getMyLastProcessor();
						if (CGConstants.DECISION_MATRIX_EXCLUSIVE.equalsIgnoreCase(src.getType())
								|| CGConstants.DECISION_MATRIX_INCLUSIVE.equalsIgnoreCase(src.getType())) {
							relationName = conn.getUi_attributes().getDecisionName();
						} else {
							relationName = src.getOutgoingRelationshipName();
						}

						if (conn.getUi_attributes().isIsBusinessFailure()
								&& src.getType().equalsIgnoreCase(CGConstants.INVOKE_BS)) {
							last_of_src = src.getMyLastAlternateProcessor();
							relationName = src.getOutgoingAlternateRelationshipName();
						}

						TemplateProcessor first_of_dest = dest.getMyFirstProcessor();

						nificonnection = generateNiFiConnection(last_of_src, first_of_dest, relationName,
								connectionName, theTarget);
					} else {
						TemplateProcessor last_of_src;
						if (conn.getUi_attributes().isIsBusinessFailure()
								&& src.getType().equalsIgnoreCase(CGConstants.INVOKE_BS)) {
							last_of_src = src.getMyLastAlternateProcessor();
							relationName = src.getOutgoingAlternateRelationshipName();
						} else {
							last_of_src = src.getMyLastProcessor();
						}

						if (CGConstants.DECISION_MATRIX_EXCLUSIVE.equalsIgnoreCase(src.getType())
								|| CGConstants.DECISION_MATRIX_INCLUSIVE.equalsIgnoreCase(src.getType())) {
							relationName = conn.getUi_attributes().getDecisionName();
						} else {
							relationName = src.getOutgoingRelationshipName();
						}

						TemplateProcessor first_of_dest = dest.getMyFirstProcessor();
						nificonnection = generateNiFiConnection(last_of_src, first_of_dest, relationName,
								connectionName, theTarget);
					}
					cm.setSuntecConnection(conn);
					cm.setNifiConnection(nificonnection);
					theTarget.getSconList().add(cm);
				}
			} // For-Loop ends here
		} catch (

		Exception e) {
			LOGGER.error("Exception occured: " + e);
			throw new CGException("Exception while Creating Connection Operator::", e);
		}

		LOGGER.info(" Created Connection Operator Request");
	}
	/*
	 * private TemplateConnection generateNiFiConnection(TemplateFunnels from,
	 * TemplateFunnels to, String relationshipName, String connectionName,
	 * SuntecNiFiModel theTarget) {
	 * LOGGER.info("Creating Each Connection from funnel to funnel ::" +
	 * connectionName); TemplateConnection nifiConnection = new
	 * TemplateConnection();
	 * 
	 * String clientID = UUID.randomUUID().toString();
	 * nifiConnection.setId(clientID);
	 * nifiConnection.setParentGroupId(theTarget.getProcessGroupID());
	 * nifiConnection.setName(""); TemplateSource source = new TemplateSource();
	 * source.setId(from.getId());
	 * 
	 * TemplateDestination destination = new TemplateDestination();
	 * 
	 * source.setType(property.getConnectionProcessorType());
	 * 
	 * destination.setId(to.getId());
	 * 
	 * source.setGroupId(theTarget.getProcessGroupID());
	 * destination.setGroupId(theTarget.getProcessGroupID());
	 * destination.setType(property.getConnectionProcessorType());
	 * 
	 * List<String> selectedRelationShips = null; selectedRelationShips = new
	 * ArrayList<String>(); selectedRelationShips.add(relationshipName);
	 * 
	 * nifiConnection.setSource(source);
	 * nifiConnection.setDestination(destination);
	 * nifiConnection.setSelectedRelationships(selectedRelationShips);
	 * 
	 * nifiConnection.setBackPressureDataSizeThreshold(property.getSizeThreshold
	 * ());
	 * nifiConnection.setBackPressureObjectThreshold(property.getObjectThreshold
	 * ());
	 * nifiConnection.setFlowFileExpiration(property.getFlowFileExpiration());
	 * nifiConnection.setLabelIndex(property.getLabelIndex());
	 * nifiConnection.setzIndex(property.getzIndex());
	 * 
	 * return nifiConnection; }
	 */

	/**
	 * generateNiFiConnection - This method having logic for generating each
	 * connection .
	 * 
	 * @param from
	 *            - holds the from connection of TemplateProcessor type
	 * @param to
	 *            - holds the to connection data information of
	 *            TemplateProcessor type
	 * @param relationshipName
	 *            - holds the relationshipName data of string type
	 * @param connectionName
	 *            - holds the connectionName information of String type
	 * @param theTarget
	 *            - holds the theTarget data information of SuntecNiFiModel type
	 * @return - returns connection list response
	 */
	public TemplateConnection generateNiFiConnection(TemplateProcessor from, TemplateProcessor to,
			String relationshipName, String connectionName, SuntecNiFiModel theTarget) throws CGException {
		LOGGER.info("Creating Each Connection ::" + connectionName);
		TemplateConnection nifiConnection = new TemplateConnection();

		String clientID = UUID.randomUUID().toString();
		nifiConnection.setId(clientID);
		nifiConnection.setParentGroupId(theTarget.getProcessGroupID());
		nifiConnection.setName("");
		TemplateSource source = new TemplateSource();
		source.setId(from.getId());

		TemplateDestination destination = new TemplateDestination();

		source.setType(property.getConnectionProcessorType());

		destination.setId(to.getId());

		source.setGroupId(theTarget.getProcessGroupID());
		destination.setGroupId(theTarget.getProcessGroupID());
		destination.setType(property.getConnectionProcessorType());

		List<String> selectedRelationShips = null;
		selectedRelationShips = new ArrayList<String>();
		selectedRelationShips.add(relationshipName);

		nifiConnection.setSource(source);
		nifiConnection.setDestination(destination);
		nifiConnection.setSelectedRelationships(selectedRelationShips);

		nifiConnection.setBackPressureDataSizeThreshold(property.getSizeThreshold());
		nifiConnection.setBackPressureObjectThreshold(property.getObjectThreshold());
		nifiConnection.setFlowFileExpiration(property.getFlowFileExpiration());
		nifiConnection.setLabelIndex(property.getLabelIndex());
		nifiConnection.setzIndex(property.getzIndex());

		return nifiConnection;
	}
	/*
		*//**
			 * generateNiFiConnection - This method having logic for generating
			 * each connection from TemplateFunnels to TemplateProcessor.
			 * 
			 * @param from
			 *            - holds the from connection of TemplateFunnels type
			 * @param to
			 *            - holds the to connection data information of
			 *            TemplateProcessor type
			 * @param relationshipName
			 *            - holds the relationshipName data of string type
			 * @param connectionName
			 *            - holds the connectionName information of String type
			 * @param theTarget
			 *            - holds the theTarget data information of
			 *            SuntecNiFiModel type
			 * @return - returns connection object response
			 */
	/*
	 * private TemplateConnection generateNiFiConnection(TemplateFunnels from,
	 * TemplateProcessor to, String relationshipName, String connectionName,
	 * SuntecNiFiModel theTarget) { LOGGER.info("Creating Each Connection ::");
	 * TemplateConnection nifiConnection = new TemplateConnection();
	 * 
	 * String clientID = UUID.randomUUID().toString();
	 * nifiConnection.setId(clientID);
	 * nifiConnection.setParentGroupId(theTarget.getProcessGroupID());
	 * nifiConnection.setName("");
	 * 
	 * TemplateSource source = new TemplateSource(); source.setId(from.getId());
	 * 
	 * TemplateDestination destination = new TemplateDestination();
	 * 
	 * source.setType(property.getConnectionFunnelType());
	 * 
	 * destination.setId(to.getId());
	 * destination.setType(property.getConnectionProcessorType());
	 * 
	 * source.setGroupId(theTarget.getProcessGroupID());
	 * destination.setGroupId(theTarget.getProcessGroupID());
	 * 
	 * List<String> selectedRelationShips = null; selectedRelationShips = new
	 * ArrayList<String>(); selectedRelationShips.add(relationshipName);
	 * 
	 * nifiConnection.setSource(source);
	 * nifiConnection.setDestination(destination);
	 * nifiConnection.setSelectedRelationships(selectedRelationShips);
	 * 
	 * nifiConnection.setBackPressureDataSizeThreshold(property.getSizeThreshold
	 * ());
	 * nifiConnection.setBackPressureObjectThreshold(property.getObjectThreshold
	 * ());
	 * nifiConnection.setFlowFileExpiration(property.getFlowFileExpiration());
	 * nifiConnection.setLabelIndex(property.getLabelIndex());
	 * nifiConnection.setzIndex(property.getzIndex());
	 * 
	 * return nifiConnection; }
	 * 
	 *//**
		 * generateNiFiConnection - This method having logic for generating each
		 * connection from TemplateProcessor to TemplateFunnels.
		 * 
		 * @param from
		 *            - holds the from connection of TemplateProcessor type
		 * @param to
		 *            - holds the to connection data information of
		 *            TemplateFunnels type
		 * @param relationshipName
		 *            - holds the relationshipName data of string type
		 * @param connectionName
		 *            - holds the connectionName information of String type
		 * @param theTarget
		 *            - holds the theTarget data information of SuntecNiFiModel
		 *            type
		 * @return - returns connection object response
		 *//*
		 * private TemplateConnection generateNiFiConnection(TemplateProcessor
		 * from, TemplateFunnels to, String relationshipName, String
		 * connectionName, SuntecNiFiModel theTarget) {
		 * LOGGER.info("Creating Each Connection :: with processor -> funnel");
		 * TemplateConnection nifiConnection = new TemplateConnection();
		 * 
		 * String clientID = UUID.randomUUID().toString();
		 * nifiConnection.setId(clientID);
		 * nifiConnection.setParentGroupId(theTarget.getProcessGroupID());
		 * nifiConnection.setName("");
		 * 
		 * TemplateSource source = new TemplateSource();
		 * source.setId(from.getId());
		 * source.setType(property.getConnectionProcessorType());
		 * 
		 * TemplateDestination destination = new TemplateDestination();
		 * destination.setType(property.getConnectionFunnelType());
		 * destination.setId(to.getId());
		 * 
		 * source.setGroupId(theTarget.getProcessGroupID());
		 * destination.setGroupId(theTarget.getProcessGroupID());
		 * 
		 * List<String> selectedRelationShips = null; selectedRelationShips =
		 * new ArrayList<String>(); selectedRelationShips.add(relationshipName);
		 * 
		 * nifiConnection.setSource(source);
		 * nifiConnection.setDestination(destination);
		 * nifiConnection.setSelectedRelationships(selectedRelationShips);
		 * 
		 * nifiConnection.setBackPressureDataSizeThreshold(property.
		 * getSizeThreshold());
		 * nifiConnection.setBackPressureObjectThreshold(property.
		 * getObjectThreshold());
		 * nifiConnection.setFlowFileExpiration(property.getFlowFileExpiration()
		 * ); nifiConnection.setLabelIndex(property.getLabelIndex());
		 * nifiConnection.setzIndex(property.getzIndex());
		 * 
		 * return nifiConnection; }
		 */
}