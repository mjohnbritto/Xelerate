package com.suntecgroup.bpruntime.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.bpruntime.bean.adminconsole.Asset;
import com.suntecgroup.bpruntime.bean.adminconsole.DeployedTemplate;
import com.suntecgroup.bpruntime.service.DeploymentService;

@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/bpruntime/deployment")
public class DeploymentController {

	Logger LOGGER = LoggerFactory.getLogger(DeploymentController.class);
	@Autowired
	DeploymentService deploymentService;

	@PostMapping(value = "/templates/upload", consumes = "multipart/form-data")
	public Object deployTemplate(@RequestParam("template") String xmlContent) {

		try {
			return deploymentService.deployTemplate(xmlContent);
		} catch (Exception exception) {
			LOGGER.error("Exceptino occurred::", exception);
		}
		return null;
	}

	@PostMapping(value = "/templates/{templateId}/instantiate", consumes = "application/json")
	public Object instantiateTemplate(@PathVariable String templateId) {

		try {
			return deploymentService.instantiateTemplate(templateId);
		} catch (Exception exception) {
			LOGGER.error("Exceptino occurred::", exception);
		}
		return null;
	}

	@PutMapping(value = "/template-properties/upload/{processGroupId}", consumes = "application/json")
	public Object uploadTemplateProperties(@PathVariable String processGroupId,
			@RequestBody HashMap<String, String> propertiesMap) {

		try {
			return deploymentService.uploadTemplateProperties(processGroupId, propertiesMap);
		} catch (Exception exception) {
			LOGGER.error("Exceptino occurred::", exception);
		}
		return null;
	}

	@PostMapping(value = "/deploy-asset")
	public Object deployBPAsset(@RequestBody Asset bpAsset) {
		return deploymentService.deployBPAsset(bpAsset);
	}

	@PostMapping(value = "/update-deployed-template-details")
	public Object updateDeployedTemplateDetails(@RequestBody DeployedTemplate deployedTemplate) {
		return deploymentService.updateDeployedTemplateDetails(deployedTemplate);
	}
	
	@PostMapping(value = "/get-latest-template-version")
	public Object getLatestTemplateVersion(@RequestBody DeployedTemplate deployedTemplate) {
		return deploymentService.getLatestTemplateVersion(deployedTemplate);
	}
	
}
