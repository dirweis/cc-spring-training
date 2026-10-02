package de.training.exception.jsonerror;

import org.springframework.http.ResponseEntity;

import de.training.model.Rfc9457Error;
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
    "detail": "Not well-formed for the JSON end. Missing brace?"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 2.0
 *
 */
@AllArgsConstructor
class JsonEofErrorHandler extends AbstractJsonErrorHandler {

    private final ErrorService errorService;

    /**
     * {@inheritDoc}
     * <p>
     * In this case for syntactical EOF violations (missing closing brace).
     */
    @Override
    public ResponseEntity<Rfc9457Error> createResponse() {
        return handleSyntaxViolations("Not well-formed for the JSON end. Missing brace?", errorService);
    }
}
