package de.infoteam.exception;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.infoteam.model.Error;
import de.infoteam.model.Error.InvalidParam;
import de.infoteam.service.ErrorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The {@link ExceptionHandler} implementation for creating {@link Error} response bodies in case of a caught
 * {@link MethodArgumentTypeMismatchException}. Ensures the response code {@code 400} is returned.
 * <p>
 * Example output:
 * 
 * <pre>
 {
    "invalid_params": [
        {
            "name": "petId",
            "reason": "Invalid UUID string: 1"
        }
    ],
    "type": "/petstore/petservice/v1/pets/1",
    "title": "Failed to convert value of type 'String' to required type 'UUID'",
    "instance": "urn:ERROR:c252c6f9-f605-4157-ab77-392065f05268"
 }
 * </pre>
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.1
 *
 */
@Order(3)
@RestControllerAdvice
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MethodArgumentTypeMismatchExceptionHandler {

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
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	private ResponseEntity<Error> handleException(final MethodArgumentTypeMismatchException ex) {
		final String errorMsg = ErrorService.removePackageInformation(ex.getLocalizedMessage());

		final String title = errorMsg.substring(0, errorMsg.indexOf(';'));
		final String name = ex.getName();
		final String reasonUncut = errorMsg.substring(errorMsg.indexOf(':') + 2);
		final String reason = reasonUncut.contains("; ") ? reasonUncut.substring(0, reasonUncut.indexOf(';') + 1)
				: reasonUncut;

		final List<InvalidParam> invalidParams = List.of(InvalidParam.builder().name(name).reason(reason).build());

		final Error error = errorService.finalizeRfc7807Error(title, invalidParams);

		return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(error);
	}
}
