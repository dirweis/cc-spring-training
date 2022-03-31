package de.infoteam.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.infoteam.model.Error;
import de.infoteam.service.ErrorService;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link MissingServletRequestParameterException}. Ensures the response code {@code 400} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "type": "/petstore/petservice/v1/pets/findByStatus",
    "title": "Missing query parameter",
    "instance": "urn:ERROR:604035db-1e80-4b08-a04a-bc67e9a54fa2",
    "detail": "Required request parameter 'status' for method parameter type PetStatus is not present"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2021-11-02
 * @version 1.0
 *
 */
@Order(5)
@RestControllerAdvice
class MissingServletRequestParameterExceptionHandler {

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
	@ExceptionHandler(MissingServletRequestParameterException.class)
	private ResponseEntity<Error> handleException(final MissingServletRequestParameterException ex) {
		final Error error = errorService.finalizeRfc7807Error("Missing query parameter", ex.getLocalizedMessage());

		return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
	}
}
