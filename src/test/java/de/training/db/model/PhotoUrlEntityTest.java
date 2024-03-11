package de.training.db.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Supplementary test on the {@link PhotoUrlEntity}.
 * 
 * @since 2022-03-15
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PhotoUrlEntityTest {

    /**
     * Ensures the URL is {@code null} in case it is not set.
     */
    @Test
    void testEmptyEntity() {
        final PhotoUrlEntity entity = new PhotoUrlEntity();

        assertThat(entity.getUrl()).isNull();
    }
}
