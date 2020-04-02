package com.suntecgroup.custom.processor.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class JSONObjectDeserializer implements JsonDeserializer<JSONObject> {

	@Override
	public JSONObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return (JSONObject) read(json);
	}

	public Object read(JsonElement in) {

		if (in.isJsonArray()) {
			List<Object> list = new ArrayList<Object>();
			JsonArray src = in.getAsJsonArray();
			for (JsonElement anArr : src) {
				list.add(read(anArr));
			}
			return list;
		} else if (in.isJsonObject()) {
			JSONObject target = new JSONObject();
			JsonObject src = in.getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> entitySet = src.entrySet();
			for (Map.Entry<String, JsonElement> entry : entitySet) {
				target.put(entry.getKey(), read(entry.getValue()));
			}
			return target;
		} else if (in.isJsonPrimitive()) {
			JsonPrimitive prim = in.getAsJsonPrimitive();
			if (prim.isBoolean()) {
				return prim.getAsBoolean();
			} else if (prim.isString()) {
				return prim.getAsString();
			} else if (prim.isNumber()) {
				return prim.getAsBigDecimal();
			}
		}
		return null;
	}
}
