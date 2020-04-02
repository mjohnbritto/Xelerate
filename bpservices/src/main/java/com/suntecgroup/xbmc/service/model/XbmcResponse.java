package com.suntecgroup.xbmc.service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "status", "data" })
public class XbmcResponse<T> {

  private String status;
  private String message;
  private List<T> data;

  public XbmcResponse() {

  }

  public XbmcResponse(String status, List<T> data) {
    this.status = status;
    this.data = data;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<T> getData() {
    return data;
  }

  public void setData(List<T> data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
