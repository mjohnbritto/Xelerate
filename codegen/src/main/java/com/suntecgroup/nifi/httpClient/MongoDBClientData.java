package com.suntecgroup.nifi.httpClient;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.frontend.bean.BPFlowUIResponse;
import com.suntecgroup.nifi.frontend.bean.CompositeKeyId;
import com.suntecgroup.nifi.xml.CGGenerateNifiXml;

/**
 * MongoDBClientData - This class used for interacting from External services.
 */
@Component
public class MongoDBClientData {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBClientData.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	@Autowired
	private CGGenerateNifiXml bpGenerateXML;
	
	/**
	 * getBPJson - This method used to get the response from External API.
	 * 
	 * @param uri-
	 *            holds the uri of String type
	 * @param departmentName
	 *            - holds departmentName of String type
	 * @param bpName
	 *            - holds the bpname of String type
	 * @return - returns response from External API.
	 * @throws JSONException
	 */
	public BPFlowUIResponse getBPJson(String departmentName, String moduleName, String release, String assetType,
			String assetName) throws CGException {
		final String url = env.getProperty("mongodb.bpservices.getinputjson");

		BPFlowUIResponse bPFlowUIResponse = new BPFlowUIResponse();
		LOGGER.info("Get Diagramjson from API With Department :" + departmentName + ", Module : " + moduleName);
		HttpEntity<String> response = invokeService(url, departmentName, moduleName, release, assetType, assetName);

		JSONObject bpAssetResponse;
		try {

			bpAssetResponse = new JSONObject(response.getBody());
			bpGenerateXML.generateAssetJSONFile(response.getBody());

			if (bpAssetResponse.has("data")) {
				JSONObject bpflow = bpAssetResponse.getJSONObject("data");

				bPFlowUIResponse.setDepartment(bpflow.getString("department"));
				bPFlowUIResponse.setModule(bpflow.getString("module"));
				bPFlowUIResponse.setRelease(bpflow.getString("release"));
				bPFlowUIResponse.setPms(bpflow.getString("pms"));
				bPFlowUIResponse.setArtifactId(bpflow.getInt("artifact_id"));
				bPFlowUIResponse.setAssetType(bpflow.getString("assetType"));
				bPFlowUIResponse.setActionType(bpflow.getString("actionType"));
				bPFlowUIResponse.setAssetName(bpflow.getString("assetName"));
				bPFlowUIResponse.setAssetDetail(bpflow.getString("assetDetail"));
				bPFlowUIResponse.setVersion(bpflow.getString("version"));
				bPFlowUIResponse.setStatus(bpflow.getString("status"));
				bPFlowUIResponse.setCheckOutUser(bpflow.getString("checkOutUser"));
				JSONObject id = bpflow.getJSONObject("compositeKey");

				CompositeKeyId keyId = new CompositeKeyId();
				keyId.setDepartment(id.getString("department"));
				keyId.setModule(id.getString("module"));
				keyId.setPms(bpflow.getString("pms"));
				keyId.setRelease(id.getString("release"));
				keyId.setVersion(id.getInt("version"));

				bPFlowUIResponse.setId(keyId);
			} else {
				LOGGER.error("No asset found matching the input criteria.");
				throw new CGException("No asset found matching the input criteria.");
			}
		} catch (CGException cge) {
			throw cge;
		} catch (Exception e) {
			LOGGER.error("Unable to Parse Json Exception in ()", e);
			throw new CGException("Unable to Parse Json Exception in ()", e);
		}

		return bPFlowUIResponse;
	}

	/**
	 * invokeService - This method will get the response from External API.
	 * 
	 * @param uri-
	 *            holds the uri of String type
	 * @param departmentName
	 *            - holds departmentName of String type
	 * @param bpName
	 *            - holds the bpname of String type
	 * @return - returns response from External API.
	 */
	public HttpEntity<String> invokeService(String uri, String department, String module, String release,
			String assetType, String assetName) {
		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("department", department);
		uriParams.put("module", module);
		uriParams.put("release", release);
		uriParams.put("artifactid", "0");
		uriParams.put("assettype", assetType);
		uriParams.put("assetname", assetName);

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);
		LOGGER.info("URL : " + builder.buildAndExpand(uriParams).toUri());

		HttpEntity<String> requestEntity = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<String> responseEn = restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.GET,
				requestEntity, String.class);

		return responseEn;
	}

}
