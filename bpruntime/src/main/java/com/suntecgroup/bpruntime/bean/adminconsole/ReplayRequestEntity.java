package com.suntecgroup.bpruntime.bean.adminconsole;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReplayRequestEntity {
	@SerializedName("eventId")
	@Expose
	private String eventId;

	@SerializedName("clusterNodeId")
	@Expose
	private String clusterNodeId;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getClusterNodeId() {
		return clusterNodeId;
	}

	public void setClusterNodeId(String clusterNodeId) {
		this.clusterNodeId = clusterNodeId;
	}

}
