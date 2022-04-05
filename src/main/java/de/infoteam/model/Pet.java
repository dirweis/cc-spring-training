package de.infoteam.model;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pet(UUID id, @NotNull Category category, @NotNull @Size(min = 3, max = 30) String name,
		@JsonProperty("photo-urls") List<URI> photoUrls, List<@Size(min = 3, max = 20) String> tags,
		@NotNull PetStatus status, @NotNull @Size(min = 30, max = 1_000) String description) {

	public enum Category {
		DOG, CAT, BIRD, MOUSE, SPIDER;
	}

	public enum PetStatus {
		AVAILABLE, PENDING, SOLD;
	}
}
