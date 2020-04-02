package com.suntecgroup.bp.designer.exception;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import com.suntecgroup.bp.designer.controller.BPDesignerController;
import com.suntecgroup.bp.util.ResourceUtil;

@Component
public class RestTemplateResponseErrorHandler 
  implements ResponseErrorHandler {
 
  private static final Logger LOGGER = LoggerFactory
      .getLogger(BPDesignerController.class);
  
  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    LOGGER.error("XBMC Response handle error: {} {}", response.getStatusCode(), response.getStatusText());
  }

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {    
      return ResourceUtil.isError(response.getStatusCode());
  }
}
