package com.suntecgroup.bpruntime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.bpruntime.service.DashboardService;

@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/bpruntime/dashboard")
public class DashboardController {

	@Autowired
	DashboardService dashboardService;

	@RequestMapping(method = RequestMethod.GET, value = "/dashboarddetails/{sessionId}/{runNumber}")
	public ResponseEntity<?> getSessionList(
			@PathVariable("sessionId") String sessionId,
			@PathVariable("runNumber") String runNumber) {
		String apiResponse = null;
		apiResponse = dashboardService.getDashboardSessionDetails(
				sessionId, runNumber);
		HttpHeaders headers = new HttpHeaders();
     headers.set("Content-Type", "application/json");
		ResponseEntity<?> res = new ResponseEntity<String>(apiResponse, headers, HttpStatus.OK);
		return res;
	}

}
