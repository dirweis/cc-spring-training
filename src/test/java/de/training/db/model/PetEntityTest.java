package de.training.db.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
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
    PetRepositoryDao petRepository;

    @AfterEach
    void cleanUp() {
        petRepository.deleteAll();
    }

    /**
     * Tests the automatically created time stamps.
     */
    @Test
    @DisplayName("WHEN a new entry is created THEN the 'created' and the 'modified' time stamps must equal")
    void testTimestampCreation() {
        final PetEntity entity = new PetEntity();

        entity.setCategory(Category.BIRD);
        entity.setDescription("This is a wonderful description of a bird. Maybe a parrot.");
        entity.setName("Bob the Bird");
        entity.setStatus(PetStatus.AVAILABLE);

        petRepository.save(entity);

        assertThat(entity.getCreatedTime()).isEqualTo(entity.getModifiedTime());
    }
}
