package com.suntecgroup.bpruntimeapp;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;

import org.apache.http.impl.client.HttpClients;

import org.apache.http.ssl.SSLContextBuilder;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.web.client.RestTemplateBuilder;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.Resource;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.springframework.web.client.RestTemplate;

//@Configuration

public class RestConfiguration {

	@Value("${http.client.ssl.key-store}")

	private Resource keyStore;

	@Value("${http.client.ssl.trust-store}")

	private Resource trustStore;

	@Value("${http.client.ssl.key-store-password}")

	private char[] keyStorePassword;

	@Value("${http.client.ssl.trust-store-password}")

	private char[] trustStorePassword;

//	@Bean(name="nifiBean")
	public RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {

		SSLContext sslContext = SSLContextBuilder.create()

				.loadKeyMaterial(keyStore.getFile(), keyStorePassword, keyStorePassword)

				.loadTrustMaterial(trustStore.getFile(), trustStorePassword).build();

		HttpClient client = HttpClients.custom().setSslcontext(sslContext).build();

		return builder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client)).build();

	}

}
