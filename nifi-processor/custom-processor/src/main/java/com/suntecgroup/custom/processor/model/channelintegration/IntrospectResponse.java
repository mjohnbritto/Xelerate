package com.suntecgroup.custom.processor.model.channelintegration;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * DTO class to hold the introspect response from the OAuth Server.
 * 
 * @author murugeshpd
 *
 */
public class IntrospectResponse {
	private Integer exp;
	private Integer iat;
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private List<String> aud;
	private boolean active;

	public Date getExpirationDate() {
		return new Date(exp * 1000L);
	}
	
	public Integer getExp() {
		return exp;
	}

	public void setExp(Integer exp) {
		this.exp = exp;
	}

	public Integer getIat() {
		return iat;
	}

	public void setIat(Integer iat) {
		this.iat = iat;
	}

	public List<String> getAud() {
		return aud;
	}

	public void setAud(List<String> aud) {
		this.aud = aud;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
