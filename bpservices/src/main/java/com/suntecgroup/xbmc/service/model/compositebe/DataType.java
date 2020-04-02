
package com.suntecgroup.xbmc.service.model.compositebe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "type", "precision", "scale", "roundingMode", "dateFormat" })
public class DataType {

    @JsonProperty("type")
    private String type;

    @JsonProperty("precision")
    private int precision;

    @JsonProperty("scale")
    private int scale;

    @JsonProperty("roundingMode")
    private String roundingMode;

    @JsonProperty("dateFormat")
    private String dateFormat;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("precision")
    public int getPrecision() {
        return precision;
    }

    @JsonProperty("precision")
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    @JsonProperty("scale")
    public int getScale() {
        return scale;
    }

    @JsonProperty("scale")
    public void setScale(int scale) {
        this.scale = scale;
    }

    @JsonProperty("roundingMode")
    public String getRoundingMode() {
        return roundingMode;
    }

    @JsonProperty("roundingMode")
    public void setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
    }

    @JsonProperty("dateFormat")
    public String getDateFormat() {
        return dateFormat;
    }

    @JsonProperty("dateFormat")
    public void setDateFormat(String string) {
        this.dateFormat = string;
    }

}

