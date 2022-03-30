package de.infoteam.exception.constraint.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * A validation for ensuring that the Content-Type header matches the content type detected in the content (the binary
 * request body as byte array).
 * 
 * @author Dirk Weissmann
 * @since 2022-03-29
 * @version 1.0
 *
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
@Constraint(validatedBy = TikaTypeCheckValidator.class)
public @interface TikaTypeCheck {

	/**
	 * @return the default message: an empty {@link String}
	 */
	String message() default "The content type given in the request header does not match the content information";

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
