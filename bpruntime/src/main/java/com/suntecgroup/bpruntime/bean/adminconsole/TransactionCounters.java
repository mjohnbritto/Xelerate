package com.suntecgroup.bpruntime.bean.adminconsole;

public class TransactionCounters {
	
   private int transactionTotalCount;
    private int transactionSuccessCount;
    private int transactionFailureCount;
    private int transactionBusinessFailureCount;
    private int transactionTechnicalFailureCount;
    private int eventTotalCount;
    private int eventSuccessCount;
    private int eventFailureCount;
    private int eventBusinessFailureCount;
    private int eventTechnicalFailureCount;
    
	
	public int getTransactionTotalCount() {
		return transactionTotalCount;
	}

	public void setTransactionTotalCount(int transactionTotalCount) {
		this.transactionTotalCount = transactionTotalCount;
	}

	public int getTransactionSuccessCount() {
		return transactionSuccessCount;
	}

	public void setTransactionSuccessCount(int transactionSuccessCount) {
		this.transactionSuccessCount = transactionSuccessCount;
	}

	public int getTransactionFailureCount() {
		return transactionFailureCount;
	}

	public void setTransactionFailureCount(int transactionFailureCount) {
		this.transactionFailureCount = transactionFailureCount;
	}

	public int getEventTotalCount() {
		return eventTotalCount;
	}

	public void setEventTotalCount(int eventTotalCount) {
		this.eventTotalCount = eventTotalCount;
	}

	public int getEventSuccessCount() {
		return eventSuccessCount;
	}

	public void setEventSuccessCount(int eventSuccessCount) {
		this.eventSuccessCount = eventSuccessCount;
	}

	public int getEventFailureCount() {
		return eventFailureCount;
	}

	public void setEventFailureCount(int eventFailureCount) {
		this.eventFailureCount = eventFailureCount;
	}

	public int getEventBusinessFailureCount() {
		return eventBusinessFailureCount;
	}

	public void setEventBusinessFailureCount(int eventBusinessFailureCount) {
		this.eventBusinessFailureCount = eventBusinessFailureCount;
	}

	public int getEventTechnicalFailureCount() {
		return eventTechnicalFailureCount;
	}

	public void setEventTechnicalFailureCount(int eventTechnicalFailureCount) {
		this.eventTechnicalFailureCount = eventTechnicalFailureCount;
	}

	public int getTransactionBusinessFailureCount() {
		return transactionBusinessFailureCount;
	}

	public void setTransactionBusinessFailureCount(int transactionBusinessFailureCount) {
		this.transactionBusinessFailureCount = transactionBusinessFailureCount;
	}

	public int getTransactionTechnicalFailureCount() {
		return transactionTechnicalFailureCount;
	}

	public void setTransactionTechnicalFailureCount(int transactionTechnicalFailureCount) {
		this.transactionTechnicalFailureCount = transactionTechnicalFailureCount;
	}

	public void incrementTotalCount(int eventsCount) {		
		this.transactionTotalCount++;
		this.eventTotalCount = this.eventTotalCount + eventsCount;
	}
	
	public void incrementSuccessCount(int eventsCount) {
		this.transactionSuccessCount++;
		this.eventSuccessCount = this.eventSuccessCount + eventsCount;
	}
	
	public void decrementSuccessCount(int eventsCount) {
		this.transactionSuccessCount--;
		this.eventSuccessCount = this.eventSuccessCount - eventsCount;
	}
	
	/*public void incrementFailureCount(int eventsCount) {
		this.transactionFailureCount++;
		this.eventFailureCount = this.eventFailureCount + eventsCount; 
	}
	
	public void decrementFailureCount(int eventsCount) {
		this.transactionFailureCount--;
		this.eventFailureCount = this.eventFailureCount - eventsCount; 
	}*/
	
	public void incrementBusinessFailureCount(int eventsCount) {
		this.transactionBusinessFailureCount++;
		this.eventBusinessFailureCount = this.eventBusinessFailureCount + eventsCount; 
	}
	
	public void decrementBusinessFailureCount(int eventsCount) {
		this.transactionBusinessFailureCount--;
		this.eventBusinessFailureCount = this.eventBusinessFailureCount - eventsCount; 
	}
	
	public void incrementTechnicalFailureCount(int eventsCount) {
		this.transactionTechnicalFailureCount++;
		this.eventTechnicalFailureCount = this.eventTechnicalFailureCount + eventsCount; 
	}
	
	public void decrementTechnicalFailureCount(int eventsCount) {
		this.transactionTechnicalFailureCount--;
		this.eventTechnicalFailureCount = this.eventTechnicalFailureCount - eventsCount; 
	}
   
}
