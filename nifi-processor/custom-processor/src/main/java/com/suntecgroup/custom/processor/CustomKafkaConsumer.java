/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor;

import static com.suntecgroup.custom.processor.kafka.KafkaProcessorUtils.HEX_ENCODING;
import static com.suntecgroup.custom.processor.kafka.KafkaProcessorUtils.UTF8_ENCODING;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.nifi.annotation.behavior.DynamicProperty;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.suntecgroup.custom.processor.kafka.KafkaProcessorUtils;
import com.suntecgroup.custom.processor.utils.Constants;

/*
 * This class is for creating a custom NiFi processor to handle kafka stream consumption
 * 
 * @version 1.0 - May 2019
 * @author John Britto
 */

@CapabilityDescription("Consumes messages from Apache Kafka specifically built against the Kafka 2.0 Consumer API. "
		+ "The complementary NiFi processor for sending messages is PublishKafka_2_0.")
@Tags({ "CustomKafkaConsumer", "suntec" })
@WritesAttributes({
		@WritesAttribute(attribute = KafkaProcessorUtils.KAFKA_COUNT, description = "The number of messages written if more than one"),
		@WritesAttribute(attribute = KafkaProcessorUtils.KAFKA_KEY, description = "The key of message if present and if single message. "
				+ "How the key is encoded depends on the value of the 'Key Attribute Encoding' property."),
		@WritesAttribute(attribute = KafkaProcessorUtils.KAFKA_OFFSET, description = "The offset of the message in the partition of the topic."),
		@WritesAttribute(attribute = KafkaProcessorUtils.KAFKA_TIMESTAMP, description = "The timestamp of the message in the partition of the topic."),
		@WritesAttribute(attribute = KafkaProcessorUtils.KAFKA_PARTITION, description = "The partition of the topic the message or message bundle is from"),
		@WritesAttribute(attribute = KafkaProcessorUtils.KAFKA_TOPIC, description = "The topic the message or message bundle is from") })
@InputRequirement(InputRequirement.Requirement.INPUT_REQUIRED)
@DynamicProperty(name = "The name of a Kafka configuration property.", value = "The value of a given Kafka configuration property.", description = "These properties will be added on the Kafka configuration after loading any provided configuration properties."
		+ " In the event a dynamic property represents a property that was already set, its value will be ignored and WARN message logged."
		+ " For the list of available Kafka properties please refer to: http://kafka.apache.org/documentation.html#configuration. ", expressionLanguageScope = ExpressionLanguageScope.VARIABLE_REGISTRY)
public class CustomKafkaConsumer extends AbstractProcessor {

	private ComponentLog LOGGER;
	private Gson gson = null;
	
