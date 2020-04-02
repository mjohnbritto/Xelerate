package com.suntecgroup.nifi.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.suntecgroup.nifi.config.CGConfigurationProperty;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.datamodel.SuntecOperatorModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Connection;
import com.suntecgroup.nifi.frontend.bean.InputConnection;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.frontend.bean.UIAttributes;
import com.suntecgroup.nifi.frontend.bean.merge.TraversingOperator;
import com.suntecgroup.nifi.operators.DecisionMatrixExclusiveOperator;
import com.suntecgroup.nifi.operators.DecisionMatrixInclusiveOperator;
import com.suntecgroup.nifi.operators.EndNifiOperator;
import com.suntecgroup.nifi.operators.FileChannelIntegrationOperator;
import com.suntecgroup.nifi.operators.InputRestChannelIntegrationOperator;
import com.suntecgroup.nifi.operators.InvokeBSNifiOperator;
import com.suntecgroup.nifi.operators.JoinCustomNifiOperator;
import com.suntecgroup.nifi.operators.MergeOperator;
import com.suntecgroup.nifi.operators.OutputFileChannelIntegrationOperator;
import com.suntecgroup.nifi.operators.OutputRestChannelIntegrationOperator;
import com.suntecgroup.nifi.operators.SmartConnectorOperator;
import com.suntecgroup.nifi.operators.StartNifiOperator;
import com.suntecgroup.nifi.template.beans.TemplateEntry;
import com.suntecgroup.nifi.template.beans.TemplateEntryDesc;
import com.suntecgroup.nifi.template.beans.TemplateProcessor;
import com.suntecgroup.nifi.template.beans.TemplateValue;

/**
 * NifiProcessorFactory - This class will generate the processors for the
 * respective operators and returns the response.
 */
