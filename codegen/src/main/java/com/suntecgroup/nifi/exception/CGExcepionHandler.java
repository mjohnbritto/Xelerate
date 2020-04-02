package com.suntecgroup.nifi.exception;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mongodb.MongoException;
import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.frontend.bean.BPFlowResponse;
import com.suntecgroup.nifi.frontend.bean.ErrorDetails;

/**
 * Spring global exception handler. It will handle the exceptions across the
 * whole application.
 */
@ControllerAdvice
@RestController
public class CGExcepionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<BPFlowResponse> handleOtherException(
			Exception ex, WebRequest request) {
		BPFlowResponse bPFlowResponse = new BPFlowResponse();
		List<ErrorDetails> errorDetailsList = new ArrayList<ErrorDetails>();
		if (ex instanceof CGException) {
			ErrorDetails errorDetails = new ErrorDetails(
					CGConstants.BPEXCEPTION_STATUS_CODE,
					CGConstants.BPEXCEPTION_STATUS_DESC);
			errorDetailsList.add(errorDetails);
		} else if (ex instanceof MongoException) {
			ErrorDetails errorDetails = new ErrorDetails(
					CGConstants.DBEXCEPTION_STATUS_CODE,
					CGConstants.DBEXCEPTION_STATUS_DESC);
			errorDetailsList.add(errorDetails);
		} else {
			ErrorDetails errorDetails = new ErrorDetails(
					CGConstants.EXCEPTION_STATUS_CODE,
					CGConstants.EXCEPTION_STATUS_DESC);
			errorDetailsList.add(errorDetails);
		}
		bPFlowResponse.setErrorDetails(errorDetailsList);
		return new ResponseEntity<BPFlowResponse>(bPFlowResponse,
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(RuntimeException.class)
	public final ResponseEntity<BPFlowResponse> handleUnCheckedException(
			HttpServletRequest request, Exception ex) {
		BPFlowResponse bPFlowResponse = new BPFlowResponse();
		List<ErrorDetails> errorDetailsList = new ArrayList<ErrorDetails>();
		ErrorDetails errorDetails = new ErrorDetails("", "service error");
		errorDetailsList.add(errorDetails);
		bPFlowResponse.setErrorDetails(errorDetailsList);
		return new ResponseEntity<BPFlowResponse>(bPFlowResponse,
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
