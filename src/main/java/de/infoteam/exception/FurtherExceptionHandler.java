package de.infoteam.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.infoteam.model.Error;
import de.infoteam.service.ErrorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught further
 * exception that is not caught explicitly by another handler. Ensures the response code {@code 500} is returned.
 * <p>
 * <em>Must not occur in the productive area! Whenever this {@link ExceptionHandler} fires, there is definitely
 * something wrong with the implementation or with the infrastructure!</em>
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "Internal problem. Please contact the support.",
    "instance": "urn:ERROR:d4f78c67-b4a8-4f3c-9322-9312dba6c8e2"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.1
 *
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
class FurtherExceptionHandler {

	@Autowired
	private ErrorService errorService;

	/**
	 * Catches the defined {@link Exception}s and creates an {@link Error} response body.
	 * 
	 * @param ex the {@link Exception} to catch, never {@code null}
	 * 
	 * @return the created {@link Error} object as response body, never {@code null}
	 * 
	 */
	@ExceptionHandler(Throwable.class)
	private ResponseEntity<Error> handleException(final Throwable ex) {
		final Error error = errorService.finalizeRfc7807Error("Internal problem. Please contact the support.", null,
				null);

		log.error("Internal Error Stack trace", ex);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.body(error);
	}
}
