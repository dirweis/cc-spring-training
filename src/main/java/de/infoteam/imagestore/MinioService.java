package de.infoteam.imagestore;

import java.io.ByteArrayInputStream;

import org.springframework.stereotype.Service;

import de.infoteam.configuration.MinioConfigDto;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * A {@link Service} bean for providing handling with the MinIO server.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-18
 * @version 2.0
 * @see <a href="https://docs.min.io/docs/java-client-api-reference.html">MinIO Java API</a>
 *
 */
@Service
@RequiredArgsConstructor
public class MinioService {

	private final MinioConfigDto minioConfig;

	private final MinioClient client;

	/**
	 * Stores images in the images' bucket on the MinIO Server.
	 * 
	 * @param contentType the content type to be stored, must not be {@code null}
	 * @param imageId     the ID of the image, must not be {@code null}
	 * @param image       the image itself as byte array, must not be {@code null}
	 */
	@SneakyThrows
	public void storeImage(final byte[] image, final String imageId, final String contentType) {
		if (!client.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.imagesBucketName()).build())) {
			client.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.imagesBucketName()).build());
		}

		final PutObjectArgs argl = PutObjectArgs.builder().bucket(minioConfig.imagesBucketName()).object(imageId)
				.contentType(contentType).stream(new ByteArrayInputStream(image), image.length, minioConfig.blocksize())
				.build();

		client.putObject(argl);
	}
}
