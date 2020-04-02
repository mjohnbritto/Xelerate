/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential -2018
 */

package com.suntecgroup.bpconf.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/*
 * This class is a spring boot application initializer
 * 
 * @version 1.0 - September 2018
 * @author John Britto
 */
@ComponentScan("com.suntecgroup.bpconf.*")
@SpringBootApplication
public class BPConfigApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(BPConfigApplication.class, args);
	}

	/**
	 * configure method - used to produce deployable war file and configures the
	 * application
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BPConfigApplication.class);
	}

}
