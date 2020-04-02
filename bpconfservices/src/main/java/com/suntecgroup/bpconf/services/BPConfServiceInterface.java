/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */

package com.suntecgroup.bpconf.services;

import java.util.List;

import com.suntecgroup.bpconf.model.Configuration;
import com.suntecgroup.bpconf.model.Response;
/*
 * This class contains the declaration for the all the rest apis
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
public interface BPConfServiceInterface {

	public Response<?> getConfiguration(final String bpName, final String confType);

	public Response<?> getConfigurationForParticularOperator(final String bpName, final String operatorKey);

	public Response<?> saveConfiguration(final String bpName, List<Configuration> configurationsList);

	public Response<?> isPropertyExisting(final String bpName, String propertyName);

}
