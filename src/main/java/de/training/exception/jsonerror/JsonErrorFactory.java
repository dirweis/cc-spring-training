package de.training.exception.jsonerror;

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
 * @version 2.1
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

        return switch (ex) {
        case final MismatchedInputException mex -> new JsonMismatchHandler(mex, errorService);
        case final UnexpectedEndOfInputException _ -> new JsonEofErrorHandler(errorService);
        case final JacksonException jex -> new JsonSyntacticalErrorHandler(jex, errorService);
        default -> new JsonNotReadableErrorHandler((HttpMessageNotReadableException) ex, errorService);
        };
    }
}
