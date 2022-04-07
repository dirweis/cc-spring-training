package de.training.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import de.training.AbstractSpringTestRunner;
import de.training.model.Error;
import de.training.model.Pet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The unit tests expecting a mocked bean for making the {@link PetsApiController} react on the specified behavior.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 0.4
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MockedControllerTest extends AbstractSpringTestRunner {

	@MockBean
	private PetsApiController controller;

	/**
	 * Test for the internal server problems: Send something to the {@code POST} endpoint for creating a new {@link Pet}
	 * resource and make the controller throw a {@link NullPointerException}.
	 * <p>
	 * Checks the response on the expected {@link HttpStatus#INTERNAL_SERVER_ERROR} and the expected {@link Error}
	 * response body.
	 */
	@Test
	@SneakyThrows
	@DisplayName("WHEN the Post endpoint for adding a new Pet resource is called an the implementation crashes internally on an exception THEN expect a response with status code 500 and an appropriate body")
	void testFurtherExceptionHandlerOnInternalFailAndExpect500() {
		when(controller.addPet(any(), any())).thenThrow(NullPointerException.class);

		mockMvc.perform(post(EndPointPrefix).contentType(MediaType.APPLICATION_JSON).content(validPetBodyWithTags))
				.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
						.isInstanceOf(NullPointerException.class))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON)).andExpect(content()
						.string(containsString("\"title\":\"Internal problem. Please contact the support.\"")));
	}
}
