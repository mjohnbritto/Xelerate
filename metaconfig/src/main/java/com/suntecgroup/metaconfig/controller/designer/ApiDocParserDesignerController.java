package com.suntecgroup.metaconfig.controller.designer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.suntecgroup.metaconfig.exception.ApiDocParserException;
import com.suntecgroup.metaconfig.model.ApiDocResponse;
import com.suntecgroup.metaconfig.services.impl.ApiDocParserService;
import com.suntecgroup.metaconfig.validator.ApiDocValidator;

import io.swagger.oas.inflector.processors.JsonNodeExampleSerializer;
import io.swagger.util.Json;

/**
 * Controller responsible for handling all the API document parsing URL's and
 * returns back the response.
 * 
 * @author murugeshpd
 *
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3000)
@RequestMapping("/apidoc")
public class ApiDocParserDesignerController {

	private static final Logger LOG = LoggerFactory.getLogger(ApiDocParserDesignerController.class);

	@PostConstruct
	public void init() {
		// JSON serializer responsible for converting the Open API JSON schema to sample JSON.
		final SimpleModule simpleModule = new SimpleModule().addSerializer(new JsonNodeExampleSerializer());
		Json.mapper().registerModule(simpleModule);
	}
	 
	@Autowired
	private ApiDocParserService apiDocParserService;
	
	@Autowired
	private ApiDocValidator apiDocValidator;
	
	@PostMapping(produces="application/json", path="/parse")
	public String parseOpenApiSpec(@RequestParam(required=false) MultipartFile file, @RequestParam(required=false) String operation,
			@RequestParam(required=false, defaultValue="application/json") String contentType, @RequestParam(required=false) String apiDocUrl) throws ApiDocParserException {
		LOG.info("Parsing API Doc started for content type {} - operation {} - ApiDocUrl {} - Yaml File {}",
				contentType, operation, apiDocUrl, (file != null) ? file.getOriginalFilename() : null);
		apiDocValidator.validate(file, operation, contentType, apiDocUrl);
		final ApiDocResponse apiDocResponse = apiDocParserService.parse(file, operation, contentType, apiDocUrl);
		return Json.pretty(apiDocResponse);
	}
}

