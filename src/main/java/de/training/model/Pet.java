package de.training.model;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Pet(UUID id, @NotNull Category category, @NotNull @Size(min = 3, max = 30) String name,
        @JsonProperty("photo-urls") List<URL> photoUrls, List<@Size(min = 3, max = 20) String> tags,
        @NotNull PetStatus status, @NotNull @Size(min = 30, max = 1_000) String description) {

    public enum Category {
        DOG, CAT, BIRD, MOUSE, SPIDER;
    }

    public enum PetStatus {
        AVAILABLE, PENDING, SOLD;
    }
}
