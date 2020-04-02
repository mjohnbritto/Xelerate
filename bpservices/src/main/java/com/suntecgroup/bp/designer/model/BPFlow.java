package com.suntecgroup.bp.designer.model;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "user", "department", "module", "bpname", "releaseNo", "timeStamp", "version", "bpFlowRequest" })
@Document(collection = "BPFlow")
public class BPFlow {

	private String user;
	private String department;
	private String module;
	private String bpname;
	private String releaseNo;
	private Date timeStamp;
	private String bpFlowRequest;
	private int version;

	public BPFlow() {

	}

	public BPFlow(String department, String module, String bpname, String releaseNo, String user, Date timeStamp,
			String bpFlowRequest) {
		this.department = department;
		this.module = module;
		this.bpname = bpname;
		this.releaseNo = releaseNo;
		this.user = user;
		this.timeStamp = timeStamp;
		this.bpFlowRequest = bpFlowRequest;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getBpname() {
		return bpname;
	}

	public void setBpname(String bpname) {
		this.bpname = bpname;
	}

	public String getReleaseNo() {
		return releaseNo;
	}

	public void setReleaseNo(String releaseNo) {
		this.releaseNo = releaseNo;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getBpFlowRequest() {
		return bpFlowRequest;
	}

	public void setBpFlowRequest(String bpFlowRequest) {
		this.bpFlowRequest = bpFlowRequest;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
