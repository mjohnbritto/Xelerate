package com.suntecgroup.bp.designer.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.bp.designer.model.Asset;
import com.suntecgroup.bp.designer.model.Response;
import com.suntecgroup.bp.designer.services.BPDesignerServiceInterface;

/**
 * BPDesignerController class is a rest controller and it handles rest
 * Operations. It accepts incoming requests and sends the response.
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/business/process")
public class BPDesignerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BPDesignerController.class);

	@Autowired
	private BPDesignerServiceInterface bpDesignerService;

	/**
	 * This method will save the BPFlow data in DB
	 */
	@PostMapping(value = "/save-asset")
	public Response<?> saveBPAsset(@Valid @RequestBody Asset bpAsset) {
		LOGGER.info("save bp asset request... ");
		return bpDesignerService.saveBPAsset(bpAsset);
	}

	/**
	 * This method will fetch the BPFlow data from database and return its
	 * response.
	 * 
	 * @param department,module,release,pms,assetType,assetName
	 * 
	 * @return response with status information
	 */
	@GetMapping(value = "/asset/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}")
	public Response<?> getBPAsset(@PathVariable("department") String department, @PathVariable("module") String module,
			@PathVariable("release") String release, @PathVariable("artifactid") int artifactId,
			@PathVariable("assettype") String assetType, @PathVariable("assetname") String assetName,
			@RequestParam(value = "version", required = false) Object version) {
		LOGGER.info("Get business flow...");
		return bpDesignerService.getBPAsset(department, module, release, artifactId, assetType, assetName, version);
	}

	/**
	 * This method will get and put the BPFlow by
	 * department,module,release,pms,assetType,assetName in the Database.
	 * 
	 * @param department,module,release,pms,assetType,assetName
	 *            - property
	 * @return response with status information
	 */
	 @GetMapping(value = "/deploy-asset-depricated/{department}/{module}/{release}/{assettype}/{assetname}")
	public Response<?> GetandPutBPFlow(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("assettype") String assetType,
			@PathVariable("assetname") String assetName) {
		LOGGER.info("Get business flow...");
		return bpDesignerService.getAndPutBPFlow(department, module, release, assetType, assetName);
	}

	/**
	 * This method will delete the BPFlow by
	 * department,module,release,pms,assetType,assetName in the Database.
	 * 
	 * @param department,module,release,pms,assetType,assetName
	 *            - property
	 * @return response with status information
	 */
	@DeleteMapping(value = "/asset/delete/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}")
	public Response<?> DeleteBP(@PathVariable("department") String department, @PathVariable("module") String module,
			@PathVariable("release") String release, @PathVariable("artifactid") int artifactId,
			@PathVariable("assettype") String assetType, @PathVariable("assetname") String assetName) {
		LOGGER.info("  Request for Deleting BP FlowFile Details from DB :: ");
		return bpDesignerService.deleteBP(department, module, release, artifactId, assetType, assetName);
	}

	/**
	 * This method will give the designer comments
	 */
	@GetMapping(value = "/designer-comments/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}/{version}")
	public Response<?> getDesignerComments(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("artifactid") int artifactId, @PathVariable("assettype") String assetType,
			@PathVariable("assetname") String assetName, @PathVariable(value = "version") int version) {
		LOGGER.info("Get Designer Comments...");
		return bpDesignerService.fetchDesignerComments(department, module, release, artifactId, assetType, assetName, version);
	}

	/**
	 * This method will give the reviewer comments
	 */
	@GetMapping(value = "/reviewer-comments/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}/{version}")
	public Response<?> getReviewerComments(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("artifactid") int artifactId, @PathVariable("assettype") String assetType,
			@PathVariable("assetname") String assetName, @PathVariable(value = "version") int version) {
		LOGGER.info("Get Designer Comments...");
		return bpDesignerService.fetchReviewerComments(department, module, release, artifactId, assetType, assetName, version);
	}

	/**
	 * This method will be used to generate report review
	 * 
	 * @param department,module,release,pms,assetType,assetName
	 */
	@GetMapping(value = "/asset-compare/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}")
	public Response<?> assetCompare(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("artifactid") int artifactId, @PathVariable("assettype") String assetType,
			@PathVariable("assetname") String assetName) {
		LOGGER.info("Get Review Report...");
		return bpDesignerService.reviewReportService(department, module, release, artifactId, assetType, assetName);
	}

	/**
	 * This method will fetch and baselines a BPFlow
	 * 
	 * @param department, module, release, assetType, assetName
	 * 
	 * @return response with status information
	 */
	@GetMapping(value = "/baseline-asset/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}")
	public Response<?> baselineBPAsset(@PathVariable("department") String department, @PathVariable("module") String module,
			@PathVariable("release") String release, @PathVariable("artifactid") int artifactId,
			@PathVariable("assettype") String assetType, @PathVariable("assetname") String assetName) {
		LOGGER.info("Baseline business flow...");
		return bpDesignerService.baselineBPAsset(department, module, release, artifactId, assetType, assetName);
	}

}
