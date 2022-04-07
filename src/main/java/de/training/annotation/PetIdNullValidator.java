package de.training.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;

import de.training.model.Pet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The very special validator checking the {@link Pet#id()} field on {@code null} in case of {@code POST} requests.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.1
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PetIdNullValidator implements ConstraintValidator<PetIdNull, Pet> {

	private String message;

	/**
	 * {@inheritDoc}
	 * <p>
	 * Fetches the field {@link PetIdNull#message()} for creating a customized {@link ConstraintViolation}.
	 */
	@Override
	public void initialize(final PetIdNull constraintAnnotation) {
		message = constraintAnnotation.message();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Hereby the {@link Pet#id()} field is checked on {@code null}.
	 */
	@Override
	public boolean isValid(final Pet pet, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode("body").addPropertyNode("id")
				.addConstraintViolation();

		return pet.id() == null;
	}
}
