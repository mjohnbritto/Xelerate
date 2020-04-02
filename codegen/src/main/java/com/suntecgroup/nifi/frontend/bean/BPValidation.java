package com.suntecgroup.nifi.frontend.bean;

import java.util.List;
import java.util.Map;

public class BPValidation {

	private List<Map<String, String>> validationError;

	public List<Map<String, String>> getValidationError() {
		return validationError;
	}

	public void setValidationError(List<Map<String, String>> validationError) {
		this.validationError = validationError;
	}

}
