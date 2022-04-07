package de.training.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.exception.service.ErrorService;
import de.training.model.Error;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link HttpMediaTypeNotSupportedException}. Ensures the response code {@code 415} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "Content type 'application/atom+xml' not supported",
    "instance": "urn:ERROR:aaf1da57-6415-4e6d-b9b1-d5969f052bc2",
    "detail": "Supported media type(s): [application/json]"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2022-02-16
 * @version 1.1
 * @see <a href="https://github.com/spring-projects/spring-framework/issues/28062">HttpMediaTypeNotSupportedException
 *      getSupportedMediaTypes() fails for unknown values in Content-Type header</a>
 */
@Order(0)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RestControllerAdvice
class HttpMediaTypeNotSupportedExceptionHandler {

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
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	private ResponseEntity<Error> handleException(final HttpMediaTypeNotSupportedException ex) {
		final String msg = ex.getLocalizedMessage();
		final String title = msg.contains("''") ? "Request header 'content-type' not found" : msg;

		final Error error = errorService.finalizeRfc7807Error(title,
				"Supported media type(s): " + ex.getSupportedMediaTypes());

		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.body(error);
	}
}
