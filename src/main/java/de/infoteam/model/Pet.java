package de.infoteam.model;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The record representing the Pet resource's DTO.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-16
 * @version 1.0
 *
 * @param id          the ID, may be {@code null} (in case of a POST request, the ID must be {@code null})
 * @param category    the pet's category, must not be {@code null}
 * @param name        the pet's name, must not be {@code null}
 * @param photoUrls   the {@link List} of {@link URI}s that link to an image, may be {@code null}
 * @param tags        a {@link List} of {@link String}s as tags for the {@link Pet}, may be {@code null}
 * @param status      the {@link Pet}'s status from {@link PetStatus}, must not be {@code null}
 * @param description the description of the {@link Pet}, must not be {@code null}
 */
public record Pet(UUID id, @NotNull Category category, @NotNull @Size(min = 3, max = 30) String name,
		@JsonProperty("photo-urls") List<URI> photoUrls, List<@Size(min = 3, max = 20) String> tags,
		@NotNull PetStatus status, @NotNull @Size(min = 30, max = 1_000) String description) {

	private enum Category {
		DOG, CAT, BIRD, MOUSE, SPIDER;
	}

	/**
	 * A little enumeration for a {@link Pet}'s status.
	 * 
	 * @author Dirk Weissmann
	 * @since 2022-02-16
	 * @version 1.0
	 *
	 */
	public enum PetStatus {
		/**
		 * the status representing the {@link String} {@code AVAILABLE}
		 */
		AVAILABLE,

		/**
		 * the status representing the {@link String} {@code PENDING}
		 */
		PENDING,

		/**
		 * the status representing the {@link String} {@code SOLD}
		 */
		SOLD;
	}
}
