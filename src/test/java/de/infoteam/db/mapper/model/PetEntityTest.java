package de.infoteam.db.mapper.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.infoteam.AbstractSpringTestRunner;
import de.infoteam.db.dao.PetRepositoryDao;
import de.infoteam.db.model.PetEntity;
import de.infoteam.db.model.PhotoUrlEntity;
import de.infoteam.model.Pet.Category;
import de.infoteam.model.Pet.PetStatus;
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
	private void cleanUp() {
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

		assertThat(entity.getId()).isInstanceOf(UUID.class);
		assertThat(entity.getCategory()).isEqualTo(Category.BIRD);
		assertThat(entity.getDescription()).isNotEmpty();
		assertThat(entity.getName()).startsWith("Bob");
		assertThat(entity.getStatus()).isEqualTo(PetStatus.AVAILABLE);
		assertThat(entity.getTags()).isNull();
		assertThat(entity.getPhotoUrls()).isNull();

		assertThat(entity.getCreatedTime()).isEqualTo(entity.getModifiedTime());
	}
}
