/**
 * 
 */
package com.suntecgroup.nifi.datamodel;

import java.util.ArrayList;
import java.util.List;

import com.suntecgroup.nifi.frontend.bean.Connection;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.template.beans.TemplateConnection;
import com.suntecgroup.nifi.template.beans.TemplateFunnels;
import com.suntecgroup.nifi.template.beans.TemplateProcessor;

/**
 * @author Venugopal-B
 *
 */
public class SuntecOperatorModel {
	
	private Operators suntec_operator;	
	private Connection suntec_connector;	
	private ArrayList<TemplateProcessor> nifiProcessorList;
	private ArrayList<TemplateConnection> nifiConnectionList;
	private TemplateProcessor myFirstProcessor;
	private TemplateProcessor myLastProcessor;
	private TemplateProcessor myLastAlternateProcessor;

	public TemplateProcessor getMyLastAlternateProcessor() {
		return myLastAlternateProcessor;
	}


	public void setMyLastAlternateProcessor(TemplateProcessor myLastAlternateProcessor) {
		this.myLastAlternateProcessor = myLastAlternateProcessor;
	}


	private List<TemplateFunnels> nifiFunnelList;
	private TemplateFunnels myLastConnection;
	private TemplateFunnels myFirstConnection;
	private String outgoingRelationshipName;
	private String outgoingAlternateRelationshipName;

	public String getOutgoingAlternateRelationshipName() {
		return outgoingAlternateRelationshipName;
	}


	public void setOutgoingAlternateRelationshipName(String outgoingAlternateRelationshipName) {
		this.outgoingAlternateRelationshipName = outgoingAlternateRelationshipName;
	}


	private String ProcessorName;
// This is the outgoing processor for any connections.
	public SuntecOperatorModel()
	{
		suntec_operator = null;
		suntec_connector = null;
		myLastProcessor = null;
		myFirstProcessor = null;
		outgoingRelationshipName = null;
		nifiProcessorList = new ArrayList<TemplateProcessor>();
		nifiConnectionList = new ArrayList<TemplateConnection>();
		nifiFunnelList=new ArrayList<TemplateFunnels>();
		myLastConnection=null;
		myFirstConnection=null;
	}
	

	public String getName()
	{
		return suntec_operator.getName();
	}

	public String getType()
	{
		return suntec_operator.getType();
	}
	public String getKey()
	{
		return suntec_operator.getKey();
	}
	public Operators getSuntec_operator() {
		return suntec_operator;
	}
	public void setSuntec_operator(Operators suntec_operator) {
		this.suntec_operator = suntec_operator;
	}
	
	public ArrayList<TemplateProcessor> getNifiProcessorList() {
		return nifiProcessorList;
	}
	public void setNifiProcessorList(ArrayList<TemplateProcessor> nifiProcessorList) {
		this.nifiProcessorList = nifiProcessorList;
	}
	public List<TemplateFunnels> getNifiFunnelList() {
		return nifiFunnelList;
	}
	public void setNifiFunnelList(List<TemplateFunnels> nifiFunnelList) {
		this.nifiFunnelList = nifiFunnelList;
	}
	
	public ArrayList<TemplateConnection> getNifiConnectionList() {
		return nifiConnectionList;
	}
	public void setNifiConnectionList(ArrayList<TemplateConnection> nifiConnectionList) {
		this.nifiConnectionList = nifiConnectionList;
	}
	
	public TemplateProcessor getMyFirstProcessor() {
		return myFirstProcessor;
	}

	public void setMyFirstProcessor(TemplateProcessor myFirstProcessor) {
		this.myFirstProcessor = myFirstProcessor;
	}

	public TemplateProcessor getMyLastProcessor() {
		return myLastProcessor;
	}

	public void setMyLastProcessor(TemplateProcessor myLastProcessor) {
		this.myLastProcessor = myLastProcessor;
	}
	public String getOutgoingRelationshipName() {
		return outgoingRelationshipName;
	}
	public void setOutgoingRelationshipName(String outgoingRelationshipName) {
		this.outgoingRelationshipName = outgoingRelationshipName;
	}


	public TemplateFunnels getMyLastConnection() {
		return myLastConnection;
	}


	public void setMyLastConnection(TemplateFunnels myLastConnection) {
		this.myLastConnection = myLastConnection;
	}


	public TemplateFunnels getMyFirstConnection() {
		return myFirstConnection;
	}


	public void setMyFirstConnection(TemplateFunnels myFirstConnection) {
		this.myFirstConnection = myFirstConnection;
	}


	public Connection getSuntec_connector() {
		return suntec_connector;
	}


	public void setSuntec_connector(Connection suntec_connector) {
		this.suntec_connector = suntec_connector;
	}


	public String getProcessorName() {
		return ProcessorName;
	}


	public void setProcessorName(String processorName) {
		ProcessorName = processorName;
	}


}
