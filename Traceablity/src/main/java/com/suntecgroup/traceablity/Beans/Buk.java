package com.suntecgroup.traceablity.Beans;

public class Buk
 {
	private String attributeName;
	private String attributeValue;

	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	@Override
	public String toString() {
		return "ClassPojo [attributeName = " + attributeName
				+ ", attributeValue = " + attributeValue + "]";
	}
}