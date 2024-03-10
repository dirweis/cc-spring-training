package de.training.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.training.model.Rfc9457Error.InvalidParam;
import de.training.model.Rfc9457Error.InvalidParam.InvalidParamBuilder;
import de.training.model.Rfc9457Error.Rfc9457ErrorBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Supplementary tests for the {@link Rfc9457ErrorBuilder}'s {@link Rfc9457ErrorBuilder#toString() toString} method for
 * the sake of code coverage.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-22
 * @version 1.1
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Rfc9457ErrorTest {

    /**
     * Tests the {@code toString} methods of the {@link InvalidParamBuilder} and the {@link Rfc9457ErrorBuilder}.
     */
    @Test
    void testErrorToString() {
        final InvalidParam.InvalidParamBuilder invalidParamBuilder = InvalidParam.builder().pointer("#/invalidParam")
                .detail("4Testing");

        final Rfc9457Error.Rfc9457ErrorBuilder errorBuilder = Rfc9457Error.builder().detail("detail")
                .instance(URI.create("urn:ERROR:9e8b9d4b-e335-4dd7-abe8-7b312eb5a27d"))
                .errors(List.of(invalidParamBuilder.build())).title("title")
                .type(URI.create("/petstore/petservice/v1/pets"));

        assertThat(invalidParamBuilder)
                .hasToString("Rfc9457Error.InvalidParam.InvalidParamBuilder(pointer=#/invalidParam, detail=4Testing)");

        assertThat(errorBuilder).hasToString(
                "Rfc9457Error.Rfc9457ErrorBuilder(type=/petstore/petservice/v1/pets, title=title, instance=urn:ERROR:9e8b9d4b-e335-4dd7-abe8-7b312eb5a27d, detail=detail, errors=[InvalidParam[pointer=#/invalidParam, detail=4Testing]])");
    }
}
