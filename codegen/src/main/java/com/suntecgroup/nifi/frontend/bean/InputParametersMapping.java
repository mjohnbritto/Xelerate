package com.suntecgroup.nifi.frontend.bean;




import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class InputParametersMapping {	

	@SerializedName("pathParam")
	@Expose
	private String pathParam;

	@SerializedName("queryParam")
	@Expose
	private String queryParam;

	@SerializedName("contentParam")
	@Expose
	private String contentParam;
	
	
	@SerializedName("headerParam")
	@Expose
	private String headerParam;
	

	public String getPathParam() {
		return pathParam;
	}

	public void setPathParam(String pathParam) {
		this.pathParam = pathParam;
	}

	public String getQueryParam() {
		return queryParam;
	}

	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}

	public String getContentParam() {
		return contentParam;
	}

	public void setContentParam(String contentParam) {
		this.contentParam = contentParam;
	}

	public String getHeaderParam() {
		return headerParam;
	}

	public void setHeaderParam(String headerParam) {
		this.headerParam = headerParam;
	}

	
	
}
