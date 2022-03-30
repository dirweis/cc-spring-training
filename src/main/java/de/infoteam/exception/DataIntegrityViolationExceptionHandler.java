package de.infoteam.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.infoteam.exception.service.ErrorService;
import de.infoteam.model.Error;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link DataIntegrityViolationException}. Ensures the response code {@code 409} is returned in case of a document /
 * image that is already stored.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets/9218aaa7-a9cd-408b-ad2e-9c591b46fc5a/image",
    "title": "Entry already exists",
    "instance": "urn:ERROR:00e51fbb-813d-461f-a275-cb3ff6b98447",
    "detail": "FEHLER: doppelter Schlüsselwert verletzt"
 }
 * </pre>
 * 
 * @since 2022-03-21
 * @version 1.1
 * @author Dirk Weissmann
 *
 */
@RestControllerAdvice
@Order(7)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
class DataIntegrityViolationExceptionHandler {

	@Autowired
	private ErrorService errorService;

	/**
	 * Catches the defined {@link Exception}s and creates an {@link Error} response body.
	 * 
	 * @param ex the {@link Exception} to catch, never {@code null}
	 * 
	 * @return the created {@link ResponseEntity} as response, never {@code null}
	 * 
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	private ResponseEntity<Error> handleException(final DataIntegrityViolationException ex) {
		log.info("Possible null values: Exception: {}", ex);
		log.info("Possible null values: Exception.getMostSpecificCause(): {}", ex.getMostSpecificCause());
		log.info("Possible null values: Exception.getLocalizedMessage(): {}", ex.getLocalizedMessage());
		log.info("Possible null values: Exception.getMostSpecificCause().getLocalizedMessage(): {}",
				ex.getMostSpecificCause().getLocalizedMessage());

		final String rawMessage = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getLocalizedMessage()
				: ex.getLocalizedMessage();

		if (rawMessage != null && !rawMessage.contains("Unique")) {
			return errorService.create500Response(ex);
		}

		return create409Response(rawMessage);
	}

	/**
	 * Creates a conflict (code {@code 409}) {@link ResponseEntity}.
	 * 
	 * @param rawMessage the raw detail {@link String}, never {@code null}
	 * 
	 * @return the {@link ResponseEntity} object with code {@code 409}
	 */
	private ResponseEntity<Error> create409Response(final String rawMessage) {
		final String detail = null == rawMessage ? null : rawMessage.substring(0, rawMessage.indexOf("Unique") - 1);

		final Error error = errorService.finalizeRfc7807Error("Entry already exists", detail);

		return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
	}
}
