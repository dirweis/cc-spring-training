package de.infoteam.imagestore;

import java.io.ByteArrayInputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * A {@link Service} bean for providing handling with the MinIO server.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-18
 * @version 1.0
 * @see <a href="https://docs.min.io/docs/java-client-api-reference.html">MinIO Java API</a>
 *
 */
@Service
public class MinioService {

	private final String bucketName;

	@Getter
	private final URL minioUrl;

	private final long minioPartSize;

	private final MinioClient client;

	/**
	 * Constructor injection due to multi-threading safety on a Singleton based bean. Uses {@link Value} for injecting
	 * the class members from single configuration values.
	 * 
	 * @param minioUrl      the URL to the MinIO server, must not be {@code null}
	 * @param username      the user name for the MinIO server, must not be {@code null}
	 * @param pw            the password for the MinIO server, must not be {@code null}
	 * @param minioPartSize the mandatory part size parameter for storing into the MinIO bucket, must not be
	 *                      {@code null}, min. value is {@code 5,242,880}
	 * @param bucketName    the bucket name for storing images
	 */
	public MinioService(@Value("${minio.username}") final String username, @Value("${minio.password}") final String pw,
			@Value("${minio.images-bucket-name}") final String bucketName,
			@Value("${minio.blocksize}") final long minioPartSize, @Value("${minio.url}") final URL minioUrl) {
		this.minioUrl = minioUrl;
		this.bucketName = bucketName;
		this.minioPartSize = minioPartSize;

		client = MinioClient.builder().credentials(username, pw).endpoint(minioUrl).build();
	}

	/**
	 * Stores images in the images' bucket on the MinIO Server.
	 * 
	 * @param contentType the content type to be stored, must not be {@code null}
	 * @param imageId     the ID of the image, must not be {@code null}
	 * @param image       the image itself as byte array, must not be {@code null}
	 */
	@SneakyThrows
	public void storeImage(final byte[] image, final String imageId, final String contentType) {
		if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
			client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
		}

		final PutObjectArgs argl = PutObjectArgs.builder().bucket(bucketName).object(imageId).contentType(contentType)
				.stream(new ByteArrayInputStream(image), image.length, minioPartSize).build();

		client.putObject(argl);
	}
}
