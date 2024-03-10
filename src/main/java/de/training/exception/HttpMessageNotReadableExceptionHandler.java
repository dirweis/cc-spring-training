package de.training.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.exception.jsonerror.AbstractJsonErrorHandler;
import de.training.exception.jsonerror.JsonErrorFactory;
import de.training.model.Rfc9457Error;
import de.training.service.ErrorService;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Rfc9457Error} response bodies in case of a caught
 * {@link HttpMessageNotReadableException}. Ensures the response code {@code 400} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "JSON parse error",
    "instance": "urn:ERROR:0125084f-d9e8-49fe-8c03-64de5b771b57",
    "detail": "Unexpected end-of-input: expected close marker for Object at [Source: line: 14, column: 1]"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.0
 * @see JsonErrorFactory
 *
 */
@Order(1)
@RestControllerAdvice
@RequiredArgsConstructor
class HttpMessageNotReadableExceptionHandler {

    private final ErrorService errorService;

    /**
     * Catches the defined {@link Exception}s and creates an {@link Rfc9457Error} response body.
     * 
     * @param ex the {@link Exception} to catch, never {@code null}
     * 
     * @return the created {@link Rfc9457Error} object as response body, never {@code null}
     * 
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<Rfc9457Error> handleException(final HttpMessageNotReadableException ex) {
        final AbstractJsonErrorHandler handler = JsonErrorFactory.getErrorHandler(ex.getMostSpecificCause(),
                errorService, ex.getLocalizedMessage());

        return handler.createResponse();
    }
}
