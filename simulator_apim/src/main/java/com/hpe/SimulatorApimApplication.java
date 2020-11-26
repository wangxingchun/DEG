package com.hpe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimulatorApimApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimulatorApimApplication.class, args);
	}

}
