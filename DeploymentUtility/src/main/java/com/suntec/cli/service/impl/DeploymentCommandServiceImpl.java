package com.suntec.cli.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.suntec.cli.bean.DeployedTemplate;
import com.suntec.cli.service.DeploymentCommandService;
import com.suntec.cli.utils.Constants;

@Component
public class DeploymentCommandServiceImpl implements DeploymentCommandService {

	@Autowired
	private Environment environment;

	@Autowired
	private RestTemplate restTemplate;

	Logger LOGGER = LoggerFactory.getLogger(DeploymentCommandServiceImpl.class);

	@Override
	public Object deployTemplate(String assetName, String artifactPath, String runtimeServer, int version) {
		try {
			JSONObject customizedResponse = new JSONObject();
			String xmlContent = readFileContent(artifactPath, assetName, "xml");
			xmlContent = versionXMLTemplateArtifact(assetName, artifactPath, runtimeServer, version);
			if (null == xmlContent || StringUtils.isEmpty(xmlContent)) {
				LOGGER.error("File content is empty or null, couldn't proceed further!");
				customizedResponse.put(Constants.STATUS, Constants.FAILURE);
				return customizedResponse;
			}

			String url = runtimeServer + environment.getProperty("template.upload");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
			parts.add("template", xmlContent);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
					parts, headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject apiResponse = new JSONObject(responseEntity.getBody());
				apiResponse = new JSONObject(apiResponse.get("responseDetails").toString());
				String templateId = apiResponse.get("templateId").toString();

				customizedResponse.put(Constants.STATUS, Constants.SUCCESS);
				customizedResponse.put("templateId", templateId);
			} else {
				customizedResponse.put(Constants.STATUS, Constants.FAILURE);
			}
			return customizedResponse;
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
			return null;
		}
	}

	@Override
	public Object instantiateTemplate(String runtimeServer, String templateId) {
		try {
			JSONObject customizedResponse = new JSONObject();
			String url = runtimeServer + environment.getProperty("template.instantiate");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class, templateId);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				JSONObject apiResponse = new JSONObject(responseEntity.getBody());
				apiResponse = new JSONObject(apiResponse.get("responseDetails").toString());
				String processGroupId = apiResponse.get("processGroupId").toString();

				customizedResponse.put(Constants.STATUS, Constants.SUCCESS);
				customizedResponse.put("processGroupId", processGroupId);
			} else {
				customizedResponse.put(Constants.STATUS, Constants.FAILURE);
			}
			return customizedResponse;
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
			return null;
		}
	}

	@Override
	public Object uploadTemplateProperties(String assetName, String artifactPath, String runtimeServer,
			String processGroupId) {
		try {
			JSONObject customizedResponse = new JSONObject();
			JSONObject propertiesToUpload = readPropertiesFromFile(artifactPath, assetName, "conf.properties");
			if (null == propertiesToUpload || propertiesToUpload.length() < 1) {
				LOGGER.error("Properties to upload is empty or null, couldn't proceed further!");
				customizedResponse.put(Constants.STATUS, Constants.FAILURE);
				return customizedResponse;
			}
			String url = runtimeServer + environment.getProperty("template.properties.upload");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(propertiesToUpload.toString(), headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,
					String.class, processGroupId);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				customizedResponse.put(Constants.STATUS, Constants.SUCCESS);
			} else {
				customizedResponse.put(Constants.STATUS, Constants.FAILURE);
			}
			return customizedResponse;
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
			return null;
		}
	}

	@Override
	public Object deployAsset(String assetName, String artifactPath, String runtimeServer, int version) {
		try {
			JSONObject customizedResponse = new JSONObject();
			String asset = readFileContent(artifactPath, assetName, "json");
			asset = versionJSONArtifact(assetName, artifactPath, runtimeServer, version);
			if (null == asset || StringUtils.isEmpty(asset)) {
				LOGGER.error("File content is empty or null, couldn't proceed further!");
				customizedResponse.put(Constants.STATUS, Constants.FAILURE);
				return customizedResponse;
			}
			String url = runtimeServer + environment.getProperty("template.asset.upload");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(asset, headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				customizedResponse.put(Constants.STATUS, Constants.SUCCESS);
			} else {
				customizedResponse.put(Constants.STATUS, Constants.FAILURE);
			}
			return customizedResponse;
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
			return null;
		}
	}

	@Override
	public Object updateDeployedTemplateDetails(String assetName, String artifactPath, String runtimeServer,
			String templateId, String processGroupId, int version) {
		try {
			JSONObject customizedResponse = new JSONObject();

			String asset = readFileContent(artifactPath, assetName, "json");
			asset = versionJSONArtifact(assetName, artifactPath, runtimeServer, version);
			if (null == asset || StringUtils.isEmpty(asset)) {
				LOGGER.error("File content is empty or null, couldn't proceed further!");
				customizedResponse.put(Constants.STATUS, Constants.FAILURE);
				return customizedResponse;
			}

			JSONObject assetObject = new JSONObject(asset);
			DeployedTemplate deployedTemplate = new DeployedTemplate();
			deployedTemplate.setBpName(assetObject.getString("assetName"));
			deployedTemplate.setAssetName(assetObject.getString("assetName"));
			deployedTemplate.setAssetType(assetObject.getString("assetType"));
			deployedTemplate.setDepartment(assetObject.getString("department"));
			deployedTemplate.setModule(assetObject.getString("module"));
			deployedTemplate.setRelease(assetObject.getString("release"));
			deployedTemplate.setArtifactId(assetObject.getInt("artifact_id"));
			deployedTemplate.setBpDescription(deployedTemplate.getDepartment() + "|" + deployedTemplate.getModule()
					+ "|" + deployedTemplate.getRelease() + "|" + deployedTemplate.getAssetType() + "|"
					+ deployedTemplate.getAssetName());
			deployedTemplate.setBpTemplateId(templateId);
			deployedTemplate.setBpGroupId(processGroupId);
			deployedTemplate.setBpStatus(assetObject.getString("status"));

			String url = runtimeServer + environment.getProperty("template.details.update");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<DeployedTemplate> requestEntity = new HttpEntity<DeployedTemplate>(deployedTemplate, headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				customizedResponse.put(Constants.STATUS, Constants.SUCCESS);
			} else {
				customizedResponse.put(Constants.STATUS, Constants.FAILURE);
			}
			return customizedResponse;
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
			return null;
		}
	}

	private String readFileContent(String filePath, String fileName, String extension) {
		String path = "";
		try {
			path = filePath + "/" + fileName + "." + extension;
			String fileContent = new String(Files.readAllBytes(Paths.get(path)));
			return fileContent;
		} catch (Exception exception) {
			LOGGER.error("Exception while reading file content to " + path, exception);
		}
		return null;
	}

	private JSONObject readPropertiesFromFile(String filePath, String fileName, String extension) {
		try {
			JSONObject properties = new JSONObject();
			String path = filePath + "/" + fileName + "." + extension;
			File file = new File(path);
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				int index = line.indexOf("=");
				if (index > -1) {
					properties.put(line.substring(0, index), line.substring(index + 1));
				}
			}
			scanner.close();
			return properties;
		} catch (Exception exception) {
			LOGGER.error("Exception while reading file content", exception);
			return null;
		}
	}
	
	@Override
	public int performTemplateVersioning(String assetName, String artifactPath, String runtimeServer) {
		int version = getNextTemplateVersion(assetName, artifactPath, runtimeServer);
		version++;
		return version;
	}
	
	private int getNextTemplateVersion(String assetName, String artifactPath, String runtimeServer) {
		int version = 1;
		try {
			JSONObject response = null;

			String asset = readFileContent(artifactPath, assetName, "json");
			if (null == asset || StringUtils.isEmpty(asset)) {
				LOGGER.error("Asset JSON File content is empty or null, couldn't proceed further!");
				return -1;
			}

			JSONObject assetObject = new JSONObject(asset);
			DeployedTemplate deployedTemplate = new DeployedTemplate();
			deployedTemplate.setBpName(assetObject.getString("assetName"));
			deployedTemplate.setAssetName(assetObject.getString("assetName"));

			String url = runtimeServer + environment.getProperty("template.details.getLatestTemplateVersion");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<DeployedTemplate> requestEntity = new HttpEntity<DeployedTemplate>(deployedTemplate, headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);
			if (HttpStatus.OK == responseEntity.getStatusCode()) {
				response = new JSONObject(responseEntity.getBody());
				version = Integer.parseInt(response.getString("responseDetails"));
			}
			return version;
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
			return -1;
		}
	}
	
	private String versionXMLTemplateArtifact(String assetName, String artifactPath, String runtimeServer, int version) {
		// update xml
		String xmlContent = readFileContent(artifactPath, assetName, "xml");
		if (null == xmlContent || StringUtils.isEmpty(xmlContent)) {
			LOGGER.error("XML File content is empty or null, couldn't proceed further!");
		} else {
			xmlContent = xmlContent.replaceAll("<name>" + assetName, "<name>" + assetName + "_v" + version);
		}
		return xmlContent;
	}

	private String versionJSONArtifact(String assetName, String artifactPath, String runtimeServer, int version) {
		// update json
		String jsonContent = readFileContent(artifactPath, assetName, "json");
		if (null == jsonContent || StringUtils.isEmpty(jsonContent)) {
			LOGGER.error("JSON File content is empty or null, couldn't proceed further!");
		} else {
			jsonContent = jsonContent.replaceAll(assetName, assetName + "_v" + version);
		}
		return jsonContent;
	}

}
