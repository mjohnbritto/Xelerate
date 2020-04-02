
package com.suntecgroup.nifi.frontend.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BPFlowRequest {

	@SerializedName("processName")
	@Expose
	private String processName;
	@SerializedName("processDescription")
	@Expose
	private String processDescription;
	@SerializedName("enableBoundedExecution")
	@Expose
	private boolean enableBoundedExecution;
	@SerializedName("isProfileable")
	@Expose
	private boolean isProfileable;
	@SerializedName("bukKey")
	@Expose
	private String bukKey;
	@SerializedName("enableRestChannel")
	@Expose
	private boolean enableRestChannel;
	@SerializedName("host")
	@Expose
	private String host;
	@SerializedName("port")
	@Expose
	private String port;
	@SerializedName("processVariable")
	@Expose
	private List<ProcessVariables> processVariable = new ArrayList<ProcessVariables>();
	@SerializedName("operators")
	@Expose
	private List<Operators> operators = new ArrayList<Operators>();
	@SerializedName("connections")
	@Expose
	private List<Connection> connections = new ArrayList<Connection>();

	private String departmentName;
	private String moduleName;
	private String releaseNo;
	private String processGroupID;
	private List<Connection> nifiConnections = new ArrayList<Connection>();

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessDescription() {
		return processDescription;
	}

	public void setProcessDescription(String processDescription) {
		this.processDescription = processDescription;
	}

	public boolean isEnableBoundedExecution() {
		return enableBoundedExecution;
	}

	public void setEnableBoundedExecution(boolean enableBoundedExecution) {
		this.enableBoundedExecution = enableBoundedExecution;
	}

	public boolean isIsProfileable() {
		return isProfileable;
	}

	public void setIsProfileable(boolean isProfileable) {
		this.isProfileable = isProfileable;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getReleaseNo() {
		return releaseNo;
	}

	public void setReleaseNo(String releaseNo) {
		this.releaseNo = releaseNo;
	}

	public String getBukKey() {
		return bukKey;
	}

	public void setBukKey(String bukKey) {
		this.bukKey = bukKey;
	}

	public List<ProcessVariables> getProcessVariable() {
		return processVariable;
	}

	public void setProcessVariable(List<ProcessVariables> processVariable) {
		this.processVariable = processVariable;
	}

	public List<Operators> getOperators() {
		return operators;
	}

	public void setOperators(List<Operators> operators) {
		this.operators = operators;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	public String getProcessGroupID() {
		return processGroupID;
	}

	public void setProcessGroupID(String processGroupID) {
		this.processGroupID = processGroupID;
	}

	public List<Connection> getNifiConnections() {
		return nifiConnections;
	}

	public void setNifiConnections(List<Connection> nifiConnections) {
		this.nifiConnections = nifiConnections;
	}

	public boolean isEnableRestChannel() {
		return enableRestChannel;
	}

	public void setEnableRestChannel(boolean enableRestChannel) {
		this.enableRestChannel = enableRestChannel;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("processName", processName)
				.append("processDescription", processDescription)
				.append("enableBoundedExecution", enableBoundedExecution).append("isProfileable", isProfileable)
				.append("departmentName", departmentName).append("moduleName", moduleName)
				.append("releaseNo", releaseNo).append("bukKey", bukKey).append("processVariable", processVariable)
				.append("operators", operators).append("connections", connections).toString();
	}

}
