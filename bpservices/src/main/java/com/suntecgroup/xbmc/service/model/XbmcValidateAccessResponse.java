package com.suntecgroup.xbmc.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(Include.NON_NULL)
public class XbmcValidateAccessResponse { 

  private String status;

  private Data data;

  private String message;

  private String errorType;

  private FieldErrors fieldErrors;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getErrorType() {
    return errorType;
  }

  public void setErrorType(String errorType) {
    this.errorType = errorType;
  }

  public FieldErrors getFieldErrors() {
    return fieldErrors;
  }

  public void setFieldErrors(FieldErrors fieldErrors) {
    this.fieldErrors = fieldErrors;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }
}
