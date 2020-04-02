package com.suntecgroup.xbmc.service.model.compositebe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "anchor" })
public class CBEMapping {

	@JsonProperty("anchor")
	private Anchor anchor;

	@JsonProperty("anchor")
	public Anchor getAnchor() {
		return anchor;
	}

	@JsonProperty("anchor")
	public void setAnchor(Anchor anchor) {
		this.anchor = anchor;
	}

}
