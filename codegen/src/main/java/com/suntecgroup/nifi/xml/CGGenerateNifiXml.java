package com.suntecgroup.nifi.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.suntecgroup.nifi.config.CGConfigurationProperty;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.datamodel.SuntecConnectionModel;
import com.suntecgroup.nifi.datamodel.SuntecNiFiModel;
import com.suntecgroup.nifi.datamodel.SuntecOperatorModel;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.factory.NifiProcessorFactory;
import com.suntecgroup.nifi.frontend.bean.BPFlowResponseXml;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.Operators;
import com.suntecgroup.nifi.frontend.bean.ProcessVariables;
import com.suntecgroup.nifi.frontend.bean.Property;
import com.suntecgroup.nifi.template.beans.Template;
import com.suntecgroup.nifi.template.beans.TemplateConnection;
import com.suntecgroup.nifi.template.beans.TemplateContents;
import com.suntecgroup.nifi.template.beans.TemplateFunnels;
import com.suntecgroup.nifi.template.beans.TemplatePosition;
import com.suntecgroup.nifi.template.beans.TemplateProcessGroups;
import com.suntecgroup.nifi.template.beans.TemplateProcessor;
import com.suntecgroup.nifi.template.beans.TemplateSnippet;

/**
 * BPGenerateNifiXml - A class for implementing logic for generating xml
 * 
 */
@Component
public class CGGenerateNifiXml {

	private final static Logger LOGGER = LoggerFactory.getLogger(CGGenerateNifiXml.class);

	@Autowired
	private CGConfigurationProperty property;

	@Autowired
	private Environment env;

	@Autowired
	private NifiProcessorFactory nifiProcessorFactory;
	
