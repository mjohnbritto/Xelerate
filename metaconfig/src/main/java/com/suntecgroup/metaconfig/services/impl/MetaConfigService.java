package com.suntecgroup.metaconfig.services.impl;

import com.suntecgroup.metaconfig.model.Data;
import com.suntecgroup.metaconfig.model.KeyRequest;
import com.suntecgroup.metaconfig.model.MetaConfig;
import com.suntecgroup.metaconfig.model.Property;
import com.suntecgroup.metaconfig.model.Response;

public interface MetaConfigService {

	public Response<Data> getPropertyValues(final KeyRequest metafile);
	
	public Response<String> createProperty(final String appname, final MetaConfig metaconfig);
	
	public boolean commentProperty(final String appname, final Property metaconfig);

}
