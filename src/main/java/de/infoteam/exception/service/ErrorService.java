package de.infoteam.exception.service;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.infoteam.model.Error;
import de.infoteam.model.Error.InvalidParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Out-sourcing error handling logic for common use.
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.5
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ErrorService {

	private static final Pattern doubleSpacePattern = Pattern.compile("\\p{IsSpace}{2}");

	private final HttpServletRequest request;

	/**
	 * Prepares the {@link Error} object with the title.
	 * 
	 * @param title the error's title, must not be {@code null}
	 * 
	 * @return the final {@link Error}, never {@code null}
	 */
	public Error finalizeRfc7807Error(final String title) {
		return finalizeRfc7807Error(title, null, null);
	}

	/**
	 * Prepares the {@link Error} object with the title and the detail field.
	 * 
	 * @param title  the error's title, must not be {@code null}
	 * @param detail the error's detailed description, must not be {@code null}
	 * 
	 * @return the final {@link Error}, never {@code null}
	 */
	public Error finalizeRfc7807Error(final String title, final String detail) {
		return finalizeRfc7807Error(title, detail, null);
	}

	/**
	 * Prepares the {@link Error} object with the title and the {@code invalid_params} field.
	 * 
	 * @param title         the error's title, must not be {@code null}
	 * @param invalidParams special field for parameter violations, must not be {@code null}
	 * 
	 * @return the final {@link Error}, never {@code null}
	 */
	public Error finalizeRfc7807Error(final String title, final List<InvalidParam> invalidParams) {
		return finalizeRfc7807Error(title, null, invalidParams);
	}

	/**
	 * Prepares the {@link Error} object with commonly used values.
	 * 
	 * @param title         the error's title, must not be {@code null}
	 * @param detail        the error's detailed description, may be {@code null}
	 * @param invalidParams special field for parameter violations, may be {@code null}
	 * 
	 * @return the final {@link Error}, never {@code null}
	 */
	private Error finalizeRfc7807Error(final String title, final String detail,
			@Valid final List<InvalidParam> invalidParams) {
		final UUID errorId = UUID.randomUUID();

		log.warn("Problems in request. ID: {}", errorId);

		return Error.builder().type(URI.create(request.getRequestURI())).title(title)
				.instance(URI.create("urn:ERROR:" + errorId)).detail(detail).invalidParams(invalidParams).build();
	}

	/**
	 * Removes the Java package information from the error response body (fields {@code title} and {@code description}).
	 * 
	 * @param errorMessage the plain exception localized message
	 * 
	 * @return the 'cleaned' {@link String}
	 */
	public static String removePackageInformation(final String errorMessage) {
		return RegExUtils.removeAll(errorMessage, "(de|com|org|io|net|\\@?javax?)(\\.\\p{IsLower}{2,20}){1,10}\\.");
	}

	/**
	 * Clean-up of the exception message: Removing implementation-specific information and ugly characters.
	 * 
	 * @param exMsg the original exception message, may not be {@code null}
	 * 
	 * @return the clean {@link String}, never {@code null}
	 */
	public static String cleanExMsg(final String exMsg) {
		final String intermediate = RegExUtils.removeAll(exMsg, "\\((\\p{IsUpper}.*?)\\)");

		return doubleSpacePattern.matcher(RegExUtils.removeAll(intermediate, "(; |\\n)")).replaceAll(StringUtils.SPACE);
	}

	/**
	 * Builds the whole {@link ResponseEntity} for all syntactical violations.
	 * 
	 * @param exMsg the {@link Exception} message, may not be {@code null}
	 * 
	 * @return the object as service error response, never {@code null}
	 */
	public ResponseEntity<Error> handleBodySyntaxViolations(final String exMsg) {
		final String preparedDetail = removePackageInformation(exMsg);
		final String detail = cleanExMsg(preparedDetail);

		final Error error = finalizeRfc7807Error("JSON Parse Error", detail, null);

		return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
	}

	/**
	 * Creates the {@code 500} Internal Server Error response.
	 * 
	 * @param ex the exception to be logged on {@code Error} level, must not be {@code null}
	 * 
	 * @return the created {@link ResponseEntity}, never {@code null}
	 */
	public ResponseEntity<Error> create500Response(final Throwable ex) {
		log.error(ex);

		ex.printStackTrace();

		final Error error = finalizeRfc7807Error("Internal problem. Please contact the support.");

		return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
	}
}
