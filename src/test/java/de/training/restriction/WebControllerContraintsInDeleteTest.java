package de.training.restriction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.training.AbstractSpringTestRunner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * Integration tests for the {@code DELETE} endpoints' restrictions. The tests defined here check the
 * {@link ExceptionHandler}s functionalities.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 1.1
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@DisplayName("WHEN the DELETE endpoint is called with")
class WebControllerContraintsInDeleteTest extends AbstractSpringTestRunner {

	/**
	 * Tests the {@code DELETE} endpoint while calling it with the falsely given {@code POST} method.
	 */
	@Test
	@SneakyThrows
	@DisplayName("a wrong HTTP method THEN respond with status 405 AND content type application/problem+json AND the expected response body")
	void testWrongHttpMethodAndExpect405() {
		mockMvc.perform(post(EndPointWithTestId)).andExpect(status().isMethodNotAllowed())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
				.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
						.isInstanceOf(HttpRequestMethodNotSupportedException.class))
				.andExpect(content().string(containsString("\"title\":\"Request method 'POST' not supported\"")))
				.andExpect(content().string(containsString("\"detail\":\"Supported method(s): [GET, PUT, DELETE]\"")));
	}

	/**
	 * Tests the {@code DELETE} endpoint while calling it with an invalid ID (invalid format).
	 */
	@Test
	@SneakyThrows
	@DisplayName("an ID that is not well-formed THEN respond with status 400 AND content type application/problem+json AND the expected response body")
	void testInvalidPetIdAndExpect400() {
		mockMvc.perform(delete(EndPointPrefix + "/no")).andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
				.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
						.isInstanceOf(MethodArgumentTypeMismatchException.class))
				.andExpect(content().string(containsString(
						"\"title\":\"Failed to convert value of type 'String' to required type 'UUID'\"")))
				.andExpect(content().string(containsString(
						"\"invalid_params\":[{\"name\":\"petId\",\"reason\":\"Invalid UUID string: no\"}]")));
	}
}
