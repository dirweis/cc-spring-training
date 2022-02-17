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
 * A very special validating annotation: Validates the {@code id} field of the {@link Pet} resource on {@code null} for
 * ensuring {@code POST} requests' non-idempotency.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.0
 *
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
@Constraint(validatedBy = PetIdNullValidator.class)
public @interface PetIdNull {

	/**
	 * @return the default message: an empty {@link String}
	 */
	String message() default "POST request: The field pet.id must be null";

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
