package com.suntecgroup.nifi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowResponseXml;
import com.suntecgroup.nifi.frontend.bean.BPFlowUI;
import com.suntecgroup.nifi.frontend.bean.BPValidation;
import com.suntecgroup.nifi.service.CGServiceInterface;

/**
 * BP Controller class is a rest controller and it handles rest Operations. It
 * accepts incoming requests and sends the response.
 */
@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/nifi")
public class CGController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CGController.class);

	@Autowired
	private CGServiceInterface bpService;

	/**
	 * This method uses for generating the bpflow xml using incoming request and
	 * returns the information
	 * 
	 * @param departmentName
	 *            - accepts departmentName request object
	 * @param moduleName
	 *            - accepts module name of the flow
	 * @param bpname
	 *            - accepts bpname of the flow
	 * @return response with bpflow generated template information
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/generate/template/{department}/{module}/{release}/{assettype}/{assetname}")
	public ResponseEntity<BPFlowResponseXml> getFlowTemplate(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("assettype") String assetType, @PathVariable("assetname") String assetName)

			throws CGException {
		LOGGER.info("creating nifi template :: controller :: started");
		BPFlowResponseXml response = bpService.createNifiTemplate(department, module, release, assetType, assetName);
		return new ResponseEntity<BPFlowResponseXml>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/validate/flow", consumes = "application/json")
	public ResponseEntity<BPValidation> validate(@RequestBody BPFlowUI bPFlowRequest) throws CGException {
		LOGGER.info("validate started");
		BPValidation response = bpService.validateInputJson(bPFlowRequest);
		return new ResponseEntity<BPValidation>(response, HttpStatus.OK);

	}

}
