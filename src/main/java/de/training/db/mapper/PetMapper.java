package de.training.db.mapper;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import de.training.db.model.PetEntity;
import de.training.db.model.PhotoUrlEntity;
import de.training.db.model.TagEntity;
import de.training.model.Pet;

/**
 * The {@link Mapper} for mapping {@link Pet} DTOs into {@link PetEntity} objects and backwards, including the
 * transformation of tag {@link String}s into complex {@link TagEntity} objects.
 * 
 * @since 2022-03-15
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
@Mapper(componentModel = "spring")
public interface PetMapper {

    /**
     * Maps a {@link Pet} DTO into a {@link PetEntity}.
     * 
     * @param pet the input DTO
     * 
     * @return the mapped entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoUrls", ignore = true)
    @Mapping(target = "tags", expression = "java(mapTags2Entities(pet.tags()))")
    PetEntity dto2Entity(Pet pet);

    /**
     * Maps a {@link PetEntity} DTO into a {@link Pet} DTO.
     * 
     * @param petEntity the input entity
     * 
     * @return the mapped entity
     */
    @Mapping(target = "photoUrls", expression = "java(mapPhotoEntities2PhotoUrls(petEntity.getPhotoUrls()))")
    @Mapping(target = "tags", expression = "java(mapTagEntities2Tags(petEntity.getTags()))")
    Pet entity2Dto(PetEntity petEntity);

    /**
     * Maps a {@link PetEntity} to another for updating the entity on the database.
     * 
     * @param targetEntity the {@link PetEntity} to be updated, may be {@code null}
     * @param updateDto    the new {@link Pet} as source
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoUrls", ignore = true)
    @Mapping(target = "tags", expression = "java(new java.util.ArrayList<>(mapTags2Entities(updateDto.tags())))")
    void updatePetEntity(@MappingTarget PetEntity targetEntity, Pet updateDto);

    /**
     * A little helper for mapping the tags of a {@link Pet} resource into a complex {@link TagEntity}.
     * 
     * @param tags the {@link Pet}'s tags, may be {@code null}
     * 
     * @return the {@link List} of mapped {@link TagEntity} objects
     */
    default List<TagEntity> mapTags2Entities(final Collection<String> tags) {
        if (null == tags) {
            return Collections.emptyList();
        }

        return tags.stream().map(TagEntity::new).toList();
    }

    /**
     * A little helper for mapping the {@link TagEntity} objects of a {@link PetEntity} into a {@link List} of tags as
     * {@link String}s.
     * 
     * @param tagEntities the {@link PetEntity}'s tags, must not be {@code null}
     * 
     * @return the {@link List} of mapped tags
     */
    default List<String> mapTagEntities2Tags(final Collection<TagEntity> tagEntities) {
        return tagEntities.stream().map(TagEntity::getTag).toList();
    }

    /**
     * A little helper for mapping the {@link PhotoUrlEntity} objects of a {@link PetEntity} into a {@link List} of tags
     * as {@link URL}s.
     * 
     * @param photoUrlEntities the {@link PetEntity}'s photo URLs, must not be {@code null}
     * 
     * @return the {@link List} of mapped photo URLs
     */
    default List<URL> mapPhotoEntities2PhotoUrls(final Collection<PhotoUrlEntity> photoUrlEntities) {
        return photoUrlEntities.stream().map(PhotoUrlEntity::getUrl).toList();
    }
}
