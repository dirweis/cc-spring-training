package de.infoteam.api;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.infoteam.model.Pet;

@RestController
class PetsApiController implements PetsApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(PetsApiController.class);

	@Override
	public ResponseEntity<Void> addPet(final Pet pet) {
		LOGGER.info("Unser Pet: {}", pet);

		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@Override
	public ResponseEntity<Void> deletePet(final UUID petId) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@Override
	public ResponseEntity<List<Pet>> findPetsRestrictedByParameters(final Integer page, final Integer size,
			final List<String> tags, final Pet.PetStatus status) {
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
		LOGGER.info("Image size: {}", body.length);

		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}
}
