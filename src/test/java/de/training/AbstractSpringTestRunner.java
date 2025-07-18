package de.training;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import de.training.db.dao.PetRepositoryDao;
import de.training.db.model.PetEntity;
import de.training.db.model.TagEntity;
import de.training.model.Pet;
import de.training.model.Pet.Category;
import de.training.model.Pet.PetStatus;
import lombok.SneakyThrows;

/**
 * Very important class for out-sourcing the general parts of the integration JUnit tests that use the {@link #mockMvc}
 * object. If you write the annotations used here in each test class, the service gets (re-)started for each class which
 * is not acceptable in respect to the runtime.
 * <p>
 * The class is implicitly abstract.
 * 
 * @author Dirk Weissmann
 * @since 2022-02-21
 * @version 1.2
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AbstractSpringTestRunner {

	/**
	 * The {@link MockMvc} class member for performing the mocked requests to the service's endpoints.
	 */
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected PetRepositoryDao petRepository;

	/**
	 * The general endpoint's prefix.
	 */
	protected static final String END_POINT_PREFIX = "http://localhost:8080/petstore/petservice/v1/pets";

	protected static final String TEST_ID = "6f09a3c7-fdec-4949-9da5-d089f9ccb378";

	/**
	 * The general endpoint with a test {@link UUID}.
	 */
	protected static final String END_POINT_WITH_TEST_ID = END_POINT_PREFIX + "/" + TEST_ID;

	/**
	 * The image endpoint with a test {@link UUID} for a {@link Pet} resource.
	 */
	protected static final String END_POINT_IMAGE_TEST_ID = END_POINT_WITH_TEST_ID + "/image";

	/**
	 * The {@link String} representing a valid maximum request body for the {@link Pet} resource.
	 */
	protected static String validPetBodyWithTags;

	/**
	 * The {@link String} representing a valid minimum request body for the {@link Pet} resource.
	 */
	protected static String validMinimumPetBody;

	/**
	 * Initializes frequently used objects in the inheriting test classes.
	 */
	@BeforeAll
	@SneakyThrows
	static void init() {
		final File contentFileWithTags = ResourceUtils.getFile("classpath:valid_pet_body_max.json");

		validPetBodyWithTags = Files.contentOf(contentFileWithTags, StandardCharsets.UTF_8);

		final File contentFileWithoutTags = ResourceUtils.getFile("classpath:valid_pet_body_min.json");

		validMinimumPetBody = Files.contentOf(contentFileWithoutTags, StandardCharsets.UTF_8);
	}

	protected PetEntity createTestEntity(final boolean withTags) {
		final PetEntity entity = new PetEntity();

		entity.setCategory(Category.SPIDER);
		entity.setDescription(
				"What?? You want me to be a representative description for a what?! A SPIDER?!? You must be kidding!");
		entity.setName("Peter Parker");
		entity.setStatus(PetStatus.PENDING);

		if (withTags) {
			final TagEntity tagEntity = new TagEntity("subba");

			tagEntity.setPet(entity);
			entity.setTags(List.of(tagEntity));
		}

		return entity;
	}
}
