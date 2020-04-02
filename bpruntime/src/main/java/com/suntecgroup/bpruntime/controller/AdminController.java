package com.suntecgroup.bpruntime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.BPDetails;
import com.suntecgroup.bpruntime.bean.adminconsole.BPState;
import com.suntecgroup.bpruntime.service.AdminService;

@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/bpruntime/adminconsole")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@RequestMapping(method = RequestMethod.GET, value = "/getbplist")
	public ResponseEntity<String> getBpList() {
		String response = adminService.getBpList();
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/updatebpstate", consumes = "application/json")
	public ResponseEntity<String> updateBpState(@RequestBody BPState bpState) {
		String response = adminService.updateBpState(bpState);
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getbpdetails/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}")
	public ResponseEntity<String> getBpDetails(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("artifactid") int artifactid, @PathVariable("assettype") String assetType,
			@PathVariable("assetname") String assetName) {
		String response = adminService.getBpDetails(department, module, release, artifactid, assetType, assetName);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getprocessvariables/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}")
	public ResponseEntity<String> getProcessVariable(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("artifactid") int artifactid, @PathVariable("assettype") String assetType,
			@PathVariable("assetname") String assetName) {
		String response = adminService.getProcessVariable(department, module, release, artifactid, assetType,
				assetName);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getprocessconfiguration/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}")
	public ResponseEntity<String> getProcessConfiguration(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("artifactid") int artifactid, @PathVariable("assettype") String assetType,
			@PathVariable("assetname") String assetName) {
		// current implementation fetch process variables for process
		// configuration, may be needs to be updated later
		String response = adminService.getProcessVariable(department, module, release, artifactid, assetType,
				assetName);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getoperatorproperties/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}/{operatorKey}")
	public ResponseEntity<String> getOperatorProperty(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("artifactid") int artifactid, @PathVariable("assettype") String assetType,
			@PathVariable("assetname") String assetName, @PathVariable("operatorKey") String operatorKey) {
		String response = adminService.getOperatorProperty(department, module, release, artifactid, assetType,
				assetName, operatorKey);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/updatebpdetails", consumes = "application/json")
	public ResponseEntity<ApiResponse<Object>> updateBpDetails(@RequestBody BPDetails bpDetails) {
		ApiResponse<Object> response = adminService.updateBpDetails(bpDetails);
		return new ResponseEntity<ApiResponse<Object>>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/getOperatorStatistics/{operatorName}/{operatorType}/{processGroupId}")
	public ResponseEntity<?> getOperatorStatistics(@PathVariable("operatorName") String operatorName,
			@PathVariable("operatorType") String operatorType, @PathVariable("processGroupId") String processGroupId) {

		ApiResponse<?> operatorStatistics = adminService.getOperatorData(operatorName, operatorType, processGroupId);
		return new ResponseEntity<ApiResponse<?>>(operatorStatistics, HttpStatus.OK);
	}

	// Asset Manager get API
	@ResponseBody
	@RequestMapping(value = "/getasset/{department}/{module}/{release}/{artifactid}/{assettype}/{assetname}")
	public ResponseEntity<?> getDeployedBPAsset(@PathVariable("department") String department,
			@PathVariable("module") String module, @PathVariable("release") String release,
			@PathVariable("artifactid") int artifactid, @PathVariable("assettype") String assetType,
			@PathVariable("assetname") String assetName,
			@RequestParam(value = "version", required = false) Object version) {
		ApiResponse<?> responseData = adminService.getDeployedBPAsset(department, module, release, artifactid,
				assetType, assetName, version);
		return new ResponseEntity<ApiResponse<?>>(responseData, HttpStatus.OK);
	}

}
