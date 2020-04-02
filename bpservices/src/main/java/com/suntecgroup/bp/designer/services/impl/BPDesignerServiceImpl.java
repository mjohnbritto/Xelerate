package com.suntecgroup.bp.designer.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.frontend.beans.BPFlowUI;
import com.suntecgroup.bp.designer.frontend.beans.Comments;
import com.suntecgroup.bp.designer.frontend.beans.Operators;
import com.suntecgroup.bp.designer.frontend.beans.Property;
import com.suntecgroup.bp.designer.model.Asset;
import com.suntecgroup.bp.designer.model.AssetComments;
import com.suntecgroup.bp.designer.model.Comment;
import com.suntecgroup.bp.designer.model.CompositeKey;
import com.suntecgroup.bp.designer.model.Fields;
import com.suntecgroup.bp.designer.model.Response;
import com.suntecgroup.bp.designer.model.Review;
import com.suntecgroup.bp.designer.model.ReviewNew;
import com.suntecgroup.bp.designer.model.ReviewReportResponse;
import com.suntecgroup.bp.designer.model.SendForReview;
import com.suntecgroup.bp.designer.model.Status;
import com.suntecgroup.bp.designer.model.UserComments;
import com.suntecgroup.bp.designer.model.UserDetails;
import com.suntecgroup.bp.designer.repository.BPDesignerRepositoryInterface;
import com.suntecgroup.bp.designer.review.report.ConfigBPReport;
import com.suntecgroup.bp.designer.review.report.DecisionMatrixReport;
import com.suntecgroup.bp.designer.review.report.EndOperatorReport;
import com.suntecgroup.bp.designer.review.report.FileChannelOperatorReport;
import com.suntecgroup.bp.designer.review.report.InvokeBSExternalReport;
import com.suntecgroup.bp.designer.review.report.InvokeBSInternalReport;
import com.suntecgroup.bp.designer.review.report.JoinOperatorReport;
import com.suntecgroup.bp.designer.review.report.MergeOperatorReport;
import com.suntecgroup.bp.designer.review.report.RestChannelOperatorReport;
import com.suntecgroup.bp.designer.review.report.SmartConnectorReport;
import com.suntecgroup.bp.designer.review.report.StartOperatorReport;
import com.suntecgroup.bp.designer.services.BPDesignerServiceInterface;
import com.suntecgroup.bp.util.BPConstant;
import com.suntecgroup.bp.util.BPUtil;
import com.suntecgroup.bp.util.ResourceUtil;
import com.suntecgroup.xbmc.service.model.ReviewStatus;
import com.suntecgroup.xbmc.service.model.XbmcBuildJobStatusAPIResponse;
import com.suntecgroup.xbmc.service.model.XbmcResponse;
import com.suntecgroup.xbmc.service.model.XbmcValidateAccessResponse;
import com.suntecgroup.xbmc.service.model.baseVersion.BaseVersionRequest;
import com.suntecgroup.xbmc.service.model.baseVersion.BaseVersionResponse;
import com.suntecgroup.xbmc.service.model.buildstatus.AssetDetail;
import com.suntecgroup.xbmc.service.model.buildstatus.BuildStatus;
import com.suntecgroup.xbmc.service.model.compositebe.Anchor;
import com.suntecgroup.xbmc.service.model.compositebe.BusinessEntityAttributeProperty;
import com.suntecgroup.xbmc.service.model.compositebe.BusinessEntityAttributes;
import com.suntecgroup.xbmc.service.model.compositebe.CBEMapping;
import com.suntecgroup.xbmc.service.model.compositebe.DataType;
import com.suntecgroup.xbmc.service.model.compositebe.EffectiveBE;
import com.suntecgroup.xbmc.service.model.compositebe.Relations;
import com.suntecgroup.xbmc.service.model.compositebe.Target;
import com.suntecgroup.xbmc.service.model.compositebe.XbmcBEResponse;

/**
 * Implementation class for business logic
 *
 */
