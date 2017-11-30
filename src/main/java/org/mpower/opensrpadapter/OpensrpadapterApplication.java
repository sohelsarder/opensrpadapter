package org.mpower.opensrpadapter;

import org.mpower.form.SubmissionBuilder;
import org.mpower.properties.AdapterProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class OpensrpadapterApplication {
	
	public static void main(String[] args) {
		
		SpringApplication.run(OpensrpadapterApplication.class, args);
	}
}
