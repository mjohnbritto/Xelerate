/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */

package com.suntecgroup.bpconf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.bpconf.model.BpConfiguration;
import com.suntecgroup.bpconf.model.Response;
import com.suntecgroup.bpconf.services.BPConfServiceInterface;
import com.suntecgroup.bpconf.util.BPConfUtils;

/*
 * This class is for handling all the rest api calls to fetch and/or update the properties of the BP.conf file
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@RestController
@RequestMapping(value = "/api/bpconf")
public class BPConfigController {

	@Autowired
	private BPConfServiceInterface bpConfServiceInterface;
	private static final Logger LOGGER = LoggerFactory.getLogger(BPConfigController.class);

	// Get all process variable or all properties
	@GetMapping(value = "/getConfiguration/{bpName}/{confType}")
	public Response<?> getConfiguration(@PathVariable("bpName") String bpName,
			@PathVariable("confType") String confType) {
		LOGGER.info("IN /getConfiguration/{bpName}/{confType} api");
		return bpConfServiceInterface.getConfiguration(bpName, confType);
	}

	// Get all properties for a operator
	@GetMapping(value = "/getConfiguration/{bpName}/operator/{operatorKey}/property")
	public Response<?> getConfigurationForAOperator(@PathVariable("bpName") String bpName,
			@PathVariable("operatorKey") String operatorKey) {
		LOGGER.info("IN /getConfiguration/{bpName}/operator/{operatorKey}/property api");
		return bpConfServiceInterface.getConfigurationForParticularOperator(bpName, operatorKey);
	}

	@PostMapping(value = "/saveConfiguration")
	public Response<?> setProperty(@RequestBody String requestPayload) {
		BpConfiguration bpConfiguration = BPConfUtils.convertJsonStringToJava(requestPayload);
		LOGGER.info("IN /saveConfiguration");
		return bpConfServiceInterface.saveConfiguration(bpConfiguration.getBpName(),
				bpConfiguration.getConfigurations());
	}

	@GetMapping(value = "/getIsPropertyExist")
	public Response<?> isPropertyExisting(@RequestParam(value = "bpName", required = true) String bpName,
			@RequestParam(value = "propertyName", required = true) String propertyName) {
		LOGGER.info("IN /getIsPropertyExist api");
		return bpConfServiceInterface.isPropertyExisting(bpName, propertyName);
	}

}
