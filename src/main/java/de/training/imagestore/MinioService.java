package de.training.imagestore;

import org.springframework.stereotype.Service;

import com.google.common.io.ByteSource;

import de.training.configuration.MinioConfigDto;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * A {@link Service} bean for providing handling with the MinIO server.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-18
 * @version 2.5
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
		final PutObjectArgs argl = PutObjectArgs.builder().bucket(minioConfig.imagesBucketName()).object(imageId)
				.contentType(contentType)
				.stream(ByteSource.wrap(image).openStream(), image.length, minioConfig.blocksize()).build();

		client.putObject(argl);
	}
}
