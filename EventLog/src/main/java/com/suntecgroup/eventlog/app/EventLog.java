package com.suntecgroup.eventlog.app;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * @author madala.s
 *
 */
@SpringBootApplication
@ComponentScan("com.suntecgroup.eventlog.*")
public class EventLog extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(EventLog.class, args);
	}

	//@Bean
	@Bean(name="nifiBean")
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	/**
	 * configure method - used to produce deployable war file and configures the
	 * application
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(EventLog.class);
	}
}