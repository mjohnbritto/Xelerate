package com.suntecgroup.bpruntime.dao;

import java.util.List;

import com.suntecgroup.bpruntime.bean.adminconsole.Asset;
import com.suntecgroup.bpruntime.bean.adminconsole.DeployedTemplate;

public interface DeploymentDAO {

	public boolean deployBPAsset(Asset bpAsset);

	public boolean updateDeployedTemplateDetails(DeployedTemplate deployedTemplate, boolean tryOnce);

	public List<DeployedTemplate> getAllDeployedTemplates();

	public DeployedTemplate getDeployedTemplate(String department, String module, String release, int artifactId,
			String assetType, String assetName);

	public int getLatestTemplateVersion(DeployedTemplate deployedTemplate, boolean tryOnce);

}
