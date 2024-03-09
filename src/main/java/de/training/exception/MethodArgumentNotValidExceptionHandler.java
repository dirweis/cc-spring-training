package de.training.exception;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.exception.service.ErrorService;
import de.training.model.Error;
import de.training.model.Error.InvalidParam;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link MethodArgumentNotValidException}. Ensures the response code {@code 405} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "JSON parse error",
    "instance": "urn:ERROR:782a424b-fdf2-4682-bab4-683d86024800",
    "detail": "Unexpected end-of-input: expected close marker for Object at [Source: line: 14, column: 1]"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.1
 *
 */
@Order(2)
@RestControllerAdvice
@RequiredArgsConstructor
class MethodArgumentNotValidExceptionHandler {

    private final ErrorService errorService;

    /**
     * Catches the defined {@link Exception}s and creates an {@link Error} response body.
     * 
     * @param ex the {@link Exception} to catch, never {@code null}
     * 
     * @return the created {@link Error} object as response body, never {@code null}
     * 
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Error> handleException(final MethodArgumentNotValidException ex) {
        final List<InvalidParam> invalidParams = ex.getAllErrors().stream().filter(FieldError.class::isInstance)
                .map(FieldError.class::cast).map(fieldError -> InvalidParam.builder().name(fieldError.getField())
                        .reason(fieldError.getDefaultMessage()).build())
                .toList();

        final Error error = errorService.finalizeRfc7807Error("Request body validation failed", invalidParams);

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }
}
