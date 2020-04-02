
package com.suntecgroup.bp.designer.frontend.bean.filechannel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Validation {

	@JsonProperty("fileNameValidation")
	private FileNameValidation fileNameValidation = null;
	@JsonProperty("fileNameDuplication")
	private FileNameDuplication fileNameDuplication = null;
	@JsonProperty("fileHeader")
	private List<FileHeader> fileHeader = null;
	@JsonProperty("fileFooter")
	private List<FileFooter> fileFooter = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Validation() {
	}

	@JsonProperty("fileNameValidation")
	public FileNameValidation getFileNameValidation() {
		return fileNameValidation;
	}

	@JsonProperty("fileNameValidation")
	public void setFileNameValidation(FileNameValidation fileNameValidation) {
		this.fileNameValidation = fileNameValidation;
	}

	@JsonProperty("fileHeader")
	public List<FileHeader> getFileHeader() {
		return fileHeader;
	}

	@JsonProperty("fileHeader")
	public void setFileHeader(List<FileHeader> fileHeader) {
		this.fileHeader = fileHeader;
	}

	@JsonProperty("fileFooter")
	public List<FileFooter> getFileFooter() {
		return fileFooter;
	}

	@JsonProperty("fileFooter")
	public void setFileFooter(List<FileFooter> fileFooter) {
		this.fileFooter = fileFooter;
	}

	public FileNameDuplication getFileNameDuplication() {
		return fileNameDuplication;
	}

	public void setFileNameDuplication(FileNameDuplication fileNameDuplication) {
		this.fileNameDuplication = fileNameDuplication;
	}

}
