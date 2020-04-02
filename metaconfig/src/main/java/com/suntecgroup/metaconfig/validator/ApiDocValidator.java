package com.suntecgroup.metaconfig.validator;

import org.springframework.web.multipart.MultipartFile;

import com.suntecgroup.metaconfig.exception.ApiDocParserException;

/**
 * Validator class to validate the API parser request.
 * 
 * @author murugeshpd
 *
 */
public interface ApiDocValidator {
	public void validate(final MultipartFile file, final String operation, final String contentType,
			final String apiDocUrl) throws ApiDocParserException;
}
