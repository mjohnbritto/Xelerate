/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */
package com.suntecgroup.bpconf.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 * This class is model for api request to accept the config details
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "key", "value" })
public class Configuration {

	@JsonProperty("key")
	private String key;
	@JsonProperty("value")
	private String value;

	public Configuration(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public Configuration() {
	}

	@JsonProperty("key")
	public String getKey() {
		return key;
	}

	@JsonProperty("key")
	public void setKey(String key) {
		this.key = key;
	}

	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}

}
