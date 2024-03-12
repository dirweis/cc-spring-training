package de.training.imagestore;

import java.io.ByteArrayInputStream;

import org.springframework.stereotype.Service;

import de.training.configuration.MinioConfigDto;
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
        final String bucketName = minioConfig.imagesBucketName();

        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        final PutObjectArgs content = PutObjectArgs.builder().bucket(bucketName).object(imageId)
                .contentType(contentType).stream(new ByteArrayInputStream(image), image.length, -1).build();

        client.putObject(content);
    }
}
