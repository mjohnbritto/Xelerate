package com.suntecgroup.custom.processor.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.suntecgroup.custom.processor.model.channelintegration.IntrospectResponse;

/**
 * Service class to validate the OAuth token. The validated token will get
 * cached in memory for 10 minutes to avoid repeated calls to the OAuth server.
 * 
 * @author murugeshp
 *
 */
public class OAuthIntrospectionService {

	private final String BEARER_TYPE = "Bearer";
	private final String CLIENT_ID = "client_id";
	private final String CLIENT_SECRET = "client_secret";
	private final String TOKEN_HINT = "token_type_hint";
	private final String TOKEN = "token";

	public static final PropertyDescriptor OAUTH_URL_PROP = new PropertyDescriptor.Builder().name("OAuth Introspect URL")
			.description("OAuth Introspect URL for validating OAuth Token").required(false)
			.expressionLanguageSupported(ExpressionLanguageScope.VARIABLE_REGISTRY)
			.addValidator(StandardValidators.URL_VALIDATOR).build();

	public static final PropertyDescriptor CLIENT_ID_PROP = new PropertyDescriptor.Builder().name("Client Id")
			.description("OAuth registered client Id").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor CLIENT_SECRET_PROP = new PropertyDescriptor.Builder().name("Client Secret")
			.description("OAuth registered client secret").required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	private RestTemplate restTemplate = new RestTemplate();

	// Cache is initialized with max size of 200 entries with a TTL of 15 minutes.
	private Cache<Object, Object> cache = CacheBuilder.newBuilder().maximumSize(200)
			.concurrencyLevel(5).expireAfterWrite(15, TimeUnit.MINUTES).build();

	public List<PropertyDescriptor> getProperties() {
		final List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(OAUTH_URL_PROP);
		properties.add(CLIENT_ID_PROP);
		properties.add(CLIENT_SECRET_PROP);
		return properties;
	}
	
	/**
	 * The token will be checked with the in memory cache, if it's not present
	 * in the cache then the introspection service will be called and the
	 * response will be cached in the memory.
	 * 
	 * @param processContext
	 * @param token
	 * @param logger
	 * @return
	 */
	public boolean isValidToken(final ProcessContext processContext, final String token, final ComponentLog logger) {
		final String oAuthUrl = processContext.getProperty(OAUTH_URL_PROP).evaluateAttributeExpressions().getValue();
		// Returning valid since the security is not configured for the processor.
		if (StringUtils.isEmpty(oAuthUrl)) {
			return true;
		}
		if (StringUtils.isNotBlank(token)) {
			final IntrospectResponse introspectResponse = (IntrospectResponse) cache.getIfPresent(token);
			if (null != introspectResponse) {
				if(introspectResponse.getExpirationDate().after(new Date())) {
					logger.debug("Token is available in the cache so returning - {}", new Object[]{token});
					return true;
				} else {
					logger.debug("Token expired so removing it from the cache - {}", new Object[]{token});
					//Removing the cache entry in case of expired token
					cache.invalidate(token);
					return false;
				}
			} else {
				logger.debug("Invoking the introspection service to validate the token - {}", new Object[]{token});
				final String clientId = processContext.getProperty(CLIENT_ID_PROP).evaluateAttributeExpressions().getValue();
				final IntrospectResponse introspectOAuthToken = introspectOAuthToken(processContext, oAuthUrl, clientId, token, logger);
				if (introspectOAuthToken != null && introspectOAuthToken.isActive() && Iterables.contains(introspectOAuthToken.getAud(), clientId)) {
					cache.put(token, introspectOAuthToken);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * To validate the token by calling the OAuth introspection service.
	 * 
	 * @param processContext
	 * @param oAuthUrl
	 * @param clientId
	 * @param token
	 * @return
	 */
	private IntrospectResponse introspectOAuthToken(final ProcessContext processContext, final String oAuthUrl,
			final String clientId, final String token, final ComponentLog logger) {
		final String clientSecret = processContext.getProperty(CLIENT_SECRET_PROP).evaluateAttributeExpressions().getValue();
		final MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add(CLIENT_ID, clientId);
		//form.add(TOKEN_HINT, "requesting_party_token");
		form.add(CLIENT_SECRET, clientSecret);
		form.add(TOKEN, parseToken(token));
		IntrospectResponse response = null;
		try {
			response = restTemplate.postForObject(oAuthUrl, form, IntrospectResponse.class);
		} catch (Exception e) {
			logger.error("Exception while introspecting the OAuth token", e);
		}
		return response;
	}
	
	/**
	 * Token parser to remove the bearer keyword from the token.
	 * 
	 * @param token
	 * @return
	 */
	private String parseToken(final String token) {
		String authHeaderValue = null;
		if (StringUtils.startsWithIgnoreCase(token, BEARER_TYPE)) {
			authHeaderValue = StringUtils.trim(StringUtils.substring(token, BEARER_TYPE.length()));
		}
		return authHeaderValue;
	}
}
