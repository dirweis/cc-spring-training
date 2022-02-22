package de.infoteam.api;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import de.infoteam.AbstractSpringTestRunner;
import de.infoteam.model.Error;
import de.infoteam.model.Pet;
import lombok.SneakyThrows;

/**
 * The unit tests expecting a mocked bean for making the {@link PetsApiController} react on the specified behavior.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 0.1
 *
 */
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
	void testFurtherExceptionHandlerOnInternalFailAndExpect500() {
		when(controller.addPet(any())).thenThrow(NullPointerException.class);

		mockMvc.perform(post(EndPointPrefix).contentType(MediaType.APPLICATION_JSON_VALUE).content(validPetBody))
				.andExpect(status().isInternalServerError())
				.andExpect(header().string("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE))
				.andExpect(content()
						.string(containsString("\"title\":\"Internal problem. Please contact the support.\"")));
	}
}
