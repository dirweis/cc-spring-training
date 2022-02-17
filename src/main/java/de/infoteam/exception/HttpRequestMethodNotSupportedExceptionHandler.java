package de.infoteam.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.infoteam.model.Error;
import de.infoteam.service.ErrorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link HttpRequestMethodNotSupportedException}. Ensures the response code {@code 405} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 * {
 *	"type": "/petstore/petservice/v1/pets",
 *	"title": "Request method 'GET' not supported",
 *	"instance": "c8ae7b1e-e122-4c68-8b6d-2c6823655af0"
 * }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2021-10-22
 * @version 1.0
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HttpRequestMethodNotSupportedExceptionHandler {

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
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	private ResponseEntity<Error> handleException(final HttpRequestMethodNotSupportedException ex) {
		final Error error = errorService.finalizeRfc7807Error(ex.getLocalizedMessage(),
				"Supported method(s): " + ex.getSupportedHttpMethods(), null);

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(ErrorService.provideProblemJsonHeader())
				.body(error);
	}
}
