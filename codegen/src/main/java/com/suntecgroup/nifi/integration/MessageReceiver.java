package com.suntecgroup.nifi.integration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowResponseXml;
import com.suntecgroup.nifi.integration.bean.AssetDetail;
import com.suntecgroup.nifi.integration.bean.AssetDetails;
import com.suntecgroup.nifi.integration.bean.BuildStatus;
import com.suntecgroup.nifi.integration.bean.Event;
import com.suntecgroup.nifi.integration.bean.JobStatus;
import com.suntecgroup.nifi.service.CGServiceInterface;
import com.suntecgroup.nifi.util.CGUtils;

@Component
public class MessageReceiver {

	@Autowired
	private CGServiceInterface bpService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);

	private static long MAX_WAIT_DURATION = 20000L;
	private static Date lastBaselineAccessed = new Date();
	private static String lastBuiltAssetTag = "";

	@JmsListener(destination = "${app.config.activemq.queue}", containerFactory = "myFactory")
	public void receiveMessage(Message incomingMessage) {
		BPFlowResponseXml response = null;
		Gson gson = new Gson();
		String message;
		BuildStatus status = null;
		JobStatus jobStatus = null;
		boolean success = false;
		boolean proceedWithBuildAndUpdate = true;
		try {
			if (incomingMessage instanceof BytesMessage) {
				BytesMessage bytesMessage = (BytesMessage) incomingMessage;
				int TEXT_LENGTH = new Long(bytesMessage.getBodyLength()).intValue();
				byte[] textBytes = new byte[TEXT_LENGTH];
				bytesMessage.readBytes(textBytes, TEXT_LENGTH);
				boolean messageStarted = false;
				int bytesLength = textBytes.length;
				byte[] actualTextBytes = new byte[bytesLength];
				int i = 0;
				while (i <= bytesLength - 1) {
					if (!messageStarted && textBytes[i] == 123) {
						messageStarted = true;
					}
					if (messageStarted) {
						actualTextBytes[i] = textBytes[i];
					}
					i++;
				}
				message = new String(actualTextBytes).trim();
			} else if (incomingMessage instanceof ActiveMQBytesMessage) {
				byte[] textBytes = ((ActiveMQBytesMessage) incomingMessage).getContent().getData();
				boolean messageStarted = false;
				int bytesLength = textBytes.length;
				byte[] actualTextBytes = new byte[bytesLength];
				int i = 0;
				while (i <= bytesLength - 1) {
					if (!messageStarted && textBytes[i] == 123) {
						messageStarted = true;
					}
					if (messageStarted) {
						actualTextBytes[i] = textBytes[i];
					}
					i++;
				}
				message = new String(actualTextBytes).trim();
			} else if (incomingMessage instanceof ActiveMQTextMessage) {
				byte[] textBytes = ((ActiveMQTextMessage) incomingMessage).getContent().getData();
				boolean messageStarted = false;
				int bytesLength = textBytes.length;
				byte[] actualTextBytes = new byte[bytesLength];
				int i = 0;
				while (i <= bytesLength - 1) {
					if (!messageStarted && textBytes[i] == 123) {
						messageStarted = true;
					}
					if (messageStarted) {
						actualTextBytes[i] = textBytes[i];
					}
					i++;
				}
				message = new String(actualTextBytes).trim();
			} else {
				message = incomingMessage.getBody(String.class);
			}
			LOGGER.info("Received message: {}", message);
			Event event = gson.fromJson(message, Event.class);

			AssetDetails assetDetails = event.getAssetDetails();

			String assetTag = assetDetails.getDepartment() + "," + assetDetails.getModule() + ","
					+ assetDetails.getRelease() + "," + assetDetails.getAssetName();

			if ((new Date().getTime() - lastBaselineAccessed.getTime()) < MAX_WAIT_DURATION
					&& assetTag.equals(lastBuiltAssetTag)) {
				proceedWithBuildAndUpdate = false;
			} else {
				lastBuiltAssetTag = assetTag;
				lastBaselineAccessed = new Date();
			}

			if (proceedWithBuildAndUpdate) {
				response = bpService.createNifiTemplate(assetDetails.getDepartment(), assetDetails.getModule(),
						assetDetails.getRelease(), assetDetails.getAssetType(), assetDetails.getAssetName());

				status = new BuildStatus();
				jobStatus = new JobStatus();
				jobStatus.setJobId(101 + new Date().getSeconds());
				List<AssetDetail> assetDetailsArray = new ArrayList<AssetDetail>();
				AssetDetail asset = new AssetDetail();
				asset.setAssetName(assetDetails.getAssetName());
				asset.setAssetType(assetDetails.getAssetType());
				asset.setDepartment(assetDetails.getDepartment());
				asset.setModule(assetDetails.getModule());
				asset.setRelease(assetDetails.getRelease());
				assetDetailsArray.add(asset);
				status.setAssetDetails(assetDetailsArray);
				status.setJobStatus(jobStatus);
				status.setBuildType("Asset");
				status.setPmsIdentifier(assetDetails.getPmsId());
				if (null != response && null != response.getTemplate()) {
					jobStatus.setStatus("COMPLETED");
					success = true;
				} else {
					jobStatus.setStatus("FAILED");
					success = false;
				}
			}

		} catch (JMSException e) {
			success = false;
			LOGGER.error("Exception while reading message from queue", e);
		} finally {
			if (proceedWithBuildAndUpdate) {
				if (!success) {
					jobStatus.setStatus("FAILED");
				}
				updateXBMC(status);
			}
		}
	}

	private void updateXBMC(BuildStatus status) {
		String url = env.getProperty("bpservice.xbmc.updatebuildjobstatus.url");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		String jsonStr = CGUtils.convertJaveToJson(status);
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr, headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		try {
			restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.POST, entity, String.class);
		} catch (Exception e) {
			LOGGER.error("Update XBMC build job status API error" + e.getMessage());
			throw new CGException("Update XBMC build job status API error", e);
		}
	}
}
