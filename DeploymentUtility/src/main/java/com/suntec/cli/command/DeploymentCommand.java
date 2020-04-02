package com.suntec.cli.command;

import java.util.Locale;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.suntec.cli.service.DeploymentCommandService;
import com.suntec.cli.utils.Constants;

@ShellComponent
public class DeploymentCommand {

	@Autowired
	private DeploymentCommandService deploymentCommandService;

	Logger LOGGER = LoggerFactory.getLogger(DeploymentCommand.class);

	@ShellMethod("Deploy the NiFi template to NiFi cluster")
	public void deployTemplate(@ShellOption(value = { "-A", "--asset" }) String assetName,
			@ShellOption(value = { "-P", "--path" }) String artifactPath,
			@ShellOption(value = { "-R", "--runtimeserver" }) String runtimeServer) {

		LOGGER.info("=====> Deploying the template \"{}\" from \"{}\" to \"{}\"",
				new Object[] { assetName, artifactPath, runtimeServer });
		try {

			int newVersion = deploymentCommandService.performTemplateVersioning(assetName, artifactPath, runtimeServer);
			
			if (newVersion < 1) {
				throw new Exception("Template versioning failed!");
			}

			// STEP 1: Calling API to upload the NiFI template(XML file)
			JSONObject responseFromService = (JSONObject) deploymentCommandService.deployTemplate(assetName,
					artifactPath, runtimeServer, newVersion);
			if (null != responseFromService
					&& Constants.SUCCESS.equalsIgnoreCase(responseFromService.get(Constants.STATUS).toString())) {
				LOGGER.info("=====> \"{}\" template uploading is successful", new Object[] { assetName });
				String templateId = responseFromService.get("templateId").toString();

				// STEP 2: Calling API to instantiate the NiFI template,
				// uploaded in step1
				responseFromService = (JSONObject) deploymentCommandService.instantiateTemplate(runtimeServer,
						templateId);
				if (null != responseFromService
						&& Constants.SUCCESS.equalsIgnoreCase(responseFromService.get(Constants.STATUS).toString())) {
					LOGGER.info("=====> \"{}\" template instantiation is successful", new Object[] { assetName });
					String processGroupId = responseFromService.get("processGroupId").toString();

					// STEP 3: Calling API to upload the global properties
					responseFromService = (JSONObject) deploymentCommandService.uploadTemplateProperties("suntec",
							artifactPath, runtimeServer, "root");
					if (null != responseFromService && Constants.SUCCESS
							.equalsIgnoreCase(responseFromService.get(Constants.STATUS).toString())) {
						LOGGER.info("=====> Global properties uploading is successful");
						// STEP 4: Calling API to upload the specific template
						// properties
						responseFromService = (JSONObject) deploymentCommandService.uploadTemplateProperties(assetName,
								artifactPath, runtimeServer, processGroupId);
						if (null != responseFromService && Constants.SUCCESS
								.equalsIgnoreCase(responseFromService.get(Constants.STATUS).toString())) {
							LOGGER.info("=====> \"{}\" template properties uploading is successful",
									new Object[] { assetName });
							// STEP 5: Calling API to upload the Asset to
							// runtime server storage
							responseFromService = (JSONObject) deploymentCommandService.deployAsset(assetName,
									artifactPath, runtimeServer, newVersion);
							if (null != responseFromService && Constants.SUCCESS
									.equalsIgnoreCase(responseFromService.get(Constants.STATUS).toString())) {
								LOGGER.info("=====> \"{}\" asset uploading is successful", new Object[] { assetName });
								// STEP 6: Calling API to update the deployed
								// asset&template details to runtime server
								responseFromService = (JSONObject) deploymentCommandService
										.updateDeployedTemplateDetails(assetName, artifactPath, runtimeServer,
												templateId, processGroupId, newVersion);
								if (null != responseFromService && Constants.SUCCESS
										.equalsIgnoreCase(responseFromService.get(Constants.STATUS).toString())) {
									LOGGER.info("=====> Deployed template \"{}\"  details updation is successful",
											new Object[] { assetName });
									LOGGER.info("=====> Deployment completed successfully!");
								} else {
									LOGGER.error(
											"Deployment failed due to error in updating the deployed template details to runtime server!");
								}
							} else {
								LOGGER.error(
										"Deployment failed due to error in uploading the Asset to runtime server storage!");
							}
						} else {
							LOGGER.error(
									"Deployment failed due to error in uploading the template \"{}\" properties: {}!",
									new Object[] { assetName });
						}
					} else {
						LOGGER.error("Deployment failed due to error in uploading the global properties!");
					}
				} else {
					LOGGER.error("Deployment failed due to error in instantiating the template: {}!",
							new Object[] { assetName });
				}
			} else {
				LOGGER.error("Deployment failed due to error in deploying the template: {}!",
						new Object[] { assetName });
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred: ", exception);
			System.exit(-1);
		}
		System.exit(0);
	}

	@ShellMethod("Translate text from one language to another.")
	public String translate(@ShellOption(value = { "", "" }) String text,
			@ShellOption(defaultValue = "en_US") Locale from, @ShellOption() Locale to) {
		// invoke service
		try {
			System.out.println("Hi from service");
		} catch (Exception exception) {
			LOGGER.error("Exception occurred: ", exception);
			System.exit(-1);
		}
		System.exit(0);
		return null;
	}

	@ShellMethod("Add two integers")
	public int add(@ShellOption(value = "-a") int a, @ShellOption(value = "-b") int b) {
		return (a + b);
	}
}