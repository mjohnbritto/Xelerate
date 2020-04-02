
package com.suntecgroup.bp.designer.frontend.beans;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Mapping {

	@SerializedName("key")
	@Expose
	private String key;
	@SerializedName("outputBeParameter")
	@Expose
	private String outputBeParameter;
	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("updateSource")
	@Expose
	private String updateSource;
	@SerializedName("processVariable")
	@Expose
	private String processVariable;
	@SerializedName("processVariableArray")
	@Expose
	private List<ProcessVariableArray> processVariableArray;
	
	@SerializedName("inputpBeAttribute")
	@Expose
	private String inputpBeAttribute;
	
	@SerializedName("inputBeAttributeArray")
	@Expose
	private List<InputBeAttributeArray> inputBeAttributeArray;
	
	@SerializedName("value")
	@Expose
	private PVValue value;

	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOutputBeParameter() {
		return outputBeParameter;
	}

	public void setOutputBeParameter(String outputBeParameter) {
		this.outputBeParameter = outputBeParameter;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUpdateSource() {
		return updateSource;
	}

	public void setUpdateSource(String updateSource) {
		this.updateSource = updateSource;
	}

	public String getProcessVariable() {
		return processVariable;
	}

	public void setProcessVariable(String processVariable) {
		this.processVariable = processVariable;
	}

	public List<ProcessVariableArray> getProcessVariableArray() {
		return processVariableArray;
	}

	public void setProcessVariableArray(List<ProcessVariableArray> processVariableArray) {
		this.processVariableArray = processVariableArray;
	}

	public String getInputpBeAttribute() {
		return inputpBeAttribute;
	}

	public void setInputpBeAttribute(String inputpBeAttribute) {
		this.inputpBeAttribute = inputpBeAttribute;
	}

	public List<InputBeAttributeArray> getInputBeAttributeArray() {
		return inputBeAttributeArray;
	}

	public void setInputBeAttributeArray(List<InputBeAttributeArray> inputBeAttributeArray) {
		this.inputBeAttributeArray = inputBeAttributeArray;
	}

	public PVValue getValue() {
		return value;
	}

	public void setValue(PVValue value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("outputBeParameter", outputBeParameter).append("type", type)
				.append("updateSource", updateSource).append("processVariable", processVariable)
				.append("processVariableArray", processVariableArray).append("inputpBeAttribute", inputpBeAttribute)
				.append("inputBeAttributeArray", inputBeAttributeArray).append("value", value).toString();
	}
}
