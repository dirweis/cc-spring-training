package de.infoteam.exception.constraint.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A little helper for comparing the data type in the given body and the one in the request header {@code Content-Type}.
 * 
 * @since 2022-03-29
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TikaTypeCheckValidator implements ConstraintValidator<TikaTypeCheck, byte[]> {

	private static final Tika tikaChecker = new Tika();

	@Autowired
	private HttpServletRequest request;

	private String message;

	/**
	 * {@inheritDoc}
	 * <p>
	 * In this case the message gets set.
	 */
	@Override
	public void initialize(final TikaTypeCheck constraintAnnotation) {
		message = constraintAnnotation.message();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * In this case the {@link Tika} object is used for checking on the content's type.
	 */
	@Override
	public boolean isValid(final byte[] body, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode("body").addConstraintViolation();

		return request.getContentType().equals(tikaChecker.detect(body));
	}
}
