package de.training;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import de.training.model.Pet;
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
 * @version 1.0
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

	/**
	 * The general endpoint's prefix.
	 */
	protected static final String EndPointPrefix = "http://localhost:8080/petstore/petservice/v1/pets";

	/**
	 * The general endpoint with a test {@link UUID}.
	 */
	protected static final String EndPointWithTestId = EndPointPrefix + "/6f09a3c7-fdec-4949-9da5-d089f9ccb378";

	/**
	 * The image endpoint with a test {@link UUID} for a {@link Pet} resource.
	 */
	protected static final String EndPointImageTestId = EndPointWithTestId + "/image";

	/**
	 * The {@link String} representing a valid request body for the {@link Pet} resource.
	 */
	protected static String validPetBody;

	/**
	 * Initializes frequently used objects in the inheriting test classes.
	 */
	@BeforeAll
	@SneakyThrows
	static void init() {
		final File contentFile = ResourceUtils.getFile("classpath:valid_pet_body.json");

		validPetBody = Files.contentOf(contentFile, StandardCharsets.UTF_8);
	}
}
