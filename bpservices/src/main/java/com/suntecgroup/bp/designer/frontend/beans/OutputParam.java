
package com.suntecgroup.bp.designer.frontend.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class OutputParam {

	@SerializedName("key")
	@Expose
	private String key;
	@SerializedName("contextVariable")
	@Expose
	private String mappedContextVariable;
	@SerializedName("type")
	@Expose
	private String type;

	@SerializedName("value")
	@Expose
	private PVValue outputParamvalue;
	@SerializedName("processVariable")
	@Expose
	private String processVariable;
	@SerializedName("selectedKey")
	@Expose
	private String selectedKey;

	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMappedContextVariable() {
		return mappedContextVariable;
	}

	public void setMappedContextVariable(String mappedContextVariable) {
		this.mappedContextVariable = mappedContextVariable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PVValue getOutputParamvalue() {
		return outputParamvalue;
	}

	public void setOutputParamvalue(PVValue outputParamvalue) {
		this.outputParamvalue = outputParamvalue;
	}

	public String getProcessVariable() {
		return processVariable;
	}

	public void setProcessVariable(String processVariable) {
		this.processVariable = processVariable;
	}

	public String getSelectedKey() {
		return selectedKey;
	}

	public void setSelectedKey(String selectedKey) {
		this.selectedKey = selectedKey;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("contextVariable", mappedContextVariable).append("type", type)
				.append("value", outputParamvalue).append("processVariable", processVariable)
				.append("selectedKey", selectedKey).toString();
	}

}
