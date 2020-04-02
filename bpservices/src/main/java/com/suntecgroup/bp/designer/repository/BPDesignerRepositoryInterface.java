package com.suntecgroup.bp.designer.repository;

import java.util.ArrayList;

import com.suntecgroup.bp.designer.model.Asset;
import com.suntecgroup.bp.designer.model.AssetComments;
import com.suntecgroup.bp.designer.model.UserComments;

/**
 * This abstract interface is a data layer used for Database Operations.
 *
 */
public interface BPDesignerRepositoryInterface {

	public void saveUploadedBPFlow(final Asset string);

	public void deleteBP(final String department, final String module, final String release, final  int artifact_id,
			final String assetType, final String assetName);

	public Asset getBPDataFlowFromDBCollection(final String department, final String module, final String release,
			final String assetType, final String assetName);

	public void putBPDataFlowToDB(Asset data);

	public Asset getBPAsset(final String department, final String module, final String release, final int artifact_id,
			final String assetType, final String assetName, Object version);

	public Object getAssetLatestVersion(final String department, final String module, final String release,
			final int artifact_id, final String assetType, final String assetName);

	public void saveBPAssetData(final Asset bpAsset);

	public void updateSendForReviewStatus(final AssetComments assetComments);

	public void updateReviewerComments(final AssetComments assetComments);

	public ArrayList<UserComments> fetchDesignerComments(final String department, final String module,
			final String release, final  int artifact_id, final String assetType, final String assetName, final int version);

	public ArrayList<UserComments> fetchReviewerComments(String department, String module, String release,  int artifact_id,
			String assetType, String assetName, int version);

	public abstract Asset baselineBPAsset(String department, String module, String release, int artifact_id, String assetType,
			String assetName);

	public abstract Asset baselineBPAsset(String department, String module, String release, String assetType, String assetName);

}
