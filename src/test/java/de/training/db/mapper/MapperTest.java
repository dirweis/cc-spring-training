package de.training.db.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;

import de.training.db.model.PetEntity;
import de.training.db.model.TagEntity;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
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
class MapperTest {

    private static final PetMapper sut = new PetMapperImpl();

    /**
     * Ensures the mapper's correct behavior with {@code null} values.
     */
    @Test
    void testMapperOnNullValues() {
        assertThat(sut.dto2Entity(null)).isNull();
        assertThat(sut.entity2Dto(null)).isNull();

        final PetEntity nullEntity = null;

        sut.updatePetEntity(nullEntity, null);

        assertThat(nullEntity).isNull();

        final Pet petWithoutTags = new Pet(null, Category.BIRD, "Wuffi", null, null, PetStatus.AVAILABLE,
                "I'm a description");

        assertThatNullPointerException().isThrownBy(() -> sut.updatePetEntity(null, petWithoutTags));

        final PetEntity targetEntity = createTestEntity(false);

        sut.updatePetEntity(targetEntity, petWithoutTags);

        assertThat(targetEntity.getTags()).isEmpty();
    }

    /**
     * Ensures the transfer of {@link TagEntity} objects in the {@link PetEntity} in the update method.
     */
    @Test
    void testMapperUpdateAddTags() {
        final PetEntity targetEntity = createTestEntity(false);
        final Pet source = new Pet(null, Category.BIRD, "Wuffi", null, List.of("Lovely"), PetStatus.AVAILABLE,
                "I'm a description");

        sut.updatePetEntity(targetEntity, source);

        assertThat(targetEntity.getTags().size()).isOne();
    }

    /**
     * Ensures the deletion of {@link TagEntity} objects in the target {@link PetEntity} in case the target entity
     * contains some and the source entity doesn't.
     */
    @Test
    void testMapperUpdateRemoveTags() {
        final PetEntity targetEntity = createTestEntity(true);
        final Pet source = new Pet(null, Category.BIRD, "Wuffi", null, null, PetStatus.AVAILABLE, "I'm a description");

        sut.updatePetEntity(targetEntity, source);

        assertThat(targetEntity.getTags()).isEmpty();
    }

    private static PetEntity createTestEntity(final boolean withTags) {
        final PetEntity entity = new PetEntity();

        entity.setCategory(Category.SPIDER);
        entity.setDescription(
                "What?? You want me to be a representative description for a what?! A SPIDER?!? You must be kidding!");
        entity.setName("Peter Parker");
        entity.setStatus(PetStatus.PENDING);

        if (withTags) {
            final TagEntity tagEntity = new TagEntity("subba");

            tagEntity.setPet(entity);
            entity.setTags(List.of(tagEntity));
        }

        return entity;
    }
}