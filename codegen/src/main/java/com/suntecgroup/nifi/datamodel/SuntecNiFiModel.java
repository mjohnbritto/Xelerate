/**
 * 
 */
package com.suntecgroup.nifi.datamodel;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.frontend.bean.ConfigureBusinessProcess;

/**
 * @author Venugopal-B
 *
 *         This models the data in the system.
 */

public class SuntecNiFiModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(SuntecNiFiModel.class);

	// A unique identified required by NiFi for execution of this flow.
	private String processGroupID;

	// Meta data of the flow. Not used by NiFi. Only for bookkeeping purposes.
	private SuntecMetaData metaData;

	// configuration data (copied from input JSON)
	private ConfigureBusinessProcess config;

	// All operators along with their internal implementations and relevant
	// connections
	private ArrayList<SuntecOperatorModel> sopList;
	private ArrayList<SuntecOperatorModel> sofList;

	// all suntec connection list - not internal implementation connections.
	private ArrayList<SuntecConnectionModel> sconList;

	public SuntecNiFiModel() {
		metaData = new SuntecMetaData();
		config = new ConfigureBusinessProcess();
		sopList = new ArrayList<SuntecOperatorModel>();
		sconList = new ArrayList<SuntecConnectionModel>();
	}

	// Getters and Setters.
	public String getProcessGroupID() {
		return processGroupID;
	}

	public void setProcessGroupID(String processGroupID) {
		this.processGroupID = processGroupID;
	}

	public ArrayList<SuntecOperatorModel> getSofList() {
		return sofList;
	}

	public void setSofList(ArrayList<SuntecOperatorModel> sofList) {
		this.sofList = sofList;
	}

	public SuntecMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(SuntecMetaData metaData) {
		this.metaData = metaData;
	}

	public ConfigureBusinessProcess getConfig() {
		return config;
	}

	public void setConfig(ConfigureBusinessProcess config) {
		this.config = config;
	}

	public ArrayList<SuntecOperatorModel> getSopList() {
		return sopList;
	}

	public void setSopList(ArrayList<SuntecOperatorModel> sopList) {
		this.sopList = sopList;
	}

	public ArrayList<SuntecConnectionModel> getSconList() {
		return sconList;
	}

	public void setSconList(ArrayList<SuntecConnectionModel> sconList) {
		this.sconList = sconList;
	}

	public SuntecOperatorModel getOperatorByKey(String sourceName) {
		LOGGER.info("getOperatorByKey: " + sourceName);
		for (SuntecOperatorModel model : sopList) {
			if (model.getKey().equals(sourceName)) {
				return model;
			}
		}
		return null;
	}

	public SuntecOperatorModel getOperatorByType(String name) {
		LOGGER.info("getOperatorByType: " + name);

		for (SuntecOperatorModel model : sopList) {
			if (model.getSuntec_operator().getType().equals(CGConstants.JOIN) && model.getName().equals(name)) {
				return model;
			}
		}
		return null;
	}

}
