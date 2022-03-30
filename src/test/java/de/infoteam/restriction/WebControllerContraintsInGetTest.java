package de.infoteam.restriction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.infoteam.AbstractSpringTestRunner;
import de.infoteam.model.Pet;
import de.infoteam.model.Pet.PetStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * Integration tests for the {@code GET} endpoints' restrictions. The tests defined here check the
 * {@link ExceptionHandler}s functionalities.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 1.1
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerContraintsInGetTest extends AbstractSpringTestRunner {

	/**
	 * Tests of the {@code GET} endpoint for getting a {@link Pet} resource by ID.
	 * 
	 * @author Dirk Weissmann
	 * @since 2022-02-21
	 * @version 1.0
	 *
	 */
	@Nested
	@DisplayName("WHEN the GET endpoint for getting a pet resource on the ID path parameter is called ")
	class GetEndpointWithPathIdTest {

		/**
		 * Tests the {@code GET} endpoint while calling it with the falsely given {@code POST} method.
		 */
		@Test
		@SneakyThrows
		@DisplayName("on the wrong HTTP method POST THEN respond with status 405 AND content type application/problem+json AND the expected response body")
		void testCallGetWithWrongHttpMethodAndExpect405() {
			mockMvc.perform(post(EndPointWithTestId)).andExpect(status().isMethodNotAllowed())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(HttpRequestMethodNotSupportedException.class))
					.andExpect(content().string(containsString("\"title\":\"Request method 'POST' not supported\"")))
					.andExpect(
							content().string(containsString("\"detail\":\"Supported method(s): [GET, PUT, DELETE]\"")));
		}

		/**
		 * Tests the {@code GET} endpoint while calling it with the ID in the wrong format.
		 */
		@Test
		@SneakyThrows
		@DisplayName("with the pet ID is in the wrong format THEN respond with status 400 AND content type application/problem+json AND the expected response body")
		void testCallGetWithWrongIdFormatAndExpect400() {
			mockMvc.perform(get(EndPointPrefix + "/1")).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(MethodArgumentTypeMismatchException.class))
					.andExpect(content().string(containsString(
							"\"title\":\"Failed to convert value of type 'String' to required type 'UUID'\"")))
					.andExpect(content().string(containsString(
							"\"invalid_params\":[{\"name\":\"petId\",\"reason\":\"Invalid UUID string: 1\"}]")));
		}
	}

	/**
	 * Tests of the {@code GET} endpoint for getting {@link Pet} resources by the parameters
	 * <dl>
	 * <dt>page</dd>
	 * <dd>the 1st argument for pageables, default value is {@code 0}</dd>
	 * <dt>size</dd>
	 * <dd>the 2nd argument for pageables, default value is {@code 20}</dd>
	 * <dt>status</dd>
	 * <dd>the query parameter to find pets on their {@link PetStatus}</dd>
	 * <dt>tags</dd>
	 * <dd>the query parameter to find pets on tags ({@link String}s)</dd>
	 * </dl>
	 * 
	 * @author Dirk Weissmann
	 * @since 2022-02-21
	 * @version 1.0
	 *
	 */
	@Nested
	@DisplayName("WHEN the GET endpoint for getting pet resources on the given query parameters is called ")
	class GetEndpointWithQueryParametersTest {

		/**
		 * Test for value scope violation of the {@code page} parameter: The value {@code -1} is not permitted.
		 */
		@Test
		@SneakyThrows
		@DisplayName("with a not acceptable page integer value THEN respond with status 400 AND content type application/problem+json AND the expected response body")
		void testCallGetWithInacceptablePageIntValue() {
			mockMvc.perform(get(EndPointPrefix + "?page=-1")).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(ConstraintViolationException.class))
					.andExpect(content().string(containsString("\"title\":\"Constraint violations")))
					.andExpect(content().string(containsString(
							"\"invalid_params\":[{\"name\":\"page\",\"reason\":\"must be greater than or equal to 0\"}]")));
		}

		/**
		 * Test for value type violation of the {@code page} parameter: The value {@code m} is not of the type
		 * {@link Integer}.
		 */
		@Test
		@SneakyThrows
		@DisplayName("with a not acceptable page string value THEN respond with status 400 AND content type application/problem+json AND the expected response body")
		void testCallGetWithInacceptablePageStringValue() {
			mockMvc.perform(get(EndPointPrefix + "?page=m")).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(MethodArgumentTypeMismatchException.class))
					.andExpect(content().string(containsString(
							"\"title\":\"Failed to convert value of type 'String' to required type 'Integer'")))
					.andExpect(content().string(containsString(
							"\"invalid_params\":[{\"name\":\"page\",\"reason\":\"For input string: \\\"m\\\"\"}]")));
		}

		/**
		 * 1st test for value scope violation of the {@code size} parameter: The value {@code -1} is not permitted.
		 */
		@Test
		@SneakyThrows
		@DisplayName("with a size value that is too low THEN respond with status 400 AND content type application/problem+json AND the expected response body")
		void testCallGetWithTooLowSizeIntValue() {
			mockMvc.perform(get(EndPointPrefix + "?size=-1")).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(ConstraintViolationException.class))
					.andExpect(content().string(containsString("\"title\":\"Constraint violations")))
					.andExpect(content().string(
							containsString("{\"name\":\"size\",\"reason\":\"must be greater than or equal to 10\"}")))
					.andExpect(content()
							.string(containsString("{\"name\":\"size\",\"reason\":\"-1 is not a multiple of 10\"}")));
		}

		/**
		 * 2nd test for value scope violation of the {@code size} parameter: The value {@code 9999999} is not permitted.
		 */
		@Test
		@SneakyThrows
		@DisplayName("with a size value that is too high THEN respond with status 400 AND content type application/problem+json AND the expected response body")
		void testCallGetWithTooHighSizeIntValue() {
			mockMvc.perform(get(EndPointPrefix + "?size=999999")).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(ConstraintViolationException.class))
					.andExpect(content().string(containsString("\"title\":\"Constraint violations")))
					.andExpect(content().string(
							containsString("{\"name\":\"size\",\"reason\":\"must be less than or equal to 1000\"}")));
		}

		/**
		 * Test for value type violation of the {@code size} parameter: The value {@code k} is not of the type
		 * {@link Integer}.
		 */
		@Test
		@SneakyThrows
		@DisplayName("with a not acceptable size string value THEN respond with status 400 AND content type application/problem+json AND the expected response body")
		void testCallGetWithInacceptableSizeStringValue() {
			mockMvc.perform(get(EndPointPrefix + "?size=k")).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(MethodArgumentTypeMismatchException.class))
					.andExpect(content().string(containsString(
							"\"title\":\"Failed to convert value of type 'String' to required type 'Integer'")))
					.andExpect(content().string(containsString(
							"\"invalid_params\":[{\"name\":\"size\",\"reason\":\"For input string: \\\"k\\\"\"}]")));
		}

		/**
		 * Test for an unknown enum value of the {@code status} parameter: The value {@code k} is not of the type
		 * {@link PetStatus}.
		 */
		@Test
		@SneakyThrows
		@DisplayName("with a not acceptable status value THEN respond with status 400 AND content type application/problem+json AND the expected response body")
		void testCallGetWithInacceptableStatusValue() {
			mockMvc.perform(get(EndPointPrefix + "?status=k")).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(MethodArgumentTypeMismatchException.class))
					.andExpect(content().string(containsString(
							"\"title\":\"Failed to convert value of type 'String' to required type 'Pet$PetStatus'\"")))
					.andExpect(content().string(containsString(
							"{\"invalid_params\":[{\"name\":\"status\",\"reason\":\"Failed to convert from type [String] to type [@RequestParam Pet$PetStatus] for value 'k';\"}]")));
		}

		/**
		 * Test for an invalid value in a tag {@link String}.
		 */
		@Test
		@SneakyThrows
		@DisplayName("with a not acceptable value for a tag THEN respond with status 400 AND content type application/problem+json AND the expected response body")
		void testCallGetWithInacceptableTagsValue() {
			mockMvc.perform(get(EndPointPrefix + "?tags=lovely,m")).andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(ConstraintViolationException.class))
					.andExpect(content().string(containsString("\"title\":\"Constraint violations")))
					.andExpect(content().string(containsString(
							"{\"invalid_params\":[{\"name\":\"<list element>\",\"reason\":\"size must be between 3 and 20\"}]")));
		}
	}
}
