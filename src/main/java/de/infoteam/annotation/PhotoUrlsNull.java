package de.infoteam.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import de.infoteam.model.Pet;

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
@Retention(RUNTIME)
@Target(PARAMETER)
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
