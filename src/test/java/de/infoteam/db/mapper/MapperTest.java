package de.infoteam.db.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import de.infoteam.AbstractSpringTestRunner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Some special tests for the Mapstruct {@link Mapper} implemented in {@link PetMapper}. For the sake of code coverage.
 * 
 * @since 2022-03-15
 * @version 0.6
 * @author Dirk Weissmann
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MapperTest extends AbstractSpringTestRunner {

	@Autowired
	private PetMapper mapper;

	/**
	 * Ensures the mapper's public methods convert {@code null} to {@code null}.
	 */
	@Test
	void testMapperOnNullValues() {
		assertThat(mapper.dto2Entity(null)).isNull();
		assertThat(mapper.entity2Dto(null)).isNull();
	}
}