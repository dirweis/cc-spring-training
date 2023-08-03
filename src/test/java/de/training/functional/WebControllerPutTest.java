package de.training.functional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

import de.training.AbstractSpringTestRunner;
import de.training.model.Pet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The integration tests of the {@code PUT} endpoints for positive cases.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 0.2
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerPutTest extends AbstractSpringTestRunner {

    /**
     * Tests for the "classic" {@code PUT} endpoint for overriding a {@link Pet} resource.
     * 
     * @author Dirk Weissmann
     * @since 2022-02-22
     * @version 0.2
     *
     */
    @Nested
    @DisplayName("WHEN a valid request with a")
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class OverridePetResourceTest {

        /**
         * Sends a valid request to the endpoint for overriding a {@link Pet} resource with a body containing tags.
         */
        @Test
        @SneakyThrows
        @DisplayName("body containing tags is sent to the PUT endpoint for a complete pet update THEN the response status 501 is returned since the endpoint is not yet implemented")
        void testUpdatePetWithMaxBodyAndExpect501() {
            mockMvc.perform(
                    put(EndPointWithTestId).contentType(MediaType.APPLICATION_JSON_VALUE).content(validPetBodyWithTags))
                    .andExpect(status().isNotImplemented());
        }

        /**
         * Sends a valid request to the endpoint for overriding a {@link Pet} resource with a minimalist body.
         */
        @Test
        @SneakyThrows
        @DisplayName("minimalist body is sent to the PUT endpoint for a complete pet update THEN the response status 501 is returned since the endpoint is not yet implemented")
        void testUpdatePetWithMinBodyAndExpect501() {
            mockMvc.perform(
                    put(EndPointWithTestId).contentType(MediaType.APPLICATION_JSON_VALUE).content(validMinimumPetBody))
                    .andExpect(status().isNotImplemented());
        }
    }

    /**
     * Tests for the {@code PUT} endpoint that adds an image to a specific {@link Pet} resource.
     * 
     * @author Dirk Weissmann
     * @since 2022-02-22
     * @version 0.1
     *
     */
    @Nested
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class AddImage2PetTest {

        /**
         * The only test so far: Sends a valid request to the endpoint for adding an image to a {@link Pet} resource.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN a valid request is sent to the PUT endpoint for adding an image to a pet resource THEN the response status 501 is returned since the endpoint is not yet implemented")
        void testAddPetSuccessfullyAndExpect501() {
            final File contentFile = ResourceUtils.getFile("classpath:valid_test.jpg");
            final byte[] content = FileUtils.readFileToByteArray(contentFile);

            mockMvc.perform(put(EndPointImageTestId).contentType(MediaType.IMAGE_JPEG_VALUE).content(content))
                    .andExpect(status().isNotImplemented());
        }
    }
}