	public static final PropertyDescriptor INPUT_BE_TYPE = new PropertyDescriptor.Builder()
			.name("Input Business Entity").description("Set input BE Type").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor SESSION_ID = new PropertyDescriptor.Builder().name("Session Id")
			.description("Current session identifier").required(true).defaultValue("${sessionId}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor RUN_NUMBER = new PropertyDescriptor.Builder().name("Run Number")
			.description("Current run identifier").required(true).defaultValue("${runNumber}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	static final PropertyDescriptor GROUP_ID = new PropertyDescriptor.Builder().name(ConsumerConfig.GROUP_ID_CONFIG)
			.displayName("Group ID")
			.description(
					"A Group ID is used to identify consumers that are within the same consumer group. Corresponds to Kafka's 'group.id' property.")
			.required(true).defaultValue("${hostname()}").addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY).build();

	static final AllowableValue OFFSET_EARLIEST = new AllowableValue("earliest", "earliest",
			"Automatically reset the offset to the earliest offset");
	static final AllowableValue OFFSET_LATEST = new AllowableValue("latest", "latest",
			"Automatically reset the offset to the latest offset");
	static final AllowableValue OFFSET_NONE = new AllowableValue("none", "none",
			"Throw exception to the consumer if no previous offset is found for the consumer's group");
	static final PropertyDescriptor AUTO_OFFSET_RESET = new PropertyDescriptor.Builder()
			.name(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG).displayName("Offset Reset")
			.description(
					"Allows you to manage the condition when there is no initial offset in Kafka or if the current offset does not exist any "
							+ "more on the server (e.g. because that data has been deleted). Corresponds to Kafka's 'auto.offset.reset' property.")
			.required(true).allowableValues(OFFSET_EARLIEST, OFFSET_LATEST, OFFSET_NONE)
			.defaultValue(OFFSET_LATEST.getValue()).build();

	static final PropertyDescriptor KEY_ATTRIBUTE_ENCODING = new PropertyDescriptor.Builder()
			.name("key-attribute-encoding").displayName("Key Attribute Encoding")
			.description("FlowFiles that are emitted have an attribute named '" + KafkaProcessorUtils.KAFKA_KEY
					+ "'. This property dictates how the value of the attribute should be encoded.")
			.required(true).defaultValue(UTF8_ENCODING.getValue()).allowableValues(UTF8_ENCODING, HEX_ENCODING).build();

	static final PropertyDescriptor COMMS_TIMEOUT = new PropertyDescriptor.Builder().name("Communications Timeout")
			.displayName("Communications Timeout")
			.description("Specifies the timeout that the consumer should use when communicating with the Kafka Broker")
			.required(true).defaultValue("60 secs").addValidator(StandardValidators.TIME_PERIOD_VALIDATOR).build();
	static final PropertyDescriptor HONOR_TRANSACTIONS = new PropertyDescriptor.Builder().name("honor-transactions")
			.displayName("Honor Transactions")
			.description(
					"Specifies whether or not NiFi should honor transactional guarantees when communicating with Kafka. If false, the Processor will use an \"isolation level\" of "
							+ "read_uncomitted. This means that messages will be received as soon as they are written to Kafka but will be pulled, even if the producer cancels the transactions. If "
							+ "this value is true, NiFi will not receive any messages for which the producer's transaction was canceled, but this can result in some latency since the consumer must wait "
							+ "for the producer to finish its entire transaction instead of pulling as the messages become available.")
			.expressionLanguageSupported(ExpressionLanguageScope.NONE).allowableValues("true", "false")
			.defaultValue("true").required(true).build();

	public static final Relationship REL_SUCCESS = new Relationship.Builder().name("Success")
			.description(
					"FlowFiles received from Kafka. Depending on demarcation strategy it is a flow file per message or a bundle of messages grouped by topic and partition.")
			.build();
	public static final Relationship REL_FAILURE = new Relationship.Builder().name("Failure")
			.description("Failure relationship").build();

	static final List<PropertyDescriptor> DESCRIPTORS;
	static final Set<Relationship> RELATIONSHIPS;
	private int contextMessageCount = 0;
	private boolean isOutputAvailable = false;
	private String inputBEType;

	static {
		List<PropertyDescriptor> descriptors = new ArrayList<>();
		descriptors.addAll(KafkaProcessorUtils.getCommonPropertyDescriptors());
		descriptors.add(HONOR_TRANSACTIONS);
		descriptors.add(GROUP_ID);
		descriptors.add(AUTO_OFFSET_RESET);
		descriptors.add(KEY_ATTRIBUTE_ENCODING);
		descriptors.add(COMMS_TIMEOUT);
		descriptors.add(INPUT_BE_TYPE);
		descriptors.add(SESSION_ID);
		descriptors.add(RUN_NUMBER);
		DESCRIPTORS = Collections.unmodifiableList(descriptors);
		Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		RELATIONSHIPS = Collections.unmodifiableSet(relationships);
	}

	@Override
	public void init(final ProcessorInitializationContext context) {
		LOGGER = context.getLogger();
		gson = new GsonBuilder().create();
	}

	@Override
	public Set<Relationship> getRelationships() {
		return RELATIONSHIPS;
	}

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return DESCRIPTORS;
	}

