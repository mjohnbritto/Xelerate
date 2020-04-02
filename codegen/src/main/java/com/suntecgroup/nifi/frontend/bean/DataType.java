package com.suntecgroup.nifi.frontend.bean;

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
	private Object dateFormat;
	@SerializedName("roundingMode")
	@Expose
	private String roundingMode;

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public Object getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(Object dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(String roundingMode) {
		this.roundingMode = roundingMode;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("type", type)
				.append("precision", precision).append("scale", scale)
				.append("dateFormat", dateFormat)
				.append("roundingMode", roundingMode).toString();
	}

}
