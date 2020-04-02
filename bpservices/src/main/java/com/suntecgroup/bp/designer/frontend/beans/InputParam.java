
package com.suntecgroup.bp.designer.frontend.beans;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class InputParam {

	@SerializedName("key")
	@Expose
	private String key;
	@SerializedName("contextVariable")
	@Expose
	private String contextVariable;
	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("selectedKey")
	@Expose
	private String selectedKey;
	@SerializedName("processVariable")
	@Expose
	private String processVariable;
	@SerializedName("value")
	@Expose
	private PVValue inputParamvalue;
	
	@SerializedName("processVariableArray")
	@Expose
	private List<ProcessVariableArray> processVariableArray;
	

	

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	

	public String getContextVariable() {
		return contextVariable;
	}

	public void setContextVariable(String contextVariable) {
		this.contextVariable = contextVariable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSelectedKey() {
		return selectedKey;
	}

	public void setSelectedKey(String selectedKey) {
		this.selectedKey = selectedKey;
	}

	
	public String getProcessVariable() {
		return processVariable;
	}

	public void setProcessVariable(String processVariable) {
		this.processVariable = processVariable;
	}

	public PVValue getInputParamvalue() {
		return inputParamvalue;
	}

	public void setInputParamvalue(PVValue inputParamvalue) {
		this.inputParamvalue = inputParamvalue;
	}

	public List<ProcessVariableArray> getProcessVariableArray() {
		return processVariableArray;
	}

	public void setProcessVariableArray(List<ProcessVariableArray> processVariableArray) {
		this.processVariableArray = processVariableArray;
	}

}
