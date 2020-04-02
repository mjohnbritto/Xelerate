package com.suntecgroup.nifi.config;

import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration 
@ComponentScan("com.suntecgroup.nifi.*") 
@EnableWebMvc
public class MessageConverter implements WebMvcConfigurer {

	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		GsonHttpMessageConverter msgConverter = new GsonHttpMessageConverter();
		Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
		msgConverter.setGson(gson);
		converters.add(msgConverter);
	}

}
