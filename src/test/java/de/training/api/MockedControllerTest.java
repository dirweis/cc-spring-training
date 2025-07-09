package de.training.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import de.training.AbstractSpringTestRunner;
import de.training.model.Pet;
import de.training.model.Rfc9457Error;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The unit tests expecting a mocked bean for making the {@link PetsApiController} react on the specified behavior.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@DisplayName("WHEN the Post endpoint for adding a new Pet resource is called and the implementation crashes internally")
class MockedControllerTest extends AbstractSpringTestRunner {

	@MockitoBean
	private PetsApiController controller;

	/**
	 * Send something to the {@code POST} endpoint for creating a new {@link Pet} resource and make the controller throw
	 * a {@link NullPointerException}.
	 * <p>
	 * Checks the response on the expected {@link HttpStatus#INTERNAL_SERVER_ERROR} and the expected
	 * {@link Rfc9457Error} response body.
	 */
	@Test
	@SneakyThrows
	@DisplayName("on an NPE THEN expect a response with status code 500 and an appropriate body")
	void testFurtherExceptionHandlerOnInternalFailAndExpect500() {
		when(controller.addPet(any(), any())).thenThrow(NullPointerException.class);

		mockMvc.perform(post(EndPointPrefix).contentType(MediaType.APPLICATION_JSON).content(validPetBodyWithTags))
				.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
						.isInstanceOf(NullPointerException.class))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON)).andExpect(content()
						.string(containsString("\"title\":\"Internal problem. Please contact the support.\"")));
	}

	/**
	 * Send something to the {@code POST} endpoint for creating a new {@link Pet} resource and make the controller throw
	 * a {@link DataIntegrityViolationException} with a message not containing {@code Unique}.
	 * <p>
	 * Checks the response on the expected {@link HttpStatus#INTERNAL_SERVER_ERROR} and the expected
	 * {@link Rfc9457Error} response body.
	 */
	@Test
	@SneakyThrows
	@DisplayName("on a Data Integrity Violation Exception THEN expect a response with status code 500 and an appropriate body")
	void testDataIntegrityExceptionHandlerOnInternalFailAndExpect500() {
		when(controller.addPet(any(), any())).thenThrow(new DataIntegrityViolationException(StringUtils.EMPTY));

		mockMvc.perform(post(EndPointPrefix).contentType(MediaType.APPLICATION_JSON).content(validPetBodyWithTags))
				.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
						.isInstanceOf(DataIntegrityViolationException.class))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON)).andExpect(content()
						.string(containsString("\"title\":\"Internal problem. Please contact the support.\"")));
	}
}
