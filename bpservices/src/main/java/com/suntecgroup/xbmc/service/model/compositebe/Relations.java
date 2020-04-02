
package com.suntecgroup.xbmc.service.model.compositebe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "relationName", "target" })
public class Relations {

	@JsonProperty("relationName")
	private String relationName;

	@JsonProperty("target")
	private Target target;

	@JsonProperty("relationName")
	public String getRelationName() {
		return relationName;
	}

	@JsonProperty("relationName")
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	@JsonProperty("target")
	public Target getTarget() {
		return target;
	}

	@JsonProperty("target")
	public void setTarget(Target target) {
		this.target = target;
	}

}
