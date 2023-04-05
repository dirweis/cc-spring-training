package de.training.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.training.model.Pet;
import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Validated
@RequestMapping("/petstore/petservice/v1/pets")
interface PetsApi {

    /**
     * POST /pets: Add a new pet to the store
     *
     * @param pet Pet resource to be added to the store (required)
     * 
     * @return Successfully created (status code 201) or Body syntactically invalid (status code 400) or User not
     *         allowed (status code 401) or Wrong HTTP method (status code 405) or Media type not acceptable (status
     *         code 415) or Given pet entity is not acceptable (status code 422) or Internal error (status code 500)
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> addPet(@Valid @RequestBody final Pet pet);

    /**
     * DELETE /pets/{petId}: Deletes a pet resource
     *
     * @param petId Pet id to delete (required)
     * 
     * @return Successful resource deletion (status code 204) or Invalid ID supplied (status code 400) or User not
     *         allowed (status code 401) or Access denied for this resource (status code 403) or Pet resource not found
     *         (status code 404) or Wrong HTTP method (status code 405) or Media type not acceptable (status code 415)
     *         or Internal error (status code 500)
     */
    @DeleteMapping(path = "{petId}")
    ResponseEntity<Void> deletePet(@PathVariable UUID petId);

    /**
     * GET /pets: Finds Pet resources by the given parameters At least page and size is given for filtering the results
     *
     * @param page     Default is 0 (required)
     * @param size     Default is 20 (required)
     * @param tags     Tags to filter by (optional)
     * @param status   Status values that need to be considered for filtering (optional)
     * @param category Category values that need to be considered for filtering (optional)
     * 
     * @return successful operation (status code 200) or Invalid status value (status code 400) or User not allowed
     *         (status code 401) or Wrong HTTP method (status code 405) or Media type not acceptable (status code 415)
     *         or Internal error (status code 500)
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Pet>> findPetsRestrictedByParameters(
            @Min(0) @RequestParam(required = false, defaultValue = "0") Integer page,
            @Min(10) @Max(1_000) @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) List<@Size(min = 3, max = 20) String> tags,
            @RequestParam(required = false) PetStatus status, @RequestParam(required = false) Category category);

    /**
     * GET /pets/{petId}: Find pet by ID Returns a single pet resource
     *
     * @param petId ID of pet to return (required)
     * 
     * @return successful operation (status code 200) or Invalid ID supplied (status code 400) or User not allowed
     *         (status code 401) or Access denied for this resource (status code 403) or Pet resource not found (status
     *         code 404) or Wrong HTTP method (status code 405) or Media type not acceptable (status code 415) or
     *         Internal error (status code 500)
     */
    @GetMapping(path = "{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Pet> getPetById(@PathVariable UUID petId);

    /**
     * PUT /pets/{petId}: Update an existing pet
     *
     * @param petId ID of pet to update (required)
     * @param pet   Pet object that needs to be added to the store (required)
     * 
     * @return Successful resource update (status code 204) or Invalid ID supplied (status code 400) or User not allowed
     *         (status code 401) or Access denied for this resource (status code 403) or Pet resource not found (status
     *         code 404) or Wrong HTTP method (status code 405) or Media type not acceptable (status code 415) or Given
     *         pet entity is not acceptable (status code 422) or Internal error (status code 500)
     */
    @PutMapping(path = "{petId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> updatePet(@PathVariable UUID petId, @Valid @RequestBody Pet pet);

    /**
     * PUT /pets/{petId}/image: Adds an image to the Pet resource
     *
     * @param petId ID of pet to update (required)
     * @param body  An image of the format JPEG, PNG or GIF (optional)
     * 
     * @return Successfully created (status code 201) or Body syntactically invalid (status code 400) or User not
     *         allowed (status code 401) or Wrong HTTP method (status code 405) or Conflict: The image already exists
     *         for the pet (status code 409) or Media type not acceptable (status code 415) or Internal error (status
     *         code 500)
     */
    @PutMapping(path = "{petId}/image", consumes = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE })
    ResponseEntity<Void> uploadFile(@PathVariable UUID petId, @Size(max = 2_000_000) @RequestBody byte[] body);
}
