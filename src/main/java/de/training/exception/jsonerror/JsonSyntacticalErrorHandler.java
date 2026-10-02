package de.training.exception.jsonerror;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import de.training.model.Rfc9457Error;
import de.training.service.ErrorService;
import lombok.AllArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.core.TokenStreamLocation;

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
 * @version 2.0
 *
 */
@AllArgsConstructor
class JsonSyntacticalErrorHandler extends AbstractJsonErrorHandler {

    private final JacksonException ex;

    private final ErrorService errorService;

    /**
     * {@inheritDoc}
     * <p>
     * In this case for syntactical violations.
     */
    @Override
    public ResponseEntity<Rfc9457Error> createResponse() {
        final TokenStreamLocation location = ex.getLocation();

        final String detail = ex.getOriginalMessage() + " at line " + location.getLineNr() + ", column "
                + location.getColumnNr();

        final Rfc9457Error error = errorService.finalizeRfc9457Error("JSON Parse Error", detail);

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }
}
