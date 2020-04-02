package com.suntecgroup.bp.designer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suntecgroup.xbmc.service.model.Apis;
import com.suntecgroup.xbmc.service.model.ContextParameters;
import com.suntecgroup.xbmc.service.model.InputBe;
import com.suntecgroup.xbmc.service.model.OutputBe;

/**
 * Model holds information on Business Service.
 *
 */
@JsonPropertyOrder({ "inputBe", "outputBe", "serviceName", "department", "module", "release", "contextParameters",
		"apis", "artifactId" })
public class BSService {

	private InputBe inputBe;
	private OutputBe outputBe;
	private String serviceName;
	private String department;
	private String module;
	private String release;
	private List<ContextParameters> contextParameters;
	private List<Apis> apis;
	private int artifactId;

	public InputBe getInputBe() {
		return inputBe;
	}

	public void setInputBe(InputBe inputBe) {
		this.inputBe = inputBe;
	}

	public OutputBe getOutputBe() {
		return outputBe;
	}

	public void setOutputBe(OutputBe outputBe) {
		this.outputBe = outputBe;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public List<ContextParameters> getContextParameters() {
		return contextParameters;
	}

	public void setContextParameters(List<ContextParameters> contextParameters) {
		this.contextParameters = contextParameters;
	}

	public List<Apis> getApis() {
		return apis;
	}

	public void setApis(List<Apis> apis) {
		this.apis = apis;
	}

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

}
