/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */

package com.suntecgroup.bpruntime.constant;

/*
 * This class contains the constants that are required in the whole project
 * 
 * @version 1.0 - December 2018
 * @author Thatchanamoorthy
 */

public class Constant {

	// Transaction & event count variable
	public static final String ACTION_ADD = "ADD";
	public static final String ACTION_SUCCESS = "SUCCESS";
	public static final String ACTION_FAILURE = "FAILURE";
	public static final int SESSION_UPDATE_LIMIT = 10;
	public static final String TECHNICAL_ERROR = "TechnicalError";
	public static final String BUSINESS_ERROR = "BusinessError";

	// Session
	public static final String INPROGRESS = "INPROGRESS";
	public static final String SHUTTING_DOWN = "SHUTTING DOWN";
	public static final String STOPPED = "STOPPED";
	public static final String RUNNING = "RUNNING";

	// DAO Constants
	public static final String ASSET_NAME = "assetName";
	public static final String ASSET_TYPE = "assetType";
	public static final String DEPARTMENT = "department";
	public static final String MODULE = "module";
	public static final String RELEASE = "release";
	public static final String PMS = "pms";
	public static final String STATUS = "status";
	public static final String VERSION = "version";
	public static final String ASSET_DETAIL = "assetDetail";
	public static final String CHECK_OUT_USER = "checkOutUser";

	public static final String DATA_NOT_FOUND = "Data Not found in db.../BPName invalid";
	public static final String ADDRESSED = "addressed";
	public static final String SESSION_ID = "sessionId";
	public static final String RUN_NUMBER = "runNumber";
	public static final String ERROR_TYPE = "errorType";
	public static final String FLOWFILEUUID = "FlowFileUUID";
	// Actions for Failed transactions
	public static final String ACTION_REPLAY = "replay";
	public static final String ACTION_PERMANENT_ERROR = "permanentError";
	public static final String ACTION_BACKOUT = "backOut";
	public static final String ACTION_NONE = "";
	public static final String ACTION = "action";
	public static final String TRANSACTION_ID = "transactionId";
}
