package de.infoteam.functional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.infoteam.AbstractSpringTestRunner;
import de.infoteam.model.Pet;
import lombok.SneakyThrows;

/**
 * The integration tests of the {@code DELETE} endpoints for positive cases.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 0.1
 *
 */
class WebControllerDeleteTest extends AbstractSpringTestRunner {

	/**
	 * The only test here so far: Calls the {@code DELETE} endpoint for deleting a {@link Pet} resource and expects a
	 * success response.
	 */
	@Test
	@SneakyThrows
	@DisplayName("WHEN a valid UUID for a pet resource is given to the DELETE enpoint THEN respond with status 501 gets returned since the endpoint is not yet implemented")
	void testDeletePetSuccessfullyAndExpect501() {
		mockMvc.perform(delete(EndPointWithTestId)).andExpect(status().isNotImplemented());
	}
}
