package com.suntecgroup.xbmc.service.model;

/**
 * OutputBe Model holds Output Business Entity details.
 *
 */
public class OutputBe {

	private int artifactId;
	private String beName;

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

	public String getBeName() {
		return beName;
	}

	public void setBeName(String beName) {
		this.beName = beName;
	}

	@Override
	public String toString() {
		return "ClassPojo [artifactId = " + artifactId + ", beName = " + beName + "]";
	}
}
