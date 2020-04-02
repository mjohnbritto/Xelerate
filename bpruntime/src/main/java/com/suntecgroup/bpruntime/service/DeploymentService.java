package com.suntecgroup.bpruntime.service;

import java.util.HashMap;

import com.suntecgroup.bpruntime.bean.adminconsole.Asset;
import com.suntecgroup.bpruntime.bean.adminconsole.DeployedTemplate;

public interface DeploymentService {

	public Object deployTemplate(String xmlContent);

	public Object instantiateTemplate(String templateId);

	public Object uploadTemplateProperties(String processGroupId, HashMap<String, String> propertiesMap);

	public Object deployBPAsset(Asset bpAsset);

	public Object updateDeployedTemplateDetails(DeployedTemplate deployedTemplate);

	Object getLatestTemplateVersion(DeployedTemplate deployedTemplate);
}
