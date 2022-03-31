package de.infoteam.service;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.RegExUtils;
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
 * @version 1.1
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ErrorService {

	private final HttpServletRequest request;

	/**
	 * Prepares the {@link Error} object with commonly used values and a specific title.
	 * 
	 * @param title the error's title, must not be {@code null}
	 * 
	 * @return the final {@link Error}, never {@code null}
	 */
	public Error finalizeRfc7807Error(final String title) {
		return finalizeRfc7807Error(title, null, null);
	}

	/**
	 * Prepares the {@link Error} object with commonly used values and a specific title and detail information.
	 * 
	 * @param title  the error's title, must not be {@code null}
	 * @param detail the error's {@code detail} field's value, never {@code null}
	 * 
	 * @return the final {@link Error}, never {@code null}
	 */
	public Error finalizeRfc7807Error(final String title, final String detail) {
		return finalizeRfc7807Error(title, detail, null);
	}

	/**
	 * Prepares the {@link Error} object with commonly used values and a specific title.
	 * 
	 * @param title         the error's title, must not be {@code null}
	 * @param invalidParams the error's {@code invalid_params} field's value, never {@code null}
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
}
