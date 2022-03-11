package de.infoteam.functional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import de.infoteam.AbstractSpringTestRunner;
import de.infoteam.model.Pet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The integration tests of the {@code POST} endpoints for positive cases.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 0.2
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerPostTest extends AbstractSpringTestRunner {

	/**
	 * The only test so far: Sends a valid request to the endpoint for creating a new {@link Pet} resource.
	 */
	@Test
	@SneakyThrows
	@DisplayName("WHEN a valid request is sent to the Post endpoint for pet creation THEN the response status 501 is returned since the endpoint is not yet implemented")
	void testAddPetSuccessfullyAndExpect501() {
		mockMvc.perform(post(EndPointPrefix).contentType(MediaType.APPLICATION_JSON_VALUE).content(validPetBody))
				.andExpect(status().isNotImplemented());
	}
}
