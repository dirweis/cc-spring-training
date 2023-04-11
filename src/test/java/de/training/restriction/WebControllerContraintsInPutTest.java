package de.training.restriction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ResourceUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.training.AbstractSpringTestRunner;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * Integration tests for the {@code PUT} endpoints' restrictions. The tests defined here check the
 * {@link ExceptionHandler}s functionalities.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 1.2
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerContraintsInPutTest extends AbstractSpringTestRunner {

    /**
     * Tests for the problems in calling the {@code PUT} endpoint {@code /petstore/petservice/v1/pets/<petId>}.
     * 
     * @author Dirk Weissmann
     * @since 2022-02-22
     * @version 1.0
     *
     */
    @Nested
    class Put4PetResourceOverrideTest {

        /**
         * Tests the {@code PUT} endpoint while calling it with the falsely given {@code POST} method.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the HTTP method is POST THEN respond with status 405 AND content type application/problem+json AND the expected response body")
        void testCallPutWithWrongHttpMethodAndExpect405() {
            mockMvc.perform(post(EndPointWithTestId)).andExpect(status().isMethodNotAllowed())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(HttpRequestMethodNotSupportedException.class))
                    .andExpect(content().string(containsString("\"title\":\"Request method 'POST' is not supported\"")))
                    .andExpect(content().string(containsString("\"detail\":\"Supported method(s): [")));
        }

        /**
         * Tests the {@code PUT} endpoint while calling it without the {@code Content-Type} header.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the HTTP request header Content-Type is missing THEN respond with status 415 AND content type application/problem+json AND the expected response body")
        void testCallPutWithMissingContentTypeAndExpect415() {
            mockMvc.perform(put(EndPointWithTestId)).andExpect(status().isUnsupportedMediaType())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(HttpMediaTypeNotSupportedException.class))
                    .andExpect(content().string(containsString("Request header 'content-type' not found")))
                    .andExpect(content()
                            .string(containsString("\"detail\":\"Supported media type(s): [application/json]\"")));
        }

        /**
         * Tests the {@code PUT} endpoint while calling it with a wrong {@code Content-Type} header.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the HTTP request header Content-Type is wrong (but known) THEN respond with status 415 AND content type application/problem+json AND the expected response body")
        void testCallPutWithWrongContentTypeAndExpect415() {
            mockMvc.perform(put(EndPointWithTestId).contentType(MediaType.APPLICATION_XML_VALUE))
                    .andExpect(status().isUnsupportedMediaType())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(HttpMediaTypeNotSupportedException.class))
                    .andExpect(content()
                            .string(containsString("\"title\":\"Content-Type 'application/xml' is not supported\"")))
                    .andExpect(content()
                            .string(containsString("\"detail\":\"Supported media type(s): [application/json]\"")));
        }

        /**
         * Tests the {@code PUT} endpoint while calling it with a crazy {@code Content-Type} header.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the HTTP request header Content-Type is wrong (and not known) THEN respond with status 415 AND content type application/problem+json AND the expected response body")
        void testCallPutWithUnknownContentTypeAndExpect415() {
            mockMvc.perform(put(EndPointWithTestId).contentType("crazy")).andExpect(status().isUnsupportedMediaType())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(HttpMediaTypeNotSupportedException.class))
                    .andExpect(content().string(
                            containsString("\"title\":\"Invalid mime type \\\"crazy\\\": does not contain '/'\"")));
        }

        /**
         * Nested class for testing various syntactical request body violations. Our non-violent body example:
         * 
         * <pre>
        {
        "name": "Mauzi",
        "status": "AVAILABLe",
        "description" : "Eine ganz, ganz liebe Katze, die ganz einfach zu halten und stubenrein ist.",
        "tags": [
            "lovely",
            "not snappish",
            "nervous"
        ],
        "category" : "cat"
        }
         * </pre>
         * 
         * @author Dirk Weissmann
         * @since 2022-02-18
         * @version 1.1
         *
         */
        @DisplayName("WHEN the request body ")
        @Nested
        class JsonSyntacticalViolationTest {

            private static final String invalidFolderPath = "classpath:invalid_request_bodies/syntactical/";

            /**
             * 1st invalid body test: The body is missing.
             */
            @Test
            @SneakyThrows
            @DisplayName("is missing THEN respond with status 400 AND content type application/problem+json AND the expected response body")
            void testCallPutWithMissingBodyAndExpect400() {
                mockMvc.perform(put(EndPointWithTestId).contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                        .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                                .isInstanceOf(HttpMessageNotReadableException.class))
                        .andExpect(content().string(containsString("\"title\":\"JSON Parse Error\"")))
                        .andExpect(content().string(containsString("\"detail\":\"Required request body is missing\"")));
            }

            /**
             * 4 invalid body tests: The body is syntactically violated
             * <ul>
             * <li>with a missing opening brace</li>
             * <li>with the wrong format ({@code XML} instead of {@code JSON})</li>
             * <li>a missing comma in {@code JSON}</li>
             * <li>a missing quotation in {@code JSON}</li>
             * </ul>
             * 
             * @param filename       the filename of the input file that contains syntactically violated content
             * @param expectedDetail the expected {@link String} in the response body's {@code detail} field
             * 
             * @see #provideParameters()
             */
            @SneakyThrows
            @ParameterizedTest
            @MethodSource("provideParameters")
            @DisplayName("contains no opening brace OR is in the wrong format OR with a missing comma OR with a missing quotation THEN respond with status 400 AND content type application/problem+json AND the expected response body")
            void testForSyntacticalInvalidBody(final String filename, final String expectedDetail) {
                final File contentFile = ResourceUtils.getFile(invalidFolderPath + filename);
                final String content = Files.contentOf(contentFile, StandardCharsets.UTF_8);

                mockMvc.perform(put(EndPointWithTestId).contentType(MediaType.APPLICATION_JSON).content(content))
                        .andExpect(status().isBadRequest())
                        .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                                .isInstanceOf(HttpMessageNotReadableException.class))
                        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                        .andExpect(content().string(containsString("\"title\":\"JSON Parse Error\"")))
                        .andExpect(content().string(containsString(expectedDetail)));
            }

            /**
             * Provides the 2 {@link Arguments} for the parameterized test
             * {@link #testForSyntacticalInvalidBody(String, String) testForSyntacticalInvalidBody}
             * <ul>
             * <li>the filename of the test file with invalid content</li>
             * <li>the expected {@link String} in the response's {@code detail} field</li>
             * </ul>
             * 
             * @return the {@link Stream} of {@link Arguments}
             */
            private static Stream<Arguments> provideParameters() {
                return Stream.of(Arguments.of("missing_opening_brace.json",
                        "\"detail\":\"Cannot construct instance of `Pet` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('name') at [Source: line: 2, column: 2]\""),
                        Arguments.of("noJson.xml",
                                "\"detail\":\"Unexpected character ('<' (code 60)): expected a valid value (JSON String, Number, Array, Object or token 'null', 'true' or 'false') at line 1, column 2\""),
                        Arguments.of("missing_closing_brace.json",
                                "\"detail\":\"Unexpected end-of-input: expected close marker for Object (start marker at [Source: line: 1, column: 1]) at [Source: line: 11, column: 1]\""),
                        Arguments.of("missing_comma.json",
                                "\"detail\":\"Unexpected character ('\\\"' (code 34)): was expecting comma to separate Object entries at line 3, column 3\""),
                        Arguments.of("missing_quotation.json",
                                "\"detail\":\"Unexpected character ('i' (code 105)): was expecting double-quote to start field name at line 2, column 6\""));
            }
        }

        /**
         * Nested class for testing various semantic request body violations. Our non-violent body example:
         * 
         * <pre>
        {
        "name": "Mauzi",
        "status": "AVAILABLe",
        "description" : "Eine ganz, ganz liebe Katze, die ganz einfach zu halten und stubenrein ist.",
        "tags": [
            "lovely",
            "not snappish",
            "nervous"
        ],
        "category" : "cat"
        }
         * </pre>
         * 
         * @author Dirk Weissmann
         * @since 2022-02-18
         * @version 1.1
         *
         */
        @DisplayName("WHEN the request body ")
        @Nested
        class JsonSemanticViolationTest {

            private static final String invalidFolderPath = "classpath:invalid_request_bodies/semantic/";

            /**
             * 4 invalid body tests: The body is semantic violated with
             * <ul>
             * <li>an invalid enumeration value</li>
             * <li>an invalid ID type</li>
             * <li>a missing (mandatory) field</li>
             * <li>various constraint violations at once</li>
             * <li>the special case with a photo URL entries which is rejected since they are forbidden for DB
             * insertion</li>
             * </ul>
             * 
             * @param filename       the filename of the input file that contains syntactically violated content
             * @param exClass        the expected exception's class
             * @param expectedDetail the expected {@link String} in the response body's {@code detail} field
             * 
             * @see #provideParameters()
             */
            @SneakyThrows
            @ParameterizedTest
            @MethodSource("provideParameters")
            @DisplayName("contains an invalid enumeration value OR an invalid ID type OR a missing (mandatory) field OR various constraint violations at once OR a valid ID which is rejected since it's a POST request and IDs are forbidden THEN respond with status 422 AND content type application/problem+json AND the expected response body")
            void testForSemanticInvalidBody(final String filename, final Class<Exception> exClass,
                    final String expectedDetail) {
                final File contentFile = ResourceUtils.getFile(invalidFolderPath + filename + ".json");
                final String content = Files.contentOf(contentFile, StandardCharsets.UTF_8);

                mockMvc.perform(put(EndPointWithTestId).contentType(MediaType.APPLICATION_JSON).content(content))
                        .andExpect(status().isUnprocessableEntity())
                        .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                                .isInstanceOf(exClass))
                        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                        .andExpect(content().string(containsString("\"title\":\"Request body validation failed\"")))
                        .andExpect(content().string(containsString(expectedDetail)));
            }

            /**
             * Provides the 3 {@link Arguments} for the parameterized test
             * {@link #testForSemanticInvalidBody(String, Class, String) testForSemanticInvalidBody}
             * <ul>
             * <li>the filename of the test file with invalid content</li>
             * <li>the expected exception's class</li>
             * <li>the expected {@link String} in the response's {@code detail} field</li>
             * </ul>
             * 
             * @return the {@link Stream} of {@link Arguments}
             */
            private static Stream<Arguments> provideParameters() {
                return Stream.of(Arguments.of("invalid_enum_value", HttpMessageNotReadableException.class,
                        "\"invalid_params\":[{\"name\":\"category\",\"reason\":\"Cannot deserialize value of type `Pet$Category` from String \\\"cats\\\": not one of the values accepted for Enum class: [BIRD, DOG, MOUSE, CAT, SPIDER] at [Source: line: 8, column: 18] (through reference chain: Pet[\\\"category\\\"])\"}]"),
                        Arguments.of("invalid_id_type", HttpMessageNotReadableException.class,
                                "\"invalid_params\":[{\"name\":\"id\",\"reason\":\"Cannot deserialize value of type `UUID` from String \\\"1\\\": UUID has to be represented by standard 36-char representation at [Source: line: 2, column: 8] (through reference chain: Pet[\\\"id\\\"])\"}]"),
                        Arguments.of("missing_field", MethodArgumentNotValidException.class,
                                "\"invalid_params\":[{\"name\":\"name\",\"reason\":\"must not be null\"}]"),
                        Arguments.of("various_semantic_violations", MethodArgumentNotValidException.class,
                                "{\"name\":\"name\",\"reason\":\"size must be between 3 and 30\"}"),
                        Arguments.of("various_semantic_violations", MethodArgumentNotValidException.class,
                                "{\"name\":\"description\",\"reason\":\"size must be between 30 and 1000\"}"),
                        Arguments.of("forbidden_photo_urls", ConstraintViolationException.class,
                                "\"invalid_params\":[{\"name\":\"photo-urls\",\"reason\":\"POST request: The field pet.photo-urls must be null\"}]"));
            }
        }
    }

    /**
     * Tests for the problems in calling the {@code PUT} endpoint {@code /petstore/petservice/v1/pets/<petId>/image}.
     * 
     * @author Dirk Weissmann
     * @since 2022-02-22
     * @version 1.0
     *
     */
    @Nested
    class Put4PetResourceImageTest {

        /**
         * Tests the {@code PUT} endpoint while calling it with the falsely given {@code POST} method.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the HTTP method is POST THEN respond with status 405 AND content type application/problem+json AND the expected response body")
        void testCallPutWithWrongHttpMethodAndExpect405() {
            mockMvc.perform(post(EndPointImageTestId)).andExpect(status().isMethodNotAllowed())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(HttpRequestMethodNotSupportedException.class))
                    .andExpect(content().string(containsString("\"title\":\"Request method 'POST' is not supported\"")))
                    .andExpect(content().string(containsString("\"detail\":\"Supported method(s): [PUT]\"")));
        }

        /**
         * Tests the {@code PUT} endpoint while calling it without the {@code Content-Type} header.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the HTTP request header Content-Type is missing THEN respond with status 415 AND content type application/problem+json AND the expected response body")
        void testCallPutWithMissingContentTypeAndExpect415() {
            mockMvc.perform(put(EndPointImageTestId)).andExpect(status().isUnsupportedMediaType())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(HttpMediaTypeNotSupportedException.class))
                    .andExpect(content().string(containsString("Request header 'content-type' not found")))
                    .andExpect(content().string(containsString(
                            "\"detail\":\"Supported media type(s): [image/gif, image/jpeg, image/png]\"")));
        }

        /**
         * Tests the {@code PUT} endpoint while calling it with a wrong {@code Content-Type} header.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the HTTP request header Content-Type is wrong (but known) THEN respond with status 415 AND content type application/problem+json AND the expected response body")
        void testCallPutWithWrongContentTypeAndExpect415() {
            mockMvc.perform(put(EndPointImageTestId).contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isUnsupportedMediaType())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(HttpMediaTypeNotSupportedException.class))
                    .andExpect(content()
                            .string(containsString("\"title\":\"Content-Type 'application/json' is not supported\"")))
                    .andExpect(content().string(containsString(
                            "\"detail\":\"Supported media type(s): [image/gif, image/jpeg, image/png]\"")));
        }

        /**
         * Tests the {@code PUT} endpoint while calling it with a crazy {@code Content-Type} header.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the HTTP request header Content-Type is wrong (and not known) THEN respond with status 415 AND content type application/problem+json AND the expected response body")
        void testCallPutWithUnknownContentTypeAndExpect415() {
            mockMvc.perform(put(EndPointImageTestId).contentType("crazy")).andExpect(status().isUnsupportedMediaType())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(HttpMediaTypeNotSupportedException.class))
                    .andExpect(content().string(
                            containsString("\"title\":\"Invalid mime type \\\"crazy\\\": does not contain '/'\"")));
        }

        /**
         * Tests the {@code PUT} endpoint while calling it with an ID in the wrong format.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the given pet ID is not well-formed as UUID THEN respond with status 400 AND content type application/problem+json AND the expected response body")
        void testCallPutWithInvalidIdFormatAndExpect400() {
            mockMvc.perform(put(EndPointPrefix + "/invalid/image").contentType(MediaType.IMAGE_JPEG_VALUE))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(MethodArgumentTypeMismatchException.class))
                    .andExpect(content().string(containsString(
                            "\"title\":\"Failed to convert value of type 'String' to required type 'UUID'\"")))
                    .andExpect(content().string(containsString(
                            "\"invalid_params\":[{\"name\":\"petId\",\"reason\":\"Invalid UUID string: invalid\"}]")));
        }

        /**
         * Tests the {@code PUT} endpoint while calling it with a too small image as body.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the given image is too small THEN respond with status 422 AND content type application/problem+json AND the expected response body")
        void testCallPutWithTooSmallBodyAndExpect422() {
            final File contentFile = ResourceUtils
                    .getFile("classpath:invalid_request_bodies/images/TDD_cool_2_icon_size.png");
            final byte[] content = FileUtils.readFileToByteArray(contentFile);

            mockMvc.perform(put(EndPointImageTestId).contentType(MediaType.IMAGE_PNG_VALUE).content(content))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(ConstraintViolationException.class))
                    .andExpect(content().string(containsString("\"title\":\"Request body validation failed\"")))
                    .andExpect(content().string(containsString(
                            "\"invalid_params\":[{\"name\":\"body\",\"reason\":\"size must be between 10000 and 2000000\"}]")));
        }

        /**
         * Tests the {@code PUT} endpoint while calling it with a too big image as body.
         */
        @Test
        @SneakyThrows
        @DisplayName("WHEN the given image is too big THEN respond with status 422 AND content type application/problem+json AND the expected response body")
        void testCallPutWithTooBigBodyAndExpect422() {
            final File contentFile = ResourceUtils
                    .getFile("classpath:invalid_request_bodies/images/invalid_too_big.jpg");
            final byte[] content = FileUtils.readFileToByteArray(contentFile);

            mockMvc.perform(put(EndPointImageTestId).contentType(MediaType.IMAGE_JPEG_VALUE).content(content))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                    .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                            .isInstanceOf(ConstraintViolationException.class))
                    .andExpect(content().string(containsString("\"title\":\"Request body validation failed\"")))
                    .andExpect(content().string(containsString(
                            "\"invalid_params\":[{\"name\":\"body\",\"reason\":\"size must be between 10000 and 2000000\"}]")));
        }
    }
}
