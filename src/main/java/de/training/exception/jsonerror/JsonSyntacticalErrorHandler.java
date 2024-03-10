package de.training.exception.jsonerror;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;

import de.training.model.Rfc9457Error;
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
    "instance": "urn:ERROR:8836b156-3a1d-41d8-a6c0-ad0f252732ea",
    "detail": "Unexpected character ('n' (code 110)): was expecting double-quote to start field name at [Source: line: 4, column: 6]"
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
    public ResponseEntity<Rfc9457Error> createResponse() {
        return handleSyntaxViolations(ex.getLocalizedMessage(), errorService);
    }
}
