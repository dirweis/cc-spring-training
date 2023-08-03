package de.training.db.service;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.training.db.dao.PetRepositoryDao;
import de.training.db.mapper.PetMapper;
import de.training.db.model.PetEntity;
import de.training.db.model.TagEntity;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

/**
 * A {@link Service} bean for database operations, using a {@link Mapper} and a {@link JpaRepository}.
 * 
 * @since 2022-03-15
 * @version 0.6
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

        StoreService.linkTags(entity);

        petRepository.save(entity);

        return entity.getId();
    }

    /**
     * Selects a {@link PetEntity} database entry by its ID and maps it into a {@link Pet} DTO. In case of a not found
     * ID, an {@link EntityNotFoundException} is thrown and handled in the {@link ExceptionHandler}.
     * 
     * @param petId the {@link PetEntity}'s ID, may be {@code null}
     * 
     * @return the mapped DTO
     */
    @Transactional(readOnly = true)
    public Pet getPetById(final UUID petId) {
        final PetEntity entity = petRepository.findById(petId).orElseThrow(
                () -> new EntityNotFoundException("Resource with ID " + petId + " not found in the persistence"));

        return mapper.entity2Dto(entity);
    }

    /**
     * Retrieves a result set of {@link PetEntity} objects by the given parameters.
     * 
     * @param status   the {@link PetStatus} as first {@code WHERE} parameter, may be {@code null}
     * @param tags     the {@link List} of tags as second {@code WHERE} parameter, may be {@code null}
     * @param category the {@link Category} of the {@link Pet}, may be {@code null}
     * @param pageable the paging parameter, may be {@code null}
     * 
     * @return the {@link List} of {@link Pet} DTOs after mapping the DB result
     */
    @Transactional(readOnly = true)
    public List<Pet> findByParameters(final List<String> tags, final PetStatus status, final Category category,
            final Pageable pageable) {

        final List<PetEntity> entities = petRepository
                .findAll(StoreService.hasStatus(status)
                        .and(StoreService.hasCategory(category).and(StoreService.isInTags(tags))), pageable)
                .getContent();

        return entities.stream().map(mapper::entity2Dto).toList();
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

    /**
     * The {@link Specification} for enriching the {@code WHERE} statement with a check on the {@link PetStatus}.
     * 
     * @param status the {@link PetStatus}, may be {@code null}
     * 
     * @return the {@link Specification} for restrictions or {@code null} in case it's not needed
     */
    private static Specification<PetEntity> hasStatus(final PetStatus status) {
        return (final Root<PetEntity> root, final CriteriaQuery<?> cq,
                final CriteriaBuilder cb) -> status != null ? cb.equal(root.get("status"), status) : null;
    }

    /**
     * The {@link Specification} for enriching the {@code WHERE} statement with a check on the {@link Category}.
     * 
     * @param category the {@link Pet}'s {@link Category}, may be {@code null}
     * 
     * @return the {@link Specification} for restrictions or {@code null} in case it's not needed
     */
    private static Specification<PetEntity> hasCategory(final Category category) {
        return (final Root<PetEntity> root, final CriteriaQuery<?> cq,
                final CriteriaBuilder cb) -> category != null ? cb.equal(root.get("category"), category) : null;
    }

    /**
     * The {@link Specification} for enriching the {@code WHERE} statement with a check on given tags..
     * 
     * @param tags the {@link List} of tag {@link String}s, may be {@code null}
     * 
     * @return the {@link Specification} for restrictions or {@code null} in case it's not needed
     */
    private static Specification<PetEntity> isInTags(final List<String> tags) {
        return (final Root<PetEntity> root, final CriteriaQuery<?> cq,
                final CriteriaBuilder cb) -> tags != null ? cb.in(root.join("tags").get("tag")).value(tags) : null;
    }
}
