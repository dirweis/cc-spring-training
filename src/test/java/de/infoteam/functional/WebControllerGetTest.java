package de.infoteam.functional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.infoteam.AbstractSpringTestRunner;
import de.infoteam.model.Pet;
import de.infoteam.model.Pet.PetStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The integration tests of the {@code GET} endpoints for positive cases.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 0.2
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerGetTest extends AbstractSpringTestRunner {

	/**
	 * Tests the endpoint that returns a {@link List} of resources found by query parameters.
	 * 
	 * @author Dirk Weissmann
	 * @since 2022-02-22
	 * @version 0.1
	 *
	 */
	@Nested
	class FindByParametersTest {

		/**
		 * Sends a valid request to the endpoint using the {@link PetStatus} parameter for filtering the result set.
		 */
		@Test
		@SneakyThrows
		@DisplayName("WHEN a valid request is sent to the GET endpoint for retrieving several pet resources restricted by their status THEN the response status 501 is returned since the endpoint is not yet implemented")
		void testFindPetsRestrictedByStatusSuccessfullyAndExpect501() {
			mockMvc.perform(get(EndPointPrefix + "?status=available")).andExpect(status().isNotImplemented());
		}

		/**
		 * Sends a valid request to the endpoint using the {@link Pet#tags() tags} parameter for filtering the result
		 * set.
		 */
		@Test
		@SneakyThrows
		@DisplayName("WHEN a valid request is sent to the GET endpoint for retrieving several pet resources restricted by the given tags THEN the response status 501 is returned since the endpoint is not yet implemented")
		void testFindPetsRestrictedByTagsSuccessfullyAndExpect501() {
			mockMvc.perform(get(EndPointPrefix + "?tags=nice,lovely")).andExpect(status().isNotImplemented());
		}

		/**
		 * Sends a valid request to the endpoint using the pageable parameters {@code page} and {@code size} for
		 * filtering the result set.
		 */
		@Test
		@SneakyThrows
		@DisplayName("WHEN a valid request is sent to the GET endpoint for retrieving several pet resources restricted by page and size THEN the response status 501 is returned since the endpoint is not yet implemented")
		void testFindPetsRestrictedByPageAndSizeSuccessfullyAndExpect501() {
			mockMvc.perform(get(EndPointPrefix + "?page=1&size=100")).andExpect(status().isNotImplemented());
		}
	}

	/**
	 * Tests the endpoint that returns a {@link Pet} resource found by its ID.
	 * 
	 * @author Dirk Weissmann
	 * @since 2022-02-22
	 * @version 0.1
	 *
	 */
	@Nested
	class GetByIdTest {

		/**
		 * Sends a valid request to the endpoint.
		 */
		@Test
		@SneakyThrows
		@DisplayName("WHEN a valid request is sent to the GET endpoint for retrieving a pet resource by its ID THEN the response status 501 is returned since the endpoint is not yet implemented")
		void testGetPetByIdSuccessfullyAndExpect501() {
			mockMvc.perform(get(EndPointWithTestId)).andExpect(status().isNotImplemented());
		}
	}
}
