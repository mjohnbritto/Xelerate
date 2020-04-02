
package com.suntecgroup.nifi.frontend.bean;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.Content;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.Footer;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.Header;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.Validation;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Eviction;
import com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.OutputFileName;

public class Operators {

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("type")
	@Expose
	private String type;

	@SerializedName("key")
	@Expose
	private String key;

	@SerializedName("node_location")
	@Expose
	private String node_location;

	@SerializedName("businessSettings")
	@Expose
	private BusinessSettings businessSettings;

	@SerializedName("properties")
	@Expose
	private List<Property> properties;

	@SerializedName("comments")
	@Expose
	private Comments comments;

	@SerializedName("selected")
	@Expose
	private String selected;

	@SerializedName("continuous")
	@Expose
	private String continuous;

	@SerializedName("inputMapping")
	@Expose
	private List<InputParam> inputMapping = null;

	@SerializedName("pvMapping")
	@Expose
	private List<com.suntecgroup.nifi.frontend.bean.filechannelintegration.Mapping> pvMapping = null;

	@SerializedName("outputMapping")
	@Expose
	private List<OutputParam> outputMapping = null;

	private boolean eventLogFlag;

	@SerializedName("outputBeMapping")
	@Expose
	private List<OutputParam> outputBeMapping = null;

	@SerializedName("processVariable")
	@Expose
	private List<ProcessVariable> processVariable;

	@Expose
	private List<VisualMapping> smartConnectorMapping = null;

	@SerializedName("header")
	@Expose
	private Header header = null;

	@SerializedName("OFCIHeader")
	@Expose
	private com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Header OFCIHeader = null;

	@SerializedName("footer")
	@Expose
	private Footer footer = null;

	@SerializedName("OFCIFooter")
	@Expose
	private com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Footer OFCIFooter = null;

	@SerializedName("eviction")
	@Expose
	private Eviction eviction = null;

	@SerializedName("mapping")
	@Expose
	private List<com.suntecgroup.nifi.frontend.bean.filechannelintegration.Mapping> mapping = null;

	@SerializedName("validation")
	@Expose
	private Validation validation = null;

	@SerializedName("content")
	@Expose
	private Content content = null;

	@SerializedName("OFCIContent")
	@Expose
	private com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Content OFCIContent = null;

	@SerializedName("autoGeneration")
	@Expose
	private boolean autoGeneration = false;

	@SerializedName("autoGenerateMapping")
	@Expose
	private boolean autoGenerateMapping = false;
	
	@SerializedName("outputFileName")
	private OutputFileName outputFileName;//APIInput
	
	@SerializedName("outputBe")
	@Expose
	private BusinessEntity outputBe;
	
	@SerializedName("outputBeType")
	@Expose
	private String outputBeType;
	
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

	public String getNode_location() {
		return node_location;
	}

	public void setNode_location(String node_location) {
		this.node_location = node_location;
	}

	public boolean isEventLogFlag() {
		return eventLogFlag;
	}

	public void setEventLogFlag(boolean eventLogFlag) {
		this.eventLogFlag = eventLogFlag;
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

	public List<ProcessVariable> getProcessVariable() {
		return processVariable;
	}

	public void setProcessVariable(List<ProcessVariable> processVariable) {
		this.processVariable = processVariable;
	}

	public List<VisualMapping> getSmartConnectorMapping() {
		return smartConnectorMapping;
	}

	public void setSmartConnectorMapping(List<VisualMapping> smartConnectorMapping) {
		this.smartConnectorMapping = smartConnectorMapping;
	}

	public List<com.suntecgroup.nifi.frontend.bean.filechannelintegration.Mapping> getMapping() {
		return mapping;
	}

	public void setMapping(List<com.suntecgroup.nifi.frontend.bean.filechannelintegration.Mapping> mapping) {
		this.mapping = mapping;
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

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public List<OutputParam> getOutputBeMapping() {
		return outputBeMapping;
	}

	public void setOutputBeMapping(List<OutputParam> outputBeMapping) {
		this.outputBeMapping = outputBeMapping;
	}

	public List<com.suntecgroup.nifi.frontend.bean.filechannelintegration.Mapping> getPvMapping() {
		return pvMapping;
	}

	public void setPvMapping(List<com.suntecgroup.nifi.frontend.bean.filechannelintegration.Mapping> pvMapping) {
		this.pvMapping = pvMapping;
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

	public Eviction getEviction() {
		return eviction;
	}

	public void setEviction(Eviction eviction) {
		this.eviction = eviction;
	}

	public String getContinuous() {
		return continuous;
	}

	public void setContinuous(String continuous) {
		this.continuous = continuous;
	}

	public OutputFileName getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(OutputFileName outputFileName) {
		this.outputFileName = outputFileName;
	}

	public com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Header getOFCIHeader() {
		return OFCIHeader;
	}

	public void setOFCIHeader(com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Header oFCIHeader) {
		OFCIHeader = oFCIHeader;
	}

	public com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Footer getOFCIFooter() {
		return OFCIFooter;
	}

	public void setOFCIFooter(com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Footer oFCIFooter) {
		OFCIFooter = oFCIFooter;
	}

	public com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Content getOFCIContent() {
		return OFCIContent;
	}

	public void setOFCIContent(com.suntecgroup.nifi.frontend.bean.filechannelintegration.output.Content oFCIContent) {
		OFCIContent = oFCIContent;
	}

	public BusinessEntity getOutputBe() {
		return outputBe;
	}

	public void setOutputBe(BusinessEntity outputBe) {
		this.outputBe = outputBe;
	}

	public String getOutputBeType() {
		return outputBeType;
	}

	public void setOutputBeType(String outputBeType) {
		this.outputBeType = outputBeType;
	}

	@Override
	public String toString() {
		return "Operator [type=" + type + ", key=" + key + "]";
	}

}