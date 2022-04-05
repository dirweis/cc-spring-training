package de.infoteam.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

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
import org.springframework.web.bind.annotation.RestController;

import de.infoteam.annotation.MultipleOf;
import de.infoteam.annotation.PetIdNull;
import de.infoteam.model.Pet;
import de.infoteam.model.Pet.Category;
import de.infoteam.model.Pet.PetStatus;

/**
 * The end points of the {@link RestController} for handling the pets' information.
 * <p>
 * Besides the 'classic' end points for storing a new entry ({@link PostMapping}), updating an entry
 * ({@link PutMapping}), getting a pet by its ID ({@link GetMapping}) and deleting the entry ({@link DeleteMapping})
 * this {@code interface} provides some further end points for
 * <ul>
 * <li>find all pets that match on given parameters like e.g. the {@link PetStatus}</li>
 * <li>post a new image of the pet</li>
 * </ul>
 * 
 * @author Dirk Weissmann
 * @since 2022-02-15
 * @version 1.2
 * @see Pet
 *
 */
@Validated
@RequestMapping("/petstore/petservice/v1/pets")
interface PetsApi {

	/**
	 * {@code POST /pets}: Add a new pet to the store.
	 *
	 * @param pet the {@link Pet} resource to be added to the store (required)
	 * 
	 * @return
	 *         <dl>
	 *         <dt>status code {@code 201}</dt>
	 *         <dd>Successfully created, including the response {@code Location} header</dd>
	 * 
	 *         <dt>status code {@code 400}</dt>
	 *         <dd>Body syntactically invalid or path/query parameters violated</dd>
	 * 
	 *         <dt>status code {@code 405}</dt>
	 *         <dd>Wrong HTTP method</dd>
	 * 
	 *         <dt>status code {@code 415}</dt>
	 *         <dd>Media type not acceptable</dd>
	 * 
	 *         <dt>status code {@code 422}</dt>
	 *         <dd>Given {@link Pet} entity is not acceptable (semantically violated)</dd>
	 * 
	 *         <dt>status code {@code 500}</dt>
	 *         <dd>Internal error</dd>
	 *         </dl>
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addPet(@Valid @RequestBody @PetIdNull final Pet pet);

	/**
	 * <code>DELETE /pets/{petId\}</code>: Deletes a pet resource from the store.
	 *
	 * @param petId the {@link Pet} ID to delete (required)
	 * 
	 * @return a {@link ResponseEntity} carrying the
	 *         <dl>
	 *         <dt>status code {@code 204}</dt>
	 *         <dd>in case of a successful resource deletion</dd>
	 * 
	 *         <dt>status code {@code 400}</dt>
	 *         <dd>in case an invalid ID is supplied</dd>
	 * 
	 *         <dt>status code {@code 404}</dt>
	 *         <dd>in case the {@link Pet} resource was not found</dd>
	 * 
	 *         <dt>status code {@code 405}</dt>
	 *         <dd>in case of a wrong {@code HTTP} method</dd>
	 * 
	 *         <dt>status code {@code 415}</dt>
	 *         <dd>in case the media type is not acceptable</dd>
	 * 
	 *         <dt>status code {@code 500}</dt>
	 *         <dd>in case of an internal error</dd>
	 *         </dl>
	 */
	@DeleteMapping(path = "{petId}")
	ResponseEntity<Void> deletePet(@PathVariable UUID petId);

