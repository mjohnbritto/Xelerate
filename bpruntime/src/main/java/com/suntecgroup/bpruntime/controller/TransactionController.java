package com.suntecgroup.bpruntime.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.bpruntime.bean.adminconsole.ApiResponse;
import com.suntecgroup.bpruntime.bean.adminconsole.Status;
import com.suntecgroup.bpruntime.bean.adminconsole.TransactionDetail;
import com.suntecgroup.bpruntime.constant.Constant;
import com.suntecgroup.bpruntime.service.TransactionService;

@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/bpruntime/transaction")
public class TransactionController {

	@Autowired
	TransactionService transactionService;

	@RequestMapping(method = RequestMethod.GET, value = "/unaddressed-failure/{sessionId}/{runNumber}/{errorType}")
	public ResponseEntity<?> getUnAddressedBusinessFailureDetails(@PathVariable("sessionId") String sessionId,
			@PathVariable("runNumber") String runNumber, @PathVariable("errorType") String errorType) {
		ApiResponse<?> apiResponse = null;
		List<?> failedTransactionDetails = null;
		try {
			failedTransactionDetails = transactionService.getFailedTransactionDetails(sessionId, runNumber, errorType,
					false);
			apiResponse = new ApiResponse<>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
					failedTransactionDetails);
		} catch (Exception exception) {
			apiResponse = new ApiResponse<>(Status.FAILURE.getStatusCode(), Status.FAILURE, exception.getMessage(),
					null);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		ResponseEntity<?> res = new ResponseEntity<ApiResponse<?>>(apiResponse, headers, HttpStatus.OK);
		return res;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addressed-failure/{sessionId}/{runNumber}/{errorType}")
	public ResponseEntity<?> getAddressedBusinessFailureDetails(@PathVariable("sessionId") String sessionId,
			@PathVariable("runNumber") String runNumber, @PathVariable("errorType") String errorType) {

		ApiResponse<?> apiResponse = null;
		HashMap<String, Long> actionTocountMap = new HashMap<String, Long>();

		try {
			// save error count to the map and constructing API response.
			Long replayCount = transactionService.getFailedTransactionCount(sessionId, runNumber, errorType, true,
					Constant.ACTION_REPLAY);
			Long permanentErrorCount = transactionService.getFailedTransactionCount(sessionId, runNumber, errorType,
					true, Constant.ACTION_PERMANENT_ERROR);
			Long backoutCount = transactionService.getFailedTransactionCount(sessionId, runNumber, errorType, true,
					Constant.ACTION_BACKOUT);
			actionTocountMap.put(Constant.ACTION_REPLAY, replayCount);
			actionTocountMap.put(Constant.ACTION_PERMANENT_ERROR, permanentErrorCount);
			actionTocountMap.put(Constant.ACTION_BACKOUT, backoutCount);
			apiResponse = new ApiResponse<HashMap>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
					actionTocountMap);
		} catch (Exception exception) {
			apiResponse = new ApiResponse<HashMap>(Status.FAILURE.getStatusCode(), Status.FAILURE,
					exception.getMessage(), null);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		ResponseEntity<?> res = new ResponseEntity<ApiResponse<?>>(apiResponse, headers, HttpStatus.OK);
		return res;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/address-business-failure", consumes = "application/json")
	public ResponseEntity<?> addressBusinessFailure(@RequestBody List<TransactionDetail> transactionList) {
		String response = transactionService.addressBusinessFailure(transactionList);
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}
}
