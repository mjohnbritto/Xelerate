
package com.suntecgroup.bpruntime.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "code",
    "context",
    "inputIndex",
    "message"
})
public class ErrorList {

    @JsonProperty("code")
    private String code;
    @JsonProperty("context")
    private String context;
    @JsonProperty("inputIndex")
    private Integer inputIndex;
    @JsonProperty("message")
    private String message;

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("context")
    public String getContext() {
        return context;
    }

    @JsonProperty("context")
    public void setContext(String context) {
        this.context = context;
    }

    @JsonProperty("inputIndex")
    public Integer getInputIndex() {
        return inputIndex;
    }

    @JsonProperty("inputIndex")
    public void setInputIndex(Integer inputIndex) {
        this.inputIndex = inputIndex;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    } 

}
