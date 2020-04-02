package com.suntecgroup.bp.designer.repository;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.suntecgroup.bp.designer.exception.BPException;
import com.suntecgroup.bp.designer.model.Asset;
import com.suntecgroup.bp.designer.model.AssetComments;
import com.suntecgroup.bp.designer.model.UserComments;
import com.suntecgroup.bp.util.BPConstant;

/**
 * BPDesignerRepositoryImpl is an implementation class to handle the Database
 * Operations.
 *
 */
@Repository
public class BPDesignerRepositoryImpl implements BPDesignerRepositoryInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(BPDesignerRepositoryImpl.class);

	@Autowired
	private Environment env;
	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * This method will save the BPFlow Object data to the database.
	 */
	public void saveUploadedBPFlow(final Asset str) {
		mongoTemplate.insert(str);
	}

	/**
	 * This method will delete the BPFlow Object data by
	 * department,module,release,pms,assetType,assetName from the database.
	 */
	public void deleteBP(String department, String module, String release, int artifact_id, String assetType,
			String assetName) {
		int version;
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(release)
				// .and(BPConstant.ARTIFACT_ID).is(artifact_id)
				.and(BPConstant.ASSET_TYPE).is(assetType).and(BPConstant.ASSET_NAME).is(assetName))
						.with(new Sort(Sort.Direction.DESC, BPConstant.VERSION)).limit(1);

		Asset data = mongoTemplate.findOne(query, Asset.class);
		if (null == data) {
			throw new BPException("Data Not found in DB, Please enter valid BP");
		} else {
			version = mongoTemplate.findOne(query, Asset.class).getVersion();
			LOGGER.info("BP Flow version in DB :: " + version);
			mongoTemplate.findAndRemove(query, Asset.class);
		}
	}

	@Override
	public Asset getBPDataFlowFromDBCollection(String department, String module, String release, String assetType,
			String assetName) {
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(release).and(BPConstant.ASSET_TYPE).is(assetType).and(BPConstant.ASSET_NAME)
				.is(assetName));
		query.with(new Sort(Sort.Direction.DESC, BPConstant.VERSION));
		query.limit(1);
		Asset data = mongoTemplate.findOne(query, Asset.class);
		if (null == data) {
			throw new BPException("No asset found matching the input criteria.");
		} else {
			return data;
		}
	}

	public void putBPDataFlowToDB(Asset data) {
		String collectionName = env.getProperty("assetManager.mongo.bpflow.collectionName");
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(data.getDepartment()).and(BPConstant.MODULE)
				.is(data.getModule()).and(BPConstant.RELEASE).is(data.getRelease())
				// .and(BPConstant.PMS).is(data.getPms())
				// .and(BPConstant.ARTIFACT_ID).is(data.getArtifact_id())
				.and(BPConstant.ASSET_TYPE).is(data.getAssetType()).and(BPConstant.ASSET_NAME).is(data.getAssetName()));
		query.with(new Sort(Sort.Direction.DESC, BPConstant.VERSION));
		query.limit(1);
		Asset bpdata = mongoTemplate.findOne(query, Asset.class, collectionName);
		if (bpdata != null) {
			Update update = new Update();
			update.set(BPConstant.DEPARTMENT, data.getDepartment());
			update.set(BPConstant.MODULE, data.getModule());
			update.set(BPConstant.RELEASE, data.getRelease());
			update.set(BPConstant.PMS, data.getPms());
			// update.set(BPConstant.ARTIFACT_ID, data.getArtifact_id());
			update.set(BPConstant.VERSION, data.getVersion());
			update.set(BPConstant.STATUS, data.getStatus());
			update.set(BPConstant.ASSET_TYPE, data.getAssetType());
			update.set(BPConstant.ASSET_NAME, data.getAssetName());
			update.set(BPConstant.ASSET_DETAIL, data.getAssetDetail());
			update.set(BPConstant.CHECK_OUT_USER, data.getCheckOutUser());
			mongoTemplate.upsert(query, update, collectionName);
		} else {
			mongoTemplate.insert(data, collectionName);
		}
	}

	/**
	 * This method will save the BPAsset Object data to the database.
	 */
	public void saveBPAssetData(Asset bpAsset) {
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(bpAsset.getDepartment()).and(BPConstant.MODULE)
				.is(bpAsset.getModule()).and(BPConstant.RELEASE).is(bpAsset.getRelease())
				// .and(BPConstant.PMS).is(bpAsset.getPms())
				// .and(BPConstant.ARTIFACT_ID).is(data.getArtifact_id())
				.and(BPConstant.ASSET_TYPE).is(bpAsset.getAssetType()).and(BPConstant.ASSET_NAME)
				.is(bpAsset.getAssetName())).with(new Sort(Sort.Direction.DESC, BPConstant.VERSION)).limit(1);
		Asset data = mongoTemplate.findOne(query, Asset.class);
		if (data == null) {
			LOGGER.info("Data is null and inserting new asset with BPName :: " + bpAsset.getAssetName());
			bpAsset.getCompositeKey().setVersion(1);
			bpAsset.setVersion(1);
			bpAsset.setStatus(BPConstant.IN_PROGRESS);
			mongoTemplate.insert(bpAsset);
		} else {
			// TODO change the approved to checked_in
			if (StringUtils.equalsIgnoreCase(data.getStatus(), BPConstant.APPROVED) || StringUtils.equalsIgnoreCase(data.getStatus(), BPConstant.REJECTED)) {
				int version = mongoTemplate.findOne(query, Asset.class).getVersion();
				int newVersion = ++version;
				bpAsset.getCompositeKey().setVersion(newVersion);
				bpAsset.setStatus(BPConstant.IN_PROGRESS);
				bpAsset.setVersion(newVersion);
				mongoTemplate.insert(bpAsset);
			} else {
				Query toUpdateQuery = new Query(Criteria.where(BPConstant.DEPARTMENT).is(bpAsset.getDepartment())
						.and(BPConstant.MODULE).is(bpAsset.getModule()).and(BPConstant.RELEASE).is(bpAsset.getRelease())
						// .and(BPConstant.PMS).is(bpAsset.getPms())
						// .and(BPConstant.ARTIFACT_ID).is(bpAsset.getArtifact_id())
						.and(BPConstant.ASSET_TYPE).is(bpAsset.getAssetType()).and(BPConstant.ASSET_NAME)
						.is(bpAsset.getAssetName()).and(BPConstant.VERSION).is(data.getVersion())).limit(1);
				Update update = new Update();
				update.set(BPConstant.ASSET_DETAIL, bpAsset.getAssetDetail());
				update.set(BPConstant.ARTIFACT_ID, bpAsset.getArtifact_id());
				update.set("actionType", bpAsset.getActionType());
				update.set(BPConstant.PMS, bpAsset.getPms());
				update.set(BPConstant.STATUS, bpAsset.getStatus());
				mongoTemplate.upsert(toUpdateQuery, update, Asset.class);
			}
		}
	}

	/**
	 * This method will fetch latest version of BPFlowData Object data from the
	 * database.
	 */
	public Object getAssetLatestVersion(final String department, final String module, final String release,
			final int artifact_id, final String assetType, final String assetName) {
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(release).and(BPConstant.ASSET_TYPE).is(assetType)
				// .and(BPConstant.PMS).is(pms)
				// .and(BPConstant.ARTIFACT_ID).is(artifact_id)
				.and(BPConstant.ASSET_NAME).is(assetName)).with(new Sort(Sort.Direction.DESC, BPConstant.VERSION))
						.limit(1);
		Asset data = mongoTemplate.findOne(query, Asset.class);
		if (data == null) {
			throw new BPException("Invalid BPName... Version not found in database");
		}
		return data.getVersion();
	}

	/**
	 * This method will fetch BPFlowData Object data from the database based on
	 * version.
	 */

	public Asset getBPAsset(final String department, final String module, final String release, final int artifact_id,
			final String assetType, final String assetName, Object version) {
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(release)
				// .and(BPConstant.PMS).is(pms)
				// .and(BPConstant.ARTIFACT_ID).is(artifact_id)
				.and(BPConstant.ASSET_TYPE).is(assetType).and(BPConstant.ASSET_NAME).is(assetName)
				.and(BPConstant.VERSION).is(version));
		Asset data = mongoTemplate.findOne(query, Asset.class);
		if (data == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		}
		return data;
	}

	/**
	 * This method will updating the designer comments in assetcomments.
	 */
	public void updateSendForReviewStatus(AssetComments assetComments) {
		Query query = new Query(
				Criteria.where(BPConstant.DEPARTMENT).is(assetComments.getDepartment()).and(BPConstant.MODULE)
						.is(assetComments.getModule()).and(BPConstant.RELEASE).is(assetComments.getRelease())
						// .and(BPConstant.PMS).is(assetComments.getPms())
						// .and(BPConstant.ARTIFACT_ID).is(assetComments.getArtifact_id())
						.and(BPConstant.ASSET_TYPE).is(assetComments.getAssetType()).and(BPConstant.ASSET_NAME)
						.is(assetComments.getAssetName())).with(new Sort(Sort.Direction.DESC, BPConstant.VERSION));
		AssetComments data = mongoTemplate.findOne(query, AssetComments.class);
		int version = updateAssetstatusAndGetVersion(assetComments.getDepartment(), assetComments.getModule(),
				assetComments.getRelease(),
				// assetComments.getPms(),
				// assetComments.getArtifact_id()
				assetComments.getAssetType(), assetComments.getAssetName());
		if (data == null) {
			LOGGER.info("Data is null and inserting new comments");
			assetComments.getCompositeKey().setVersion(1);
			assetComments.setVersion(1);
			assetComments.setStatus(BPConstant.IN_REVIEW);
			mongoTemplate.insert(assetComments);
		} else {
			if (data.getVersion() == version) {
				Query toUpdateQuery = new Query(Criteria.where(BPConstant.DEPARTMENT).is(assetComments.getDepartment())
						.and(BPConstant.MODULE).is(assetComments.getModule()).and(BPConstant.RELEASE)
						.is(assetComments.getRelease())
						.and(BPConstant.ASSET_TYPE).is(assetComments.getAssetType()).and(BPConstant.ASSET_NAME)
						.is(assetComments.getAssetName()).and(BPConstant.VERSION).is(data.getVersion()));
				data.getDesignerComments().addAll(assetComments.getDesignerComments());
				Update update = new Update();
				update.set(BPConstant.STATUS, BPConstant.IN_REVIEW);
				update.set(BPConstant.DESIGNER_COMMENTS, data.getDesignerComments());
				mongoTemplate.upsert(toUpdateQuery, update, AssetComments.class);
			} else {
				assetComments.getCompositeKey().setVersion(version);
				assetComments.setVersion(version);
				assetComments.setStatus(BPConstant.IN_REVIEW);
				mongoTemplate.insert(assetComments);
			}
		}
	}

	/**
	 * This method will update the status in asset document while sending for
	 * review.
	 * 
	 * @param
	 */
	private int updateAssetstatusAndGetVersion(String department, String module, String releaseNo,
			// int artifact_id,String pms,
			String assetType, String assetName) {
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(releaseNo)
				// .and(BPConstant.PMS).is(pms)
				// .and(BPConstant.ARTIFACT_ID).is(artifact_id)
				.and(BPConstant.ASSET_TYPE).is(assetType).and(BPConstant.ASSET_NAME).is(assetName)
				.and(BPConstant.STATUS).is(BPConstant.IN_PROGRESS)).limit(1);
		Asset data = mongoTemplate.findOne(query, Asset.class);
		if (data == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			Update update = new Update();
			update.set(BPConstant.STATUS, BPConstant.IN_REVIEW);
			mongoTemplate.upsert(query, update, Asset.class);
		}
		return data.getVersion();
	}

	/**
	 * This method will update the reviewer comments in assetcomments.
	 */
	public void updateReviewerComments(AssetComments assetComments) {
		Query query = new Query(
				Criteria.where(BPConstant.DEPARTMENT).is(assetComments.getDepartment()).and(BPConstant.MODULE)
						.is(assetComments.getModule()).and(BPConstant.RELEASE).is(assetComments.getRelease())
						// .and(BPConstant.PMS).is(assetComments.getPms())
						// .and(BPConstant.ARTIFACT_ID).is(assetComments.getArtifact_id())
						.and(BPConstant.ASSET_TYPE).is(assetComments.getAssetType()).and(BPConstant.ASSET_NAME)
						.is(assetComments.getAssetName()).and(BPConstant.STATUS).is(BPConstant.IN_REVIEW));
		AssetComments data = mongoTemplate.findOne(query, AssetComments.class);
		if (StringUtils.equalsIgnoreCase(assetComments.getStatus(), BPConstant.APPROVED)) {
			updateAssetStatusForReviewer(assetComments.getDepartment(), assetComments.getModule(),
					assetComments.getRelease(),
					// assetComments.getPms(),
					// assetComments.getArtifact_id(),
					assetComments.getAssetType(), assetComments.getAssetName(), BPConstant.APPROVED);
			Update update = new Update();
			update.set(BPConstant.STATUS, BPConstant.APPROVED);
			if (data.getApproverComments() != null) {
				data.getApproverComments().addAll(assetComments.getApproverComments());
				update.set(BPConstant.APPROVER_COMMENTS, data.getApproverComments());
			} else {
				update.set(BPConstant.APPROVER_COMMENTS, assetComments.getApproverComments());
			}
			mongoTemplate.upsert(query, update, AssetComments.class);
		} else {
			updateAssetStatusForReviewer(assetComments.getDepartment(), assetComments.getModule(),
					assetComments.getRelease(),
					// assetComments.getPms(),assetComments.getArtifact_id(),
					assetComments.getAssetType(), assetComments.getAssetName(), BPConstant.REJECTED);
			Update update = new Update();
			update.set(BPConstant.STATUS, BPConstant.REJECTED);
			if (data.getReviewerComments() != null) {
				data.getReviewerComments().addAll(assetComments.getReviewerComments());
				update.set(BPConstant.REVIEWER_COMMENTS, data.getReviewerComments());
			} else {
				update.set(BPConstant.REVIEWER_COMMENTS, assetComments.getReviewerComments());
			}
			mongoTemplate.upsert(query, update, AssetComments.class);
		}
	}

	/**
	 * This method will update the asset status for reviewer.
	 */
	private void updateAssetStatusForReviewer(String department, String module, String release,
			// String pms, int artifact_id,
			String assetType, String assetName, String status) {
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(release)
				// .and(BPConstant.PMS).is(pms)
				// .and(BPConstant.ARTIFACT_ID).is(artifact_id)
				.and(BPConstant.ASSET_TYPE).is(assetType).and(BPConstant.ASSET_NAME).is(assetName)
				.and(BPConstant.STATUS).is(BPConstant.IN_REVIEW)).limit(1);
		Asset data = mongoTemplate.findOne(query, Asset.class);
		if (data == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			Update update = new Update();
			update.set(BPConstant.STATUS, status);
			mongoTemplate.upsert(query, update, Asset.class);
		}
	}

	/**
	 * This method will fetch designer comments.
	 */
	public ArrayList<UserComments> fetchDesignerComments(String department, String module, String release,
			int artifact_id, String assetType, String assetName, int version) {
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(release)
//				.and(BPConstant.PMS).is(pms)
				.and(BPConstant.ASSET_TYPE).is(assetType)
				.and(BPConstant.ASSET_NAME).is(assetName)
				.and(BPConstant.VERSION).is(version)).limit(1);
		AssetComments assetComments = mongoTemplate.findOne(query, AssetComments.class);
		if (assetComments == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			return assetComments.getDesignerComments();
		}
	}

	/**
	 * This method will fetch reviewer comments.
	 */
	public ArrayList<UserComments> fetchReviewerComments(String department, String module, String release,
			int artifact_id, String assetType, String assetName, int version) {
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(release)
//				.and(BPConstant.PMS).is(pms)
				.and(BPConstant.ASSET_TYPE).is(assetType)
				.and(BPConstant.ASSET_NAME).is(assetName)
				.and(BPConstant.VERSION).is(version)).limit(1);
		AssetComments assetComments = mongoTemplate.findOne(query, AssetComments.class);
		if (assetComments == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			return assetComments.getReviewerComments();
		}
	}

	/**
	 * This method will fetch and baseline an existing BP Flow. 
	 * @return 
	 */
	@Override
	public Asset baselineBPAsset(final String department, final String module, final String release, final int artifact_id,
			final String assetType, final String assetName) {

		Asset bpAsset = null;
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(release)
				//.and(BPConstant.PMS).is(pms)
				.and(BPConstant.ARTIFACT_ID).is(artifact_id).and(BPConstant.ASSET_TYPE).is(assetType)
				.and(BPConstant.ASSET_NAME).is(assetName)).with(new Sort(Sort.Direction.DESC, BPConstant.VERSION))
						.limit(1);
		bpAsset = mongoTemplate.findOne(query, Asset.class);

		if (bpAsset == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			int version = bpAsset.getVersion();
			bpAsset.getCompositeKey().setVersion(version + 1);
			bpAsset.setStatus(BPConstant.IN_PROGRESS);
			bpAsset.setVersion(version + 1);
			mongoTemplate.insert(bpAsset);
		}
		
		return bpAsset;
	}

	/**
	 * This method will fetch and baseline an existing BP Flow. 
	 * @return 
	 */
	@Override
	public Asset baselineBPAsset(final String department, final String module, final String release,
			final String assetType, final String assetName) {

		Asset bpAsset = null;
		Query query = new Query(Criteria.where(BPConstant.DEPARTMENT).is(department).and(BPConstant.MODULE).is(module)
				.and(BPConstant.RELEASE).is(release)
				.and(BPConstant.ASSET_TYPE).is(assetType).and(BPConstant.ASSET_NAME).is(assetName))
						.with(new Sort(Sort.Direction.DESC, BPConstant.VERSION)).limit(1);
		bpAsset = mongoTemplate.findOne(query, Asset.class);

		if (bpAsset == null) {
			throw new BPException(BPConstant.DATA_NOT_FOUND);
		} else {
			int version = bpAsset.getVersion();
			bpAsset.getCompositeKey().setVersion(version + 1);
			bpAsset.setStatus(BPConstant.IN_PROGRESS);
			bpAsset.setVersion(version + 1);
			mongoTemplate.insert(bpAsset);
		}
		
		return bpAsset;
	}

}
