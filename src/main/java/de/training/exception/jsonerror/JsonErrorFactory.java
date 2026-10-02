package de.training.exception.jsonerror;

import java.net.MalformedURLException;

import org.springframework.http.converter.HttpMessageNotReadableException;

import de.training.service.ErrorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.UnexpectedEndOfInputException;
import tools.jackson.databind.exc.MismatchedInputException;

/**
 * A little factory for treating various validations in the JSON request body.
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonErrorFactory {

    /**
     * All that is implemented here: Get a sub of {@link AbstractJsonErrorHandler}.
     * 
     * @param ex           the exception to specify the specific error handler, must not be {@code null}
     * @param errorService the {@link ErrorService} object, must not be {@code null}
     * @param originalMsg  the original {@link HttpMessageNotReadableException} message, may be {@code null}
     * 
     * @return the equivalent handler
     */
    public static AbstractJsonErrorHandler getErrorHandler(final Throwable ex, final ErrorService errorService,
            final String originalMsg) {

        if (ex instanceof final MismatchedInputException mex) {
            return new JsonMismatchHandler(mex, errorService);
        }

        if (ex instanceof UnexpectedEndOfInputException) {
            return new JsonEofErrorHandler(errorService);
        }

        if (ex instanceof final JacksonException jex) {
            return new JsonSyntacticalErrorHandler(jex, errorService);
        }

        if (ex instanceof MalformedURLException) {
            return new JsonSemanticErrorHandler(errorService, originalMsg);
        }

        return new JsonNotReadableErrorHandler((HttpMessageNotReadableException) ex, errorService);
    }
}
