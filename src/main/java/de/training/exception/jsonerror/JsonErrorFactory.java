package de.training.exception.jsonerror;

import org.springframework.http.converter.HttpMessageNotReadableException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import de.training.service.ErrorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A little factory for treating various validations in the JSON request body.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 1.1
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonErrorFactory {

	/**
	 * All that is implemented here: Get a sub of {@link AbstractJsonErrorHandler}.
	 * 
	 * @param ex           the exception to specify the specific error handler, must not be {@code null}
	 * @param errorService the {@link ErrorService} object, must not be {@code null}
	 * 
	 * @return the equivalent handler
	 */
	public static AbstractJsonErrorHandler getErrorHandler(final Throwable ex, final ErrorService errorService) {

		if (ex instanceof final MismatchedInputException mex) {
			return new JsonMismatchHandler(mex, errorService);
		}

		if (ex instanceof final JsonEOFException jex) {
			return new JsonEofErrorHandler(jex, errorService);
		}

		if (ex instanceof final JsonParseException jex) {
			return new JsonSyntacticalErrorHandler(jex, errorService);
		}

		return new JsonNotReadableErrorHandler((HttpMessageNotReadableException) ex, errorService);
	}
}
