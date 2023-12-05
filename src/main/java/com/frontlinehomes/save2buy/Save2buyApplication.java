package com.frontlinehomes.save2buy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ConfigurationPropertiesScan("com.frontlinehomes.save2buy.config")
public class Save2buyApplication {
	public static void main(String[] args) {
		SpringApplication.run(Save2buyApplication.class, args);
	}



}
