package com.suntecgroup.bpruntime.bean.nifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "version" })
public class ProcessGroupRevision {

	@JsonProperty("version")
	private Integer version;

	@JsonProperty("version")
	public Integer getVersion() {
		return version;
	}

	@JsonProperty("version")
	public void setVersion(Integer version) {
		this.version = version;
	}

}
