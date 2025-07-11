package de.training.exception;

import java.util.List;
import java.util.Objects;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import de.training.exception.service.ErrorService;
import de.training.model.Rfc9457Error;
import de.training.model.Rfc9457Error.InvalidParam;
import lombok.RequiredArgsConstructor;

/**
 * The implementation of the {@link ExceptionHandler}s rising from sub classes of an
 * {@link HttpMessageNotReadableException} in case of violations of the request body.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-14
 * @version 1.3
 *
 */
@Order(1)
@RestControllerAdvice
@RequiredArgsConstructor
class JsonParseErrorsHandler {

    private final ErrorService errorService;

    /**
     * The implementation of a {@link MismatchedInputException} {@link ExceptionHandler} for {@code JSON} mismatches.
     * <p>
     * Example output:
     * 
     * <pre>
     {
        "invalid_params": [
            {
                "name": "category",
                "reason": "Cannot deserialize value of type `Pet$Category` from String \"cats\": not one of the values accepted for Enum class: [BIRD, DOG, MOUSE, CAT, SPIDER] at [Source: line: 10, column: 18] (through reference chain: Pet[\"category\"])"
            }
        ],
        "type": "/petstore/petservice/v1/pets",
        "title": "Request body validation failed",
        "instance": "urn:ERROR:6080226b-38d9-44a1-8694-685e14330915"
     }
     * </pre>
     * 
     * @param ex the {@link Exception} object for handling, never {@code null}
     * 
     * @return the {@link ResponseEntity} including an {@link Rfc9457Error} body
     */
    @ExceptionHandler(MismatchedInputException.class)
    private ResponseEntity<Rfc9457Error> handleMismatchException(final MismatchedInputException ex) {
        final String errMsg = ex.getLocalizedMessage();

        if (errMsg.startsWith("Cannot deserialize ")) {
            return createSemanticResponse(ex);
        }

        return errorService.handleBodySyntaxViolations("No parsable JSON. Opening brace missing?");
    }

    /**
     * The implementation of an {@link ExceptionHandler} in case a {@link JsonEOFException} is thrown.
     * <p>
     * Example output:
     * 
     * <pre>
     {
        "type": "/petstore/petservice/v1/pets",
        "title": "JSON Parse Error",
        "instance": "urn:ERROR:bc438273-070b-47e9-9f9b-fc4663f77e31",
        "detail": "Not well-formed for the JSON end. Missing brace?"
     }
     * </pre>
     * 
     * @param ex the {@link Exception} object for handling, never {@code null}
     * 
     * @return the {@link ResponseEntity} including an {@link Rfc9457Error} body
     *
     */
    @ExceptionHandler(JsonEOFException.class)
    private ResponseEntity<Rfc9457Error> handleJsonEOFException(final JsonEOFException ex) {
        return errorService.handleBodySyntaxViolations("Not well-formed for the JSON end. Missing brace?");
    }

    /**
     * The implementation of an {@link ExceptionHandler} in case a {@link JsonParseException} is thrown.
     * <p>
     * Example output:
     * 
     * <pre>
    {
      "type": "/petstore/petservice/v1/pets",
      "title": "JSON Parse Error",
      "instance": "urn:ERROR:bdbbc818-2c50-4bdf-a212-ff803fb430ab",
      "detail": "Unexpected character ('\"' (code 34)): was expecting comma to separate Object entries at line 3, column 3"
    }
     * </pre>
     * 
     * @param ex the {@link Exception} object for handling, never {@code null}
     * 
     * @return the {@link ResponseEntity} including an {@link Rfc9457Error} body
     */
    @ExceptionHandler(JsonParseException.class)
    private ResponseEntity<Rfc9457Error> handleJsonParseException(final JsonParseException ex) {
        final JsonLocation location = ex.getLocation();

        final String detail = ex.getOriginalMessage() + " at line " + location.getLineNr() + ", column "
                + location.getColumnNr();

        final Rfc9457Error error = errorService.finalizeRfc9457Error("JSON Parse Error", detail);

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }

    /**
     * Creates a response entity for an {@link Rfc9457Error} body in case of semantic violations in the JSON request
     * body. Sets the response status to {@link HttpStatus#UNPROCESSABLE_ENTITY}.
     * 
     * @param ex the {@link Exception} for creating the semantic response with code {@code 422}. Must not be
     *           {@code null}.
     * 
     * @return the {@link ResponseEntity} object, never {@code null}
     */
    private ResponseEntity<Rfc9457Error> createSemanticResponse(final MismatchedInputException ex) {
        final List<String> invalidParamNameParts = ex.getPath().stream().map(Reference::getFieldName)
                .filter(Objects::nonNull).toList();

        final String invalidParamName = String.join(".", invalidParamNameParts);
        final String reasonString = ErrorService.removePackageInformation(ex.getOriginalMessage()).trim();
        final String reason = reasonString + " (line " + ex.getLocation().getLineNr() + ", column "
                + ex.getLocation().getColumnNr() + ")";

        final InvalidParam invalidParam = new InvalidParam("#/" + invalidParamName, reason);

        final Rfc9457Error error = errorService.finalizeRfc9457Error("Request body validation failed",
                List.of(invalidParam));

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }
}
