
package com.suntecgroup.nifi.template.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DataType {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("precision")
    @Expose
    private int precision;
    @SerializedName("scale")
    @Expose
    private int scale;
    @SerializedName("dateFormat")
    @Expose
    private String dateFormat;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int string) {
        this.precision = string;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("type", type).append("precision", precision).append("scale", scale).append("dateFormat", dateFormat).toString();
    }

}
