package com.suntecgroup.bp.designer.exception;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.suntecgroup.bp.designer.model.Response;
import com.suntecgroup.bp.designer.model.Status;

/**
 * Spring global exception handler. It will handle the exceptions across the
 * whole application.
 */
@ControllerAdvice
public class BPExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BPExceptionHandler.class);

	@ExceptionHandler(BPException.class)
	@ResponseBody
	public final Response<List<ErrorDetail>> handleBPException(
			HttpServletRequest request, Exception ex) {

		LOGGER.error("Business exception captured :" + ex.getMessage());
		ErrorDetail errorDetails = new ErrorDetail("", ex.getMessage());
		return new Response<List<ErrorDetail>>(Status.FAILURE.getStatusCode(),
				Status.FAILURE, errorDetails, null);
	}
	
	@ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public final Response<ErrorDetail> handleUnCheckedException(
            HttpServletRequest request, Exception ex) {
        LOGGER.error("Exception occoured : " + ex.getMessage());
        ErrorDetail errorDetails = new ErrorDetail("", "service error");
        return new Response<ErrorDetail>(Status.FAILURE.getStatusCode(),
                Status.FAILURE, errorDetails, null);
    }

	/*@ExceptionHandler(org.springframework.web.client.HttpClientErrorException.class)
	@ResponseBody
	public final Response<List<ErrorDetail>> handleServiceCallException(
			HttpServletRequest request, Exception ex) {

	    ex.printStackTrace();
		LOGGER.error("Exception occoured in service call :" + ex.getMessage());
		
		ErrorDetail errorDetails = new ErrorDetail("",
		    ex.getMessage());
		return new Response<List<ErrorDetail>>(Status.FAILURE.getStatusCode(),
				Status.FAILURE, errorDetails, null);
	}*/
	
	/*@ExceptionHandler(org.springframework.web.client.HttpStatusCodeException.class)
	@ResponseBody
	public final Response<List<ErrorDetail>> handleHttpErrorCodeException(
	    HttpServletRequest request, org.springframework.web.client.HttpStatusCodeException hex) {

	  ObjectMapper mapper = new ObjectMapper();	 
	  Response <List<ErrorDetail>> res = null;
	  ErrorDetail ed = null;
	  int  httpStatuscode =  hex.getRawStatusCode();	
	  if(httpStatuscode == 404){	   
	    ed = new ErrorDetail("", "URL not found");
	    res =  new Response<List<ErrorDetail>>(Status.FAILURE.getStatusCode(),
	        Status.FAILURE, ed, null);
	  } else {   
	      LOGGER.info("Message======>" + (hex.getResponseBodyAsString()));
	      XbmcValidateAccessResponse xbmcres = null;
        try {
          xbmcres = mapper.readValue(hex.getResponseBodyAsString(), XbmcValidateAccessResponse.class);
        } catch (IOException e) {
          LOGGER.error("xbmc response parsing error...");
          e.printStackTrace();
        } 

	     ed = new ErrorDetail("", xbmcres.getMessage());
	     res =  new Response<List<ErrorDetail>>(Status.FAILURE.getStatusCode(),
	          Status.FAILURE, ed, null); 
	   
	  } 
	  return res;
	}*/

	
}
