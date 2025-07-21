package de.training.exception.constraint;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.exception.service.ErrorService;
import de.training.model.Rfc9457Error;
import de.training.model.Rfc9457Error.InvalidParam;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Path.Node;
import lombok.RequiredArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Rfc9457Error} response bodies in case of a caught
 * {@link ConstraintViolationException}. Ensures the response code {@code 400} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "Constraint violations",
    "instance": "urn:ERROR:61bb8581-9d92-4447-b49f-44ea526f18b8",
    "invalid_params": [
        {
            "name": "#/count",
            "reason": "11 is not a multiple of 10"
        }
    ]
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.0
 *
 */
@Order(4)
@RestControllerAdvice
@RequiredArgsConstructor
class ConstraintViolationExceptionHandler {

    private final ErrorService errorService;

    /**
     * Catches the defined {@link Exception}s and creates an {@link Rfc9457Error} response body.
     * 
     * @param ex the {@link Exception} to catch, never {@code null}
     * 
     * @return the created {@link Rfc9457Error} object as response body, never {@code null}
     * 
     */
    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<Rfc9457Error> handleException(final ConstraintViolationException ex) {
        if (semanticError(ex)) {
            return create422Response(ex);
        }

        return create400Response(ex);
    }

    /**
     * Creates a {@code BAD_REQUEST} response for path parameter / query parameter violations.
     * 
     * @param ex the given {@link ConstraintViolationException} rising from our own customized checks
     * 
     * @return the finalized {@link ResponseEntity}, never {@code null}
     */
    private ResponseEntity<Rfc9457Error> create400Response(final ConstraintViolationException ex) {
        final ConstraintViolation<?> violation = ex.getConstraintViolations().stream().findFirst().orElseThrow();
        final String fieldName = extractParamNameFromPath(violation.getPropertyPath());

        final Rfc9457Error error = errorService.finalizeRfc9457Error("Violation in parameter",
                fieldName + ": " + violation.getMessage());

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }

    /**
     * Creates a {@code UNPROCESSABLE_ENTITY} response for violations in the request body.
     * 
     * @param ex the given {@link ConstraintViolationException} rising from our own customized checks
     * 
     * @return the finalized {@link ResponseEntity}, never {@code null}
     */
    private ResponseEntity<Rfc9457Error> create422Response(final ConstraintViolationException ex) {
        final List<InvalidParam> invalidParams = ex.getConstraintViolations().stream()
                .map(constraintViolation -> InvalidParam.builder()
                        .pointer("#/" + extractParamNameFromPath(constraintViolation.getPropertyPath()))
                        .detail(constraintViolation.getMessage()).build())
                .toList();

        final Rfc9457Error error = errorService.finalizeRfc9457Error("Request body validation failed", invalidParams);

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }

    /**
     * Checks for an existing {@link Node} named {@code semantic} in the {@link Exception}'s
     * {@link ConstraintViolation}s.
     * 
     * @param ex the given exception for extracting the {@link ConstraintViolation}s
     * 
     * @return {@code true} if found
     */
    private static boolean semanticError(final ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .anyMatch((final ConstraintViolation<?> cv) -> StreamSupport
                        .stream(cv.getPropertyPath().spliterator(), false)
                        .anyMatch((final Node node) -> "body".equalsIgnoreCase(node.getName())));
    }

    /**
     * Extracts the parameter name from the last node in the property path.
     * 
     * @param propertyPath the given property {@link Path}, must not be {@code null}
     * 
     * @return the parameter name, never {@code null}
     */
    private static String extractParamNameFromPath(final Path propertyPath) {
        return StreamSupport.stream(propertyPath.spliterator(), false).map(Node::getName)
                .reduce((first, second) -> second).orElseGet(propertyPath::toString);
    }
}
