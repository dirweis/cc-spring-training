package de.training.exception.jsonerror;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import de.training.model.Error;
import de.training.service.ErrorService;
import lombok.AllArgsConstructor;

/**
 * The implementation of {@link AbstractJsonErrorHandler} for not readable JSONs. The only use case so far is a
 * completely missing JSON.
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
 * @since 2021-10-25
 * @version 1.0
 *
 */
@AllArgsConstructor
class JsonNotReadableErrorHandler extends AbstractJsonErrorHandler {

	private final HttpMessageNotReadableException ex;

	private final ErrorService errorService;

	/**
	 * {@inheritDoc}
	 * <p>
	 * In this case for syntactical EOF violations (missing closing brace).
	 */
	@Override
	public ResponseEntity<Error> createResponse() {
		final String errorMsg = ex.getLocalizedMessage();

		final String detailInfo = errorMsg.substring(0, errorMsg.indexOf(':'));

		return handleSyntaxViolations(detailInfo, errorService);
	}
}
