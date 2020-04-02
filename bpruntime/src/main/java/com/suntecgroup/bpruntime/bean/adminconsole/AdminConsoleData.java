package com.suntecgroup.bpruntime.bean.adminconsole;

import java.util.ArrayList;
import java.util.List;

public class AdminConsoleData {
	List<BPTemplate> bpTemplateList = new ArrayList<BPTemplate>();

	public List<BPTemplate> getBpTemplateList() {
		return bpTemplateList;
	}

	public void setBpTemplateList(List<BPTemplate> bpTemplateList) {
		this.bpTemplateList = bpTemplateList;
	}
	
}
