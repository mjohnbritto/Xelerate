package com.suntecgroup.bpruntime.bean.adminconsole;

import java.util.HashMap;
import java.util.Map;

public class RunNumberCounterDtl {
	
	Map<String, CounterDetailsBean> runNumberDtlsMap = new HashMap<String, CounterDetailsBean>();

	public Map<String, CounterDetailsBean> getRunNumberDtlsMap() {
		return runNumberDtlsMap;
	}

	public void setRunNumberDtlsMap(Map<String, CounterDetailsBean> runNumberDtlsMap) {
		this.runNumberDtlsMap = runNumberDtlsMap;
	}

	

}
