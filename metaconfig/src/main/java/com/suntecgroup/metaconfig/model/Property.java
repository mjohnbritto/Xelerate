package com.suntecgroup.metaconfig.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Property extends KeyValue {
  
  private List<KeyValue> additionalInfo;

  private List<Property> subkeys;

  private Map<String, String[]> dropdownlist;

  public List<Property> getSubkeys() {
    return subkeys;
  }

  public void setSubkeys(List<Property> subkeys) {
    this.subkeys = subkeys;
  }

  public Map<String, String[]> getDropdownlist() {
    return dropdownlist;
  }

  public void setDropdownlist(Map<String, String[]> dropdownlist) {
    this.dropdownlist = dropdownlist;
  }

  public List<KeyValue> getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(List<KeyValue> additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

}