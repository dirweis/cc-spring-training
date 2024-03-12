package de.training.exception;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.training.exception.service.ErrorService;
import de.training.model.Rfc9457Error;
import de.training.model.Rfc9457Error.InvalidParam;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Rfc9457Error} response bodies in case of a caught
 * {@link MethodArgumentTypeMismatchException}. Ensures the response code {@code 400} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "invalid_params": [
        {
            "name": "petId",
            "reason": "Invalid UUID string: 1"
        }
    ],
    "type": "/petstore/petservice/v1/pets/1",
    "title": "Failed to convert value of type 'String' to required type 'UUID'",
    "instance": "urn:ERROR:c252c6f9-f605-4157-ab77-392065f05268"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.4
 *
 */
@Order(4)
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
        final String errorMsg = ErrorService.removePackageInformation(ex.getLocalizedMessage());

        final String title = errorMsg.substring(0, errorMsg.indexOf(';'));
        final String name = ex.getName();
        final String reason = errorMsg.substring(errorMsg.indexOf(';') + 2);

        final List<InvalidParam> invalidParams = List
                .of(InvalidParam.builder().pointer("#/" + name).detail(reason).build());

        final Rfc9457Error error = errorService.finalizeRfc9457Error(title, invalidParams);

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }
}
