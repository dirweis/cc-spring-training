package de.training.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.apache.catalina.mapper.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.training.db.service.StoreService;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The controller implementation of the {@code REST} end points defined in the interface {@link PetsApi}.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-15
 * @version 0.2
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
        log.info("Unser Pet: {}", pet);

        final UUID petId = storeService.storeNewPet(pet);

        return ResponseEntity.created(URI.create(request.getRequestURI() + "/" + petId)).build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <i>Not yet implemented</i>
     */
    @Override
    public ResponseEntity<Void> deletePet(final UUID petId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <i>Not yet implemented</i>
     */
    @Override
    public ResponseEntity<List<Pet>> findPetsRestrictedByParameters(final Integer page, final Integer size,
            final List<String> tags, final Pet.PetStatus status, final Category category) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <i>Not yet implemented</i>
     */
    @Override
    public ResponseEntity<Pet> getPetById(final UUID petId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <i>Not yet implemented</i>
     */
    @Override
    public ResponseEntity<Void> updatePet(final UUID petId, final Pet pet) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <i>Not yet implemented</i>
     */
    @Override
    public ResponseEntity<Void> uploadFile(final UUID petId, final byte[] body) {
        log.info("Image size: {}", body.length);

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