	@Override
	protected PropertyDescriptor getSupportedDynamicPropertyDescriptor(final String propertyDescriptorName) {
		return new PropertyDescriptor.Builder()
				.description("Specifies the value for '" + propertyDescriptorName + "' Kafka Configuration.")
				.name(propertyDescriptorName)
				.addValidator(new KafkaProcessorUtils.KafkaConfigValidator(ConsumerConfig.class)).dynamic(true)
				.expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY).build();
	}

	@Override
	protected Collection<ValidationResult> customValidate(final ValidationContext validationContext) {
		return KafkaProcessorUtils.validateCommonProperties(validationContext);
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {

		FlowFile inputFlowFile = session.get();
		if (null == inputFlowFile) {
			return;
		}

		inputBEType = context.getProperty(INPUT_BE_TYPE).evaluateAttributeExpressions().getValue();
		if (null != inputBEType) {
			inputBEType = inputBEType.trim();
		}
		String sessionId = context.getProperty(SESSION_ID).evaluateAttributeExpressions().getValue();
		sessionId = sessionId.trim();
		session.putAttribute(inputFlowFile, Constants.ATTR_SESSION_ID, sessionId);
		String runNumber = context.getProperty(RUN_NUMBER).evaluateAttributeExpressions().getValue();
		runNumber = runNumber.trim();
		session.putAttribute(inputFlowFile, Constants.ATTR_RUN_NUMBER, runNumber);

		String topicName = getKafkaTopicName(inputFlowFile, session);
		if (StringUtils.isBlank(topicName)) {
			LOGGER.error("Error while getting the topic name!");
			session.transfer(inputFlowFile, REL_FAILURE);
			session.commit();
			return;
		}

		final List<String> topics = new ArrayList<>();
		topics.add(topicName);
		contextMessageCount = 0;
		isOutputAvailable = false;
		List<Object> polledMessages = new ArrayList<Object>();

		final Map<String, Object> properties = new HashMap<>();
		KafkaProcessorUtils.buildCommonKafkaProperties(context, ConsumerConfig.class, properties);
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
		final boolean honorTransactions = context.getProperty(HONOR_TRANSACTIONS).asBoolean();
		final int commsTimeoutMillis = context.getProperty(COMMS_TIMEOUT).asTimePeriod(TimeUnit.MILLISECONDS)
				.intValue();
		properties.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, commsTimeoutMillis);

		if (honorTransactions) {
			properties.put("isolation.level", "read_committed");
		} else {
			properties.put("isolation.level", "read_uncommitted");
		}

		// create kafka consumer
		final Consumer<byte[], byte[]> kafkaConsumer = new KafkaConsumer<>(properties);
		// subscribe for the topic
		if (topics != null) {
			kafkaConsumer.subscribe(topics);
		}
		// Poll messages
		pollMessages(kafkaConsumer, polledMessages, session);
		// close kafka consumer
		kafkaConsumer.close();

		processKafkaMessages(polledMessages, session, inputFlowFile);
		if (isOutputAvailable) {
			session.remove(inputFlowFile);
		} else {
			session.transfer(inputFlowFile, REL_FAILURE);
		}
		session.commit();
	}

	/**
	 * @param flowfile
	 *            - the input flowfile object
	 * @param session
	 *            - process session object
	 * @return the name of the topic to listen to
	 */
	private String getKafkaTopicName(FlowFile flowfile, final ProcessSession session) {
		InputStream inputStream = null;
		JsonReader reader = null;
		String sessionId = null;
		try {
			inputStream = session.read(flowfile);
			reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			reader.setLenient(true);

			reader.beginObject();
			while (reader.hasNext()) {
				JsonToken nextElement = reader.peek();
				if (nextElement.equals(JsonToken.NAME)) {
					String name = reader.nextName();
					if (name != null && Constants.sessionId.equals(name.trim().toLowerCase())) {
						nextElement = reader.peek();
						if (!nextElement.equals(JsonToken.NULL)) {
							sessionId = "session-" + reader.nextString();
						} else {
							reader.skipValue();
						}
					} else if (name != null && !Constants.context.equals(name.trim().toLowerCase())) {
						reader.skipValue();
					}
				} else {
					jsonManipulation(reader, nextElement);
				}
			}
			reader.endObject();
		} catch (Exception exception) {
			LOGGER.error("Exception occurred :: " + exception.getMessage(), exception);
			closeStream(inputStream, null, reader, null);
		} finally {
			closeStream(inputStream, null, reader, null);
		}
		return sessionId;
	}

	/**
	 * @param kafkaConsumer
	 *            - the kafkaconsumer object
	 * @param polledMessages
	 *            - list object to put the polled messages
	 * @param session
	 *            - process session object
	 */
	private void pollMessages(Consumer<byte[], byte[]> kafkaConsumer, List<Object> polledMessages,
			final ProcessSession session) {
		while (contextMessageCount != 2) {
			final ConsumerRecords<byte[], byte[]> records = kafkaConsumer.poll(100);
			if (records.count() > 0) {
				records.partitions().stream().forEach(partition -> {
					List<ConsumerRecord<byte[], byte[]>> messages = records.records(partition);
					if (!messages.isEmpty()) {
						messages.stream().forEach(message -> {
							writeData(session, message, partition, polledMessages);
						});
					}
				});
			}
		}
	}

	/**
	 * @param session
	 *            - process session object
	 * @param record
	 *            - messageData of a singe poll
	 * @param topicPartition
	 *            - message partition details
	 * @param polledMessages
	 *            - list object to put the polled messages
	 */
	private void writeData(final ProcessSession session, ConsumerRecord<byte[], byte[]> record,
			final TopicPartition topicPartition, List<Object> polledMessages) {
		final byte[] value = record.value();
		String messageJson = new String(value);
		if (!StringUtils.isBlank(messageJson)) {
			try {
				JSONObject jsonObject = new JSONObject(messageJson);
				if (jsonObject.has(Constants.context) && !jsonObject.isNull(Constants.context)) {
					jsonObject = jsonObject.getJSONObject(Constants.context);
					if (null != jsonObject) {
						contextMessageCount++;
						polledMessages.add(messageJson);
					}
				}
				if (jsonObject.has(Constants.entities) && !jsonObject.isNull(Constants.entities)) {
					JSONArray jsonArray = jsonObject.getJSONArray(Constants.entities);
					if (null != jsonArray) {
						polledMessages.add(messageJson);
					}
				}
			} catch (Exception exception) {
				LOGGER.error("Exception occurred :: " + exception.getMessage(), exception);
			}
		}
	}

	/**
	 * @param polledMessages
	 *            - list object that contains the polled messages
	 * @param session
	 *            - process session object
	 * @param inputFlowFile
	 *            - the incoming flowfile object
	 */
	private void processKafkaMessages(List<Object> polledMessages, ProcessSession session, FlowFile inputFlowFile) {
		polledMessages.forEach(message -> {
			// ignore the first and last message as they will be context
			if (!message.equals(polledMessages.get(0))
					&& !message.equals(polledMessages.get(polledMessages.size() - 1))) {
				FlowFile outputFlowFile = session.create(inputFlowFile);
				outputFlowFile = constructFlowFile(polledMessages.get(polledMessages.size() - 1), message,
						outputFlowFile, session);
				session.transfer(outputFlowFile, REL_SUCCESS);
				isOutputAvailable = true;
			}
		});
	}

	/**
	 * @param contextMessage
	 *            - the kafka message that contains the final context details
	 * @param entityMessage
	 *            - the kafka message that contains the BE data
	 * @param outputFlowFile
	 *            - the flowfile object to write the output data
	 * @param session
	 *            - process session object
	 * @return the output flowfile object
	 */
	private FlowFile constructFlowFile(Object contextMessage, Object entityMessage, FlowFile outputFlowFile,
			final ProcessSession session) {
		JsonWriter writer = null;
		
		OutputStream outputStream = session.write(outputFlowFile);

		try {
			writer = new JsonWriter(new OutputStreamWriter(outputStream, Constants.UTF_ENCODING));
			writer.beginObject();

			// Writing the context
			writer.name(StringUtils.uncapitalize(Constants.context));
			JSONObject jsonObject = new JSONObject((String) contextMessage);
			jsonObject = jsonObject.getJSONObject(Constants.context);
			JsonElement jsonElement = gson.fromJson(jsonObject.toString(), JsonElement.class);
			gson.toJson(jsonElement, writer);

			// Writing the BE entity
			if (!StringUtils.isBlank(inputBEType)) {
				writer.name(StringUtils.uncapitalize(inputBEType));
				jsonObject = new JSONObject((String) entityMessage);
				JSONArray jsonArray = jsonObject.getJSONArray(Constants.entities);
				jsonElement = gson.fromJson(jsonArray.toString(), JsonElement.class);
				gson.toJson(jsonElement, writer);
			}

			writer.endObject();
		} catch (Exception exception) {
			LOGGER.error("Exception occurred :: " + exception.getMessage(), exception);
			closeStream(null, outputStream, null, writer);
		} finally {
			closeStream(null, outputStream, null, writer);
		}
		return outputFlowFile;
	}

	/**
	 * This method is used for reading json.
	 * 
	 * @param reader
	 *            - object of json reader
	 * @param nextElement
	 *            - contains next element while reading the json
	 * @throws IOException
	 *             - IOException is thrown
	 */
	private void jsonManipulation(JsonReader reader, JsonToken nextElement) throws IOException {

		switch (nextElement) {
		case BEGIN_OBJECT: {
			reader.beginObject();
			break;
		}
		case END_OBJECT: {
			reader.endObject();
			break;
		}
		case BEGIN_ARRAY: {
			reader.beginArray();
			break;
		}
		case END_ARRAY: {
			reader.endArray();
			break;
		}
		case STRING: {
			reader.skipValue();
			break;
		}
		case NUMBER: {
			reader.skipValue();
			break;
		}
		case BOOLEAN: {
			reader.skipValue();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * @param inputStreamObject
	 *            - InputStream object
	 * @param outputStreamObject
	 *            - OutputStream object
	 * @param reader
	 *            - JsonReader object
	 * @param writer
	 *            - JsonWriter object
	 */
	private void closeStream(InputStream inputStreamObject, OutputStream outputStreamObject, JsonReader reader,
			JsonWriter writer) {
		try {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (inputStreamObject != null) {
				inputStreamObject.close();
			}

			if (outputStreamObject != null) {
				outputStreamObject.close();
			}
		} catch (IOException e) {
			LOGGER.debug("Exception occured while closing the stream " + e.getMessage(), e);
		}
	}
}