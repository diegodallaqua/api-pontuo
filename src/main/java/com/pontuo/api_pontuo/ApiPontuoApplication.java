package com.pontuo.api_pontuo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ApiPontuoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiPontuoApplication.class, args);
	}

}
