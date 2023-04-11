package de.training.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import de.training.AbstractSpringTestRunner;
import de.training.db.dao.PetRepositoryDao;
import de.training.db.model.PetEntity;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The integration tests of the {@code POST} endpoints for positive cases as system tests. The tests store a new
 * {@link Pet} resource successfully and compare the response with the DB entry.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WebControllerPostTest extends AbstractSpringTestRunner {

    @Autowired
    private PetRepositoryDao petRepository;

    @AfterEach
    void cleanUp() {
        petRepository.deleteAll();
    }

    /**
     * Sends a valid request with a maximum example body to the endpoint for creating a new {@link Pet} resource.
     */
    @Test
    @SneakyThrows
    @DisplayName("WHEN a valid request with a body including tags is sent to the Post endpoint for pet creation THEN the response status 201 is expected and the database must contain the entry")
    void testAddPetMaxBodyAndExpect201() {
        final MvcResult result = mockMvc
                .perform(post(EndPointPrefix).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(validPetBodyWithTags))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(header().string("Location", startsWith("/petstore/petservice/v1/pets/"))).andReturn();

        final String locationHeader = result.getResponse().getHeader("Location");
        final UUID entityId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf('/') + 1));

        final PetEntity entity = petRepository.findById(entityId).get();

        assertThat(entity).isNotNull();
        assertThat(entity.getCategory()).isEqualTo(Category.CAT);
        assertThat(entity.getTags()).hasSize(3);
    }

    /**
     * Sends a valid request with a minimum example body to the endpoint for creating a new {@link Pet} resource.
     */
    @Test
    @SneakyThrows
    @DisplayName("WHEN a valid request with a body without tags is sent to the Post endpoint for pet creation THEN the response status 201 is expected and the database must contain the entry")
    void testAddPetMinBodyAndExpect201() {
        final MvcResult result = mockMvc
                .perform(
                        post(EndPointPrefix).contentType(MediaType.APPLICATION_JSON_VALUE).content(validMinimumPetBody))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(header().string("Location", startsWith("/petstore/petservice/v1/pets/"))).andReturn();

        final String locationHeader = result.getResponse().getHeader("Location");
        final UUID entityId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf('/') + 1));

        final PetEntity entity = petRepository.findById(entityId).get();

        assertThat(entity).isNotNull();
        assertThat(entity.getCategory()).isEqualTo(Category.CAT);
        assertThat(entity.getTags()).isEmpty();
    }
}
