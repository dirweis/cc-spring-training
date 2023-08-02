package de.training.db.model;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.training.AbstractSpringTestRunner;
import de.training.db.dao.PetRepositoryDao;
import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Supplementary test on the {@link PhotoUrlEntity}.
 * 
 * @since 2022-03-15
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PetEntityTest extends AbstractSpringTestRunner {

    @Autowired
    private PetRepositoryDao petRepository;

    @AfterEach
    void cleanUp() {
        petRepository.deleteAll();
    }

    /**
     * Tests the getters for all fields. Most of these assertions will become obsolete shortly (when the behavior tests
     * for a GET endpoint is implemented). But this test is OK anyways.
     */
    @Test
    void testAllGetters() {
        final PetEntity entity = new PetEntity();

        entity.setCategory(Category.BIRD);
        entity.setDescription("This is a wonderful description of a bird. Maybe a parrot.");
        entity.setName("Bob the Bird");
        entity.setStatus(PetStatus.AVAILABLE);

        petRepository.save(entity);

        Assertions.assertThat(entity.getId()).isInstanceOf(UUID.class);
        Assertions.assertThat(entity.getCategory()).isEqualTo(Category.BIRD);
        Assertions.assertThat(entity.getDescription()).isNotEmpty();
        Assertions.assertThat(entity.getName()).startsWith("Bob");
        Assertions.assertThat(entity.getStatus()).isEqualTo(PetStatus.AVAILABLE);
        Assertions.assertThat(entity.getTags()).isNull();
        Assertions.assertThat(entity.getPhotoUrls()).isNull();

        Assertions.assertThat(entity.getCreatedTime()).isEqualTo(entity.getModifiedTime());
    }
}
