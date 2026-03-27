package com.myfitnessjourney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MyFitnessJourneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyFitnessJourneyApplication.class, args);
	}

}
