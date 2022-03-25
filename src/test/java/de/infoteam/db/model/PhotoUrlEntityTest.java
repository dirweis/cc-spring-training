package de.infoteam.db.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PhotoUrlEntityTest {

	@Test
	void testEmptyEntity() {
		final PhotoUrlEntity entity = new PhotoUrlEntity();

		assertThat(entity.getUrl()).isNull();
	}
}
