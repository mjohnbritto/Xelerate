package com.suntecgroup.metaconfig.model;

import java.util.List;

public class KeyRequest {

	private String appname;
	private boolean allKeys;
	private List<String> keys;

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public boolean isAllKeys() {
		return allKeys;
	}

	public void setAllKeys(boolean allKeys) {
		this.allKeys = allKeys;
	}
}
