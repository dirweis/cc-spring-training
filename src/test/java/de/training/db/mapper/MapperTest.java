package de.training.db.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import de.training.AbstractSpringTestRunner;
import de.training.db.model.PetEntity;
import de.training.db.model.TagEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Some special tests for the Mapstruct {@link Mapper} implemented in {@link PetMapper}. For the sake of code coverage.
 * 
 * @since 2022-03-15
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MapperTest extends AbstractSpringTestRunner {

    @Autowired
    private PetMapper mapper;

    /**
     * Ensures the mapper's correct behavior with {@code null} values.
     */
    @Test
    void testMapperOnNullValues() {
        assertThat(mapper.dto2Entity(null)).isNull();
        assertThat(mapper.entity2Dto(null)).isNull();
        assertThat(mapper.update(null, null)).isNull();

        final PetEntity entityWithTag = createTestEntity(true);

        assertThat(mapper.update(entityWithTag, null)).isEqualTo(entityWithTag);

        assertThatNullPointerException().isThrownBy(() -> mapper.update(null, entityWithTag));

        final PetEntity targetEntity = createTestEntity(false);
        final PetEntity sourceEntity = createTestEntity(false);

        mapper.update(targetEntity, sourceEntity);

        assertThat(targetEntity.getTags()).isNull();
    }

    /**
     * Ensures the transfer of {@link TagEntity} objects in the {@link PetEntity} in the update method.
     */
    @Test
    void testMapperUpdateAddTags() {
        final PetEntity targetEntity = createTestEntity(false);
        final PetEntity sourceEntity = createTestEntity(true);

        mapper.update(targetEntity, sourceEntity);

        assertThat(targetEntity.getTags().size()).isOne();
    }

    /**
     * Ensures the deletion of {@link TagEntity} objects in the target {@link PetEntity} in case the target entity
     * contains some and the source entity doesn't.
     */
    @Test
    void testMapperUpdateRemoveTags() {
        final PetEntity targetEntity = createTestEntity(true);
        final PetEntity sourceEntity = createTestEntity(false);

        mapper.update(targetEntity, sourceEntity);

        assertThat(targetEntity.getTags()).isNull();
    }
}