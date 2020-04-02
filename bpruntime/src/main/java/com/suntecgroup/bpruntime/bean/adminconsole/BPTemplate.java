package com.suntecgroup.bpruntime.bean.adminconsole;

import java.util.ArrayList;
import java.util.List;

public class BPTemplate {

	private String templateId;
	private String bpName;
	List<BPExecution> bpExecutionList = new ArrayList<BPExecution>();

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getBpName() {
		return bpName;
	}

	public void setBpName(String bpName) {
		this.bpName = bpName;
	}

	public List<BPExecution> getBpExecutionList() {
		return bpExecutionList;
	}

	public void setBpExecutionList(List<BPExecution> bpExecutionList) {
		this.bpExecutionList = bpExecutionList;
	}

}
