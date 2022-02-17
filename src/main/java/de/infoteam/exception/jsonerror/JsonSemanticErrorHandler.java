package de.infoteam.exception.jsonerror;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.infoteam.model.Error;
import de.infoteam.service.ErrorService;
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
	public ResponseEntity<Error> createResponse() {
		final String detail = ErrorService.removePackageInformation(originalMsg);

		final int firstColonOffset = detail.indexOf(':');

		final Error error = errorService.finalizeRfc7807Error("Request body validation failed",
				detail.substring(firstColonOffset, detail.indexOf(':', firstColonOffset + 1)), null);

		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).headers(ErrorService.provideProblemJsonHeader())
				.body(error);
	}
}
