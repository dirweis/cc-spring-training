package de.training.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;
import lombok.NoArgsConstructor;

/**
 * A little {@link Configuration} bean providing the {@link MinioClient} for further use via Spring dependency
 * injection.
 * 
 * @since 2022-03-29
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
@Configuration
@NoArgsConstructor
class MinioConfiguration {

	/**
	 * Injects the {@link MinioClient} object on the given values in the {@link MinioConfigDto} parameter.
	 * 
	 * @param configDto the {@link ConfigurationProperties} DTO for providing the properties starting with {@code minio}
	 *                  in the service's configuration
	 * 
	 * @return the injected {@link MinioClient}, never {@code null}
	 */
	@Bean
	MinioClient getMinioClient(final MinioConfigDto configDto) {
		return MinioClient.builder().endpoint(configDto.url()).credentials(configDto.accessKey(), configDto.secretKey())
				.build();
	}
}
