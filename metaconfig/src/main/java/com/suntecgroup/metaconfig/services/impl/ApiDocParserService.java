package com.suntecgroup.metaconfig.services.impl;

import org.springframework.web.multipart.MultipartFile;

import com.suntecgroup.metaconfig.exception.ApiDocParserException;
import com.suntecgroup.metaconfig.model.ApiDocResponse;

/**
 * Skeleton for different types of API documentation parser implementation.
 * 
 * @author murugeshpd
 *
 */
public interface ApiDocParserService {
	public ApiDocResponse parse(final MultipartFile file, final String operation, final String contentType,
			final String apiDocUrl) throws ApiDocParserException;
}
