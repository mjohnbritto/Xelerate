package com.suntecgroup.bp.designer.frontend.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BPsetUpProperties {
	@SerializedName("isRestChannelEnabled")
	@Expose
	private boolean isRestChannelEnabled;
	@SerializedName("host")
	@Expose
	private String host;
	@SerializedName("port")
	@Expose
	private String port;

	public boolean isRestChannelEnabled() {
		return isRestChannelEnabled;
	}

	public void setRestChannelEnabled(boolean isRestChannelEnabled) {
		this.isRestChannelEnabled = isRestChannelEnabled;
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
		return new ToStringBuilder(this).append("isRestChannelEnabled", isRestChannelEnabled).append("host", host)
				.append("port", port).toString();
	}

}
