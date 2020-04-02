package com.suntec.cli.service;

public interface DeploymentCommandService {

	public Object deployTemplate(String assetName, String artifactPath, String runtimeServer, int version);

	public Object instantiateTemplate(String runtimeServer, String templateId);

	public Object uploadTemplateProperties(String assetName, String artifactPath, String runtimeServer,
			String processGroupId);

	public Object deployAsset(String assetName, String artifactPath, String runtimeServer, int version);

	public Object updateDeployedTemplateDetails(String assetName, String artifactPath, String runtimeServer,
			String templateId, String processGroupId, int version);

	public int performTemplateVersioning(String assetName, String artifactPath, String runtimeServer);
}
