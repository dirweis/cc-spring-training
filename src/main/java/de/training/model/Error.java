package de.training.model;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The record representing the Error DTO for RFC 7807 specified error response bodies.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-16
 * @version 1.0
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 7807 specification</a>
 *
 * @param type          the error type, must be a {@link URI}, must not be {@code null}
 * @param title         the error title, a {@link String}, must not be {@code null}
 * @param instance      a URN carrying the error's ID, must not be {@code null}
 * @param detail        the error's detailed description, a {@link String}, may be {@code null}
 * @param invalidParams in case of parameter violations, this {@link List} is used, may be {@code null}
 * @param timestamp     the error's time stamp, must not be {@code null}
 */
public record Error(@NotNull URI type, @NotNull @Size(min = 5, max = 50) String title, @NotNull URI instance,
        @Size(min = 10, max = 200) String detail,
        @JsonProperty("invalid_params") @Valid List<InvalidParam> invalidParams, @NotNull Long timestamp) {

    /**
     * The record representing an invalid parameter information DTO as part of the {@link Error} DTO.
     * 
     * @author Dirk Weissmann
     * @since 2022-02-16
     * @version 1.0
     * 
     * @param name   the parameter's name, must not be {@code null}
     * @param reason the parameter violation's reason, must not be {@code null}
     *
     */
    public record InvalidParam(@NotNull @Size(min = 3, max = 20) String name,
            @NotNull @Size(min = 8, max = 200) String reason) {
        /* Nothing special in this record */
    }
}
