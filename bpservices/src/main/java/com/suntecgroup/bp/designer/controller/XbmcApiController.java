package com.suntecgroup.bp.designer.controller;

import javax.validation.Valid;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.bp.designer.model.AssetComments;
import com.suntecgroup.bp.designer.model.Response;
import com.suntecgroup.bp.designer.model.SendForReview;
import com.suntecgroup.bp.designer.model.UserDetails;
import com.suntecgroup.bp.designer.services.BPDesignerServiceInterface;
import com.suntecgroup.xbmc.service.model.buildstatus.BuildStatus;

/**
 * BPDesignerController class is a rest controller and it handles rest
 * Operations. It accepts incoming requests and sends the response.
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/business/process")
public class XbmcApiController {
	@Autowired
	private Environment env;

	private static final Logger LOGGER = LoggerFactory.getLogger(BPDesignerController.class);

	@Autowired
	private BPDesignerServiceInterface bpDesignerService;

	/**
	 * This method will fetch the Business Entity Types from External API and
	 * returns the response to the UI.
	 * 
	 * @param department
	 *            - holds the department data of String type
	 * @param module
	 *            - holds the module data of String type
	 * @param release
	 *            - holds the release data of String type
	 * @param beName
	 *            - holds the beName data of String type
	 * @return response object containing Business Entity details
	 */
	@GetMapping(value = "/be/types")
	public Response<?> getInputBETypes(@RequestParam(value = "department") String department,
			@RequestParam(value = "module", required = false) String module,
			@RequestParam(value = "release", required = false) String release,
			@RequestParam(value = "bename", required = false) String bename,
			@RequestHeader("Authorization") final String token) {
		LOGGER.info(" GetBETypes Request::  ".concat(department + "," + module + "," + release + "," + bename));
		Response<?> response = bpDesignerService.getBEType(token, department, module, release, bename);
		return response;
	}
	
	
	/**
	 * This method will fetch the Business Entity Types from External Effective
	 * API and returns the response to the UI.
	 * 
	 * @param department
	 *            - holds the department data of String type
	 * @param module
	 *            - holds the module data of String type
	 * @param release
	 *            - holds the release data of String type
	 * @param beName
	 *            - holds the beName data of String type
	 * @return response object containing Business Entity details
	 */
	@GetMapping(value = "/be/effective/types")
	public List<Map<String, Object>> getEffectiveInputBETypes(@RequestParam(value = "department") String department,
			@RequestParam(value = "module", required = false) String module,
			@RequestParam(value = "release", required = false) String release,
			@RequestParam(value = "bename", required = false) String bename,
			@RequestParam(value = "pmsId", required = false) Long pmsId,
			@RequestHeader("Authorization") final String token) {
		LOGGER.info(" getEffectiveInputBETypes Request::  "
				.concat(department + "," + module + "," + release + "," + bename));
		List<Map<String, Object>> resultResponseList = bpDesignerService.getEffectiveBEType(token, department, module, release, bename, pmsId);
		return resultResponseList;
	}
	

	/**
	 * This method will fetch the Business Service Details from the External API
	 * and returns response to UI.
	 * 
	 * @param department
	 *            - holds the department data of String type
	 * @param module
	 *            - holds the module data of String type
	 * @param release
	 *            - holds the release data of String type
	 * @param beName
	 *            - holds the beName data of String type
	 * @return response object containing Business Service details
	 */
	@GetMapping(value = "/bs/details")
	public Response<?> getBServiceDetails(@RequestParam("department") String department,
			@RequestParam(value = "module", required = false) String module,
			@RequestParam(value = "release", required = false) String release,
			@RequestParam(value = "bsname", required = false) String bsname,
			@RequestParam(value = "pmsId", required = false) Long pmsId,
			@RequestHeader("Authorization") final String token) {
		LOGGER.info(" GetBSservicesRequest ::  ");
		return bpDesignerService.getBSServiceDetails(token, department, module, release, bsname, pmsId);
	}

	/**
	 * This method will fetch the Business Service Details from the External API
	 * and returns response to UI.
	 * 
	 * @param department
	 *            - holds the department data of String type
	 * @param module
	 *            - holds the module data of String type
	 * @param release
	 *            - holds the release data of String type
	 * @param beName
	 *            - holds the beName data of String type
	 * @return response object containing Business Service details
	 */
	@GetMapping(value = "/bs/effective/details")
	public Response<?> getBSEffectiveDetails(@RequestParam("department") String department,
			@RequestParam(value = "module", required = false) String module,
			@RequestParam(value = "release", required = false) String release,
			@RequestParam(value = "bsname", required = false) String bsname,
			@RequestParam(value = "pmsId", required = false) Long pmsId,
			@RequestHeader("Authorization") final String token) {
		LOGGER.info(" getBSEffectiveDetails ::  ");
		return bpDesignerService.getBSEffectiveDetails(token, department, module, release, bsname, pmsId);
	}

	/**
	 * This method will be used to validate the access token
	 * 
	 * @param user
	 * @return success/failure status message
	 */
	@PostMapping(value = "/xbmc/validate/access/token")
	public Response<?> validateAccessToken(@RequestBody UserDetails user,
			@RequestHeader("Authorization") final String token) {
		LOGGER.info(" Validate access token... ");
		return bpDesignerService.validateAccessToken(token, user);
	}

	/**
	 * This method will be used to notify the xbmc to take the review of
	 * business process
	 * 
	 * @param review
	 * @return success/failure status message
	 */
	@PostMapping(value = "/xbmc/review")
	public Response<?> sendForReview(@Valid @RequestBody final SendForReview review,
			@RequestHeader("Authorization") final String token) {
		LOGGER.info("Sending BP for review process...");
		return bpDesignerService.review(token, review);
	}

	/**
	 * This method will be used to update the status of review
	 * 
	 * @param artifactid
	 * @param status
	 * @return success/failure status message
	 */
	@PutMapping(value = "/xbmc/review/status/{artifactid}")
	public Response<?> updateReviewStatus(@PathVariable("artifactid") String artifactid,
			@Valid @RequestBody final AssetComments assetComments, @RequestHeader("Authorization") final String token) {
		LOGGER.info("update review status ..." + artifactid);
		return bpDesignerService.sendXbmcReviewStatus(token, artifactid, assetComments);
	}

	/**
	 * This method will be used to update the asset build job status
	 * 
	 * @param buildStatus
	 * @return success/failure status message
	 */
	@PostMapping(value = "/xbmc/api/asset/build")
	public Response<?> updateBuildJobStatus(@Valid @RequestBody final BuildStatus buildStatus) {
		LOGGER.info("Updating asset build job status to XBMC...");
		return bpDesignerService.updateBuildJobStatus(buildStatus);
	}

}
