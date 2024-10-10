package com.nestify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
public class NestifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NestifyApplication.class, args);
	}

}
