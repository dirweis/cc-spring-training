package de.training.exception;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.training.exception.service.ErrorService;
import de.training.model.Error;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link EntityNotFoundException} that gets thrown internally if a database entry could not be found. Ensures the
 * response code {@code 404} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
  {
    "type": "/petstore/petservice/v1/pets/8576286c-dc4c-46f8-aef5-287d89da741",
    "title": "Not found",
    "instance": "urn:ERROR:902704d7-ba3a-4007-a516-5ea0f0378b26",
    "detail": "Resource with ID 8576286c-dc4c-46f8-aef5-0287d89da741 not found in the persistence"
  }
 * </pre>
 * 
 * @since 2022-03-15
 * @version 1.1
 * @author Dirk Weissmann
 *
 */
@RestControllerAdvice
@Order(6)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class EntityNotFoundExceptionHandler {

	@Autowired
	private ErrorService errorService;

	/**
	 * Catches the defined {@link Exception}s and creates an {@link Error} response body.
	 * 
	 * @param ex the {@link Exception} to catch, never {@code null}
	 * 
	 * @return the created {@link Error} object as response body, never {@code null}
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	private ResponseEntity<Error> handleException(final EntityNotFoundException ex) {
		final Error error = errorService.finalizeRfc7807Error("Not found", ex.getLocalizedMessage());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
	}
}
