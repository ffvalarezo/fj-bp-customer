package com.pichincha.customerbp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "com.pichincha.customerbp.repository")
public class OptimusApplication {

  public static void main(String[] args) {
    SpringApplication.run(OptimusApplication.class, args);
  }

}