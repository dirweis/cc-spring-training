package de.training.restriction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

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

import de.training.AbstractSpringTestRunner;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * Integration tests for the {@code POST} endpoints' restrictions. The tests defined here check the
 * {@link ExceptionHandler}s functionalities.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-18
 * @version 1.2
 *
 */
@DisplayName("WHEN the HTTP")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerContraintsInPostTest extends AbstractSpringTestRunner {

    /**
     * Tests the {@code POST} endpoint while calling it with the falsely given {@code DELETE} method.
     */
    @Test
    @SneakyThrows
    @DisplayName("method is DELETE THEN respond with status 405 AND content type application/problem+json AND the expected response body")
    void testCallPostWithWrongHttpMethodAndExpect405() {
        mockMvc.perform(delete(END_POINT_PREFIX)).andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                        .isInstanceOf(HttpRequestMethodNotSupportedException.class))
                .andExpect(content().string(containsString("\"title\":\"Request method 'DELETE' is not supported\"")))
                .andExpect(content().string(containsString("\"detail\":\"Supported method(s): [")));
    }

    /**
     * Tests the {@code POST} endpoint while calling it with the endpoint without the {@code Content-Type} header.
     */
    @Test
    @SneakyThrows
    @DisplayName("request header Content-Type is missing THEN respond with status 415 AND content type application/problem+json AND the expected response body")
    void testCallPostWithMissingContentTypeAndExpect415() {
        mockMvc.perform(post(END_POINT_PREFIX)).andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                        .isInstanceOf(HttpMediaTypeNotSupportedException.class))
                .andExpect(content().string(containsString("Request header 'Content-Type' not found"))).andExpect(
                        content().string(containsString("\"detail\":\"Supported media type(s): [application/json]\"")));
    }

    /**
     * Tests the {@code POST} endpoint while calling it with the endpoint with a wrong {@code Content-Type} header.
     */
    @Test
    @SneakyThrows
    @DisplayName("request header Content-Type is wrong (but known) THEN respond with status 415 AND content type application/problem+json AND the expected response body")
    void testCallPostWithWrongContentTypeAndExpect415() {
        mockMvc.perform(post(END_POINT_PREFIX).contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                        .isInstanceOf(HttpMediaTypeNotSupportedException.class))
                .andExpect(content()
                        .string(containsString("\"title\":\"Content-Type 'application/xml' is not supported\"")))
                .andExpect(
                        content().string(containsString("\"detail\":\"Supported media type(s): [application/json]\"")));
    }

    /**
     * Tests the {@code POST} endpoint while calling it with the endpoint with a crazy {@code Content-Type} header.
     */
    @Test
    @SneakyThrows
    @DisplayName("request header Content-Type is wrong (and not known) THEN respond with status 415 AND content type application/problem+json AND the expected response body")
    void testCallPostWithUnknownContentTypeAndExpect415() {
        mockMvc.perform(post(END_POINT_PREFIX).contentType("crazy")).andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                        .isInstanceOf(HttpMediaTypeNotSupportedException.class))
                .andExpect(content()
                        .string(containsString("\"title\":\"Invalid mime type \\\"crazy\\\": does not contain '/'\"")));
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
    @Nested
    @DisplayName("WHEN the request body ")
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class JsonSyntacticalViolationTest {

        private static final String INVALID_FOLDER_PATH = "classpath:invalid_request_bodies/syntactical/";

        /**
         * 1st invalid body test: The body is missing.
         */
        @Test
        @SneakyThrows
        @DisplayName("is missing THEN respond with status 400 AND content type application/problem+json AND the expected response body")
        void testCallPostWithMissingBodyAndExpect400() {
            mockMvc.perform(post(END_POINT_PREFIX).contentType(MediaType.APPLICATION_JSON_VALUE))
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
            final File contentFile = ResourceUtils.getFile(INVALID_FOLDER_PATH + filename);
            final String content = Files.contentOf(contentFile, StandardCharsets.UTF_8);

            mockMvc.perform(post(END_POINT_PREFIX).contentType(MediaType.APPLICATION_JSON).content(content))
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
            return Stream.of(
                    Arguments.of("missing_opening_brace.json", "\"detail\":\"No parsable JSON. Opening brace missing?"),
                    Arguments.of("noJson.xml",
                            "\"detail\":\"Unexpected character ('<' (code 60)): expected a valid value (JSON String, Number, Array, Object or token 'null', 'true' or 'false') at line 1, column 1\""),
                    Arguments.of("missing_closing_brace.json",
                            "\"detail\":\"Not well-formed for the JSON end. Missing brace?"),
                    Arguments.of("missing_comma.json",
                            "\"detail\":\"Unexpected character ('\\\"' (code 34)): was expecting comma to separate Object entries at line 3, column 2\""),
                    Arguments.of("missing_quotation.json",
                            "\"detail\":\"Unexpected character ('i' (code 105)): was expecting double-quote to start field name at line 2, column 5\""));
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
    @Nested
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class JsonSemanticViolationTest {

        private static final String INVALID_FOLDER_PATH = "classpath:invalid_request_bodies/semantic/";

        /**
         * 5 invalid body tests: The body is semantic violated with
         * <ul>
         * <li>an invalid enumeration value</li>
         * <li>an invalid ID type</li>
         * <li>a missing (mandatory) field</li>
         * <li>various constraint violations at once</li>
         * <li>the special case with a valid ID which is rejected since it's a POST request and IDs are forbidden</li>
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
        @DisplayName("WHEN the request body contains an invalid enumeration value OR an invalid ID type OR a missing (mandatory) field OR various constraint violations at once OR a valid ID which is rejected since it's a POST request and IDs are forbidden THEN respond with status 422 AND content type application/problem+json AND the expected response body")
        void testForSemanticInvalidBody(final String filename, final Class<Exception> exClass,
                final String expectedDetail) {
            final File contentFile = ResourceUtils.getFile(INVALID_FOLDER_PATH + filename + ".json");
            final String content = Files.contentOf(contentFile, StandardCharsets.UTF_8);

            mockMvc.perform(post(END_POINT_PREFIX).contentType(MediaType.APPLICATION_JSON).content(content))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(
                            (final MvcResult result) -> assertThat(result.getResolvedException()).isInstanceOf(exClass))
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
                    "\"errors\":[{\"pointer\":\"#/category\",\"detail\":\"Cannot deserialize value of type `Pet$Category` from String \\\"cats\\\": not one of the values accepted for Enum class: [BIRD, DOG, MOUSE, CAT, SPIDER] (line 8, column 18)"),
                    Arguments.of("invalid_id_type", HttpMessageNotReadableException.class,
                            "\"errors\":[{\"pointer\":\"#/id\",\"detail\":\"Cannot deserialize value of type `UUID` from String \\\"1\\\": UUID has to be represented by standard 36-char representation (line 2, column 8)\"}]"),
                    Arguments.of("missing_field", MethodArgumentNotValidException.class,
                            "\"errors\":[{\"pointer\":\"#/name\",\"detail\":\"must not be null\"}]"),
                    Arguments.of("various_semantic_violations", MethodArgumentNotValidException.class,
                            "{\"pointer\":\"#/name\",\"detail\":\"size must be between 3 and 30\"}"),
                    Arguments.of("various_semantic_violations", MethodArgumentNotValidException.class,
                            "{\"pointer\":\"#/description\",\"detail\":\"size must be between 30 and 1000\"}"),
                    Arguments.of("forbidden_id", ConstraintViolationException.class,
                            "\"errors\":[{\"pointer\":\"#/id\",\"detail\":\"POST request: The field pet.id must be null\"}]"),
                    Arguments.of("forbidden_photo_urls", ConstraintViolationException.class,
                            "\"errors\":[{\"pointer\":\"#/photo-urls\",\"detail\":\"POST request: The field pet.photo-urls must be null\"}]"));
        }
    }
}
