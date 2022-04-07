package de.training.configuration;

import java.util.Locale;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import de.training.model.Pet.Category;

/**
 * Minimalist converter bean for processing {@link Category} values in parameters (path / query) case insensitively.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-17
 * @version 1.0
 *
 */
@Configuration
class CategoryConverter implements Converter<String, Category> {

	/**
	 * {@inheritDoc}
	 * <p>
	 * In this case convert a given {@link String} case insensitive into a value for the enumeration {@link Category}.
	 */
	@Override
	public Category convert(final String source) {
		return Category.valueOf(source.toUpperCase(Locale.getDefault()));
	}
}
