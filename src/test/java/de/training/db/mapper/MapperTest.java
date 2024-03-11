package de.training.db.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Some special tests for the Mapstruct {@link Mapper} implemented in {@link PetMapper}. For the sake of code coverage.
 * 
 * @since 2022-03-15
 * @version 0.7
 * @author Dirk Weissmann
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MapperTest {

    private final PetMapper mapper = new PetMapperImpl();

    /**
     * Ensures the mapper's public methods convert {@code null} to {@code null}.
     */
    @Test
    @DisplayName("WHEN null is given to the mapper's methods THEN null is expected")
    void testMapperOnNullValues() {
        assertThat(mapper.dto2Entity(null)).isNull();
        assertThat(mapper.entity2Dto(null)).isNull();
    }
}