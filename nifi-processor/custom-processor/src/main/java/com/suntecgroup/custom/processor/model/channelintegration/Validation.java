package com.suntecgroup.custom.processor.model.channelintegration;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "fileNameValidation", "fileNameDuplication", "fileHeader", "fileFooter" })

public class Validation {

	@JsonProperty("fileNameValidation")
    private FileNameValidationAttr fileNameValidation;

    @JsonProperty("fileNameDuplication")
    private Map<String, String> fileNameDuplication;

    @JsonProperty("fileHeader")
    private List<ValidationAttributes> headerValidation;

    @JsonProperty("fileFooter")
    private List<ValidationAttributes> footerValidation;
    
    @JsonProperty("fileNameValidation")
	public FileNameValidationAttr getFileNameValidation() {
		return fileNameValidation;
	}
    @JsonProperty("fileNameValidation")
	public void setFileNameValidation(FileNameValidationAttr fileNameValidation) {
		this.fileNameValidation = fileNameValidation;
	}

	@JsonProperty("fileNameDuplication")
    public Map<String, String> getFileNameDuplication() {
        return fileNameDuplication;
    }

    @JsonProperty("fileNameDuplication")
    public void setFileNameDuplication(Map<String, String> fileNameDuplication) {
        this.fileNameDuplication = fileNameDuplication;
    }

    @JsonProperty("fileHeader")
    public List<ValidationAttributes> getHeaderValidation() {
        return headerValidation;
    }

    @JsonProperty("fileHeader")
    public void setHeaderValidation(List<ValidationAttributes> headerValidation) {
        this.headerValidation = headerValidation;
    }

    @JsonProperty("fileFooter")
    public List<ValidationAttributes> getFooterValidation() {
        return footerValidation;
    }

    @JsonProperty("fileFooter")
    public void setFooterValidation(List<ValidationAttributes> footerValidation) {
        this.footerValidation = footerValidation;
    }

}