	/**
	 * generateBusinessProcess - This method will generate generate
	 * BusinessProcess data from input.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bpflow data of BPFlowUI type
	 * @param theTarget
	 *            - holds the SuntecNiFiModel data information of
	 *            SuntecNiFiModel type
	 * @return - returns template response
	 */
	public BPFlowResponseXml generateBusinessProcess(BPFlowUI bPFlowRequest, SuntecNiFiModel theTarget)
			throws CGException {

		LOGGER.info(" Generating xml Business Process Response ::");
		BPFlowResponseXml bpResponse = new BPFlowResponseXml();
		final String filepath = env.getProperty("output.xml.file.path");
		System.getProperties().put("PATH", filepath);
		LOGGER.info("File generated with destination ::" + filepath);
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");

			Template template = new Template();
			TemplateSnippet snippet = new TemplateSnippet();
			TemplateProcessGroups processGroups = new TemplateProcessGroups();
			TemplatePosition position = new TemplatePosition();
			position.setX("0.0");
			position.setY("0.0");
			TemplateContents contents = new TemplateContents();
			List<TemplateProcessor> processorsList = new ArrayList<TemplateProcessor>();
			List<TemplateFunnels> FunnelsList = new ArrayList<TemplateFunnels>();

			for (SuntecOperatorModel model : theTarget.getSopList()) {
				for (TemplateProcessor nifi_processor : model.getNifiProcessorList()) {
					processorsList.add(nifi_processor);
				}
				for (TemplateFunnels nifi_funnel : model.getNifiFunnelList()) {
					FunnelsList.add(nifi_funnel);
				}
			}
			contents.setProcessorsList(processorsList);
			contents.setFunnelList(FunnelsList);

			List<TemplateConnection> connectionsList = new ArrayList<TemplateConnection>();
			for (SuntecOperatorModel model : theTarget.getSopList()) {
				for (TemplateConnection nifi_connection : model.getNifiConnectionList()) {
					connectionsList.add(nifi_connection);
				}
			}

			for (SuntecConnectionModel model : theTarget.getSconList()) {
				connectionsList.add(model.getNifiConnection());
			}
			contents.setConnectionsList(connectionsList);

			processGroups.setId(theTarget.getProcessGroupID());
			processGroups.setPosition(position);
			processGroups.setComments("");
			processGroups.setParentGroupID(property.getGroupID());

			processGroups.setContents(contents);
			processGroups.setName(bPFlowRequest.getConfigureBusinessProcess().getFunctional().getBusinessProcessSetup()
					.getProcessName());
			processGroups.setVariables(null);
			snippet.setProcessGroups(processGroups);

			template.setEncodingVersion(property.getEncodingVersion());

			// Template Name generation
			String bpName = bPFlowRequest.getConfigureBusinessProcess().getFunctional().getBusinessProcessSetup()
					.getProcessName();
			String department = theTarget.getMetaData().getDepartmentName();
			String module = theTarget.getMetaData().getModuleName();
			String release = theTarget.getMetaData().getReleaseNo();
			String assetType = theTarget.getMetaData().getAssetType();
			String artifactId = "" + theTarget.getMetaData().getArtifactId();

			String templateDescription = department.concat("|").concat(module).concat("|").concat(release)
					+ "|".concat(artifactId).concat("|").concat(assetType).concat("|").concat(bpName);

			template.setName(bPFlowRequest.getConfigureBusinessProcess().getFunctional().getBusinessProcessSetup()
					.getProcessName());
			template.setGroupId(property.getGroupID());
			template.setTimestamp(formatter.format(new Date()));
			template.setDescription(templateDescription);
			template.setSnippet(snippet);

			bpResponse.setTemplate(template);
			nifiProcessorFactory.flagMergeSource(bPFlowRequest, theTarget);
			nifiProcessorFactory.updatePathAndFlowFilesExpectedCountForMerge(bPFlowRequest, theTarget);
			generateXML(template, bPFlowRequest);
			generateCfgFile(bPFlowRequest, theTarget);
		} catch (Exception e) {
			throw new CGException("Exception occured while creating BusinessProcessResponse::", e);
		}
		LOGGER.info(" Business Process Response is generated::");
		return bpResponse;
	}

	/**
	 * generateXML - This method will translate input to xml format.
	 * 
	 * @param template
	 *            - holds the template data of template type
	 * @param bPFlowRequest
	 *            - holds the bPFlowRequest data of BPFlowUI type
	 */
	private void generateXML(Template template, BPFlowUI bPFlowRequest) throws CGException {
		LOGGER.info("Generating XML Template for nifi : path: " + System.getProperty("PATH"));
		try {
			JAXBContext context = JAXBContext.newInstance(Template.class);
			Marshaller mmarshaller = context.createMarshaller();
			mmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			mmarshaller.marshal(template,
					new File(System.getProperty("PATH").concat("/").concat(bPFlowRequest.getConfigureBusinessProcess()
							.getFunctional().getBusinessProcessSetup().getProcessName().concat(".xml"))));
			LOGGER.info("XML Template is generated for nifi, the generated file is: "
					+ System.getProperty("PATH").concat("/").concat(bPFlowRequest.getConfigureBusinessProcess()
							.getFunctional().getBusinessProcessSetup().getProcessName().concat(".xml")));
		} catch (JAXBException e) {
			throw new CGException("Exception occured while creating XML Template file ::", e);
		}
	}

	/**
	 * generateCfgFile - This method will generate config file.
	 * 
	 * @param bPFlowRequest
	 *            - holds the bPFlowRequest data of BPFlowUI type
	 * @param theTarget
	 *            - holds the theTarget data of SuntecNiFiModel type
	 */
	private void generateCfgFile(BPFlowUI bPFlowRequest, SuntecNiFiModel theTarget) throws CGException {
		LOGGER.info(" Generating Config File for nifi");
		Properties prop = new Properties();
		String bpName = bPFlowRequest.getConfigureBusinessProcess().getFunctional().getBusinessProcessSetup()
				.getProcessName();
		String entryString, key, value;

		String filePath = env.getProperty("nifi_configpath");
		String fileName = filePath.concat(bpName).concat(CGConstants.CONF_PROPERTIES);
		PrintWriter printWriter = null;
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(fileName, false);
			printWriter = new PrintWriter(fileWriter);
			List<ProcessVariables> listProcessVariables = bPFlowRequest.getConfigureBusinessProcess().getFunctional()
					.getProcessVariables();

			// process variables

			for (ProcessVariables processVariables : listProcessVariables) {
				key = URLEncoder.encode(bpName + ".processvariable." + processVariables.getName(),
						CGConstants.formatEncodeDecode);
				entryString = key + "=" + processVariables.toJsonString();
				printWriter.println(entryString);
			}

			// Operator properties

			List<Operators> listOperators = bPFlowRequest.getOperators();
			for (Operators operators : listOperators) {
				List<Property> listProperties = operators.getProperties();
				if (null != listProperties && listProperties.size() > 0) {
					for (Property property : listProperties) {
						if (property.isProfileableAtOperation()) {
							key = URLEncoder.encode(bpName + "." + operators.getKey() + "." + property.getName(),
									CGConstants.formatEncodeDecode);
							value = StringUtils.isEmpty(property.getValue()) ? "" : property.getValue();
							prop.setProperty(key, value);
							entryString = key + "=" + value;
							printWriter.println(entryString);
						}
					}
				}

				// Business Error code config property construction

				if (operators.getType().equalsIgnoreCase(CGConstants.INVOKE_BS)) {
					key = (new StringBuilder()).append(bpName).append("." + operators.getKey())
							.append("." + CGConstants.BUSINESS_ERRORCODES).toString();

					String final_value = "";
					List<String> codes = operators.getBusinessSettings().getBusinessErrorCodes();
					for (int i = 0; i < codes.size(); i++) {
						if (i != 0) {
							final_value = final_value + ",";
						}
						final_value = final_value + codes.get(i);
					}
					value = final_value;
					entryString = key.concat(CGConstants.EQUALS).concat(value);
					printWriter.println(entryString);
				}
			}

		} catch (IOException e) {
			throw new CGException("IO Exception occured creating config file::", e);
		} finally {
			printWriter.close();
			try {
				if (fileWriter != null)
					fileWriter.close();
			} catch (IOException e) {
				LOGGER.error("IO Exception occured while closing config file.");
			}
		}

	}

	/**
	 * generateAssetJSONFile - This method will generate asset JSON file.
	 * 
	 * @param inputJSON
	 *            - holds the bPFlowRequest data of BPFlowUI type
	 */
	public void generateAssetJSONFile(String inputJSON) throws CGException {
		LOGGER.info(" Generating Asset JSON File ");
		String bpName = "";
		PrintWriter printWriter = null;
		FileWriter fileWriter = null;
		try {
			String filePath = env.getProperty("nifi_configpath");
			JSONObject inputJSONobj = new JSONObject(inputJSON);
			bpName = inputJSONobj.getJSONObject("data").getString("assetName");
			String fileName = filePath.concat(bpName).concat(CGConstants.JSON_FILE_EXTENSION);
			fileWriter = new FileWriter(fileName, false);
			printWriter = new PrintWriter(fileWriter);
			printWriter.println("" + inputJSONobj.getJSONObject("data").toString());
		} catch (IOException e) {
			throw new CGException("IO Exception occured creating config file::", e);
		} catch (JSONException e) {
			throw new CGException("IO Exception occured creating config file::", e);
		} finally {
			printWriter.close();
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException e) {
				LOGGER.error("IO Exception occured while closing config file.");
			}
		}
	}

}
