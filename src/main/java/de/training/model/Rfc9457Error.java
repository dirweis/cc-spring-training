package de.training.model;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Rfc9457Error(@NotNull URI type, @NotNull @Size(min = 5, max = 50) String title, @NotNull URI instance,
        @Size(min = 10, max = 200) String detail, List<@Valid InvalidParam> errors) {

    public static record InvalidParam(@NotNull @Size(min = 3, max = 30) String pointer,
            @NotNull @Size(min = 8, max = 200) String detail) {
    }
}
