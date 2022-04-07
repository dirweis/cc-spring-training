package de.training.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import de.training.AbstractSpringTestRunner;
import de.training.db.model.PetEntity;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The integration tests of the {@code GET} endpoints for positive cases.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerGetTest extends AbstractSpringTestRunner {

	@BeforeEach
	private void prepareTests() {
		petRepository.deleteAll();
	}

	@AfterEach
	private void cleanUp() {
		petRepository.deleteAll();
	}

	/**
	 * Tests the endpoint that returns a {@link List} of resources found by query parameters.
	 * 
	 * @author Dirk Weissmann
	 * @since 2022-02-22
	 * @version 1.1
	 *
	 */
	@Nested
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@DisplayName("WHEN")
	class FindByParametersTest {

		/**
		 * Sends a valid request to the endpoint and expects the {@code 200} response code.
		 */
		@Test
		@SneakyThrows
		@DisplayName("a valid request is sent to the GET endpoint for retrieving several pet resources THEN the response status 200 is returned without content")
		void testFindPetsWithoutRestrictionsSuccessfullyAndExpect200WithoutContent() {
			mockMvc.perform(get(EndPointPrefix)).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().json("[]"));
		}

		/**
		 * Sends a valid request to the endpoint and expects the {@code 200} response code. Checks the default values
		 * for the query parameters {@code page} and {@code size}. With 100 entries and the default values
		 * <code>page: 0, size:20</code> 20 items must be returned.
		 */
		@Test
		@SneakyThrows
		@DisplayName("test data is inserted in the database AND a valid request is sent to the GET endpoint for retrieving several pet resources THEN the response status 200 is returned with 20 items in the response body")
		void testFindPetsWithoutRestrictionsSuccessfullyAndExpect200With20Entries() {
			for (int i = 0; i < 100; i++) {
				petRepository.save(createTestEntity(true));
			}

			mockMvc.perform(get(EndPointPrefix)).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(20)));
		}

		/**
		 * Sends a valid request to the endpoint and expects the {@code 200} response code. Checks the default values
		 * for the query parameters {@code page} and {@code size}. With 10 entries and the default values
		 * <code>page: 0, size:20</code> 10 items must be returned.
		 */
		@Test
		@SneakyThrows
		@DisplayName("test data is inserted in the database AND a valid request is sent to the GET endpoint for retrieving several pet resources THEN the response status 200 is returned with 10 items in the response body")
		void testFindPetsWithoutRestrictionsSuccessfullyAndExpect200With10Entries() {
			for (int i = 0; i < 10; i++) {
				petRepository.save(createTestEntity(true));
			}

			mockMvc.perform(get(EndPointPrefix)).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(10)));
		}

		/**
		 * Sends a valid request to the endpoint using the {@link PetStatus} parameter for filtering the result set.
		 * Since 100 times the status {@link PetStatus#AVAILABLE} is present in the database, the returned items will be
		 * 100.
		 */
		@Test
		@SneakyThrows
		@DisplayName("a valid request is sent to the GET endpoint for retrieving several pet resources restricted by their status THEN the response status 200 is returned with 100 items in the response body")
		void testFindPetsRestrictedByStatusSuccessfullyAndExpect200WithAllItems() {
			for (int i = 0; i < 100; i++) {
				petRepository.save(createTestEntity(true));
			}

			mockMvc.perform(get(EndPointPrefix + "?status=pending&size=100")).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$", hasSize(100)));
		}

		/**
		 * Sends a valid request to the endpoint using the {@link PetStatus} parameter for filtering the result set.
		 * Since 100 times the status {@link PetStatus#AVAILABLE} is present in the database but the requested status is
		 * not found, no items will be returned.
		 */
		@Test
		@SneakyThrows
		@DisplayName("a valid request is sent to the GET endpoint for retrieving several pet resources restricted by their status THEN the response status 200 is returned without items in the response body")
		void testFindPetsRestrictedByStatusSuccessfullyAndExpect200WithNoItems() {
			for (int i = 0; i < 100; i++) {
				petRepository.save(createTestEntity(true));
			}

			mockMvc.perform(get(EndPointPrefix + "?status=sold&size=100")).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", empty()));
		}

		/**
		 * Sends a valid request to the endpoint using the {@link Category} parameter for filtering the result set.
		 * Since 100 times the status {@link Category#BIRD} is present in the database, the returned items will be 100.
		 */
		@Test
		@SneakyThrows
		@DisplayName("a valid request is sent to the GET endpoint for retrieving several pet resources restricted by their category THEN the response status 200 is returned with 100 items in the response body")
		void testFindPetsRestrictedByCategorySuccessfullyAndExpect200WithAllItems() {
			for (int i = 0; i < 100; i++) {
				petRepository.save(createTestEntity(true));
			}

			mockMvc.perform(get(EndPointPrefix + "?category=spider&size=100")).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$", hasSize(100)));
		}

		/**
		 * Sends a valid request to the endpoint using the {@link Category} parameter for filtering the result set.
		 * Since 100 times the status {@link Category#CAT} is present in the database but the requested status is not
		 * found, no items will be returned.
		 */
		@Test
		@SneakyThrows
		@DisplayName("a valid request is sent to the GET endpoint for retrieving several pet resources restricted by their category THEN the response status 200 is returned without items in the response body")
		void testFindPetsRestrictedByCategorySuccessfullyAndExpect200WithNoItems() {
			for (int i = 0; i < 100; i++) {
				petRepository.save(createTestEntity(true));
			}

			mockMvc.perform(get(EndPointPrefix + "?category=cat&size=100")).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", empty()));
		}

		/**
		 * Sends a valid request to the endpoint using the {@link Pet#tags() tags} parameter for filtering the result
		 * set. Returns an empty body since no data is available in the database.
		 */
		@Test
		@SneakyThrows
		@DisplayName("a valid request is sent to the GET endpoint for retrieving several pet resources restricted by the given tags THEN the response status 200 is returned without data")
		void testFindPetsRestrictedByTagsSuccessfullyAndExpect200WithoutItems() {
			mockMvc.perform(get(EndPointPrefix + "?tags=nice,lovely")).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().json("[]"));
		}

		/**
		 * Sends a valid request to the endpoint using the {@link Pet#tags() tags} parameter for filtering the result
		 * set. Returns 50 items in the response with 100 entries in the database since half of them are tagged with one
		 * of the given tags.
		 */
		@Test
		@SneakyThrows
		@DisplayName("the database has a set of 50 tageed entries AND a valid request is sent to the GET endpoint for retrieving several pet resources restricted by the given tags THEN the response status 200 is returned with the data")
		void testFindPetsRestrictedByTagsSuccessfullyAndExpect200With50Items() {
			for (int i = 0; i < 100; i++) {
				petRepository.save(createTestEntity(i % 2 == 0));
			}

			mockMvc.perform(get(EndPointPrefix + "?tags=subba,lovely&size=100")).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(50)));
		}

		/**
		 * Sends a valid request to the endpoint using the pageable parameter {@code page} for filtering the result set.
		 * Since 30 entries are stored in the database and the second page with max. 20 entries is selected, 10 entries
		 * are expected.
		 */
		@Test
		@SneakyThrows
		@DisplayName("a valid request is sent to the GET endpoint for retrieving several pet resources restricted by page and size THEN the response status 200 is returned")
		void testFindPetsRestrictedByPageAndSizeSuccessfullyAndExpect200() {
			for (int i = 0; i < 30; i++) {
				petRepository.save(createTestEntity(true));
			}

			mockMvc.perform(get(EndPointPrefix + "?page=1")).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(10)));
		}
	}

	/**
	 * Tests the endpoint that returns a {@link Pet} resource found by its ID.
	 * 
	 * @author Dirk Weissmann
	 * @since 2022-02-22
	 * @version 1.0
	 *
	 */
	@Nested
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	class GetByIdTest {

		/**
		 * Sends a valid request to the endpoint with an ID that is unknown and responds with code {@code 404}.
		 */
		@Test
		@SneakyThrows
		@DisplayName("WHEN a valid request is sent to the GET endpoint for retrieving a pet resource by its ID which is not found THEN the response status 404 is returned")
		void testGetPetByNotFoundIdAndExpect404() {
			mockMvc.perform(get(EndPointWithTestId))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(EntityNotFoundException.class))
					.andExpect(status().isNotFound())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
					.andExpect(content().string(containsString(
							"\"detail\":\"Resource with ID " + testId + " not found in the persistence")));
		}

		/**
		 * Sends a valid request to the endpoint with an ID that is known and responds with code {@code 200} and checks
		 * for the tags.
		 */
		@Test
		@SneakyThrows
		@DisplayName("WHEN a valid request is sent to the GET endpoint for retrieving a pet resource with tags by its ID which is found THEN the response status 200 is returned")
		void testGetPetByFoundWithTagsIdAndExpect200() {
			final PetEntity testEntity = createTestEntity(true);

			petRepository.save(testEntity);

			mockMvc.perform(get(EndPointPrefix + "/" + testEntity.getId())).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(content().string(containsString("\"name\":\"Peter")))
					.andExpect(content().string(containsString("\"tags\":[\"subba")));
		}

		/**
		 * Sends a valid request to the endpoint with an ID that is known and responds with code {@code 200} and checks
		 * for missing tags.
		 */
		@Test
		@SneakyThrows
		@DisplayName("WHEN a valid request is sent to the GET endpoint for retrieving a pet resource without tags by its ID which is found THEN the response status 200 is returned")
		void testGetPetByFoundWithoutTagsIdAndExpect200() {
			final PetEntity testEntity = createTestEntity(false);

			petRepository.save(testEntity);

			mockMvc.perform(get(EndPointPrefix + "/" + testEntity.getId())).andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(content().string(containsString("\"name\":\"Peter")))
					.andExpect(content().string(containsString("\"tags\":[]")));
		}
	}
}
