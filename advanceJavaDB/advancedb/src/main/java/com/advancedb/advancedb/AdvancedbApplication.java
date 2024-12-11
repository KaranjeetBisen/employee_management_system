package com.advancedb.advancedb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AdvancedbApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdvancedbApplication.class, args);
	}

}
