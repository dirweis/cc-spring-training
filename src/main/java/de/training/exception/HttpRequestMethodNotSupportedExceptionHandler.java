package de.training.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.model.Error;
import de.training.service.ErrorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link HttpRequestMethodNotSupportedException}. Ensures the response code {@code 405} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "Request method 'DELETE' not supported",
    "instance": "urn:ERROR:51dda052-32cd-4b75-b88f-e8780b34aff3",
    "detail": "Supported method(s): [GET, POST]"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.0
 *
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HttpRequestMethodNotSupportedExceptionHandler {

    @Autowired
    private ErrorService errorService;

    /**
     * Catches the defined {@link Exception}s and creates an {@link Error} response body.
     * 
     * @param ex the {@link Exception} to catch, never {@code null}
     * 
     * @return the created {@link Error} object as response body, never {@code null}
     * 
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<Error> handleException(final HttpRequestMethodNotSupportedException ex) {
        final Error error = errorService.finalizeRfc7807Error(ex.getLocalizedMessage(),
                "Supported method(s): " + ex.getSupportedHttpMethods());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(error);
    }
}
