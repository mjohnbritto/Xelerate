package com.suntecgroup.bp.designer.frontend.beans;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
//import com.suntecgroup.nifi.frontend.beans.ConnectionsRequest;


/*
 * venu
 * This class captures the JSON information saved by the BP-Designer UI.
 * It should not have other responsibilities and should be read only.
 */
public class BPFlowUI {
	@SerializedName("configureBusinessProcess")
	@Expose
	private ConfigureBusinessProcess configureBusinessProcess;

	@SerializedName("operators")
	@Expose
	private List<Operators> operators;

	@SerializedName("connections")
	@Expose
	private List<Connection> connections;


	public ConfigureBusinessProcess getConfigureBusinessProcess() {
		return configureBusinessProcess;
	}

	public void setConfigureBusinessProcess(ConfigureBusinessProcess configureBusinessProcess) {
		this.configureBusinessProcess = configureBusinessProcess;
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

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("configureBusinessProcess", configureBusinessProcess)
				.append("operators", operators).append("connections", connections).toString();
	}
}
