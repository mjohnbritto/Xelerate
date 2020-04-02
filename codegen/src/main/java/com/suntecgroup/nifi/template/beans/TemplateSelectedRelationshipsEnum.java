package com.suntecgroup.nifi.template.beans;

public enum TemplateSelectedRelationshipsEnum {
	FAILURE("Failure"), NO_RETRY("No Retry"), ORIGINAL("Original"), RESPONSE("Response"), RETRY("Retry"), SUCCESS(
			"Success");

	private final String value;

	private TemplateSelectedRelationshipsEnum(String s) {
		value = s;
	}

	public String getValue() {
		return value;
	}

}
