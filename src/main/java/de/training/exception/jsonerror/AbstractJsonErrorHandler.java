package de.training.exception.jsonerror;

import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import de.training.model.Error;
import de.training.service.ErrorService;

/**
 * An abstract class as super implementation for JSON violations in the request body.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.0
 *
 */
public abstract class AbstractJsonErrorHandler {

	private static final Pattern doubleSpacePattern = Pattern.compile("\\p{IsSpace}{2}");

	/**
	 * The error's unique Id.
	 */
	protected final UUID errorId = UUID.randomUUID();

	/**
	 * Creates a complete {@link ResponseEntity} object with an {@link Error} object as body and a fitting
	 * {@link HttpStatus}.
	 * 
	 * @return the finalized {@link ResponseEntity}, never {@code null}
	 */
	public abstract ResponseEntity<Error> createResponse();

	/**
	 * Builds the whole {@link ResponseEntity} for all syntactical violations.
	 * 
	 * @param exMsg        the {@link Exception} message, may not be {@code null}
	 * @param errorService the {@link ErrorService} object, may not be {@code null}
	 * 
	 * @return the object as service error response, never {@code null}
	 */
	protected static ResponseEntity<Error> handleSyntaxViolations(final String exMsg, final ErrorService errorService) {
		final String preparedDetail = ErrorService.removePackageInformation(exMsg);
		final String detail = cleanExMsg(preparedDetail);

		final Error error = errorService.finalizeRfc7807Error("JSON Parse Error", detail);

		return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
	}

	/**
	 * Clean-up of the exception message: Removing implementation-specific information and ugly characters.
	 * 
	 * @param exMsg the original exception message, may not be {@code null}
	 * 
	 * @return the clean {@link String}, never {@code null}
	 */
	protected static String cleanExMsg(final String exMsg) {
		final String intermediate = RegExUtils.removeAll(exMsg, "\\((\\p{IsUpper}.*?)\\)");

		return doubleSpacePattern.matcher(RegExUtils.removeAll(intermediate, "(; |\\n)")).replaceAll(StringUtils.SPACE);
	}
}
