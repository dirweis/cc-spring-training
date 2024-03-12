package de.training.exception.service;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.training.model.Rfc9457Error;
import de.training.model.Rfc9457Error.InvalidParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Out-sourcing error handling logic for common use.
 * 
 * @author Dirk Weissmann
 * @since 2021-10-25
 * @version 1.0
 *
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ErrorService {

    private static final Pattern doubleSpacePattern = Pattern.compile("\\p{IsSpace}{2}");

    private final HttpServletRequest request;

    /**
     * Prepares the {@link Rfc9457Error} object with commonly used values and a specific title.
     * 
     * @param title the error's title, must not be {@code null}
     * 
     * @return the final {@link Rfc9457Error}, never {@code null}
     */
    public Rfc9457Error finalizeRfc9457Error(final String title) {
        return finalizeRfc9457Error(title, null, null);
    }

    /**
     * Prepares the {@link Rfc9457Error} object with commonly used values and a specific title and detail information.
     * 
     * @param title  the error's title, must not be {@code null}
     * @param detail the error's {@code detail} field's value, never {@code null}
     * 
     * @return the final {@link Rfc9457Error}, never {@code null}
     */
    public Rfc9457Error finalizeRfc9457Error(final String title, final String detail) {
        return finalizeRfc9457Error(title, detail, null);
    }

    /**
     * Prepares the {@link Rfc9457Error} object with commonly used values and a specific title.
     * 
     * @param title         the error's title, must not be {@code null}
     * @param invalidParams the error's {@code invalid_params} field's value, never {@code null}
     * 
     * @return the final {@link Rfc9457Error}, never {@code null}
     */
    public Rfc9457Error finalizeRfc9457Error(final String title, final List<InvalidParam> invalidParams) {
        return finalizeRfc9457Error(title, null, invalidParams);
    }

    /**
     * Prepares the {@link Rfc9457Error} object with commonly used values.
     * 
     * @param title         the error's title, must not be {@code null}
     * @param detail        the error's detailed description, may be {@code null}
     * @param invalidParams special field for parameter violations, may be {@code null}
     * 
     * @return the final {@link Rfc9457Error}, never {@code null}
     */
    private Rfc9457Error finalizeRfc9457Error(final String title, final String detail,
            @Valid final List<InvalidParam> invalidParams) {
        final UUID errorId = UUID.randomUUID();

        ErrorService.log.warn("Problems in request. ID: {}", errorId);

        return Rfc9457Error.builder().type(URI.create(request.getRequestURI())).title(title)
                .instance(URI.create("urn:ERROR:" + errorId)).detail(detail).errors(invalidParams).build();
    }

    /**
     * Removes the Java package information from the error response body (fields {@code title} and {@code description}).
     * 
     * @param errorMessage the plain exception localized message
     * 
     * @return the 'cleaned' {@link String}
     */
    public static String removePackageInformation(final String errorMessage) {
        return RegExUtils.removeAll(errorMessage, "(de|com|org|io|net|\\@?javax?)(\\.\\p{IsLower}{2,20}){1,10}\\.");
    }

    /**
     * Clean-up of the exception message: Removing implementation-specific information and ugly characters.
     * 
     * @param exMsg the original exception message, may not be {@code null}
     * 
     * @return the clean {@link String}, never {@code null}
     */
    public static String cleanExMsg(final String exMsg) {
        final String intermediate = RegExUtils.removeAll(exMsg, "\\((\\p{IsUpper}.*?)\\)");

        return ErrorService.doubleSpacePattern.matcher(RegExUtils.removeAll(intermediate, "(; |\\n)"))
                .replaceAll(StringUtils.SPACE);
    }

    /**
     * Builds the whole {@link ResponseEntity} for all syntactical violations.
     * 
     * @param exMsg the {@link Exception} message, may not be {@code null}
     * 
     * @return the object as service error response, never {@code null}
     */
    public ResponseEntity<Rfc9457Error> handleBodySyntaxViolations(final String exMsg) {
        final String preparedDetail = ErrorService.removePackageInformation(exMsg);
        final String detail = ErrorService.cleanExMsg(preparedDetail);

        final Rfc9457Error error = finalizeRfc9457Error("JSON Parse Error", detail, null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(error);
    }
}
