package de.training.exception.constraint.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.training.model.Pet;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * A very special validating annotation: Validates the {@code photo-urls} field of the {@link Pet} resource on
 * {@code null} for ensuring the URLs get inserted only with inserting the images.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-24
 * @version 1.0
 *
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = PhotoUrlsNullValidator.class)
public @interface PhotoUrlsNull {

    /**
     * @return the default message: an empty {@link String}
     */
    String message() default "POST request: The field pet.photo-urls must be null";

    /**
     * The mandatory groups field.
     * 
     * @return empty list
     */
    Class<?>[] groups() default {};

    /**
     * The mandatory payload field.
     * 
     * @return empty list
     */
    Class<? extends Payload>[] payload() default {};
}
