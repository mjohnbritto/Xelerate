package com.suntecgroup.bp.designer.frontend.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.suntecgroup.bp.designer.exception.BPException;

public class ProcessVariables {

	@SerializedName("key")
	@Expose
	private String key;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("type")
	@Expose
	private Type type;
	@SerializedName("value")
	@Expose
	private PVValue value;
	@SerializedName("flags")
	@Expose
	private PVFlags flags;

	public String toJsonString() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = mapper.writeValueAsString(this);
		} catch (Exception e) {
			throw new BPException("Exception occurred at ProcessVariables.toJsonString method: ", e);
		}
		return jsonString;
	}

	
	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public PVValue getValue() {
		return value;
	}

	public void setValue(PVValue value) {
		this.value = value;
	}

	public PVFlags getFlags() {
		return flags;
	}

	public void setFlags(PVFlags flags) {
		this.flags = flags;
	}

}