	/**
	 * <code>GET /pets</code> Finds {@link Pet} resources by the given parameters At least page and size is given for
	 * filtering the results
	 *
	 * @param page     the first parameter for DB paging; default is {@code 0}
	 * @param size     the first parameter for DB paging; default is {@code 20}
	 * @param tags     tags for filtering the results
	 * @param status   the status value that need to be considered for filtering, may be {@code null}
	 * @param category the category value that need to be considered for filtering, may be {@code null}
	 * 
	 * @return a {@link ResponseEntity} carrying the
	 * 
	 *         <dl>
	 *         <dt>status code {@code 200}</dt>
	 *         <dd>in case of a successful operation</dd>
	 * 
	 *         <dt>status code {@code 400}</dt>
	 *         <dd>in case of an invalid value for the parameters</dd>
	 * 
	 *         <dt>status code {@code 405}</dt>
	 *         <dd>in case of the wrong given {@code HTTP} method</dd>
	 * 
	 *         <dt>status code {@code 415}</dt>
	 *         <dd>in case the media type is not acceptable</dd>
	 * 
	 *         <dt>status code {@code 500}</dt>
	 *         <dd>in case of an internal error</dd>
	 *         </dl>
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<Pet>> findPetsRestrictedByParameters(
			@Min(0) @RequestParam(required = false, defaultValue = "0") Integer page,
			@Min(10) @Max(1_000) @MultipleOf(10) @RequestParam(required = false, defaultValue = "20") Integer size,
			@RequestParam(required = false) List<@Size(min = 3, max = 20) String> tags,
			@RequestParam(required = false) PetStatus status, @RequestParam(required = false) Category category);

	/**
	 * <code>GET /pets/{petId}</code>: Finds a {@link Pet} resource by its ID
	 *
	 * @param petId ID of pet to return (required)
	 * 
	 * @return a {@link ResponseEntity} carrying the
	 * 
	 *         <dl>
	 *         <dt>status code {@code 200}</dt>
	 *         <dd>in case of a successful operation</dd>
	 * 
	 *         <dt>status code {@code 400}</dt>
	 *         <dd>in case an invalid ID was supplied</dd>
	 * 
	 *         <dt>status code {@code 404}</dt>
	 *         <dd>in case the {@link Pet} resource not found</dd>
	 * 
	 *         <dt>status code {@code 405}</dt>
	 *         <dd>in case of the wrong {@code HTTP} method</dd>
	 * 
	 *         <dt>status code {@code 415}</dt>
	 *         <dd>in case the media type is not acceptable</dd>
	 * 
	 *         <dt>status code {@code 500}</dt>
	 *         <dd>in case of an internal error</dd>
	 *         </dl>
	 */
	@GetMapping(path = "{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Pet> getPetById(@PathVariable UUID petId);

	/**
	 * <code>PUT /pets/{petId}</code> Update an existing pet
	 *
	 * @param petId the ID of pet to update (required)
	 * @param pet   {@link Pet} object that needs to be overwritten (required)
	 * 
	 * @return a {@link ResponseEntity} carrying the
	 * 
	 *         <dl>
	 * 
	 *         <dt>status code {@code 204}</dt>
	 *         <dd>in case of a successful resource update</dd>
	 * 
	 *         <dt>status code {@code 400}</dt>
	 *         <dd>in case an invalid ID was supplied or syntactical body violations occurred</dd>
	 * 
	 *         <dt>status code {@code 404}</dt>
	 *         <dd>in case the {@link Pet} resource was not found</dd>
	 * 
	 *         <dt>status code {@code 405}</dt>
	 *         <dd>in case the wrong {@code HTTP} method was used</dd>
	 * 
	 *         <dt>status code {@code 415}</dt>
	 *         <dd>in case the media type is not acceptable</dd>
	 * 
	 *         <dt>status code {@code 422}</dt>
	 *         <dd>in case the given {@link Pet} entity is not acceptable (contains semantic violations)</dd>
	 * 
	 *         <dt>status code {@code 500}</dt>
	 *         <dd>in case an internal error occurred</dd>
	 * 
	 *         </dl>
	 */
	@PutMapping(path = "{petId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> updatePet(@PathVariable UUID petId, @Valid @RequestBody Pet pet);

	/**
	 * <code>PUT /pets/{petId}/image</code> Adds an image to the Pet resource
	 *
	 * @param petId the ID of the {@link Pet} resource to enrich with an image (required)
	 * @param body  an image of the format {@code JPEG, PNG or GIF}
	 * 
	 * @return a {@link ResponseEntity} carrying the
	 * 
	 *         <dl>
	 *         <dt>status code {@code 201}</dt>
	 *         <dd>in case of a successfully created image resource</dd>
	 * 
	 *         <dt>status code {@code 400}</dt>
	 *         <dd>in case of an inacceptable {@link Pet} ID or a syntactically invalid body</dd>
	 * 
	 *         <dt>status code {@code 405}</dt>
	 *         <dd>in case the wrong {@code HTTP} method was used</dd>
	 * 
	 *         <dt>status code {@code 409}</dt>
	 *         <dd>in case of a conflict: the image already exists for the {@link Pet}</dd>
	 * 
	 *         <dt>status code {@code 415}</dt>
	 *         <dd>in case of a not acceptable media type</dd>
	 * 
	 *         <dt>status code {@code 500}</dt>
	 *         <dd>in case of an internal error</dd>
	 *         </dl>
	 */
	@PutMapping(path = "{petId}/image", consumes = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_PNG_VALUE })
	ResponseEntity<Void> uploadFile(@PathVariable UUID petId,
			@Size(min = 10_000, max = 2_000_000) @RequestBody byte[] body);
}
