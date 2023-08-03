package de.training.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.Payload;

/**
 * The {@code multipleOf} annotation as a {@link ConstraintValidator}.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.0
 *
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = MultipleOfValidator.class)
public @interface MultipleOf {

    /**
     * The annotation's value field.
     * 
     * @return the given value in the annotation
     */
    int value();

    /**
     * The annotation's error message, gets set (overwritten) in the implementation.
     * <p>
     * <em>Don't override the message field! The message gets overwritten in the {@link PetIdNullValidator}
     * anyways.</em>
     * 
     * @return the default error message: the empty {@link String}
     * 
     * @see MultipleOfValidator#isValid
     */
    String message() default StringUtils.EMPTY;

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
