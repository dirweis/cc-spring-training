package de.infoteam.db.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.infoteam.db.model.PetEntity;
import de.infoteam.db.model.TagEntity;
import de.infoteam.model.Pet;

/**
 * The {@link Mapper} for mapping {@link Pet} DTOs into {@link PetEntity} objects and backwards, including the
 * transformation of tag {@link String}s into complex {@link TagEntity} objects.
 * 
 * @since 2022-03-15
 * @version 0.2
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
}
