package com.suntecgroup.eventlog.beans;

/**
 * @author madala.s
 *
 */
public class Events
 {
	private Buk[] buk;
	private String payload;

	public Buk[] getBuk() {
		return buk;
	}
	public void setBuk(Buk[] buk) {
		this.buk = buk;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
}