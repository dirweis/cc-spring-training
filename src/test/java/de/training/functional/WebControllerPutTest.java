package de.training.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ResourceUtils;

import de.training.AbstractSpringTestRunner;
import de.training.db.model.PetEntity;
import de.training.db.model.TagEntity;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import io.minio.MinioClient;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The integration tests of the {@code PUT} endpoints for positive cases.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerPutTest extends AbstractSpringTestRunner {

    @BeforeEach
    void prepareDb() {
        petRepository.deleteAll();
    }

    @AfterEach
    void cleanUpDb() {
        petRepository.deleteAll();
    }

    /**
     * Tests for the "classic" {@code PUT} endpoint for overriding a {@link Pet} resource.
     * 
     * @author Dirk Weissmann
     * @since 2022-02-22
     * @version 1.0
     *
     */
    @Nested
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @DisplayName("WHEN a valid request is sent to the PUT endpoint for a pet that is ")
    class OverridePetResourceTest {

        /**
         * Sends a valid request to the endpoint for overriding a {@link Pet} resource with an unknown pet ID.
         */
        @Test
        @SneakyThrows
        @DisplayName("not already stored in the database THEN the response with status 404 and an appropriate body is returned")
        void testUpdatePetNotFoundAndExpect404() {
            mockMvc.perform(
                    put(EndPointWithTestId).contentType(MediaType.APPLICATION_JSON).content(validPetBodyWithTags))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(EntityNotFoundException.class))
                    .andExpect(content()
                            .string(containsString("Resource with ID " + testId + " not found in the persistence")));
        }

        /**
         * Sends a valid request to the endpoint for overriding a {@link Pet} resource with a known pet ID. Assures that
         * the {@link Entity} in the database became overwritten. Includes tags for the update while the
         * {@link PetEntity} on the database has no {@link TagEntity} objects.
         */
        @Test
        @SneakyThrows
        @DisplayName("already stored in the database without tags THEN the response status 204 is returned")
        void testUpdatePetSuccessfullyWithTagsAndExpect204() {
            final PetEntity entity = createTestEntity(false);

            petRepository.save(entity);

            final UUID id = entity.getId();

            mockMvc.perform(put(EndPointPrefix + "/" + id).contentType(MediaType.APPLICATION_JSON)
                    .content(validPetBodyWithTags)).andExpect(status().isNoContent());

            final Optional<PetEntity> newEntityOptional = petRepository.findById(id);

            assertThat(newEntityOptional).isPresent().isNotEmpty();

            final PetEntity newEntity = newEntityOptional.get();

            assertThat(newEntity.getCategory()).isEqualTo(Category.CAT);
            assertThat(newEntity.getTags()).hasSize(3);
        }

        /**
         * Sends a valid request to the endpoint for overriding a {@link Pet} resource with a known pet ID. Assures that
         * the {@link Entity} in the database became overwritten. The request body does not contain tags for the update
         * while the {@link PetEntity} on the database has some {@link TagEntity} objects.
         */
        @Test
        @SneakyThrows
        @DisplayName("already stored in the database with tags THEN the response status 204 is returned")
        void testUpdatePetSuccessfullyWithoutTagsAndExpect204() {
            final PetEntity entity = createTestEntity(true);

            petRepository.save(entity);

            final UUID id = entity.getId();

            mockMvc.perform(
                    put(EndPointPrefix + "/" + id).contentType(MediaType.APPLICATION_JSON).content(validMinimumPetBody))
                    .andExpect(status().isNoContent());

            final Optional<PetEntity> newEntityOptional = petRepository.findById(id);

            assertThat(newEntityOptional).isPresent().isNotEmpty();

            final PetEntity newEntity = newEntityOptional.get();

            assertThat(newEntity.getCategory()).isEqualTo(Category.CAT);
            assertThat(newEntity.getTags()).isEmpty();
        }
    }

    /**
     * Tests for the {@code PUT} endpoint that adds an image to a specific {@link Pet} resource.
     * 
     * @author Dirk Weissmann
     * @since 2022-02-22
     * @version 1.0
     *
     */
    @Nested
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @DisplayName("WHEN a valid request is sent to the PUT endpoint for adding an image")
    class AddImage2PetTest {

        private static MinioClient minioClient;
        private static String minioBucketName;
        private static byte[] content;
        private static String imageId;

        /**
         * The Show Case for initializing some of the service's parameters in a static context: Initializes a
         * {@link MinioClient} object on the needed parameters from {@code application.yml}.
         */
        @BeforeAll
        @SneakyThrows
        static void prepareAll() {
            final YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();

            yamlFactory.setResources(new ClassPathResource("application.yml"));

            final Properties properties = yamlFactory.getObject();
            final URL minioUrl = new URL(properties.getProperty("minio.url"));
            final String minioAccessKey = properties.getProperty("minio.access-key");
            final String minioSecretKey = properties.getProperty("minio.secret-key");

            minioBucketName = properties.getProperty("minio.images-bucket-name");
            minioClient = MinioClient.builder().endpoint(minioUrl).credentials(minioAccessKey, minioSecretKey).build();

            final File contentFile = ResourceUtils.getFile("classpath:valid_test.jpg");

            content = FileUtils.readFileToByteArray(contentFile);
            imageId = UUID.nameUUIDFromBytes(content).toString();
        }

        /**
         * Sends a valid request to the endpoint for adding an image to a {@link Pet} resource that is not found.
         */
        @Test
        @SneakyThrows
        @DisplayName("to an unknown pet resource THEN the response status 404 is returned")
        void testAddImageToPetNotFoundAndExpect404() {
            mockMvc.perform(put(EndPointImageTestId).contentType(MediaType.IMAGE_JPEG).content(content))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(EntityNotFoundException.class))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(content().string(containsString("\"title\":\"Not found\"")))
                    .andExpect(content().string(containsString(
                            "\"detail\":\"Resource with ID " + testId + " not found in the persistence\"")));
        }

        /**
         * Sends a valid request to the endpoint for adding an image to a {@link Pet} resource that is found.
         */
        @Test
        @SneakyThrows
        @DisplayName("to a known pet resource THEN the response status 201 is returned")
        void testAddImageToPetFoundAndExpect201() {
            final PetEntity entity = createTestEntity(true);

            petRepository.save(entity);

            mockMvc.perform(put(EndPointPrefix + "/" + entity.getId() + "/image").contentType(MediaType.IMAGE_JPEG)
                    .content(content)).andExpect(status().isCreated());

            final Optional<PetEntity> resultEntityOption = petRepository.findById(entity.getId());

            assertThat(resultEntityOption).isPresent();

            final PetEntity resultEntity = resultEntityOption.get();

            assertThat(resultEntity.getPhotoUrls().size()).isOne();
            assertThat(resultEntity.getPhotoUrls().get(0).getUrl().toString()).endsWith(imageId);
        }

        /**
         * Sends a valid request twice to the endpoint for adding an image to a {@link Pet} resource which ends up in a
         * conflict.
         */
        @Test
        @SneakyThrows
        @DisplayName("to a pet resource twice THEN the response status 409 is returned")
        void testAddImageToPetTwiceAndExpect409() {
            final PetEntity entity = createTestEntity(true);

            petRepository.save(entity);

            mockMvc.perform(put(EndPointPrefix + "/" + entity.getId() + "/image").contentType(MediaType.IMAGE_JPEG)
                    .content(content)).andExpect(status().isCreated());

            mockMvc.perform(put(EndPointPrefix + "/" + entity.getId() + "/image").contentType(MediaType.IMAGE_JPEG)
                    .content(content))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(DataIntegrityViolationException.class))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(content().string(containsString("\"title\":\"Entry already exists\"")));
        }

        /**
         * Removes the test bucket's image from the MinIO server after all tests are done.
         */
        @AfterAll
        @SneakyThrows
        static void tearDown() {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioBucketName).object(imageId).build());
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(minioBucketName).build());
        }
    }
}
