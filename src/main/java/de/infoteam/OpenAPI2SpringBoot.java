package de.infoteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenAPI2SpringBoot {
	public static void main(final String[] args) {
		new SpringApplication(OpenAPI2SpringBoot.class).run(args);
	}
}
