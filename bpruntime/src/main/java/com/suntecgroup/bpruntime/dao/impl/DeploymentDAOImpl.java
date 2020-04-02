package com.suntecgroup.bpruntime.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.suntecgroup.bpruntime.bean.adminconsole.Asset;
import com.suntecgroup.bpruntime.bean.adminconsole.DeployedTemplate;
import com.suntecgroup.bpruntime.dao.DeploymentDAO;
import com.suntecgroup.bpruntime.model.AssetRequest;
import com.suntecgroup.bpruntime.model.AssetResponse;
import com.suntecgroup.bpruntime.model.Context;
import com.suntecgroup.bpruntime.model.ContextParameters;
import com.suntecgroup.bpruntime.model.DeployedTemplateRequest;
import com.suntecgroup.bpruntime.model.DeployedTemplateResponse;

@Repository
public class DeploymentDAOImpl implements DeploymentDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentDAOImpl.class);

	@Autowired
	@Qualifier("nifiBean")
	private RestTemplate restTemplate;
	
	@Autowired
	Environment environment;

	@Override
	public boolean deployBPAsset(Asset bpAsset) {
		try {			
			List<Asset> assetList = new ArrayList<Asset>();
			bpAsset.setAssetVersion(bpAsset.getVersion());
			assetList.add(bpAsset);			
			AssetRequest assetRequest = new AssetRequest();
			assetRequest.setDeployedAsset(assetList);
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			final HttpEntity<?> request = new HttpEntity<>(assetRequest, headers);
			String url = environment.getProperty("xbmc.datastore.bs.DeployedAssetBS.save");
			restTemplate.postForEntity(url, request, AssetResponse.class);		
			return true;			
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
			return false;
		}
	}
	
	public boolean updateDeployedTemplateDetails(DeployedTemplate deployedTemplate, boolean tryOnce) {
		try {			
			List<DeployedTemplate> deployedList = new ArrayList<DeployedTemplate>();
			deployedList.add(deployedTemplate);			
			DeployedTemplateRequest deployedTemplateRequest = new DeployedTemplateRequest();
			deployedTemplateRequest.setDeployedTemplate(deployedList);
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			final HttpEntity<?> request = new HttpEntity<>(deployedTemplateRequest, headers);
			String url = environment.getProperty("xbmc.datastore.bs.DeployedTemplateBS.save");
			LOGGER.info("Request body from updateDeployedTemplateDetails: " + new Gson().toJson(request.getBody()));
			restTemplate.postForEntity(url, request, DeployedTemplateResponse.class);
			return true;
		} catch (ResourceAccessException resourceAccessException) {
			try {
				if (tryOnce) {
					Thread.sleep(1000);
					return updateDeployedTemplateDetails(deployedTemplate, false);
				} else {
					LOGGER.error("Exception occurred! ", resourceAccessException);
					return false;
				}
			} catch (InterruptedException exception) {
				LOGGER.error("Exception occurred:" + exception.getMessage(), exception);
				return false;
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
			return false;
		}
	}

	@Override
	public List<DeployedTemplate> getAllDeployedTemplates() {
		try {
			DeployedTemplateRequest deployedTemplateRequest = new DeployedTemplateRequest();
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			final HttpEntity<?> request = new HttpEntity<>(deployedTemplateRequest, headers);
			String url = environment.getProperty("xbmc.datastore.bs.DeployedTemplateBS.findAll");
			ResponseEntity<DeployedTemplateResponse> deployedTemplateResponse = restTemplate.postForEntity(url, 
					request, DeployedTemplateResponse.class);			
			List<DeployedTemplate> deployedTemplateList = deployedTemplateResponse.getBody().getDeployedTemplate();
			if (null != deployedTemplateList && deployedTemplateList.size() > 0) {
				return deployedTemplateList;
			}
			return null;
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
			return null;
		}
	}

	@Override
	public DeployedTemplate getDeployedTemplate(String department, String module, String release, int artifactId,
			String assetType, String assetName) {
		try {
			DeployedTemplateRequest deployedTemplateRequest = new DeployedTemplateRequest();
			Context context = new Context();
			ContextParameters contextParameters = new ContextParameters();
			contextParameters.setADepart(department);
			contextParameters.setAMod(module);
			contextParameters.setARelease(release);
			contextParameters.setAAssetName(assetName);
			contextParameters.setAAssettype(assetType);
			context.setContextParameters(contextParameters);
			deployedTemplateRequest.setContext(context);
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			final HttpEntity<?> request = new HttpEntity<>(deployedTemplateRequest, headers);
			String url = environment.getProperty("xbmc.datastore.bs.DeployedTemplateBS.departmentand4more");
			ResponseEntity<DeployedTemplateResponse> deployedTemplateResponse = restTemplate.postForEntity(url, 
					request, DeployedTemplateResponse.class);
			List<DeployedTemplate> deployedTemplateList = deployedTemplateResponse.getBody().getDeployedTemplate();
			if (deployedTemplateList != null && deployedTemplateList.size() > 0) {
				return deployedTemplateList.get(0);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred:", exception);
		}
		return null;
	}

	@Override
	public int getLatestTemplateVersion(DeployedTemplate deployedTemplate, boolean tryOnce) {
		int version = -1;
		try {
						
			String regExp = deployedTemplate.getAssetName() + "_";
			DeployedTemplateRequest deployedTemplateRequest = new DeployedTemplateRequest();
			Context context = new Context();
			ContextParameters contextParameters = new ContextParameters();
			contextParameters.setARegularexpression(regExp);
			context.setContextParameters(contextParameters);
			deployedTemplateRequest.setContext(context);
			
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			final HttpEntity<?> request = new HttpEntity<>(deployedTemplateRequest, headers);
			String url = environment.getProperty("xbmc.datastore.bs.DeployedTemplateBS.assetNamestartswith");
			ResponseEntity<DeployedTemplateResponse> responseEntity = restTemplate.postForEntity(url, request, DeployedTemplateResponse.class);
			List<DeployedTemplate> deployedTemplateList = responseEntity.getBody().getDeployedTemplate();
			
			if (deployedTemplateList != null && deployedTemplateList.size() > 0) {
				String name = deployedTemplateList.get(deployedTemplateList.size() - 1).getBpName();
				String[] fragments = name.split("_");
				String ver = fragments[fragments.length - 1];
				return Integer.parseInt(ver.substring(1));
			} else {
				return 0;
			}
		} catch (ResourceAccessException resourceAccessException) {
			try {
				if (tryOnce) {
					System.out.println("ResourceAccessException error happended");
					Thread.sleep(1000);
					return getLatestTemplateVersion(deployedTemplate, false);
				} else {
					LOGGER.error("Exception occurred! ", resourceAccessException);
					return version;
				}
			} catch (InterruptedException exception) {
				LOGGER.error("Exception occurred:" + exception.getMessage(), exception);
				return version;
			}
		} catch (Exception exception) {
			if (exception instanceof HttpClientErrorException) {
				HttpClientErrorException  error = (HttpClientErrorException) exception;
				if (400 == error.getStatusCode().value()) {
					return 0;
				}
			} else {
				LOGGER.error("Exception occurred:", exception);
			}
		}
		return version;
	}


}
