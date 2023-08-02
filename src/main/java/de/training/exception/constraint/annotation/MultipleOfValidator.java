package de.training.exception.constraint.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Validates a {@link MultipleOf}.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MultipleOfValidator implements ConstraintValidator<MultipleOf, Integer> {

    private int value4Validation;

    /**
     * {@inheritDoc}
     * <p>
     * In this case the integer field {@link MultipleOf#value()} gets stored into the class member.
     */
    @Override
    public void initialize(final MultipleOf constraintAnnotation) {
        value4Validation = constraintAnnotation.value();
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this case the given parameter's value gets divided by modulo and the result must be {@code 0}.
     */
    @Override
    public boolean isValid(final Integer paramValue, final ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(paramValue + " is not a multiple of " + value4Validation)
                .addConstraintViolation();

        return paramValue % value4Validation == 0;
    }
}
