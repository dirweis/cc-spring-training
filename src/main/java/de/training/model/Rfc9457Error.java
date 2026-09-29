package de.training.model;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * The record representing the Error DTO for RFC 9457 specified error response bodies.
 *
 * @author Dirk Weissmann
 * @version 1.0
 * @since 2024-03-09
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9457">RFC 9457 specification</a>
 *
 * @param type     the error type, must be a {@link URI}, must not be {@code null}
 * @param title    the error title, a {@link String}, must not be {@code null}
 * @param instance a URN carrying the error's ID, must not be {@code null}
 * @param detail   the error's detailed description, a {@link String}, may be {@code null}
 * @param errors   the {@link List} of semantic body violations with JSON pointer and detail fields, may be {@code null}
 */
@Builder
public record Rfc9457Error(@NotNull URI type, @NotNull @Size(min = 5, max = 50) String title, @NotNull URI instance,
        @Size(min = 10, max = 200) String detail, List<@Valid InvalidParam> errors) {

    /**
     * The record RFC 9457 errors' array in case of a {@code 422} error as part of the response body.
     *
     * @author Dirk Weissmann
     * @version 1.0
     * @since 2024-03-09
     *
     * @param pointer the JSON path pointer, never {@code null}
     * @param detail  the error message, never {@code null}
     */
    @Builder
    public static record InvalidParam(@NotNull @Size(min = 3, max = 30) String pointer,
            @NotNull @Size(min = 8, max = 200) String detail) {
    }
}
