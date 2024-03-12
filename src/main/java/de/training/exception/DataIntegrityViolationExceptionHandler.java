package de.training.exception;

import java.util.Locale;

import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.exception.service.ErrorService;
import de.training.model.Rfc9457Error;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Rfc9457Error} response bodies in case of a caught
 * {@link DataIntegrityViolationException}. Ensures the response code {@code 409} is returned in case of a document /
 * image that is already stored.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets/9218aaa7-a9cd-408b-ad2e-9c591b46fc5a/image",
    "title": "Entry already exists",
    "instance": "urn:ERROR:00e51fbb-813d-461f-a275-cb3ff6b98447",
    "detail": "FEHLER: doppelter Schlüsselwert verletzt"
 }
 * </pre>
 * 
 * @since 2022-03-21
 * @version 1.1
 * @author Dirk Weissmann
 *
 */
@Order(7)
@RestControllerAdvice
@RequiredArgsConstructor
class DataIntegrityViolationExceptionHandler {

    private final ErrorService errorService;

    /**
     * Catches the defined {@link Exception}s and creates an {@link Rfc9457Error} response body.
     * 
     * @param ex the {@link Exception} to catch, never {@code null}
     * 
     * @return the created {@link ResponseEntity} as response, never {@code null}
     * 
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<Rfc9457Error> handleException(final DataIntegrityViolationException ex) {
        final String rawMessage = ex.getMostSpecificCause().getLocalizedMessage();

        if (rawMessage.toLowerCase(Locale.getDefault()).contains("unique")) {
            return create409Response();
        }

        return errorService.create500Response(ex);
    }

    /**
     * Creates a conflict (code {@code 409}) {@link ResponseEntity}.
     * 
     * @return the {@link ResponseEntity} object with code {@code 409}
     */
    private ResponseEntity<Rfc9457Error> create409Response() {
        final Rfc9457Error error = errorService.finalizeRfc9457Error("Entry already exists",
                "Unique constraint violated (already exist)");

        return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }
}
