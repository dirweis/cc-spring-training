package de.training.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ResourceUtils;

import de.training.AbstractSpringTestRunner;
import de.training.db.model.PetEntity;
import de.training.db.model.TagEntity;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The integration tests of the {@code PUT} endpoints for positive cases.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 0.2
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerPutTest extends AbstractSpringTestRunner {

	/**
	 * Tests for the "classic" {@code PUT} endpoint for overriding a {@link Pet} resource.
	 * 
	 * @author Dirk Weissmann
	 * @since 2022-02-22
	 * @version 1.0
	 *
	 */
	@Nested
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@DisplayName("WHEN a valid request is sent to the PUT endpoint for a pet that is ")
	class OverridePetResourceTest {

		@BeforeEach
		private void prepareDb() {
			petRepository.deleteAll();
		}

		@AfterEach
		private void cleanUpDb() {
			petRepository.deleteAll();
		}

		/**
		 * Sends a valid request to the endpoint for overriding a {@link Pet} resource with an unknown pet ID.
		 */
		@Test
		@SneakyThrows
		@DisplayName("not already stored in the database THEN the response with status 404 and an appropriate body is returned")
		void testUpdatePetNotFoundAndExpect404() {
			mockMvc.perform(
					put(EndPointWithTestId).contentType(MediaType.APPLICATION_JSON).content(validPetBodyWithTags))
					.andExpect(status().isNotFound())
					.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
					.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
							.isInstanceOf(EntityNotFoundException.class))
					.andExpect(content()
							.string(containsString("Resource with ID " + testId + " not found in the persistence")));
		}

		/**
		 * Sends a valid request to the endpoint for overriding a {@link Pet} resource with a known pet ID. Assures that
		 * the {@link Entity} in the database became overwritten. Includes tags for the update while the
		 * {@link PetEntity} on the database has no {@link TagEntity} objects.
		 */
		@Test
		@SneakyThrows
		@DisplayName("already stored in the database without tags THEN the response status 204 is returned")
		void testUpdatePetSuccessfullyWithTagsAndExpect204() {
			final PetEntity entity = createTestEntity(false);

			petRepository.save(entity);

			final UUID id = entity.getId();

			mockMvc.perform(put(EndPointPrefix + "/" + id).contentType(MediaType.APPLICATION_JSON)
					.content(validPetBodyWithTags)).andExpect(status().isNoContent());

			final Optional<PetEntity> newEntityOptional = petRepository.findById(id);

			assertThat(newEntityOptional).isPresent().isNotEmpty();

			final PetEntity newEntity = newEntityOptional.get();

			assertThat(newEntity.getCategory()).isEqualTo(Category.CAT);
			assertThat(newEntity.getTags()).hasSize(3);
		}

		/**
		 * Sends a valid request to the endpoint for overriding a {@link Pet} resource with a known pet ID. Assures that
		 * the {@link Entity} in the database became overwritten. The request body does not contain tags for the update
		 * while the {@link PetEntity} on the database has some {@link TagEntity} objects.
		 */
		@Test
		@SneakyThrows
		@DisplayName("already stored in the database with tags THEN the response status 204 is returned")
		void testUpdatePetSuccessfullyWithoutTagsAndExpect204() {
			final PetEntity entity = createTestEntity(true);

			petRepository.save(entity);

			final UUID id = entity.getId();

			mockMvc.perform(
					put(EndPointPrefix + "/" + id).contentType(MediaType.APPLICATION_JSON).content(validMinimumPetBody))
					.andExpect(status().isNoContent());

			final Optional<PetEntity> newEntityOptional = petRepository.findById(id);

			assertThat(newEntityOptional).isPresent().isNotEmpty();

			final PetEntity newEntity = newEntityOptional.get();

			assertThat(newEntity.getCategory()).isEqualTo(Category.CAT);
			assertThat(newEntity.getTags()).isEmpty();
		}
	}

	/**
	 * Tests for the {@code PUT} endpoint that adds an image to a specific {@link Pet} resource.
	 * 
	 * @author Dirk Weissmann
	 * @since 2022-02-22
	 * @version 0.1
	 *
	 */
	@Nested
	class AddImage2PetTest {

		/**
		 * The only test so far: Sends a valid request to the endpoint for adding an image to a {@link Pet} resource.
		 */
		@Test
		@SneakyThrows
		@DisplayName("WHEN a valid request is sent to the PUT endpoint for adding an image to a pet resource THEN the response status 501 is returned since the endpoint is not yet implemented")
		void testAddPetSuccessfullyAndExpect501() {
			final File contentFile = ResourceUtils.getFile("classpath:valid_test.jpg");
			final byte[] content = FileUtils.readFileToByteArray(contentFile);

			mockMvc.perform(put(EndPointImageTestId).contentType(MediaType.IMAGE_JPEG_VALUE).content(content))
					.andExpect(status().isNotImplemented());
		}
	}
}
