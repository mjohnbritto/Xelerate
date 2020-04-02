package com.suntecgroup.nifi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "process")
@PropertySource("${app.config.location}businessprocess")
@Component
public class CGConfigurationProperty {

	private String customProcessordecisionMatrixInclusiveType;
	private String customProcessordecisionMatrixInclusiveAppName;

	private String customProcessordecisionMatrixExclusiveType;
	private String customProcessordecisionMatrixExclusiveAppName;

	private String customProcessorConnectorProcessorType;
	private String customProcessorConnectorProcessorAppName;

	private String UpdateAttributeBundleGroup;
	private String UpdateAttributeBundleArtifact;
	private String UpdateAttributeBundleVersion;

	private String invokeHttpComponentType;
	private String invokeHttpBundleGroup;
	private String invokeHttpBundleArtifact;
	private String invokeHttpBundleVersion;
	private String invokeHttpAppName;

	private String connectionProcessorType;
	private String connectionFunnelType;

	private String sizeThreshold;
	private String objectThreshold;
	private String flowFileExpiration;
	private String labelIndex;
	private String zIndex;
	private String groupID;
	private String templateName;
	private String encodingVersion;

	private String customBundleGroup;
	private String customBundleArtifact;
	private String customBundleVersion;

	private String customStartComponentType;
	private String customStartAppName;

	private String customEndComponentType;
	private String customEndAppName;

	private String customListenHttpComponentType;
	private String customListenHttpAppName;

	private String customPreProcessorIBSType;
	private String customPreProcessorIBSAppName;

	private String customPostProcessorIBSType;
	private String customPostProcessorIBSAppName;

	private String customFailureComponentType;
	private String customFailureComponentAppName;

	private String customBusinessFailureComponentType;
	private String customBusinessFailureComponentAppName;

	private String customPostEventLoggerComponentType;
	private String customPostEventLoggerComponentAppName;

	private String customPreEventLoggerComponentType;
	private String customPreEventLoggerComponentAppName;

	private String UpdateAttributeComponentType;
	private String UpdateAttributeComponentAppName;

	private String customInvokeHttpComponentType;
	private String custominvokeHttpAppName;

	private String getFileComponentType;
	private String getFileAppName;

	private String putFileComponentType;
	private String putFileAppName;

	private String customFixedWidthInputFileChannelComponentType;
	private String customDelimitedInputFileChannelComponentType;
	private String customFileChannelIntegrationInputAppName;

	private String customFixedWidthOutputFileChannelComponentType;
	private String customDelimitedOutputFileChannelComponentType;
	private String customFileChannelIntegrationOutputAppName;

	private String customInputRestCIProcessorComponentType;
	private String customRestChannelIntegrationInputAppName;
	
	private String customOutputRestCIProcessorComponentType;
	private String customRestChannelIntegrationOutputAppName;
	
	private String customPreMergeComponentType;
	private String customPostMergeComponentType;
	private String customMergeProcessorAppName;

	private String customJoinComponentType;
	private String customJoinAppName;

	private String customKafkaConsumerIBSType;
	private String customKafkaConsumerIBSAppName;
	
	public String getCustomJoinComponentType() {
		return customJoinComponentType;
	}

	public void setCustomJoinComponentType(String customJoinComponentType) {
		this.customJoinComponentType = customJoinComponentType;
	}

	public String getCustomJoinAppName() {
		return customJoinAppName;
	}

	public void setCustomJoinAppName(String customJoinAppName) {
		this.customJoinAppName = customJoinAppName;
	}

	public String getCustomBundleOneVersion() {
		return customBundleOneVersion;
	}

	public void setCustomBundleOneVersion(String customBundleOneVersion) {
		this.customBundleOneVersion = customBundleOneVersion;
	}

	private String customBundleOneVersion;

	public String getCustomBusinessFailureComponentType() {
		return customBusinessFailureComponentType;
	}

	public void setCustomBusinessFailureComponentType(String customBusinessFailureComponentType) {
		this.customBusinessFailureComponentType = customBusinessFailureComponentType;
	}

	public String getCustomBusinessFailureComponentAppName() {
		return customBusinessFailureComponentAppName;
	}

