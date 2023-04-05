package de.training.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.training.model.Pet;
import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
class PetsApiController implements PetsApi {

    @Override
    public ResponseEntity<Void> addPet(final Pet pet) {
        log.info("Unser Pet: {}", pet);

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> deletePet(final UUID petId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<List<Pet>> findPetsRestrictedByParameters(final Integer page, final Integer size,
            final List<String> tags, final PetStatus status, final Category category) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Pet> getPetById(final UUID petId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> updatePet(final UUID petId, final Pet pet) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> uploadFile(final UUID petId, final byte[] body) {
        log.info("Image size: {}", body.length);

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
