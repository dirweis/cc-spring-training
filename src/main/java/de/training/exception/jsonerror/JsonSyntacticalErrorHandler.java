package de.training.exception.jsonerror;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;

import de.training.model.Error;
import de.training.service.ErrorService;
import lombok.AllArgsConstructor;

/**
 * The implementation of {@link AbstractJsonErrorHandler} for syntactical JSON violations.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "JSON Parse Error",
    "instance": "urn:ERROR:707b29df-bc8e-41d5-8d6e-5caca0f20072",
    "detail": "Unexpected character : was expecting double-quote to start field name at [Source: line: 2, column: 6]"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.0
 *
 */
@AllArgsConstructor
class JsonSyntacticalErrorHandler extends AbstractJsonErrorHandler {

	private final JsonParseException ex;

	private final ErrorService errorService;

	/**
	 * {@inheritDoc}
	 * <p>
	 * In this case for syntactical violations.
	 */
	@Override
	public ResponseEntity<Error> createResponse() {
		return handleSyntaxViolations(ex.getLocalizedMessage(), errorService);
	}
}
