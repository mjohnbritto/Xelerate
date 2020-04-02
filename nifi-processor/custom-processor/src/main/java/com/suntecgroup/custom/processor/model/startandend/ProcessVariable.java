/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.startandend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suntecgroup.custom.processor.kafka.KafkaProcessorUtils;

/*
 * This is a model/pojo class to the request/response to data
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "name", "description", "type", "value", "flags" })
public class ProcessVariable {

	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("type")
	private Type type;
	@JsonProperty("value")
	private Value value;
	@JsonProperty("flags")
	private Flags flags;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("type")
	public Type getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(Type type) {
		this.type = type;
	}

	@JsonProperty("value")
	public Value getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(Value value) {
		this.value = value;
	}

	@JsonProperty("flags")
	public Flags getFlags() {
		return flags;
	}

	@JsonProperty("flags")
	public void setFlags(Flags flags) {
		this.flags = flags;
	}

	public String toJsonString() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = mapper.writeValueAsString(this);
		} catch (JsonProcessingException exception) {
			Logger logger = LoggerFactory.getLogger(KafkaProcessorUtils.class.getName());
			logger.debug("Exception Occurred:"+exception.getMessage(), exception);
		}
		return jsonString;
	}

	@Override
	public String toString() {
		return "ProcessVariable [name=" + name + ", description=" + description + ", type=" + type.toString()
				+ ", value=" + value.toString() + ", flags=" + flags.toString() + "]";
	}
}
