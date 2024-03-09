package de.training.db.service;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.training.db.dao.PetRepositoryDao;
import de.training.db.mapper.PetMapper;
import de.training.db.model.PetEntity;
import de.training.db.model.TagEntity;
import de.training.model.Pet;
import lombok.RequiredArgsConstructor;

/**
 * A {@link Service} bean for database operations, using a {@link Mapper} and a {@link JpaRepository}.
 * 
 * @since 2022-03-15
 * @version 0.4
 * @author Dirk Weissmann
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {

    private final PetRepositoryDao petRepository;

    private final PetMapper mapper;

    /**
     * Stores a new {@link Pet} resource into the database. The {@link PetMapper} is used for mapping the {@link Pet} to
     * a {@link PetEntity}. Then the {@link PetRepositoryDao} is used for the storing itself.
     * 
     * @param petDto the {@link Pet} resources to be stored
     * 
     * @return the stored entity's ID
     */
    public UUID storeNewPet(final Pet petDto) {
        final PetEntity entity = mapper.dto2Entity(petDto);

        linkTags(entity);

        petRepository.save(entity);

        return entity.getId();
    }

    /**
     * The crazy JPA "feature": Even though the {@link TagEntity} objects are already allocated to the
     * {@link PetEntity}, it must also be done the other way.
     * 
     * @param petEntity the {@link PetEntity} for allocation
     */
    private static void linkTags(final PetEntity petEntity) {
        petEntity.getTags().forEach(tagEntity -> tagEntity.setPet(petEntity));
    }
}
