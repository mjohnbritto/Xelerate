package com.suntecgroup.custom.processor.model.channelintegration;

/**
 * Transaction acknowledgement response for REST input channel.
 * 
 * @author murugeshpd
 *
 */
public class TransactionAcknowledgement {
	private String transactionId;

	public TransactionAcknowledgement(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
