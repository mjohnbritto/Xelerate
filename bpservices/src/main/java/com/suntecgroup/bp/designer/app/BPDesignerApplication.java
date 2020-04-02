package com.suntecgroup.bp.designer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import com.suntecgroup.bp.designer.exception.RestTemplateResponseErrorHandler;

/**
 * Spring Boot application
 */
@SpringBootApplication
@ComponentScan("com.suntecgroup.bp.designer.*")
// @EnableCaching
public class BPDesignerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {

		// Enable for development-
		SpringApplication.run(BPDesignerApplication.class, args);

		// Disable in development and enable for deployment
		// ConfigurableApplicationContext applicationContext = new
		// SpringApplicationBuilder(BPDesignerApplication.class)
		// .properties("spring.config.name:application",
		// "spring.config.location:file:/home/hcluser/bpconfig/,file:C:/bpconfig/")
		// .build().run(args);

	}

	/**
	 * configure method - used to produce deployable war file and configures the
	 * application
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BPDesignerApplication.class);
	}

	/**
	 * configure and create a RestTemplate
	 ** 
	 * @param builder
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		//return builder.build();
	  return builder.errorHandler(new RestTemplateResponseErrorHandler()).build();
	}
}
