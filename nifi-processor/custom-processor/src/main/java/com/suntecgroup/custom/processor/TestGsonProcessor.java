package com.suntecgroup.custom.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.JSONObjectDeserializer;
import com.suntecgroup.custom.processor.utils.MapDeserializer;

public class TestGsonProcessor extends AbstractProcessor {
	
	private List<PropertyDescriptor> properties;
	private Set<Relationship> relationships;
	
	public static final PropertyDescriptor PATH_NAME = new PropertyDescriptor.Builder().name("Path_Name")
			.description("path name for the merge processor").addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.required(false).build();
	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description("Success relationship").build();
	
	@Override
	protected void init(final ProcessorInitializationContext context) {
		
		final List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
		properties.add(PATH_NAME);
		this.properties = Collections.unmodifiableList(properties);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		this.relationships = Collections.unmodifiableSet(relationships);
	}
	
	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {

		// Reading the flowFile
		final FlowFile inFlowFile = session.get();
		if (null == inFlowFile) {
			return;
		}
		FlowFile outFlowFile = session.create();
		InputStream inputStream = session.read(inFlowFile);
		OutputStream outputStream = session.write(outFlowFile);
		
		try {
			// Reader
			JsonReader reader = new JsonReader(new InputStreamReader(inputStream, Constants.UTF_ENCODING));
			reader.setLenient(true);
			reader.beginArray();
			
			// Writer
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, Constants.UTF_ENCODING));
			writer.beginArray();
			
			// Mapper objects
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			JsonDeserializer<Map<String, Object>> customMapDeserializer = new MapDeserializer();
			
			Type jsonObjectType = new TypeToken<JSONObject>() {
			}.getType();
			JsonDeserializer<JSONObject> customJSONObjectDeserializer = new JSONObjectDeserializer();
			
			// Gson builder
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(type, customMapDeserializer);
			gsonBuilder.registerTypeAdapter(jsonObjectType, customJSONObjectDeserializer);
			Gson gson = gsonBuilder.create();
			
			if (reader.hasNext()) {
				// reading & writing as Map entry
				Map<String, Object> eventRecord = gson.fromJson(reader, type);
				gson.toJson(eventRecord, type, writer);
				
				// reading & writing as JsonObject
				JsonObject jsonObj = gson.fromJson(reader, JsonObject.class);
				gson.toJson(jsonObj, writer);
								
				// reading & writing as JSONObject 
				JSONObject JSONObject = gson.fromJson(reader, jsonObjectType);
				JsonElement jsonElement = gson.fromJson(JSONObject.toString(), JsonElement.class);
				gson.toJson(jsonElement, JsonElement.class, writer);
			}
			reader.close();
			writer.endArray();
			writer.close();
			session.transfer(outFlowFile, REL_SUCCESS);
			session.remove(inFlowFile);
			session.commit();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != outputStream) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Set<Relationship> getRelationships() {
		return relationships;
	}

	@Override
	public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return properties;
	}
	
}
