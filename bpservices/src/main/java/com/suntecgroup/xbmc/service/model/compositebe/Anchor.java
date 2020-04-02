package com.suntecgroup.xbmc.service.model.compositebe;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "aliasName", "beName", "isInherited", "relations" })
public class Anchor {

	@JsonProperty("aliasName")
	private String aliasName;

	@JsonProperty("beName")
	private String beName;

	@JsonProperty("isInherited")
	private boolean isInherited;

	@JsonProperty("relations")
	private List<Relations> relations;

	@JsonProperty("aliasName")
	public String getAliasName() {
		return aliasName;
	}

	@JsonProperty("aliasName")
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	@JsonProperty("beName")
	public String getBeName() {
		return beName;
	}

	@JsonProperty("beName")
	public void setBeName(String beName) {
		this.beName = beName;
	}

	@JsonProperty("isInherited")
	public boolean isInherited() {
		return isInherited;
	}

	@JsonProperty("isInherited")
	public void setInherited(boolean isInherited) {
		this.isInherited = isInherited;
	}

	@JsonProperty("relations")
	public List<Relations> getRelations() {
		return relations;
	}

	@JsonProperty("relations")
	public void setRelations(List<Relations> relations) {
		this.relations = relations;
	}

}
