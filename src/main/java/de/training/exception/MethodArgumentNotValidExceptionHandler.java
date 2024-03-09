package de.training.exception;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.model.Rfc9457Error;
import de.training.model.Rfc9457Error.InvalidParam;
import de.training.service.ErrorService;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Rfc9457Error} response bodies in case of a caught
 * {@link MethodArgumentNotValidException}. Ensures the response code {@code 422} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "Request body validation failed",
    "instance": "urn:ERROR:9a6a0b9c-1d0d-4096-9bb5-245f2e60dcfe",
    "errors": [
        {
            "pointer": "#/description",
            "detail": "darf nicht null sein"
        }
    ]
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2024-03-09
 * @version 1.0
 *
 */
@Order(2)
@RestControllerAdvice
@RequiredArgsConstructor
class MethodArgumentNotValidExceptionHandler {

    private final ErrorService errorService;

    /**
     * Catches the defined {@link Exception}s and creates an {@link Rfc9457Error} response body.
     * 
     * @param ex the {@link Exception} to catch, never {@code null}
     * 
     * @return the created {@link Rfc9457Error} object as response body, never {@code null}
     * 
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Rfc9457Error> handleException(final MethodArgumentNotValidException ex) {
        final List<InvalidParam> invalidParams = ex.getAllErrors().stream().filter(FieldError.class::isInstance)
                .map(FieldError.class::cast).map(fieldError -> InvalidParam.builder()
                        .pointer("#/" + fieldError.getField()).detail(fieldError.getDefaultMessage()).build())
                .toList();

        final Rfc9457Error error = errorService.finalizeRfc9457Error("Request body validation failed", invalidParams);

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }
}
