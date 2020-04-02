
package com.suntecgroup.bp.designer.frontend.beans;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Content;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Footer;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Header;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.Validation;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.output.Eviction;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.output.OutputFileName;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.output.OFCIHeader;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.output.OFCIFooter;
import com.suntecgroup.bp.designer.frontend.bean.filechannel.output.OFCIContent;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Operators {
	
	@SerializedName("key")
	@Expose
	private String key;

	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("node_location")
	@Expose
	private String nodeLocation;
	@SerializedName("businessSettings")
	@Expose
	private BusinessSettings businessSettings;
	@SerializedName("outputBeType")
	@Expose
	private String outputBeType;
	@SerializedName("properties")
	@Expose
	private List<Property> properties;
	@SerializedName("comments")
	@Expose
	private Comments comments;
	@SerializedName("inputMapping")
	@Expose
	private List<InputParam> inputMapping = null;
	@SerializedName("outputMapping")
	@Expose
	private List<OutputParam> outputMapping = null;

	@SerializedName("processVariable")
	@Expose
	private List<ProcessVariable> processVariable;

	
	@SerializedName("selected")
	@Expose
	private String selected;
	
	@SerializedName("continuous")
	@Expose
	private String continuous;
	
	@SerializedName("header")
	@Expose
	private Header header = null;
	
	@SerializedName("footer")
	@Expose
	private Footer footer = null;
	
	@SerializedName("autoGeneration")
	@Expose
	private boolean autoGeneration = false;

	@SerializedName("autoGenerateMapping")
	@Expose
	private boolean autoGenerateMapping = false;
	
	@SerializedName("pvMapping")
	@Expose
	private List<com.suntecgroup.bp.designer.frontend.bean.filechannel.Mapping> pvMapping = null;
	
	@SerializedName("mapping")
	@Expose
	private List<com.suntecgroup.bp.designer.frontend.bean.filechannel.Mapping> mapping = null;

	@SerializedName("validation")
	@Expose
	private Validation validation = null;

	@SerializedName("content")
	@Expose
	private Content content = null;
	
	@SerializedName("OFCIHeader")
	@Expose
	private OFCIHeader OFCIHeader = null;
	
	@SerializedName("OFCIFooter")
	@Expose
	private OFCIFooter OFCIFooter = null;
	
	@SerializedName("outputFileName")
	private OutputFileName outputFileName;
	
	@SerializedName("eviction")
	@Expose
	private Eviction eviction = null;
	
	@SerializedName("OFCIContent")
	@Expose
	private OFCIContent OFCIContent = null;
	
	public String getOutputBeType() {
		return outputBeType;
	}
	public void setOutputBeType(String outputBeType) {
		this.outputBeType = outputBeType;
	}
	public String getContinuous() {
		return continuous;
	}
	public void setContinuous(String continuous) {
		this.continuous = continuous;
	}
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public Footer getFooter() {
		return footer;
	}
	public void setFooter(Footer footer) {
		this.footer = footer;
	}
	public boolean isAutoGeneration() {
		return autoGeneration;
	}
	public void setAutoGeneration(boolean autoGeneration) {
		this.autoGeneration = autoGeneration;
	}
	public boolean isAutoGenerateMapping() {
		return autoGenerateMapping;
	}
	public void setAutoGenerateMapping(boolean autoGenerateMapping) {
		this.autoGenerateMapping = autoGenerateMapping;
	}
	public List<com.suntecgroup.bp.designer.frontend.bean.filechannel.Mapping> getPvMapping() {
		return pvMapping;
	}
	public void setPvMapping(List<com.suntecgroup.bp.designer.frontend.bean.filechannel.Mapping> pvMapping) {
		this.pvMapping = pvMapping;
	}
	public List<com.suntecgroup.bp.designer.frontend.bean.filechannel.Mapping> getMapping() {
		return mapping;
	}
	public void setMapping(List<com.suntecgroup.bp.designer.frontend.bean.filechannel.Mapping> mapping) {
		this.mapping = mapping;
	}
	public Validation getValidation() {
		return validation;
	}
	public void setValidation(Validation validation) {
		this.validation = validation;
	}
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
	public OFCIHeader getOFCIHeader() {
		return OFCIHeader;
	}
	public void setOFCIHeader(OFCIHeader oFCIHeader) {
		OFCIHeader = oFCIHeader;
	}
	public OFCIFooter getOFCIFooter() {
		return OFCIFooter;
	}
	public void setOFCIFooter(OFCIFooter oFCIFooter) {
		OFCIFooter = oFCIFooter;
	}
	public OutputFileName getOutputFileName() {
		return outputFileName;
	}
	public void setOutputFileName(OutputFileName outputFileName) {
		this.outputFileName = outputFileName;
	}
	public Eviction getEviction() {
		return eviction;
	}
	public void setEviction(Eviction eviction) {
		this.eviction = eviction;
	}
	public OFCIContent getOFCIContent() {
		return OFCIContent;
	}
	public void setOFCIContent(OFCIContent oFCIContent) {
		OFCIContent = oFCIContent;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNodeLocation() {
		return nodeLocation;
	}
	public void setNodeLocation(String node_location) {
		this.nodeLocation = node_location;
	}
	public BusinessSettings getBusinessSettings() {
		return businessSettings;
	}
	public void setBusinessSettings(BusinessSettings businessSettings) {
		this.businessSettings = businessSettings;
	}
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public Comments getComments() {
		return comments;
	}
	public void setComments(Comments comments) {
		this.comments = comments;
	}

	public List<InputParam> getInputMapping() {
		return inputMapping;
	}
	public void setInputMapping(List<InputParam> inputMapping) {
		this.inputMapping = inputMapping;
	}
	public List<OutputParam> getOutputMapping() {
		return outputMapping;
	}

	public void setOutputMapping(List<OutputParam> outputMapping) {
		this.outputMapping = outputMapping;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("type", type)
				.append("node_location", nodeLocation).append("businessSettings", businessSettings)
				.append("properties", properties).append("comments", comments).append("inputMapping", inputMapping)
				.append("outputMapping", outputMapping).toString();
	}

	public List<ProcessVariable> getProcessVariable() {
		return processVariable;
	}

	public void setProcessVariable(List<ProcessVariable> processVariable) {
		this.processVariable = processVariable;
	}

}
