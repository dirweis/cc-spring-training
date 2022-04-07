package de.training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import de.training.configuration.MinioConfigDto;

/**
 * The main class for the web microservice.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-16
 * @version 1.2
 *
 */
@EnableJpaAuditing
@EnableConfigurationProperties(MinioConfigDto.class)
@SpringBootApplication(exclude = { MultipartAutoConfiguration.class, JmxAutoConfiguration.class })
public class PetshopWebApp {

	/**
	 * The main method not doing much but running the service until stopped explicitly.
	 * 
	 * @param args no arguments needed, for examples see
	 *             <a href="https://www.baeldung.com/spring-boot-command-line-arguments">Baeldung Spring arguments</a>
	 */
	public static void main(final String[] args) {
		SpringApplication.run(PetshopWebApp.class, args);
	}
}
