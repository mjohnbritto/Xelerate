package com.suntecgroup.bpruntimeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;


@ComponentScan({"com.suntecgroup.bpruntimeapp", "com.suntecgroup.bpruntime", 
	"com.suntecgroup.traceablity","com.suntecgroup.eventlog","com.suntecgroup.metaconfig"})
@SpringBootApplication
@EnableScheduling
public class BpRuntimeAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BpRuntimeAppApplication.class, args);
	}
	
	@Bean(name="nifiBean")
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}	
}
