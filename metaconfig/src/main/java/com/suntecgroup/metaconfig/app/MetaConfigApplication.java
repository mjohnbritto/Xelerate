package com.suntecgroup.metaconfig.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.suntecgroup.metaconfig.*")
public class MetaConfigApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(MetaConfigApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MetaConfigApplication.class);
	}

}
