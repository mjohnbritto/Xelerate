package com.suntecgroup.nifi.frontend.bean.merge;

import java.util.List;

import com.suntecgroup.nifi.frontend.bean.Operators;

public class TraversingOperator {

	private Operators operator;
	private List<String> connectionNameList;
	private List<String> pathNameList;
	
	public TraversingOperator(Operators operator, List<String> connectionNameList, List<String> pathNameList) {
		super();
		this.operator = operator;
		this.connectionNameList = connectionNameList;
		this.pathNameList = pathNameList;
	}

	public Operators getOperator() {
		return operator;
	}

	public void setOperator(Operators operator) {
		this.operator = operator;
	}

	public List<String> getConnectionNameList() {
		return connectionNameList;
	}

	public void setConnectionNameList(List<String> connectionNameList) {
		this.connectionNameList = connectionNameList;
	}

	public List<String> getPathNameList() {
		return pathNameList;
	}

	public void setPathNameList(List<String> pathNameList) {
		this.pathNameList = pathNameList;
	}

}
