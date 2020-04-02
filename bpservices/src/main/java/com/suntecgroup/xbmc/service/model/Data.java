package com.suntecgroup.xbmc.service.model;

public class Data {
  
  private String userId;
  private String username;
  private Context context;
  private BPDetails bpdetails;
  
  
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public Context getContext() {
    return context;
  }
  public void setContext(Context context) {
    this.context = context;
  }
 
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public BPDetails getBpdetails() {
    return bpdetails;
  }
  public void setBpdetails(BPDetails bpdetails) {
    this.bpdetails = bpdetails;
  }
}
