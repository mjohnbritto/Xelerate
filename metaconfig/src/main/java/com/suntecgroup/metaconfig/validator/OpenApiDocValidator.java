package com.suntecgroup.metaconfig.validator;

import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.suntecgroup.metaconfig.constant.MetaConstant;
import com.suntecgroup.metaconfig.exception.ApiDocParserException;
import com.suntecgroup.metaconfig.model.Response;

/**
 * Validator class to validate the open API parser request.
 * 
 * @author murugeshpd
 *
 */
@Component
public class OpenApiDocValidator implements ApiDocValidator {

	public void validate(final MultipartFile file, final String operation, final String contentType,
			final String apiDocUrl) throws ApiDocParserException {
		if ((file == null && StringUtils.isBlank(apiDocUrl)) || (file != null && StringUtils.isNotBlank(apiDocUrl))) {
			final Response<Set<String>> errorResponse = new Response<Set<String>>(MetaConstant.API_PARSER_VALIDATION_FAILURE_CODE, " Either Yaml file or ApiDocUrl should be provided",
					null);
			throw new ApiDocParserException(errorResponse);
		}
		if(file != null) {
			if(!StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()), "yaml")) {
				final Response<Set<String>> errorResponse = new Response<Set<String>>(MetaConstant.API_PARSER_VALIDATION_FAILURE_CODE, " Invalid Yaml file",
						null);
				throw new ApiDocParserException(errorResponse);
			}
		}
	}
}
