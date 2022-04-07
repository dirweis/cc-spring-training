package de.training.exception.jsonerror;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.io.JsonEOFException;

import de.training.model.Error;
import de.training.service.ErrorService;
import lombok.AllArgsConstructor;

/**
 * The implementation of {@link AbstractJsonErrorHandler} for syntactical JSON violations with missing closing braces.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "JSON Parse Error",
    "instance": "urn:ERROR:bc438273-070b-47e9-9f9b-fc4663f77e31",
    "detail": "Unexpected end-of-input: expected close marker for Object (start marker at [Source: line: 1, column: 1]) at [Source: line: 11, column: 1]"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.0
 *
 */
@AllArgsConstructor
class JsonEofErrorHandler extends AbstractJsonErrorHandler {

	private final JsonEOFException ex;

	private final ErrorService errorService;

	/**
	 * {@inheritDoc}
	 * <p>
	 * In this case for syntactical EOF violations (missing closing brace).
	 */
	@Override
	public ResponseEntity<Error> createResponse() {
		return handleSyntaxViolations(ex.getLocalizedMessage(), errorService);
	}
}
