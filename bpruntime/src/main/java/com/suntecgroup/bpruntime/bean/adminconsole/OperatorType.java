package com.suntecgroup.bpruntime.bean.adminconsole;

public enum OperatorType {
	START("customStart"), END("customEnd"), INVOKE_BS("customInvokeHTTP"), DECISION_MATRIX_INCLUSIVE(
			"decisionMatrixInclusive"), DECISION_MATRIX_EXCLUSIVE("decisionMatrixExclusive"), SMART(
					"SmartConnector"), INPUT_CHANNEL_INTEGRATION("FileInputProcessor"), REST_INPUT_CHANNEL_INTEGRATION(
							"RestInputProcessor"), MERGE("customInvokeHTTP"), OUTPUT_CHANNEL_INTEGRATION(
									"FileOutputProcessor"), OUTPUT_REST_CHANNEL_INTEGRATION("customInvokeHTTP"),INVOKE_BS_EXTERNAL("customInvokeHTTP");

	private String processorType;

	public String getProcessorType() {
		return processorType;
	}

	public void setProcessorType(String processorType) {
		this.processorType = processorType;
	}

	private OperatorType(String processorType) {
		this.processorType = processorType;
	}

}
