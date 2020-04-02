package com.suntecgroup.custom.processor.model.channelintegration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "calculation", "contentField", "headerField", "footerField", "evaluate" })
public class ValidationAttributes {

	  @JsonProperty("calculation")
	    private String calculation;

	    @JsonProperty("contentField")
	    private String contentField;
	    
	    @JsonProperty("headerField")
	    private String headerField;
	    
	    @JsonProperty("footerField")
	    private String footerField;
	    
	    @JsonProperty("evaluate")
	    private String evaluate;

	    @JsonProperty("calculation")
	    public String getCalculation() {
	        return calculation;
	    }

	    @JsonProperty("calculation")
	    public void setCalculation(String calculation) {
	        this.calculation = calculation;
	    }
	    
	    @JsonProperty("contentField")
	    public String getContentField() {
	        return contentField;
	    }

	    @JsonProperty("contentField")
	    public void setContentField(String contentField) {
	        this.contentField = contentField;
	    }

	    @JsonProperty("headerField")
	    public String getHeaderField() {
	        return headerField;
	    }

	    @JsonProperty("headerField")
	    public void setHeaderField(String headerField) {
	        this.headerField = headerField;
	    }

	    @JsonProperty("footerField")
	    public String getFooterField() {
	        return footerField;
	    }

	    @JsonProperty("footerField")
	    public void setFooterField(String footerField) {
	        this.footerField = footerField;
	    }

	    @JsonProperty("evaluate")
	    public String getEvaluate() {
	        return evaluate;
	    }

	    @JsonProperty("evaluate")
	    public void setEvaluate(String evaluate) {
	        this.evaluate = evaluate;
	    }

}
