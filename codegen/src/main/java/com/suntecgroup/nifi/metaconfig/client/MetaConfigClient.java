package com.suntecgroup.nifi.metaconfig.client;

import java.util.Map;

import com.suntecgroup.nifi.exception.CGException;

public interface MetaConfigClient {
	public abstract Map<String, String> getDefaultPropertyValues() throws CGException;
	public abstract Map<String, Float> getMetaConfig(String operator, String processor) throws CGException;
}
