package de.training.exception.jsonerror;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import de.training.model.Rfc9457Error;
import de.training.service.ErrorService;
import lombok.AllArgsConstructor;

/**
 * The implementation of {@link AbstractJsonErrorHandler} for semantic JSON violations.
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.0
 *
 */
@AllArgsConstructor
class JsonSemanticErrorHandler extends AbstractJsonErrorHandler {

    private final ErrorService errorService;

    private final String originalMsg;

    /**
     * {@inheritDoc}
     * <p>
     * In this case for semantic violations.
     */
    @Override
    public ResponseEntity<Rfc9457Error> createResponse() {
        final String detail = ErrorService.removePackageInformation(originalMsg);

        final int firstColonOffset = detail.indexOf(':');

        final Rfc9457Error error = errorService.finalizeRfc9457Error("Request body validation failed",
                detail.substring(firstColonOffset, detail.indexOf(':', firstColonOffset + 1)));

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
    }
}
