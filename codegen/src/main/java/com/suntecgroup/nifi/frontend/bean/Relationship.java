package com.suntecgroup.nifi.frontend.bean;

public class Relationship {

	private boolean isSuccess;
	private boolean isFailure;
	private boolean retry;
	private boolean noRetry;
	private boolean original;
	private boolean response;

	// TRUE - Autoterminate
	// False - Dont AutoTerminate

	public Relationship() {
		this.isSuccess = true;
		this.isFailure = true;
		this.retry = true;
		this.noRetry = true;
		this.original = true;
		this.response = true;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public boolean isFailure() {
		return isFailure;
	}

	public void setFailure(boolean isFailure) {
		this.isFailure = isFailure;
	}

	public boolean isRetry() {
		return retry;
	}

	public void setRetry(boolean retry) {
		this.retry = retry;
	}

	public boolean isNoRetry() {
		return noRetry;
	}

	public void setNoRetry(boolean noRetry) {
		this.noRetry = noRetry;
	}

	public boolean isOriginal() {
		return original;
	}

	public void setOriginal(boolean original) {
		this.original = original;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

}