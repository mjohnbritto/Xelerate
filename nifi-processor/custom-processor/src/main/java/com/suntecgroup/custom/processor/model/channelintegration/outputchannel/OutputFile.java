package com.suntecgroup.custom.processor.model.channelintegration.outputchannel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "staticName", "dynamicName", "timeStampFormat" })
public class OutputFile {

    @JsonProperty("staticName")
    private String staticName;

    @JsonProperty("dynamicName")
    private String dynamicName;

    @JsonProperty("timeStampFormat")
    private String timeStampFormat;

	@JsonProperty("staticName")
    public String getStaticName() {
        return staticName;
    }

    @JsonProperty("staticName")
    public void setStaticName(String staticName) {
        this.staticName = staticName;
    }

    @JsonProperty("dynamicName")
    public String getDynamicName() {
        return dynamicName;
    }

    @JsonProperty("dynamicName")
    public void setDynamicName(String dynamicName) {
        this.dynamicName = dynamicName;
    }

    @JsonProperty("timeStampFormat")
	public String getTimeStampFormat() {
		return timeStampFormat;
	}

    @JsonProperty("timeStampFormat")
	public void setTimeStampFormat(String timeStampFormat) {
		this.timeStampFormat = timeStampFormat;
	}

}