
package com.suntecgroup.xbmc.service.model.compositebe;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "status", "data" })
public class XbmcBEResponse {

	@JsonProperty("status")
	private String status;

	@JsonProperty("data")
	private List<EffectiveBE> data;

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("data")
	public List<EffectiveBE> getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(List<EffectiveBE> data) {
		this.data = data;
	}

}
