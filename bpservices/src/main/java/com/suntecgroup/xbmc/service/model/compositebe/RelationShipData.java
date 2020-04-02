
package com.suntecgroup.xbmc.service.model.compositebe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "relationshipType", "relationshipside", "aSideArtifactName", "aSideRelationshipLabel",
		"aSideRelationshipBinding", "aSideRelationshipExposed", "bSideArtifactName", "bSideRelationshipLabel",
		"bSideRelationshipBinding", "bSideRelationshipExposed" })
public class RelationShipData {

	@JsonProperty("relationshipType")
	private String relationshipType;

	@JsonProperty("relationshipside")
	private String relationshipside;

	@JsonProperty("aSideArtifactName")
	private String aSideArtifactName;

	@JsonProperty("aSideRelationshipLabel")
	private String aSideRelationshipLabel;

	@JsonProperty("aSideRelationshipBinding")
	private String aSideRelationshipBinding;

	@JsonProperty("aSideRelationshipExposed")
	private String aSideRelationshipExposed;

	@JsonProperty("bSideArtifactName")
	private String bSideArtifactName;

	@JsonProperty("bSideRelationshipLabel")
	private String bSideRelationshipLabel;

	@JsonProperty("bSideRelationshipBinding")
	private String bSideRelationshipBinding;

	@JsonProperty("relationshipType")
	public String getRelationshipType() {
		return relationshipType;
	}

	@JsonProperty("relationshipType")
	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	@JsonProperty("relationshipside")
	public String getRelationshipside() {
		return relationshipside;
	}

	@JsonProperty("relationshipside")
	public void setRelationshipside(String relationshipside) {
		this.relationshipside = relationshipside;
	}

	@JsonProperty("aSideArtifactName")
	public String getaSideArtifactName() {
		return aSideArtifactName;
	}

	@JsonProperty("aSideArtifactName")
	public void setaSideArtifactName(String aSideArtifactName) {
		this.aSideArtifactName = aSideArtifactName;
	}

	@JsonProperty("aSideRelationshipLabel")
	public String getaSideRelationshipLabel() {
		return aSideRelationshipLabel;
	}

	@JsonProperty("aSideRelationshipLabel")
	public void setaSideRelationshipLabel(String aSideRelationshipLabel) {
		this.aSideRelationshipLabel = aSideRelationshipLabel;
	}

	@JsonProperty("aSideRelationshipBinding")
	public String getaSideRelationshipBinding() {
		return aSideRelationshipBinding;
	}

	@JsonProperty("aSideRelationshipBinding")
	public void setaSideRelationshipBinding(String aSideRelationshipBinding) {
		this.aSideRelationshipBinding = aSideRelationshipBinding;
	}

	@JsonProperty("aSideRelationshipExposed")
	public String getaSideRelationshipExposed() {
		return aSideRelationshipExposed;
	}

	@JsonProperty("aSideRelationshipExposed")
	public void setaSideRelationshipExposed(String aSideRelationshipExposed) {
		this.aSideRelationshipExposed = aSideRelationshipExposed;
	}

	@JsonProperty("bSideArtifactName")
	public String getbSideArtifactName() {
		return bSideArtifactName;
	}

	@JsonProperty("bSideArtifactName")
	public void setbSideArtifactName(String bSideArtifactName) {
		this.bSideArtifactName = bSideArtifactName;
	}

	@JsonProperty("bSideRelationshipLabel")
	public String getbSideRelationshipLabel() {
		return bSideRelationshipLabel;
	}

	@JsonProperty("bSideRelationshipLabel")
	public void setbSideRelationshipLabel(String bSideRelationshipLabel) {
		this.bSideRelationshipLabel = bSideRelationshipLabel;
	}

	@JsonProperty("bSideRelationshipBinding")
	public String getbSideRelationshipBinding() {
		return bSideRelationshipBinding;
	}

	@JsonProperty("bSideRelationshipBinding")
	public void setbSideRelationshipBinding(String bSideRelationshipBinding) {
		this.bSideRelationshipBinding = bSideRelationshipBinding;
	}

}
