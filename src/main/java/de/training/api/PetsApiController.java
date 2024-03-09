package de.training.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.apache.catalina.mapper.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.training.db.model.PetEntity;
import de.training.db.service.StoreService;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The controller implementation of the {@code REST} end points defined in the interface {@link PetsApi}.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-15
 * @version 1.1
 *
 */
@Log4j2
@RestController
@RequiredArgsConstructor
class PetsApiController implements PetsApi {

    private final StoreService storeService;

    /**
     * {@inheritDoc}
     * <p>
     * Uses a Mapstruct {@link Mapper} and a {@link JpaRepository} via the {@link StoreService}.
     */
    @Override
    public ResponseEntity<Void> addPet(final Pet pet, final HttpServletRequest request) {
        log.info("Start creating a pet entry on given body: {}", pet);

        final UUID petId = storeService.storeNewPet(pet);

        return ResponseEntity.created(URI.create(request.getRequestURI() + "/" + petId)).build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses the {@link StoreService} for the deletion on the database.
     */
    @Override
    public ResponseEntity<Void> deletePet(final UUID petId) {
        log.info("Start deleting pet entry with ID {}", petId);

        storeService.deleteEntry(petId);

        return ResponseEntity.noContent().build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses {@link Specification}s for dynamic {@code WHERE} clauses via the {@link StoreService}.
     */
    @Override
    public ResponseEntity<List<Pet>> findPetsRestrictedByParameters(final Integer page, final Integer size,
            final List<String> tags, final PetStatus status, final Category category) {
        log.info("Start retrieving pets on the parameters page: {}, size: {}, tags: {}, status: {}, category: {}", page,
                size, tags, status, category);

        final List<Pet> responseBody = storeService.findByParameters(tags, status, category,
                PageRequest.of(page, size, Sort.by(Direction.DESC, "createdTime")));

        return ResponseEntity.ok(responseBody);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses the {@link StoreService} for the {@code SELECT} statement on the database.
     */
    @Override
    public ResponseEntity<Pet> getPetById(final UUID petId) {
        log.info("Start getting pet entry with ID {}", petId);

        final Pet responseBody = storeService.getPetById(petId);

        return ResponseEntity.ok(responseBody);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses a Mapstruct {@link Mapper} and a {@link JpaRepository} for overwriting the {@link PetEntity} via the
     * {@link StoreService}.
     */
    @Override
    public ResponseEntity<Void> updatePet(final UUID petId, final Pet pet) {
        log.info("Start overwriting pet with ID {}.", petId);
        log.info("The new values are: {}", pet);

        storeService.overwritePetEntity(petId, pet);

        return ResponseEntity.noContent().build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Uses a <a href="https://min.io/">MinIo server</a> as document store for the images.
     */
    @Override
    public ResponseEntity<Void> uploadFile(final UUID petId, final byte[] body, final HttpServletRequest request) {
        log.info("Image size: {}", body.length);

        final URI imageUrl = storeService.storeImage(petId, body, request.getContentType());

        return ResponseEntity.created(imageUrl).build();
    }
}
