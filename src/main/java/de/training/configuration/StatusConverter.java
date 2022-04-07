package de.training.configuration;

import java.util.Locale;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import de.training.model.Pet.PetStatus;

/**
 * Minimalist converter bean for processing {@link PetStatus} values in parameters (path / query) case insensitively.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.0
 *
 */
@Configuration
class StatusConverter implements Converter<String, PetStatus> {

	/**
	 * {@inheritDoc}
	 * <p>
	 * In this case convert a given {@link String} case insensitive into a value for the enumeration {@link PetStatus}.
	 */
	@Override
	public PetStatus convert(final String source) {
		return PetStatus.valueOf(source.toUpperCase(Locale.getDefault()));
	}
}
