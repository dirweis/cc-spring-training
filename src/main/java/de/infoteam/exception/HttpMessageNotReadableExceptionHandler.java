package de.infoteam.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.infoteam.exception.jsonerror.AbstractJsonErrorHandler;
import de.infoteam.exception.jsonerror.JsonErrorFactory;
import de.infoteam.model.Error;
import de.infoteam.service.ErrorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link HttpMessageNotReadableException}. Ensures the response code {@code 400} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets",
    "title": "JSON parse error",
    "instance": "urn:ERROR:0125084f-d9e8-49fe-8c03-64de5b771b57",
    "detail": "Unexpected end-of-input: expected close marker for Object at [Source: line: 14, column: 1]"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 1.0
 * @see JsonErrorFactory
 *
 */
@Order(1)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RestControllerAdvice
class HttpMessageNotReadableExceptionHandler {

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
	@ExceptionHandler(HttpMessageNotReadableException.class)
	private ResponseEntity<Error> handleException(final HttpMessageNotReadableException ex) {
		final AbstractJsonErrorHandler handler = JsonErrorFactory.getErrorHandler(ex.getMostSpecificCause(),
				errorService);

		return handler.createResponse();
	}
}
