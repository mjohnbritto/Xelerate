package com.suntecgroup.nifi.metaconfig.bean;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Operator {

	@SerializedName("name")
	@Expose
	String name;

	@SerializedName("processors")
	@Expose
	List<Processor> processors;

	public Operator() {
	}

	public Operator(String name, List<Processor> processors) {
		this.name = name;
		this.processors = processors;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Processor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<Processor> processors) {
		this.processors = processors;
	}

}
