package de.training.exception;

import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.training.model.Rfc9457Error;
import de.training.service.ErrorService;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link MethodArgumentTypeMismatchException}. Ensures the response code {@code 400} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "Error in parameter 'page'",
    "instance": "urn:ERROR:ce0663b0-51a9-4f19-9b21-28b94a8d1ab6",
    "detail": "Failed to convert value of type 'String' to required type 'Integer'; For input string: \"a\""
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2024-03-09
 * @version 1.0
 *
 */
@Order(3)
@RestControllerAdvice
@RequiredArgsConstructor
class MethodArgumentTypeMismatchExceptionHandler {

    private final ErrorService errorService;

    /**
     * Catches the defined {@link Exception}s and creates an {@link Rfc9457Error} response body.
     * 
     * @param ex the {@link Exception} to catch, never {@code null}
     * 
     * @return the created {@link Rfc9457Error} object as response body, never {@code null}
     * 
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<Rfc9457Error> handleException(final MethodArgumentTypeMismatchException ex) {
        final String title = "Error in parameter '" + ex.getPropertyName() + "'";
        final String detail = ErrorService.removePackageInformation(ex.getLocalizedMessage());

        final Rfc9457Error error = errorService.finalizeRfc9457Error(title, detail);

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }
}
