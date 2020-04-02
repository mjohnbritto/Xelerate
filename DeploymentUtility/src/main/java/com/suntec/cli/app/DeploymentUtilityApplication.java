package com.suntec.cli.app;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.shell.Shell;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan("com.suntec.cli.*")
public class DeploymentUtilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeploymentUtilityApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public ApplicationRunner shellRunner(Shell shell) {
		return new NonInteractiveShellRunner(shell);
	}
}
