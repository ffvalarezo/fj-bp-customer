package com.pichincha.movement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "com.pichincha.movement.repository")
@ComponentScan(basePackages = { "com.pichincha.movement", "com.pichincha.reporting" })
public class OptimusApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptimusApplication.class, args);
	}

}