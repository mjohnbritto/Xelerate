package com.suntecgroup.nifi.datamodel;

import com.suntecgroup.nifi.frontend.bean.Connection;
import com.suntecgroup.nifi.template.beans.TemplateConnection;

public class SuntecConnectionModel {
	private Connection suntecConnection;
	private TemplateConnection nifiConnection;
	
	public Connection getSuntecConnection() {
		return suntecConnection;
	}
	public void setSuntecConnection(Connection suntecConnection) {
		this.suntecConnection = suntecConnection;
	}
	public TemplateConnection getNifiConnection() {
		return nifiConnection;
	}
	public void setNifiConnection(TemplateConnection nifiConnection) {
		this.nifiConnection = nifiConnection;
	}

}