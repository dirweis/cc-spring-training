package de.infoteam.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.infoteam.model.Error.ErrorBuilder;
import de.infoteam.model.Error.InvalidParam;
import de.infoteam.model.Error.InvalidParam.InvalidParamBuilder;

/**
 * Supplementary tests for the {@link ErrorBuilder}'s {@link ErrorBuilder#toString() toString} method for the sake of
 * code coverage.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 1.0
 *
 */
class ErrorTest {

	/**
	 * Tests the {@code toString} methods of the {@link InvalidParamBuilder} and the {@link ErrorBuilder}.
	 */
	@Test
	void testErrorToString() {
		final InvalidParam.InvalidParamBuilder invalidParamBuilder = InvalidParam.builder().name("invalidParam")
				.reason("4Testing");

		final Error.ErrorBuilder errorBuilder = Error.builder().detail("detail")
				.instance(URI.create("urn:ERROR:9e8b9d4b-e335-4dd7-abe8-7b312eb5a27d"))
				.invalidParams(List.of(invalidParamBuilder.build())).title("title")
				.type(URI.create("/petstore/petservice/v1/pets"));

		assertThat(invalidParamBuilder)
				.hasToString("Error.InvalidParam.InvalidParamBuilder(name=invalidParam, reason=4Testing)");

		assertThat(errorBuilder).hasToString(
				"Error.ErrorBuilder(type=/petstore/petservice/v1/pets, title=title, instance=urn:ERROR:9e8b9d4b-e335-4dd7-abe8-7b312eb5a27d, detail=detail, invalidParams=[InvalidParam[name=invalidParam, reason=4Testing]])");
	}
}