@Service
public class BPDesignerServiceImpl implements BPDesignerServiceInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(BPDesignerServiceImpl.class);

	@Autowired
	private BPDesignerRepositoryInterface bpDesignerRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;
	Gson gson = new Gson();

	/**
	 * getBEType - This method will fetch the Business Entity Types from
	 * External API and returns the response to the BPController.
	 * 
	 * @param department,module,release,bename
	 * 
	 * @return response object containing Business Entity details
	 */

	public Response<List<?>> getBEType(final String token, String department, String module, String release,
			String bename) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		final String url = env.getProperty("xbmc.service.betype.url");
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("department", department)
				.queryParam("module", module).queryParam("release", release).queryParam("bename", bename);

		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<String> remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(),
				HttpMethod.GET, entity, String.class);
		String responseBody = remoteServiceRes.getBody();
		Response<List<?>> response = null;
		ObjectMapper objectmapper = new ObjectMapper();
		XbmcResponse<?> xbmcresponse = null;
		try {
			if (ResourceUtil.isError(remoteServiceRes.getStatusCode())) {
				xbmcresponse = objectmapper.readValue(responseBody, XbmcResponse.class);
				throw new BPException(xbmcresponse.getMessage());
			} else {
				xbmcresponse = objectmapper.readValue(responseBody, XbmcResponse.class);
				response = new Response<List<?>>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
						xbmcresponse.getData());
			}
		} catch (IOException e) {
			LOGGER.error("XBMC Response parsing error" + e.getMessage());
			throw new BPException("XBMC Response parsing error");
		}
		return response;
	}

	/**
	 * getBSServiceDetails - This method will fetch the Business Service Details
	 * from the External API and returns response to BPController.
	 * 
	 * @param department,module,release,bename
	 * 
	 * @return response object containing Business Service details
	 */

	public Response<List<?>> getBSServiceDetails(final String token, String department, String module, String release,
			String bsname, Long pmsId) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		final String url = env.getProperty("xbmc.service.bs.url");

		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("department", department)
				.queryParam("module", module).queryParam("release", release).queryParam("bsname", bsname);
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<String> remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(),
				HttpMethod.GET, entity, String.class);
		String responseBody = remoteServiceRes.getBody();
		Response<List<?>> response = null;
		ObjectMapper objectmapper = new ObjectMapper();
		XbmcResponse<?> xbmcresponse = null;
		try {
			if (ResourceUtil.isError(remoteServiceRes.getStatusCode())) {
				xbmcresponse = objectmapper.readValue(responseBody, XbmcResponse.class);
				throw new BPException(xbmcresponse.getMessage());
			} else {
				xbmcresponse = objectmapper.readValue(responseBody, XbmcResponse.class);
				response = new Response<List<?>>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
						xbmcresponse.getData());
			}
		} catch (IOException e) {
			LOGGER.error("XBMC Response parsing error" + e.getMessage());
			throw new BPException("XBMC Response parsing error");
		}

		return response;
	}

	/**
	 * getBSEffectiveDetails - This method will fetch the Business Service
	 * Details from the External API and returns response to BPController.
	 * 
	 * @param department,module,release,bename
	 * 
	 * @return response object containing Business Service details
	 */

	public Response<List<?>> getBSEffectiveDetails(final String token, String department, String module, String release,
			String bsname, Long pmsId) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		final String url = env.getProperty("xbmc.service.bs.effective.url");

		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("department", department)
				.queryParam("module", module).queryParam("release", release).queryParam("bsName", bsname);
		if (pmsId != null && pmsId > 0) {
			builder.queryParam("pmsId", pmsId);
		}

		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<String> remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(),
				HttpMethod.GET, entity, String.class);
		String responseBody = remoteServiceRes.getBody();
		Response<List<?>> response = null;
		ObjectMapper objectmapper = new ObjectMapper();
		XbmcResponse<?> xbmcresponse = null;
		try {
			if (ResourceUtil.isError(remoteServiceRes.getStatusCode())) {
				xbmcresponse = objectmapper.readValue(responseBody, XbmcResponse.class);
				throw new BPException(xbmcresponse.getMessage());
			} else {
				xbmcresponse = objectmapper.readValue(responseBody, XbmcResponse.class);
				response = new Response<List<?>>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
						xbmcresponse.getData());
			}
		} catch (IOException e) {
			LOGGER.error("XBMC Response parsing error" + e.getMessage());
			throw new BPException("XBMC Response parsing error");
		}

		return response;
	}

	/**
	 * This method will give the delete the BP asset.
	 */
	public Response<String> deleteBP(String department, String module, String release, int artifact_id,
			String assetType, String assetName) {
		Response<String> response;
		bpDesignerRepository.deleteBP(department, module, release, artifact_id, assetType, assetName);
		response = new Response<String>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
				BPConstant.BP_DELETED_MSG);
		return response;
	}

	/**
	 * The user landed from xbmc to BP designer, the UI side get the access
	 * token by using xbmc plugin. This will help to validate the token again
	 * and ensure that user is really logged in from xbmc. This kind of second
	 * level verification for the token
	 */
	public Response<?> validateAccessToken(final String token, final UserDetails user) {
		String jsonStr = BPUtil.convertJaveToJson(user);
		if (jsonStr.isEmpty()) {
			throw new BPException(BPConstant.USER_DETAILS_EMPTY);
		}
		if (token.isEmpty()) {
			throw new BPException(BPConstant.TOKEN_EMPTY);
		}
		final String url = env.getProperty("xbmc.service.validate.token");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr, headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("type", BPConstant.BP_TYPE);
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<String> remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(),
				HttpMethod.POST, entity, String.class);
		String responseBody = remoteServiceRes.getBody();
		Response<XbmcValidateAccessResponse> response = null;
		ObjectMapper objectmapper = new ObjectMapper();
		XbmcValidateAccessResponse xbmcObj = null;
		try {
			if (ResourceUtil.isError(remoteServiceRes.getStatusCode())) {
				xbmcObj = objectmapper.readValue(responseBody, XbmcValidateAccessResponse.class);
				throw new BPException(xbmcObj.getMessage());
			} else {
				xbmcObj = objectmapper.readValue(responseBody, XbmcValidateAccessResponse.class);
				response = new Response<XbmcValidateAccessResponse>(Status.SUCCESS.getStatusCode(), Status.SUCCESS,
						null, xbmcObj);
			}
		} catch (IOException e) {
			LOGGER.error("XBMC Response parsing error" + e.getMessage());
			throw new BPException("XBMC Response parsing error");
		}
		return response;
	}

	/**
	 * This method will be inform the xbmc that the BP flow has been completed.
	 * Once submitted, the xbmc side notify the user for taking review.
	 */
	public Response<?> review(final String token, final SendForReview reviewjson) {
		CompositeKey compositeKey = new CompositeKey();
		Review reviewOld = reviewjson.getReview();
		ReviewNew review = new ReviewNew();
		review.setDependencies(reviewOld.getDependencies());
		review.setExtendable(reviewOld.isExtendable() ? "true" : "false");
		review.setProfileable(reviewOld.isProfileable() ? "true" : "false");
		review.setPmsAssetId(reviewOld.getPmsAssetId());
		
		String jsonStr = BPUtil.convertJaveToJson(review);
		if (jsonStr.isEmpty()) {
			throw new BPException(BPConstant.REVIEW_DETAILS_EMPTY);
		}
		if (token.isEmpty()) {
			throw new BPException(BPConstant.TOKEN_EMPTY);
		}
		final String url = env.getProperty("xbmc.send.for.review");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr, headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("type", BPConstant.BP_TYPE);

		ResponseEntity<String> remoteServiceRes = null;
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.PUT, entity,
				String.class);
		String responseBody = remoteServiceRes.getBody();
		Response<XbmcValidateAccessResponse> response = null;
		ObjectMapper objectmapper = new ObjectMapper();
		XbmcValidateAccessResponse xbmcObj = null;
		try {
			if (ResourceUtil.isError(remoteServiceRes.getStatusCode())) {
				xbmcObj = objectmapper.readValue(responseBody, XbmcValidateAccessResponse.class);
				throw new BPException(xbmcObj.getMessage());
			} else {
				xbmcObj = objectmapper.readValue(responseBody, XbmcValidateAccessResponse.class);
				response = new Response<XbmcValidateAccessResponse>(Status.SUCCESS.getStatusCode(), Status.SUCCESS,
						null, xbmcObj);
			}
			compositeKey.setDepartment(reviewjson.getAssetComments().getDepartment());
			compositeKey.setModule(reviewjson.getAssetComments().getModule());
			compositeKey.setRelease(reviewjson.getAssetComments().getRelease());
			compositeKey.setAssetType(reviewjson.getAssetComments().getAssetType());
			compositeKey.setAssetName(reviewjson.getAssetComments().getAssetName());
			compositeKey.setArtifactId(reviewjson.getAssetComments().getArtifactId());
			reviewjson.getAssetComments().setCompositeKey(compositeKey);
			bpDesignerRepository.updateSendForReviewStatus(reviewjson.getAssetComments());
		} catch (IOException e) {
			LOGGER.error("XBMC Response parsing error" + e.getMessage());
			throw new BPException("XBMC Response parsing error");
		}
		return response;
	}

	/**
	 * This method will be used to send review status to xbmc.The review status
	 * will include whether BP flow accepted or not and comments
	 */
	public Response<?> sendXbmcReviewStatus(final String token, final String artifactid, AssetComments assetComments) {
		ReviewStatus status = new ReviewStatus();
		if (StringUtils.equalsIgnoreCase(assetComments.getStatus(), BPConstant.APPROVED)) {
			for (UserComments approverComments : assetComments.getApproverComments()) {
				setReviewDetails(approverComments, BPConstant.APPROVED, status);
			}
		} else {
			for (UserComments reviewerComments : assetComments.getReviewerComments()) {
				setReviewDetails(reviewerComments, BPConstant.REJECTED, status);
			}
		}
		String jsonStr = BPUtil.convertJaveToJson(status);
		if (token.isEmpty()) {
			throw new BPException(BPConstant.TOKEN_EMPTY);
		}
		String url = env.getProperty("xbmc.review.update.status");
		url = url.concat("/" + artifactid);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr, headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("type", BPConstant.BP_TYPE);
		ResponseEntity<String> remoteServiceRes = null;
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.PUT, entity,
				String.class);
		String responseBody = remoteServiceRes.getBody();
		Response<XbmcValidateAccessResponse> response = null;
		ObjectMapper objectmapper = new ObjectMapper();
		XbmcValidateAccessResponse xbmcObj = null;
		try {
			if (ResourceUtil.isError(remoteServiceRes.getStatusCode())) {
				xbmcObj = objectmapper.readValue(responseBody, XbmcValidateAccessResponse.class);
				throw new BPException(xbmcObj.getMessage());
			} else {
				xbmcObj = objectmapper.readValue(responseBody, XbmcValidateAccessResponse.class);
				response = new Response<XbmcValidateAccessResponse>(Status.SUCCESS.getStatusCode(), Status.SUCCESS,
						null, xbmcObj);
			}
			bpDesignerRepository.updateReviewerComments(assetComments);
		} catch (IOException e) {
			LOGGER.error("XBMC Response parsing error" + e.getMessage());
			throw new BPException("XBMC Response parsing error");
		}
		return response;
	}

	/**
	 * This method will set request details for update review status.
	 */
	private void setReviewDetails(UserComments userComments, String reviewStatus, ReviewStatus status) {
		for (Comment comment : userComments.getComments()) {
			if (StringUtils.equalsIgnoreCase(reviewStatus, BPConstant.REJECTED)) {
				if (StringUtils.equalsIgnoreCase(comment.getOperator(), BPConstant.FLOW)) {
					status.setComment(comment.getComment());
					status.setStatus(BPConstant.FAIL);
				}
			} else {
				status.setComment(comment.getComment());
				status.setStatus(BPConstant.PASS);
			}
		}
	}

	@Override
	public Response<?> getAndPutBPFlow(String department, String module, String release,
			String assetType, String assetName) {
		Response<Asset> response;
		Asset data = bpDesignerRepository.getBPDataFlowFromDBCollection(department, module, release,
				assetType, assetName);
		if (data == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			bpDesignerRepository.putBPDataFlowToDB(data);
			response = new Response<Asset>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null, data);
		}
		LOGGER.info("Response from API :: " + data);
		return response;
	}

	/**
	 * saveBPFlow method - Required to save the flowfile(string type) and its
	 * details to Database
	 * 
	 * @param bpFlow
	 * @return response as success status if passed else will return fail
	 */
	public Response<String> saveBPAsset(Asset bpAsset) {
		Response<String> response;

		if (bpAsset == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			CompositeKey compositeKey = new CompositeKey();
			compositeKey.setDepartment(bpAsset.getDepartment());
			compositeKey.setModule(bpAsset.getModule());
			compositeKey.setRelease(bpAsset.getRelease());
			compositeKey.setAssetType(bpAsset.getAssetType());
			compositeKey.setAssetName(bpAsset.getAssetName());
			compositeKey.setArtifactId(bpAsset.getArtifact_id());
			bpAsset.setCompositeKey(compositeKey);
			bpDesignerRepository.saveBPAssetData(bpAsset);
			response = new Response<String>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
					BPConstant.ASSET_SAVED);
		}
		LOGGER.info("Response from API :: " + response);
		return response;
	}

	/**
	 * getBPFlow - Required to get the flowfile and its details from Database
	 * 
	 * @param department,module,release,pms,assetType,assetName
	 * @return response as data if passed else will return error status response
	 *         return response as data if passed else will return error status
	 *         response
	 */

	public Response<Asset> getBPAsset(String department, String module, String release, int artifact_id,
			String assetType, String assetName, Object version) {
		Response<Asset> response;
		String token = getToken();
		
		BaseVersionRequest baseVerReq = new BaseVersionRequest();
		baseVerReq.setArtifactId(artifact_id);
		baseVerReq.setType(assetType);
		baseVerReq.setAssetName(assetName);
		baseVerReq.setDepartment(department);
		baseVerReq.setModule(module);
		baseVerReq.setRelease(release);
		
		// get base version
		BaseVersionResponse baseVerRes = getAssetBaseVersion(baseVerReq, token);
		
		if (baseVerRes != null && baseVerRes.getData() != null && baseVerRes.getData().getContext() != null
				&& baseVerRes.getData().getAssetDetails() != null) {
			
			Asset data = null;
			try {
				if (version == null || version.toString().isEmpty()) {
					version = bpDesignerRepository.getAssetLatestVersion(department, module, release, artifact_id, assetType,
							assetName);
				}
				data = bpDesignerRepository.getBPAsset(department, module, release, artifact_id, assetType, assetName,
						Integer.parseInt(version.toString()));
			} catch (Exception e) {
				LOGGER.info("Asset data is empty! Probably '" + assetName + "' is a newly profiled/extended asset.");
			}
			
			// check if extended/profiled asset already in db 
			if (data == null) {
				// set department, module, release artifact_id, assetType, assetName
				department = baseVerRes.getData().getContext().getDepartment();
				module = baseVerRes.getData().getContext().getModule();
				release = baseVerRes.getData().getContext().getRelease();
				artifact_id = baseVerRes.getData().getAssetDetails().getArtifactId();
				assetType = baseVerRes.getData().getAssetDetails().getType();
				assetName = baseVerRes.getData().getAssetDetails().getAssetName();
			} else {
				response = new Response<Asset>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null, data);
				LOGGER.info("Response from API :: " + data);
				return response;
			}
		}

		if (version == null || version.toString().isEmpty()) {
			version = bpDesignerRepository.getAssetLatestVersion(department, module, release, artifact_id, assetType,
					assetName);
		}
		
		Asset data = bpDesignerRepository.getBPAsset(department, module, release, artifact_id, assetType, assetName,
				Integer.parseInt(version.toString()));
		if (data == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			response = new Response<Asset>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null, data);
		}
		LOGGER.info("Response from API :: " + data);
		return response;
	}

	/**
	 * This method will give the designer comments
	 */
	public Response<?> fetchDesignerComments(String department, String module, String release, int artifact_id,
			String assetType, String assetName, int version) {
		ArrayList<UserComments> designerComments = bpDesignerRepository.fetchDesignerComments(department, module,
				release, artifact_id, assetType, assetName, version);
		if (designerComments == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			return new Response<ArrayList<UserComments>>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
					designerComments);
		}
	}

	/**
	 * This method will give the reviewer comments
	 */
	public Response<?> fetchReviewerComments(String department, String module, String release, int artifact_id,
			String assetType, String assetName, int version) {
		ArrayList<UserComments> reviewerComments = bpDesignerRepository.fetchReviewerComments(department, module,
				release, artifact_id, assetType, assetName, version);
		if (reviewerComments == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			return new Response<ArrayList<UserComments>>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
					reviewerComments);
		}
	}

	/**
	 * getReviewReport - Required to get the list of details which has been
	 * modified in BPflow
	 * 
	 * @param department,module,release,pms,assetType,assetName
	 * @return response as data if passed else will return error status response
	 */
	public Response<?> reviewReportService(String department, String module, String release, int artifact_id,
			String assetType, String assetName) {
		try {
			Object version = bpDesignerRepository.getAssetLatestVersion(department, module, release, artifact_id,
					assetType, assetName);
			Asset targetData = bpDesignerRepository.getBPAsset(department, module, release, artifact_id, assetType,
					assetName, version);
			Asset sourceData = bpDesignerRepository.getBPAsset(department, module, release, artifact_id, assetType,
					assetName, Integer.parseInt(version.toString()) - 1);
			BPFlowUI targetJson = gson.fromJson(targetData.getAssetDetail(), BPFlowUI.class);
			BPFlowUI sourceJson = gson.fromJson(sourceData.getAssetDetail(), BPFlowUI.class);
			List<ReviewReportResponse> reviewReportResponse = ConfigBPReport.configureBusinessProcessComparision(
					sourceJson.getConfigureBusinessProcess(), targetJson.getConfigureBusinessProcess());
			reviewReportResponse.addAll(compareOperators(sourceJson.getOperators(), targetJson.getOperators()));
			reviewReportResponse.addAll(SmartConnectorReport.smartConnectorComparision(sourceJson.getConnections(),
					targetJson.getConnections()));
			return new Response<List<ReviewReportResponse>>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null,
					reviewReportResponse);
		} catch (Exception exception) {
			LOGGER.error("Review report generation error");
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * compareOperators - Required to compare the operators and get the
	 * difference
	 * 
	 * @param operators
	 * @return response
	 */
	public List<ReviewReportResponse> compareOperators(List<Operators> sourceOperators,
			List<Operators> targetOperators) {
		try {
			List<ReviewReportResponse> operatorList = new ArrayList<>();
			ArrayList<Operators> deletedoperatorList = new ArrayList<>();
			boolean isOperatorDeleted;
			boolean isOperatorAdded;
			for (Operators sourceOperator : sourceOperators) {
				isOperatorDeleted = true;
				for (Operators targetOperator : targetOperators) {
					if (StringUtils.equalsIgnoreCase(sourceOperator.getKey(), targetOperator.getKey())) {
						isOperatorDeleted = false;
						break;
					}
				}
				if (isOperatorDeleted) {
					deletedoperatorList.add(sourceOperator);
				}
			}
			if (!deletedoperatorList.isEmpty())
				for (Operators sourceOperator : deletedoperatorList) {
					ReviewReportResponse operators = checkByOperatorType(sourceOperator, new Operators(), false, true);
					if (!operators.getFields().isEmpty()) {
						operators.setKey(sourceOperator.getKey());
						operators.setName(sourceOperator.getName());
						operators.setType(sourceOperator.getType());
						operators.setStatus(BPConstant.DELETED);
						operatorList.add(operators);
					}
				}
			for (Operators targetOperator : targetOperators) {
				isOperatorAdded = false;
				for (Operators sourceOperator : sourceOperators) {
					if (StringUtils.equalsIgnoreCase(targetOperator.getKey(), sourceOperator.getKey())) {
						isOperatorAdded = true;
						ReviewReportResponse operators = checkByOperatorType(sourceOperator, targetOperator, false,
								false);
						if (operators.getFields().size() > 0) {
							operators.setKey(sourceOperator.getKey());
							operators.setName(sourceOperator.getName());
							operators.setType(targetOperator.getType());
							operators.setStatus(BPConstant.MODIFIED);
							operatorList.add(operators);
						}
					}
				}
				if (!isOperatorAdded) {
					ReviewReportResponse operators = checkByOperatorType(new Operators(), targetOperator, true, false);
					if (!operators.getFields().isEmpty()) {
						operators.setKey(targetOperator.getKey());
						operators.setName(targetOperator.getName());
						operators.setType(targetOperator.getType());
						operators.setStatus(BPConstant.ADDED);
						operatorList.add(operators);
					}
				}
			}
			return operatorList;
		} catch (Exception e) {
			LOGGER.error("Error occurred while comparing operators"+e);
			throw new BPException(e.getMessage());
		}
	}

	/**
	 * This method will check for Operator type.
	 */
	private ReviewReportResponse checkByOperatorType(Operators sourceOperator, Operators targetOperator,
			boolean isAdded, boolean isDeleted) {
		ReviewReportResponse operators = new ReviewReportResponse();
		try {
			String operatorType = null;
			if (isDeleted) {
				operatorType = sourceOperator.getType();
			} else {
				operatorType = targetOperator.getType();
			}
			switch (operatorType.toUpperCase()) {
			case BPConstant.START:
				operators.setFields(StartOperatorReport.startOperatorComparision(sourceOperator, targetOperator,
						isAdded, isDeleted));
				break;
			case BPConstant.INVOKE_BS:
				operators.setFields(InvokeBSInternalReport.invokeBSOperatorComparision(sourceOperator, targetOperator,
						isAdded, isDeleted));
				break;
			case BPConstant.INVOKE_BS_EXTERNAL:
				operators.setFields(InvokeBSExternalReport.invokeBSExternalOperatorComparision(sourceOperator,
						targetOperator, isAdded, isDeleted));
				break;
			case BPConstant.END:
				operators.setFields(
						EndOperatorReport.endOperatorComparision(sourceOperator, targetOperator, isAdded, isDeleted));
				break;
			case BPConstant.DECISION_MATRIX_EXCLUSIVE:
				operators.setFields(DecisionMatrixReport.decisionMatrixOperatorComparision(sourceOperator,
						targetOperator, isAdded, isDeleted));
				break;
			case BPConstant.DECISION_MATRIX_INCLUSIVE:
				operators.setFields(DecisionMatrixReport.decisionMatrixOperatorComparision(sourceOperator,
						targetOperator, isAdded, isDeleted));
				break;
			case BPConstant.JOIN:
				operators.setFields(
						JoinOperatorReport.joinOperatorComparision(sourceOperator, targetOperator, isAdded, isDeleted));
				break;
			case BPConstant.MERGE:
				operators.setFields(
						MergeOperatorReport.compareMergeOperator(sourceOperator, targetOperator, isAdded, isDeleted));
				break;
			case BPConstant.INPUT_CHANNEL_INTEGRATION:
				operators.setFields(FileChannelOperatorReport.fileInputOperatorComparision(sourceOperator,
						targetOperator, isAdded, isDeleted));
				break;
			case BPConstant.OUTPUT_CHANNEL_INTEGRATION:
				operators.setFields(FileChannelOperatorReport.fileOutputOperatorComparision(sourceOperator,
						targetOperator, isAdded, isDeleted));
				break;
			case BPConstant.REST_INPUT_CHANNEL_INTEGRATION:
				operators.setFields(RestChannelOperatorReport.restInputOperatorComparision(sourceOperator,
						targetOperator, isAdded, isDeleted));
				break;
			case BPConstant.REST_OUTPUT_CHANNEL_INTEGRATION:
				operators.setFields(RestChannelOperatorReport.restOutputOperatorComparision(sourceOperator,
						targetOperator, isAdded, isDeleted));
				break;
			default:
				List<Fields> fields = new ArrayList<>();
				operators.setFields(fields);
			}
			return operators;
		} catch (Exception exception) {
			LOGGER.error("Error occurred while checking for operator type");
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will compare the properties for all operators.
	 */
	public static void operatorPropertiesComparision(List<Property> sourceProperties, List<Property> targetProperties,
			List<Fields> fieldsList, boolean isAdded, boolean isDeleted) {
		try {
			if (isDeleted) {
				for (int sourceProperty = 0; sourceProperty < sourceProperties.size(); sourceProperty++) {
					fieldsList.add(buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.PROPERTIES,
							sourceProperties.get(sourceProperty).getName(),
							sourceProperties.get(sourceProperty).getValue(), BPConstant.EMPTY_STRING));
				}
			} else if (isAdded) {
				for (int targetProperty = 0; targetProperty < targetProperties.size(); targetProperty++) {
					fieldsList.add(buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.PROPERTIES,
							targetProperties.get(targetProperty).getName(), BPConstant.EMPTY_STRING,
							targetProperties.get(targetProperty).getValue()));

				}
			} else {
				ArrayList<Property> deletedProperty = new ArrayList<>();
				boolean isPropertyDeleted;
				boolean isPropertyAdded;
				for (Property sourceProperty : sourceProperties) {
					isPropertyDeleted = true;
					for (Property targetProperty : targetProperties) {
						if (StringUtils.equalsIgnoreCase(sourceProperty.getName(), targetProperty.getName())) {
							isPropertyDeleted = false;
							break;
						}
					}
					if (isPropertyDeleted) {
						deletedProperty.add(sourceProperty);
					}
				}
				if (!deletedProperty.isEmpty())
					for (Property property : deletedProperty) {
						if (!StringUtils.isEmpty(property.getValue())) {
							fieldsList.add(buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.PROPERTIES,
									property.getName(), property.getValue(), BPConstant.EMPTY_STRING));
						}
					}
				for (Property targetProperty : targetProperties) {
					isPropertyAdded = false;
					for (Property sourceProperty : sourceProperties) {
						if (StringUtils.equalsIgnoreCase(targetProperty.getName(), sourceProperty.getName())) {
							isPropertyAdded = true;
							if (!(sourceProperty.getValue().equals(targetProperty.getValue()))) {
								fieldsList.add(buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.PROPERTIES,
										targetProperty.getName(), sourceProperty.getValue(),
										targetProperty.getValue()));
							}

						}
					}
					if (!isPropertyAdded) {
						if (!StringUtils.isEmpty(targetProperty.getValue())) {
							fieldsList.add(buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.PROPERTIES,
									targetProperty.getName(), BPConstant.EMPTY_STRING, targetProperty.getValue()));
						}
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Error occurred in properties check for all operator");
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will compare the comments.
	 */
	public static void operatorCommentsCheck(Comments sourceComment, Comments targetComment, List<Fields> fieldsList,
			boolean isAdded, boolean isDeleted) {
		try {
			if (isAdded) {
				if (!StringUtils.isEmpty(targetComment.getComments()))
					fieldsList.add(buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.COMMENTS, BPConstant.COMMENTS,
							BPConstant.EMPTY_STRING, targetComment.getComments()));
			} else if (isDeleted) {
				if (!StringUtils.isEmpty(sourceComment.getComments()))
					fieldsList.add(buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.COMMENTS, BPConstant.COMMENTS,
							sourceComment.getComments(), BPConstant.EMPTY_STRING));
			} else {
				if (!StringUtils.equalsIgnoreCase(sourceComment.getComments(), targetComment.getComments()))
					fieldsList.add(buildResponseJson(BPConstant.EMPTY_STRING, BPConstant.COMMENTS, BPConstant.COMMENTS,
							sourceComment.getComments(), targetComment.getComments()));
			}
		} catch (Exception exception) {
			LOGGER.error("Error occurred in comments check for all operator");
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will build the response json.
	 */
	public static Fields buildResponseJson(String key, String tabName, String fieldName, Object oldValue,
			Object newValue) {
		try {
			Fields fields = new Fields();
			fields.setKey(key);
			fields.setTabName(tabName);
			fields.setFieldName(fieldName);
			fields.setOldValue(oldValue);
			fields.setNewValue(newValue);
			return fields;
		} catch (Exception exception) {
			LOGGER.error("Exception occurred while building response json"+exception);
			throw new BPException(exception.getMessage());
		}
	}

	/**
	 * This method will be used to update the asset build job status
	 * 
	 * @param buildStatus
	 * @return success/failure status message
	 */
	@Override
	public Response<?> updateBuildJobStatus(@Valid BuildStatus buildStatus) {
		List<AssetDetail> assetDetails = buildStatus.getAssetDetails();
		if ("COMPLETED".equals(buildStatus.getJobStatus().getStatus())) {
			bpDesignerRepository.baselineBPAsset(assetDetails.get(0).getDepartment(), assetDetails.get(0).getModule(),
					assetDetails.get(0).getRelease(), assetDetails.get(0).getAssetType(),
					assetDetails.get(0).getAssetName());
		}
		String token = getToken();
		Response<XbmcBuildJobStatusAPIResponse> response = null;
		if (!StringUtils.isBlank(token)) {
			String url = env.getProperty("xbmc.service.buildjobstatusupdate.url");
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + token);
			headers.set("Content-Type", "application/json");
			String jsonStr = BPUtil.convertJaveToJson(buildStatus);
			HttpEntity<String> entity = new HttpEntity<String>(jsonStr, headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
			ResponseEntity<String> remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(),
					HttpMethod.POST, entity, String.class);
			String responseBody = remoteServiceRes.getBody();
			response = null;
			ObjectMapper objectmapper = new ObjectMapper();
			XbmcBuildJobStatusAPIResponse xbmcObj = null;
			try {
				xbmcObj = objectmapper.readValue(responseBody, XbmcBuildJobStatusAPIResponse.class);
				if (ResourceUtil.isError(remoteServiceRes.getStatusCode())
						|| "failure".equalsIgnoreCase(xbmcObj.getStatus())) {
					throw new BPException(xbmcObj.getMessage());
				} else {
					response = new Response<XbmcBuildJobStatusAPIResponse>(Status.SUCCESS.getStatusCode(),
							Status.SUCCESS, null, xbmcObj);
				}
			} catch (IOException e) {
				LOGGER.error("XBMC Response parsing error" + e.getMessage());
				throw new BPException("XBMC Response parsing error", e);
			}
			return response;
		}
		return response;
	}

	private String getToken() {
		String url = env.getProperty("xbmc.service.auth.url");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/x-www-form-urlencoded");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "password");
		map.add("username", env.getProperty("xbmc.service.auth.username"));
		map.add("password", env.getProperty("xbmc.service.auth.password"));
		map.add("client_id", env.getProperty("xbmc.service.auth.client_id"));
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		ResponseEntity<String> remoteServiceRes = null;
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.POST, entity,
				String.class);
		String responseBody = remoteServiceRes.getBody();
		try {
			JSONObject resPayload = new JSONObject(responseBody);
			if (resPayload.has("access_token")) {
				return resPayload.getString("access_token");
			} else {
				throw new BPException("Failure in getting access token from KeyCloak.");
			}
		} catch (JSONException je) {
			LOGGER.error("Exception in getting token from XBMC KeyCloak service: ", je);
			throw new BPException(je.getMessage(), je);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new BPException(e.getMessage(), e);
		}
	}

	@Override
	public Response<?> baselineBPAsset(String department, String module, String release, int artifact_id,
			String assetType, String assetName) {
		Response<Asset> response;
		Asset data = bpDesignerRepository.baselineBPAsset(department, module, release, artifact_id, assetType,
				assetName);
		if (data == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			response = new Response<Asset>(Status.SUCCESS.getStatusCode(), Status.SUCCESS, null, data);
		}
		LOGGER.info("Response from API :: " + data);
		return response;
	}

	/**
	 * getEffectiveBEType - This method will fetch the Business Entity Types
	 * from External Effective API and returns the response to the BPController.
	 * 
	 * @param department,module,release,bename
	 * 
	 * @return response object containing Business Entity details
	 */

	public List<Map<String, Object>> getEffectiveBEType(final String token, String department, String module,
			String release, String bename, Long pmsId) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		final String url = env.getProperty("xbmc.service.effective.betype.url");
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("department", department)
				.queryParam("module", module).queryParam("release", release).queryParam("bename", bename);
		if (pmsId != null && pmsId > 0) {
			builder.queryParam("pmsId", pmsId);
		}

		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<String> remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(),
				HttpMethod.GET, entity, String.class);
		String responseBody = remoteServiceRes.getBody();

		XbmcResponse<?> xbmcresponse = null;
		XbmcBEResponse xbmcBEResponse = null;
		ObjectMapper objectmapper = new ObjectMapper();
		List<Map<String, Object>> totalResponseList = null;

		try {
			if (ResourceUtil.isError(remoteServiceRes.getStatusCode())) {
				xbmcresponse = objectmapper.readValue(responseBody, XbmcResponse.class);
				throw new BPException(xbmcresponse.getMessage());
			} else {
				xbmcBEResponse = objectmapper.readValue(responseBody, XbmcBEResponse.class);
				if (xbmcBEResponse != null && xbmcBEResponse.getData() != null) {
					List<EffectiveBE> beEffectiveList = xbmcBEResponse.getData();

					if (beEffectiveList != null && beEffectiveList.size() > 0) {
						totalResponseList = new ArrayList<Map<String, Object>>();
						for (EffectiveBE effectiveObj : beEffectiveList) {
							Map<String, Object> temp = getEffectiveBEResponse(effectiveObj, token, department, module,
									release, bename, pmsId);
							if (temp != null && temp.size() > 0) {
								totalResponseList.add(temp);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("XBMC Response parsing error" + e.getMessage());
			throw new BPException("XBMC Response parsing error");
		} catch (Exception e) {
			LOGGER.error("XBMC Exception occured " + e.getMessage());
			throw new BPException("Exception occured while processing EffectiveBE response");
		}

		return totalResponseList;
	}

	private Map<String, Object> getEffectiveBEResponse(EffectiveBE effectiveObj, String token, String department,
			String module, String release, String bename, Long pmsId) {

		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

		if (effectiveObj.getAssetType() != null && effectiveObj.getAssetType().trim().equalsIgnoreCase("BE")) {

			String rootBE = effectiveObj.getBusinessEntityObject().getBeName();
			resultMap = constructCompositeBEDetails(token, department, module, release, rootBE, null, effectiveObj,
					null, null, null, pmsId);
		}

		else if (effectiveObj.getAssetType() != null && effectiveObj.getAssetType().trim().equalsIgnoreCase("CBE")) {

			CBEMapping compositeMapping = effectiveObj.getCbeMappingObject();
			String compositeBEName = effectiveObj.getBusinessEntityObject().getBeName();

			if (compositeMapping != null && compositeMapping.getAnchor() != null) {
				Anchor anchor = compositeMapping.getAnchor();
				String rootBE = anchor.getBeName();
				String aliasName = anchor.getAliasName();
				List<Relations> relations = anchor.getRelations();
				resultMap = fetchCompositeBEJsonResponse(token, department, module, release, rootBE, relations, null,
						compositeBEName, effectiveObj, aliasName, pmsId);
			}
		}
		return resultMap;
	}

	private Map<String, Object> fetchCompositeBEJsonResponse(String token, String department, String module,
			String release, String rootBE, List<Relations> relations, String relationType, String compositeBEName,
			EffectiveBE compositeBEEffectiveObj, String aliasName, Long pmsId) {

		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

		try {
			EffectiveBE lhsBE = getEffectiveBe(token, department, module, release, rootBE, pmsId);

			if (lhsBE != null && lhsBE.getBusinessEntityObject() != null
					&& lhsBE.getBusinessEntityObject().getBeName() != null
					&& lhsBE.getBusinessEntityObject().getBeName().equalsIgnoreCase(rootBE)) {

				if (relationType == null) {
					resultMap = constructCompositeBEDetails(token, department, module, release, rootBE, relations,
							lhsBE, compositeBEName, compositeBEEffectiveObj, aliasName, pmsId);
				}

				else if (relationType != null && relationType.trim().length() > 0) {

					String[] array = relationType.split(":");
					String rhsRelationType = actualRelation(array[1]);

					if (rhsRelationType != null && rhsRelationType.trim().equalsIgnoreCase("one")) {

						resultMap = constructCompositeBEDetails(token, department, module, release, rootBE, relations,
								lhsBE, null, null, aliasName, pmsId);

					} else if (rhsRelationType != null && rhsRelationType.trim().equalsIgnoreCase("many")) {

						resultMap.put("type", "array");
						Map<String, Object> temp = constructCompositeBEDetails(token, department, module, release,
								rootBE, relations, lhsBE, null, null, aliasName, pmsId);
						resultMap.put("items", temp);
					}
				}
			}
		} catch (Exception e) {
			throw new BPException(e.getMessage());
		}

		return resultMap;
	}

	private Map<String, Object> constructCompositeBEDetails(String token, String department, String module,
			String release, String rootBE, List<Relations> relations, EffectiveBE lhsBE, String compositeBEName,
			EffectiveBE compositeBEEffectiveObj, String aliasName, Long pmsId) {

		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

		resultMap.put("type", "object");

		if (compositeBEName != null) {
			resultMap.put("name", compositeBEName);
			resultMap.put("aliasName", aliasName);
			Map<String, Object> context = getContext(compositeBEEffectiveObj);
			resultMap.put("context", context);
		}

		else {
			resultMap.put("name", rootBE);
			resultMap.put("aliasName", aliasName);
			Map<String, Object> context = getContext(lhsBE);
			resultMap.put("context", context);
		}

		List<Integer> bukList = lhsBE.getBusinessEntityObject().getBusinessUniqueKeys();
		List<BusinessEntityAttributes> attributesList = lhsBE.getBusinessEntityAttributes();
		Map<String, Object> properties = prepareAttributes(attributesList, bukList);

		if (relations != null && relations.size() > 0) {
			properties = constructInnerBEDetails(properties, relations, token, department, module, release, rootBE, pmsId);
		}

		resultMap.put("properties", properties);

		return resultMap;
	}

	private Map<String, Object> constructInnerBEDetails(Map<String, Object> properties, List<Relations> relationList,
			String token, String department, String module, String release, String rootBE, Long pmsId) {

		for (Relations relationObj : relationList) {
			String innerBeName = null;
			String innerAliasName = null;

			String relationType = null;
			List<Relations> innerRelations = null;
			if (relationObj != null && relationObj.getRelationName() != null
					&& relationObj.getRelationName().trim().length() > 0) {
				Target target = relationObj.getTarget();
				relationType = target.getRelationType();
				innerRelations = target.getRelations();
				innerBeName = target.getBeName();
				innerAliasName = target.getAliasName();
			}

			Map<String, Object> temp = fetchCompositeBEJsonResponse(token, department, module, release, innerBeName,
					innerRelations, relationType, null, null, innerAliasName, pmsId);

			properties.put(innerAliasName, temp);
		}
		return properties;
	}

	private Map<String, Object> getContext(EffectiveBE effectiveBE) {
		Map<String, Object> context = new LinkedHashMap<String, Object>();

		if (effectiveBE != null && effectiveBE.getBusinessEntityObject() != null) {
			int artifactId = effectiveBE.getBusinessEntityObject().getArtifactId();
			String department = effectiveBE.getBusinessEntityObject().getDepartment();
			String ownerDepartment = effectiveBE.getBusinessEntityObject().getOwnerDepartment();
			String module = effectiveBE.getBusinessEntityObject().getModule();
			String release = effectiveBE.getBusinessEntityObject().getRelease();

			context.put("artifactId", artifactId);
			context.put("department", department);
			context.put("ownerDepartment", ownerDepartment);
			context.put("module", module);
			context.put("release", release);
		}

		return context;

	}

	private Map<String, Object> prepareAttributes(List<BusinessEntityAttributes> attributesList, List<Integer> buk) {
		Map<String, Object> attributeNameValue = new LinkedHashMap<String, Object>();
		if (attributesList != null && attributesList.size() > 0) {
			for (BusinessEntityAttributes beAttr : attributesList) {
				BusinessEntityAttributeProperty beAttrProp = beAttr.getBusinessEntityAttributeProperty();
				if (beAttrProp != null) {
					Map<String, Object> attributeDetails = new LinkedHashMap<String, Object>();

					String type = null;
					int precision = 0;
					int scale = 0;
					String roundingMode = null;
					String dateFormat = null;
					String attributeName = beAttrProp.getBeAttrName();
					int attributeId = beAttrProp.getBeAttrId();
					boolean required = beAttrProp.getIsMandatory();
					boolean isBuk = false;

					if (buk != null && buk.size() > 0 && buk.contains(attributeId)) {
						isBuk = true;
					}
					DataType dataType = beAttrProp.getDataType();
					if (dataType != null) {
						type = dataType.getType();
						precision = dataType.getPrecision();
						scale = dataType.getScale();
						roundingMode = dataType.getRoundingMode();
						dateFormat = dataType.getDateFormat();
					}

					attributeDetails.put("required", required);
					attributeDetails.put("isBuk", isBuk);
					attributeDetails.put("type", type);
					attributeDetails.put("precision", precision);
					attributeDetails.put("scale", scale);
					attributeDetails.put("roundingMode", roundingMode);
					attributeDetails.put("dateFormat", dateFormat);

					attributeNameValue.put(attributeName, attributeDetails);
				}
			}
		}
		return attributeNameValue;
	}

	private EffectiveBE getEffectiveBe(final String token, String department, String module, String release,
			String beName, Long pmsId) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		final String url = env.getProperty("xbmc.service.effective.betype.url");
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("department", department)
				.queryParam("module", module).queryParam("release", release).queryParam("bename", beName);
				
			if (pmsId != null && pmsId > 0) {
				builder.queryParam("pmsId", pmsId);
			}

		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<String> remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(),
				HttpMethod.GET, entity, String.class);
		String responseBody = remoteServiceRes.getBody();

		ObjectMapper objectmapper = new ObjectMapper();
		XbmcBEResponse xbmcBEResponse = null;
		XbmcResponse<?> xbmcresponse = null;

		try {
			if (ResourceUtil.isError(remoteServiceRes.getStatusCode())) {
				xbmcresponse = objectmapper.readValue(responseBody, XbmcResponse.class);
				throw new BPException(xbmcresponse.getMessage());
			} else {
				xbmcBEResponse = objectmapper.readValue(responseBody, XbmcBEResponse.class);

				if (xbmcBEResponse != null && xbmcBEResponse.getData() != null) {
					List<EffectiveBE> beEffectiveObj = xbmcBEResponse.getData();

					for (EffectiveBE beObj : beEffectiveObj) {
						if (beName.equalsIgnoreCase(beObj.getBusinessEntityObject().getBeName())) {
							return beObj;
						}
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("Got Error Response while fetching the BE details from Effective URL " + e.getMessage());
			throw new BPException(e.getMessage());

		}
		return null;
	}

	private String actualRelation(String type) {
		switch (type) {
		case "1":
			return "one";
		case "M":
			return "many";
		default:
			return null;
		}
	}

	private BaseVersionResponse getAssetBaseVersion(BaseVersionRequest baseVerReq, String token) {
		String jsonStr = BPUtil.convertJaveToJson(baseVerReq);
		if (jsonStr.isEmpty()) {
			throw new BPException(BPConstant.ASSET_DETAILS_EMPTY);
		}
		if (token.isEmpty()) {
			throw new BPException(BPConstant.TOKEN_EMPTY);
		}
		final String url = env.getProperty("xbmc.service.asset.baseversion");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<String>(jsonStr, headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<String> remoteServiceRes = restTemplate.exchange(builder.buildAndExpand().toUri(),
				HttpMethod.POST, entity, String.class);
		String responseBody = remoteServiceRes.getBody();
		ObjectMapper objectmapper = new ObjectMapper();
		BaseVersionResponse xbmcObj = null;
		try {
			if (ResourceUtil.isError(remoteServiceRes.getStatusCode())) {
				xbmcObj = objectmapper.readValue(responseBody, BaseVersionResponse.class);
				LOGGER.error("XBMC error" + xbmcObj.getMessage());
			} else {
				xbmcObj = objectmapper.readValue(responseBody, BaseVersionResponse.class);
			}
		} catch (IOException e) {
			LOGGER.error("XBMC Response parsing error" + e.getMessage());
		}
		return xbmcObj;
	}

}