	public void setCustomBusinessFailureComponentAppName(String customBusinessFailureComponentAppName) {
		this.customBusinessFailureComponentAppName = customBusinessFailureComponentAppName;
	}

	public String getInvokeHttpComponentType() {
		return invokeHttpComponentType;
	}

	public void setInvokeHttpComponentType(String invokeHttpComponentType) {
		this.invokeHttpComponentType = invokeHttpComponentType;
	}

	public String getInvokeHttpBundleGroup() {
		return invokeHttpBundleGroup;
	}

	public void setInvokeHttpBundleGroup(String invokeHttpBundleGroup) {
		this.invokeHttpBundleGroup = invokeHttpBundleGroup;
	}

	public String getInvokeHttpBundleArtifact() {
		return invokeHttpBundleArtifact;
	}

	public void setInvokeHttpBundleArtifact(String invokeHttpBundleArtifact) {
		this.invokeHttpBundleArtifact = invokeHttpBundleArtifact;
	}

	public String getInvokeHttpBundleVersion() {
		return invokeHttpBundleVersion;
	}

	public void setInvokeHttpBundleVersion(String invokeHttpBundleVersion) {
		this.invokeHttpBundleVersion = invokeHttpBundleVersion;
	}

	public String getInvokeHttpAppName() {
		return invokeHttpAppName;
	}

	public void setInvokeHttpAppName(String invokeHttpAppName) {
		this.invokeHttpAppName = invokeHttpAppName;
	}

	public String getConnectionProcessorType() {
		return connectionProcessorType;
	}

	public void setConnectionProcessorType(String connectionProcessorType) {
		this.connectionProcessorType = connectionProcessorType;
	}

	public String getConnectionFunnelType() {
		return connectionFunnelType;
	}

	public void setConnectionFunnelType(String connectionFunnelType) {
		this.connectionFunnelType = connectionFunnelType;
	}

	public String getSizeThreshold() {
		return sizeThreshold;
	}

	public void setSizeThreshold(String sizeThreshold) {
		this.sizeThreshold = sizeThreshold;
	}

	public String getObjectThreshold() {
		return objectThreshold;
	}

	public void setObjectThreshold(String objectThreshold) {
		this.objectThreshold = objectThreshold;
	}

	public String getFlowFileExpiration() {
		return flowFileExpiration;
	}

	public void setFlowFileExpiration(String flowFileExpiration) {
		this.flowFileExpiration = flowFileExpiration;
	}

	public String getLabelIndex() {
		return labelIndex;
	}

	public void setLabelIndex(String labelIndex) {
		this.labelIndex = labelIndex;
	}

	public String getzIndex() {
		return zIndex;
	}

