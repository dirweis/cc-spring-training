package de.training.configuration;

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
 * @param browsePort       the port for browsing to the document
 * @param accessKey        the MinIO user's username token
 * @param secretKey        the MinIO user's password token
 * @param imagesBucketName the name for the images' bucket on the MinIO server
 * 
 * @see <a href="https://docs.min.io/minio/baremetal/introduction/minio-overview.html">MinIO Object Storage</a>
 *
 */
@ConfigurationProperties(prefix = "minio")
public record MinioConfigDto(URL url, int browsePort, String accessKey, String secretKey, String imagesBucketName) {
    /* nothing special in this Record */
}
