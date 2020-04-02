package com.suntecgroup.xbmc.service.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection  = "BEFlowData")
public class BPFlowData {

	private String class1;
	public BPFlowData(){
		
	}
	public BPFlowData(String class1) {
		super();
		this.class1 = class1;
	}

	@Override
	public String toString() {
		return "BPFlowData [class1=" + class1 + "]";
	}

	public String getClass1() {
		return this.class1;
	}

	public void setClass(String class1) {
		this.class1 = class1;

	}
//
//	private ModelData modelData;
//
//	public ModelData getModelData() {
//		return this.modelData;
//	}
//
//	public void setModelData(ModelData modelData) {
//		this.modelData = modelData;
//	}
//
//	private ArrayList<NodeDataArray> nodeDataArray;
//
//	public ArrayList<NodeDataArray> getNodeDataArray() {
//		return this.nodeDataArray;
//	}
//
//	public void setNodeDataArray(ArrayList<NodeDataArray> nodeDataArray) {
//		this.nodeDataArray = nodeDataArray;
//	}
//
//	private ArrayList<LinkDataArray> linkDataArray;
//
//	public ArrayList<LinkDataArray> getLinkDataArray() {
//		return this.linkDataArray;
//	}
//
//	public void setLinkDataArray(ArrayList<LinkDataArray> linkDataArray) {
//		this.linkDataArray = linkDataArray;
//	}
//}
}