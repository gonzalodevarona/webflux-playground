package com.gonzalo.webflux_playground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

//This is used to determine which package will scan for bean creation
@SpringBootApplication(scanBasePackages = "com.gonzalo.webflux-playground.${sec}")
@EnableR2dbcRepositories(basePackages = "com.gonzalo.webflux-playground.${sec}")
public class WebfluxPlaygroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxPlaygroundApplication.class, args);
	}

}
