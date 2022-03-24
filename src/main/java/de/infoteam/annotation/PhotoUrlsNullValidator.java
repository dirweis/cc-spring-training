package de.infoteam.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;

import de.infoteam.model.Pet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The very special validator checking the {@link Pet#photoUrls()} field on {@code null} in case of {@code POST} or
 * {@code PUT} requests.
 * 
 * @author Dirk Weissmann
 * @since 2022-03-24
 * @version 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PhotoUrlsNullValidator implements ConstraintValidator<PhotoUrlsNull, Pet> {

	private String message;

	/**
	 * {@inheritDoc}
	 * <p>
	 * Fetches the field {@link PetIdNull#message()} for creating a customized {@link ConstraintViolation}.
	 */
	@Override
	public void initialize(final PhotoUrlsNull constraintAnnotation) {
		message = constraintAnnotation.message();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Hereby the {@link Pet#photoUrls()} field is checked on {@code null}.
	 */
	@Override
	public boolean isValid(final Pet pet, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode("body").addPropertyNode("photo-urls")
				.addConstraintViolation();

		return pet.photoUrls() == null;
	}
}
