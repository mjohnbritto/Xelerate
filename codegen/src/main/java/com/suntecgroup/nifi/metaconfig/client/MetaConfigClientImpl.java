package com.suntecgroup.nifi.metaconfig.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.suntecgroup.nifi.constants.CGConstants;
import com.suntecgroup.nifi.exception.CGException;
import com.suntecgroup.nifi.metaconfig.bean.KeyValue;
import com.suntecgroup.nifi.metaconfig.bean.Property;
import com.suntecgroup.nifi.metaconfig.bean.Response;
import com.suntecgroup.nifi.util.CGUtils;

@Component
public class MetaConfigClientImpl implements MetaConfigClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetaConfigClientImpl.class);
	private Map<String, Property> propDistResponse = null;
	private Map<String, Property> defPropResponse = null;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	@Override
	public Map<String, String> getDefaultPropertyValues()
			throws CGException {
		Map<String, String> propMap = new HashMap<String, String>();
		List<String> key = new ArrayList<String>();
		key.add(env.getProperty("metaconfig.def-op-prop-key"));
		Map<String, Property> map = generateMetaConfigMap(key);
		List<Property> properties = map.get(key.get(0)).getSubkeys();
		for (Property p : properties) {
			propMap.put(p.getKey(), p.getValue());
		}
		return propMap;
	}

	@Override
	public Map<String, Float> getMetaConfig(String operator, String processor)
			throws CGException {
		operator = CGConstants.OPERATOR_NAME_PREPEND + operator.toUpperCase();
		Map<String, Float> propMap = new HashMap<String, Float>();
		List<Property> propList = null;
		ArrayList<String> key = new ArrayList<String>(
				Arrays.asList(CGConstants.OPERATORS_IN_METACONFIG));
		Map<String, Property> map = generateMetaConfigMap(key);
		List<Property> processors = map.get(operator).getSubkeys();
		for (Property p : processors) {
			if (processor.equals(p.getKey())) {
				propList = p.getSubkeys();
				break;
			}
		}
		List<KeyValue> properties;
		String propName;
		for (Property p : propList) {
			properties = p.getAdditionalInfo();
			propName = p.getKey();
			for (KeyValue k : properties) {
				if (CGConstants.RATIO.equalsIgnoreCase(k.getKey())) {
					propMap.put(propName, Float.valueOf(k.getValue()));
				}
			}
		}
		return propMap;
	}

	private Map<String, Property> generateMetaConfigMap(List<String> key) throws CGException {
		if (propDistResponse == null || defPropResponse == null) {
			List<Property> properties;
			Map<String, Property> response = new HashMap<String, Property>();
			final String uri = env.getProperty("metaconfig.getinputjson");
			final String appname = env
					.getProperty("metaconfig.appname");
			Property p = null;
			try {
				properties = invokeMetaConfigAPI(uri, appname, key).getData().getProperties();
				if (null != properties) {
					for (int i = 0; i < properties.size(); i++) {
						p = properties.get(i);
						response.put(p.getKey(), p);
					}
				}
			} catch (CGException e) {
				LOGGER.error("Unable to fetch meta config for operators.", e);
				throw e;
			}
			if(key.size() == 1) {
				defPropResponse = response;
			} else {
				propDistResponse = response;
			}
		}
		if(key.size() == 1) {
			return defPropResponse;
		} else {
			return propDistResponse;
		}
		
	}

	private Response invokeMetaConfigAPI(String uri, String appName, List<String> key)
			throws CGException {
		ResponseEntity<Response> res = null;
		try {
			String jsonStr = CGUtils.convertJaveToJson(key);
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			HttpEntity<?> entity = new HttpEntity<>(jsonStr, headers);
			res = restTemplate.exchange(uri + "/" + appName, HttpMethod.POST, entity,
					Response.class);
		} catch (Exception e) {
			LOGGER.error("Invoke of Meta Config API failed", e);
			throw new CGException("Invoke of Meta Config API failed", e);
		}
		if (res != null) {
			return res.getBody();
		} else {
			return null;
		}
	}
}
