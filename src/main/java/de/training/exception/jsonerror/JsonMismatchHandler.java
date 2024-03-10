package de.training.exception.jsonerror;

import java.util.List;

import org.apache.commons.lang3.RegExUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import de.training.model.Rfc9457Error;
import de.training.model.Rfc9457Error.InvalidParam;
import de.training.service.ErrorService;
import lombok.AllArgsConstructor;

/**
 * The implementation of {@link AbstractJsonErrorHandler} for syntactical {@code JSON} mismatches (the {@code JSON}
 * input is not recognized as {@code JSON}).
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "Request body validation failed",
    "instance": "urn:ERROR:f15ee54b-6472-4d22-b02c-cbf63d5b6072",
    "errors": [
        {
            "pointer": "#/category",
            "detail": "Cannot deserialize value of type `Pet$Category` from String \"cast\": not one of the values accepted for Enum class: [BIRD, DOG, MOUSE, CAT, SPIDER] at [Source: (StreamUtils$NonClosingInputStream)line: 2, column: 17] (through reference chain: Pet[\"category\"])"
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
@AllArgsConstructor
class JsonMismatchHandler extends AbstractJsonErrorHandler {

    private final MismatchedInputException ex;

    private final ErrorService errorService;

    /**
     * {@inheritDoc}
     * <p>
     * In this case for syntactical violations.
     */
    @Override
    public ResponseEntity<Rfc9457Error> createResponse() {
        final String errMsg = ex.getLocalizedMessage();

        if (errMsg.startsWith("Cannot deserialize ")) {
            return createSemanticResponse();
        }

        final String detailMsg = ErrorService.removePackageInformation(errMsg);

        return handleSyntaxViolations(detailMsg, errorService);
    }

    /**
     * Creates a response entity for an {@link Error} body in case of semantic violations in the JSON request body. Sets
     * the response status to {@link HttpStatus#UNPROCESSABLE_ENTITY}.
     * 
     * @return the {@link ResponseEntity} object, never {@code null}
     */
    private ResponseEntity<Rfc9457Error> createSemanticResponse() {
        final List<String> invalidParamNameParts = ex.getPath().stream().map(Reference::getFieldName).toList();

        final String invalidParamName = String.join(".", invalidParamNameParts);
        final String reasonString = cleanExMsg(ex.getLocalizedMessage());
        final String rawReason = ErrorService.removePackageInformation(reasonString).trim();
        final String reason = RegExUtils.removeAll(rawReason, "\\([^\\\\)]*+\\)");

        final InvalidParam invalidParam = InvalidParam.builder().pointer("#/" + invalidParamName).detail(reason)
                .build();

        final Rfc9457Error error = errorService.finalizeRfc9457Error("Request body validation failed",
                List.of(invalidParam));

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }
}
