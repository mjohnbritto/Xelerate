package com.suntecgroup.nifi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.MongoException;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.factory.NifiProcessorFactory;
import com.suntecgroup.nifi.frontend.bean.BPFlowResponseXml;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.BPFlowUIResponse;
import com.suntecgroup.nifi.frontend.bean.BPValidation;
import com.suntecgroup.nifi.frontend.bean.Connection;
import com.suntecgroup.nifi.frontend.bean.ErrorDetails;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.httpClient.MongoDBClientData;
import com.suntecgroup.nifi.operators.ConnectionsNifiOperator;
import com.suntecgroup.nifi.service.CGServiceInterface;
import com.suntecgroup.nifi.util.BPCanvasUtils;
import com.suntecgroup.nifi.xml.CGGenerateNifiXml;

/**
 * Implementation service class for business logic
 *
 */
@Service
public class CGServiceImpl implements CGServiceInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(CGServiceImpl.class);

	@Autowired
	private NifiProcessorFactory nifiProcessorFactory;

	@Autowired
	private ConnectionsNifiOperator connectionsNifiOperator;

	@Autowired
	private CGGenerateNifiXml bpGenerateXML;

	@Autowired
	private MongoDBClientData mongoDBClientData;

	@Autowired
	private Environment env;

	@Autowired
	private CGValidator cgValidator;

	@Autowired
	private CGServiceInterface bpService;

	/**
	 * createNifiTemplate - This method will fetch the Saved diagram from
	 * External API and returns the response to the BPController.
	 * 
	 * @param departmentName
	 *            - holds the department data of String type
	 * @param moduleName
	 *            - holds the module data of String type
	 * @param beName
	 *            - holds the beName data of String type
	 * @return response object containing Business Entity details
	 */

	public BPFlowResponseXml createNifiTemplate(String department, String module, String release, String assetType,
			String assetName) {
		LOGGER.info("Creating Nifi Template for Processors::");
		BPFlowUIResponse bPFlowUIResponse = new BPFlowUIResponse();
		BPFlowResponseXml bpResponse = new BPFlowResponseXml();
		List<ErrorDetails> errorDetailsList = new ArrayList<ErrorDetails>();
		Gson gson = new Gson();
		ErrorDetails validationErrorDetails = null;
		try {
			bPFlowUIResponse = getBPJson(department, module, release, assetType, assetName);
			if (null != bPFlowUIResponse) {
				LOGGER.info("BPflow UI Response retrieved and started flow to generate template");
				BPFlowUI bui = null;
				String bpf = bPFlowUIResponse.getAssetDetail();
				String assetJSON = bPFlowUIResponse.getAssetDetail();
				bui = gson.fromJson(bpf, BPFlowUI.class);

				// check for No BE scenario
				updateEventLogInfoForNoBEScenario(bui, bPFlowUIResponse);

				BPValidation response = bpService.validateInputJson(bui);
				if (null != response.getValidationError() && response.getValidationError().size() > 0) {
					LOGGER.error("Found " + response.getValidationError().size() + " more validation error(s).");
					validationErrorDetails = new ErrorDetails(CGConstants.EXCEPTION_STATUS_CODE,
							"BP Asset Validation Test Failed :: Found " + response.getValidationError().size()
									+ " more validation error(s).");
				}

				for (Connection con : bui.getConnections()) {
					List<Operators> listop = bui.getOperators();
					if (null != con.getUi_attributes().getType()
							&& CGConstants.SMARTCONNECTOR.equals(con.getUi_attributes().getType())) {
						Operators op = new Operators();
						op.setBusinessSettings(con.getLink_properties().getBusinessSettings());
						op.getBusinessSettings()
								.setInputBe(con.getLink_properties().getBusinessSettings().getInputBe());
						op.getBusinessSettings()
								.setOutputBe(con.getLink_properties().getBusinessSettings().getOutputBe());
						op.setSmartConnectorMapping(con.getLink_properties().getMapping());
						op.setComments(con.getLink_properties().getComments());
						op.setProperties(con.getLink_properties().getProperties());
						op.setName(con.getUi_attributes().getKey());
						op.setKey(con.getUi_attributes().getKey());
						op.setType(con.getUi_attributes().getType());
						listop.add(op);
						bui.setOperators(listop);
					} else {
						con.getUi_attributes().setConnName(con.getUi_attributes().getKey());
						con.getUi_attributes().setKey("");
					}
				}
				bpResponse = generateNifiTemplate(bui, bPFlowUIResponse);
			}
			LOGGER.info("Nifi Template for Processors is generated::");

		} catch (MongoException dbException) {
			LOGGER.error("DB Exception occured while retrieving data ", dbException);
			ErrorDetails errorDetails = new ErrorDetails(CGConstants.EXCEPTION_STATUS_CODE, dbException.getMessage());
			errorDetailsList.add(errorDetails);
		} catch (CGException bpException) {
			LOGGER.error("BP Exception occured while retrieving data ", bpException);
			ErrorDetails errorDetails = new ErrorDetails(CGConstants.EXCEPTION_STATUS_CODE, bpException.getMessage());
			errorDetailsList.add(errorDetails);
		} catch (Exception e) {
			LOGGER.error("Exception occured while retrieving data ", e);
			ErrorDetails errorDetails = new ErrorDetails(CGConstants.EXCEPTION_STATUS_CODE,
					CGConstants.EXCEPTION_STATUS_DESC);
			errorDetailsList.add(errorDetails);
		} finally {
			if (null != validationErrorDetails) {
				errorDetailsList.clear();
				errorDetailsList.add(validationErrorDetails);
				bpResponse.setErrorDetails(errorDetailsList);
			} else if (null != errorDetailsList && errorDetailsList.size() > 0) {
				bpResponse.setErrorDetails(errorDetailsList);
			}
		}
		return bpResponse;
	}

	/**
	 * generateNifiTemplate - This method will generate the template structure
	 * from API and returns the response to the createNifiTemplate method.
	 * 
	 * @param bpFlowRequest
	 *            - holds the BPFlowUI data of BPFlowUI type
	 * @param bPFlowUIResponse
	 *            - holds the meta data information of BPFlow type
	 * @return bpResponse object
	 */
	private BPFlowResponseXml generateNifiTemplate(BPFlowUI bpFlowRequest, BPFlowUIResponse bPFlowUIResponse)
			throws CGException {

		SuntecNiFiModel target = new SuntecNiFiModel();
		BPFlowResponseXml bpResponse = new BPFlowResponseXml();

		target.setProcessGroupID(UUID.randomUUID().toString());

		target.getMetaData().setDepartmentName(bPFlowUIResponse.getDepartment());
		target.getMetaData().setModuleName(bPFlowUIResponse.getModule());
		target.getMetaData().setReleaseNo(bPFlowUIResponse.getRelease());
		target.getMetaData().setAssetType(bPFlowUIResponse.getAssetType());
		target.getMetaData().setAssetName(bPFlowUIResponse.getAssetName());
		target.getMetaData().setArtifactId(bPFlowUIResponse.getArtifactId());
		target.setConfig(bpFlowRequest.getConfigureBusinessProcess());
		try {
			BPCanvasUtils.rearrangeOperators(bpFlowRequest);
		} catch (ArrayIndexOutOfBoundsException ae) {
		}
		nifiProcessorFactory.generateNiFiProcessors(bpFlowRequest, target);
		connectionsNifiOperator.generateNiFiConnections(bpFlowRequest, target);
		bpResponse = bpGenerateXML.generateBusinessProcess(bpFlowRequest, target);
		// reset canvas Axis default positions
		BPCanvasUtils.resetPos();
		return bpResponse;
	}

	private void updateEventLogInfoForNoBEScenario(BPFlowUI bui, BPFlowUIResponse bPFlowUIResponse) {
		for (Operators operator : bui.getOperators()) {
			// if start operator
			if (operator.getType().equalsIgnoreCase(CGConstants.START)) {
				// if start operator is configured with no BE
				if (StringUtils.isBlank(operator.getBusinessSettings().getInputBeType())) {
					List<Operators> level_2_Operators = getDestinationOperators(bui.getOperators(),
							bui.getConnections(), operator);
					// level 2 operators
					if (StringUtils.equalsIgnoreCase(CGConstants.INVOKE_BS, level_2_Operators.get(0).getType())
							|| StringUtils.equalsIgnoreCase(CGConstants.INVOKE_BS_EXTERNAL,
									level_2_Operators.get(0).getType())) {
						updateEventLogFlag(bui.getOperators(),
								new ArrayList<String>(Arrays.asList(level_2_Operators.get(0).getKey())));
					} else if (StringUtils.equalsIgnoreCase(CGConstants.DECISION_MATRIX_INCLUSIVE,
							level_2_Operators.get(0).getType())
							|| StringUtils.equalsIgnoreCase(CGConstants.DECISION_MATRIX_EXCLUSIVE,
									level_2_Operators.get(0).getType())) {
						List<Operators> level_3_Operators = getDestinationOperators(bui.getOperators(),
								bui.getConnections(), level_2_Operators.get(0));
						for (Operators level_3_Operator : level_3_Operators) {
							// level 3 operators
							if (StringUtils.equalsIgnoreCase(CGConstants.INVOKE_BS, level_3_Operator.getType())
									|| StringUtils.equalsIgnoreCase(CGConstants.INVOKE_BS_EXTERNAL,
											level_3_Operator.getType())) {
								updateEventLogFlag(bui.getOperators(),
										new ArrayList<String>(Arrays.asList(level_3_Operator.getKey())));
							} else if (StringUtils.equalsIgnoreCase(CGConstants.JOIN, level_3_Operator.getType())) {
								List<Operators> level_4_Operators = getDestinationOperators(bui.getOperators(),
										bui.getConnections(), level_3_Operator);
								for (Operators level_4_Operator : level_4_Operators) {
									// level 4 operators
									if (StringUtils.equalsIgnoreCase(CGConstants.INVOKE_BS, level_4_Operator.getType())
											|| StringUtils.equalsIgnoreCase(CGConstants.INVOKE_BS_EXTERNAL,
													level_4_Operator.getType())) {
										updateEventLogFlag(bui.getOperators(),
												new ArrayList<String>(Arrays.asList(level_4_Operator.getKey())));
									}
								}
							}
						}
					}
				}
				break;
			}
		}
	}

	private List<Operators> getDestinationOperators(List<Operators> operators, List<Connection> connections,
			Operators sourceOperator) {
		List<String> operatorKeys = new ArrayList<String>();
		List<Operators> destinationOperators = new ArrayList<Operators>();
		for (Connection connection : connections) {
			if (StringUtils.equalsIgnoreCase(sourceOperator.getKey(), connection.getUi_attributes().getSourceName())) {
				operatorKeys.add(connection.getUi_attributes().getDestinationName());
			}
		}
		for (Operators operator : operators) {
			if (operatorKeys.contains(operator.getKey())) {
				destinationOperators.add(operator);
			}
		}
		return destinationOperators;
	}

	private void updateEventLogFlag(List<Operators> operators, List<String> operatorKeys) {
		for (Operators operator : operators) {
			if (operatorKeys.contains(operator.getKey())) {
				operator.setEventLogFlag(true);
			}
		}
	}

	/**
	 * getBPJson - This method will get the response from External API.
	 * 
	 * @param departmentName
	 *            - holds the departmentName data of String type
	 * @param moduleName
	 *            - holds the moduleName data information of String type
	 * @param bpName
	 *            - holds the moduleName data information of String type
	 * @return - returns bpflow response from API
	 */
	private BPFlowUIResponse getBPJson(String departmentName, String moduleName, String release, String assetType,
			String assetName) throws Exception {
		LOGGER.info("GetBPJson Starts:: " + departmentName + " " + moduleName + " " + release + " " + assetType + " "
				+ assetName + ".");
		BPFlowUIResponse bPFlowUIResponse = new BPFlowUIResponse();
		try {
			bPFlowUIResponse = mongoDBClientData.getBPJson(departmentName, moduleName, release, assetType, assetName);
			LOGGER.info("BPFlow retrieved from the service :: " + bPFlowUIResponse.getAssetName());
		} catch (CGException cge) {
			throw cge;
		} catch (Exception e) {
			throw new CGException(
					"Exception occured while retrieving BPFlow from DB for given department/module/bpname:: "
							+ e.getMessage());
		}
		return bPFlowUIResponse;
	}

	public BPValidation validateInputJson(BPFlowUI bPFlowRequest) {
		BPValidation responseObj = cgValidator.validateInputJson(bPFlowRequest);
		return responseObj;
	}

}
