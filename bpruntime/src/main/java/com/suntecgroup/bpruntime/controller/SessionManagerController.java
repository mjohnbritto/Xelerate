package com.suntecgroup.bpruntime.controller;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.BPState;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplaySessionRequest;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplaySessionResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.SessionState;
import com.suntecgroup.bpruntime.bean.adminconsole.TransactionDetail;
import com.suntecgroup.bpruntime.service.DashboardService;
import com.suntecgroup.bpruntime.service.SessionManagerService;

@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/bpruntime/sessionmanager")
public class SessionManagerController {

	@Autowired
	private SessionManagerService sessionManagerService;

	@Autowired
	DashboardService dashboardService;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

	@RequestMapping(method = RequestMethod.POST, value = "/startsession", consumes = "application/json")
	public ResponseEntity<ApiResponse<String>> startSession(@RequestBody BPState bpState) {
		ApiResponse<String> response = sessionManagerService.startSession(bpState);
		return new ResponseEntity<ApiResponse<String>>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getsessionlist/{templateId}")
	public ResponseEntity<String> getSessionList(@PathVariable("templateId") String templateId) {
		String response = sessionManagerService.getSessionList(templateId);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/stopsession", consumes = "application/json")
	public ResponseEntity<ApiResponse<String>> stopSession(@RequestBody SessionState sessionState) {
		ApiResponse<String> response = sessionManagerService.stopSession(sessionState);
		return new ResponseEntity<ApiResponse<String>>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/replay-technical-failure", consumes = "application/json")
	public ResponseEntity<?> replayTechnicalFailure(@RequestBody ReplaySessionRequest replaySessionRequest) {
		ReplaySessionResponse<?> resumeSessionResponse = dashboardService.replayTechnicalError(replaySessionRequest);
		return new ResponseEntity<ReplaySessionResponse<?>>(resumeSessionResponse, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/updateTransactionStatus", consumes = "application/json")
	public ResponseEntity<String> updateTransactionStatus(@RequestBody TransactionDetail transactionDtl)
			throws Exception {

		String response = sessionManagerService.updateTransactionStatus(transactionDtl);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	//To update input file channel file name details and counts
	@RequestMapping(method = RequestMethod.POST, value = "/updateFilesRecStatus", consumes = "application/json")
	public ResponseEntity<String> updateFilesRecStatus(@RequestBody TransactionDetail channelIntDtl)
			throws Exception {

		String response = sessionManagerService.updateFilesRecStatus(channelIntDtl);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	//To get input file channel file name details
	@RequestMapping(method = RequestMethod.POST, value = "/getFilesNameStatus", consumes = "application/json")
	public ResponseEntity<String> getFilesNameStatus(@RequestBody TransactionDetail fileNameDtl)
			throws Exception {
		String response = sessionManagerService.getFilesNameStatus(fileNameDtl);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/updateOperatorStats", consumes = "application/json")
	public ResponseEntity<String> updateOperatorStats(@RequestBody TransactionDetail operatorStats ) throws Exception{
		String response=sessionManagerService.updateOperatorStats(operatorStats);
		return new ResponseEntity<String>(response,HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/getOperatorStats", consumes = "application/json")
	public ResponseEntity<String> getOperatorStats(@RequestBody TransactionDetail operatorDtl)
			throws Exception {
		String response = sessionManagerService.getOperatorStats(operatorDtl);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	

}