@Component
public class NifiProcessorFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(NifiProcessorFactory.class);

	@Autowired
	private StartNifiOperator startNifiOperator;
	@Autowired
	private InvokeBSNifiOperator invokeBSNifiOperator;
	@Autowired
	private EndNifiOperator endNifiOperator;
	@Autowired
	private DecisionMatrixExclusiveOperator decisionMatrixExclusiveOperator;
	@Autowired
	private SmartConnectorOperator smartConnectorOperator;
	@Autowired
	private InvokeBSNifiOperator invokeBSExternalNifiOperator;
	@Autowired
	private FileChannelIntegrationOperator fileChannelIntegrationOperator;
	@Autowired
	private OutputFileChannelIntegrationOperator outputFileChannelIntegrationOperator;
	@Autowired
	private InputRestChannelIntegrationOperator inputRestChannelIntegrationOperator;
	@Autowired
	private OutputRestChannelIntegrationOperator outputRestChannelIntegrationOperator;
	@Autowired
	private MergeOperator mergeOperator;
	@Autowired
	private DecisionMatrixInclusiveOperator decisionMatrixInclusiveOperator;
	@Autowired
	private JoinCustomNifiOperator joinCustomNifiOperator;
	@Autowired
	private CGConfigurationProperty businessConfigProperty;

	/**
	 * generateNiFiProcessors - This method will generate the processors
	 * structure from the suntec operators and returns the response .
	 * 
	 * @param bpFlowRequest
	 *            - holds the BPFlowUI data of BPFlowUI type
	 * @param theTarget
	 *            - holds the full meta data information of BPFlow type
	 * @return suntec object having processor list
	 */
	public void generateNiFiProcessors(BPFlowUI bPFlowRequest, SuntecNiFiModel theTarget) throws CGException {
		if (null != bPFlowRequest) {
			SuntecOperatorModel som = null;
			String opType = "";

			for (Operators op : bPFlowRequest.getOperators()) {
				opType = op.getType();
				if (null != opType && !"".equals(opType)) {
					if (CGConstants.START.equals(opType)) {
						som = startNifiOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.INVOKE_BS.equals(opType)) {
						som = invokeBSNifiOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.END.equals(opType)) {
						som = endNifiOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.DECISION_MATRIX_EXCLUSIVE.equals(opType)) {
						som = decisionMatrixExclusiveOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.SMARTCONNECTOR.equals(opType)) {
						som = smartConnectorOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.INVOKE_BS_EXTERNAL.equals(opType)) {
						som = invokeBSExternalNifiOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.DECISION_MATRIX_INCLUSIVE.equals(opType)) {
						som = decisionMatrixInclusiveOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.FILE_CHANNEL_INTEGRATION_INPUT.equals(opType)) {
						som = fileChannelIntegrationOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.FILE_CHANNEL_INTEGRATION_OUTPUT.equals(opType)) {
						som = outputFileChannelIntegrationOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.REST_INPUT_CHANNEL_INTEGRATION.equals(opType)) {
						som = inputRestChannelIntegrationOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.REST_OUTPUT_CHANNEL_INTEGRATION.equals(opType)) {
						som = outputRestChannelIntegrationOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.MERGE.equals(opType)) {
						som = mergeOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					} else if (CGConstants.JOIN.equals(opType)) {
						som = joinCustomNifiOperator.generateNifiProcessors(bPFlowRequest, theTarget, op);
					}
				}
				som.setSuntec_operator(op);
				theTarget.getSopList().add(som);
			}
		}
	}

	/**
	 * flagMergeSource - This method will add a flag to operators for which the
	 * outgoing connection has merge on succeeding hops.
	 * 
	 * @param bpFlow
	 *            - holds the BPFlowUI data of BPFlowUI type
	 * @param model
	 *            - holds the full meta data information of BPFlow type
	 * @return void
	 */
	public void flagMergeSource(BPFlowUI bpFlow, SuntecNiFiModel model) throws CGException {

		List<Connection> connList = prepareConn(bpFlow.getConnections());
		Map<String, Operators> operatorsMap = new HashMap<String, Operators>();
		Stack<Operators> opStack = new Stack<Operators>();
		Set<String> tracedOp = new HashSet<String>();
		Operators op;
		int count = 0;
		boolean isMergeSource = true;
		boolean ignoreFirstMergeFlap;
		Operators currentOp;

		for (Operators operator : bpFlow.getOperators()) {
			operatorsMap.put(operator.getKey(), operator);
		}

		for (Operators operator : bpFlow.getOperators()) {
			if (CGConstants.MERGE.equals(operator.getType())) {
				ignoreFirstMergeFlap = true;
				opStack.push(operator);
				do {
					if (ignoreFirstMergeFlap) {
						op = opStack.pop();
					} else {
						op = opStack.peek();
					}
					count = 0;
					if (!tracedOp.contains(op.getKey())) {
						for (Connection conn : connList) {
							if (op.getKey().equals(conn.getUi_attributes().getDestinationName())) {
								if (!(CGConstants.MERGE.equals(op.getType()) || CGConstants.START.equals(op.getType()))
										|| ignoreFirstMergeFlap) {
									currentOp = operatorsMap.get(conn.getUi_attributes().getSourceName());
									if (!(opStack.contains(currentOp) || tracedOp.contains(currentOp.getKey()))) {
										opStack.push(currentOp);
										count++;
									}
								}
							}
						}
//						if (CGConstants.START.equals(op.getType())) {
//							isMergeSource = false;
//						}
					}
					if (count == 0) {
						op = opStack.pop();
						if (!(CGConstants.START.equals(op.getType()))) {
//							if ((CGConstants.DECISION_MATRIX_INCLUSIVE.equals(op.getType())
//									|| CGConstants.DECISION_MATRIX_EXCLUSIVE.equals(op.getType())) && !isMergeSource) {
//								isMergeSource = true;
//							}
							if (isMergeSource) {
								for (TemplateProcessor processor : model.getOperatorByKey(op.getKey())
										.getNifiProcessorList()) {
									if (!businessConfigProperty.getInvokeHttpComponentType().equals(processor.getType())
											&& !businessConfigProperty.getCustomFailureComponentType()
													.equals(processor.getType())) {
										try {
											TemplateEntryDesc descEntry = new TemplateEntryDesc();
											TemplateValue value = new TemplateValue();
											value.setName(CGConstants.MERGE_SOURCE);
											descEntry.setKey(CGConstants.MERGE_SOURCE);
											descEntry.setValue(value);
											TemplateEntry propertyEntry = new TemplateEntry();
											propertyEntry.setKey(CGConstants.MERGE_SOURCE);
											propertyEntry.setValue("true");
											processor.getConfig().getDescriptors().getEntryDescList().add(descEntry);
											processor.getConfig().getProperties().getEntryList().add(propertyEntry);
										} catch (Exception e) {
											LOGGER.error("Exception occurred: ", e);
										}
									}
								}
							}
						}
					} else if (!ignoreFirstMergeFlap) {
						tracedOp.add(op.getKey());
					}
					if (ignoreFirstMergeFlap) {
						ignoreFirstMergeFlap = false;
					}
				} while (opStack.size() > 0);
			}
		}
	}

	/**
	 * updatePathAndFlowFilesExpectedCountForMerge - This method will update
	 * path info along with expected flow files count for each merge operator.
	 * 
	 * @param bpFlow
	 *            - holds the BPFlowUI data of BPFlowUI type
	 * @param model
	 *            - holds the full meta data information of BPFlow type
	 * @return void
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public void updatePathAndFlowFilesExpectedCountForMerge(BPFlowUI bpFlow, SuntecNiFiModel model) throws CGException {

		try {
			Gson gson = new Gson();
			List<Connection> connList = prepareConn(bpFlow.getConnections());
			Map<String, Integer> count;
			int stage = 1;
			List<String> nextStageOp = null;
			List<String> currentStageOp = null;
			String sourceOp;
			Map<String, TraversingOperator> operatorsMap = new HashMap<String, TraversingOperator>();
			List<TraversingOperator> tOpList = new ArrayList<TraversingOperator>();
			TraversingOperator tOp;
			TemplateProcessor mergePreProcessor = null;

			for (Operators operator : bpFlow.getOperators()) {
				tOp = new TraversingOperator(operator, new ArrayList<String>(), new ArrayList<String>());
				operatorsMap.put(operator.getKey(), tOp);
				tOpList.add(tOp);
			}

			for (TraversingOperator trOperator : tOpList) {
				if (CGConstants.MERGE.equals(trOperator.getOperator().getType())) {
					count = new HashMap<String, Integer>();
					stage = 1;
					nextStageOp = new ArrayList<String>();
					nextStageOp.add(trOperator.getOperator().getKey());
					do {
						currentStageOp = new ArrayList<String>(nextStageOp);
						nextStageOp = new ArrayList<String>();

						for (String opKey : currentStageOp) {
							// for each op on that stage
							TraversingOperator op = operatorsMap.get(opKey);
							
							if (stage == 2) {
								for (String _connName : op.getConnectionNameList()) {
									count.put(_connName, 1);
								}
							}

							for (Connection conn : connList) {
								if (op.getOperator().getKey().equals(conn.getUi_attributes().getDestinationName())) {
									sourceOp = conn.getUi_attributes().getSourceName();
									if (!CGConstants.START.equals(operatorsMap.get(sourceOp).getOperator().getType())) {
										if (!CGConstants.MERGE
												.equals(operatorsMap.get(sourceOp).getOperator().getType())
												|| stage == 1) {
											nextStageOp.add(sourceOp);
										}
										if (stage == 1) {
											String _connName = conn.getUi_attributes().getConnName();
											if (CGConstants.DECISION_MATRIX_INCLUSIVE
													.equals(operatorsMap.get(sourceOp).getOperator().getType())
													|| CGConstants.DECISION_MATRIX_EXCLUSIVE.equals(
															operatorsMap.get(sourceOp).getOperator().getType())) {
												_connName = conn.getUi_attributes().getDecisionName() + "=" + _connName;
											}
											operatorsMap.get(sourceOp).getConnectionNameList().add(_connName);
											operatorsMap.get(sourceOp).getPathNameList().add(_connName);
										} else {
											operatorsMap.get(sourceOp).getConnectionNameList()
													.addAll(op.getConnectionNameList());
										}
										if (CGConstants.JOIN
												.equals(operatorsMap.get(conn.getUi_attributes().getDestinationName())
														.getOperator().getType())) {
											if (stage > 1) {
												for (String _connName : op.getConnectionNameList()) {
													count.put(_connName, count.get(_connName) + 1);
												}
											}
										}
										if (CGConstants.MERGE
												.equals(operatorsMap.get(sourceOp).getOperator().getType())) {
											count.put(operatorsMap.get(sourceOp).getConnectionNameList().get(0), 1);
										}
									}
								}
							}

							if (stage == 2) {
								List<TemplateProcessor> processors = new ArrayList<TemplateProcessor>();
								processors.add(model.getOperatorByKey(op.getOperator().getKey()).getMyLastProcessor());
								if (null != model.getOperatorByKey(op.getOperator().getKey()).getMyLastAlternateProcessor()) {
									processors.add(model.getOperatorByKey(op.getOperator().getKey()).getMyLastAlternateProcessor());
								}
								for (TemplateProcessor processor : processors) {
									try {
										String propValue = "";
										TemplateEntryDesc descEntry = new TemplateEntryDesc();
										TemplateValue value = new TemplateValue();
										value.setName(CGConstants.PATH_NAME);
										descEntry.setKey(CGConstants.PATH_NAME);
										descEntry.setValue(value);
										TemplateEntry propertyEntry = new TemplateEntry();
										propertyEntry.setKey(CGConstants.PATH_NAME);
										for (int i = 0; i < op.getPathNameList().size(); i++) {
											if (i == 0) {
												propValue = op.getPathNameList().get(0);
											} else {
												propValue = propValue + " " + op.getPathNameList().get(i);
											}
										}
										propertyEntry.setValue(propValue);
										processor.getConfig().getDescriptors().getEntryDescList().add(descEntry);
										processor.getConfig().getProperties().getEntryList().add(propertyEntry);
									} catch (Exception e) {
										LOGGER.error("Exception occurred: ", e);
									}
								}
							}

							if (CGConstants.JOIN.equals(op.getOperator().getType())) {
								for (String _connName : op.getConnectionNameList()) {
									count.put(_connName, count.get(_connName) - 1);
								}
							}
						}
						stage++;
					} while (nextStageOp.size() > 0);

					// update the values in InputConnections
					for (TemplateProcessor processor : model.getOperatorByKey(trOperator.getOperator().getKey())
							.getNifiProcessorList()) {
						if (processor.getType().equals(businessConfigProperty.getCustomPreMergeComponentType())) {
							mergePreProcessor = processor;
							break;
						}
					}

					if (mergePreProcessor != null) {
						List<TemplateEntry> entryList = mergePreProcessor.getConfig().getProperties().getEntryList();
						int totalCount = 0;
						for (TemplateEntry entry : entryList) {
							if ("Input and CV mapping".equals(entry.getKey())) {
								// update connection list with count values
								List<InputConnection> inputConnList = null;
								ObjectMapper mapper = new ObjectMapper();
								inputConnList = mapper.readValue(entry.getValue(),
										new TypeReference<List<InputConnection>>() {
										});
								Map<String, Integer> _countMap = null;
								for (InputConnection ipConn : inputConnList) {
									String _connName = ipConn.getConnectionName();
									_countMap = new HashMap<String, Integer>();
									for (String k : count.keySet()) {
										_countMap.put(k.contains("=") ? k.split("=")[1] : k, count.get(k));
									}
									int _count = _countMap.get(_connName);
									ipConn.setFileCount(_count);
									totalCount += _count;
								}
								entry.setValue(gson.toJson(inputConnList));
								break;
							}
						}
						// also update the total in 'Expected File Count'
						TemplateEntryDesc descEntry = new TemplateEntryDesc();
						TemplateValue value = new TemplateValue();
						value.setName("Expected File Count");
						descEntry.setKey("Expected File Count");
						descEntry.setValue(value);
						TemplateEntry propertyEntry = new TemplateEntry();
						propertyEntry.setKey("Expected File Count");
						propertyEntry.setValue("" + totalCount);
						mergePreProcessor.getConfig().getDescriptors().getEntryDescList().add(descEntry);
						mergePreProcessor.getConfig().getProperties().getEntryList().add(propertyEntry);
					}
				}
			}
		} catch (JsonParseException jpe) {
			throw new CGException("JsonParseException", jpe);
		} catch (JsonMappingException jme) {
			throw new CGException("JsonMappingException", jme);
		} catch (IOException ioe) {
			throw new CGException("IOException", ioe);
		}
	}

	/**
	 * prepareConn - This method will update
	 * connection list to include smartconnector.
	 * 
	 * @param connections
	 *            - holds the list of connection
	 * @return List of updated connection
	 */
	private List<Connection> prepareConn(final List<Connection> connections) {

		List<Connection> connList = new ArrayList<Connection>();

		for (Connection conn : connections) {
			if (conn.getUi_attributes().getType() != null) {
				Connection sourceToSmart = new Connection(new UIAttributes(), null);
				sourceToSmart.getUi_attributes().setSourceName(conn.getUi_attributes().getSourceName());
				sourceToSmart.getUi_attributes().setDestinationName(conn.getUi_attributes().getKey());
				sourceToSmart.getUi_attributes().setConnName(conn.getUi_attributes().getConnName());
				sourceToSmart.getUi_attributes().setDecisionName(conn.getUi_attributes().getDecisionName());
				sourceToSmart.getUi_attributes().setType(null);
				Connection smartToDestination = new Connection(new UIAttributes(), null);
				smartToDestination.getUi_attributes().setSourceName(conn.getUi_attributes().getKey());
				smartToDestination.getUi_attributes().setDestinationName(conn.getUi_attributes().getDestinationName());
				smartToDestination.getUi_attributes().setConnName(conn.getUi_attributes().getConnName());
				smartToDestination.getUi_attributes().setDecisionName(conn.getUi_attributes().getDecisionName());
				smartToDestination.getUi_attributes().setType(null);
				connList.add(sourceToSmart);
				connList.add(smartToDestination);
			} else {
				connList.add(conn);
			}
		}

		return connList;
	}

}
