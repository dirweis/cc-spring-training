package de.training.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.model.Rfc9457Error;
import de.training.service.ErrorService;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Rfc9457Error} response bodies in case of a caught
 * {@link HttpMediaTypeNotSupportedException}. Ensures the response code {@code 415} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "Content type 'application/atom+xml' not supported",
    "instance": "urn:ERROR:aaf1da57-6415-4e6d-b9b1-d5969f052bc2",
    "detail": "Supported media type(s): [application/json]"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2022-02-16
 * @version 1.0
 * @see <a href="https://github.com/spring-projects/spring-framework/issues/28062">HttpMediaTypeNotSupportedException
 *      getSupportedMediaTypes() fails for unknown values in Content-Type header</a>
 */
@Order(0)
@RestControllerAdvice
@RequiredArgsConstructor
class HttpMediaTypeNotSupportedExceptionHandler {

    private final ErrorService errorService;

    /**
     * Catches the defined {@link Exception}s and creates an {@link Rfc9457Error} response body.
     * 
     * @param ex the {@link Exception} to catch, never {@code null}
     * 
     * @return the created {@link Rfc9457Error} object as response body, never {@code null}
     * 
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    private ResponseEntity<Rfc9457Error> handleException(final HttpMediaTypeNotSupportedException ex) {
        final String msg = ex.getLocalizedMessage();
        final String title = !msg.contains("'") ? "Request header 'Content-Type' not found" : msg;

        final Rfc9457Error error = errorService.finalizeRfc9457Error(title,
                "Supported media type(s): " + ex.getSupportedMediaTypes());

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(error);
    }
}
