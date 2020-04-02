package com.suntecgroup.bp.designer.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Comment")
public class Comment {
private String operator;
private String comment;
public String getOperator() {
	return operator;
}
public void setOperator(String operator) {
	this.operator = operator;
}
public String getComment() {
	return comment;
}
public void setComment(String comment) {
	this.comment = comment;
}

}
