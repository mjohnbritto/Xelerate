package com.suntecgroup.metaconfig.exception;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.suntecgroup.metaconfig.model.Response;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CustomExceptionHandler.class);
	
	@ExceptionHandler(ApiDocParserException.class)
	public final ResponseEntity<Response<Set<String>>> handleUserNotFoundException(ApiDocParserException ex,
			WebRequest request) {
		return new ResponseEntity<Response<Set<String>>>(ex.getErrorResponse(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Response<String>> handleGenericException(Exception ex, WebRequest request) {
		LOG.error("Exception while processing the request", ex);
		return new ResponseEntity<Response<String>>(
				new Response<String>("500", ex.getMessage(), "Exception while processing the request. Please use Open API spec 3.0"),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
