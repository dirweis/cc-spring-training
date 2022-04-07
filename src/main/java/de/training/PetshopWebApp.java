package de.training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetshopWebApp {
	public static void main(final String[] args) {
		new SpringApplication(PetshopWebApp.class).run(args);
	}
}
