package com.suntecgroup.metaconfig.model;

import java.util.List;

import io.swagger.v3.oas.models.security.OAuthFlows;

public class ApiSecurity {
	
	private List<String> apiScopes;
	private OAuthFlows securityDetail;
	public List<String> getApiScopes() {
		return apiScopes;
	}
	public void setApiScopes(List<String> list) {
		this.apiScopes = list;
	}
	public OAuthFlows getSecurityDetail() {
		return securityDetail;
	}
	public void setSecurityDetail(OAuthFlows securityDetail) {
		this.securityDetail = securityDetail;
	}

}
