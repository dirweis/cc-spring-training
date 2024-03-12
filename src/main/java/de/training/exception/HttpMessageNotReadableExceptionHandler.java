package de.training.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.exception.service.ErrorService;
import de.training.model.Rfc9457Error;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} in case the request body is missing. Other request body violations get processed in
 * {@link JsonParseErrorsHandler} now.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "JSON Parse Error",
    "instance": "urn:ERROR:81f65772-d2a0-4c9f-8873-14f8215187f2",
    "detail": "Required request body is missing"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2022-03-11
 * @version 2.0
 *
 */
@Order(2)
@RestControllerAdvice
@RequiredArgsConstructor
class HttpMessageNotReadableExceptionHandler {

    private final ErrorService errorService;

    /**
     * Creates the {@link ResponseEntity} including an {@link Rfc9457Error} body with status {@code 400}.
     * 
     * @param ex the {@link Exception} object for handling, never {@code null}
     * 
     * @return the {@link ResponseEntity} including an {@link Rfc9457Error} body
     * 
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<Rfc9457Error> handleException(final HttpMessageNotReadableException ex) {
        final String errorMsg = ex.getLocalizedMessage();

        final String detailInfo = errorMsg.substring(0, errorMsg.indexOf(':'));

        return errorService.handleBodySyntaxViolations(detailInfo);
    }
}