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
 * @since 2021-10-25
 * @version 2.0
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

        return switch (ex) {
        case final MismatchedInputException mex -> new JsonMismatchHandler(mex, errorService);
        case final JsonEOFException jex -> new JsonEofErrorHandler(jex, errorService);
        case final JsonParseException jex -> new JsonSyntacticalErrorHandler(jex, errorService);
        default -> new JsonNotReadableErrorHandler((HttpMessageNotReadableException) ex, errorService);
        };
    }
}
