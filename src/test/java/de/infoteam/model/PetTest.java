package de.infoteam.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.infoteam.model.Pet.PetStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The tests (especially in respect to code coverage) for the {@link Pet} resource that are not covered from integration
 * tests.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 1.1
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PetTest {

	/**
	 * The only test here so far: Creates a new {@link Pet} and checks for the values.
	 */
	@Test
	void testPetFields() {
		final Pet testPet = new Pet(UUID.fromString("6f09a3c7-fdec-4949-9da5-d089f9ccb378"), null, "name", null, null,
				PetStatus.AVAILABLE, "description");

		assertThat(testPet.id()).isExactlyInstanceOf(UUID.class);
		assertThat(testPet.id()).isEqualTo(UUID.fromString("6f09a3c7-fdec-4949-9da5-d089f9ccb378"));
		assertThat(testPet.name()).isEqualTo("name");
		assertThat(testPet.category()).isNull();
		assertThat(testPet.photoUrls()).isNull();
		assertThat(testPet.tags()).isNull();
		assertThat(testPet.status()).isEqualTo(PetStatus.AVAILABLE);
		assertThat(testPet.description()).isEqualTo("description");
		assertThat(testPet).hasToString(
				"Pet[id=6f09a3c7-fdec-4949-9da5-d089f9ccb378, category=null, name=name, photoUrls=null, tags=null, status=AVAILABLE, description=description]");
	}
}
