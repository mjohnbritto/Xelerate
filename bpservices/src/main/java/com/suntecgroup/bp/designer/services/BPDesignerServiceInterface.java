package com.suntecgroup.bp.designer.services;

import javax.validation.Valid;

import java.util.List;
import java.util.Map;

import com.suntecgroup.bp.designer.model.Asset;
import com.suntecgroup.bp.designer.model.AssetComments;
import com.suntecgroup.bp.designer.model.Response;
import com.suntecgroup.bp.designer.model.SendForReview;
import com.suntecgroup.bp.designer.model.UserDetails;
import com.suntecgroup.xbmc.service.model.buildstatus.BuildStatus;

/**
 * This class contains interface methods which will be defined in an
 * implementation class.
 *
 */
public interface BPDesignerServiceInterface {

	public Response<?> getBEType(final String token, final String department, final String module, final String release,
			final String bename);
	
	public List<Map<String, Object>> getEffectiveBEType(final String token, final String department, final String module, final String release,
			final String bename, Long pmsId);

	public Response<?> getBSServiceDetails(final String token, final String department, final String module,
			final String release, final String bename, Long pmsId);

	public Response<?> getBSEffectiveDetails(final String token, final String department, final String module,
			final String release, final String bename, Long pmsId);

	public Response<?> deleteBP(final String department, final String module, final String release,
			final int artifact_id, final String assetType, final String assetName);

	public Response<?> validateAccessToken(final String token, final UserDetails user);

	public Response<?> review(final String token, final SendForReview review);

	public Response<?> sendXbmcReviewStatus(final String token, final String artifactid,
			final AssetComments assetComments);

	public Response<?> getAndPutBPFlow(final String department, final String module, final String release,
			final String assetType, final String assetName);

	public Response<?> getBPAsset(final String department, final String module, final String release,
			final int artifact_id, final String assetType, final String assetName, Object version);

	public Response<?> reviewReportService(final String department, final String module, final String release,
			final int artifact_id, String assetName, String assetType);

	public Response<?> saveBPAsset(final Asset bpAsset);

	public Response<?> fetchDesignerComments(final String department, final String module, final String release,
			final int artifact_id, final String assetType, final String assetName, final int version);

	public Response<?> fetchReviewerComments(final String department, final String module, final String release,
			final int artifact_id, String assetType, final String assetName, final int version);

	public Response<?> updateBuildJobStatus(@Valid BuildStatus buildStatus);

	public abstract Response<?> baselineBPAsset(final String department, final String module, final String release,
			final int artifact_id, final String assetType, final String assetName);

}
