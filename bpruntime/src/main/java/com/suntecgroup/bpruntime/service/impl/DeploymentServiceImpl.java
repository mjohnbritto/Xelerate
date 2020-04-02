package com.suntecgroup.bpruntime.service.impl;

import java.util.HashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.Asset;
import com.suntecgroup.bpruntime.bean.adminconsole.DeployedTemplate;
import com.suntecgroup.bpruntime.bean.adminconsole.Status;
import com.suntecgroup.bpruntime.dao.DeploymentDAO;
import com.suntecgroup.bpruntime.dao.SessionManagerDao;
import com.suntecgroup.bpruntime.service.DeploymentService;
import com.suntecgroup.bpruntime.service.NifiService;

@Service
public class DeploymentServiceImpl implements DeploymentService {

	private static Logger LOGGER = LoggerFactory.getLogger(DeploymentServiceImpl.class);

	@Autowired
	Environment environment;

	@Autowired
	@Qualifier("nifiBean")
	private RestTemplate restTemplate;

	@Autowired
	NifiService nifiService;

	@Autowired
	SessionManagerDao sessionManagerDao;

	@Autowired
	DeploymentDAO deploymentDAO;

	@Override
	public Object deployTemplate(String xmlContent) {
		return nifiService.deployTemplate(xmlContent);
	}

	@Override
	public Object instantiateTemplate(String templateId) {
		try {
			String processGroupId = nifiService.instantiateTemplate(templateId);
			if (null != processGroupId) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("processGroupId", processGroupId);

				return new ResponseEntity<ApiResponse<String>>(new ApiResponse<String>(Status.SUCCESS.getStatusCode(),
						Status.SUCCESS, null, responseJson.toString()), HttpStatus.OK);
			} else {
				return new ResponseEntity<ApiResponse<String>>(new ApiResponse<String>(Status.FAILURE.getStatusCode(),
						Status.FAILURE, "Could not instantiate the template!", null), HttpStatus.CONFLICT);
			}
		} catch (Exception exception) {
			LOGGER.error("Exceptino occurred::", exception);
			return new ResponseEntity<ApiResponse<String>>(new ApiResponse<String>(Status.FAILURE.getStatusCode(),
					Status.FAILURE, "Something went wrong!", null), HttpStatus.CONFLICT);
		}
	}

	@Override
	public Object uploadTemplateProperties(String processGroupId, HashMap<String, String> propertiesMap) {
		return nifiService.updateVariableRegistry(processGroupId, propertiesMap);
	}

	@Override
	public Object deployBPAsset(Asset bpAsset) {
		boolean status = deploymentDAO.deployBPAsset(bpAsset);
		if (status) {
			return new ResponseEntity<ApiResponse<Object>>(new ApiResponse<Object>(Status.SUCCESS.getStatusCode(),
					Status.SUCCESS, "Asset deployment successful!", null), HttpStatus.OK);
		} else {
			return new ResponseEntity<ApiResponse<Object>>(new ApiResponse<Object>(Status.FAILURE.getStatusCode(),
					Status.FAILURE, "Something went wrong!", null), HttpStatus.CONFLICT);
		}
	}

	@Override
	public Object updateDeployedTemplateDetails(DeployedTemplate deployedTemplate) {
		boolean status = deploymentDAO.updateDeployedTemplateDetails(deployedTemplate, true);
		if (status) {
			return new ResponseEntity<ApiResponse<Object>>(new ApiResponse<Object>(Status.SUCCESS.getStatusCode(),
					Status.SUCCESS, "Template details updation successful!", null), HttpStatus.OK);
		} else {
			return new ResponseEntity<ApiResponse<Object>>(new ApiResponse<Object>(Status.FAILURE.getStatusCode(),
					Status.FAILURE, "Something went wrong!", null), HttpStatus.CONFLICT);
		}
	}

	@Override
	public Object getLatestTemplateVersion(DeployedTemplate deployedTemplate) {
		int version = deploymentDAO.getLatestTemplateVersion(deployedTemplate, true);
		if (version >= 0) {
			return new ResponseEntity<ApiResponse<String>>(new ApiResponse<String>(Status.SUCCESS.getStatusCode(),
					Status.SUCCESS, "Fetch successful!", "" + version), HttpStatus.OK);
		} else {
			return new ResponseEntity<ApiResponse<String>>(new ApiResponse<String>(Status.FAILURE.getStatusCode(),
					Status.FAILURE, "Fetch failed!", "" + version), HttpStatus.CONFLICT);
		}
	}
}
