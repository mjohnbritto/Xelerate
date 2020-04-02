package com.suntecgroup.bp.designer.model;

import java.util.List;

import javax.validation.Valid;

public class Dependencies {
	@Valid
	private List<Bs> bs;
	@Valid
	private List<Be> be;

	public List<Bs> getBs() {
		return bs;
	}
	public void setBs(List<Bs> bs) {
		this.bs = bs;
	}
	public List<Be> getBe() {
		return be;
	}
	public void setBe(List<Be> be) {
		this.be = be;
	}


}
