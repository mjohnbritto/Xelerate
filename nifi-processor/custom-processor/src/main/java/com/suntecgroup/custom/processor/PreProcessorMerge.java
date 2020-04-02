/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */

package com.suntecgroup.custom.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.nifi.annotation.behavior.TriggerWhenEmpty;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.exception.NifiCustomException;
import com.suntecgroup.custom.processor.merge.Bin;
import com.suntecgroup.custom.processor.merge.BinFiles;
import com.suntecgroup.custom.processor.merge.BinManager;
import com.suntecgroup.custom.processor.merge.InputCVMapping;
import com.suntecgroup.custom.processor.utils.Constants;
import com.suntecgroup.custom.processor.utils.JSONObjectDeserializer;

import java.lang.reflect.Type;

/*
 * This class is for creating a custom NiFi processor to merge multiple business entity.
 * 
 * @version 1.0 - April 2019
 * @author Thatchanamoorthy
 */
@Tags({ "preprocessor,merge" })
@TriggerWhenEmpty
public class PreProcessorMerge extends BinFiles {

	public static final PropertyDescriptor CORRELATION_ATTRIBUTE_NAME = new PropertyDescriptor.Builder()
			.name("Correlation Attribute Name")
			.description(
					"If specified, like FlowFiles will be binned together, where 'like FlowFiles' means FlowFiles that have the same value for "
							+ "this Attribute. If not specified, FlowFiles are bundled by the order in which they are pulled from the queue.")
			.required(true).expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES)
			.addValidator(StandardValidators.ATTRIBUTE_KEY_VALIDATOR).defaultValue("transactionId").build();

	public static final PropertyDescriptor OUTPUTBE_NAME = new PropertyDescriptor.Builder()
			.name("Output Business Entity Type").description("Merge Processor Output Business Entity").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor PV_NAME_ARRAY = new PropertyDescriptor.Builder().name("PV Name Array")
			.description("Process variables array").required(false).addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final Relationship REL_MERGED = new Relationship.Builder().name("merged")
			.description("The FlowFile containing the merged content").build();

	@Override
	public Set<Relationship> getRelationships() {
		final Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_ORIGINAL);
		relationships.add(REL_FAILURE);
		relationships.add(REL_MERGED);
		return relationships;
	}

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		final List<PropertyDescriptor> descriptors = new ArrayList<>();
		descriptors.add(CORRELATION_ATTRIBUTE_NAME);
		descriptors.add(MAPPING);
		descriptors.add(FILE_COUNT);
		descriptors.add(MAX_BIN_AGE);
		descriptors.add(MAX_BIN_COUNT);
		descriptors.add(OUTPUTBE_NAME);
		descriptors.add(SESSION_ID);
		descriptors.add(RUN_NUMBER);
		descriptors.add(MERGE_SOURCE);
		descriptors.add(PV_NAME_ARRAY);
		return descriptors;
	}

	@Override
	protected Collection<ValidationResult> additionalCustomValidation(ValidationContext context) {
		final Collection<ValidationResult> results = new ArrayList<>();
		return results;
	}

	@Override
	protected String getGroupId(final ProcessContext context, final FlowFile flowFile) {
		final String correlationAttributeName = context.getProperty(CORRELATION_ATTRIBUTE_NAME)
				.evaluateAttributeExpressions(flowFile).getValue();
		String groupId = correlationAttributeName == null ? null : flowFile.getAttribute(correlationAttributeName);
		return groupId;
	}

	@Override
	protected void setUpBinManager(final BinManager binManager, final ProcessContext context) {
		binManager.setFileCountAttribute(null);
	}

	@Override
	protected void processBin(final Bin bin, final ProcessContext context)
			throws ProcessException, NifiCustomException {

		MergeBin merger = new BusinessEntityMerge();
		final ProcessSession binSession = bin.getSession();
		
		FlowFile bundle = merger.merge(bin, context);
		// keep the filename, as it is added to the bundle.
		final String filename = bundle.getAttribute(CoreAttributes.FILENAME.key());
		
		// merge all of the attributes
		final Map<String, String> bundleAttributes = new HashMap<String, String>();
		bundleAttributes.put(CoreAttributes.MIME_TYPE.key(), merger.getMergedContentType());
		// restore the filename of the bundle
		bundleAttributes.put(CoreAttributes.FILENAME.key(), filename);

		// Adding process variable from first flow file
		String pvVariableArray = context.getProperty(PV_NAME_ARRAY).evaluateAttributeExpressions().getValue();
		JSONArray pvArray = new JSONArray(pvVariableArray);
		Map<String, String> flowFileAttr = bin.getFirstDataFileAttr();

		if (null != flowFileAttr &&  flowFileAttr.size() > 0) {
			bundleAttributes.put(Constants.EVENT_COUNT,flowFileAttr.get(Constants.EVENT_COUNT));
			String outputBEName = context.getProperty(OUTPUTBE_NAME).evaluateAttributeExpressions().getValue();
			bundleAttributes.put(Constants.BENAME, outputBEName);
			for (int i = 0; i < pvArray.length(); i++) {
				String pvName = (String) pvArray.get(i);
				String pvValue = flowFileAttr.get(pvName) != null ? flowFileAttr.get(pvName).toString() : StringUtils.EMPTY;
				bundleAttributes.put(pvName, pvValue);
			}
		}

		bundle = binSession.putAllAttributes(bundle, bundleAttributes);
		binSession.transfer(bundle, REL_MERGED);
		binSession.remove(bin.getContents());
		binSession.commit();
	}

	private class BusinessEntityMerge implements MergeBin {

		private String mimeType = "application/json";
		
		private Type jsonObjectType = new TypeToken<JSONObject>() {
		}.getType();
		private JsonDeserializer<JSONObject> customJSONObjectDeserializer = new JSONObjectDeserializer();
		private GsonBuilder gsonBuilder = new GsonBuilder();
		private Gson gson = null;

		public BusinessEntityMerge() {
			gsonBuilder.registerTypeAdapter(jsonObjectType, customJSONObjectDeserializer);
			gson = gsonBuilder.create();
		}

		@Override
		public FlowFile merge(final Bin bin, final ProcessContext context) throws NifiCustomException {
			final List<FlowFile> contents = bin.getContents();
			List<InputCVMapping> mappingList = (List<InputCVMapping>) bin.getMapping();

			Map<String, InputCVMapping> inputCVMap = new HashMap<String, InputCVMapping>();
			for (InputCVMapping mapping : mappingList) {
				inputCVMap.put(mapping.getConnectionName(), mapping);
			}

			final ProcessSession session = bin.getSession();
			FlowFile bundle = session.create(bin.getContents());
			
			List<FlowFile> validDataFiles = getValidDataFiles(contents);
			Map<String, List<FlowFile>> inputChannelFilesMap = groupFlowFilesByChannel(validDataFiles);
			

			
			try {
				OutputStream outputStream = session.write(bundle);
				JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, Constants.UTF_ENCODING));
				Gson gsonBuilder = new GsonBuilder().create();
				writer.beginObject();
				writer.name(Constants.context);
				writer.beginObject();
				writer.name(Constants.contextParameters);
				writer.beginObject();
				for (Map.Entry<String, List<FlowFile>> inputChannelFiles : inputChannelFilesMap.entrySet()) {
					List<FlowFile> flowFiles = inputChannelFiles.getValue();
					String channelName = inputChannelFiles.getKey();
					InputCVMapping mapBean = inputCVMap.get(channelName);
					String cvName = mapBean.getContextVariable();
					writer.name(cvName);
					writer.beginArray();
					for (FlowFile flowFile : flowFiles) {						
						InputStream inputStream = session.read(flowFile);
						JsonReader reader = new JsonReader(new InputStreamReader(inputStream, Constants.UTF_ENCODING));
						reader.setLenient(true);
						reader.beginArray();
						while (reader.hasNext()) { 
							JSONObject jsonObject = gson.fromJson(reader, jsonObjectType);							
							// For collection
							if (mapBean.isCompositeBE()) {								
								JSONObject containerObj = new JSONObject();
								containerObj.put(mapBean.getAliasName(), jsonObject);
								JsonElement jsonElement = gsonBuilder.fromJson(containerObj.toString(), JsonElement.class);
								gsonBuilder.toJson(jsonElement, writer);
							} else {
								JsonElement jsonElement = gsonBuilder.fromJson(jsonObject.toString(), JsonElement.class);
								gsonBuilder.toJson(jsonElement, writer);
							}
							// Ends
						}
						reader.close();				
					}
					writer.endArray();
				}
				writer.endObject();
				writer.endObject();
				writer.endObject();
				writer.close();
				outputStream.close();
			} catch (UnsupportedEncodingException e) {
				throw new NifiCustomException(
						"Error occurred while reading Process Variable property: ", e);
			} catch (IOException e) {
				throw new NifiCustomException(
						"Error occurred while reading Process Variable property: ", e);
			}
			return bundle;
		}

		List<FlowFile> getValidDataFiles(List<FlowFile> contents) {
			List<FlowFile> dataFiles = new ArrayList<FlowFile>();
			for (FlowFile flowFile : contents) {
				String markerFlag = flowFile.getAttribute(Constants.IS_MARKER);
				if (StringUtils.isBlank(markerFlag)) {
					dataFiles.add(flowFile);
				}
			}
			return dataFiles;
		}

		@Override
		public String getMergedContentType() {
			return mimeType;
		}
	}

	private interface MergeBin {

		FlowFile merge(Bin bin, ProcessContext context) throws NifiCustomException;

		String getMergedContentType();

	}
}