	public void setzIndex(String zIndex) {
		this.zIndex = zIndex;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getEncodingVersion() {
		return encodingVersion;
	}

	public void setEncodingVersion(String encodingVersion) {
		this.encodingVersion = encodingVersion;
	}

	public String getCustomStartComponentType() {
		return customStartComponentType;
	}

	public void setCustomStartComponentType(String customStartComponentType) {
		this.customStartComponentType = customStartComponentType;
	}

	public String getCustomBundleGroup() {
		return customBundleGroup;
	}

	public void setCustomBundleGroup(String customBundleGroup) {
		this.customBundleGroup = customBundleGroup;
	}

	public String getCustomBundleArtifact() {
		return customBundleArtifact;
	}

	public void setCustomBundleArtifact(String customBundleArtifact) {
		this.customBundleArtifact = customBundleArtifact;
	}

	public String getCustomBundleVersion() {
		return customBundleVersion;
	}

	public void setCustomBundleVersion(String customBundleVersion) {
		this.customBundleVersion = customBundleVersion;
	}

	public String getCustomStartAppName() {
		return customStartAppName;
	}

	public void setCustomStartAppName(String customStartAppName) {
		this.customStartAppName = customStartAppName;
	}

	public String getCustomEndComponentType() {
		return customEndComponentType;
	}

	public void setCustomEndComponentType(String customEndComponentType) {
		this.customEndComponentType = customEndComponentType;
	}

	public String getCustomEndAppName() {
		return customEndAppName;
	}

	public void setCustomEndAppName(String customEndAppName) {
		this.customEndAppName = customEndAppName;
	}

	public String getCustomListenHttpComponentType() {
		return customListenHttpComponentType;
	}

	public void setCustomListenHttpComponentType(String customListenHttpComponentType) {
		this.customListenHttpComponentType = customListenHttpComponentType;
	}

	public String getCustomListenHttpAppName() {
		return customListenHttpAppName;
	}

	public void setCustomListenHttpAppName(String customListenHttpAppName) {
		this.customListenHttpAppName = customListenHttpAppName;
	}

	public String getCustomPreProcessorIBSType() {
		return customPreProcessorIBSType;
	}

	public void setCustomPreProcessorIBSType(String customPreProcessorIBSType) {
		this.customPreProcessorIBSType = customPreProcessorIBSType;
	}

	public String getCustomPreProcessorIBSAppName() {
		return customPreProcessorIBSAppName;
	}

	public void setCustomPreProcessorIBSAppName(String customPreProcessorIBSAppName) {
		this.customPreProcessorIBSAppName = customPreProcessorIBSAppName;
	}

	public String getCustomPostProcessorIBSType() {
		return customPostProcessorIBSType;
	}

	public void setCustomPostProcessorIBSType(String customPostProcessorIBSType) {
		this.customPostProcessorIBSType = customPostProcessorIBSType;
	}

	public String getCustomPostProcessorIBSAppName() {
		return customPostProcessorIBSAppName;
	}

	public void setCustomPostProcessorIBSAppName(String customPostProcessorIBSAppName) {
		this.customPostProcessorIBSAppName = customPostProcessorIBSAppName;
	}

	public String getCustomProcessorConnectorProcessorType() {
		return customProcessorConnectorProcessorType;
	}

	public void setCustomProcessorConnectorProcessorType(String customProcessorConnectorProcessorType) {
		this.customProcessorConnectorProcessorType = customProcessorConnectorProcessorType;
	}

	public String getCustomProcessorConnectorProcessorAppName() {
		return customProcessorConnectorProcessorAppName;
	}

	public void setCustomProcessorConnectorProcessorAppName(String customProcessorConnectorProcessorAppName) {
		this.customProcessorConnectorProcessorAppName = customProcessorConnectorProcessorAppName;
	}

	public String getCustomFailureComponentType() {
		return customFailureComponentType;
	}

	public void setCustomFailureComponentType(String customFailureComponentType) {
		this.customFailureComponentType = customFailureComponentType;
	}

	public String getCustomFailureComponentAppName() {
		return customFailureComponentAppName;
	}

	public void setCustomFailureComponentAppName(String customFailureComponentAppName) {
		this.customFailureComponentAppName = customFailureComponentAppName;
	}

	public String getCustomPostEventLoggerComponentType() {
		return customPostEventLoggerComponentType;
	}

	public void setCustomPostEventLoggerComponentType(String customPostEventLoggerComponentType) {
		this.customPostEventLoggerComponentType = customPostEventLoggerComponentType;
	}

	public String getCustomPostEventLoggerComponentAppName() {
		return customPostEventLoggerComponentAppName;
	}

	public void setCustomPostEventLoggerComponentAppName(String customPostEventLoggerComponentAppName) {
		this.customPostEventLoggerComponentAppName = customPostEventLoggerComponentAppName;
	}

	public String getCustomPreEventLoggerComponentType() {
		return customPreEventLoggerComponentType;
	}

	public void setCustomPreEventLoggerComponentType(String customPreEventLoggerComponentType) {
		this.customPreEventLoggerComponentType = customPreEventLoggerComponentType;
	}

	public String getCustomPreEventLoggerComponentAppName() {
		return customPreEventLoggerComponentAppName;
	}

	public void setCustomPreEventLoggerComponentAppName(String customPreEventLoggerComponentAppName) {
		this.customPreEventLoggerComponentAppName = customPreEventLoggerComponentAppName;
	}

	public String getUpdateAttributeComponentType() {
		return UpdateAttributeComponentType;
	}

	public void setUpdateAttributeComponentType(String updateAttributeComponentType) {
		UpdateAttributeComponentType = updateAttributeComponentType;
	}

	public String getUpdateAttributeComponentAppName() {
		return UpdateAttributeComponentAppName;
	}

	public void setUpdateAttributeComponentAppName(String updateAttributeComponentAppName) {
		UpdateAttributeComponentAppName = updateAttributeComponentAppName;
	}

	public String getUpdateAttributeBundleGroup() {
		return UpdateAttributeBundleGroup;
	}

	public void setUpdateAttributeBundleGroup(String updateAttributeBundleGroup) {
		UpdateAttributeBundleGroup = updateAttributeBundleGroup;
	}

	public String getUpdateAttributeBundleArtifact() {
		return UpdateAttributeBundleArtifact;
	}

	public void setUpdateAttributeBundleArtifact(String updateAttributeBundleArtifact) {
		UpdateAttributeBundleArtifact = updateAttributeBundleArtifact;
	}

	public String getUpdateAttributeBundleVersion() {
		return UpdateAttributeBundleVersion;
	}

	public void setUpdateAttributeBundleVersion(String updateAttributeBundleVersion) {
		UpdateAttributeBundleVersion = updateAttributeBundleVersion;
	}

	public String getCustomProcessordecisionMatrixInclusiveType() {
		return customProcessordecisionMatrixInclusiveType;
	}

	public void setCustomProcessordecisionMatrixInclusiveType(String customProcessordecisionMatrixInclusiveType) {
		this.customProcessordecisionMatrixInclusiveType = customProcessordecisionMatrixInclusiveType;
	}

	public String getCustomProcessordecisionMatrixInclusiveAppName() {
		return customProcessordecisionMatrixInclusiveAppName;
	}

	public void setCustomProcessordecisionMatrixInclusiveAppName(String customProcessordecisionMatrixInclusiveAppName) {
		this.customProcessordecisionMatrixInclusiveAppName = customProcessordecisionMatrixInclusiveAppName;
	}

	public String getCustomProcessordecisionMatrixExclusiveType() {
		return customProcessordecisionMatrixExclusiveType;
	}

	public void setCustomProcessordecisionMatrixExclusiveType(String customProcessordecisionMatrixExclusiveType) {
		this.customProcessordecisionMatrixExclusiveType = customProcessordecisionMatrixExclusiveType;
	}

	public String getCustomProcessordecisionMatrixExclusiveAppName() {
		return customProcessordecisionMatrixExclusiveAppName;
	}

	public void setCustomProcessordecisionMatrixExclusiveAppName(String customProcessordecisionMatrixExclusiveAppName) {
		this.customProcessordecisionMatrixExclusiveAppName = customProcessordecisionMatrixExclusiveAppName;
	}

	public String getCustomInvokeHttpComponentType() {
		return customInvokeHttpComponentType;
	}

	public void setCustomInvokeHttpComponentType(String customInvokeHttpComponentType) {
		this.customInvokeHttpComponentType = customInvokeHttpComponentType;
	}

	public String getCustominvokeHttpAppName() {
		return custominvokeHttpAppName;
	}

	public void setCustominvokeHttpAppName(String custominvokeHttpAppName) {
		this.custominvokeHttpAppName = custominvokeHttpAppName;
	}

	public String getGetFileComponentType() {
		return getFileComponentType;
	}

	public void setGetFileComponentType(String getFileComponentType) {
		this.getFileComponentType = getFileComponentType;
	}

	public String getGetFileAppName() {
		return getFileAppName;
	}

	public void setGetFileAppName(String getFileAppName) {
		this.getFileAppName = getFileAppName;
	}

	public String getPutFileComponentType() {
		return putFileComponentType;
	}

	public void setPutFileComponentType(String putFileComponentType) {
		this.putFileComponentType = putFileComponentType;
	}

	public String getPutFileAppName() {
		return putFileAppName;
	}

	public void setPutFileAppName(String putFileAppName) {
		this.putFileAppName = putFileAppName;
	}

	public String getCustomFixedWidthInputFileChannelComponentType() {
		return customFixedWidthInputFileChannelComponentType;
	}

	public void setCustomFixedWidthInputFileChannelComponentType(String customFixedWidthInputFileChannelComponentType) {
		this.customFixedWidthInputFileChannelComponentType = customFixedWidthInputFileChannelComponentType;
	}

	public String getCustomDelimitedInputFileChannelComponentType() {
		return customDelimitedInputFileChannelComponentType;
	}

	public void setCustomDelimitedInputFileChannelComponentType(String customDelimitedInputFileChannelComponentType) {
		this.customDelimitedInputFileChannelComponentType = customDelimitedInputFileChannelComponentType;
	}

	public String getCustomFileChannelIntegrationInputAppName() {
		return customFileChannelIntegrationInputAppName;
	}

	public void setCustomFileChannelIntegrationInputAppName(String customFileChannelIntegrationInputAppName) {
		this.customFileChannelIntegrationInputAppName = customFileChannelIntegrationInputAppName;
	}

	public String getCustomFixedWidthOutputFileChannelComponentType() {
		return customFixedWidthOutputFileChannelComponentType;
	}

	public void setCustomFixedWidthOutputFileChannelComponentType(String customFixedWidthOutputFileChannelComponentType) {
		this.customFixedWidthOutputFileChannelComponentType = customFixedWidthOutputFileChannelComponentType;
	}

	public String getCustomDelimitedOutputFileChannelComponentType() {
		return customDelimitedOutputFileChannelComponentType;
	}

	public void setCustomDelimitedOutputFileChannelComponentType(String customDelimitedOutputFileChannelComponentType) {
		this.customDelimitedOutputFileChannelComponentType = customDelimitedOutputFileChannelComponentType;
	}

	public String getCustomFileChannelIntegrationOutputAppName() {
		return customFileChannelIntegrationOutputAppName;
	}

	public void setCustomFileChannelIntegrationOutputAppName(String customFileChannelIntegrationOutputAppName) {
		this.customFileChannelIntegrationOutputAppName = customFileChannelIntegrationOutputAppName;
	}

	public String getCustomPreMergeComponentType() {
		return customPreMergeComponentType;
	}

	public void setCustomPreMergeComponentType(String customPreMergeComponentType) {
		this.customPreMergeComponentType = customPreMergeComponentType;
	}

	public String getCustomPostMergeComponentType() {
		return customPostMergeComponentType;
	}

	public void setCustomPostMergeComponentType(String customPostMergeComponentType) {
		this.customPostMergeComponentType = customPostMergeComponentType;
	}

	public String getCustomMergeProcessorAppName() {
		return customMergeProcessorAppName;
	}

	public void setCustomMergeProcessorAppName(String customMergeProcessorAppName) {
		this.customMergeProcessorAppName = customMergeProcessorAppName;
	}

	public String getCustomKafkaConsumerIBSType() {
		return customKafkaConsumerIBSType;
	}

	public void setCustomKafkaConsumerIBSType(String customKafkaConsumerIBSType) {
		this.customKafkaConsumerIBSType = customKafkaConsumerIBSType;
	}

	public String getCustomKafkaConsumerIBSAppName() {
		return customKafkaConsumerIBSAppName;
	}

	public void setCustomKafkaConsumerIBSAppName(String customKafkaConsumerIBSAppName) {
		this.customKafkaConsumerIBSAppName = customKafkaConsumerIBSAppName;
	}

	public String getCustomInputRestCIProcessorComponentType() {
		return customInputRestCIProcessorComponentType;
	}

	public void setCustomInputRestCIProcessorComponentType(String customInputRestCIProcessorComponentType) {
		this.customInputRestCIProcessorComponentType = customInputRestCIProcessorComponentType;
	}

	public String getCustomRestChannelIntegrationInputAppName() {
		return customRestChannelIntegrationInputAppName;
	}

	public void setCustomRestChannelIntegrationInputAppName(String customRestChannelIntegrationInputAppName) {
		this.customRestChannelIntegrationInputAppName = customRestChannelIntegrationInputAppName;
	}

	public String getCustomOutputRestCIProcessorComponentType() {
		return customOutputRestCIProcessorComponentType;
	}

	public void setCustomOutputRestCIProcessorComponentType(String customOutputRestCIProcessorComponentType) {
		this.customOutputRestCIProcessorComponentType = customOutputRestCIProcessorComponentType;
	}

	public String getCustomRestChannelIntegrationOutputAppName() {
		return customRestChannelIntegrationOutputAppName;
	}

	public void setCustomRestChannelIntegrationOutputAppName(String customRestChannelIntegrationOutputAppName) {
		this.customRestChannelIntegrationOutputAppName = customRestChannelIntegrationOutputAppName;
	}

}