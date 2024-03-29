package de.training.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.exception.service.ErrorService;
import de.training.model.Rfc9457Error;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Rfc9457Error} response bodies in case of a caught further
 * exception that is not caught explicitly by another handler. Ensures the response code {@code 500} is returned.
 * <p>
 * <em>Must not occur in the productive area! Whenever this {@link ExceptionHandler} fires, there is definitely
 * something wrong with the implementation or with the infrastructure!</em>
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "Internal problem. Please contact the support.",
    "instance": "urn:ERROR:d4f78c67-b4a8-4f3c-9322-9312dba6c8e2"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.5
 *
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
class FurtherExceptionHandler {

    private final ErrorService errorService;

    /**
     * Catches the defined {@link Exception}s and creates an {@link Rfc9457Error} response body.
     * 
     * @param ex the {@link Exception} to catch, never {@code null}
     * 
     * @return the created {@link Rfc9457Error} object as response body, never {@code null}
     * 
     */
    @ExceptionHandler(Throwable.class)
    private ResponseEntity<Rfc9457Error> handleException(final Throwable ex) {
        return errorService.create500Response(ex);
    }
}
