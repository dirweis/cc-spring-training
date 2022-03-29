package de.infoteam.configuration;

import java.net.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The DTO to hold the MinIO server configuration from the {@code application.yml}.
 * 
 * @since 2022-03-29
 * @version 1.0
 * @author Dirk Weissmann
 * 
 * @param url              the MinIO server's URL
 * @param username         the MinIO user's username
 * @param password         the MinIO user's password
 * @param imagesBucketName the name for the images' bucket on the MinIO server
 * @param blocksize        the mandatory part size parameter for storing into the MinIO bucket, must not be
 *                         {@code null}, min. value is {@code 5,242,880}
 * 
 * @see <a href="https://docs.min.io/minio/baremetal/introduction/minio-overview.html">MinIO Object Storage</a>
 *
 */
@ConfigurationProperties(prefix = "minio")
public record MinioConfigDto(URL url, String username, String password, String imagesBucketName, long blocksize) {
	/* nothing special in this Record */
}
