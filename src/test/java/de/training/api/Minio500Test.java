package de.training.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ResourceUtils;

import de.training.AbstractSpringTestRunner;
import de.training.db.model.PetEntity;
import de.training.model.Pet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * The unit test(s) for testing the {@link SneakyThrows} annotation in case a MinIo command is not executable.
 * 
 * @since 2022-03-28
 * @version 1.0
 * @author Dirk Weissmann
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SpringBootTest(properties = "minio.images-bucket-name=a")
class Minio500Test extends AbstractSpringTestRunner {

	@AfterEach
	private void cleanUp() {
		petRepository.deleteAll();
	}

	/**
	 * Test for the internal server problems in case of MinIo problems with a thrown {@link Exception}: Send something
	 * to the {@code PUT} endpoint for putting a new image to a {@link Pet} resource and make the MinIo service throw an
	 * {@link IllegalArgumentException} due to the too short bucket name.
	 * <p>
	 * Checks the response on the expected {@link HttpStatus#INTERNAL_SERVER_ERROR} and the expected {@link Error}
	 * response body.
	 */
	@Test
	@SneakyThrows
	@DisplayName("WHEN the MinIo client tries to store an image with a given invalid bucket name THEN the response code 500 is expected due to a thrown IllegalArgumentException")
	void testMinioCrashOnInvalidUrl() {
		final File contentFile = ResourceUtils.getFile("classpath:valid_test.jpg");
		final byte[] content = FileUtils.readFileToByteArray(contentFile);

		final PetEntity entity = createTestEntity(true);

		petRepository.save(entity);

		mockMvc.perform(put(EndPointPrefix + "/" + entity.getId() + "/image").contentType(MediaType.IMAGE_JPEG)
				.content(content))
				.andExpect((final MvcResult result) -> assertThat(result.getResolvedException())
						.isInstanceOf(IllegalArgumentException.class))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON)).andExpect(content()
						.string(containsString("\"title\":\"Internal problem. Please contact the support.\"")));
	}
}
