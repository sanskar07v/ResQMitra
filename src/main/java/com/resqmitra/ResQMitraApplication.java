package com.resqmitra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ResQMitraApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResQMitraApplication.class, args);
	}

}
