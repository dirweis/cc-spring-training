package de.training.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import de.training.AbstractSpringTestRunner;
import de.training.db.model.PetEntity;
import de.training.model.Pet;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The integration tests of the {@code DELETE} endpoints for positive cases.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@DisplayName("WHEN a valid ID for a pet resource")
class WebControllerDeleteTest extends AbstractSpringTestRunner {

    @AfterEach
    void cleanUp() {
        petRepository.deleteAll();
    }

    /**
     * Calls the {@code DELETE} endpoint for deleting a {@link Pet} resource that doesn't exist and expects a response
     * with code {@code 404}.
     */
    @Test
    @SneakyThrows
    @DisplayName("that is not stored yet is given to the DELETE enpoint THEN respond with status 404 and the appropriate body")
    void testDeletePetNotFoundAndExpect404() {
        mockMvc.perform(delete(END_POINT_WITH_TEST_ID))
                .andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
                        .isInstanceOf(EntityNotFoundException.class))
                .andExpect(status().isNotFound()).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(
                        containsString("\"detail\":\"Resource with ID " + TEST_ID + " not found in the persistence\"")));
    }

    /**
     * Calls the {@code DELETE} endpoint for deleting a {@link Pet} resource and expects a response with code
     * {@code 204}.
     */
    @Test
    @SneakyThrows
    @DisplayName("is given to the DELETE enpoint THEN respond with status 204")
    void testDeletePetFoundAndExpect204() {

        final PetEntity entity = createTestEntity(true);

        petRepository.save(entity);

        final UUID entityId = entity.getId();

        mockMvc.perform(delete(END_POINT_PREFIX + "/" + entityId)).andExpect(status().isNoContent());

        assertThat(petRepository.findById(entityId)).isNotPresent();
    }
}
