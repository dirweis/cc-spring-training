package de.training.exception.jsonerror;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import de.training.model.Error;
import de.training.model.Error.InvalidParam;
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
	public ResponseEntity<Error> createResponse() {
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
	private ResponseEntity<Error> createSemanticResponse() {
		final List<String> invalidParamNameParts = ex.getPath().stream().map(Reference::getFieldName).toList();

		final String invalidParamName = String.join(".", invalidParamNameParts);
		final String reasonString = ErrorService.removePackageInformation(ex.getLocalizedMessage());
		final String reason = cleanExMsg(reasonString).trim();

		final InvalidParam invalidParam = InvalidParam.builder().name(invalidParamName).reason(reason).build();

		final Error error = errorService.finalizeRfc7807Error("Request body validation failed", List.of(invalidParam));

		return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
	}
}
