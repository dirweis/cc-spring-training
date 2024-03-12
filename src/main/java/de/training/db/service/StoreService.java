package de.training.db.service;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.training.configuration.MinioConfigDto;
import de.training.db.dao.PetRepositoryDao;
import de.training.db.dao.PhotoUrlRepositoryDao;
import de.training.db.dao.TagRepositoryDao;
import de.training.db.mapper.PetMapper;
import de.training.db.model.PetEntity;
import de.training.db.model.PhotoUrlEntity;
import de.training.db.model.TagEntity;
import de.training.imagestore.MinioService;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * A {@link Service} bean for database operations, using a {@link Mapper} and a {@link JpaRepository}.
 * 
 * @since 2022-03-15
 * @version 1.1
 * @author Dirk Weissmann
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {

    private static final String ERROR_MSG_FORMAT = "Resource with ID %s not found in the persistence";

    private final PetRepositoryDao petRepository;
    private final TagRepositoryDao tagRepository;
    private final PhotoUrlRepositoryDao photoUrlRepository;

    private final PetMapper mapper;

    private final MinioService minioService;
    private final MinioConfigDto minioConfigDto;

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
        final PetEntity entity = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(StoreService.ERROR_MSG_FORMAT, petId)));

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
     * Deletes a {@link PetEntity} from the database. In case of a not found ID, an {@link EntityNotFoundException} is
     * thrown and handled in the {@link ExceptionHandler}.
     * 
     * @param petId the {@link PetEntity}'s ID, may be {@code null}
     */
    public void deleteEntry(final UUID petId) {
        final PetEntity entity = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(StoreService.ERROR_MSG_FORMAT, petId)));

        petRepository.delete(entity);
    }

    /**
     * Overrides a {@link PetEntity} with the given {@link Pet} DTO.
     * 
     * @param petId the {@link PetEntity}'s ID
     * @param pet   the given DTO
     */
    public void overwritePetEntity(final UUID petId, final Pet pet) {
        final PetEntity entity = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(StoreService.ERROR_MSG_FORMAT, petId)));

        tagRepository.deleteAllInBatch(entity.getTags());
        mapper.updatePetEntity(entity, pet);

        StoreService.linkTags(entity);

        petRepository.save(entity);
    }

    /**
     * Stores an image in the MinIo server. In addition the URL of the image on the server is stored in the database.
     * <p>
     * IMPORTANT: The database entry is done first since in case of a problem with the MinIo server the entry gets
     * rolled back using the {@link Transactional} annotation.
     * 
     * @param petId       the ID of the stored entity for assigning the URL, never {@code null}
     * @param body        the image as byte array, never empty
     * @param contentType the content type given from the {@link HttpServletRequest}
     * 
     * @return the {@link URI} object from the URL to the image
     */
    @SneakyThrows
    public URI storeImage(final UUID petId, final byte[] body, final String contentType) {
        final PetEntity entity = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ERROR_MSG_FORMAT, petId)));

        final String imageId = UUID.nameUUIDFromBytes(body).toString();
        final String imageUrlString = minioConfigDto.url() + ":" + minioConfigDto.browsePort() + "/buckets/"
                + minioConfigDto.imagesBucketName() + "/browse/" + imageId;

        final URI minioUri = URI.create(imageUrlString);

        final PhotoUrlEntity urlEntity = new PhotoUrlEntity(entity, minioUri.toURL());

        photoUrlRepository.save(urlEntity);

        minioService.storeImage(body, imageId, contentType);

        return minioUri;
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
     * The {@link Specification} for enriching the {@code WHERE} statement with a check on given tags.
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
